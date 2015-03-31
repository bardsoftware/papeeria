package com.bardsoftware.papeeria.sufler.searcher;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.File;

@XStreamAlias("searcherConfiguration")
public class SearcherConfiguration {
    private static final String CONFIGURATION_PATH = "resources/searcher/";
    private static final String CONFIGURATION_FILENAME = "searcher_config.xml";

    private static SearcherConfiguration instance;

    @XStreamAlias("indexDirectory")
    private String indexDirectory;

    @XStreamAlias("size")
    private int size;

    private static void readFromFile() {
        XStream xstream = new XStream();
        xstream.processAnnotations(SearcherConfiguration.class);
        File file = new File(CONFIGURATION_PATH + CONFIGURATION_FILENAME);
        instance = (SearcherConfiguration) (xstream.fromXML(file));
    }

    public static SearcherConfiguration getInstance() {
        if (instance == null) {
            readFromFile();
        }
        return instance;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public int getSize() {
        return size;
    }
}
