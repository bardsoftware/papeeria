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

package com.bardsoftware.papeeria.sufler.indexer;

import com.bardsoftware.papeeria.sufler.indexer.configuration.IndexerConfiguration;
import com.bardsoftware.papeeria.sufler.indexer.configuration.IndexerConfigurationXml;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IndexConfigurationTest {
	private static IndexerConfiguration configuration = null;

	@BeforeClass
	public static void setUp() throws Exception {
		configuration = IndexerConfigurationXml.getInstance();
	}

	@Test
	public void testGetIndexerConfiguration() throws Exception {
		assertNotNull("configuration is null", configuration);
	}

	@Test
	public void testGetIndexPath() throws Exception {
		assertNotNull("index path is null", configuration.getIndexPath());
	}

	@Test
	public void testGetDirectories() throws Exception {
		assertNotNull("directories list is null", configuration.getDirectories());

		for (String dir : configuration.getDirectories()) {
			assertNotNull("dir is null", dir);
		}
	}
}
