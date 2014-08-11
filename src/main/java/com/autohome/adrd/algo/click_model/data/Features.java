package com.autohome.adrd.algo.click_model.data;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


/**
 * 
 * 存储一条样本的所有特征
 *
 */
public class Features implements Cloneable {

	private HashSet<String> id_features = new HashSet<String>();
 	private HashMap<String, Double> float_features = new HashMap<String, Double>();
 	 	
	public HashMap<String, Double> getFloatFeatures() {
		return float_features;
	}
	
	public HashSet<String> getIdFeatures() {
		return id_features;
	}
	
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		Features o = null;
		try {
			o = (Features) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		o.id_features = (HashSet<String>) id_features.clone();
		o.float_features = (HashMap<String, Double>) float_features.clone();
		return o;
	}
	
	public double getFeature(String feature_name) {
		if(id_features.contains(feature_name))
			return 1.0;
		else if(float_features.containsKey(feature_name))
			return float_features.get(feature_name);
		else
			return 0.0;
	}
	
	//set float feature
	public void setFeature(String feature_name, double value) {
		float_features.put(feature_name, value);
	}
	
	//set id feature
	public void setFeature(String feature_name) {
		id_features.add(feature_name);
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append(id_features.size());
		for(String feature : id_features) {
			str.append('\t');
			str.append(feature);
		}
		
		str.append('\t');
		str.append(float_features.size());
		for(java.util.Iterator<Entry<String, Double>> iter = float_features.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry<String, Double> entry = iter.next();
			str.append('\t');
			str.append(entry.getKey());
			str.append(' ');
			str.append(entry.getValue());
		}
		
		return str.toString();
	}
	
	public static Features fromString(String str) {
		Features ans = new Features();
		Scanner in = new Scanner(str);
		int sz = in.nextInt();
		for(int i = 0; i < sz; i++) {
			ans.setFeature(in.next());
		}
		sz = in.nextInt();
		String feature;
		double value;
		for(int i = 0; i < sz; i++) {
			feature = in.next();
			value = in.nextDouble();
			ans.setFeature(feature, value);
		}
		in.close();
		
		return ans;
	}
	
}
