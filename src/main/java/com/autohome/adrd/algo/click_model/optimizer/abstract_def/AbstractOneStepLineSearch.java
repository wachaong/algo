package com.autohome.adrd.algo.click_model.optimizer.abstract_def;

import com.autohome.adrd.algo.click_model.data.SparseVector;

/**
 * 
 * @author Yang Mingmin
 * 
 *
 */
public abstract class AbstractOneStepLineSearch {
	protected SparseVector x0 = null;   //search point
	protected SparseVector df_x0 = null; //gradient of search point
	protected double f_x0;  //
	protected double dd0 = 0;
	protected SparseVector direction = null;	//search direction
	protected double stepLength = 1.0;
	protected int iter_num = 0;
	protected int max_iter_num = 100;
	protected double leftBound = 0.0;
	protected double rightBound = Double.MAX_VALUE;
	//protected double rightBound = 1.0;
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

		SparseVector xt = (SparseVector) this.x0.clone();
		xt.plusAssign(this.stepLength, this.direction);
		return xt;
	}
	
	public SparseVector getNextPoint(float regularizationFactor) {
		System.out.println("step length is:" + stepLength);
		SparseVector xt = (SparseVector) this.x0.clone();
		xt.plusAssign(this.stepLength, this.direction);
		
		//Project back onto quadrant
		if(regularizationFactor>0){
			for(int i : xt.getData().keySet()){
				if(xt.getValue(i)*x0.getValue(i)< 0.0){
					xt.setValue(i, 0.0);
				}
			}			
		}	
		return xt;
	}
	
	public void set(SparseVector x0, double f_x0, SparseVector df_x0, SparseVector direction) {
		this.x0 = x0;
		this.f_x0 = f_x0;
		this.df_x0 = df_x0;
		this.direction = direction;
		this.dd0 = direction.dot(df_x0); 
		if(dd0 > 0) {
			System.out.println("not a decent direction");
			//this.direction = (SparseVector) df_x0.scale(-1.0);
			//System.exit(-1);
		}
	}
	
	public abstract void update(SparseVector xt, double f_xt, SparseVector df_xt);

	

	
}
