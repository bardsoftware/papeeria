package com.bardsoftware.papeeria.ner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class IndexFiles {

	private IndexFiles() {
	}

	public static void indexDocs(final IndexWriter writer, Path corpus) throws IOException {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(corpus)) {
			for (Path category : directoryStream) {
				try (BufferedReader index = Files.newBufferedReader(category.resolve("index"), StandardCharsets.UTF_8)) {
					Files.walkFileTree(category, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (!file.getFileName().toString().equals("index")) {
								try {
									String title = removeFirstWord(index.readLine());
									indexDoc(writer, file, title);
								} catch (IOException ignore) {
									// don't index files that can't be read.
								}
							}
							return FileVisitResult.CONTINUE;
						}
					});
				}
			}
		}
	}

	private static String removeFirstWord(String str) {
		return str.split(" ", 2)[1];
	}

	public static void indexDoc(IndexWriter writer, Path file, String title) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			Document doc = new Document();

			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			doc.add(new StringField("category", file.getParent().getFileName().toString(), Field.Store.YES));

			doc.add(new StringField("title", title, Field.Store.YES));

			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}