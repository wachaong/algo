package com.autohome.adrd.algo.click_model.io;

/**

 * author : wang chao
 */

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.codehaus.jackson.map.ObjectMapper;











import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.LBFGSReducerContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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

    public static Map<String, String> readHashMap(FileSystem fs, Path WeightOutputPath) 
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
    
    public static SparseVector readSparseVector(FileSystem fs, Path WeightOutputPath) 
    {
    	SparseVector Parameters = new SparseVector();
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
                        	Parameters.setValue(Integer.parseInt(arr[0]), Double.parseDouble(arr[1]));
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
    
    /*
     * format:
     * 2&15 0.01
     * 3&18 0.02
     */
    public static Map<Integer,SparseVector> readSparseVectorMap(FileSystem fs, Path WeightOutputPath) 
    {
    	Map<Integer,SparseVector> Parameters = new HashMap<Integer,SparseVector>();
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
                        	int model_id = Integer.parseInt(arr[0].split("&")[0]);
                        	int id = Integer.parseInt(arr[0].split("&")[1]);
                        	if(! Parameters.containsKey(model_id))
                        	{
                        		SparseVector tmp = new SparseVector();
                        		Parameters.put(model_id, tmp);
                        	}
                        	Parameters.get(model_id).setValue(id, Double.parseDouble(arr[1]));                        	
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
    
    public static void writeSparseVectorMap(FileSystem fs, Path WeightOutputPath, HashMap<Integer,SparseVector> weight) 
    {
    	Map<Integer,SparseVector> Parameters = new HashMap<Integer,SparseVector>();
        try {
            if (fs.exists(WeightOutputPath)) 
            	fs.delete(WeightOutputPath);
            fs.mkdirs(WeightOutputPath);
            Path file_w = new Path(WeightOutputPath.toString()+"/weight");
            FSDataOutputStream out = fs.create(file_w);
            BufferedWriter bis = new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
            
            Iterator<Entry<Integer, SparseVector>> weight_iter = weight.entrySet().iterator();
			while (weight_iter.hasNext()) {
				Entry<Integer, SparseVector> entry = weight_iter.next();
				String model_id = String.valueOf(entry.getKey());
				Iterator<Entry<Integer, Double>> vec_iter = entry.getValue().getData().entrySet().iterator();
				while (vec_iter.hasNext()) {
					Entry<Integer, Double> entry_inner = vec_iter.next();
					String tmp = model_id + "&" + String.valueOf(entry_inner.getKey()) + "\t" + String.valueOf(entry_inner.getValue());
					bis.write(tmp + "\n");
				}
				bis.close();
            }
        } catch (IOException e) {
            LOG.log(Level.FINE, e.toString());
        }
    }
    
}
