package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class SingleOptimizer {
	//public OneStepWolfeLineSearch line_searcher = new OneStepWolfeLineSearch();
	//public AbstractOneStepLineSearch line_searcher = new OneStepWolfeLineSearch();
	public OneStepBacktrackingLineSearch line_searcher = new OneStepBacktrackingLineSearch();
	public LbfgsSearchDirection search_direction = new LbfgsSearchDirection();
	private double tol = 1e-5;
	private int max_iter_num = 100;
	
	public SingleOptimizer() {
	}
	
	//public setLineSearcher(AbstractOneStepLineSearch)
	
	public int minimize(IDifferentiableFunction<SparseVector> f, SparseVector x0) {
		int status = 0;
		MyPair<Double, SparseVector> pair = f.calcValueGradient(x0);
		Double f_x0 = pair.getFirst();
		SparseVector df_x0 = pair.getSecond();
		if(df_x0.norm_2() < tol) {
			return 0;
		}
		search_direction = new LbfgsSearchDirection();	
		
		for(int i = 0; i < max_iter_num; ++i) {
			if(status == 0) { //find a new direction
				line_searcher = new OneStepBacktrackingLineSearch();
				//line_searcher = new OneStepWolfeLineSearch();
				line_searcher.set(x0, f_x0, df_x0, search_direction.calcSearchDirction(df_x0));
				status = 1;
			}
			SparseVector xt = line_searcher.getNextPoint();
			MyPair<Double, SparseVector> tmp = f.calcValueGradient(xt);
			Double f_xt = tmp.getFirst();
			SparseVector df_xt = tmp.getSecond();
			
			line_searcher.update(xt, f_xt, df_xt);
			System.out.println("loss :");
			System.out.println(f_xt);
			System.out.println("grad :");
			System.out.println(df_xt);
			status = line_searcher.getStatus();
			search_direction.update(x0, xt, f_x0, f_xt, df_x0, df_xt);
			
			x0 = xt;
			f_x0 = f_xt;
			df_x0 = df_xt;
			System.out.println(x0);
			
			if(df_x0.norm_2() < tol) {
				return 0;
			}
		}
		return -1;
	}
}
