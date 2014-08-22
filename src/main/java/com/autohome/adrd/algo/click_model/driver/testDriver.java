package com.autohome.adrd.algo.click_model.driver;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.common.base.Optional;

public class testDriver extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new testDriver(), args);
    }
	
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		OptimizerDriverArguments OptimizerDriverArguments = new OptimizerDriverArguments();
        parseArgs(arg0, OptimizerDriverArguments);
        
		System.out.println("hahah");
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
