package com.autohome.adrd.algo.click_model.optimizer;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;





public interface HadoopLineSearch {
	
	/*public void search(Configuration conf,
					   Path dataset_path,
					   Path weight_in_path,   // initial x0
					   Path weight_out_path,  //
					   SparseVector x0,
					   SparseVector direction
					   );*/
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
			   ) throws IOException, InterruptedException, ClassNotFoundException;
	
	//multidata
	public void search(Configuration conf,
			   Class<? extends Mapper> mapper_class,
			   Class<? extends Reducer> reducer_class,
			   Class<? extends Reducer> combiner_class,
			   Path dataset_path,
			   Path weight_in_path,   // initial x0
			   Path out_path,  //
			   Double f_x0,	//
			   Map<Integer, SparseVector> df_x0s,
			   Map<Integer, SparseVector> x0s,
			   Map<Integer, SparseVector> directions
			   ) throws IOException, InterruptedException, ClassNotFoundException;
}
