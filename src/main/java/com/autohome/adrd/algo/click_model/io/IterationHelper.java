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
    public static Map<String,SparseVector> readSparseVectorMap(FileSystem fs, Path WeightOutputPath) 
    {
    	Map<String,SparseVector> Parameters = new HashMap<String, SparseVector>();
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
                        	String id_str = arr[0].split("&")[1];                        	
                        	String model_id = arr[0].split("&")[0];                 
                        	if(! Parameters.containsKey(model_id))
                        	{
                        		SparseVector tmp = new SparseVector();
                        		Parameters.put(model_id, tmp);
                        	}
                        	if(id_str.equals("loss"))
                        	{
                        		Parameters.get(model_id).setValue(-1, Double.valueOf(arr[1])); 
                        	}
                        	else
                        	{
                        		int id = Integer.parseInt(id_str);
                        		Parameters.get(model_id).setValue(id, Double.valueOf(arr[1])); 
                        	}
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
    
    public static void writeSparseVectorMap(FileSystem fs, Path WeightOutputPath, Map<Integer,SparseVector> weight) 
    {
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
    
    public static void writeSparseVectorMapFast(FileSystem fs, Path WeightOutputPath, Map<String,SparseVector> weight) 
    {
        try {
            if (fs.exists(WeightOutputPath)) 
            	fs.delete(WeightOutputPath);
            fs.mkdirs(WeightOutputPath);
            Path file_w = new Path(WeightOutputPath.toString()+"/weight");
            FSDataOutputStream out = fs.create(file_w);
            BufferedWriter bis = new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
            
            Iterator<Entry<String, SparseVector>> weight_iter = weight.entrySet().iterator();
			while (weight_iter.hasNext()) {
				Entry<String, SparseVector> entry = weight_iter.next();
				String model_id = String.valueOf(entry.getKey());
				String vec = entry.getValue().toString();
				bis.write(model_id + "\t" + vec + "\n");							
            }
			bis.close();
        } catch (IOException e) {
        	System.out.println("write failed!");
            LOG.log(Level.FINE, e.toString());
        }
    }
    
    public static Map<String,SparseVector> readSparseVectorMapFast(FileSystem fs, Path WeightOutputPath) 
    {
    	Map<String,SparseVector> Parameters = new HashMap<String,SparseVector>();
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
                        	String[] arr = temp.split("\t", 2);
                        	SparseVector tmp = SparseVector.fromString(arr[1]);                        	
                        	Parameters.put(arr[0], tmp);
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
