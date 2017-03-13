package com.johnny.learn.lucene.searching;

import java.io.IOException;

import org.apache.lucene.store.Directory;

import com.johnny.learn.lucene.common.CommonInit;

import junit.framework.TestCase;

public class QueryTest extends TestCase {
	private Directory directory;
	
	@Override
	public void tearDown() throws IOException{
		if(directory != null){
			directory.close();
			directory = null;
		}
	}
	
	
	public void testTermRangeQuery() throws IOException{
		
		directory = CommonInit.initIndexDirectory();
	}

}
