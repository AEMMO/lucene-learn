package com.johnny.learn.lucene.indexing;

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
 */

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.johnny.learn.lucene.common.TestUtil;

/**
 * 
* @Description: 删除文档索引/更新文档索引
* lucene 6.3.0废弃了Field的域索引选项 Field.Index
*
 */
public class IndexingTest extends TestCase {
	protected String[] ids = { "1", "2" };
	protected String[] unindexed = { "Netherlands", "Italy" };
	protected String[] unstored = { "Amsterdam has lots of bridges",
			"Venice has lots of canals" };
	protected String[] text = { "Amsterdam", "Venice" };
	private Directory directory;

	@Override
	protected void setUp() throws IOException {
		directory = new RAMDirectory();// 内存存储索引
		IndexWriter writer = getWriter();
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("country", unindexed[i], Field.Store.YES));
			doc.add(new StringField("contents", unstored[i], Field.Store.NO));
			doc.add(new StringField("city", text[i], Field.Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
	}

	private IndexWriter getWriter() throws IOException {
		Analyzer analyzer = new WhitespaceAnalyzer();
		IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
		return new IndexWriter(directory, writerConfig);

	}

	protected int getHitCount(String fieldName, String searchString)
			throws IOException {
		// 读索引
		IndexReader reader = DirectoryReader.open(directory);
		
		// 创建索引搜索器
		IndexSearcher searcher = new IndexSearcher(reader);

		Term term = new Term(fieldName, searchString);
		Query query = new TermQuery(term);
		return TestUtil.hitCount(searcher, query);
	}

	public void testIndexWriter() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());
		writer.close();
	}

	/*
	 * #1 Run before every test #2 Create IndexWriter #3 Add documents #4 Create
	 * new searcher #5 Build simple single-term query #6 Get number of hits #7
	 * Verify writer document count #8 Verify reader document count
	 */
	public void testDelete() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(2, writer.numDocs()); //获取文档数
		writer.deleteDocuments(new Term("id", "1")); //根据id来删除文档
		writer.commit();//事务提交
		assertTrue(writer.hasDeletions()); // 1
		assertEquals(2, writer.maxDoc()); // 2
		assertEquals(1, writer.numDocs()); // 2
		writer.close();
	}
	
	/**
	* @Description: 先删除再添加文档索引
	* @param     设定文件 
	* @return void    返回类型 
	* @throws IOException
	 */
	public void testUpdate() throws IOException{
		assertEquals(1, getHitCount("city", "Amsterdam"));
	    IndexWriter writer = getWriter();

	    Document doc = new Document(); 
	    doc.add(new StringField("id", "1", Field.Store.YES));
		doc.add(new StringField("country", "Netherlands", Field.Store.YES));
		doc.add(new StringField("contents", "Den Haag has a lot of museums", Field.Store.NO));
		doc.add(new StringField("city", "Den Haag", Field.Store.YES));       
		
	    writer.updateDocument(new Term("id", "1"), doc);                  
	    writer.close();

	    assertEquals(0, getHitCount("city", "Amsterdam"));
	    assertEquals(1, getHitCount("city", "Den Haag"));
	}

}
