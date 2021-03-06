package com.bhle.access.download.batch;

import org.akubraproject.Blob;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.bhle.access.download.BasicDownloadRequest;
import com.bhle.access.download.ContentType;
import com.bhle.access.download.DownloadGateway;
import com.bhle.access.download.generator.PageURIExtractor;
import com.bhle.access.download.generator.PageURIExtractorImpl;
import com.bhle.access.storage.LowLevelStorage;
import com.bhle.access.util.FedoraURI;

public class FullOcrTasklet implements Tasklet {

	private String pid;

	public void setPid(String pid) {
		this.pid = pid;
	}

	private DownloadGateway downloadGateway;

	public void setDownloadGateway(DownloadGateway downloadGateway) {
		this.downloadGateway = downloadGateway;
	}

	private LowLevelStorage lowLevelStorage;

	public void setLowLevelStorage(LowLevelStorage lowLevelStorage) {
		this.lowLevelStorage = lowLevelStorage;
	}

	private static PageURIExtractor PID_EXTRACTOR = new PageURIExtractorImpl();

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		String guid = FedoraURI.getGuidFromPid(pid);

		BasicDownloadRequest request = new BasicDownloadRequest();
		Blob blob = lowLevelStorage.getBlob(FedoraURI.getPidFromGuid(guid),
				"FULL_OCR");
		request.setBlob(blob);
		request.setContentType(ContentType.OCR);
		String[] pageUris = PID_EXTRACTOR.getPageURIs(guid, "");
		request.setPageURIs(pageUris);
		downloadGateway.download(request).get();
		return RepeatStatus.FINISHED;
	}

}
