package com.bardsoftware.papeeria.sufler.searcher;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SearcherConfigurationTest {
	private static SearcherConfiguration configuration = null;

	@BeforeClass
	public static void setUp() throws Exception {
		configuration = SearcherConfiguration.getInstance();
	}

	@Test
	public void testGetIndexerConfiguration() throws Exception {
		assertNotNull("configuration is null", configuration);
	}

	@Test
	public void testGetDirectories() throws Exception {
		assertNotNull("directories list is null", configuration.getIndexDirectory());

		assertNotNull("dir is null", configuration.getIndexDirectory());
	}
}
