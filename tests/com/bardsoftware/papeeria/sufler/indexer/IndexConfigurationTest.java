package com.bardsoftware.papeeria.sufler.indexer;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class IndexConfigurationTest {
	private static IndexerConfiguration configuration = null;

	@BeforeClass
	public static void setUp() throws Exception {
		configuration = IndexerConfiguration.getInstance();
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
