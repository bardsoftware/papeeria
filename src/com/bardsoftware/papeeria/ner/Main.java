package com.bardsoftware.papeeria.ner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

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
		String usage = "Usage:\tjava com.bardsoftware.papeeria.ner.Main <command> [-options]\n"
				+ "Commands:\n"
				+ "\tindex\n"
				+ "\t\tOptions: [-docs DOCS_PATH] [-index INDEX_PATH] [-update]\n"
				+ "\tsearch\n"
				+ "\t\tOptions: [-index INDEX_PATH] [-query QUERY_PATH] [-pdf PDF_DIR_PATH]\n"
				+ "\t\tIf -query, searches with txt file as query\n"
				+ "\t\tIf -pdf, searches with pdf as query for every pdf in directory\n"
				+ "\t\tIf PDF_DIR_PATH is omitted, 'pdf' dir is used as default\n"
				+ "Common options:\n"
				+ "\t [-ru]"
				+ "\t\t Enables indexing (or searching) with russian language analyzer";
		if (args.length == 0) {
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

	private static void index(String[] args) {
		String indexPath = "index";
		String docsPath = "corpus";
		boolean create = true;
		boolean ru = false;

		for (int i = 0; i < args.length; i++) {
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

	private static void search(String[] args) {
		String index = "index";
		String query = null;
		String pdfs = "pdf";
		boolean ru = false;


		for (int i = 1; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				index = args[++i];
			} else if ("-query".equals(args[i])) {
				query = args[++i];
			} else if ("-pdf".equals(args[i])) {
				pdfs = args[++i];
			} else if ("-ru".equals(args[i])) {
				ru = true;
			}
		}

		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer =  ru ? new RussianAnalyzer() : new StandardAnalyzer();

			if (query != null) {
				toString(Searcher.searchByTxt(Paths.get(query), searcher, analyzer));
			} else {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(pdfs))) {
					for (Path pathToPDF : directoryStream) {
						System.out.println(pathToPDF.getFileName());
						try {
							toString(Searcher.searchByPDF(pathToPDF, searcher, analyzer));
						} catch (ParseException e) {
							System.out.println("unable to parse this pdf file");
						}
						System.out.println("__________________________________\n");
					}
				} catch (IOException e) {
					System.err.println("wrong pdfs dir");
				}
			}
		} catch (IOException e) {
			System.err.println("wrong index dir");
		} catch (ParseException ignored) {
		}
	}

	private static void toString(List<CategoryWeightPair> sorted) {
		sorted.forEach(System.out::println);
		System.out.println("\nClustering result:");
		Searcher.cluster(sorted).forEach(System.out::println);
	}

}
