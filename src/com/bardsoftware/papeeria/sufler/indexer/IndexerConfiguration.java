package com.bardsoftware.papeeria.sufler.indexer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("indexerConfiguration")
public class IndexerConfiguration {
    public enum Type {
        OAI;
    }

    private static final String CONFIGURATION_PATH = "resources/indexer/";
    private static final String CONFIGURATION_FILENAME = "indexer_config.xml";

    private static IndexerConfiguration instance;

    @XStreamAlias("indexPath")
    private String indexPath;

    @XStreamImplicit(itemFieldName = "directory")
    private List<String> directories = new ArrayList<>();

    public IndexerConfiguration() {

    }

    private static void readFromFile() {
        XStream xstream = new XStream();
        xstream.processAnnotations(IndexerConfiguration.class);
        File file = new File(CONFIGURATION_PATH + CONFIGURATION_FILENAME);
        instance = (IndexerConfiguration) (xstream.fromXML(file));
    }

    public String getIndexPath() {
        return indexPath;
    }

    public List<String> getDirectories() {
        return directories;
    }

    public static IndexerConfiguration getInstance() {
        if (instance == null) {
            readFromFile();
        }
        return instance;
    }
}

