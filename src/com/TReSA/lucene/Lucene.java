package com.TReSA.lucene;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Lucene{
	
	String indexDir = "C:\\Users\\stefa\\eclipse-workspace\\TReSA\\index";
	String dataDir = "C:\\Users\\stefa\\eclipse-workspace\\TReSA\\data";
	Indexer indexer;
	Searcher searcher;
	
	private int time = 0, results = 0;
	
	private ArrayList<Document> docs = new ArrayList<>();
	
	public void createIndex() throws IOException {
		indexer = new Indexer(indexDir);
		int numIndexed;
		
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		results += numIndexed;
		time += (endTime - startTime);
		
		indexer.close();
	}
	
	public void searchPlaces(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		try {
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.searchPlaces(searchQuery);
			long endTime = System.currentTimeMillis();
			results += hits.scoreDocs.length;
			time += (endTime - startTime);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.getDocument(scoreDoc);
				docs.add(doc);
				System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
			}
		} catch(ParseException e) {
			results = 0;
			time = 0;
		}
	}
	
	public void searchPeople(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		try {
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.searchPeople(searchQuery);
			long endTime = System.currentTimeMillis();
			results += hits.scoreDocs.length;
			time += (endTime - startTime);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.getDocument(scoreDoc);
				docs.add(doc);
				System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
			}
		} catch(ParseException e) {
			results = 0;
			time = 0;
		}
	}
	
	public void searchTitle(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		try {
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.searchTitle(searchQuery);
			long endTime = System.currentTimeMillis();
			results += hits.scoreDocs.length;
			time += (endTime - startTime);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.getDocument(scoreDoc);
				docs.add(doc);
				System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
			}
		} catch(ParseException e) {
			results = 0;
			time = 0;
		}
	}
	
	public void searchBody(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		try {
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.searchBody(searchQuery);
			long endTime = System.currentTimeMillis();
			results += hits.scoreDocs.length;
			time += (endTime - startTime);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.getDocument(scoreDoc);
				docs.add(doc);
				System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
			}
		} catch(ParseException e) {
			results = 0;
			time = 0;
		}
	}
	
	public void clearValues() {
		time = 0;
		results = 0;
		docs.clear();
	}
	
	public int getResults() {
		return results;
	}
	
	public int getTime() {
		return time;
	}
	
	public ArrayList<Document> getDocs(){
		return docs;
	}
}
