package com.autohome.adrd.algo.click_model.data.writable;

/**

 * author : wang chao
 */
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Writable;


public class InstancesWritable implements Writable{
	private List<SingleInstanceWritable> file_instances=null;
	
	public InstancesWritable(){
		file_instances=new ArrayList<SingleInstanceWritable>();
	}
	
	public void addIns(SingleInstanceWritable sins){
		this.file_instances.add(sins);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(file_instances.size());
		for(SingleInstanceWritable si: this.file_instances){
			si.write(out);
		}
	}

	public List<SingleInstanceWritable> getFile_instances() {
		return file_instances;
	}

	public void setFile_instances(List<SingleInstanceWritable> file_instances) {
		this.file_instances = file_instances;
	}

	@Override
	public void readFields(DataInput in) throws IOException {

		file_instances=new ArrayList<SingleInstanceWritable>();
		
		int instance_num=in.readInt();
		for(int i=0; i< instance_num; i++){
			SingleInstanceWritable si=new SingleInstanceWritable();
			si.readFields(in);
			this.file_instances.add(si);
		}
	}
	
	public static InstancesWritable read(DataInput in) throws IOException {
		InstancesWritable obj = new InstancesWritable();
		obj.readFields(in);
		return obj;
	}

}
