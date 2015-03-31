package com.bardsoftware.papeeria.sufler.indexer;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Indexer {
    protected static Logger logger = Logger.getLogger(Indexer.class);

    private IndexerConfiguration configuration;
    protected IndexWriter writer;

    public Indexer() {
        configuration = IndexerConfiguration.getInstance();
    }

    public void index() throws IOException {
        writer = createWriter();
        for (String path : configuration.getDirectories()) {
            File directory = new File(path);
            if (isValidDirectory(directory)) {
                indexDirectory(directory);
            }
        }
        writer.commit();
        writer.close();
    }

    private void indexDirectory(File directory) throws IOException {
        logger.debug("Indexing directory: " + directory.getName());
        File[] files = directory.listFiles();
        for (File file : files) {
            if (isValidFile(file)) {
                indexFile(file);
            }
        }
    }

    protected abstract void indexFile(File file) throws IOException;

    private IndexWriter createWriter() throws IOException {
        Path path = Paths.get(configuration.getIndexPath());
        Directory indexDir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(indexDir, config);
        return indexWriter;
    }

    private static boolean isValidDirectory(File directory) {
        if (!directory.exists()) {
            return false;
        }

        if (!directory.isDirectory()) {
            return false;
        }

        return true;
    }

    private boolean isValidFile(File file) {
        if (file.isDirectory()) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }

        if (file.isHidden()) {
            return false;
        }

        if (!file.canRead()) {
            return false;
        }
        return true;
    }
}
