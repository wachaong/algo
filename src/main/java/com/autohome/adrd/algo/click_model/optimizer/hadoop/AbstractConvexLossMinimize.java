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
		Map<Integer, SparseVector> weight_last = new HashMap<Integer, SparseVector>();
		weight = init_weights();
		weight_last.putAll(weight);
		
		System.out.println("iteration 1 begins");
		
		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad = calc_grad_loss(weight, 1);		
		
		for(int id : weight.keySet()) {
			System.out.println("1. init_status begins");
			init_status(id);
			System.out.println("init_status ends");
			
			System.out.println("2. has_converged begins");
			has_converged.put(id, loss_grad.get(id).getSecond().norm_2() < 1e-9 ? true : false);
			System.out.println("has_converged ends");
			
			System.out.println("3. init_search_direction begins");
			init_search_direction(id);
			System.out.println("init_search_direction ends");
			
			System.out.println("4. init_linesearcher begins");
			init_linesearcher(id, loss_grad, weight);
			System.out.println("init_linesearcher ends");
			
			System.out.println("5. weight.put begins");
			if(!has_converged.get(id))
				weight.put(id, update_step(id));
			System.out.println("weight.put ends");
		}

		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad_last = loss_grad;
		for(int iter = 2; iter <= get_max_iter(); iter++)
		{
			loss_grad = calc_grad_loss(weight, iter);
			
			for(Map.Entry<Integer, Integer> entry : status.entrySet()) {
				int id = entry.getKey();
				int stat = entry.getValue();
				System.out.println("6. has_converged begins");
				has_converged.put(id, loss_grad.get(id).getSecond().norm_2() < 1e-9 ? true : false);
				System.out.println("has_converged ends");
			
				if(status.get(id) == 0) { // find a new direction
					//update_linesearcher()
					
					System.out.println("7. update_search_direction begins");
					update_search_direction(id, weight_last, weight, loss_grad_last, loss_grad);
					System.out.println("update_search_direction ends");
					
					System.out.println("8. init_linesearcher begins");
					init_linesearcher(id, loss_grad, weight);
					System.out.println("init_linesearcher ends");
					status.put(id, 1);
				}
				
				else { //keep searching
					System.out.println("9. update_linesearcher begins");
					update_linesearcher(id, loss_grad, weight);
					System.out.println("update_linesearcher ends");
					update_status(id);
				}
				
				System.out.println("10. weight.put begins");
				if(!has_converged.get(id))
					weight.put(id, update_step(id));
				System.out.println("weight.put ends");

			}
			/*write weight to HDFS*/
			//
			
		}
		save_weights(weight);
	}
}



