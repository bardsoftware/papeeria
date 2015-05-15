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

public class OaiConfiguration {

    public static class Metadata {
        public Metadata(String myPrefix, String myClazz) {
            this.myPrefix = myPrefix;
            this.myClazz = myClazz;
        }

        private String myPrefix;

        private String myClazz;

        public String getPrefix() {
            return myPrefix;
        }

        public String getClazz() {
            return myClazz;
        }
    }

    private String myUrl;

    private String myPath;

    private String mySource;

    private Metadata myMetadata;

    private long myRequestGap;

    public OaiConfiguration(String myUrl, String myPath, String mySource, Metadata myMetadata, long myRequestGap) {
        this.myUrl = myUrl;
        this.myPath = myPath;
        this.mySource = mySource;
        this.myMetadata = myMetadata;
        this.myRequestGap = myRequestGap;
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
