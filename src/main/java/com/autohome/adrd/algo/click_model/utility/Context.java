package com.autohome.adrd.algo.click_model.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Yang Mingmin
 *
 */
public class Context {
	Map<String, Integer> param_int = new HashMap<String, Integer>();
	Map<String, Double> param_double = new HashMap<String, Double>();
	Map<String, String> param_other = new HashMap<String, String>();
	
	public int getInteger(String key, int val) {
		if(param_int.containsKey(key))
			return param_int.get(key);
		else if(param_other.containsKey(key))
			return Integer.parseInt(param_other.get(key));
		else
			return val;
	}
	
	public double getDouble(String key, double val) {
		if(param_double.containsKey(key))
			return param_double.get(key);
		else if(param_other.containsKey(key))
			return Double.parseDouble(param_other.get(key));
		else
			return val;
	}
	
	public String get(String key, String val) {
		if(param_other.containsKey(key))
			return param_other.get(key);
		else
			return val;
	}
	
	public void setInteger(String key, int val) {
		
	}
	
	public void setDouble(String key, double val) {
		param_double.put(key, val);
	}

}
