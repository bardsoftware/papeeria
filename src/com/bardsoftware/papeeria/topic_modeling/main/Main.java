package com.bardsoftware.papeeria.topic_modeling.main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

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


public class Main {

	public static final String DEFAULT_CORPUS_PATH = "corpus";
	public static final String DEFAULT_INDEX_PATH = "index";
	public static final String DEFAULT_PDF_DIR_PATH = "pdf";
	public static final String DEFAULT_QUERY_FILE_PATH = "query.txt";
	public static final String DELIMITER = "____________________________________\n";
	public static final String INDEX_COMMAND = "index";
	public static final String SEARCH_COMMAND = "search";
	public static final String WRONG_COMMAND_MESSAGE = "Wrong command";
	public static final String WRONG_INDEX_PATH_MESSAGE = "wrong index path: ";
	public static final String WRONG_QUERY_PATH_MESSAGE = "wrong query path: ";
	public static final String UNABLE_TO_PARSE_MESSAGE = "unable to parse the file";

	public static void main(String[] args) {
		String usage = "Usage:\tclassifier <command> [-options]\n"
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
		String index = DEFAULT_INDEX_PATH;
		String path = DEFAULT_QUERY_FILE_PATH;
		boolean pdf = false;
		boolean ru = false;


		for (int i = 1; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				index = args[++i];
			} else if ("-pdf".equals(args[i])) {
				pdf = true;
			} else if ("-ru".equals(args[i])) {
				ru = true;
			} else if (i == 1) {
				path = args[i];
			}
		}

		if (pdf && path.equals(DEFAULT_QUERY_FILE_PATH)) {
			path = DEFAULT_PDF_DIR_PATH;
		}

		Date start = new Date();
		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = getAnalyzer(ru);

			if (pdf) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(path))) {
					for (Path pathToPDF : directoryStream) {
						System.out.println(pathToPDF.getFileName());
						try {
							CategoryWeightPair.clusterAndPrintToStdOut(Searcher.searchByPDF(pathToPDF, searcher, analyzer));
						} catch (ParseException e) {
							System.out.println(UNABLE_TO_PARSE_MESSAGE);
						}
						System.out.println(DELIMITER);
					}
				} catch (IOException e) {
					System.out.println(WRONG_QUERY_PATH_MESSAGE + path);
				}
			} else {
				try {
					CategoryWeightPair.clusterAndPrintToStdOut(Searcher.searchByTxt(Paths.get(path), searcher, analyzer));
				} catch (IOException e) {
					System.out.println(WRONG_QUERY_PATH_MESSAGE + path);
				} catch (ParseException e) {
					System.out.println(UNABLE_TO_PARSE_MESSAGE);
				}
			}
			Date end = new Date();
			System.out.println((end.getTime() - start.getTime()) * 1e-3 + " total seconds");
		} catch (IOException e) {
			System.out.println(WRONG_INDEX_PATH_MESSAGE + index);
		}
	}

	private static void index(String[] args) {
		String indexPath = DEFAULT_INDEX_PATH;
		String docsPath = DEFAULT_CORPUS_PATH;
		boolean create = true;
		boolean ru = false;

		for (int i = 1; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-docs".equals(args[i])) {
				docsPath = args[i + 1];
				i++;
			} else if ("-update".equals(args[i])) {
				create = false;
			} else if ("-ru".equals(args[i])) {
				ru = true;
			}
		}

		Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath() +
					"' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = getAnalyzer(ru);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			} else {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			}

			IndexWriter writer = new IndexWriter(dir, iwc);
			Indexer.indexDocs(writer, docDir);

			writer.forceMerge(1);

			writer.close();

			Date end = new Date();
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
