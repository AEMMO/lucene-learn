package com.johnny.learn.lucene.searching;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

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
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		//在指定的term范围内：查询city名称在C-Z之间的范围的索引
		TopDocs topDocs = searcher.search(new TermRangeQuery("city", new BytesRef("C"), new BytesRef("Z"), true, true), 100);
		assertEquals(3, topDocs.totalHits);
		reader.close();
	}

}
