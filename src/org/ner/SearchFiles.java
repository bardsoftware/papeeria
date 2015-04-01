package org.ner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.ml.clustering.*;
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
	private static final int NUMBER_OF_CLUSTERS = 3;

	private static class CategoryWeightPair implements Clusterable, Comparable<CategoryWeightPair> {
		private final String category;
		private final Float weight;

		public CategoryWeightPair(Map.Entry<String, Float> pair) {
			this.category = pair.getKey();
			this.weight = pair.getValue();
		}

		@Override
		public double[] getPoint() {
			return new double[]{weight};
		}

		@Override
		public String toString() {
			return String.format("%s : %f", category, weight);
		}

		@Override
		public int compareTo(CategoryWeightPair that) {
			return -Float.compare(this.weight, that.weight);
		}
	}

	private SearchFiles() {
	}

	public static void main(String[] args) throws Exception {
		String usage =
				"Usage:\tjava org.ner.SearchFiles [-index dir] [-query file] [-ru] [-pdf]";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		String index = "index";
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

		if (queries == null) {
			System.err.println("-query argument is absent");
			return;
		}

		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer =  ru ? new RussianAnalyzer() : new StandardAnalyzer();

			QueryParser parser = new QueryParser("contents", analyzer);

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
								mergeTwoMaps(searchResult, search(searcher, query, hitsPerPage));
							}
						}
						List<CategoryWeightPair> sorted = sortByWeights(searchResult);
						sorted.forEach(System.out::println);
						System.out.println("\nClustering result:");
						KMeans(sorted).forEach(System.out::println);
						System.out.println("__________________________\n");
					}
				}
			} else {
				String stringQuery = removeNonAlphanumeric(new String(Files.readAllBytes(Paths.get(queries))));
				Query query = parser.parse(stringQuery);
				List<CategoryWeightPair> sorted = sortByWeights(search(searcher, query, hitsPerPage));
				sorted.forEach(System.out::println);
				System.out.println("\nClustering result:");
				KMeans(sorted).forEach(System.out::println);
			}
		}
	}

	static String removeNonAlphanumeric(String s) {
		return s.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
	}

	static Map<String, Float> search(IndexSearcher searcher, Query query, int hitsPerPage) throws IOException {
		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		Map<String, Float> categoryIntoScore = new HashMap<>();
		for (ScoreDoc hit : hits) {
			String category = searcher.doc(hit.doc).get("category");
			addToMap(categoryIntoScore, category, hit.score);
		}
		return categoryIntoScore;
	}

	static List<CategoryWeightPair> KMeans(List<CategoryWeightPair> searchResult) {
		KMeansPlusPlusClusterer<CategoryWeightPair> clusterer = new KMeansPlusPlusClusterer<>(NUMBER_OF_CLUSTERS, 100);
		List<CentroidCluster<CategoryWeightPair>> clusterResults = clusterer.cluster(searchResult);
		CentroidCluster<CategoryWeightPair> max = Collections.max(clusterResults, (c1, c2) ->
						Double.compare(c1.getCenter().getPoint()[0], c2.getCenter().getPoint()[0])
		);
		return max.getPoints();
	}

	static List<CategoryWeightPair> sortByWeights(Map<String, Float> categoriesIntoWeights) {
		List<CategoryWeightPair> storage = new ArrayList<>();
		for (Map.Entry<String, Float> entry : categoriesIntoWeights.entrySet()) {
			storage.add(new CategoryWeightPair(entry));
		}
		Collections.sort(storage);
		return storage;
	}

	static void addToMap(Map<String, Float> mapToAddIn, String keyToAdd, Float valueToAdd) {
		if (mapToAddIn.containsKey(keyToAdd)) {
			mapToAddIn.put(keyToAdd, mapToAddIn.get(keyToAdd) + valueToAdd);
		} else {
			mapToAddIn.put(keyToAdd, valueToAdd);
		}
	}

	static void mergeTwoMaps(Map<String, Float> toMergeIn, Map<String, Float> toBeMerged) {
		for (Map.Entry<String, Float> entry : toBeMerged.entrySet()) {
			addToMap(toMergeIn, entry.getKey(), entry.getValue());
		}
	}
}
