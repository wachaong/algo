package com.autohome.adrd.algo.click_model.optimizer;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.utility.*;
import com.autohome.adrd.algo.click_model.io.DriverIOHelper;

public class HadoopLBFGS implements HadoopOptimizer {
	
	private int M = 10;
	private int MAX_ITER_NUM = 100;
	private final double TOL = 1e-10;
	private int status = 1;
	private HadoopLineSearch searcher = new HadoopWolfeLineSearch();
	
	public void setup(String configure_file) {
		//to do.
	}
	
	public void setM(int _M) {
		M = _M;
	}
	
	public int getM() {
		return M;
	}
	
	public void setMaxIterNum(int max_iter_num) {
		MAX_ITER_NUM = max_iter_num;
	}
	
	public int getMaxIterNum(int max_iter_num) {
		return MAX_ITER_NUM;
	}
	
	public int getStatus() {
		return status;
	}

	@Override
	public void minimize(Configuration conf,
			   Class mapper_class,
			   Class reducer_class,
			   Class combiner_class,
			   Path dataset_path,
			   Path weight_in_path,   // initial x0
			   Path weight_out_path
			   )  {
		// TODO Auto-generated method stub
		
		//init the weight
		SparseVector x0 = new SparseVector();
		x0 = CommonFunc.readSparseVector(weight_in_path.toString(), "\t", 0, 1, "utf-8");
		DriverIOHelper driver = new DriverIOHelper();
		
		Job job = null;
		try {
			job = new Job(conf);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//calc 
		try {
			driver.doLbfgsIteration(job, dataset_path.toString(), weight_out_path.toString(), "", "",
					mapper_class, reducer_class, combiner_class, false, 1, 0L, 0.1f, 1.0f);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		SparseVector df_x0 = CommonFunc.readSparseVector(weight_in_path.toString(), "\t", 0, 1, "utf-8");
		double f_x0 = df_x0.getValue(-1);
		df_x0.getData().remove(-1);
		
		SparseVector xt = (SparseVector)x0.clone();
		SparseVector df_xt = (SparseVector)df_x0.clone();
	    double f_xt = f_x0;
		
	    LBFGS<SparseVector> lbfgs = new LBFGS<SparseVector>();
		int iter_num = 0;
		int m = 0;
		SparseVector q = null;
		SparseVector d = null;
		LinkedList<SparseVector> s = new LinkedList<SparseVector>();
		LinkedList<SparseVector> y = new LinkedList<SparseVector>();
		LinkedList<Double> rho = new LinkedList<Double>();
		 
		 while(iter_num < MAX_ITER_NUM && df_xt.norm_2() > TOL) {
			 iter_num++;
			 
			 //compute search direction
			 //d = - H0 * dfx0.
			 q = (SparseVector)df_x0.clone();
			 d = lbfgs.LBFGSLoop(q, s, y, rho);
			 d.scaleAssign(-1.0);
			 
			 try {
				searcher.search(conf, mapper_class, reducer_class, combiner_class, dataset_path,
						 weight_in_path, weight_out_path, f_x0, df_x0, df_x0, d);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 if(m >= M) {
				 s.pop();
				 y.pop();
				 rho.pop();
			 }
			 
			 ++m;
			 s.add((SparseVector)(xt.minus(x0)));
			 y.add((SparseVector) df_xt.minus(df_x0));
			 rho.add(1.0 / y.getLast().dot(s.getLast()));
			 
			 x0 = (SparseVector)xt.clone();
			 df_x0 = (SparseVector)df_xt.clone();
			 f_x0 = f_xt;
		 }
		 
		 if(iter_num == MAX_ITER_NUM) 
			 status = -1;
		 else
			 status = 0;
		
		
	}

	
}
