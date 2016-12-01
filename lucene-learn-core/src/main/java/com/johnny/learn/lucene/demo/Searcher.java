package com.johnny.learn.lucene.demo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @Description: 搜索索引
 *
 * @author johnny  
 * @date 2017年3月6日 上午10:58:33
 *
 */
public class Searcher {
	
	/** 索引搜索器 */
	private IndexSearcher indexSearcher;
	
	public Searcher(String indexDir) throws IOException{
		Path indexPath = Paths.get(indexDir);
		
		Directory dir = FSDirectory.open(indexPath);
		IndexReader reader = DirectoryReader.open(dir);
		indexSearcher = new IndexSearcher(reader);
	}
	
	public void searchTxt(String queryStr) throws IOException{
		Term term = new Term("contents", queryStr.toLowerCase());
		TermQuery luceneQuery = new TermQuery(term);
		TopDocs results = indexSearcher.search(luceneQuery, 10);
		if(results.scoreDocs != null && results.scoreDocs.length > 0){
			System.out.println("Query Word:["+queryStr+"]:");
			for(ScoreDoc scoreDoc : results.scoreDocs){
				Document doc = indexSearcher.doc(scoreDoc.doc);
				System.out.println(doc.get("path"));
			}
		} else {
			System.out.println("Query Word:["+queryStr+"]: It's empty.");
		}
	}
	
	public static void main(String[] args) {
		try {
			Searcher searcher = new Searcher("D:\\myWorkspace\\lucene\\index");
			searcher.searchTxt("hello");
			searcher.searchTxt("wall");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
