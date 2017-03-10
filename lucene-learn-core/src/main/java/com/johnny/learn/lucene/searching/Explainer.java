package com.johnny.learn.lucene.searching;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.johnny.learn.lucene.common.CommonInit;

/**
 * 
* @Description: Explanation 包含了所有关于评分计算中各因子的信息
*
* @author johnny  
* @date 2017年3月10日 下午4:26:28
*
 */
public class Explainer extends TestCase{


	private Directory directory;
	
	@Override
	public void tearDown() throws IOException{
		if(directory != null){
			directory.close();
			directory = null;
		}
	}
	
	public void testExplainer() throws IOException{
		directory = CommonInit.initIndexDirectory();
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Query query = new TermQuery(new Term("id", "1"));
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Explanation explanation = searcher.explain(query, scoreDoc.doc);//scoreDoc.doc 击中的文档编号
			Document doc = searcher.doc(scoreDoc.doc);
			
			System.out.println(doc.get("id"));
			System.out.println(explanation.toString());//获取评分因子的细节信息
		}
		
		reader.close();
	}
	
}
