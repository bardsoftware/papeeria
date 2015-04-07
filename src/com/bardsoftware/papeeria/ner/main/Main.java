package com.bardsoftware.papeeria.ner.main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import com.bardsoftware.papeeria.ner.index.Indexer;
import com.bardsoftware.papeeria.ner.search.CategoryWeightPair;
import com.bardsoftware.papeeria.ner.search.Searcher;
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
	public static void main(String[] args) {
		String usage = "Usage:\tclassifier <command> [-options]\n"
				+ "Commands:\n"
				+ "\tindex\n"
				+ "\t\tOptions: [-docs DOCS_PATH] [-index INDEX_PATH] [-update]\n"
				+ "\t\t-docs: specify path to corpus. 'corpus' directory is used as default\n"
				+ "\t\t-index: specify path to index. 'index' directory is used as default\n"
				+ "\t\t-update: does not erase existing corpus - just appends new documents\n"
				+ "\tsearch\n"
				+ "\t\tOptions: [-index INDEX_PATH] QUERY_PATH [-pdf PDF_DIR_PATH]\n"
				+ "\t\t-index: specify path to index. 'index' directory is used as default\n"
				+ "\t\tQUERY_PATH: path to query txt file or directory (if -pdf)."
				+ " Default value is 'pdf' directory (if -pdf) and 'query.txt' otherwise\n"
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
			case "search":
				search(args);
				break;
			case "index":
				index(args);
				break;
			default:
				System.out.println(usage);
		}
	}

	private static void search(String[] args) {
		String index = "index";
		String path = "query.txt";
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

		if (pdf && path.equals("query.txt")) {
			path = "pdf";
		}

		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer =  ru ? new RussianAnalyzer() : new StandardAnalyzer();

			if (pdf) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(path))) {
					for (Path pathToPDF : directoryStream) {
						System.out.println(pathToPDF.getFileName());
						try {
							CategoryWeightPair.clusterAndPrintToStdOut(Searcher.searchByPDF(pathToPDF, searcher, analyzer));
						} catch (ParseException e) {
							System.out.println("unable to parse this pdf file");
						}
						System.out.println("__________________________________\n");
					}
				} catch (IOException e) {
					System.err.println("wrong pdf dir: " + path);
				}
			} else {
				try {
					CategoryWeightPair.clusterAndPrintToStdOut(Searcher.searchByTxt(Paths.get(path), searcher, analyzer));
				} catch (IOException e) {
					System.err.println("wrong query path: " + path);
				}
			}
		} catch (IOException e) {
			System.err.println("wrong index dir: " + index);
			e.printStackTrace();
		} catch (ParseException ignored) {
		}
	}

	private static void index(String[] args) {
		String indexPath = "index";
		String docsPath = "corpus";
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

		final Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath() +
					"' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer =  ru ? new RussianAnalyzer() : new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			} else {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer.  But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, iwc);
			Indexer.indexDocs(writer, docDir);

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here.  This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			writer.forceMerge(1);

			writer.close();

			Date end = new Date();
			System.out.println((end.getTime() - start.getTime()) * 1e-3 + " total seconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}
}
