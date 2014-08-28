package com.autohome.adrd.algo.click_model.test;

import java.io.*;
import java.util.HashMap;

public class TestSparseVectorMapWriter {
	
	public static void main(String args[]) throws IOException {	
	HashMap<Integer, Double> v1 = new HashMap<Integer, Double>();
	
	int N = (int) 1e8;
	System.out.println("init v1");
	long pre= System.currentTimeMillis();
	for(int i = 0; i < N; ++i)
		v1.put(i, 0.0);
	 long post= System.currentTimeMillis();
	 System.out.println(post-pre);
	
	String dst1 = "E:\\data\\1.txt";
	String dst2 = "E:\\data\\2.txt";
	//InputStream in = new BufferedInputStream(new FileInputStream(localSrc));


	
    BufferedWriter bis = new BufferedWriter(new FileWriter(dst1));
    
    System.out.println("write 1");
    pre= System.currentTimeMillis();
    for(int i = 0; i < N; ++i) {
    	bis.write(String.valueOf(i) +"\t" +  String.valueOf(v1.get(i)));
    }
    post= System.currentTimeMillis();
    System.out.println("write 1 end");
    System.out.println(post-pre);
    
	for(int i = 0; i < N; ++i)
		v1.put(i, Math.random() * 1000);
	//out = fs.create(new Path(dst2));
	bis = new BufferedWriter(new FileWriter(dst2));

    System.out.println("write 2");
    pre= System.currentTimeMillis();
    for(int i = 0; i < N; ++i) {
    	bis.write(String.valueOf(i) +"\t" +  String.valueOf(v1.get(i)));
    }
    post= System.currentTimeMillis();
    System.out.println(post-pre);
    System.out.println("write 2 end");
}
}
