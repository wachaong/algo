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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommonFunc {
	/**
	 */
	
	public static final String TAB = "\t";
	public static final String SPACE = " ";
	public static final String COLON = ":";
	
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

}
