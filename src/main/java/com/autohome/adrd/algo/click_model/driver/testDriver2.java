package com.autohome.adrd.algo.click_model.driver;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.autohome.adrd.algo.click_model.io.DriverIOHelper;
import com.autohome.adrd.algo.click_model.model.LR_L2_MultiData_ModelMapper;
import com.autohome.adrd.algo.click_model.model.LR_L2_MultiData_ModelReducer;
import com.autohome.adrd.algo.click_model.model.TestMapper;
import com.autohome.adrd.algo.click_model.model.TestReducer;
import com.autohome.adrd.algo.click_model.model.SumCombiner;
import com.autohome.adrd.algo.click_model.optimizer.hadoop.ConvexLossMinimize;

/**
 * 
 * author : wangchao
 */

public class testDriver2 extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new Configuration(), new testDriver2(), args);
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
		int iterationsMaximum = OptimizerDriverArguments.getIterationsMaximum();
		float regularizationFactor = OptimizerDriverArguments.getRegularizationFactor();
		float sample_freq = OptimizerDriverArguments.getSample_freq();
		int instance_num = OptimizerDriverArguments.getInstance_num();

		Configuration conf = getConf();

		DriverIOHelper driver_io = new DriverIOHelper();
		
		driver_io.doLbfgsIteration(conf, input_path, output_path, calweight_path, 
				TestMapper.class, TestReducer.class, SumCombiner.class, 1, instance_num, regularizationFactor , sample_freq);
		

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
