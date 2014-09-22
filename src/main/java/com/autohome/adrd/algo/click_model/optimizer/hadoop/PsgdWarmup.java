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
//import com.autohome.adrd.algo.click_model.optimizer.MultiModelContext;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;
import com.autohome.adrd.algo.click_model.utility.MyPair;
import com.autohome.adrd.algo.click_model.model.AvgDoubleReducer;
import com.autohome.adrd.algo.click_model.model.CalLossMapper;
import com.autohome.adrd.algo.click_model.model.PSGD_MultiData_ModelMapper;
import com.autohome.adrd.algo.click_model.model.PSGD_MultiData_ModelReducer;
import com.autohome.adrd.algo.click_model.model.SumDoubleReducer;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.AbstractOneStepLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.abstract_def.ISearchDirection;
import com.autohome.adrd.algo.click_model.optimizer.common.LbfgsSearchDirection;
import com.autohome.adrd.algo.click_model.optimizer.common.OneStepBacktrackingLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.common.OneStepWolfeLineSearch;

/**
 * author : wang chao
 */

public class PsgdWarmup {

	private String input_loc, output_loc, calc_weight_path; 
	Configuration conf;
	FileSystem fs;
	private float sample_freq;
	private DriverIOHelper driver_io = new DriverIOHelper();
	
	public void SetTrainEnv(Configuration conf, 
			String input_loc, String output_loc, String calc_weight_path, float sample_freq)
	{
		this.conf = conf;
		this.input_loc = input_loc;
		this.output_loc = output_loc;
		this.calc_weight_path = calc_weight_path;
		this.sample_freq = sample_freq;
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void minimize() {
		// TODO Auto-generated method stub				
		try {
			
			//cal weight, result format : 
			//id	model_vec.tostrint
						
			driver_io.doPSGD(conf, input_loc, output_loc, calc_weight_path, 
					PSGD_MultiData_ModelMapper.class, PSGD_MultiData_ModelReducer.class, sample_freq);
			
			Map<String, SparseVector> weight_maps = IterationHelper.readSparseVectorMapFast(fs, new Path(output_loc));
			IterationHelper.writeSparseVectorMapFast(fs, new Path(calc_weight_path), weight_maps);
			
			//cal loss
			/*
			driver_io.doPSGD(conf, input_loc, result_path, output_loc, 
					CalLossMapper.class, AvgDoubleReducer.class, sample_freq);
					*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
