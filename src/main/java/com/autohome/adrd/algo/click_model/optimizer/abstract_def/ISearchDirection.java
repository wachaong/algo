package com.autohome.adrd.algo.click_model.optimizer.abstract_def;

import com.autohome.adrd.algo.click_model.data.SparseVector;

/**
 * Calculate the search Direction
 * @author Yang Mingmin
 *
 */
public  interface ISearchDirection {
	public SparseVector calcSearchDirction(SparseVector grad);
	public void update(SparseVector x0, SparseVector xt, 
			double f_x0, double f_xt,
			SparseVector df_x0, SparseVector df_xt);
}
