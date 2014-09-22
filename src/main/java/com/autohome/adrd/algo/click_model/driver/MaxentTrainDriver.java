package com.autohome.adrd.algo.click_model.driver;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.autohome.adrd.algo.click_model.model.LR_L2_MultiData_ModelMapper;
import com.autohome.adrd.algo.click_model.model.LR_L2_MultiData_ModelReducer;
import com.autohome.adrd.algo.click_model.model.SumDoubleReducer;
import com.autohome.adrd.algo.click_model.optimizer.hadoop.AbstractConvexLossMinimize;
import com.autohome.adrd.algo.click_model.optimizer.hadoop.LbfgsConvexLossMinimize;
import com.autohome.adrd.algo.click_model.optimizer.hadoop.OwlqnConvexLossMinimize;
import com.autohome.adrd.algo.click_model.optimizer.hadoop.PsgdWarmup;

/**
 * 
 * author : wangchao
 */

public class MaxentTrainDriver extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new Configuration(), new MaxentTrainDriver(), args);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		OptimizerDriverArguments OptimizerDriverArguments = new OptimizerDriverArguments();
		parseArgs(arg0, OptimizerDriverArguments);

		String optimization = OptimizerDriverArguments.getOptimization();
		String input_path = OptimizerDriverArguments.getInputPath();
		String output_path = OptimizerDriverArguments.getOutputPath();
		String calweight_path = OptimizerDriverArguments.getCalcWeightLoc();
		int iterationsMaximum = OptimizerDriverArguments.getIterationsMaximum();
		float regularizationFactor = OptimizerDriverArguments.getRegularizationFactor();
		float sample_freq = OptimizerDriverArguments.getSample_freq();
		int instance_num = OptimizerDriverArguments.getInstance_num();

		//instance_num and calweight_path's dic should be prepared
		
		Configuration conf = getConf();		
		String update_choice = OptimizerDriverArguments.getInit_choice();		
		if(update_choice.equals("psgd"))
		{	
			PsgdWarmup pw = new PsgdWarmup();
			pw.SetTrainEnv(conf, input_path, output_path, calweight_path, sample_freq);
			pw.minimize();
		}
		else if( ! update_choice.equals("update"))
		{
			System.out.println("only support psgd and update now");
			return -1;
		}
		
		AbstractConvexLossMinimize mdm = null;
		
		if(optimization.equals("lbfgs"))
		{
			mdm = new LbfgsConvexLossMinimize();
			mdm.SetJobEnv(LR_L2_MultiData_ModelMapper.class, LR_L2_MultiData_ModelReducer.class, 
					SumDoubleReducer.class, instance_num);
		}
		else if(optimization.equals("owlqn"))
		{
			mdm = new OwlqnConvexLossMinimize();
			mdm.SetJobEnv(LR_L2_MultiData_ModelMapper.class, LR_L2_MultiData_ModelReducer.class, 
					SumDoubleReducer.class, instance_num);
		}
		
		mdm.SetTrainEnv(conf, input_path, output_path, calweight_path, 
				instance_num, sample_freq, iterationsMaximum, regularizationFactor);

		mdm.minimize();
		
		return 0;
	}

	private void parseArgs(String[] args, OptimizerDriverArguments admmOptimizerDriverArguments) throws CmdLineException {
		ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));

		for (int i = 0; i < args.length; i++) {
			if (i % 2 == 0 && !OptimizerDriverArguments.VALID_ARGUMENTS.contains(args[i])) {
				System.out.println("not valid" + args[i]);
				argsList.remove(args[i]);
				argsList.remove(args[i + 1]);
			}
		}

		new CmdLineParser(admmOptimizerDriverArguments).parseArgument(argsList.toArray(new String[argsList.size()]));
	}

}
