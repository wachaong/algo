package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.SparseVector;

/**
 * 
 * @author Yang Mingmin
 *
 */
public abstract class AbstractOneStepLineSearch {
	protected SparseVector x0 = null;   //search point
	protected SparseVector df_x0 = null; //gradient of search point
	protected double f_x0;  //
	protected SparseVector direction = null;	//search direction
	protected double stepLength = 1.0;
	protected int iter_num = 0;
	protected int max_iter_num = 100;
	protected double leftBound = 0.0;
	protected double rightBound = Double.MAX_VALUE;
	protected int status = 1;
	
	public SparseVector getSearchPoint() {
		return x0;
	}
	
	public void setSearchPoint(SparseVector x0) {
		this.x0 = x0;
	}
	
	public SparseVector getSearchDirection() {
		return direction;
	}
	
	public void setSearchDirection(SparseVector direction) {
		this.direction = direction;
	}
	
	public double getStepLength() {
		return stepLength;
	}
	
	public void setStepLength(double stepLength) {
		this.stepLength = stepLength;
	}
	
	public int getIterationNumber() {
		return iter_num;
	}
	
	public void setItetationnumber(int iter_num) {
		this.iter_num = iter_num;
	}
	public int getMaxIterationNumber() {
		return max_iter_num;
	}
	public void setMaxIterationNumber(int max_iter_num) {
		this.max_iter_num = max_iter_num;
	}
	public SparseVector getFunctionGradient() {
		return df_x0;
	}
	public void setFunctionGradient(SparseVector df_x0) {
		this.df_x0 = df_x0;
	}
	public double getFunctionValue() {
		return f_x0;
	}
	public void setFunctionValue(double f_x0) {
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
	
	public void set(SparseVector x0, double f_x0, SparseVector df_x0, SparseVector direction) {
		this.x0 = x0;
		this.f_x0 = f_x0;
		this.df_x0 = df_x0;
		this.direction = direction;
	}
	
	public abstract void update(SparseVector xt, double f_xt, SparseVector df_xt);

	
}