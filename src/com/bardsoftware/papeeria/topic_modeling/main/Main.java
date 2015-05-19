package com.bardsoftware.papeeria.topic_modeling.main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import com.bardsoftware.papeeria.topic_modeling.index.Indexer;
import com.bardsoftware.papeeria.topic_modeling.search.CategoryWeightPair;
import com.bardsoftware.papeeria.topic_modeling.search.Searcher;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public final class Main {

	private static final String DEFAULT_CORPUS_PATH = "corpus";
	private static final String DEFAULT_INDEX_PATH = "index";
	private static final String DEFAULT_PDF_DIR_PATH = "pdf";
	private static final String DEFAULT_QUERY_FILE_PATH = "query.txt";
	private static final String DELIMITER = "____________________________________\n";
	private static final String INDEX_COMMAND = "index";
	private static final String SEARCH_COMMAND = "search";
	private static final String WRONG_COMMAND_MESSAGE = "Wrong command";
	private static final String WRONG_INDEX_PATH_MESSAGE = "wrong index path: ";
	private static final String WRONG_CORPUS_PATH_MESSAGE = "wrong corpus path: ";
	private static final String WRONG_QUERY_PATH_MESSAGE = "wrong query path: ";
	private static final String UNABLE_TO_PARSE_MESSAGE = "unable to parse the file";

	public static void main(String[] args) {
		final String usage = "Usage:\tclassifier <command> [-options]\n"
				+ "Commands:\n"
				+ "\tindex\n"
				+ "\t\tOptions: [-docs DOCS_PATH] [-index INDEX_PATH] [-update]\n"
				+ "\t\t-docs: specify path to corpus. 'corpus' directory is used as default\n"
				+ "\t\t-index: specify path to index. 'index' directory is used as default\n"
				+ "\t\t-update: does not erase existing corpus - just appends new documents\n"
				+ "\tsearch QUERY_PATH\n"
				+ "\t\tOptions: [-index INDEX_PATH]\n"
				+ "\t\t-index: specify path to index. 'index' directory is used as default\n"
				+ "\t\tQUERY_PATH: path to query txt file (or directory if -pdf is used)."
				+ " Default value is 'pdf' directory if -pdf is used and 'query.txt' otherwise\n"
				+ "\t\t-pdf: searches with pdf as query for every pdf in directory\n"
				+ "Common options:\n"
				+ "\t-ru\n"
				+ "\t\tEnables indexing (or searching) with russian language analyzer.\n"
				+ "\t\tStandard (i.e. english) analyzer is used as default.";
		if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
			System.out.println(usage);
			System.exit(1);
		}
		switch (args[0]) {
			case INDEX_COMMAND:
				index(args);
				break;
			case SEARCH_COMMAND:
				search(args);
				break;
			default:
				System.out.println(WRONG_COMMAND_MESSAGE);
				System.out.println(usage);
		}
	}

	private static void search(String[] args) {
		String stringIndexPath = DEFAULT_INDEX_PATH;
		String stringQueryPath = DEFAULT_QUERY_FILE_PATH;
		boolean pdf = false;
		boolean ru = false;


		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
				case "-index":
					stringIndexPath = args[++i];
					break;
				case "-pdf":
					pdf = true;
					break;
				case "-ru":
					ru = true;
					break;
				default:
					if (i == 1) {
						stringQueryPath = args[i];
					}
					break;
			}
		}

		if (pdf && stringQueryPath.equals(DEFAULT_QUERY_FILE_PATH)) {
			stringQueryPath = DEFAULT_PDF_DIR_PATH;
		}

		final Path queryPath = Paths.get(stringQueryPath), indexPath = Paths.get(stringIndexPath);
		final Date start = new Date();
		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath))) {
			final IndexSearcher searcher = new IndexSearcher(reader);
			final Analyzer analyzer = getAnalyzer(ru);

			if (pdf) {
				searchPDFs(queryPath, searcher, analyzer);
			} else {
				searchTxtFile(queryPath, searcher, analyzer);
			}

			final Date end = new Date();
			System.out.println((end.getTime() - start.getTime()) * 1e-3 + " total seconds");
		} catch (IOException e) {
			System.out.println(WRONG_INDEX_PATH_MESSAGE + stringIndexPath);
		}
	}

	private static void searchPDFs(Path pathToDir, IndexSearcher searcher, Analyzer analyzer) {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pathToDir)) {
			for (Path pathToPDF : directoryStream) {
				try {
					final List<CategoryWeightPair> result = Searcher.searchByPDF(pathToPDF, searcher, analyzer);
					System.out.println(pathToPDF.getFileName());
					CategoryWeightPair.clusterAndPrintToStdOut(result);
					System.out.println(DELIMITER);
				} catch (ParseException e) {
					System.out.println(UNABLE_TO_PARSE_MESSAGE);
				} catch (IOException ignored) {}  // nonPDF file
			}
		} catch (IOException e) {
			System.out.println(WRONG_QUERY_PATH_MESSAGE + pathToDir);
		}
	}

	private static void searchTxtFile(Path pathToFile, IndexSearcher searcher, Analyzer analyzer) {
		try {
			CategoryWeightPair.clusterAndPrintToStdOut(Searcher.searchByTxt(pathToFile, searcher, analyzer));
		} catch (IOException e) {
			System.out.println(WRONG_QUERY_PATH_MESSAGE + pathToFile);
		} catch (ParseException e) {
			System.out.println(UNABLE_TO_PARSE_MESSAGE);
		}
	}

	private static void index(String[] args) {
		String indexPath = DEFAULT_INDEX_PATH;
		String docsPath = DEFAULT_CORPUS_PATH;
		boolean create = true;
		boolean ru = false;

		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
				case "-index":
					indexPath = args[i + 1];
					i++;
					break;
				case "-docs":
					docsPath = args[i + 1];
					i++;
					break;
				case "-update":
					create = false;
					break;
				case "-ru":
					ru = true;
					break;
			}
		}

		final Path documentsDirectory = Paths.get(docsPath);
		if (!Files.isReadable(documentsDirectory)) {
			System.out.println(WRONG_CORPUS_PATH_MESSAGE + documentsDirectory);
			System.exit(1);
		}

		final Date start = new Date();
		try {
			final Directory dir = FSDirectory.open(Paths.get(indexPath));
			final Analyzer analyzer = getAnalyzer(ru);
			final IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			} else {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			}

			final IndexWriter writer = new IndexWriter(dir, iwc);
			Indexer.indexDocs(writer, documentsDirectory);

			writer.forceMerge(1);
			writer.close();

			final Date end = new Date();
			System.out.println((end.getTime() - start.getTime()) * 1e-3 + " total seconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}

	private static Analyzer getAnalyzer(boolean ru) {
		return ru ? new RussianAnalyzer() : new StandardAnalyzer();
	}
}