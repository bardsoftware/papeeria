package org.ner;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class SearchFiles {
	private static final int STEP = 5_000;

	private SearchFiles() {
	}

	public static void main(String[] args) throws Exception {
		String usage =
				"Usage:\tjava org.ner.SearchFiles [-index dir] [-queries file] [-ru] [-pdf]";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		String index = "index";
		String field = "contents";
		String queries = null;
		boolean ru = false;
		boolean pdf = false;
		int hitsPerPage = 50;


		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				index = args[i + 1];
				i++;
			} else if ("-query".equals(args[i])) {
				queries = args[i + 1];
				i++;
			} else if ("-ru".equals(args[i])) {
				ru = true;
			} else if ("-pdf".equals(args[i])) {
				pdf = true;
			}
		}
		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer =  ru ? new RussianAnalyzer() : new StandardAnalyzer();

			QueryParser parser = new QueryParser(field, analyzer);

			if (pdf) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(queries))) {
					for (Path pathToPDF : directoryStream) {
						Map<String, Float> searchResult = new HashMap<>();
						try (PDDocument document = PDDocument.load(pathToPDF.toString())) {
							PDFTextStripper stripper = new PDFTextStripper();
							String stringQuery = removeNonAlphanumeric(stripper.getText(document));
							System.out.println(pathToPDF.getFileName());
							String[] chunks = stringQuery.split(String.format("(?<=\\G.{%d})", STEP));

							for (String s : chunks) {
								Query query = parser.parse(s);
								Map<String, Float> chunkResult = search(searcher, query, hitsPerPage);
								for (Map.Entry<String, Float> entry : chunkResult.entrySet()) {
									if (searchResult.containsKey(entry.getKey())) {
										searchResult.put(
												entry.getKey(), searchResult.get(entry.getKey()) + entry.getValue());
									} else {
										searchResult.put(entry.getKey(), entry.getValue());
									}
								}
							}
						}
						sortByWeights(searchResult)
								.forEach(entry -> System.out.printf("%s : %f%n", entry.getKey(), entry.getValue()));
					}
				}
			} else {
				String stringQuery = removeNonAlphanumeric(new String(Files.readAllBytes(Paths.get(queries))));
				Query query = parser.parse(stringQuery);
				sortByWeights(search(searcher, query, hitsPerPage))
						.forEach(entry -> System.out.printf("%s : %f%n", entry.getKey(), entry.getValue()));
			}

		}
	}

	private static String removeNonAlphanumeric(String s) {
		return s.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
	}

	public static Map<String, Float> search(IndexSearcher searcher, Query query,
	                          int hitsPerPage) throws IOException {

		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		Map<String, Float> categoryIntoScore = new HashMap<>();
		for (ScoreDoc hit : hits) {
			String category = searcher.doc(hit.doc).get("category");
			if (categoryIntoScore.containsKey(category)) {
				categoryIntoScore.put(category, categoryIntoScore.get(category) + hit.score);
			} else {
				categoryIntoScore.put(category, hit.score);
			}
		}
		return categoryIntoScore;
	}

	public static List<Map.Entry<String, Float>> sortByWeights(Map<String, Float> categoryIntoWeight) {
		List<Map.Entry<String, Float>> scores = new ArrayList<>(categoryIntoWeight.entrySet());
		Collections.sort(scores, (Map.Entry<String, Float> e1, Map.Entry<String, Float> e2) ->
				Float.compare(e2.getValue(), e1.getValue()));
		return scores;
	}
}
