package com.autohome.adrd.algo.click_model.optimizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class HadoopLBFGS implements HadoopOptimizer {
	
	private int M = 10;
	private int MAX_ITER_NUM = 100;
	private final double TOL = 1e-10;
	private int status = 1;
	
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
	public void minimize(Configuration conf, Class mapper_class,
			Class reducer_class, Class combiner_class, Path dataset_path,
			Path weight_in_path, Path weight_out_path) {
		// TODO Auto-generated method stub
		
	}
	
	
}
