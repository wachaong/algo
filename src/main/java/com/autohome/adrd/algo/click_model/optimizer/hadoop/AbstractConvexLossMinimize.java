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
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.AbstractOneStepLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.ISearchDirection;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;
import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * author : wang chao
 */


public abstract class AbstractConvexLossMinimize {

	/**
	 * Implement this function to specify your algorithm
	 */

	protected HashMap<String, Boolean> new_iter = new HashMap<String, Boolean>();
	protected HashMap<String, Boolean> has_converged = new HashMap<String, Boolean>();
	protected HashMap<String, Integer> status = new HashMap<String, Integer>();
	protected Map<String, ISearchDirection> search_direction = new HashMap<String, ISearchDirection>();  
	protected Map<String, AbstractOneStepLineSearch> line_search = new HashMap<String, AbstractOneStepLineSearch>();
	protected int iterationsMaximum;
	protected String input_loc, output_loc, calc_weight_path; 
	protected Configuration conf;
	protected FileSystem fs;
	protected float regularizationFactor;
	protected float sample_freq;
	
	public void SetTrainEnv(Configuration conf, String input_loc, String output_loc, String calc_weight_path, int instance_num, float sample_freq, int iterationsMaximum, float regularizationFactor) {
		this.conf = conf;
		this.input_loc = input_loc;
		this.output_loc = output_loc;
		this.calc_weight_path = calc_weight_path;
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
	
	public abstract void SetJobEnv(Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class, int instance_num);		
	
	protected void update_linesearcher(String id,Map<String,MyPair<Double, SparseVector>> grad_loss, 
			Map<String, SparseVector> weight) {
		line_search.get(id).update(weight.get(id), grad_loss.get(id).getFirst(), grad_loss.get(id).getSecond());
		
	}

	protected void init_status(String id) {
		// TODO Auto-generated method stub
		status.put(id, 1);
		
	}

	protected void update_status(String id) {
		status.put(id, line_search.get(id).getStatus());
	}
	
	protected int get_max_iter() {
		// TODO Auto-generated method stub
		return iterationsMaximum;
	}
	
	protected void update_search_direction(String id, Map<String,SparseVector> weight_last, 
			Map<String,SparseVector> weight,
			Map<String,MyPair<Double, SparseVector>> grad_loss_last,
			Map<String,MyPair<Double, SparseVector>> grad_loss) {
		search_direction.get(id).update(weight_last.get(id), weight.get(id),
										grad_loss_last.get(id).getFirst(), grad_loss.get(id).getFirst(), 
										grad_loss_last.get(id).getSecond(), grad_loss.get(id).getSecond()); 

	}
			
	//about weights
	protected Map<String, SparseVector> init_weights() {
		// TODO Auto-generated method stub		
		Map<String, SparseVector> weight_maps = IterationHelper.readSparseVectorMapFast(fs, new Path(calc_weight_path));
		return weight_maps;
	}
	
	protected void save_weights(Map<Integer, SparseVector> weight) {
		IterationHelper.writeSparseVectorMap(fs, new Path(calc_weight_path), weight);
		
	}
	
	//about grad and loss
	protected abstract HashMap<String,MyPair<Double, SparseVector>> calc_grad_loss(Map<String, SparseVector> weight,
			int iter);
	
	//about optimizer
	protected abstract void init_search_direction(String id);
	protected abstract void init_linesearcher(String id, Map<String,MyPair<Double, SparseVector>> grad_loss, Map<String, SparseVector> weight_map);
	
	
	//about line search
	protected abstract SparseVector update_step(String id);
	
	//minimize
	public void minimize()
	{
		
		Map<String, SparseVector> weight = new HashMap<String, SparseVector>();	
		Map<String, SparseVector> weight_tmp = new HashMap<String, SparseVector>();

		weight = init_weights();
		for(String id : weight.keySet()) {
			weight_tmp.put(id, (SparseVector)weight.get(id).clone());
		}
		
		
		HashMap<String,MyPair<Double, SparseVector>> loss_grad = calc_grad_loss(weight, 1);
		HashMap<String,MyPair<Double, SparseVector>> loss_grad_tmp 
				= new HashMap<String,MyPair<Double, SparseVector>>();
		for(Map.Entry<String, MyPair<Double, SparseVector>> ent : loss_grad.entrySet()) {
			String id = ent.getKey();
			MyPair<Double, SparseVector> pair = ent.getValue();
			double loss = pair.getFirst();
			SparseVector grad = pair.getSecond();
			loss_grad_tmp.put(id, new MyPair<Double, SparseVector>(loss, (SparseVector)grad.clone()));
			
		}
		
		for(String id : weight.keySet()) {
			init_status(id);
			//new lbfgs
			init_search_direction(id);
			new_iter.put(id, true);
			double grad_norm = loss_grad.get(id).getSecond().norm_2();
			System.out.println("grad_norm " + String.valueOf(id) + String.valueOf(grad_norm));			
			has_converged.put(id, grad_norm < 1e-9 ? true : false);	
		}
		
		double loss0 = loss_grad.get(1).getFirst();
		System.out.println("loss is :" + loss0);
		double loss1;
		System.out.println("max iter " + get_max_iter());
		for(int iter = 2; iter <= get_max_iter(); iter++)
		{
			boolean converge_flag = true;
			for(String id : weight.keySet()) {  //step forward
				if(has_converged.get(id))
				{
					System.out.println("converged " + String.valueOf(id));
					continue;
				}
				converge_flag = false;
				if(new_iter.get(id)) { // find a new direction
					System.out.println("iter " + iter + " a new direction");
					
					if(iter > 2) {
						update_search_direction(id, weight, weight_tmp, loss_grad_tmp, loss_grad);
						double loss = loss_grad.get(id).getFirst();
						SparseVector grad = loss_grad.get(id).getSecond();
						loss_grad_tmp.put(id, new MyPair<Double, SparseVector>(loss, (SparseVector)grad.clone()));
					}
					//init hessian dir, when iter2, give minus grad dir
					init_linesearcher(id, loss_grad, weight_tmp);
					new_iter.put(id, false);
					//weight keeps newest effective point
					weight.put(id, (SparseVector)weight_tmp.get(id).clone());
				}
				
				System.out.println("step forword:");
				weight_tmp.put(id, update_step(id)); //step forward
				
			}
			
			if(converge_flag)
				break;

			loss_grad = calc_grad_loss(weight_tmp, iter);
			
			loss1 = loss_grad.get(1).getFirst();
			System.out.println("loss is :" + loss1);
			System.out.println("loss diff is :" + (loss0 - loss1));
			loss0 = loss1;
			
		
			for(String id : weight.keySet()) {
				if(has_converged.get(id))
					continue;
				
				//update has_converged:
				double norm = loss_grad.get(id).getSecond().norm_2();
				if(norm < 1e-9) {
					weight.put(id, (SparseVector)weight_tmp.get(id).clone());
					has_converged.put(id, true);
					continue;
				}
				
				//based on weight_tmp, loss_grad, judge if search finishing, updating stepsize
				update_linesearcher(id, loss_grad, weight_tmp);
				//review inner searching status
				update_status(id);
				
				if(status.get(id) == 0) { //next point found
					new_iter.put(id, true);	
				} 
				else {			
					System.out.println("iter " + iter + " keep searching");
				}	
			}
		}
		//save_weights(weight);
	}
}



