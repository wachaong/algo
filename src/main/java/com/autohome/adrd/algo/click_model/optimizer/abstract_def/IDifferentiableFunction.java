package com.autohome.adrd.algo.click_model.optimizer.abstract_def;

import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * differentiable function  
 * @author Yang Mingmin
 *
 */
public interface IDifferentiableFunction<V> {
	public double calcValue(V x);  // calculate f(x)
	public V calcGradient(V x);       // calculate f'(x)
	public MyPair<Double, V> calcValueGradient(V x); //calculate f(x) and f'(x) simultaneously
}
