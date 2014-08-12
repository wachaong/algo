package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * a differentiable function  
 * @author Yang Mingmin
 *
 */
public interface DifferentiableFunction<V> {
	public double eval(V x);  // calculate f(x)
	public V diff(V x);       // calculate f'(x)
	public MyPair<Double, V> calcValueGradient(V x); //calculate f(x) and f'(x) simultaneously
}
