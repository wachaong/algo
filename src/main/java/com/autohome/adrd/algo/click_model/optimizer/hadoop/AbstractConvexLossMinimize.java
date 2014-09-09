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

	protected HashMap<Integer, Boolean> new_iter = new HashMap<Integer, Boolean>();
	protected HashMap<Integer, Boolean> has_converged = new HashMap<Integer, Boolean>();
	protected HashMap<Integer, Integer> status = new HashMap<Integer, Integer>();
	protected Map<Integer, ISearchDirection> search_direction = new HashMap<Integer, ISearchDirection>();  
	protected Map<Integer, AbstractOneStepLineSearch> line_search = new HashMap<Integer, AbstractOneStepLineSearch>();
	protected int iterationsMaximum;
	protected String input_loc, output_loc, init_weight_path, calc_weight_path; 
	protected Configuration conf;
	protected FileSystem fs;
	protected float regularizationFactor;
	protected float sample_freq;
	
	public void SetTrainEnv(Configuration conf, String input_loc, String output_loc, String init_weight_path, String calc_weight_path, int instance_num, float sample_freq, int iterationsMaximum, float regularizationFactor) {
		this.conf = conf;
		this.input_loc = input_loc;
		this.output_loc = output_loc;
		this.init_weight_path = init_weight_path;
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
	
	protected void update_linesearcher(int id,Map<Integer,MyPair<Double, SparseVector>> grad_loss, 
			Map<Integer, SparseVector> weight) {
		line_search.get(id).update(weight.get(id), grad_loss.get(id).getFirst(), grad_loss.get(id).getSecond());
		
	}

	protected void init_status(int id) {
		// TODO Auto-generated method stub
		status.put(id, 1);
		
	}

	protected void update_status(int id) {
		status.put(id, line_search.get(id).getStatus());
	}
	
	protected int get_max_iter() {
		// TODO Auto-generated method stub
		return iterationsMaximum;
	}
	
	protected void update_search_direction(int id, Map<Integer,SparseVector> weight_last, 
			Map<Integer,SparseVector> weight,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss_last,
			Map<Integer,MyPair<Double, SparseVector>> grad_loss) {
		search_direction.get(id).update(weight_last.get(id), weight.get(id),
										grad_loss_last.get(id).getFirst(), grad_loss.get(id).getFirst(), 
										grad_loss_last.get(id).getSecond(), grad_loss.get(id).getSecond()); 

	}
			
	//about weights
	protected Map<Integer, SparseVector> init_weights() {
		// TODO Auto-generated method stub
		/*
		Map<Integer, SparseVector> weight_maps = new HashMap<Integer, SparseVector>();
		weight_maps = CommonFunc.readSparseVectorMap(init_weight_path);
		*/
		
		Map<Integer, SparseVector> weight_maps = IterationHelper.readSparseVectorMapFast(fs, new Path(init_weight_path));
		return weight_maps;
		
	}
	
	protected void save_weights(Map<Integer, SparseVector> weight) {
		IterationHelper.writeSparseVectorMap(fs, new Path(calc_weight_path), weight);
		
	}
	
	//about grad and loss
	protected abstract HashMap<Integer,MyPair<Double, SparseVector>> calc_grad_loss(Map<Integer, SparseVector> weight,
			int iter);
	
	//about optimizer
	protected abstract void init_search_direction(int id);
	protected abstract void init_linesearcher(int id, Map<Integer,MyPair<Double, SparseVector>> grad_loss, Map<Integer, SparseVector> weight_map);
	
	
	//about line search
	protected abstract SparseVector update_step(int id);
	
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
		HashMap<Integer,MyPair<Double, SparseVector>> loss_grad_tmp 
				= new HashMap<Integer,MyPair<Double, SparseVector>>();
		for(Map.Entry<Integer, MyPair<Double, SparseVector>> ent : loss_grad.entrySet()) {
			Integer id = ent.getKey();
			MyPair<Double, SparseVector> pair = ent.getValue();
			double loss = pair.getFirst();
			SparseVector grad = pair.getSecond();
			loss_grad_tmp.put(id, new MyPair<Double, SparseVector>(loss, (SparseVector)grad.clone()));
			
		}
		
		
		for(int id : weight.keySet()) {
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
			for(int id : weight.keySet()) {  //step forward
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
			
		
			for(int id : weight.keySet()) {
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



