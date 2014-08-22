package com.autohome.adrd.algo.click_model.bucket;

import org.apache.hadoop.fs.Path;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class BucketTest {
	
	private Map<Integer, Vector<Integer>> model_featureIds_map = new HashMap<Integer, Vector<Integer>>();
	private String input_features = null;
	private Map<Integer, SparseVector> weights = new HashMap<Integer, SparseVector>();
	//private Model model = null;
	//private ArrayList<Model> models = null;  //Models
	//private ArrayList<Train> opt_methods = null;
	//private Train opt_method = null;
	private int max_iter_num = 100;

	//indicate the model and the opt_methods
	public void setup(String input_dataset_path,  //the location of the dataset
					  String 
			String other) {
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
