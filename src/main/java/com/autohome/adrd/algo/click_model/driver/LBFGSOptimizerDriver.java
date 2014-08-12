package com.autohome.adrd.algo.click_model.driver;

/*
 * Author : wang chao
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.autohome.adrd.algo.click_model.io.DriverIOHelper;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class LBFGSOptimizerDriver extends Configured implements Tool {

    private static final int DEFAULT_ADMM_ITERATIONS_MAX = 2;
    private static final float DEFAULT_REGULARIZATION_FACTOR = 0.000001f;
    private static final String S3_ITERATION_FOLDER_NAME = "iteration_";
    private static final String S3_FINAL_ITERATION_FOLDER_NAME = S3_ITERATION_FOLDER_NAME + "final";
    private static final String S3_STANDARD_ERROR_FOLDER_NAME = "standard-error";
    private static final String S3_BETAS_FOLDER_NAME = "betas";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new LBFGSOptimizerDriver(), args);
    }

    @Override
    public int run(String[] args) throws IOException {


        int iterationNumber = 0;
        boolean isFinalIteration = false;

        DriverIOHelper driver_io = new DriverIOHelper();
        
        while (!isFinalIteration) {
            long preStatus = 0;
            Job job = new Job(getConf());
            job.setJarByClass(LBFGSOptimizerDriver.class);
            
            Path previousHdfsResultsPath = new Path(S3_ITERATION_FOLDER_NAME + (iterationNumber - 1));
            Path currentHdfsResultsPath = new Path(S3_ITERATION_FOLDER_NAME + iterationNumber);

            driver_io.doLbfgsIteration(job, 
            		inputPath, 
            		outputPath, 
            		mapper_class, 
            		reduce_class, 
            		combine_class, 
            		iterationNumber, 
            		instance_num, 
            		reg);            
            
            isFinalIteration = convergedOrMaxed(curStatus, preStatus, iterationNumber, iterationsMaximum);
            String s3IterationFolderName = getS3IterationFolderName(isFinalIteration, iterationNumber);
            printResultsToS3(job, currentHdfsResultsPath, finalOutputBaseUrl, new AdmmResultWriterIteration(), s3IterationFolderName);

            if (isFinalIteration) {
                printResultsToS3(job, currentHdfsResultsPath, finalOutputBaseUrl, new AdmmResultWriterBetas(),
                        S3_BETAS_FOLDER_NAME);
                Job stdErrJob = new Job(getConf());
                stdErrJob.setJarByClass(LBFGSOptimizerDriver.class);
                Path standardErrorHdfsPath = new Path(intermediateHdfsBaseString + S3_STANDARD_ERROR_FOLDER_NAME);
                doStandardErrorCalculation(
                		stdErrJob,
                        currentHdfsResultsPath,
                        standardErrorHdfsPath,
                        signalDataLocation,
                        iterationNumber,
                        columnsToExclude,
                        addIntercept,
                        regularizeIntercept,
                        regularizationFactor);
                printResultsToS3(stdErrJob, standardErrorHdfsPath, finalOutputBaseUrl, new AdmmResultWriterIteration(),
                        S3_STANDARD_ERROR_FOLDER_NAME);
            }
            iterationNumber++;
        }

        return 0;
    }

    private void parseArgs(String[] args, AdmmOptimizerDriverArguments admmOptimizerDriverArguments) throws CmdLineException {
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));

        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0 && !AdmmOptimizerDriverArguments.VALID_ARGUMENTS.contains(args[i])) {
                argsList.remove(args[i]);
                argsList.remove(args[i + 1]);
            }
        }

        new CmdLineParser(admmOptimizerDriverArguments).parseArgument(argsList.toArray(new String[argsList.size()]));
    }

    private String getS3IterationFolderName(boolean isFinalIteration, int iterationNumber) {
        return (isFinalIteration) ? S3_FINAL_ITERATION_FOLDER_NAME : S3_ITERATION_FOLDER_NAME + iterationNumber;
    }

    public void printResultsToS3(Job conf, Path hdfsDirectoryPath, URI finalOutputBaseUrl,
                                 AdmmResultWriter admmResultWriter, String finalOutputFolderName) throws IOException {
        Path finalOutputPath = new Path(finalOutputBaseUrl.resolve(finalOutputFolderName).toString());
        HdfsToS3ResultsWriter hdfsToS3ResultsWriter = new HdfsToS3ResultsWriter(conf, hdfsDirectoryPath,
                admmResultWriter, finalOutputPath);
        hdfsToS3ResultsWriter.writeToS3();
    }

  

    private boolean convergedOrMaxed(long curStatus, long preStatus, int iterationNumber, int iterationsMaximum) {
        return curStatus <= preStatus || iterationNumber >= iterationsMaximum;
    }
}
