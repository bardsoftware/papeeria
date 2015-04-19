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

package com.bardsoftware.papeeria.sufler.indexer.oai;

import com.bardsoftware.papeeria.sufler.indexer.IndexedField;
import com.bardsoftware.papeeria.sufler.indexer.Indexer;
import com.bardsoftware.papeeria.sufler.indexer.configuration.IndexerConfiguration;
import com.bardsoftware.papeeria.sufler.indexer.configuration.IndexerConfigurationXml;
import com.bardsoftware.papeeria.sufler.struct.oai.MetadataType;
import com.bardsoftware.papeeria.sufler.struct.oai.OAIPMHtype;
import com.bardsoftware.papeeria.sufler.struct.oai.RecordType;
import com.bardsoftware.papeeria.sufler.struct.oai.dc.ElementType;
import com.bardsoftware.papeeria.sufler.struct.oai.dc.OaiDcType;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OaiDcIndexer extends Indexer {
    private static final Logger LOGGER = Logger.getLogger(Indexer.class);

    private Map<String, IndexedField> myFieldsMap;

    public OaiDcIndexer(IndexerConfiguration configuration) {
        super(configuration);
        myFieldsMap = new HashMap<>();
        myFieldsMap.put("creator", IndexedField.CREATOR);
        myFieldsMap.put("title", IndexedField.TITLE);
        myFieldsMap.put("description", IndexedField.DESCRIPTION);
        myFieldsMap.put("identifier", IndexedField.IDENTIFIER);
        myFieldsMap.put("subject", IndexedField.SUBJECT);
        myFieldsMap.put("date", IndexedField.DATE);
    }

    @Override
    protected void indexFile(File file) throws IOException {
        try {
            LOGGER.debug("Indexing file: " + file.getName());
            JAXBContext jaxbContext = JAXBContext.newInstance("com.bardsoftware.papeeria.sufler.struct.oai");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            OAIPMHtype oaiDoc = (OAIPMHtype) ((JAXBElement) unmarshaller.unmarshal(file)).getValue();

            for (RecordType record : oaiDoc.getListRecords().getRecord()) {
                indexRecord(record);
            }
        } catch (JAXBException e) {
            LOGGER.error("Error when indexing file " + file.getName(), e);
        }

    }

    private void indexRecord(RecordType record) throws JAXBException, IOException {
        Document document = new Document();

        MetadataType metadata = record.getMetadata();
        Node oaiDcNode = (Node) metadata.getAny();
        JAXBContext dcContext = JAXBContext.newInstance("com.bardsoftware.papeeria.sufler.struct.oai.dc");
        Unmarshaller un = dcContext.createUnmarshaller();
        OaiDcType dc = (OaiDcType) ((JAXBElement) un.unmarshal(oaiDcNode)).getValue();
        for (JAXBElement<ElementType> element : dc.getTitleOrCreatorOrSubject()) {
            Field field = null;
            String name = element.getName().getLocalPart();
            String value = element.getValue().getValue().trim();

            if (myFieldsMap.containsKey(name)) {
                field = createField(name, value);
            }

            if (field != null) {
                document.add(field);
            }
        }

        myWriter.addDocument(document);
    }

    private Field createField(String name, String value) {
        IndexedField indexedField = myFieldsMap.get(name);
        return indexedField.createField(value);
    }

    public static void main(String[] args) throws IOException {
        IndexerConfiguration configuration = IndexerConfigurationXml.getInstance();
        Indexer indexer = new OaiDcIndexer(configuration);
        indexer.index();
    }
}
