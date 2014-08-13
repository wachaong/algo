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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static Map<String, String> readParametersFromHdfs(FileSystem fs, Path WeightOutputPath) 
    {
        Map<String, String> Parameters = new HashMap<String, String>();
        try {
            if (fs.exists(WeightOutputPath)) {
                FileStatus[] fileStatuses = fs.listStatus(WeightOutputPath);
                for (FileStatus fileStatus : fileStatuses) {
                    Path thisFilePath = fileStatus.getPath();
                    if (!thisFilePath.getName().contains("_SUCCESS") && !thisFilePath.getName().contains("_logs")) {
                        FSDataInputStream in = fs.open(thisFilePath);                        
                        BufferedReader bis = new BufferedReader(new InputStreamReader(in,"utf-8"));     
                        String temp;  
                        while ((temp = bis.readLine()) != null) {  
                        	String[] arr = temp.split("\t", -1);
                        	Parameters.put(arr[0], arr[1]);
                        }         
                        bis.close();
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.FINE, e.toString());
        }
        return Parameters;
    }
}
