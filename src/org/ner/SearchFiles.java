package org.ner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


public class SearchFiles {

	private SearchFiles() {
	}

	public static void main(String[] args) throws Exception {
		String usage =
				"Usage:\tjava org.ner.SearchFiles [-index dir] [-queries file]";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		String index = "index";
		String field = "contents";
		String queries = null;
		int hitsPerPage = 50;

		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				index = args[i + 1];
				i++;
			} else if ("-queries".equals(args[i])) {
				queries = args[i + 1];
				i++;
			}
		}

		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();

			QueryParser parser = new QueryParser(field, analyzer);
			String line = new String(Files.readAllBytes(Paths.get(queries)));
			line = line.replaceAll("[^A-Za-z0-9 ]", "").trim();
			Query query = parser.parse(line);
			search(searcher, query, hitsPerPage);
		}
	}

	public static void search(IndexSearcher searcher, Query query,
	                          int hitsPerPage) throws IOException {

		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");
		Map<String, Float> categoryIntoScore = new HashMap<>();
		for (ScoreDoc hit : hits) {
			String category = searcher.doc(hit.doc).get("category");
			if (categoryIntoScore.containsKey(category)) {
				categoryIntoScore.put(category, categoryIntoScore.get(category) + hit.score);
			} else {
				categoryIntoScore.put(category, hit.score);
			}
		}
		List<Map.Entry<String, Float>> scores = new ArrayList<>(categoryIntoScore.entrySet());
		Collections.sort(scores, (Map.Entry<String, Float> e1, Map.Entry<String, Float> e2) ->
				Float.compare(e2.getValue(), e1.getValue()));
		scores.forEach(s -> System.out.printf("%s : %f%n", s.getKey(), s.getValue()));
	}
}
