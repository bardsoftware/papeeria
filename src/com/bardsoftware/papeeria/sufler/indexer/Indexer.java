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

package com.bardsoftware.papeeria.sufler.indexer;

import com.bardsoftware.papeeria.sufler.indexer.configuration.IndexerConfiguration;
import com.google.common.base.Preconditions;
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
    private static final Logger LOGGER = Logger.getLogger(Indexer.class);

    private IndexerConfiguration myConfiguration;
    protected IndexWriter myWriter;

    public Indexer(IndexerConfiguration configuration) {
        myConfiguration = configuration;
        Preconditions.checkNotNull(myConfiguration);
    }

    public void index() throws IOException {
        myWriter = createWriter();
        for (String path : myConfiguration.getDirectories()) {
            File directory = new File(path);
            if (isValidDirectory(directory)) {
                indexDirectory(directory);
            }
        }
        myWriter.commit();
        myWriter.close();
    }

    private void indexDirectory(File directory) throws IOException {
        LOGGER.debug("Indexing directory: " + directory.getName());
        File[] files = directory.listFiles();
        for (File file : files) {
            if (isValidFile(file)) {
                indexFile(file);
            }
        }
    }

    protected abstract void indexFile(File file) throws IOException;

    private IndexWriter createWriter() throws IOException {
        Path path = Paths.get(myConfiguration.getIndexPath());
        Directory indexDir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        return new IndexWriter(indexDir, config);
    }

    private static boolean isValidDirectory(File directory) {
        return (directory.isDirectory());
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
