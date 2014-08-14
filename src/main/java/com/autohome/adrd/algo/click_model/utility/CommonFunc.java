package com.autohome.adrd.algo.click_model.utility;

/**

 * author : wang chao
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.autohome.adrd.algo.click_model.data.SparseVector;

public class CommonFunc {
	/**
	 */
	
	public static final String TAB = "\t";
	public static final String SPACE = " ";
	public static final String COLON = ":";
	
	public static boolean isBlank(String str) {
		if (str == null || str.length() == 0 || str.trim().length() == 0 || str.equals(""))
			return true;
		else
			return false;
	}
	
	public static StringBuilder join(Collection<?> follows, String sep) {
		StringBuilder sb = new StringBuilder();

		if (follows == null || sep == null) {
			return sb;
		}

		Iterator<?> it = follows.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(sep);
			}
		}
		return sb;
	}
	
	public static Set<String> readSets(String fileName, String sep, int index,
			String encoding) {
		Set<String> res = new HashSet<String>();
		if (index < 0) {
			return res;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), encoding));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] arr = line.split(sep, -1);
				if (arr.length <= index) {
					continue;
				}
				res.add(arr[index]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public static Map<String, Double> readDoubleMaps(String fileName,
			String sep, int kInd, int vInd, String encoding) {
		Map<String, Double> res = new HashMap<String, Double>();
		if (kInd < 0 || vInd < 0) {
			return res;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), encoding));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] arr = line.split(sep, -1);
				if (arr.length <= kInd || arr.length <= vInd) {
					continue;
				}
				res.put((String) arr[kInd], Double.valueOf(arr[vInd]));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static SparseVector readSparseVector(String fileName,
			String sep, int kInd, int vInd, String encoding) {
		SparseVector res = new SparseVector();
		if (kInd < 0 || vInd < 0) {
			return res;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), encoding));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] arr = line.split(sep, -1);
				if (arr.length <= kInd || arr.length <= vInd) {
					continue;
				}
				res.setValue(Integer.parseInt(arr[kInd]), Double.valueOf(arr[vInd]));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	 /*
     * format:
     * 2&15 0.01
     * 3&18 0.02
     */
	public static Map<Integer,SparseVector> readSparseVectorMap(String fileName) {
		Map<Integer,SparseVector> res = new HashMap<Integer,SparseVector>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] arr = line.split("\t", -1);
            	int model_id = Integer.parseInt(arr[0].split("&")[0]);
            	int id = Integer.parseInt(arr[0].split("&")[1]);
            	if(! res.containsKey(model_id))
            	{
            		SparseVector tmp = new SparseVector();
            		res.put(model_id, tmp);
            	}
            	res.get(model_id).setValue(id, Double.parseDouble(arr[1])); 
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}	
}
