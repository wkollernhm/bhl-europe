package resource;

import util.GlobalParameter;
import util.PagePIDExtractor;
import util.SerialPagePIDExtractor;

public class FedoraObjectService {

	protected PagePIDExtractor pidExtracoteor = new SerialPagePIDExtractor();

	protected final static String PAGE_DEF = "demo:pageSdef";

	public static String getURLFromPID(String pid) {
		return GlobalParameter.BASE_URL + "/" + GlobalParameter.FEDORA
				+ "/objects/" + pid;
	}

	public static String getOCRURLFromPID(String pid) {
		return getURLFromPID(pid) + "/datastreams/TEI/content";
	}

	public static String getBoxURLFromPID(String pid) {
		return getURLFromPID(pid) + "/datastreams/BOX/content";
	}
	
	public static String getJPEGURLFromPID(String pid, int level) {
		return getURLFromPID(pid) + "/methods/demo:pageSdef/jpeg?level=" + level;
	}
}