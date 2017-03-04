package com.johnny.learn.lucene.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @Description: TODO(这里用一句话描述这个类的作用)
 *
 * @author johnny  
 * @date 2017年3月4日 下午1:58:16
 *
 */
public class Indexer {

	private IndexWriter indexWriter;
	
	public Indexer(String indexDir) throws IOException{
		//设置索引文档存储路径
		Path indexPath = Paths.get(indexDir);
		Directory directory = FSDirectory.open(indexPath);
		
		//设置分词器默认
		Analyzer analyzer = new StandardAnalyzer();
		
		//设置索引写入器配置
		IndexWriterConfig iWriterConfig = new IndexWriterConfig(analyzer);
		iWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		
		//创建indexWriter
		indexWriter = new IndexWriter(directory, iWriterConfig);
	}
	
	public void indexTxt(String docDir) throws IOException{
		
		InputStream stream = null;
		try {
			long startTime = System.currentTimeMillis();
			
			Path docPath = Paths.get(docDir);
			long lastModified = Files.getLastModifiedTime(docPath, new LinkOption[0]).toMillis();
			stream = Files.newInputStream(docPath, new OpenOption[0]);
			
			Document document = new Document();
			Field pathField = new StringField("path", docPath.toString(), Field.Store.YES);
			document.add(pathField);
			document.add(new LongPoint("modified", lastModified));
			document.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			
			indexWriter.addDocument(document);
			System.out.println("添加索引完成.共花费"+ (System.currentTimeMillis() - startTime)+"s");
		} finally {
			if(stream != null){
				stream.close();
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		try {
			Indexer indexer = new Indexer("D:\\myWorkspace\\lucene\\index");
			indexer.indexTxt("D:\\myWorkspace\\lucene\\test.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
