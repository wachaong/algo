package com.autohome.adrd.algo.click_model.io;

/**

 * author : wang chao
 */

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.codehaus.jackson.map.ObjectMapper;



import com.autohome.adrd.algo.click_model.data.writable.LBFGSReducerContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class IterationHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern COMPILE = Pattern.compile(",");
    private static final Logger LOG = Logger.getLogger(IterationHelper.class.getName());
    private static final Pattern TAB_PATTERN = Pattern.compile("\t");
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\n");
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    private IterationHelper() {
    }

    public static String lbfgsReducerContextToJson(LBFGSReducerContext context) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(context);
    }

    public static String mapToJson(Map<String, String> vector) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(vector);
    }

    public static String arrayToJson(double[] array) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(array);
    }

    public static Map<String, String> jsonToMap(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, HashMap.class);
    }

    public static LBFGSReducerContext jsonToLbfgsReducerContext(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, LBFGSReducerContext.class);
    }

    public static String fsDataInputStreamToString(FSDataInputStream in, int inputSize) throws IOException {
        byte[] fileContents = new byte[inputSize];
        IOUtils.readFully(in, fileContents, 0, fileContents.length);
        String keyValue = new Text(fileContents).toString();
        return TAB_PATTERN.split(keyValue)[1]; // output from the last reduce job will be key | value
    }

    public static int getFileLength(FileSystem fs, Path thisFilePath) throws IOException {
        return (int) fs.getFileStatus(thisFilePath).getLen();
    }

    public static Map<String, String> readParametersFromHdfs(FileSystem fs, Path previousIntermediateOutputLocationPath,
                                                             int iteration) {
        Map<String, String> splitToParameters = new HashMap<String, String>();
        try {
            splitToParameters = new HashMap<String, String>();
            if (iteration > 0 && fs.exists(previousIntermediateOutputLocationPath)) {
                FileStatus[] fileStatuses = fs.listStatus(previousIntermediateOutputLocationPath);
                for (FileStatus fileStatus : fileStatuses) {
                    Path thisFilePath = fileStatus.getPath();
                    if (!thisFilePath.getName().contains("_SUCCESS") && !thisFilePath.getName().contains("_logs")) {
                        FSDataInputStream in = fs.open(thisFilePath);
                        int inputSize = getFileLength(fs, thisFilePath);
                        if (inputSize > 0) {
                            String value = fsDataInputStreamToString(in, inputSize);
                            Map<String, String> additionalSplitToParameters = jsonToMap(value);
                            splitToParameters.putAll(additionalSplitToParameters);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.FINE, e.toString());
        }
        return splitToParameters;
    }
}
