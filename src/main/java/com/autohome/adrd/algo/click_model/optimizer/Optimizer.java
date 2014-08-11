package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.Vector;

public interface Optimizer<V extends Vector> {
	public void setup(String configure_file);
	public void minimize(DifferentiableFunction<V> f, V x0);
	public int getStatus();

}
