package com.bhle.access.download.offline;

import java.net.URI;

import org.akubraproject.map.IdMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bhle.access.download.storage.FileStorageIdMapper;

public class OfflineTest {
	@Test
	public void testOfflineProductIdMapper(){
		IdMapper mapper = new FileStorageIdMapper();
		URI externalId = URI.create("mailto:bhletech@bhle.com/a0hhmgs3_122344.jpg");
		URI internalId = mapper.getInternalId(externalId);
		Assert.assertEquals("file:bhletech%40bhle.com/a0hhmgs3_122344.jpg", internalId.toString());
	}
}
