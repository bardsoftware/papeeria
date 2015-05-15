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

import com.bardsoftware.papeeria.sufler.retriever.Retriever;
import com.bardsoftware.papeeria.sufler.struct.oai.OAIPMHtype;
import com.google.common.base.Preconditions;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;

public class OaiRetriever implements Retriever {
    private static final Logger LOGGER = Logger.getLogger(OaiRetriever.class);

    private static final String VERB = "ListRecords";

    private OaiConfiguration myConfiguration;

    public OaiRetriever(OaiConfiguration configuration) {
        myConfiguration = configuration;
        Preconditions.checkNotNull(myConfiguration);
    }

    public void retrieve() {
        boolean firstRequest = true;
        String resumptionToken = null;
        int requestNumber = 1;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            while (resumptionToken != null || firstRequest) {
                if (firstRequest) {
                    firstRequest = false;
                }
                LOGGER.debug("Request #" + requestNumber);
                String uri = getUri(resumptionToken);
                LOGGER.debug("Uri=" + uri);
                HttpGet httpget = new HttpGet(uri);
                try (CloseableHttpResponse response = httpClient.execute(httpget)) {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    File file = saveToFile(is, requestNumber);
                    resumptionToken = getResumptionToken(file);
                    LOGGER.debug("resumptionToken=" + resumptionToken);
                } finally {
                    httpget.releaseConnection();
                }
                requestNumber++;
                Thread.sleep(myConfiguration.getRequestGap() * 1000 + 5);
            }
        } catch (Exception ex) {
            LOGGER.error("Retrieve failed. Caught exception " + ex);
        }
    }

    private String getUri(String resumptionToken) {
        return myConfiguration.getUrl() +
                "?verb=" + VERB +
                ((resumptionToken == null) ?
                        "&metadataPrefix=" + myConfiguration.getMetadata().getPrefix()
                        : "&resumptionToken=" + encodeResumptionToken(resumptionToken));
    }

    private String encodeResumptionToken(String resumptionToken) {
        Preconditions.checkNotNull(resumptionToken);

        String result = resumptionToken;
        try {
            result = URLEncoder.encode(resumptionToken, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Caught exception: " + ex.getMessage());
        }
        return result;
    }

    private File saveToFile(InputStream is, int requestNumber) throws IOException {
        File dir = new File(myConfiguration.getPath() + myConfiguration.getSource());
        dir.mkdirs();
        File file = new File(dir, myConfiguration.getSource() + requestNumber + ".xml");
        Files.copy(is, file.toPath());
        return file;
    }

    private static String getResumptionToken(File file) throws JAXBException {
        String result = null;
        JAXBContext jc = JAXBContext.newInstance("com.bardsoftware.papeeria.sufler.struct.oai");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        OAIPMHtype oaipmh = (OAIPMHtype) ((JAXBElement) unmarshaller.unmarshal(file)).getValue();
        if ((oaipmh != null)
                && (oaipmh.getListRecords() != null)
                && (oaipmh.getListRecords().getResumptionToken() != null)) {
            result = oaipmh.getListRecords().getResumptionToken().getValue();
        }
        return result;
    }

    public static void main(String[] args) {
        OaiConfiguration.Metadata metadata = new OaiConfiguration.Metadata("oai_dc", "com.bardsoftware.papeeria.sufler.retriever.oai.metadata.DublinCoreMetadata");
        OaiConfiguration configuration = new OaiConfiguration(
                "http://export.arxiv.org/oai2",
                "resources/data/test/", "arXiv",
                metadata,
                20);
        Retriever retriever = new OaiRetriever(configuration);
        retriever.retrieve();
    }
}
