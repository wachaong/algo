package com.autohome.adrd.algo.click_model.optimizer_hadoop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.mapreduce.Job;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.optimizer.MultiModelContext;
import com.autohome.adrd.algo.click_model.optimizer.OneStepLineSearch;
import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * author : wang chao
 */


public abstract class AbstractMultiDataMinimize {

	/**
	 * Implement this function to specify your algorithm
	 */

	//protected HashMap<Integer, Boolean> has_converged ;
	protected HashMap<Integer, Integer> status;
	protected abstract void init_status(int id);
	protected abstract void update_status(int id);
	

	
	//about status
	//protected HashMap<Integer, MultiModelContext> model_context;
	//protected abstract HashMap<Integer, MultiModelContext> init_model_context(HashMap<Integer,MyPair<Double, SparseVector>> first_iter);
	//protected abstract boolean update_model_context(MultiModelContext mc, MyPair<Double, SparseVector> grad_loss);	
	
	
	//about weights
	protected abstract HashMap<Integer, SparseVector> set_weights();
	
	//about grad and loss
	protected abstract HashMap<Integer,MyPair<Double, SparseVector>> calc_grad_loss(HashMap<Integer, SparseVector> weight);
	
	//about optimizer
	//protected abstract void init_optimizer(HashMap<Integer,MyPair<Double, SparseVector>> job_result, HashMap<Integer, SparseVector> weight);
	protected abstract void init_search_direction(int id);
	protected abstract void update_search_direction(int id, Map<Integer,SparseVector> weight_last, 
			Map<Integer,SparseVector> weight,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss_last,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss);
	//protected abstract void update_optimizer(int id);
	protected abstract void init_linesearcher(int id, Map<Integer,MyPair<Double, SparseVector>> grad_loss, Map<Integer, SparseVector> weight_map);
	protected abstract void update_linesearcher(int id,Map<Integer,MyPair<Double, SparseVector>> grad_loss, Map<Integer, SparseVector> weight_map);
	//protected abstract void update_direction(int id);
	//protected abstract void init_step(int id);
	
	//about line search
	protected abstract SparseVector update_step(int id); //	
	//protected abstract void keep_searching();	
	//protected abstract boolean finish_searching(MultiModelContext mc, MyPair<Double, SparseVector> grad_loss);
	
	
	//minimize
	public void minimize()
	{
		
		HashMap<Integer, SparseVector> weight = new HashMap<Integer, SparseVector>();	
		weight = set_weights();
		
		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad = calc_grad_loss(weight);
		HashMap<Integer, SparseVector> weight_last = (HashMap<Integer, SparseVector>) weight.clone();
		
		for(int id : weight.keySet()) {
			init_status(id);
			init_search_direction(id);
			init_linesearcher(id, loss_grad, weight);
			weight.put(id, update_step(id));
		}

		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad_last = loss_grad;
		for(int iter = 0; iter < 10; iter++)
		{
			loss_grad = calc_grad_loss(weight);
			
			for(Map.Entry<Integer, Integer> entry : status.entrySet()) {
				int id = entry.getKey();
				int stat = entry.getValue();
			
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
				
				weight.put(id, update_step(id));

			}
			
		}
	}
}



