package com.TReSA.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	
	private IndexWriter writer;
	public Directory indexDirectory;
	
	public Indexer(String indexDirectoryPath) throws IOException {
		// This directory will contain the indexes
		Path indexPath = Paths.get(indexDirectoryPath);
		if(!Files.exists(indexPath)) {
			Files.createDirectory(indexPath);
		}
		// Path indexPath = Files.createTempDirectory(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		
		// Create the indexer
		Analyzer analyzer = new StopAnalyzer(getStopSet());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// OpenMode.CREATE -> Opens index folder with no other files inside.
		config.setOpenMode(OpenMode.CREATE);
		writer = new IndexWriter(indexDirectory, config);
		writer.deleteAll();
		writer.commit();
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
	
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
	
	private Document getDocument(File file) throws IOException {
		Document document = new Document();
		Boolean formatError = false;
		// Index file contents
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		// First line is for the places.
		String linePlaces = br.readLine().toString();
		if (!linePlaces.contains("</PLACES>")) {
			linePlaces += br.readLine().toString();
			// Checking for the format of the file.
			if (!linePlaces.contains("<PLACES>") || !linePlaces.contains("</PLACES>")) {
				formatError = true;
			}
			linePlaces = linePlaces.replace("<PLACES>", "");
			linePlaces = linePlaces.replace("</PLACES>", "");
		} else {
			linePlaces = linePlaces.replace("<PLACES>", "");
			linePlaces = linePlaces.replace("</PLACES>", "");
		}
		// Index places.
		Field fieldPlaces = new Field(LuceneConstants.PLACES, linePlaces, TextField.TYPE_STORED);
		
		// Second line is for the people.
		String linePeople = br.readLine().toString();
		if (!linePeople.contains("</PEOPLE>")) {
			linePeople += br.readLine().toString();
			// Checking for the format of the file.
			if (!linePeople.contains("<PEOPLE>") || !linePeople.contains("</PEOPLE>")) {
				formatError = true;
			}
			linePeople = linePeople.replace("<PEOPLE>", "");
			linePeople = linePeople.replace("</PEOPLE>", "");
		} else {
			linePeople = linePeople.replace("<PEOPLE>", "");
			linePeople = linePeople.replace("</PEOPLE>", "");
		}
		// Index people.
		Field fieldPeople = new Field(LuceneConstants.PEOPLE, linePeople, TextField.TYPE_STORED);
		
		// Third line is for the title.
		String lineTitle = br.readLine().toString();
		if (!lineTitle.contains("</TITLE>")) {
			lineTitle += br.readLine().toString();
			// Checking for the format of the file.
			if (!lineTitle.contains("<TITLE>") || !lineTitle.contains("</TITLE>")) {
				formatError = true;
			}
			lineTitle = lineTitle.replace("<TITLE>", "");
			lineTitle = lineTitle.replace("</TITLE>", "");
		} else {
			lineTitle = lineTitle.replace("<TITLE>", "");
			lineTitle = lineTitle.replace("</TITLE>", "");
		}
		// Index title.
		Field fieldTitle = new Field(LuceneConstants.TITLE, lineTitle, TextField.TYPE_STORED);
		
		// Fourth and beyond is for body.
		String tmp, lineBody = "";
		while ((tmp = br.readLine()) != null) {
			if (tmp.contains("<BODY>")) {
				// Checking for the format of the file.
				if (!tmp.contains("<BODY>")) {
					formatError = true;
				}
				tmp = tmp.replace("<BODY>", "");
			}
			if (tmp.contains("</BODY>")) {
				// Checking for the format of the file.
				if (!tmp.contains("</BODY>")) {
					formatError = true;
				}
				tmp = tmp.replace("</BODY>", "");
			}
			lineBody += tmp + "\n";
		}
		// Stemming BODY.
		PorterStemmer porter = new PorterStemmer();
		String[] words = lineBody.split(" |,|\\.");
		String wordsStem = "";
		for (String w: words) {
			wordsStem += porter.stemWord(w) + " ";
		}
		// Index body.
		Field fieldBody = new Field(LuceneConstants.BODY, wordsStem, TextField.TYPE_STORED);

		// Index content.
		String content = linePlaces + "\n" + linePeople + "\n" + lineTitle + "\n" + lineBody;
		Field fieldContent = new Field(LuceneConstants.CONTENT, content, TextField.TYPE_STORED);
		
		// Index file name.
		Field fileName = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
		// Index file path.
		Field filePath = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), StringField.TYPE_STORED);

		document.add(fieldPlaces);
		document.add(fieldPeople);
		document.add(fieldTitle);
		document.add(fieldBody);
		document.add(fieldContent);
		document.add(fileName);
		document.add(filePath);
		br.close();
		
		// Checking if there was any problem with the format of the file.
		if (!formatError) {
			return document;
		} else {
			return null;
		}
	}
	
	private void indexFile(File file) throws IOException {
		System.out.println("Indexing "+ file.getCanonicalPath());
		Document document = getDocument(file);
		if (!(document == null)) {
			writer.addDocument(document);
		}
	}
	
	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		// Get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)){
				indexFile(file);
			}
		}
		return writer.numRamDocs();
	}
}