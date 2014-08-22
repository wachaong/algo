package com.autohome.adrd.algo.click_model.feature_process_impl;

import com.autohome.adrd.algo.click_model.data.Sample;
import com.autohome.adrd.algo.click_model.feature_process.Source;
import com.autohome.adrd.algo.click_model.feature_process.Transformer;

import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * generate multiple data sets in one map-reduce job.
 * @author Yang Mingmin
 *
 */

public class SampleGeneratorHelper {
	private Source source = null;
	private ArrayList<ArrayList<Transformer>> trans = new ArrayList<ArrayList<Transformer>>();
	private ArrayList<String> dataset_names = new ArrayList<String>();
	private Document doc = null;
	
	public void setup(String conf_file) {
		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(new File(conf_file));
			
		} catch (DocumentException e) {
			e.printStackTrace();
			System.exit(-1);  //configure file not found.
		}
		
		setupSource();
		setupInteractTransform();
	}
	
	public Sample process(Object raw_data) {
		
		Sample s = source.process(raw_data);
		
		if(s == null) {
			return s;
		}
		
		//interaction and transformation
		ArrayList<Sample> s1 = new ArrayList<Sample>();
		s1.add(s);
		for(ArrayList<Transformer> trans_list : trans) {
			ArrayList<Sample> s2 = new ArrayList<Sample>();
			for(Sample sample_in : s1) {
				for(Transformer trans_tmp : trans_list) {
					Sample stmp = trans_tmp.transform(sample_in);
					s2.add(stmp);
				}
			}
			s1 = s2;
		}
		
		//assemble all the samples
		s = Assembler.assemble(s1);
		return s;
	}
	
	public Source getSource() {
		return source;
	}
	
	public ArrayList<ArrayList<Transformer>> getTransformers() {
		return trans;
	}
	
	public ArrayList<String> getDatasetNames() { 
		return dataset_names;
	}
	
	private void setupSource() {
		Element node = (Element)doc.selectSingleNode("/layers/layer[@type = 'source']/experiments/experiment");
		String source_class = node.elementText("class");
		try {
			source = (Source) Class.forName(source_class).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String name = node.attributeValue("name");
		dataset_names.add(name);
	}  
	   
	@SuppressWarnings("rawtypes")
	private void setupInteractTransform() {
		List list = doc.selectNodes("/layers/layer[@type = 'interaction' or @type = 'transform']"); 
		String class_name = null;
		String name = null;
		String param = null;     
		for(Iterator layer_iter = list.iterator(); layer_iter.hasNext(); ) { //parse every layer
			
			ArrayList<Transformer> trans_tmp = new ArrayList<Transformer>();
			Element experiments = ((Element) layer_iter.next()).element("experiments");
			
			ArrayList<String> names_layer = new ArrayList<String>();
			for(Iterator iiter = experiments.elementIterator(); iiter.hasNext();) {//parse every experiment
				Element exp = (Element) iiter.next();
				
				name = exp.attributeValue("name");
				class_name = exp.elementText("class");
				param = exp.elementText("parameters");
				names_layer.add(name);
				
				Transformer tmp = null;
				try {
					tmp = (Transformer) Class.forName(class_name).newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				tmp.setup(param);				
				trans_tmp.add(tmp);
				
			}
			trans.add(trans_tmp);
			dataset_names = concatLayers(dataset_names, names_layer);
		}
	}
	
	private ArrayList<String> concatLayers(ArrayList<String> names_last, ArrayList<String> names_layer) {
		ArrayList<String> names_after = new ArrayList<String>();
		for(String name0 : names_last) {
			for(String name1 : names_layer) {
				names_after.add(name0 + '.' + name1);
			}
		}
		return names_after;
	}
	
	
	public HashMap<String, ArrayList<String>> getDatasetFeatures(ArrayList<String> features) {
		ArrayList<ArrayList<String>> features_out = new ArrayList<ArrayList<String>>();
		features_out.add(features);
		
		for(ArrayList<Transformer> trans_list : trans) {
			ArrayList<ArrayList<String>> features_tmp = new ArrayList<ArrayList<String>>();
			for(ArrayList<String> feas : features_out) {
				for(Transformer tr : trans_list) {
					features_tmp.add(tr.transformFeatures(feas));
				}
			}
			features_out = features_tmp;
		}
		
		HashMap<String, ArrayList<String>> ans = new HashMap<String, ArrayList<String>>();
		
		Iterator<ArrayList<String>> iter = features_out.iterator();
		for(String dataset_name : dataset_names) {
			ans.put(dataset_name, iter.next());
		}
		return ans;
	}
	
	public void labelize_features(ArrayList<String> features_in, 
			HashMap<String, Integer> feature_id, 
			HashMap<String, ArrayList<Integer>> feature_model) {
		HashMap<String, ArrayList<String>> tmp = getDatasetFeatures(features_in);
		feature_id.clear();
		feature_model.clear();
		int id = 1;
		for(Map.Entry<String, ArrayList<String>> entry : tmp.entrySet()) {
			String model_name = entry.getKey();
			feature_model.put(model_name, new ArrayList<Integer>());
			for(String fea : entry.getValue()) {
				if(!feature_id.containsKey(fea)) {
					feature_id.put(fea, id);
					id++;
				}
				feature_model.get(model_name).add(feature_id.get(fea));
			}
		}
		
	}
}
