package com.johnny.learn.lucene.common;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class CommonInit {

	protected static String[] ids = { "1", "2", "3", "4", "5" };
	protected static String[] unindexed = { "Netherlands", "Italy", "America", "The United Kingdom", "Germany" };
	protected static String[] unstored = { "Amsterdam has lots of bridges",
			"Venice has lots of canals", "New York has lots of large building", "London has lots of river", "Berlin has lots of bridges"};
	protected static String[] text = { "Amsterdam", "Venice", "New York", "London", "Berlin" };
	private static Directory directory;

	public synchronized static Directory initIndexDirectory() throws IOException {
		directory = new RAMDirectory();// 内存存储索引
		Analyzer analyzer = new WhitespaceAnalyzer();
		IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
		
		IndexWriter writer = new IndexWriter(directory, writerConfig);
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("country", unindexed[i], Field.Store.YES));
			doc.add(new StringField("contents", unstored[i], Field.Store.NO));
			doc.add(new StringField("city", text[i], Field.Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
		return directory;
	}
	
}
