package com.autohome.adrd.algo.click_model.driver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.FloatOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;

/**

 * author : wangchao
 */

public class OptimizerDriverArguments {

    public static final Set<String> VALID_ARGUMENTS = new HashSet<String>(Arrays.asList(
            "-outputPath",
            "-inputPath",
            "-iterationsMaximum",
            "-regularizationFactor",
            "-mutilple",
            "-instance_num",
            "-sample_freq",
            "-initweight_loc",
            "-calweight_loc"));
    
    @Option(name = "-outputPath", required = true, handler = StringOptionHandler.class)
    private String outputPath;

    @Option(name = "-inputPath", required = true, handler = StringOptionHandler.class)
    private String inputPath;
    
    @Option(name = "-initweight_loc", required = true, handler = StringOptionHandler.class)
    private String initweight_loc;
    
    @Option(name = "-initweight_loc", required = true, handler = StringOptionHandler.class)
    private String calweight_loc;

    @Option(name = "-iterationsMaximum", required = false, handler = IntOptionHandler.class)
    private int iterationsMaximum;

    @Option(name = "-regularizationFactor", required = false, handler = FloatOptionHandler.class)
    private float regularizationFactor;
    
    @Option(name = "-mutilple", required = false, handler = BooleanOptionHandler.class)
    private boolean mutilple;
    
    @Option(name = "-instance_num", required = true, handler = IntOptionHandler.class)
    private int instance_num;
    
    @Option(name = "-sample_freq", required = false, handler = FloatOptionHandler.class)
    private float sample_freq;
    
    public String getOutputPath() {
        return outputPath;
    }

    public String getInputPath() {
        return inputPath;
    }
    
    public String getInitWeightLoc() {
        return initweight_loc;
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

    public boolean getMutilple() {
        return mutilple;
    }

    public int getInstance_num() {
        return instance_num;
    }
 
    public float getSample_freq() {
        return sample_freq;
    }
    
}
