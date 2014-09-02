package com.autohome.adrd.algo.click_model.optimizer.single;

import com.autohome.adrd.algo.click_model.data.Vector;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.IDifferentiableFunction;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.IGradientFunction;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.ILineSearch;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.IObjectFunction;

public class WolfeLineSearch implements ILineSearch {

	private final double stepLength = 1;  //initial step size
	private double c1 = 0.1;
	private double c2 = 0.9;
	private int MAX_ITER_NUM = 1000;
	private int status = 0;

	WolfeLineSearch() {
	}

	WolfeLineSearch(double _c1, double _c2) {
		c1 = _c1;
		c2 = _c2;
	}

	public double getC1() {
		return c1;
	}

	public double getC2() {
		return c2;
	}

	public void setC1(double _c1) {
		c1 = _c1;
	}

	public void setC2(double _c2) {
		c2 = _c2;
	}

	public int getStatus() {
		return status;
	}

	public int getMaxIterNum() {
		return MAX_ITER_NUM;
	}

	public void setMaxIterNum(int N) {
		MAX_ITER_NUM = N;
	}



	public <V extends Vector> double search(IObjectFunction<V> f, IGradientFunction<V> df,
			V x0, 
			final V d,
			double f_x0,
			V df_x0) {
		double leftBound = 0.0;
		double rightBound = Double.MAX_VALUE;

		double f_xt;
		V df_xt = null;
		double alpha = stepLength;
		double ddt, dd0 = d.dot(df_x0);

		int iterNum = 0;
		while(iterNum < MAX_ITER_NUM) {
			++iterNum;
			x0.plusAssign(alpha, d);

			//xt.assignTmp(BLAS.add(x0, d.scale(alpha)));  //xt = x0 + alpha * d
			f_xt = f.eval(x0);
			df_xt = df.eval(x0);
			ddt = d.dot(df_xt);

			//check Armijo condition
			if(f_xt > f_x0 + c1 * alpha * dd0) {
				rightBound = alpha;
				alpha = (leftBound + rightBound) / 2;
			}

			//check Wolfe condition
			else if(ddt < c2 * dd0) {
				leftBound = alpha;
				alpha = (leftBound + rightBound) / 2;
			}

			else {
				status = 0;
				df_x0 = df_xt;
				return f_xt;

			}
		}
		status = 1;
		return f.eval(x0);
	}

	public <V extends Vector> double search(IDifferentiableFunction<V> f,
			V x0, 
			final V d,
			double f_x0,
			V df_x0) {
		double leftBound = 0.0;
		double rightBound = Double.MAX_VALUE;

		double f_xt;
		V df_xt = null;
		double alpha = stepLength;
		double ddt, dd0 = d.dot(df_x0);

		int iterNum = 0;
		while(iterNum < MAX_ITER_NUM) {
			++iterNum;
			x0.plusAssign(alpha, d);

			//xt.assignTmp(BLAS.add(x0, d.scale(alpha)));  //xt = x0 + alpha * d
			f_xt = f.calcValue(x0);
			df_xt = f.calcGradient(x0);
			ddt = d.dot(df_xt);

			//check Armijo condition
			if(f_xt > f_x0 + c1 * alpha * dd0) {
				rightBound = alpha;
				alpha = (leftBound + rightBound) / 2;
			}

			//check Wolfe condition
			else if(ddt < c2 * dd0) {
				leftBound = alpha;
				alpha = (leftBound + rightBound) / 2;
			}

			else {
				status = 0;
				df_x0 = df_xt;
				return f_xt;

			}
		}
		status = 1;
		return f.calcValue(x0);
	}


}
