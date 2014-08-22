package com.autohome.adrd.algo.click_model.driver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.FloatOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;

public class OptimizerDriverArguments {

    public static final Set<String> VALID_ARGUMENTS = new HashSet<String>(Arrays.asList(
            "-outputPath",
            "-inputPath",
            "-iterationsMaximum",
            "-regularizationFactor",
            "-update",
            "-mutilple",
            "-instance_num",
            "-sample_freq",
            "-initweight_loc"));
    
    @Option(name = "-outputPath", required = true, handler = StringOptionHandler.class)
    private String outputPath;

    @Option(name = "-signalPath", required = true, handler = StringOptionHandler.class)
    private String signalPath;

    @Option(name = "-iterationsMaximum", required = false, handler = IntOptionHandler.class)
    private int iterationsMaximum;

    @Option(name = "-regularizationFactor", required = false, handler = FloatOptionHandler.class)
    private float regularizationFactor;
	
}
