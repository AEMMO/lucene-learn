package com.johnny.learn.lucene.indexing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.johnny.learn.lucene.common.TestUtil;

public class LockTest extends TestCase {

	private Directory directory;
	
	private String indexDir;
	
	@Override
	public void setUp() throws IOException{
		indexDir = System.getProperty("java.io.tmpdir", "tmp") + System.getProperty("file.separator") + "index";
		System.out.println("indexDir:" + indexDir);
		Path indexPath = Paths.get(indexDir);
		directory = FSDirectory.open(indexPath);
	}
	
	/**
	 * 
	* @Description: 对于一个索引，一次只能打开一个Writer，一旦建立起IndexWriter对象，
	* 	系统会分配一个锁给它，只有当IndexWriter对象被关闭时才会释放。
	* @return void    返回类型 
	* @throws
	 */
	public void testWriteLock() throws IOException {
		IndexWriterConfig writerConfig = new IndexWriterConfig(new SimpleAnalyzer());
		IndexWriter writer1 = new IndexWriter(directory, writerConfig);
		IndexWriter writer2 = null;
		try {
			writer2 = new IndexWriter(directory, writerConfig);
			fail("We should never reach this point");
		} catch(Exception e){
			//e.printStackTrace();
		} finally {
			writer1.close();
			assertNull(writer2);
			TestUtil.rmDir(new File(indexDir));
		}
	}
	
	public void initIndex() throws IOException{
		IndexWriterConfig writerConfig = new IndexWriterConfig(new SimpleAnalyzer());
		IndexWriter writer = new IndexWriter(directory, writerConfig);
		Document doc = new Document();
		doc.add(new StringField("id", "1", Field.Store.YES));
		writer.addDocument(doc);
		writer.close();
	}
	
	/**
	* @Description: 任意数量的只读属性的IndexReader类都可以同时打开一个索引
	* @return void    返回类型 
	* @throws
	 */
	public void testReadLock() throws IOException{
		initIndex();
		IndexReader reader1 = DirectoryReader.open(directory);
		IndexReader reader2 = null;
		try {
			reader2 = DirectoryReader.open(directory);
			assertNotNull(reader1);
			assertNotNull(reader2);
		} finally {
			reader1.close();
			reader2.close();
			TestUtil.rmDir(new File(indexDir));
		}
	}
}
