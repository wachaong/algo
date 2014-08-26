package com.autohome.adrd.algo.click_model.optimizer.hadoop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.mapreduce.Job;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * author : wang chao
 */


public abstract class AbstractConvexLossMinimize {

	/**
	 * Implement this function to specify your algorithm
	 */

	protected HashMap<Integer, Boolean> has_converged ;
	protected HashMap<Integer, Integer> status;
	protected abstract void init_status(int id);
	protected abstract void update_status(int id);
		
	//about weights
	protected abstract Map<Integer, SparseVector> init_weights();
	protected abstract void save_weights(Map<Integer, SparseVector> weight);
	
	//about grad and loss
	protected abstract HashMap<Integer,MyPair<Double, SparseVector>> calc_grad_loss(Map<Integer, SparseVector> weight,
			int iter);
	
	//about optimizer
	protected abstract void init_search_direction(int id);
	protected abstract void update_search_direction(int id, Map<Integer,SparseVector> weight_last, 
			Map<Integer,SparseVector> weight,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss_last,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss);
	protected abstract void init_linesearcher(int id, Map<Integer,MyPair<Double, SparseVector>> grad_loss, Map<Integer, SparseVector> weight_map);
	protected abstract void update_linesearcher(int id,Map<Integer,MyPair<Double, SparseVector>> grad_loss, Map<Integer, SparseVector> weight_map);
	
	//about line search
	protected abstract SparseVector update_step(int id);
	
	protected abstract int get_max_iter();
	
	//minimize
	public void minimize()
	{
		
		Map<Integer, SparseVector> weight = new HashMap<Integer, SparseVector>();	
		Map<Integer, SparseVector> weight_last = new HashMap<Integer, SparseVector>();
		weight = init_weights();
		weight_last.putAll(weight);
		
		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad = calc_grad_loss(weight, 1);		
		
		for(int id : weight.keySet()) {
			init_status(id);
			has_converged.put(id, loss_grad.get(id).getSecond().norm_2() < 1e-9 ? true : false);
			init_search_direction(id);
			init_linesearcher(id, loss_grad, weight);
			if(!has_converged.get(id))
				weight.put(id, update_step(id));
		}

		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad_last = loss_grad;
		for(int iter = 2; iter < get_max_iter(); iter++)
		{
			loss_grad = calc_grad_loss(weight, iter);
			
			for(Map.Entry<Integer, Integer> entry : status.entrySet()) {
				int id = entry.getKey();
				int stat = entry.getValue();
				has_converged.put(id, loss_grad.get(id).getSecond().norm_2() < 1e-9 ? true : false);
			
				if(status.get(id) == 0) { // find a new direction
					//update_linesearcher()
					update_search_direction(id, weight_last, weight, loss_grad_last, loss_grad);
					init_linesearcher(id, loss_grad, weight);
					status.put(id, 1);
				}
				else { //keep searching
					update_linesearcher(id, loss_grad, weight);
					update_status(id);
				}
				
				if(!has_converged.get(id))
					weight.put(id, update_step(id));

			}
			/*write weight to HDFS*/
			//
			
		}
		save_weights(weight);
	}
}



