package com.bardsoftware.papeeria.sufler.indexer;

import org.junit.Test;

import java.io.IOException;

public class OaiDcIndexerTest {

	@Test
	public void testIndex() throws IOException {
		OaiDcIndexer indexer = new OaiDcIndexer();
		indexer.index();
	}
}
