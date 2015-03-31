package com.bardsoftware.papeeria.sufler.retriever.oai;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.File;

@XStreamAlias("oaiConfiguration")
public class OaiConfiguration {
    private static final String CONFIGURATION_PATH = "resources/retriever/";
    private static final String CONFIGURATION_FILENAME = "oai_retrieve_config.xml";

    private static OaiConfiguration instance;

    public class Metadata {
        @XStreamAlias("prefix")
        private String prefix;

        @XStreamAlias("class")
        private String clazz;

        public String getPrefix() {
            return prefix;
        }

        public String getClazz() {
            return clazz;
        }
    }

    @XStreamAlias("url")
    private String url;

    @XStreamAlias("path")
    private String path;

    @XStreamAlias("source")
    private String source;

    @XStreamAlias("metadata")
    private Metadata metadata;

    @XStreamAlias("requestGap")
    private long requestGap;

    public OaiConfiguration() {

    }

    private static void readFromFile() {
        XStream xstream = new XStream();
        xstream.processAnnotations(OaiConfiguration.class);
        File file = new File(CONFIGURATION_PATH + CONFIGURATION_FILENAME);
        instance = (OaiConfiguration) (xstream.fromXML(file));
    }

    public static OaiConfiguration getInstance() {
        if (instance == null) {
            readFromFile();
        }
        return instance;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * @return time between requests in seconds
     */
    public long getRequestGap() {
        return requestGap;
    }
}
