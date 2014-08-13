package com.autohome.adrd.algo.click_model.optimizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;

import com.autohome.adrd.algo.click_model.data.SparseVector;





public interface HadoopLineSearch {
	
	public void search(Configuration conf,
					   Path dataset_path,
					   Path weight_in_path,   // initial x0
					   Path weight_out_path,  //
					   SparseVector x0,
					   SparseVector direction
					   );
}
