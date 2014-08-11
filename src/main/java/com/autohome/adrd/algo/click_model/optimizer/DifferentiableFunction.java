package com.autohome.adrd.algo.click_model.optimizer;

public interface DifferentiableFunction<V> {
	public double eval(V x);
	public V diff(V x);
}
