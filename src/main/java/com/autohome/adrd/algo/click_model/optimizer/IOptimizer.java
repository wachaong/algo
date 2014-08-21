package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.Vector;

public interface IOptimizer<V extends Vector> {
	public void setup(String configure_file);
	public void minimize(IDifferentiableFunction<V> f, V x0);
	public int getStatus();
}
