<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
    <title>BHL Europe </title>
    <%@ page import="org.apache.commons.httpclient.*, org.apache.commons.httpclient.methods.*, java.io.IOException, javax.xml.parsers.*, org.w3c.dom.*" %>
    <% 
    	String fedoraPath = "http://localhost:8080/fedora";
        int pageCount = 0;
        String pid = request.getParameter("pid");
        String title = null;
        
        HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://localhost:8080/fedora/risearch");
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-model:hasModel> <fedora:islandora:pageCModel> "
				+ "and $object <fedora-rels-ext:isMemberOf> <fedora:" + pid + ">)";
		NameValuePair[] data = { new NameValuePair("type", "tuples"),
				new NameValuePair("format", "count"),
				new NameValuePair("lang", "iTQL"),
				new NameValuePair("query", query) };
		post.setRequestBody(data);
		try {
			client.executeMethod(post);
			String pageCountString = post.getResponseBodyAsString();
			pageCount = Integer.valueOf(pageCountString);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fedoraPath + "/objects/" + pid + "?format=xml");
			NodeList list = doc.getElementsByTagName("objLabel");
			title = list.item(0).getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String pageDimensionsArray = null;
		GetMethod get = new GetMethod(fedoraPath + "/objects/" + pid + "/datastreams/DIMENSIONS/content");
		try {
			client.executeMethod(get);
			if (get.getStatusCode() == 200)
				pageDimensionsArray = get.getResponseBodyAsString();
			else
				pageDimensionsArray = "[]";
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    %>
	<link rel="stylesheet" type="text/css" href="/bookreader/style/BookReader.css"/>
    
    <script type="text/javascript" src="http://www.archive.org/includes/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery-ui-1.8.5.custom.min.js"></script>

    <script type="text/javascript" src="http://www.archive.org/bookreader/dragscrollable.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery.colorbox-min.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery.ui.ipad.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery.bt.min.js"></script>

    <script type="text/javascript" src="/bookreader/script/BookReader.js"></script>
</head>
<body style="background-color: ##939598;">

<div id="BookReader">
    BHL Europe BookReader<br/>
    
    <noscript>
    <p>
        The BookReader requires JavaScript to be enabled. Please check that your browser supports JavaScript and that it is enabled in the browser settings.</a>.
    </p>
    </noscript>
</div>

<script type="text/javascript">
// Create the BookReader object
br = new BookReader();

//Extract variable pid from URL
br.pid = '<%= request.getParameter("pid") %>';

// Extract variable pid from URL
br.ui = '<%= request.getParameter("ui") %>';

br.pageDimensions = <%= pageDimensionsArray %>;

// Return the width of a given page.  Here we assume all images are 800 pixels wide
br.getPageWidth = function(index) {
	if (br.pageDimensions[index] != undefined){
		return parseInt(br.pageDimensions[index].split("x")[0]);
	} else {
		return 800;
	}
}

// Return the height of a given page.  Here we assume all images are 1200 pixels high
br.getPageHeight = function(index) {
	if (br.pageDimensions[index] != undefined){
		return parseInt(br.pageDimensions[index].split("x")[1]);
	} else {
		return 1200;
	}
}

br.getPageURI = function(index, reduce, rotate) {
 var leafStr = '0000';            
 var imgStr = br.getPageNum(index).toString();
 var re = new RegExp("0{"+imgStr.length+"}$");
 var url = '/fedora/objects/' + br.pid + '-' + leafStr.replace(re, imgStr) + '/methods/bhle-service:pageSdef/jpeg';
 return url;
}

br.getPageOCRURI = function(index) {
 var leafStr = '0000';            
 var indexStr = (index).toString();
 var re = new RegExp("0{"+indexStr.length+"}$");
 var url = '/fedora/objects/' + br.pid + '-' + leafStr.replace(re, indexStr) + '/datastreams/TEI/content';
 return url;
}

// Return which side, left or right, that a given page should be displayed on
br.getPageSide = function(index) {
    if (0 == (index & 0x1)) {
        return 'R';
    } else {
        return 'L';
    }
}

// This function returns the left and right indices for the user-visible
// spread that contains the given index.  The return values may be
// null if there is no facing page or the index is invalid.
br.getSpreadIndices = function(pindex) {   
    var spreadIndices = [null, null]; 
    if ('rl' == this.pageProgression) {
        // Right to Left
        if (this.getPageSide(pindex) == 'R') {
            spreadIndices[1] = pindex;
            spreadIndices[0] = pindex + 1;
        } else {
            // Given index was LHS
            spreadIndices[0] = pindex;
            spreadIndices[1] = pindex - 1;
        }
    } else {
        // Left to right
        if (this.getPageSide(pindex) == 'L') {
            spreadIndices[0] = pindex;
            spreadIndices[1] = pindex + 1;
        } else {
            // Given index was RHS
            spreadIndices[1] = pindex;
            spreadIndices[0] = pindex - 1;
        }
    }
    
    return spreadIndices;
}

// For a given "accessible page index" return the page number in the book.
//
// For example, index 5 might correspond to "Page 1" if there is front matter such
// as a title page and table of contents.
br.getPageNum = function(index) {
    return index + 1;
}

// Total number of leafs
br.numLeafs = <%= pageCount %>;

//Book title and the URL used for the book title link
br.bookTitle= "<%= title %>";
br.bookUrl  = '/fedora/objects/' + br.pid + '/methods/bhle-service:bookSdef/bookreader?ui=';

// Override the path used to find UI images
br.imagesBaseURL = '/bookreader/images/';

br.getEmbedCode = function(frameWidth, frameHeight, viewParams) {
    return "Embed code not supported in bookreader.";
}

// Let's go!
br.init();

// read-aloud and search need backend compenents and are not supported in the demo
$('#BRtoolbar').find('.read').hide();
$('#BRtoolbar').find('.info').hide();
//$('#textSrch').hide();
//$('#btnSrch').hide();
</script>

</body>
</html>
