package com.autohome.adrd.algo.click_model.sample_generator;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 增加交叉特征。输入一个sample, 增加一些交叉特征后作为一个新的sample输出. 增加哪些交叉特征由file_path指定文本文件给出。文件的格式为：
 * A\tB
 * C\tD\tE
 * ...
 * 
 * 输出的sample中交叉特征的名字为 A#@#B, C#@#D#@#E, ...
 * 
 * @author Mingmin Yang
 *
 */
public class InteractionTransformer implements Transformer {
	
	private ArrayList<String[]> inter_feature = null;
	private ArrayList<String> inter_feature_name = null;
	
	public InteractionTransformer() {
		inter_feature = new ArrayList<String[]>();
		inter_feature_name = new ArrayList<String>();
	}
	
	private static String concat(String [] features) {
		StringBuilder sb = new StringBuilder();
		sb.append(features[0]);
		for(int i = 1; i < features.length; ++i) {
			sb.append("#@#");
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
				inter_feature.add(feature_names);
				inter_feature_name.add(concat(feature_names));
			}
			
			fin.close();
		} catch(IOException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	

	public Sample transform(Sample sample_in) {

		Sample sample_out = (Sample)sample_in.clone();
		double value = 0.0;
		for(String[] feature_names : inter_feature) {
			boolean is_id_feature = true;

			is_id_feature = is_id_feature && sample_in.getIdFeatures().contains(feature_names[0]);
			value = sample_in.getFeature(feature_names[0]);


			for (int i = 1; i < feature_names.length; ++i) {
				is_id_feature = is_id_feature && sample_in.getIdFeatures().contains(feature_names[i]);
				value *= sample_in.getFeature(feature_names[i]);
			}
			
			String new_feature = concat(feature_names);

			if(Math.abs(value) > 1e-20) {
				if(is_id_feature) {
					sample_out.setFeature(new_feature.toString());
				}
				else {
					sample_out.setFeature(new_feature.toString(), value);
				}
			}

		}

		return sample_out;
	}
	
	public void inplaceTransform(Sample sample_in) {

		double value = 0.0;
		for(String[] feature_names : inter_feature) {
			boolean is_id_feature = true;

			is_id_feature = is_id_feature && sample_in.getIdFeatures().contains(feature_names[0]);
			value = sample_in.getFeature(feature_names[0]);


			for (int i = 1; i < feature_names.length; ++i) {
				is_id_feature = is_id_feature && sample_in.getIdFeatures().contains(feature_names[i]);
				value *= sample_in.getFeature(feature_names[i]);
			}
			
			String new_feature = concat(feature_names);

			if(Math.abs(value) > 1e-20) {
				if(is_id_feature) {
					sample_in.setFeature(new_feature.toString());
				}
				else {
					sample_in.setFeature(new_feature.toString(), value);
				}
			}

		}


	}

	@Override
	public ArrayList<String> transformFeatures(ArrayList<String> features_in) {
		// TODO Auto-generated method stub
		return null;
	}	

}
