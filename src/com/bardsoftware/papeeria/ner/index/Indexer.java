package com.bardsoftware.papeeria.ner.index;

import com.bardsoftware.papeeria.ner.util.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Indexer {

	private Indexer() {
	}

	public static void indexDocs(final IndexWriter writer, Path corpus) throws IOException {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(corpus)) {
			for (Path categoryDir : directoryStream) {
				try (BufferedReader index = Files.newBufferedReader(categoryDir.resolve("index"), StandardCharsets.UTF_8)) {
					Files.walkFileTree(categoryDir, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (!file.getFileName().toString().equals("index")) {
								try {
									String title = StringUtils.removeFirstWord(index.readLine());
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