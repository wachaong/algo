package com.autohome.adrd.algo.click_model.bucket;

import org.apache.hadoop.fs.Path;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.model.*;
import com.autohome.adrd.algo.click_model.optimizer.HadoopOptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BucketTest {
	
	//private ArrayList<SparseVector> weights = null; 
	
	private Map<Integer, SparseVector> weights = new HashMap<Integer, SparseVector>();
	private Model model = null;
	//private ArrayList<Model> models = null;  //Models
	//private ArrayList<Train> opt_methods = null;
	//private Train opt_method = null;
	private int max_iter_num = 100;
	private HadoopOptimizer opt = null;

	//indicate the model and the opt_methods
	public void setup() {
	}
	
	public void run() {	
	}
	
	//initial the weight of all the models
	public void initWeights() {
		//
	}
	
	
	
	public void train(Path inputData) {
		//for(Train t : opt_methods) {
			//for(models :
			
		//}
		
		initWeights();
		
		
		//opt.setup(configure_file);	
		//opt.minimize(conf, mapper_class, reducer_class, combiner_class, dataset_path, weight_in_path);
	}
	
	
	

}
