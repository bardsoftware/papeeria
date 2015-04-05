package com.bardsoftware.papeeria.ner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class Searcher {
	private static final int STEP = 5_000;
	private static final int NUMBER_OF_CLUSTERS = 3;
	private static final int HITS_PER_PAGE = 50;

	private Searcher() {
	}

	private static String removeNonAlphanumeric(String s) {
		return s.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
	}

	public static List<CategoryWeightPair> cluster(List<CategoryWeightPair> searchResult) {
		KMeansPlusPlusClusterer<CategoryWeightPair> clusterer = new KMeansPlusPlusClusterer<>(NUMBER_OF_CLUSTERS, 100);
		List<CentroidCluster<CategoryWeightPair>> clusterResults = clusterer.cluster(searchResult);
		CentroidCluster<CategoryWeightPair> maxCluster = Collections.max(clusterResults, (c1, c2) ->
						Double.compare(c1.getCenter().getPoint()[0], c2.getCenter().getPoint()[0])
		);
		return maxCluster.getPoints();
	}

	private static List<CategoryWeightPair> sortByWeights(Map<String, Float> categoriesIntoWeights) {
		List<CategoryWeightPair> storage = new ArrayList<>();
		for (Map.Entry<String, Float> entry : categoriesIntoWeights.entrySet()) {
			storage.add(new CategoryWeightPair(entry));
		}
		Collections.sort(storage);
		return storage;
	}

	private static void addToMap(Map<String, Float> mapToAddIn, String keyToAdd, Float valueToAdd) {
		if (mapToAddIn.containsKey(keyToAdd)) {
			mapToAddIn.put(keyToAdd, mapToAddIn.get(keyToAdd) + valueToAdd);
		} else {
			mapToAddIn.put(keyToAdd, valueToAdd);
		}
	}

	private static void mergeTwoMaps(Map<String, Float> toMergeIn, Map<String, Float> toBeMerged) {
		for (Map.Entry<String, Float> entry : toBeMerged.entrySet()) {
			addToMap(toMergeIn, entry.getKey(), entry.getValue());
		}
	}

	public static List<CategoryWeightPair> searchByPDF(Path pathToPDF, IndexSearcher searcher, Analyzer analyzer)
			throws IOException, ParseException {
		Map<String, Float> searchResult = new HashMap<>();
		QueryParser parser = new QueryParser("contents", analyzer);
		try (PDDocument document = PDDocument.load(pathToPDF.toString())) {
			PDFTextStripper stripper = new PDFTextStripper();
			String stringQuery = removeNonAlphanumeric(stripper.getText(document));
			String[] chunks = stringQuery.split(String.format("(?<=\\G.{%d})", STEP));

			for (String s : chunks) {
				Query query = parser.parse(s);
				mergeTwoMaps(searchResult, search(searcher, query));
			}
		}
		return sortByWeights(searchResult);
	}

	public static List<CategoryWeightPair> searchByTxt(Path pathToTxt, IndexSearcher searcher, Analyzer analyzer)
			throws IOException, ParseException {
		String stringQuery = removeNonAlphanumeric(new String(Files.readAllBytes(pathToTxt)));
		QueryParser parser = new QueryParser("contents", analyzer);
		Query query = parser.parse(stringQuery);
		return sortByWeights(search(searcher, query));
	}

	private static Map<String, Float> search(IndexSearcher searcher, Query query) throws IOException {
		TopDocs results = searcher.search(query, HITS_PER_PAGE);
		ScoreDoc[] hits = results.scoreDocs;

		Map<String, Float> categoryIntoScore = new HashMap<>();
		for (ScoreDoc hit : hits) {
			String category = searcher.doc(hit.doc).get("category");
			addToMap(categoryIntoScore, category, hit.score);
		}
		return categoryIntoScore;
	}
}
