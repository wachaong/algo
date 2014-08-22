package com.autohome.adrd.algo.click_model.optimizer.hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.io.DriverIOHelper;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.optimizer.AbstractOneStepLineSearch;
//import com.autohome.adrd.algo.click_model.optimizer.MultiModelContext;
import com.autohome.adrd.algo.click_model.optimizer.OneStepWolfeLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.ISearchDirection;
import com.autohome.adrd.algo.click_model.utility.MyPair;
import com.autohome.adrd.algo.click_model.optimizer.LbfgsSearchDirection;

/**
 * author : wang chao ; yangmingmin
 */

public class ConvexLossMinimize extends AbstractConvexLossMinimize{

	private String input_loc, output_loc, init_weight_path, calc_weight_path; 
	private Class<? extends Mapper> mapper_class;
	private Class<? extends Reducer> reduce_class;
	private Class<? extends Reducer> combine_class;
	Configuration conf;
	FileSystem fs;
	private int instance_num;
	private int iterationsMaximum;
	private float regularizationFactor;
	private float sample_freq;
	private Map<Integer, ISearchDirection> search_direction = new HashMap<Integer, ISearchDirection>();  
	private Map<Integer, AbstractOneStepLineSearch> line_search = new HashMap<Integer, AbstractOneStepLineSearch>();
	//private Map<Integer, MyPair<Double, SparseVector>> loss_grad = null;
	
	
	
	public void SetTrainEnv(Configuration conf, 
			String input_loc, String output_loc, String init_weight_path, String calc_weight_path,
			Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class,
			int instance_num, float sample_freq, int iterationsMaximum, float regularizationFactor)
	{
		this.conf = conf;
		this.input_loc = input_loc;
		this.output_loc = output_loc;
		this.init_weight_path = init_weight_path;
		this.calc_weight_path = calc_weight_path;
		this.mapper_class = mapper_class;
		this.combine_class = combine_class;
		this.reduce_class = reduce_class;
		this.instance_num = instance_num;
		this.sample_freq = sample_freq;
		this.regularizationFactor = regularizationFactor;
		this.iterationsMaximum = iterationsMaximum;
		try {
			fs = FileSystem.get(conf);
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
	protected HashMap<Integer, MyPair<Double, SparseVector>> calc_grad_loss(HashMap<Integer, SparseVector> weight,
			int iter) {
		// TODO Auto-generated method stub
		DriverIOHelper driver_io = new DriverIOHelper();
		HashMap<Integer, MyPair<Double, SparseVector>> result = new HashMap<Integer, MyPair<Double, SparseVector>>();
		try {
			//save weight
			
			
			driver_io.doLbfgsIteration(conf, input_loc, output_loc, init_weight_path, calc_weight_path, 
					mapper_class, reduce_class, combine_class, iter, instance_num, regularizationFactor , sample_freq);
			
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
			 Map<Integer,MyPair<Double, SparseVector>> loss_grad, 
			 Map<Integer, SparseVector> weights_map) {
		
		OneStepWolfeLineSearch ls = new OneStepWolfeLineSearch();
		ls.set(weights_map.get(id),
				loss_grad.get(id).getFirst(),
				loss_grad.get(id).getSecond(),
				search_direction.get(id).calcSearchDirction(loss_grad.get(id).getSecond()));

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

	@Override
	protected int get_max_iter() {
		// TODO Auto-generated method stub
		return iterationsMaximum;
	}

}
