package com.bardsoftware.papeeria.sufler.retriever.oai;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import com.bardsoftware.papeeria.sufler.retriever.Retriever;
import com.bardsoftware.papeeria.sufler.struct.oai.OAIPMHtype;

import javax.xml.bind.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;

public class OaiRetriever implements Retriever {
    private static Logger logger = Logger.getLogger(OaiRetriever.class);

    private static final String VERB = "ListRecords";

    private OaiConfiguration configuration;

    public OaiRetriever() {
        configuration = OaiConfiguration.getInstance();
    }

    public void retrieve() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        boolean firstRequest = true;
        String resumptionToken = null;
        int requestNumber = 1;

        try {
            while (resumptionToken != null || firstRequest) {
                if (firstRequest) {
                    firstRequest = false;
                }
                logger.debug("Request #" + requestNumber);
                String uri = getUri(resumptionToken);
                logger.debug("Uri=" + uri);
                HttpGet httpget = new HttpGet(uri);
                CloseableHttpResponse response = httpClient.execute(httpget);
                try {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    File file = saveToFile(is, requestNumber);
                    resumptionToken = getResumptionToken(file);
                    logger.debug("resumptionToken=" + resumptionToken);
                } finally {
                    httpget.releaseConnection();
                }
                requestNumber++;
                Thread.sleep(configuration.getRequestGap() * 1000 + 5);
            }
        } catch (Exception ex) {
            logger.error("Retrieve failed. Caught exception " + ex);
        } finally {
            try {
                httpClient.close();
            } catch (IOException ex) {

            }
        }
    }

    private String getUri(String resumptionToken) {
        return configuration.getUrl() +
                "?verb=" + VERB +
                ((resumptionToken == null) ?
                        "&metadataPrefix=" + configuration.getMetadata().getPrefix()
                        : "&resumptionToken=" + encode(resumptionToken));
    }

    private String encode(String str) {
        if (str == null)
            return str;

        String result = str;
        try {
            if (str != null) {
                result = URLEncoder.encode(str, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            logger.error("Caught exception: " + ex.getMessage());
        }
        return result;
    }

    private File saveToFile(InputStream is, int requestNumber) throws IOException {
        File dir = new File(configuration.getPath() + configuration.getSource());
        dir.mkdirs();
        File file = new File(dir, configuration.getSource() + requestNumber + ".xml");
        Files.copy(is, file.toPath());
        return file;
    }

    private String getResumptionToken(File file) throws JAXBException {
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
        Retriever retriever = new OaiRetriever();
        retriever.retrieve();
    }
}
