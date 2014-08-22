package com.autohome.adrd.algo.click_model.feature_process_impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.autohome.adrd.algo.click_model.feature_process_impl.*;

/**
 * 
 * @author Yang Mingmin
 *
 */
public class NumberFeature {
	
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<String> input_features = new ArrayList<String>();
		Map<String, Integer> feature_id_map = new HashMap<String, Integer>();
		Map<String, ArrayList<Integer>> model_featureIds_map = new HashMap<String, ArrayList<Integer>>();
		SampleGeneratorHelper helper = new SampleGeneratorHelper();
		helper.setup(args[0]);
		input_features = readArrayList(args[1]);
		helper.labelize_features(input_features, feature_id_map, model_featureIds_map);
		
		//output the results
		PrintWriter out = new PrintWriter(args[2]);
		for(Map.Entry<String, Integer> entry : feature_id_map.entrySet()) {
			out.write(entry.getKey());
			out.write("\t");
			out.write(entry.getValue());
			out.write("\n");
		}
		out.close();
		
		out = new PrintWriter(args[3]);
		for(Map.Entry<String, ArrayList<Integer>> entry : model_featureIds_map.entrySet()) {
			out.write(entry.getKey());
			out.write("\t");
			int n = 0;
			for(Integer i : entry.getValue()) {
				if(n != 0)
					out.write(",");
				out.write(i);
				n++;
			}
			
		}
		out.close();
	}
	
	private static ArrayList<String> readArrayList(String path) throws FileNotFoundException {
		ArrayList<String> result = new ArrayList<String>();
		Scanner fin = new Scanner(new File(path));
		while(fin.hasNext()) {
			result.add(fin.next());
		}
		return result;
	}

}
