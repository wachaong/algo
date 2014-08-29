package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.SparseVector;

public class OneStepBacktrackingLineSearch extends AbstractOneStepLineSearch {
	private double alpha = 0.5;
	private double c1 = 0.1;

	@Override
	public void update(SparseVector xt, double f_xt, SparseVector df_xt) {
		
		if(iter_num > max_iter_num) {
			status = -1;
			return;
		}
		
		iter_num++;
		
		
		if(f_xt > f_x0 + c1 * stepLength * dd0) {
			stepLength *= alpha;
		 } else {
			 status = 0;
		 }
	}
		
}
