package com.autohome.adrd.algo.click_model.driver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FloatOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;

/**

 * author : wangchao
 */

public class OptimizerDriverArguments {

    public static final Set<String> VALID_ARGUMENTS = new HashSet<String>(Arrays.asList(
    		"-init_choice",
            "-outputPath",
            "-inputPath",
            "-iterationsMaximum",
            "-regularizationFactor",
            "-instance_num",
            "-sample_freq",
            "-optimization",
            "-calweight_loc"));
    
    @Option(name = "-optimization", required = true, handler = StringOptionHandler.class)
    private String optimization;
    
    @Option(name = "-init_choice", required = true, handler = StringOptionHandler.class)
    private String init_choice;
    
    @Option(name = "-outputPath", required = true, handler = StringOptionHandler.class)
    private String outputPath;

    @Option(name = "-inputPath", required = true, handler = StringOptionHandler.class)
    private String inputPath;
        
    @Option(name = "-calweight_loc", required = true, handler = StringOptionHandler.class)
    private String calweight_loc;

    @Option(name = "-iterationsMaximum", required = true, handler = IntOptionHandler.class)
    private int iterationsMaximum;

    @Option(name = "-regularizationFactor", required = true, handler = FloatOptionHandler.class)
    private float regularizationFactor;
    
    @Option(name = "-instance_num", required = true, handler = IntOptionHandler.class)
    private int instance_num;
    
    @Option(name = "-sample_freq", required = true, handler = FloatOptionHandler.class)
    private float sample_freq;
    
    public String getOutputPath() {
        return outputPath;
    }

    public String getInputPath() {
        return inputPath;
    }
        
    public String getCalcWeightLoc() {
        return calweight_loc;
    }
    
    public int getIterationsMaximum() {
        return iterationsMaximum;
    }

    public float getRegularizationFactor() {
        return regularizationFactor;
    }

    public int getInstance_num() {
        return instance_num;
    }
 
    public float getSample_freq() {
        return sample_freq;
    }
    
    public String getInit_choice() {
    	return init_choice;
    }
    
    public String getOptimization() {
    	return optimization;
    }
    
}
