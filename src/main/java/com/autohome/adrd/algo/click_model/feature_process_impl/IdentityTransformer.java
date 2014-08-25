package com.autohome.adrd.algo.click_model.feature_process_impl;

import java.util.ArrayList;

import com.autohome.adrd.algo.click_model.data.Sample;
import com.autohome.adrd.algo.click_model.feature_process.Transformer;

public class IdentityTransformer implements Transformer {

	@Override
	public void setup(String conf_path) {
		return;
	}

	@Override
	public void inplaceTransform(Sample sample) {
		return;
	}

	@Override
	public Sample transform(Sample input_sample) {
		Sample output_sample = (Sample)input_sample.clone();
		return output_sample;
	}

	@Override
	public ArrayList<String> transformFeatures(ArrayList<String> features_in) {
		ArrayList<String> features_out = (ArrayList<String>)features_in.clone();
		return features_out;
	}
	

}
