package com.autohome.adrd.algo.click_model.optimizer_hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.io.DriverIOHelper;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.optimizer.MultiModelContext;
import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * author : wang chao
 */

public class MultiDataMinimize extends AbstractMultiDataMinimize{

	private Job job;
	private String input_loc, output_loc, bucket_map_path, init_weight_path; 
	private Class<? extends Mapper> mapper_class;
	private Class<? extends Reducer> reduce_class;
	private Class<? extends Reducer> combine_class;
	FileSystem fs;
	
	public void SetHadoopEnv(Job job, 
			String input_loc, String output_loc, String bucket_map_path, String init_weight_path, 
			Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class)
	{
		this.job = job;
		this.input_loc = input_loc;
		this.output_loc = output_loc;
		this.bucket_map_path = bucket_map_path;
		this.init_weight_path = init_weight_path;
		this.mapper_class = mapper_class;
		this.combine_class = combine_class;
		this.reduce_class = reduce_class;
		try {
			fs = FileSystem.get(job.getConfiguration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected HashMap<Integer, MultiModelContext> init_model_context(HashMap<Integer, MyPair<Double, SparseVector>> first_iter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean update_model_context(MultiModelContext mc, MyPair<Double, SparseVector> grad_loss) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void set_weights() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected HashMap<Integer, MyPair<Double, SparseVector>> cal_grad_loss(HashMap<Integer, SparseVector> weight) {
		// TODO Auto-generated method stub
		DriverIOHelper driver_io = new DriverIOHelper();
		HashMap<Integer, MyPair<Double, SparseVector>> result = new HashMap<Integer, MyPair<Double, SparseVector>>();
		try {
			
			
			driver_io.doLbfgsIteration(job, input_loc, output_loc, bucket_map_path, init_weight_path, 
					mapper_class, reduce_class, combine_class, true, true, 9999, 3.0 , 0.1);
			
			Map<Integer,SparseVector> grads = IterationHelper.readSparseVectorMap(fs, new Path(output_loc));
			
			Iterator<Entry<Integer, SparseVector>> grads_iter = grads.entrySet().iterator();
			while (grads_iter.hasNext()) {
				Entry<Integer, SparseVector> entry = grads_iter.next();
				double loss = entry.getValue().getValue(-1);
				entry.getValue().getData().remove(-1);				
				MyPair<Double, SparseVector> pair = new MyPair<Double, SparseVector>(loss, entry.getValue());
				result.put(entry.getKey(), pair);
			}
			return result;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void init_optimizer(HashMap<Integer, MyPair<Double, SparseVector>> job_result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update_optimizer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update_direction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected SparseVector update_step() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void keep_searching() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean finish_searching(MultiModelContext mc, MyPair<Double, SparseVector> grad_loss) {
		// TODO Auto-generated method stub
		return false;
	}

}
