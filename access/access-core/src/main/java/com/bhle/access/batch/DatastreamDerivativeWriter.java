package com.bhle.access.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.bhle.access.domain.Derivative;
import com.bhle.access.storage.StorageService;

public class DatastreamDerivativeWriter implements ItemWriter<Derivative[]> {

	private static StorageService store;
	
	public void setStore(StorageService store) {
		DatastreamDerivativeWriter.store = store;
	}

	public void write(List<? extends Derivative[]> items) throws Exception {
		for (Derivative[] derivatives : items) {
			for (Derivative derivative : derivatives) {
				store.updateDerivative(derivative);
			}
			
			for (Derivative derivative : derivatives) {
				derivative.close();
			}
		}

	}

}
