package com.TReSA.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher{
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParser;
	Query query;
	PhraseQuery phraseQuery;
	
	public Searcher(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}

	public TopDocs searchPlaces(String searchQuery) throws IOException, ParseException {
		queryParser = new QueryParser(LuceneConstants.PLACES, new SimpleAnalyzer());
		query = queryParser.parse(searchQuery);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, Sort.RELEVANCE);
	}

	public TopDocs searchPeople(String searchQuery) throws IOException, ParseException {
		queryParser = new QueryParser(LuceneConstants.PEOPLE, new SimpleAnalyzer());
		query = queryParser.parse(searchQuery);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, Sort.RELEVANCE);
	}

	public TopDocs searchTitle(String searchQuery) throws IOException, ParseException {
		queryParser = new QueryParser(LuceneConstants.TITLE, new SimpleAnalyzer());
		query = queryParser.parse(searchQuery);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, Sort.RELEVANCE);
	}

	public TopDocs searchBody(String searchQuery) throws IOException, ParseException {
		// Stemming BODY.
		PorterStemmer porter = new PorterStemmer();
		String[] words = searchQuery.split(" |,|\\.");
		String searchStem = "";
		for (String w: words) {
			if (!(w.equals("AND") || w.equals("OR") || w.equals("NOT"))) {
				searchStem += porter.stemWord(w) + " ";
			} else {
				searchStem += w + " ";
			}
		}
		queryParser = new QueryParser(LuceneConstants.BODY, new StopAnalyzer(getStopSet()));
		query = queryParser.parse(searchStem);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, Sort.RELEVANCE);
	}
	
	private CharArraySet getStopSet() {
		List<String> enDefault = Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at",
											   "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot",
											   "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each",
											   "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd",
											   "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd",
											   "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most",
											   "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours",
											   "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
											   "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them",
											   "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're",
											   "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't",
											   "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's",
											   "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't",
											   "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves");

		CharArraySet stopSet = new CharArraySet(enDefault, false);
		return stopSet;
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}