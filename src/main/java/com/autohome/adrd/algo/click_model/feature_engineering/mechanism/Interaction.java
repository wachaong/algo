package com.autohome.adrd.algo.click_model.feature_engineering.mechanism;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import com.autohome.adrd.algo.click_model.data.Sample;
import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * Add interaction features to the sample. 
 * 增加交叉特征。输入一个sample, 增加一些交叉特征后作为一个新的sample输出. 增加哪些交叉特征由file_path指定文本文件给出。文件的格式为：
 * A\tB
 * C\tD\tE
 * 2\tId2\tId3
 * 
 * 输出的sample中交叉特征的名字为 A##B, C##D##E, ...
 * 
 * @author Mingmin Yang
 *
 */
public class Interaction implements Transformer {

	private ArrayList<String[]> inter_feature = null;
	private ArrayList<MyPair<Integer, String[]>> inter_group = null;

	public Interaction () {
		inter_feature = new ArrayList<String[]>();
		inter_group = new ArrayList<MyPair<Integer, String[]>>();
	}

	private static String concat(String [] features) {
		StringBuilder sb = new StringBuilder();
		sb.append(features[0]);
		for(int i = 1; i < features.length; ++i) {
			sb.append("##");
			sb.append(features[i]);
		}
		return sb.toString();
	}

	public void setup(String file_path) {
		File file_in = new File(file_path);
		BufferedReader fin = null;
		try {
			fin = new BufferedReader(new FileReader(file_in));
			String line = null;
			while((line = fin.readLine()) != null) {
				line = line.trim();
				String [] feature_names = line.split("\t");
				if(1 >= feature_names.length) {
					continue;
				}
				try {
					int order = Integer.parseInt(feature_names[0]);
					String[] tmp = new String[feature_names.length - 1];
					System.arraycopy(feature_names, 1, tmp, 0, tmp.length);
					inter_group.add(new MyPair<Integer, String[]>(order, tmp));
				} catch(NumberFormatException e) {
					inter_feature.add(feature_names);
				}

			}

			fin.close();
		} catch(IOException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}


	public Sample transform(Sample sample_in) {

		Sample sample_out = (Sample)sample_in.clone();

		for(String[] feature_names : inter_feature) {
			String new_feature = inplaceTransformImpl(sample_in, feature_names);
			if(new_feature != null) {
				sample_out.setFeature(new_feature);
			}

		}

		for(MyPair<Integer, String[]> pair : inter_group) {
			ArrayList<String> new_features = inplaceTransformImpl(sample_in, pair.getSecond(), pair.getFirst());
			for(String new_feature : new_features) {
				sample_out.setFeature(new_feature);
			}
		}

		return sample_out;
	}


	public void inplaceTransform(Sample sample_in) {
		sample_in = transform(sample_in);
		return;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> transformFeatures(ArrayList<String> features_in) {
		ArrayList<String> features_out = (ArrayList<String>) features_in.clone();

		for(String[] inter_features : inter_feature) {
			String new_feature = transformFeaturesImpl(features_in, inter_features);
			if(new_feature != null) {
				features_out.add(new_feature);
			}
		}

		for(MyPair<Integer, String[]> pair : inter_group) {
			ArrayList<String> new_features = transformFeaturesImpl(features_in, pair.getSecond(), pair.getFirst());		
			for(String new_feature : new_features) {
				features_out.add(new_feature);
			}
		}

		return features_out;

	}
	

	private String inplaceTransformImpl(Sample sample_in, String[] feature_names) {

		boolean value = true;

		for (String feature : feature_names) {
			value = value && sample_in.getIdFeatures().contains(feature);
		}

		if(value) {
			return concat(feature_names);
		}
		else {
			return null;
		}
	}

	private ArrayList<String> inplaceTransformImpl(Sample sample_in, String[] feature_patterns, int order) {
		Vector<String> tmp = new Vector<String>(feature_patterns.length);


		for(String feature : sample_in.getIdFeatures()) {
			for(String prefix : feature_patterns) {
				if(feature.startsWith(prefix + "@")) {
					tmp.add(feature);
				}
			}

		}

		ArrayList<String> new_features = new ArrayList<String>();
		if(tmp.size() >=  order) {
			if(order == 2) {
				for(int i = 0; i < tmp.size() - 1; ++i) {
					for(int j = i + 1; j < tmp.size(); ++j) {
						String[] feas = {tmp.get(i), tmp.get(j)};
						new_features.add(concat(feas));
					}
				}
			} 
			else if(order == 3) {
				for(int i = 0; i < tmp.size() - 2; ++i) {
					for(int j = i + 1; j < tmp.size() - 1; ++j) {
						for(int k = j + 1; k < tmp.size(); ++k) {
							String[] feas = {tmp.get(i), tmp.get(j), tmp.get(k)} ;
							new_features.add(concat(feas));

						}
					}
				}
			}
		}

		return new_features;
	}

	private String transformFeaturesImpl(ArrayList<String> features_in, String[] feature_names) {
		HashSet<String> tmp = new HashSet<String>();
		tmp.addAll(features_in);
		boolean value = true;
		for (String feature : feature_names) {
			value = value && tmp.contains(feature);
		}
		if(value) {
			return concat(feature_names);
		}
		else {
			return null;
		}
	}
	
	private ArrayList<String> transformFeaturesImpl(ArrayList<String> features_in, String[] feature_patterns, int order) {
		//Vector<String> tmp = new Vector<String>(feature_patterns.length);
		HashMap<String, ArrayList<String>> tmp2 = new HashMap<String, ArrayList<String>>();

		for(String feature : features_in) {
			for(String prefix : feature_patterns) {
				if(feature.startsWith(prefix + "@")) {
					if(!tmp2.containsKey(prefix)) {
						tmp2.put(prefix, new ArrayList<String>());
					}
					tmp2.get(prefix).add(feature);
					//tmp.add(feature);
				}
			}

		}
		
		String[] prefix = new String[tmp2.size()];
		tmp2.keySet().toArray(prefix);
		ArrayList<String> new_features = new ArrayList<String>();
		if(tmp2.size() >= order) {
			if(order == 2) {
				for(int i = 0; i < prefix.length - 1; ++i) {
					for(int j = i + 1; j < prefix.length; ++j) {
						System.out.printf("%d,%d,%d\n", i, j, tmp2.size());
						int n1 = 0; 
						for(String f1 : tmp2.get(prefix[i])) {
							n1++;
							int n2 = 0;
							for(String f2 : tmp2.get(prefix[j])) {
								n2++;
								System.out.printf("%d,%d,%d, %d\n", n1, n2, tmp2.get(prefix[i]).size(), tmp2.get(prefix[j]).size());
								new_features.add(f1 + "##" + f2);
								
							}
						}

						
						
					}
				}
			} 
			else if(order == 3) {
				for(int i = 0; i < prefix.length - 2; ++i) {
					for(int j = i + 1; j < prefix.length - 1; ++j) {
						for(int k = j + 1; k < prefix.length; ++k) {
							for(String f1 : tmp2.get(prefix[i])) {
								for(String f2 : tmp2.get(prefix[j])) {
									for(String f3 : tmp2.get(prefix[k])) {
										new_features.add(f1 + "##" + f2 + "##" + f3);
									}
								}
							}
						}
					}
				}
			}
		}
		return new_features;
	}

}
								
