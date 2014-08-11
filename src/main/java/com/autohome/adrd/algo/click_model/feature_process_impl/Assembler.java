package com.autohome.adrd.algo.click_model.feature_process_impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Collection;

import com.autohome.adrd.algo.click_model.data.Sample;

/**
 * 
 * Assemble a list of samples into a big one.
 *
 */
public class Assembler {
	public static Sample assemble(Collection<Sample> sample_ins) {
		Sample sample_out = new Sample();
		for(Sample sample : sample_ins) {
			
			//set label
			sample_out.setLabel(sample.getLabel());
			
			//add boolean features.
			for(String feature : sample.getIdFeatures()) {
				sample_out.getIdFeatures().add(feature);
			}
			
			//add float features.
			for(Iterator<Entry<String, Double>> iter 
					= sample.getFloatFeatures().entrySet().iterator();
					iter.hasNext();) {
				Map.Entry<String, Double> entry = (Map.Entry<String, Double>)iter.next();
				sample_out.setFeature(entry.getKey(), entry.getValue());
			}
			
			
		}
		return sample_out;
	}

}
