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

package com.bardsoftware.papeeria.sufler.indexer.configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("indexerConfiguration")
public class IndexerConfigurationXml implements IndexerConfiguration {

    private static final String CONFIGURATION_PATH = "resources/indexer/";
    private static final String CONFIGURATION_FILENAME = "indexer_config.xml";

    private static IndexerConfiguration ourInstance;

    @XStreamAlias("indexPath")
    private String myIndexPath;

    @XStreamImplicit(itemFieldName = "directory")
    private List<String> myDirectories = new ArrayList<>();

    public IndexerConfigurationXml() {

    }

    private static void readFromFile() {
        XStream xstream = new XStream();
        xstream.processAnnotations(IndexerConfigurationXml.class);
        File file = new File(CONFIGURATION_PATH + CONFIGURATION_FILENAME);
        ourInstance = (IndexerConfiguration) (xstream.fromXML(file));
    }

    @Override
    public String getIndexPath() {
        return myIndexPath;
    }

    @Override
    public List<String> getDirectories() {
        return myDirectories;
    }

    public static IndexerConfiguration getInstance() {
        if (ourInstance == null) {
            readFromFile();
        }
        return ourInstance;
    }
}

