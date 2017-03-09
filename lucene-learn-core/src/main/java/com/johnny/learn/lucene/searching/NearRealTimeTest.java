package com.johnny.learn.lucene.searching;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class NearRealTimeTest extends TestCase {

	private static int MAX_SIZE = 10;

	public void testNearRealTime() throws Exception {
		Directory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(
				new StandardAnalyzer()));
		for (int i = 0; i < MAX_SIZE; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", "" + i, Field.Store.YES));
			doc.add(new StringField("text", "same text", Field.Store.YES));
			writer.addDocument(doc);
		}

		// 打开一个近实时的IndexReader
		IndexReader reader = DirectoryReader.open(writer);
		IndexSearcher searcher = new IndexSearcher(reader);

		Query query = new TermQuery(new Term("text", "same text"));
		TopDocs docs = searcher.search(query, 1);
		assertEquals(10, docs.totalHits);// 返回查询结果数

		// 先删除一个文档
		writer.deleteDocuments(new Term("id", "7"));

		// 新增一个文档
		Document doc = new Document();
		doc.add(new StringField("id", MAX_SIZE + "1", Field.Store.YES));
		doc.add(new StringField("text", "other text", Field.Store.YES));
		writer.addDocument(doc);

		//重新打开reader：将缓存中的所有变更刷新到索引目录中，然后创建一个新的包含这些变更的IndexReader
		IndexReader newReader = DirectoryReader
				.openIfChanged((DirectoryReader) reader);
		reader.close();
		if (newReader == reader) {
			assertFalse(newReader==reader);
			System.out.println("IndexReader打开的是同一个");
		} else {
			searcher = new IndexSearcher(newReader);
			TopDocs hits = searcher.search(query, 10);
			assertEquals(9, hits.totalHits);//查询text:"same text"

			query = new TermQuery(new Term("text", "other text"));
			hits = searcher.search(query, 1);//查询text:"other text"
			assertEquals(1, hits.totalHits);

			newReader.close();
			writer.close();
		}

	}

}
