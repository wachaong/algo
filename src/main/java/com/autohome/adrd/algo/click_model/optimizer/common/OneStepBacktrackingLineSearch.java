package com.autohome.adrd.algo.click_model.optimizer.common;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.AbstractOneStepLineSearch;

public class OneStepBacktrackingLineSearch extends AbstractOneStepLineSearch {
	private double alpha = 0.5;
	private double c1 = 1e-4;
	private double stepLength = 1.0;
	@Override
	public void update(SparseVector xt, double f_xt, SparseVector df_xt) {
		
		if(iter_num > max_iter_num) {
			status = -1;
			return;
		}
		
		iter_num++;
		
		if(iter_num==1){
			double normDir =Math.sqrt( direction.dot(direction));
			stepLength = (1 / normDir);
			alpha = 0.1;
		}else{
			stepLength =  1.0;
			alpha = 0.5;
		}
		
		if(f_xt > f_x0 + c1 * stepLength * dd0) {
			stepLength *= alpha;
		 } else {
			 status = 0;
		 }
	}
		
}
