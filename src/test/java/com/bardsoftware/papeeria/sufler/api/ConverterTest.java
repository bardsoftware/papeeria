package com.bardsoftware.papeeria.sufler.api;

import com.bardsoftware.papeeria.sufler.api.input.SuflerInput;
import com.bardsoftware.papeeria.sufler.api.output.SuflerOutput;
import org.junit.Test;

public class ConverterTest {
    @Test
    public void convertJsonToInputTest() throws Exception {
        String json = "{  \"size\" : 10,  \"query\" : \"Higgs\"}";
        SuflerInput input = Converter.convertJsonToInput(json);
    }

    @Test
    public void convertOutputToJsonTest() throws Exception {
        SuflerOutput output = new SuflerOutput();
        String json = Converter.convertSearchOutputToJson(output);
    }
}
