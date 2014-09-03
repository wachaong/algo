package com.autohome.adrd.algo.click_model.utility;

/**

 * author : wang chao
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.driver.MaxentTrainDriver;

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
	
	public static void main(String[] args) throws Exception {
		
		Map<Integer, SparseVector> weight_maps = new HashMap<Integer, SparseVector>();
		//weight_maps = CommonFunc.readSparseVectorMap("E:\\init_weight.txt");
		//weight_maps = CommonFunc.readSparseVectorMapFast("D:\\autohome\\algo\\target\\bbb");
		
		/*
		Iterator<Entry<Integer, SparseVector>> iter = weight_maps.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, SparseVector> entry = iter.next();
			int model_id = entry.getKey();
			String result = String.valueOf(model_id) + "\t" + entry.getValue().toString();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("E:\\out_weight.txt"), "utf-8"));
			writer.write(result);
			writer.close();
		}*/
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream("D:\\autohome\\algo\\target\\bbb"), "utf-8"));
		String line;
		line = reader.readLine();
		String[] arr = line.split("\t", 2);
    	int model_id = Integer.parseInt(arr[0]);
    	SparseVector tmp = SparseVector.fromString(arr[1]);  
		System.out.println(tmp.square());
		System.out.println(tmp.norm_1());
		System.out.println(tmp.norm_2());
		System.out.println(tmp.getData().size());
		System.out.println("haha");
		
	}

	
}
