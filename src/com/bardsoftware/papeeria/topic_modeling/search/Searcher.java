package com.bardsoftware.papeeria.topic_modeling.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bardsoftware.papeeria.topic_modeling.util.MapUtils;
import com.bardsoftware.papeeria.topic_modeling.util.StringUtils;
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
	private static final int CHUNK_LENGTH = 5_000;
	private static final int HITS_PER_PAGE = 50;

	private Searcher() {
	}

	public static List<CategoryWeightPair> searchByPDF(Path pathToPDF, IndexSearcher searcher, Analyzer analyzer)
			throws IOException, ParseException {
		Map<String, Float> searchResult = new HashMap<>();
		QueryParser parser = new QueryParser("contents", analyzer);
		try (PDDocument document = PDDocument.load(pathToPDF.toString())) {
			PDFTextStripper stripper = new PDFTextStripper();
			String stringQuery = StringUtils.removeNonAlphanumeric(stripper.getText(document));
			String[] chunks = StringUtils.splitIntoChunks(stringQuery, CHUNK_LENGTH);

			for (String s : chunks) {
				Query query = parser.parse(s);
				MapUtils.mergeTwoMaps(searchResult, search(searcher, query));
			}
		}
		return CategoryWeightPair.sortByWeights(searchResult);
	}

	public static List<CategoryWeightPair> searchByTxt(Path pathToTxt, IndexSearcher searcher, Analyzer analyzer)
			throws IOException, ParseException {
		String stringQuery = StringUtils.removeNonAlphanumeric(new String(Files.readAllBytes(pathToTxt)));
		QueryParser parser = new QueryParser("contents", analyzer);
		Query query = parser.parse(stringQuery);
		return CategoryWeightPair.sortByWeights(search(searcher, query));
	}

	private static Map<String, Float> search(IndexSearcher searcher, Query query) throws IOException {
		TopDocs results = searcher.search(query, HITS_PER_PAGE);
		ScoreDoc[] hits = results.scoreDocs;

		Map<String, Float> categoryIntoScore = new HashMap<>();
		for (ScoreDoc hit : hits) {
			String category = searcher.doc(hit.doc).get("category");
			MapUtils.addToMap(categoryIntoScore, category, hit.score);
		}
		return categoryIntoScore;
	}
}
