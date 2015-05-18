package com.bardsoftware.papeeria.sufler.api;

import com.bardsoftware.papeeria.sufler.api.input.SuflerInput;
import com.bardsoftware.papeeria.sufler.api.output.ErrorOutput;
import com.bardsoftware.papeeria.sufler.api.output.Source;
import com.bardsoftware.papeeria.sufler.api.output.SuflerOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;

public class Converter {

    public static SuflerInput convertJsonToInput(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SuflerInput input = mapper.readValue(json, SuflerInput.class);
        return input;
    }

    public static String convertSearchOutputToJson(SuflerOutput output) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(output);
    }

    public static String convertErrorOutputToJson(ErrorOutput output) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(output);
    }

    public static Source convertDocumentToSource(Document doc, float score) {
        Source source = new Source();
        source.setScore(score);
        for (IndexableField field : doc.getFields()) {
            addFieldToSource(source, field);
        }
        return source;
    }

    private static void addFieldToSource(Source source, IndexableField field) {
        String value = field.stringValue();
        switch (field.name().toLowerCase()) {
            case "creator":
                source.getCreators().add(value);
                break;
            case "title":
                source.getTitles().add(value);
                break;
            case "description":
                if ((source.getDescription() == null) || (value.length() > source.getDescription().length())) {
                    source.setDescription(value);
                }
                break;
            case "identifier":
                source.getIdentifiers().add(value);
                break;
            case "subject":
                source.getSubjects().add(value);
                break;
            case "date":
                source.getDates().add(value);
                break;
        }
    }
}
