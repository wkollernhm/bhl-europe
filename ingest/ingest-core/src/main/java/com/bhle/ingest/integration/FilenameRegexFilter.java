package com.bhle.ingest.integration;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilenameRegexFilter implements FilenameFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(FilenameRegexFilter.class);

	private String regex;

	public FilenameRegexFilter(String regex) {
		this.regex = regex;
	}

	@Override
	public boolean accept(File dir, String name) {
		boolean isValid = name.matches(regex);
		logger.info("Accept file {} ? {}",
				new String[] { name, String.valueOf(isValid) });
		return isValid;
	}

}
