package com.bardsoftware.papeeria.topic_modeling.index;

import com.bardsoftware.papeeria.topic_modeling.util.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
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
import java.util.HashMap;
import java.util.Map;

public final class Indexer {

	private Indexer() {
	}

	public static void indexDocs(IndexWriter writer, Path corpus) throws IOException {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(corpus)) {
			final Map<String, Integer> sizes = readSizes(corpus.resolve("sizes"));
			for (Path categoryDir : directoryStream) {
				if (Files.isDirectory(categoryDir)) {
					try (BufferedReader index = Files.newBufferedReader(categoryDir.resolve("index"), StandardCharsets.UTF_8)) {
						Files.walkFileTree(categoryDir, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								if (!file.getFileName().toString().equals("index")) {
									try {
										final String title = StringUtils.removeFirstWord(index.readLine());
										indexDoc(writer, file, title, sizes);
									} catch (IOException e) {
										System.err.printf("unable to index a '%s' file", file.getFileName());
									}
								}
								return FileVisitResult.CONTINUE;
							}
						});
					}
				}
			}
		}
	}

	private static Map<String, Integer> readSizes(Path pathToSizesFile) throws IOException {
		final Map<String, Integer> sizes = new HashMap<>();
		final String content = new String(Files.readAllBytes(pathToSizesFile));
		final String[] split = content.split("\\s");
		for (int i = 0; i < split.length; i++) {
			final String category = split[i];
			final Integer size = Integer.parseInt(split[++i]);
			sizes.put(category, size);
		}
		return sizes;
	}

	public static void indexDoc(IndexWriter writer, Path file, String title, Map<String, Integer> sizes) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			final Document doc = new Document();

			final Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			final String category = file.getParent().getFileName().toString();
			doc.add(new StringField("category", category, Field.Store.YES));

			doc.add(new IntField("category_size", sizes.getOrDefault(category, -1), Field.Store.YES));

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
