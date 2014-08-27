package com.autohome.adrd.algo.click_model.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.driver.OptimizerDriverArguments;
import com.autohome.adrd.algo.click_model.io.DriverIOHelper;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.model.SumCombiner;
import com.autohome.adrd.algo.click_model.optimizer.hadoop.ConvexLossMinimize;
import com.google.common.base.Optional;

/**

 * author : wangchao
 */

public class testDriver extends Configured implements Tool {

	private static final float DEFAULT_SAMPLE_FREQ = 1.0f;
	
    private static final int DEFAULT_ADMM_ITERATIONS_MAX = 2;
    private static final float DEFAULT_REGULARIZATION_FACTOR = 0.000001f;
    private static final String S3_ITERATION_FOLDER_NAME = "iteration_";
    private static final String S3_FINAL_ITERATION_FOLDER_NAME = S3_ITERATION_FOLDER_NAME + "final";
    private static final String S3_STANDARD_ERROR_FOLDER_NAME = "standard-error";
    private static final String S3_BETAS_FOLDER_NAME = "betas";
	
    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new testDriver(), args);
    }
	
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		OptimizerDriverArguments OptimizerDriverArguments = new OptimizerDriverArguments();
        parseArgs(arg0, OptimizerDriverArguments);
        
        String input_path = OptimizerDriverArguments.getInputPath();
        String output_path = OptimizerDriverArguments.getOutputPath();
        String calweight_path = OptimizerDriverArguments.getCalcWeightLoc();
        String initweight_loc = OptimizerDriverArguments.getInitWeightLoc();        
        int iterationsMaximum = Optional.fromNullable(OptimizerDriverArguments.getIterationsMaximum()).or(
                DEFAULT_ADMM_ITERATIONS_MAX);
        float regularizationFactor = Optional.fromNullable(OptimizerDriverArguments.getRegularizationFactor()).or(
                DEFAULT_REGULARIZATION_FACTOR);

        float sample_freq = Optional.fromNullable(OptimizerDriverArguments.getSample_freq()).or(
        		DEFAULT_SAMPLE_FREQ);
        int instance_num = OptimizerDriverArguments.getInstance_num();
        
        System.out.println(input_path);
        System.out.println(output_path);
        
        Configuration conf = getConf();
        
        TestIOHelper driver_io = new TestIOHelper();
        
        for(int i=0; i< 3; i++)
        	driver_io.doLbfgsIteration(conf, "/dw/ods/test", "/dw/ods/test2", TestlMapper.class);
        
        System.out.println("haha1");
        
        HashMap<Integer, SparseVector> weight = new HashMap<Integer, SparseVector>();
        SparseVector s1 = new SparseVector();
        SparseVector s2 = new SparseVector();
        s1.setValue(2, 3);
        s1.setValue(3, 5);
        s2.setValue(2, 4);
        s2.setValue(3, 6);
        weight.put(1, s1);
        weight.put(2, s2);
        
        FileSystem fs;
        try {
			fs = FileSystem.get(conf);
			
			IterationHelper.writeSparseVectorMap(fs, new Path("/dw/ods/test3"), weight);
			IterationHelper.readSparseVectorMap(fs, new Path("/dw/ods/test3"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        
		System.out.println("haha2");
		return 0;
	}
	
    private void parseArgs(String[] args, OptimizerDriverArguments admmOptimizerDriverArguments) throws CmdLineException {
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));

        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0 && !OptimizerDriverArguments.VALID_ARGUMENTS.contains(args[i])) {
                argsList.remove(args[i]);
                argsList.remove(args[i + 1]);
            }
        }

        new CmdLineParser(admmOptimizerDriverArguments).parseArgument(argsList.toArray(new String[argsList.size()]));
    }

}
