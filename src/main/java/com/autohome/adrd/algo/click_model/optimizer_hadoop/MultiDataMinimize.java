package com.autohome.adrd.algo.click_model.optimizer_hadoop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.mapreduce.Job;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.optimizer.MultiModelContext;
import com.autohome.adrd.algo.click_model.utility.MyPair;

/**
 * author : wang chao
 */


public abstract class MultiDataMinimize {

	/**
	 * Implement this function to specify your algorithm
	 */
	
	//about status
	protected HashMap<Integer, MultiModelContext> model_context;
	protected abstract HashMap<Integer, MultiModelContext> init_model_context(HashMap<Integer,MyPair<Double, SparseVector>> first_iter);
	protected abstract boolean update_model_context(MultiModelContext mc, MyPair<Double, SparseVector> grad_loss);	
	
	//about weights
	protected abstract void set_weights();
	
	//about grad and loss
	protected abstract HashMap<Integer,MyPair<Double, SparseVector>> cal_grad_loss(HashMap<Integer, SparseVector> weight);
	
	//about optimizer
	protected abstract void init_optimizer(HashMap<Integer,MyPair<Double, SparseVector>> job_result);
	protected abstract void update_optimizer();
	protected abstract void update_direction();
	
	//about line search
	protected abstract SparseVector update_step();	
	protected abstract void keep_searching();	
	protected abstract boolean finish_searching(MultiModelContext mc, MyPair<Double, SparseVector> grad_loss);
	
	//minimize
	public void minimize()
	{
		
		HashMap<Integer, SparseVector> weight = new HashMap<Integer, SparseVector>();	//temp
		HashMap<Integer,MyPair<Double, SparseVector>> iter_result = cal_grad_loss(weight);
		init_optimizer(iter_result);
		model_context = init_model_context(iter_result);
		
		for(int iter = 0; iter < 10; iter++)
		{
			Iterator<Entry<Integer, MultiModelContext>> models_iter = model_context.entrySet().iterator();
			while (models_iter.hasNext()) {
				Entry<Integer, MultiModelContext> entry = models_iter.next();
				if(entry.getValue().if_new_iter == true)
				{
					update_direction();		//update direction
				}
				weight.put(entry.getKey(), update_step());  //update model weight
			}
			
			iter_result = cal_grad_loss(weight); //cal model's loss and grad
			
			models_iter = model_context.entrySet().iterator();
			while (models_iter.hasNext()) {
				Entry<Integer, MultiModelContext> entry = models_iter.next();
				MultiModelContext mc = entry.getValue();
				MyPair<Double, SparseVector> grad_loss = iter_result.get(entry.getKey());
				if( finish_searching(mc, grad_loss) )
				{
					update_optimizer(); //update s,y,z
					
				}
				else
				{
					keep_searching();
				}
				update_model_context(mc, grad_loss);
			}			
		}
		
	};
	
}



