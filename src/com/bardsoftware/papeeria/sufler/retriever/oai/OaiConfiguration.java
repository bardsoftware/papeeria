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

package com.bardsoftware.papeeria.sufler.retriever.oai;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.File;

@XStreamAlias("oaiConfiguration")
public class OaiConfiguration {
    private static final String CONFIGURATION_PATH = "resources/retriever/";
    private static final String CONFIGURATION_FILENAME = "oai_retrieve_config.xml";

    private static OaiConfiguration ourInstance;

    public class Metadata {
        @XStreamAlias("prefix")
        private String myPrefix;

        @XStreamAlias("class")
        private String myClazz;

        public String getPrefix() {
            return myPrefix;
        }

        public String getClazz() {
            return myClazz;
        }
    }

    @XStreamAlias("url")
    private String myUrl;

    @XStreamAlias("path")
    private String myPath;

    @XStreamAlias("source")
    private String mySource;

    @XStreamAlias("metadata")
    private Metadata myMetadata;

    @XStreamAlias("requestGap")
    private long myRequestGap;

    public OaiConfiguration() {

    }

    private static void readFromFile() {
        XStream xstream = new XStream();
        xstream.processAnnotations(OaiConfiguration.class);
        File file = new File(CONFIGURATION_PATH + CONFIGURATION_FILENAME);
        ourInstance = (OaiConfiguration) (xstream.fromXML(file));
    }

    public static OaiConfiguration getInstance() {
        if (ourInstance == null) {
            readFromFile();
        }
        return ourInstance;
    }

    public String getUrl() {
        return myUrl;
    }

    public String getPath() {
        return myPath;
    }

    public String getSource() {
        return mySource;
    }

    public Metadata getMetadata() {
        return myMetadata;
    }

    /**
     * @return time between requests in seconds
     */
    public long getRequestGap() {
        return myRequestGap;
    }
}
