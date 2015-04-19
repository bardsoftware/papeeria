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

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class OaiConfigurationTest {
	private static OaiConfiguration configuration = null;

	@BeforeClass
	public static void setUp() throws Exception {
		configuration = OaiConfiguration.getInstance();
	}

	@Test
	public void testGetOaiConfiguration() throws Exception {
		assertNotNull("configuration is null", configuration);
	}

	@Test
	public void testGetUrl() throws Exception {
		assertNotNull("url is null", configuration.getUrl());
	}

	@Test
	public void testGetPath() throws Exception {
		assertNotNull("path is null", configuration.getPath());
	}

	@Test
	public void testGetSource() throws Exception {
		assertNotNull("source is null", configuration.getSource());
	}

	@Test
	public void testGetMetadata() throws Exception {
		assertNotNull("metadata is null", configuration.getMetadata());
	}

	@Test
	public void testGetMetadataPrefix() throws Exception {
		assertNotNull("metadata is null", configuration.getMetadata());
		assertNotNull("metadata prefix is null", configuration.getMetadata().getPrefix());
	}

	@Test
	public void testGetMetadataClass() throws Exception {
		assertNotNull("metadata is null", configuration.getMetadata());
		assertNotNull("metadata class is null", configuration.getMetadata().getClazz());
	}

	@Test
	public void testGetRequestGap() throws Exception {
		assertNotNull("request gap is null", configuration.getRequestGap());
		assertFalse("request gap is 0", configuration.getRequestGap() == 0);
	}
}
