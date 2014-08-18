package com.autohome.adrd.algo.click_model.optimizer;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;  
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.autohome.adrd.algo.click_model.data.SparseVector;

public class HadoopWolfeLineSearch implements HadoopLineSearch {
	private final double stepLength = 1;  //initial step size
	private double c1 = 0.1;
	private double c2 = 0.9;
	private int MAX_ITER_NUM = 1000;
	private int status = 1;
	/*
	 * status = 0 : converged
	 * status = -1: has reached the maximum iteration number.
	 * status = 1 : other 
	 */

	HadoopWolfeLineSearch() {
	}

	HadoopWolfeLineSearch(double _c1, double _c2) {
		c1 = _c1;
		c2 = _c2;
		assert(0 < c1);
		assert(c1 < 0.5);
		assert(c1 < c2);
		assert(0.5 < c2);
		assert(c2 < 1);
		
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

	public void search(Configuration conf,
			   Class<? extends Mapper> mapper_class,
			   Class<? extends Reducer> reducer_class,
			   Class<? extends Reducer> combiner_class,
			   Path dataset_path,
			   Path weight_in_path,   // initial x0
			   Path out_path,  //
			   Double f_x0,	//
			   SparseVector df_x0,
			   SparseVector x0,
			   SparseVector direction
			   ) throws IOException, InterruptedException, ClassNotFoundException {
		double leftBound = 0.0;
		double rightBound = Double.MAX_VALUE;


		double alpha = stepLength;
		double f_xt = 0;
		SparseVector df_xt = null;
		double ddt, dd0 = direction.dot(df_x0);

		int iterNum = 0;
		while(iterNum < MAX_ITER_NUM) {
			++iterNum;
			x0.plusAssign(alpha, direction);

			//xt.assignTmp(BLAS.add(x0, d.scale(alpha)));  //xt = x0 + alpha * d

			
			Job job = new Job(conf);
			job.setJobName("Wolfe Line Search : iteration" + iterNum);
			job.setMapperClass(mapper_class);
			job.setReducerClass(reducer_class);
			job.setCombinerClass(combiner_class);
			
/*	        FileSystem fs = FileSystem.get(conf);
	        if (fs.exists(weight_out_path)) {
	            fs.delete(weight_out_path, true);
	        }*/
	        
	        FileInputFormat.setInputPaths(job, dataset_path);
	        FileOutputFormat.setOutputPath(job,out_path);
	        job.waitForCompletion(true);
	        


	       // RunningJob job = JobClient.runJob(conf);

			//* reading output
	        //f_xt = readFromHdfs()
			//ft = f.eval(x0);
			//df_xt = df.eval(x0);
			ddt = direction.dot(df_xt);

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
				f_x0 = f_xt;
				//return f_xt;

			}
		}
		status = 1;
		//return f.eval(x0);
	}

	@Override
	public void search(Configuration conf,
			Class<? extends Mapper> mapper_class,
			Class<? extends Reducer> reducer_class,
			Class<? extends Reducer> combiner_class, Path dataset_path,
			Path weight_in_path, Path out_path, Double f_x0,
			Map<Integer, SparseVector> df_x0s, Map<Integer, SparseVector> x0s,
			Map<Integer, SparseVector> directions) throws IOException,
			InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		double leftBound = 0.0;
		double rightBound = Double.MAX_VALUE;


		double alpha = stepLength;
		Map<Integer, Double> alphas = new 
		Map<Integer, Double> f_xts = new HashMap<Integer, Double>();
		Map<Integer, SparseVector> df_xts = new HashMap<Integer, SparseVector>();
		Map<Integer, Double> ddts = new HashMap<Integer, Double>();
		Map<Integer, Double> dd0s = new HashMap<Integer, Double>();
		
		//double ddt, dd0 = direction.dot(df_x0);
		Integer id = null;
		SparseVector df_x0 = null;
		for(Map.Entry<Integer, SparseVector> elem : df_x0s.entrySet()) {
			id = elem.getKey();
			df_x0 = elem.getValue();
			dd0s.put(id, directions.get(id).dot(df_x0));
		}
		

		int iterNum = 0;
		while(iterNum < MAX_ITER_NUM) {
			++iterNum;
			
			//step forward
			SparseVector x0 = null;
			for(Map.Entry<Integer, SparseVector> elem : x0s.entrySet()) {
				id = elem.getKey();
				x0 = elem.getValue();
				x0.plusAssign(alpha, directions.get(id));
			}
			

			//xt.assignTmp(BLAS.add(x0, d.scale(alpha)));  //xt = x0 + alpha * d

			
			Job job = new Job(conf);
			job.setJobName("Wolfe Line Search : iteration" + iterNum);
			job.setMapperClass(mapper_class);
			job.setReducerClass(reducer_class);
			job.setCombinerClass(combiner_class);
			
/*	        FileSystem fs = FileSystem.get(conf);
	        if (fs.exists(weight_out_path)) {
	            fs.delete(weight_out_path, true);
	        }*/
	        
	        FileInputFormat.setInputPaths(job, dataset_path);
	        FileOutputFormat.setOutputPath(job,out_path);
	        job.waitForCompletion(true);
	        


	       // RunningJob job = JobClient.runJob(conf);

			//* reading output
	        //f_xt = readFromHdfs()
			//ft = f.eval(x0);
			//df_xts = df.eval(x0);
			//ddt = direction.dot(df_xt);
			
			for(Map.Entry<Integer, SparseVector> elem : df_xts.entrySet()) {
				id = elem.getKey();
				df_x0 = elem.getValue();
				ddts.put(id, directions.get(id).dot(df_x0));
			}

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
				f_x0 = f_xt;
				//return f_xt;

			}
		}
		status = 1;
		//return f.eval(x0);
	}

}

