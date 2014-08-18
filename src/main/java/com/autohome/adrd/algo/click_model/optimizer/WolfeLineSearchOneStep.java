package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.SparseVector;

public class WolfeLineSearchOneStep {
	private double stepLength = 1.0;
	private int iter_num = 0;
	private static int max_iter_num = 50;
	private int status = 1;
	private SparseVector x0 = null;
	private SparseVector direction = null;
	private SparseVector df_x0 = null;
	private double f_x0;
	private double dd0 = 0;
	private double c1 = 0.1;
	private double c2 = 0.9;
	private double leftBound = 0.0;
	private double rightBound = Double.MAX_VALUE;
	
	public SparseVector stepForward() {
		SparseVector xt = (SparseVector) x0.clone();
		xt.plusAssign(stepLength, direction);
		return xt;
	}
	
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
