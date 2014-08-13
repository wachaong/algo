package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.Vector;


/**
 * Backtracking line search.
 * See Stephen Boyd, Lieven Vandenberghe, 2004, Convex Optimization p464 for more infomation.
 * @author Mingmin Yang
 * 
 *
 */
public class BacktrackingLineSearch implements LineSearch {
	private double alpha = 0.1;
	private double beta = 0.8;
	private int max_iter_num = 100;
	private int status = 0;
	
	public BacktrackingLineSearch() {
	}
	
	public BacktrackingLineSearch(double _alpha, double _beta) {
		alpha = _alpha;
		beta = _beta;
	}
	
	public double getbeta() {
		return beta;
	}
	
	public void setbeta(double _beta) {
		beta = _beta;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public void setAlpha(double _alpha) {
		alpha = _alpha;
	}
	
	public int getMaxIterNum() {
		return max_iter_num;
	}
	
	public void setMaxIterNum(int num) {
		max_iter_num = num;
	}
	
	public int getStatus() {
		return status;
	}
	
	public <V extends Vector> double search(ObjectFunction<V> f, GradientFunction<V> df,
			   V x0, 
			   final V direction,
			   double f_x0, 
			   V df_x0) {
		double step_length = 1;
		double back_length = step_length;
		int iter_num = 0;
		double f_xt = f_x0;
		double tmp = alpha * direction.dot(df_x0);
		while(iter_num < max_iter_num) {
			iter_num++;
			x0.plusAssign(back_length, direction);
			f_xt = f.eval(x0);
			//Armijo condition
			if(f_xt <= f_x0 + step_length * tmp) {
				status = 1;
				df_x0 = df.eval(x0);
				return f_xt;
			}
			
			back_length = step_length * (beta - 1);
			step_length *= beta;
		}
		status = -1;
		df_x0 = df.eval(x0);
		return f_xt;
	}
	
	public <V extends Vector> double search(DifferentiableFunction<V> f,
			   V x0, 
			   final V direction,
			   double f_x0, 
			   V df_x0) {
		double step_length = 1;
		double back_length = step_length;
		int iter_num = 0;
		double f_xt = f_x0;
		double tmp = alpha * direction.dot(df_x0);
		while(iter_num < max_iter_num) {
			iter_num++;
			x0.plusAssign(back_length, direction);
			f_xt = f.calcValue(x0);
			//Armijo condition
			if(f_xt <= f_x0 + step_length * tmp) {
				status = 1;
				df_x0 = f.calcGradient(x0);
				return f_xt;
			}
			
			back_length = step_length * (beta - 1);
			step_length *= beta;
		}
		status = -1;
		df_x0 = f.calcGradient(x0);
		return f_xt;
	}
	
}
