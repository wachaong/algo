package com.autohome.adrd.algo.click_model.optimizer;

public interface Optimizer<V extends Vector> {
	public void setup(String configure_file);
	public void minimize(DifferentiableFunction<V> f, V x0);
	public int getStatus();

}
