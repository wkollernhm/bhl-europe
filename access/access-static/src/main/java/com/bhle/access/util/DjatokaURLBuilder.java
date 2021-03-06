package com.bhle.access.util;

import java.net.MalformedURLException;
import java.net.URL;

public class DjatokaURLBuilder {
	private static String BASE_URL;

	private static final String URL_VER = "url_ver";
	private static final String RFT_ID = "rft_id";
	private static final String SVC_ID = "svc_id";
	private static final String SVC_FORMAT = "svc.format";
	private static final String SVC_VAL_FMT = "svc_val_fmt";
	private static final String SVC_LEVEL = "svc.level";

	private static final String DEFAULT_RESOLUTION = "medium";

	public void setBaseUrl(String baseUrl) {
		BASE_URL = baseUrl;
	}

	public static URL build(URL referent) {
		Resolution resolution = new Resolution(DEFAULT_RESOLUTION);
		return build(referent, resolution.getLevel());
	}

	public static URL build(URL referent, int level) {
		URL url = null;
		try {
			url = new URL(BASE_URL + "resolver?" + getUrlVersion() + "&"
					+ getRefId(referent) + "&" + getSvcId() + "&"
					+ getSvcFormat() + "&" + getSvcValFmt() + "&"
					+ getSvcLevel(level));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return url;
	}

	private static String getUrlVersion() {
		return getParameter(URL_VER, "Z39.88-2004");
	}

	private static String getSvcLevel(int level) {
		return getParameter(SVC_LEVEL, String.valueOf(level));
	}

	private static String getSvcFormat() {
		return getParameter(SVC_FORMAT, "image/jpeg");
	}

	private static String getSvcId() {
		return getParameter(SVC_ID, "info:lanl-repo/svc/getRegion");
	}

	private static String getRefId(URL referent) {
		return getParameter(RFT_ID, referent.toString());
	}

	public static String getSvcValFmt() {
		return getParameter(SVC_VAL_FMT, "info:ofi/fmt:kev:mtx:jpeg2000");
	}

	private static String getParameter(String key, String value) {
		return key + "=" + value;
	}
}
