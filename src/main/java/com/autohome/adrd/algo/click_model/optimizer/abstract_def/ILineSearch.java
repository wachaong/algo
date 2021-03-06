package com.autohome.adrd.algo.click_model.optimizer.abstract_def;

import com.autohome.adrd.algo.click_model.data.Vector;

public interface ILineSearch {
	/**
	 * 
	 * @param f
	 * 	A multivariable function the line-searcher acts on.
	 * @param df
	 *  The derivative of f.
	 * @param x0
	 * 	The initial searching point.
	 * @param df_x0
	 *  The derivative of f at initial searching point.
	 * @param f_x0
	 * @param direction
	 * 	The searching direction
	 */
	public <V extends Vector> double search(IObjectFunction<V> f, IGradientFunction<V> df,
			   V x0, 
			   final V direction,
			   double f_x0, 
			   V df_x0);
	
	public <V extends Vector> double search(IDifferentiableFunction<V> f,
			   V x0, 
			   final V direction,
			   double f_x0, 
			   V df_x0);
	//hadoop line search
	//public double search();
	//public<V extends Vector> double searchOneStep(IDifferentiableFunction<V> f);
}
