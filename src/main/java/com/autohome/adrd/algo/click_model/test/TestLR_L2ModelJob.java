package com.autohome.adrd.algo.click_model.test;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;

import com.autohome.adrd.algo.click_model.io.AbstractProcessor;
import com.autohome.adrd.algo.click_model.model.LR_L2_MultiData_ModelMapper;
import com.autohome.adrd.algo.click_model.model.LR_L2_MultiData_ModelReducer;
import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider.Text;

public class TestLR_L2ModelJob extends AbstractProcessor {

	@Override
	protected void configJob(Job job) {
		job.setJarByClass(this.getClass());
		job.setMapperClass(LR_L2_MultiData_ModelMapper.class);
		job.setReducerClass(LR_L2_MultiData_ModelReducer.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
	}

}
