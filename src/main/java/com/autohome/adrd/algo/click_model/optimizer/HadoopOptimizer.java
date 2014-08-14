package com.autohome.adrd.algo.click_model.optimizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.Vector;

public interface HadoopOptimizer<V extends Vector> {
	public void setup(String configure_file);
	//public void minimize(DifferentiableFunction<V> f, V x0);
	public void minimize(Configuration conf,
			   Class<? extends Mapper> mapper_class,
			   Class<? extends Reducer> reducer_class,
			   Class<? extends Reducer> combiner_class,
			   Path dataset_path,
			   Path weight_in_path,   // initial x0
			   Path weight_out_path
			   );
	public int getStatus();

}

