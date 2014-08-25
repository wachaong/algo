package com.autohome.adrd.algo.click_model.feature_process_impl;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;

import com.autohome.adrd.algo.click_model.data.Sample;
import com.autohome.adrd.algo.click_model.feature_process.Transformer;

/**
 * 
 * @author Mingmin Yang
 * 
 */


public class SubsetTransformer implements Transformer {
	private ArrayList<String> chosen_features = null;
	
	SubsetTransformer() {
		chosen_features = new ArrayList<String>();
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
		Sample sample_out = new Sample();
		
		for(String feature : chosen_features) {
			if(sample_in.getIdFeatures().contains(feature)) {
				sample_out.setFeature(feature);
			}
			else if(sample_in.getFloatFeatures().containsKey(feature)) {
				sample_out.setFeature(feature, sample_in.getFeature(feature));
			}
		}
		
		sample_in = sample_out;	
	
	}
	
	public Sample transform(Sample sample_in) {
		Sample sample_out = new Sample();
		
		for(String feature : chosen_features) {
			if(sample_in.getIdFeatures().contains(feature)) {
				sample_out.setFeature(feature);
			}
			else if(sample_in.getFloatFeatures().containsKey(feature)) {
				sample_out.setFeature(feature, sample_in.getFeature(feature));
			}
		}
		
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
