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

package com.bardsoftware.papeeria.sufler.searcher;

import com.bardsoftware.papeeria.sufler.searcher.configuration.SearcherConfiguration;
import com.bardsoftware.papeeria.sufler.searcher.configuration.SearcherConfigurationXml;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class SearcherTest {
	private static Searcher searcher;

	@BeforeClass
	public static void setUp() throws IOException {
        SearcherConfiguration configuration = SearcherConfigurationXml.getInstance();
        searcher = new Searcher(configuration);
	}

	@Test
	public void testSearch() throws IOException, ParseException {
		String text = "The Higgs boson or Higgs particle is an elementary particle in the Standard Model of particle physics. Its main relevance is that it allows scientists to explore the Higgs field[5][6] – a fundamental field first suspected to exist in the 1960s that unlike the more familiar electromagnetic field cannot be \"turned off\", but instead takes a non-zero constant value almost everywhere. The presence of this field – now believed to be confirmed – explains why some fundamental particles have mass even though the symmetries controlling their interactions should require them to be massless, and also answers several other long-standing puzzles in physics, such as the reason the weak force has a much shorter range than the electromagnetic force.\n" +
				"\n" +
				"Despite being present everywhere, the existence of the Higgs field is very hard to confirm. It can be detected through its excitations (i.e. Higgs particles), but these are extremely hard to produce and detect. The importance of this fundamental question led to a 40 year search for this elusive particle, and the construction of one of the world's most expensive and complex experimental facilities to date, CERN's Large Hadron Collider,[7] able to create Higgs bosons and other particles for observation and study. On 4 July 2012, the discovery of a new particle with a mass between 125 and 127 GeV/c2 was announced; physicists suspected that it was the Higgs boson.[8][9][10] By March 2013, the particle had been proven to behave, interact and decay in many of the ways predicted by the Standard Model, and was also tentatively confirmed to have positive parity and zero spin,[1] two fundamental attributes of a Higgs boson. This appears to be the first elementary scalar particle discovered in nature.[11] More data is needed to know if the discovered particle exactly matches the predictions of the Standard Model, or whether, as predicted by some theories, multiple Higgs bosons exist.[3]\n" +
				"\n" +
				"The Higgs boson is named after Peter Higgs, one of six physicists who, in 1964, proposed the mechanism that suggested the existence of such a particle. Although Higgs's name has come to be associated with this theory, several researchers between about 1960 and 1972 each independently developed different parts of it. In mainstream media the Higgs boson has often been called the \"God particle\", from a 1993 book on the topic; the nickname is strongly disliked by many physicists, including Higgs, who regard it as inappropriate sensationalism.[12][13] On December 10, 2013 two of the original researchers, Peter Higgs and François Englert, were awarded the Nobel Prize in Physics for their work and prediction.[14] Englert's co-researcher Robert Brout had died in 2011 and the Nobel Prize is not ordinarily given posthumously.\n" +
				"\n" +
				"In the Standard Model, the Higgs particle is a boson with no spin, electric charge, or colour charge. It is also very unstable, decaying into other particles almost immediately. It is a quantum excitation of one of the four components of the Higgs field. The latter constitutes a scalar field, with two neutral and two electrically charged components, and forms a complex doublet of the weak isospin SU(2) symmetry. The field has a \"Mexican hat\" shaped potential with nonzero strength everywhere (including otherwise empty space), which in its vacuum state breaks the weak isospin symmetry of the electroweak interaction. When this happens, three components of the Higgs field are \"absorbed\" by the SU(2) and U(1) gauge bosons (the \"Higgs mechanism\") to become the longitudinal components of the now-massive W and Z bosons of the weak force. The remaining electrically neutral component separately couples to other particles known as fermions (via Yukawa couplings), causing these to acquire mass as well. Some versions of the theory predict more than one kind of Higgs fields and bosons. Alternative \"Higgsless\" models would have been considered if the Higgs boson was not discovered.";
		//	String text = "Higgs boson";
		TopDocs result = searcher.search(text);
		Document doc = searcher.getDocument(result.scoreDocs[0]);
		//assertEquals(text, doc.get("description"));

		for(ScoreDoc scoreDoc : result.scoreDocs) {
			System.out.print(scoreDoc.score + ": ");
			doc = searcher.getDocument(scoreDoc);
			for (IndexableField field : doc.getFields("identifier")) {
				System.out.print(field.stringValue() + "; ");
			}
			System.out.println("\n");
		}
	}


	@AfterClass
	public static  void clean() throws IOException {
		searcher.close();
	}
}
