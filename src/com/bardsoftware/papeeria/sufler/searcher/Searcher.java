package com.bardsoftware.papeeria.sufler.searcher;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Searcher {
    protected Logger log = Logger.getLogger(Searcher.class);

    private SearcherConfiguration configuration;
    IndexReader reader;
    IndexSearcher searcher;
    QueryParser queryParser;
    Query query;

    public Searcher() throws IOException {
        configuration = SearcherConfiguration.getInstance();
        Path path = Paths.get(configuration.getIndexDirectory());
        Directory indexDirectory = FSDirectory.open(path);
        reader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(reader);
        queryParser = new QueryParser("description", new StandardAnalyzer());
    }

    public TopDocs search(String searchQuery) throws ParseException, IOException {

        query = queryParser.parse(QueryParser.escape(searchQuery));
        return searcher.search(query, configuration.getSize());
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException {
        return searcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        reader.close();
    }
}
