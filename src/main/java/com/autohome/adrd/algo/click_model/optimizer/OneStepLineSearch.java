package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.SparseVector;

public abstract class OneStepLineSearch {
	protected SparseVector x0 = null;   //search point
	protected SparseVector df_x0 = null;
	protected double f_x0;
	protected SparseVector direction = null;	//search direction
	protected double stepLength = 1.0;
	protected int iter_num = 0;
	protected int max_iter_num = 100;
	protected double leftBound = 0.0;
	protected double rightBound = Double.MAX_VALUE;
	protected int status = 1;
	
	public SparseVector getX0() {
		return x0;
	}
	public void setX0(SparseVector x0) {
		this.x0 = x0;
	}
	public SparseVector getDirection() {
		return direction;
	}
	public void setDirection(SparseVector direction) {
		this.direction = direction;
	}
	public double getStepLength() {
		return stepLength;
	}
	public void setStepLength(double stepLength) {
		this.stepLength = stepLength;
	}
	public int getIter_num() {
		return iter_num;
	}
	public void setIter_num(int iter_num) {
		this.iter_num = iter_num;
	}
	public int getMax_iter_num() {
		return max_iter_num;
	}
	public void setMax_iter_num(int max_iter_num) {
		this.max_iter_num = max_iter_num;
	}
	public SparseVector getDf_x0() {
		return df_x0;
	}
	public void setDf_x0(SparseVector df_x0) {
		this.df_x0 = df_x0;
	}
	public double getF_x0() {
		return f_x0;
	}
	public void setF_x0(double f_x0) {
		this.f_x0 = f_x0;
	}
	public double getLeftBound() {
		return leftBound;
	}
	public void setLeftBound(double leftBound) {
		this.leftBound = leftBound;
	}
	public double getRightBound() {
		return rightBound;
	}
	public int getStatus() {
		return status;
	}
	
	public void setRightBound(double rightBound) {
		this.rightBound = rightBound;
	}
	
	public SparseVector getNextPoint() {
		SparseVector xt = (SparseVector) x0.clone();
		xt.plusAssign(stepLength, direction);
		return xt;
	}
	public abstract void update(SparseVector xt, double f_xt, SparseVector df_xt);
	
}
