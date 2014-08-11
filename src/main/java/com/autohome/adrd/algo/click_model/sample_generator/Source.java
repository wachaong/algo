package com.autohome.adrd.algo.click_model.sample_generator;

/**
 * 
 * @brief Transform a data source into a sample.
 *
 */
public interface Source {
	public Sample process(Object raw_data);
}
