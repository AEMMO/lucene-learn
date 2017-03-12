package com.johnny.learn.lucene.searching;

import java.io.IOException;

import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
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

	/**
	 * 指定Term范围查询
	 * @throws IOException
	 */
	public void testTermRangeQuery() throws IOException{
		directory = CommonInit.initIndexDirectory();
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		//在指定的term范围内：查询city名称在C-Z之间的范围的索引（包含首尾）
		TopDocs topDocs = searcher.search(new TermRangeQuery("city", new BytesRef("C"), new BytesRef("Z"), true, true), 100);
		assertEquals(3, topDocs.totalHits);
		reader.close();
	}
	
	/**
	 * 数字类型范围查询：IntPoint, LongPoint, FloatPoint, DoublePoint, and create range queries with IntPoint.newRangeQuery().  
	 * @throws IOException
	 */
	public void testPointRangeQuery() throws IOException {
		directory = CommonInit.initIndexDirectory();
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		//Long类型的数字范围查询
		TopDocs topDocs = searcher.search(LongPoint.newRangeQuery("population", 100, 1000), 100);
		assertEquals(3, topDocs.totalHits);
		reader.close();
	}
	
	/**
	 * 前缀查询
	 * @throws IOException
	 */
	public void testPrefixQuery() throws IOException {
		directory = CommonInit.initIndexDirectory();
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Term term = new Term("contents", "Amsterdam");
		
		PrefixQuery prefixQuery = new PrefixQuery(term);//前缀
		TopDocs topDocs = searcher.search(prefixQuery, 100);

		Query query = new TermQuery(term);//全词
		TopDocs topDocs1 = searcher.search(query, 100);
		
		assertEquals(1, topDocs.totalHits);
		assertEquals(0, topDocs1.totalHits);
		reader.close();
	}
	
	/**
	 * 组合查询
	 * @throws IOException
	 */
	public void testBooleanQuery() throws IOException{
		directory = CommonInit.initIndexDirectory();
		
		Query prefixQuery = new PrefixQuery(new Term("contents", "Amsterdam"));//前缀
		Query pointQuery = LongPoint.newRangeQuery("population", 100, 1000);//人口数100-1000万
		
		BooleanClause booleanClause1 = new BooleanClause(prefixQuery, Occur.MUST);
		BooleanClause booleanClause2 = new BooleanClause(pointQuery, Occur.MUST);
		
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(booleanClause1);
		builder.add(booleanClause2);
		
		BooleanQuery booleanQuery = builder.build();

		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs topDocs = searcher.search(booleanQuery, 100);
		assertEquals(1, topDocs.totalHits);
		reader.close();
	}
	
	

}
