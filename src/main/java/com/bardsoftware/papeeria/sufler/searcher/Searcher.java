/*
 Copyright 2015 BarD Software s.r.o

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.bardsoftware.papeeria.sufler.searcher;

import com.bardsoftware.papeeria.sufler.searcher.configuration.SearcherConfiguration;
import com.google.common.base.Preconditions;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
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
    private SearcherConfiguration myConfiguration;

    private IndexReader myReader;
    private IndexSearcher mySearcher;
    private QueryParser myQueryParser;

    public Searcher(SearcherConfiguration configuration) throws IOException {
        myConfiguration = configuration;
        Preconditions.checkNotNull(myConfiguration);
        Path path = Paths.get(myConfiguration.getIndexDirectory());
        Directory indexDirectory = FSDirectory.open(path);
        myReader = DirectoryReader.open(indexDirectory);
        mySearcher = new IndexSearcher(myReader);
        myQueryParser = new QueryParser("description", new StandardAnalyzer());
    }

    public TopDocs search(String searchQuery, Integer resultSize) throws ParseException, IOException {
        BooleanQuery.setMaxClauseCount(20000);
        Query myQuery = myQueryParser.parse(QueryParser.escape(searchQuery));
        resultSize = (resultSize != null) ? resultSize : myConfiguration.getSize();
        return mySearcher.search(myQuery, resultSize);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return mySearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        myReader.close();
    }
}
