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

package com.bardsoftware.papeeria.sufler.api;

import com.bardsoftware.papeeria.sufler.api.input.SuflerInput;
import com.bardsoftware.papeeria.sufler.api.output.ErrorOutput;
import com.bardsoftware.papeeria.sufler.api.output.Source;
import com.bardsoftware.papeeria.sufler.api.output.SuflerOutput;
import com.bardsoftware.papeeria.sufler.searcher.Searcher;
import com.bardsoftware.papeeria.sufler.searcher.configuration.SearcherConfiguration;
import com.google.common.io.CharStreams;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

public class SuflerServlet extends HttpServlet {

    private static final long serialVersionUID = 1970678161455410871L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doService(request, response);
    }

    private void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jsonOutput = "";
        try {
            // get json from request
            String jsonInput = fetchJsonFromRequest(request);

            // parse json into object
            SuflerInput input = Converter.convertJsonToInput(jsonInput);

            // call Searcher
            SuflerOutput output = search(input);

            // serialize result to json
            jsonOutput = Converter.convertSearchOutputToJson(output);
        } catch (Exception e) {
            jsonOutput = Converter.convertErrorOutputToJson(new ErrorOutput(e.getMessage()));
        } finally {
            // pass json to resp
            fillResponse(response, jsonOutput);
        }
    }

    public static String fetchJsonFromRequest(HttpServletRequest req)
            throws IOException {
        InputStream is = req.getInputStream();
        InputStreamReader reader = new InputStreamReader(is);
        String json = CharStreams.toString(reader);
        return json;
    }

    public static SuflerOutput search(SuflerInput input) throws IOException, ParseException {
        SuflerOutput output = new SuflerOutput();
        SearcherConfiguration configuration = new SearcherConfiguration("/media/sufler/index", 10);
        Searcher searcher = new Searcher(configuration);
        TopDocs docs = searcher.search(decodeFromBase64(input.getQuery()), input.getSize());
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            float score = scoreDoc.score;
            Document doc = searcher.getDocument(scoreDoc);
            Source source = Converter.convertDocumentToSource(doc, score);
            output.getSources().add(source);
        }
        return output;
    }

    private static String decodeFromBase64(String encodedString) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encodedJson = decoder.decode(encodedString);
        String decodedString = new String(encodedJson);
        return decodedString;
    }

    public static void fillResponse(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.getOutputStream().write(json.getBytes());
    }
}
