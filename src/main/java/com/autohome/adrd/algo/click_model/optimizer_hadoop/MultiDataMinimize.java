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
//import com.autohome.adrd.algo.click_model.optimizer.MultiModelContext;
import com.autohome.adrd.algo.click_model.optimizer.OneStepLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.OneStepWolfeLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.SearchDirection;
import com.autohome.adrd.algo.click_model.utility.MyPair;
import com.autohome.adrd.algo.click_model.optimizer.LbfgsSearchDirection;

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
	private Map<Integer, SearchDirection> search_direction = new HashMap<Integer, SearchDirection>();  
	private Map<Integer, OneStepLineSearch> line_search = new HashMap<Integer, OneStepLineSearch>();
	//private Map<Integer, SparseVector> weights_map = new HashMap<Integer, SparseVector>();
	private Map<Integer, MyPair<Double, SparseVector>> loss_grad = null;
	
	
	
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
	
//

	@Override
	protected HashMap<Integer, SparseVector> set_weights() {
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	protected HashMap<Integer, MyPair<Double, SparseVector>> calc_grad_loss(HashMap<Integer, SparseVector> weight) {
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
	protected SparseVector update_step(int id) {
		return line_search.get(id).getNextPoint();
	}


	@Override
	protected void init_search_direction(int id) {
		LbfgsSearchDirection d = new LbfgsSearchDirection();
		search_direction.put(id, d);	
	}

	@Override
	protected void update_search_direction(int id, Map<Integer,SparseVector> weight_last, 
			Map<Integer,SparseVector> weight,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss_last,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss) {
		search_direction.get(id).update(weight_last.get(id), weight.get(id),
										grad_loss_last.get(id).getFirst(), grad_loss.get(id).getFirst(), 
										grad_loss_last.get(id).getSecond(), grad_loss.get(id).getSecond()); 

	}

	@Override
	protected void init_linesearcher(int id,
			 Map<Integer,MyPair<Double, SparseVector>> grad_loss, 
			 Map<Integer, SparseVector> weights_map) {
		
		OneStepWolfeLineSearch ls = new OneStepWolfeLineSearch();
		ls.setX0(weights_map.get(id));
		ls.setF_x0(loss_grad.get(id).getFirst());
		ls.setDf_x0(loss_grad.get(id).getSecond());
		ls.setDirection(search_direction.get(id).calcSearchDirction(loss_grad.get(id).getSecond()));
		line_search.put(id, ls);
	}

	@Override
	protected void update_linesearcher(int id,Map<Integer,MyPair<Double, SparseVector>> grad_loss, 
			Map<Integer, SparseVector> weight) {
		line_search.get(id).update(weight.get(id), grad_loss.get(id).getFirst(), grad_loss.get(id).getSecond());
		
	}

	@Override
	protected void init_status(int id) {
		// TODO Auto-generated method stub
		status.put(id, 1);
		
	}

	@Override
	protected void update_status(int id) {
		status.put(id, line_search.get(id).getStatus());
	}

}
