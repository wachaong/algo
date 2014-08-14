package com.autohome.adrd.algo.click_model.optimizer;


import java.io.IOException;

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
	 * status = -1 : has reached the maximum iteration number.
	 * status = 1 : other 
	 */

	HadoopWolfeLineSearch() {
	}

	HadoopWolfeLineSearch(double _c1, double _c2) {
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

}

