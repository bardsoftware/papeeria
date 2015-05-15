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

import org.junit.Test;

public class OaiRetrieverTest {
    @Test
    public void testRetrieve() {
        OaiConfiguration.Metadata metadata = new OaiConfiguration.Metadata("oai_dc", "com.bardsoftware.papeeria.sufler.retriever.oai.metadata.DublinCoreMetadata");
        OaiConfiguration configuration = new OaiConfiguration(
                "http://export.arxiv.org/oai2",
                "resources/data/test/", "arXiv",
                metadata,
                20);
        OaiRetriever retriever = new OaiRetriever(configuration);
        //retriever.retrieve();
    }
}
