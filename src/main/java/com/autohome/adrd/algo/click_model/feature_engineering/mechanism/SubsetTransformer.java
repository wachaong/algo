package com.autohome.adrd.algo.click_model.feature_engineering.mechanism;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.autohome.adrd.algo.click_model.data.Sample;

/**
 * 
 * @author Mingmin Yang
 * 
 */


public class SubsetTransformer implements Transformer {
	private Set<String> chosen_features = null;
	
	public SubsetTransformer() {
		chosen_features = new HashSet<String>();
	}
	
	public void setup(String input_file){
		try {
			Scanner in = new Scanner(new File(input_file));
			while(in.hasNext()) {
				chosen_features.add(in.next());
			}
			in.close();
		}catch(IOException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void inplaceTransform(Sample sample_in) {
		return;
	}
	
	public Sample transform(Sample sample_in) {
		Sample sample_out = new Sample();
		
		for(String fea : sample_in.getIdFeatures()) {
			if(chosen_features.contains(fea))
				sample_out.setFeature(fea);
		}
		sample_out.getFloatFeatures().putAll(sample_in.getFloatFeatures());
		
		return sample_out;	
	
	}

	public ArrayList<String> transformFeatures(ArrayList<String> features_in) {
		HashSet<String> tmp = new HashSet<String>();
		tmp.addAll(features_in);
		ArrayList<String> ans = new ArrayList<String>();
		
		for(String feature : chosen_features) {
			if(tmp.contains(feature)) {
				ans.add(feature);
			}
			
		}
		return ans;
	}
}
