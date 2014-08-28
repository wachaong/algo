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

	protected HashMap<Integer, Boolean> new_iter = new HashMap<Integer, Boolean>();
	protected HashMap<Integer, Boolean> has_converged = new HashMap<Integer, Boolean>();
	protected HashMap<Integer, Integer> status = new HashMap<Integer, Integer>();
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
		Map<Integer, SparseVector> weight_tmp = new HashMap<Integer, SparseVector>();

		weight = init_weights();
		for(int id : weight.keySet()) {
			weight_tmp.put(id, (SparseVector)weight.get(id).clone());
		}
		
		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad = calc_grad_loss(weight, 1);
		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad_last = loss_grad;
		
		for(int id : weight.keySet()) {
			init_status(id);
			init_search_direction(id);
			new_iter.put(id, true);
			double grad_norm = loss_grad.get(id).getSecond().norm_2();
			has_converged.put(id, grad_norm < 1e-9 ? true : false);	
		}
		

		//double loss0 = loss_grad.get(1).getFirst();
		for(int iter = 2; iter <= get_max_iter(); iter++)
		{
			for(int id : weight.keySet()) {  //step forward
				if(has_converged.get(id)) 
					continue;
				
				if(new_iter.get(id)) { // find a new direction
					if(iter > 2)
						update_search_direction(id, weight, weight_tmp, loss_grad_last, loss_grad);
					init_linesearcher(id, loss_grad, weight_tmp);
					new_iter.put(id, false);
					weight.put(id, (SparseVector)weight_tmp.get(id).clone());
				}
				
				else{ //keep searching
					update_linesearcher(id, loss_grad, weight_tmp);
				}
				
				weight_tmp.put(id, update_step(id)); //step forward
				update_status(id);
			}
			
			loss_grad = calc_grad_loss(weight_tmp, iter);
			
		
			for(int id : weight.keySet()) {
				if(has_converged.get(id))
					continue;
				
				//update has_converged:
				double norm = loss_grad.get(id).getSecond().norm_2();
				if(norm < 1e-9) {
					weight.put(id, (SparseVector)weight_tmp.get(id).clone());
					has_converged.put(id, true);
				}
				
				else if(status.get(id) == 0) { //next point found
					new_iter.put(id, true);	
				}
			}
			

			loss_grad_last = loss_grad;
		}
		
		save_weights(weight);
	}
}



