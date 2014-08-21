package com.autohome.adrd.algo.click_model.test;


import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.optimizer.LbfgsSearchDirection;
import com.autohome.adrd.algo.click_model.optimizer.AbstractOneStepLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.OneStepWolfeLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.SearchDirection;

public class AbstractOneStepLineSearchTest {
	
	private SparseVector x0 = new SparseVector();;
	private SparseVector df_x0 = new SparseVector();;
	private double f_x0;
	private SparseVector x1 = new SparseVector();;
	private SearchDirection d = new LbfgsSearchDirection();
	private AbstractOneStepLineSearch ls = new OneStepWolfeLineSearch();
	
	private double f(SparseVector x) {
		return x.dot(x);
	}
	
	private SparseVector df(SparseVector x) {
		return (SparseVector) x.scale(2.0);
	}
	


	public void setUp() throws Exception {
		x0 = new SparseVector();
		x0.setValue(1, 8);
		x0.setValue(2, 1);
		df_x0 = df(x0);
		f_x0 = f(x0);
		//ls.setFunctionValue(f_x0);
		//ls.setFunctionGradient(df_x0);
		//ls.setSearchPoint(x0);
		
	}

	public void test() {
		while(df_x0.norm_2() > 1e-10) {
			SparseVector dir = d.calcSearchDirction(df_x0);
			ls = new OneStepWolfeLineSearch();
			ls.set(x0, f_x0, dir, dir);
			//ls.setSearchDirection(dir);
			x1 = ls.getNextPoint();
			double f_x1 = f(x1);
			SparseVector df_x1 = df(x1);
			while(ls.getStatus() != 0) {
				
				System.out.println(x1);
				
				
				ls.update(x1, f_x1, df_x1);
				
			}
			d.update(x0, x1, f_x0, f_x1, df_x0, df_x1);
			x0 = x1;
			f_x0 = f_x1;
			df_x0 = df_x1;
			
		}
		
	}

}
