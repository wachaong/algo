package com.autohome.adrd.algo.click_model.optimizer.common;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.AbstractOneStepLineSearch;

public class OneStepWolfeLineSearch extends AbstractOneStepLineSearch {

	
	private double c1 = 0.1;
	private double c2 = 0.9;

	public void update(SparseVector xt, double f_xt, SparseVector df_xt) {
		if(iter_num > max_iter_num) {
			status = -1;
			return;
		}
		
		iter_num++;
		double ddt = direction.dot(df_xt);
		if(f_xt > f_x0 + c1 * stepLength * dd0) {
				rightBound = stepLength;
				stepLength = (leftBound + rightBound) / 2;
		 }
		 
		 else if(ddt < c2 * dd0) {
				leftBound = stepLength;
				stepLength = (leftBound + rightBound) / 2;
		 }
		 else
			 status = 0;
	}
	
}
