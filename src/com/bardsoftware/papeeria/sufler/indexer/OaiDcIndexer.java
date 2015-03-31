package com.bardsoftware.papeeria.sufler.indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.w3c.dom.Node;
import com.bardsoftware.papeeria.sufler.struct.oai.MetadataType;
import com.bardsoftware.papeeria.sufler.struct.oai.OAIPMHtype;
import com.bardsoftware.papeeria.sufler.struct.oai.RecordType;
import com.bardsoftware.papeeria.sufler.struct.oai.dc.ElementType;
import com.bardsoftware.papeeria.sufler.struct.oai.dc.OaiDcType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OaiDcIndexer extends Indexer {
    private Map<String, IndexedField> fieldsMap;

    public OaiDcIndexer() {
        fieldsMap = new HashMap<>();
        fieldsMap.put("creator", IndexedField.CREATOR);
        fieldsMap.put("title", IndexedField.TITLE);
        fieldsMap.put("description", IndexedField.DESCRIPTION);
        fieldsMap.put("identifier", IndexedField.IDENTIFIER);
        fieldsMap.put("subject", IndexedField.SUBJECT);
        fieldsMap.put("date", IndexedField.DATE);
    }

    @Override
    protected void indexFile(File file) throws IOException {
        try {
            logger.debug("Indexing file: " + file.getName());
            JAXBContext jaxbContext = JAXBContext.newInstance("com.bardsoftware.papeeria.sufler.struct.oai");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            OAIPMHtype oaiDoc = (OAIPMHtype) ((JAXBElement) unmarshaller.unmarshal(file)).getValue();

            for (RecordType record : oaiDoc.getListRecords().getRecord()) {
                indexRecord(record);
            }
        } catch (JAXBException e) {
            logger.error("Error when indexing file " + file.getName(), e);
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

            if (fieldsMap.containsKey(name)) {
                field = createField(name, value);
            }

            if (field != null) {
                document.add(field);
            }
        }

        writer.addDocument(document);
    }

    private Field createField(String name, String value) {
        IndexedField indexedField = fieldsMap.get(name);
        return indexedField.createField(value);
    }

    public static void main(String[] args) throws IOException {
        Indexer indexer = new OaiDcIndexer();
        indexer.index();
    }
}
