package com.bhle.ingest;

import java.io.File;

import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;

import com.bhle.ingest.batch.IngestException;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;

public class FedoraServiceImpl {

	private FedoraClient client;

	public FedoraServiceImpl(FedoraClient client) {
		this.client = client;
	}

	@ServiceActivator
	public int ingestMETS(Message<File> message) throws IngestException {
		IngestResponse response = null;
		try {
			response = FedoraClient.ingest().content(message.getPayload())
					.format("info:fedora/fedora-system:METSFedoraExt-1.1")
					.execute(client);
		} catch (FedoraClientException e) {
			throw new IngestException(e);
		}
		return response.getStatus();
	}

	public int purge(String pid) {
		FedoraResponse response = null;
		try {
			response = FedoraClient.purgeObject(pid).execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return response.getStatus();
	}

	public void activate(String pid) {
		try {
			FedoraClient.modifyObject(pid).state("A").execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
	}
}
