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
