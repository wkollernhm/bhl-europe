<?php

/**
 * Lowest-level wrappers for Fedora Commons's REST API functions. 
 * Functions return the HTTP response directly without parsing it.
 */

/************************************************************************
 * API-A
 ************************************************************************/
class FedoraAPI {

  public $connection;
  
  function __construct($connection = NULL) {
    if ($connection != NULL) {
      $this->connection = $connection;
    }
    else {
      // Construct a connection from Drupal's API settings.
      $url = variable_get('fedora_server_url', 'http://localhost:8080/fedora');
      $username = variable_get('fedora_user', 'fedoraAdmin');
      $password = variable_get('fedora_password', 'fedoraAdmin');
      $this->connection = new FedoraConnection($url, $username, $password);
    }
  }
  
  /**
   *
   * @param <type> $terms
   * @param <type> $query
   * @param <type> $maxResults
   * @param <type> $resultFormat
   * @param <type> $displayFields
   * @return StdClass HTTP Response object. 'data' has XML set of results
   * <?xml version="1.0" encoding="UTF-8"?>
   * <result xmlns="http://www.fedora.info/definitions/1/0/types/">
   *   <resultList>
   *   <objectFields>
   *       <pid>islandora:collectionCModel</pid>
   *       <title>Islandora Collection Content Model</title>
   *   </objectFields>
   *   <objectFields>
   *       <pid>islandora:strict_pdf</pid>
   *       <title>Strict PDF</title>
   *   </objectFields>
   *   </resultList>
   * </result>
   */
  public function findObjects($terms = '', $query = '', $maxResults = '', $resultFormat = 'xml', $displayFields = array('pid', 'title')) {
    $fedora_url = $this->connection->requestURL();
    $request = "$fedora_url/objects?";

    if (!empty($terms)) {
      $request .= "terms=" . drupal_encode_path($terms);
    }
    else if (!empty($query)) {
      $request .= "query=" . drupal_encode_path($query);
    }

    $request .= "&resultFormat=$resultFormat";

    foreach ($displayFields as $displayField) {
      $request .= "&$displayField=true";
    }
    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }
  
  public function getDatastream($pid, $dsID, $format = 'xml', $asOfDateTime = NULL, $validateChecksum = FALSE) {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL();
    $request = "$fedora_url/objects/$pid/datastreams/$dsID";
    $format = strtolower($format);
    $separator = '?';
    if (in_array($format, array('html', 'xml'))) {
      $request .= "{$separator}format=$format";
      $separator = '&';
    }
    if (!empty($asOfDateTime)) {
      $request .= "{$separator}asOfDateTime=$asOfDateTime";
      $separator = '&';
    }
    if (!empty($validateChecksum)) {
      $request .= "{$separator}validateChecksum=$validateChecksum";
    }
    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }
  
  /**
   *
   * @param String $pid persistent identifier of the digital object
   * @param String $dsID datastream identifier
   * @param String $asOfDateTime indicates that the result should be relative to the digital object as it existed at the given date and time
   * @param String $download If true,  a content-disposition header value "attachment" will be included in the response, prompting the user to save the datastream as a file.  A content-disposition header value "inline" will be used otherwise.  The filename used in the header is generated by examining in order: RELS-INT for the relationship fedora-model:downloadFilename, the datastream label, and the datastream ID.  The file extension (apart from where the filename is specified in RELS-INT) is determined from the MIMETYPE.  The order in which these filename sources are searched, and whether or not to generate an extension from the MIMETYPE, is configured in fedora.fcfg.  The file used to map between MIMETYPEs and extensions is mime-to-extensions.xml located in the server config directory.
   */
  public function getDatastreamDissemination($pid, $dsID, $asOfDateTime = NULL, $download = NULL) {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL(); 
    $request = "$fedora_url/objects/$pid/datastreams/$dsID/content";
    $request .= (!empty($asOfDateTime) ? "&asOfDateTime=$asOfDateTime" : '');
    if (!empty($download)) {
      $request .= (!empty($asOfDateTime) ? '&' : '?');
      $request .= "download=$download";
    }

    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }

  /**
   *
   * @param String $pid
   * @param String $sdefPid
   * @param String $method
   * @param String $methodParameters A key-value paired array of parameters
   * @return StdClass HTTP response object
   */
  public function getDissemination($pid, $sdefPid, $method, $methodParameters = array()) {
    $pid = drupal_encode_path($pid);
    $sdefPid = drupal_encode_path($sdefPid);

    $fedora_url = $this->connection->requestURL(); 
    $request = "$fedora_url/objects/$pid/methods/$sdefPid/$method?";

    //$request .= (!empty($asOfDateTime) ? "&asOfDateTime=$asOfDateTime" : '');
    foreach ($methodParameters as $param_name => $param_value) {
      $request .= $param_name . (!empty($param_value) ? "=$param_value&" : '&');
    }

    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }
  /**
   *
   * @param String $pid persistent identifier of the digital object
   * @param String $format the preferred output format (xml, html)
   * @return HTTP response object.
   * $response->data looks like:
   * <?xml version="1.0" encoding="utf-16"?>
   * <fedoraObjectHistory xsi:schemaLocation="http://www.fedora.info/definitions/1/0/access/ http://localhost:8080/fedoraObjectHistory.xsd" pid="demo:29" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   *     <objectChangeDate>2008-07-02T05:09:43.234Z</objectChangeDate>
   * </fedoraObjectHistory>
   */
  public function getObjectHistory($pid, $format = 'xml') {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL(); 

    $request = "$fedora_url/objects/$pid/versions?format=$format";
    $response = drupal_http_request($request);

    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }

  /**
   * Implements the getObjectProfile Fedora API-A method.
   * @param String $pid
   * @param String $format
   * @param String $asOfDateTime
   * @return StdClass HTTP Response object. 'data' has XML response string
   * Response is of the form:
   * <?xml version="1.0" encoding="utf-16"?>
   * <objectProfile xsi:schemaLocation="http://www.fedora.info/definitions/1/0/access/ http://localhost:8080/objectProfile.xsd" pid="islandora:demos" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   *     <objLabel>Islandora Demo Collections</objLabel>
   *     <objOwnerId>fedoraAdmin</objOwnerId>
   *     <objModels>
   *         <model>info:fedora/islandora:collectionCModel</model>
   *         <model>info:fedora/fedora-system:FedoraObject-3.0</model>
   *     </objModels>
   *     <objCreateDate>2009-03-10T07:09:53.333Z</objCreateDate>
   *     <objLastModDate>2010-03-20T23:39:58.490Z</objLastModDate>
   *     <objDissIndexViewURL>http://localhost:8080/fedora/get/islandora:demos/fedora-system:3/viewMethodIndex</objDissIndexViewURL>
   *     <objItemIndexViewURL>http://localhost:8080/fedora/get/islandora:demos/fedora-system:3/viewItemIndex</objItemIndexViewURL>
   *     <objState>A</objState>
   * </objectProfile>
   */
  public function getObjectProfile($pid, $format = 'xml', $asOfDateTime = '') {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL(); 

    $request = "$fedora_url/objects/$pid?format=$format";
    $request .= (!empty($asOfDateTime) ? "&asOfDateTime=$asOfDateTime" : '');
    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }

  /**
   *
   * @param <type> $pid
   * @param <type> $format
   * @param <type> $asOfDateTime
   * @return StdClass HTTP Response object. 'data' has XML Response
   * XML is of the form:
   * <?xml version="1.0" encoding="utf-16"?>
   * <objectDatastreams xsi:schemaLocation="http://www.fedora.info/definitions/1/0/access/ http://localhost:8080/listDatastreams.xsd" pid="islandora:demos" baseURL="http://localhost:8080/fedora/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   *     <datastream dsid="DC" label="Dublin Core Record for this object" mimeType="text/xml" />
   *     <datastream dsid="RELS-EXT" label="RDF Statements about this object" mimeType="application/rdf+xml" />
   *     <datastream dsid="COLLECTION_POLICY" label="Collection Policy" mimeType="text/xml" />
   *     <datastream dsid="TN" label="Thumbnail.png" mimeType="image/png" />
   * </objectDatastreams>
   */
  public function listDatastreams($pid, $format = 'xml', $asOfDateTime = '') {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL(); 

    $request = "$fedora_url/objects/$pid/datastreams?format=$format";
    $request .= (!empty($asOfDateTime) ? "&asOfDateTime=$asOfDateTime" : '');
    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }

  /**
   *
   * @param String $pid persistent identifier of the digital object
   * @param String $sdefPid persistent identifier of the SDef defining the methods
   * @param String $format the preferred output format
   * @param String $asOfDateTime indicates that the result should be relative to the digital object as it existed on the given date
   * @return StdClass HTTP Response object. 'data' has response XML
   * Response is of the form:
   * <?xml version="1.0" encoding="utf-16"?>
   * <objectMethods xsi:schemaLocation="http://www.fedora.info/definitions/1/0/access/ http://localhost:8080/listMethods.xsd" pid="islandora:demos" baseURL="http://localhost:8080/fedora/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   *     <sDef pid="fedora-system:3">
   *         <method name="viewObjectProfile"></method>
   *         <method name="viewMethodIndex"></method>
   *         <method name="viewItemIndex"></method>
   *         <method name="viewDublinCore"></method>
   *     </sDef>
   * </objectMethods>
   */
  public function listMethods($pid, $sdefPid = '', $format = 'xml', $asOfDateTime = '') {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL(); 

    $request = "$fedora_url/objects/$pid/methods";
    $request .= (!empty($sdefPid) ? "/$sdefPid" : '');
    $request .= "?format=$format";
    $request .= (!empty($asOfDateTime) ? "&asOfDateTime=$asOfDateTime" : '');
    $response = drupal_http_request($request);
    if (!empty($response->error)) {
      watchdog('fedora api', 'Error executing Fedora REST request %request: %error', array('%request' => $request, '%error' => $response->error), WATCHDOG_ERROR);
    }
    return $response;
  }

  /************************************************************************
   * API-A
   ************************************************************************/

  /**
   * @todo Please document this function.
   * @see http://drupal.org/node/1354
   */
  public function addDatastream($pid, $dsID, $file_path = NULL, $ds_string = NULL,
                         $params = array('controlGroup' => NULL, 'dsLocation' => NULL, 'altIDs' => NULL, 'dsLabel' => NULL,
                                                  'dsState' => NULL, 'formatURI' => NULL, 'checksumType' => NULL, 'checksum' => NULL,
                                                  'mimeType' => NULL, 'logMessage' => NULL)
          ) {
    $pid = drupal_encode_path($pid);
    
    foreach (array('dsLabel', 'formatURI', 'logMessage') as $string_param) {
      if (key_exists($string_param, $params) && $params[$string_param] != NULL) {
        $params[$string_param] = drupal_encode_path($params[$string_param]);
      }
    }
    
    $request_params = NULL;

    $headers = array(); //'Keep-Alive' => 115,
    // 'Connection' => 'keep-alive');
    if (!empty($params['dsLocation'])) {
      if ($params['controlGroup'] == 'X') {
        $headers['Content-Type'] = 'text/xml';
      }
      elseif (!empty($params['mimeType'])) {
        $headers['Content-Type'] = $params['mimeType'];
      }
      else {
        // TODO: More sophisticated MIME type detection. Possibly including retrieving headers of dsLocation
        $headers['Content-Type'] = 'application/x-octet-stream';
      }
    }
    elseif (!empty($file_path)) {
      // We must construct a multipart HTTP POST.
      $boundary = 'A0sFSD';
      $headers["Content-Type"] = "multipart/form-data; boundary=$boundary";
      $request_params = _fedora_api_multipart_encode($boundary, array('file' => $file_path));
    }
    elseif (!empty($ds_string)) {
      // We must construct a multipart HTTP POST.
      $boundary = 'A0sFSD';
      $headers["Content-Type"] = "text/plain";
      $request_params = $ds_string;
      //$request_params = _fedora_api_multipart_encode($boundary, array('string' => $ds_string));
    }
    else {
      return NULL;
    }

    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/$pid/datastreams/$dsID?";
	
    foreach ($params as $param_name => $param_value) {
      $request .= $param_value != NULL ? "$param_name=$param_value&" : '';
    }
    $response = drupal_http_request($request, array('headers' => $headers, 'method' => 'POST', 'data' => $request_params));
	
    return $response;
  }

  /**
   * Export a Fedora object with the given PID.
   *
   * @param String $pid
   * @param String $format
   *        One of: info:fedora/fedora-system:FOXML-1.1, info:fedora/fedora-system:FOXML-1.0, info:fedora/fedora-system:METSFedoraExt-1.1, info:fedora/fedora-system:METSFedoraExt-1.0, info:fedora/fedora-system:ATOM-1.1, info:fedora/fedora-system:ATOMZip-1.1
   * @param String $context
   *        One of: public, migrate, archive
   * @param String $encoding
   * @return StdClass HTTP Response object
   */
  public function export($pid, $format = 'info:fedora/fedora-system:FOXML-1.1', $context = 'public', $encoding = 'UTF-8') {
    $pid = drupal_encode_path($pid);
    $format = drupal_encode_path($format);

    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/$pid/export?";

    $request .= (!empty($format) ? "format=$format&" : '')
             . (!empty($context) ? "context=$context&" : '')
             . (!empty($encoding) ? "encoding=$encoding" : '');
    $response = drupal_http_request($request);
    return $response;
  }

  /**
   *
   * @param <type> $numPIDS
   * @param <type> $namespace
   * @param <type> $format
   * @return StdClass HTTP response
   * $response->data looks like this:
   * <?xml version="1.0" encoding="UTF-8"?>
   * <pidList  xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.fedora.info/definitions/1/0/management/ http://localhost:8080/getNextPIDInfo.xsd">
   *   <pid>changeme:2</pid>
   * </pidList>
   */
  public function getNextPID($numPIDS = 1, $namespace = '', $format = 'xml') {
    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/nextPID?";
    $request .= (!empty($numPIDS) ? "numPIDS=$numPIDS&" : '')
             . (!empty($namespace) ? "namespace=$namespace&" : '')
             . (!empty($format) ? "format=$format" : '');
    $response = drupal_http_request($request, array('headers' => array(), 'method' => 'POST'));
    return $response;
  }

  /**
   *
   * @param String $pid
   * @return StdClass HTTP response object.
   */
  public function getObjectXML($pid) {
    $pid = drupal_encode_path($pid);
    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/$pid/objectXML";
    $response = drupal_http_request($request);
    return $response;
  }

  /**
   *
   * @param String $foxml_file
   * @param String $foxml_string
   * @param String $pid
   * @param String $new
   * @param String $label
   * @param String $format
   * @param String $encoding
   * @param String $namespace
   * @param String $ownerId
   * @param String $logMessage
   * @param String $ignoreMime
   * @return StdClass HTTP Response Object
   * $response->data contains just the PID of the ingested object.
   *
   * Issue
   */
  public function ingest($foxml_file = NULL, $foxml_string = NULL, $pid = NULL, $new = TRUE, $label = NULL, $format = 'info:fedora/fedora-system:FOXML-1.1', $encoding = 'UTF-8', $namespace = NULL,
                  $ownerId = NULL, $logMessage = NULL, $ignoreMime = 'false') {
    if (empty($pid)) {
      // Set $new to TRUE regardless of what the user specified if no PID is given
      $pid = "new";
    }
    else {
      $pid = drupal_encode_path($pid);
    }
    $label = drupal_encode_path($label);
    $format = drupal_encode_path($format);
    $logMessage = drupal_encode_path($logMessage);
    $params = NULL;
    $headers = array(
      'Keep-Alive' => 115,
      'Connection' => 'keep-alive',
    );
    if (!empty($foxml_file)) {
      // We must construct a multipart HTTP POST.
      $boundary = 'A0sFSD';
      $headers["Content-Type"] = "multipart/form-data; boundary=$boundary";
      $params = _fedora_api_multipart_encode($boundary, array('foxml_file' => $foxml_file));
    }
    elseif (!empty($foxml_string)) {
      // We must construct a multipart HTTP POST.
      $boundary = 'A0sFSD';
      $headers["Content-Type"] = "multipart/form-data; boundary=$boundary";
      $params = _fedora_api_multipart_encode($boundary, array('foxml_string' => $foxml_string));
    }

    $fedora_url = $this->connection->requestURL(); 

    $request = $fedora_url . "/objects/$pid?";

    $request .= (!empty($label) ? "label=$label&" : '')
             . (!empty($format) ? "format=$format&" : '')
             . (!empty($encoding) ? "encoding=$encoding&" : '')
             . (!empty($namespace) ? "namespace=$namespace&" : '')
             . (!empty($ownerId) ? "ownerId=$ownerId&" : '')
             . (!empty($logMessage) ? "logMessage=$logMessage&" : '')
             . (!empty($ignoreMime) ? "ignoreMime=$ignoreMime&" : '');
    $response = drupal_http_request($request, array('headers' => $headers, 'method' => 'POST', 'data' => $params));
    return $response;

  }

  /**
   * Due ot a bug in Fedora you need to specify the Mime Type for each call.
   * @param <type> $pid
   * @param <type> $dsID
   * @param <type> $file_path
   * @param <type> $dsLocation
   * @param <type> $altIDs
   * @param <type> $dsLabel
   * @param <type> $versionable
   * @param <type> $dsState
   * @param <type> $formatURI
   * @param <type> $checksumType
   * @param <type> $checksum
   * @param <type> $mimeType
   * @param <type> $logMessage
   * @param <type> $force
   * @param <type> $ignoreContent
   * @return StdClass HTTP response object
   * $response->data XML looks like this:
   * <?xml version="1.0" encoding="utf-16"?>
   * <datastreamProfile xsi:schemaLocation="http://www.fedora.info/definitions/1/0/management/ http://localhost:8080/datastreamProfile.xsd" pid="islandora:demos" dsID="TN" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   *     <dsLabel>Thumbnail.png</dsLabel>
   *     <dsVersionID>TN.4</dsVersionID>
   *     <dsCreateDate>2010-04-08T12:27:01.966Z</dsCreateDate>
   *     <dsState>A</dsState>
   *     <dsMIME>image/png</dsMIME>
   *     <dsFormatURI/>
   *     <dsControlGroup>M</dsControlGroup>
   *     <dsSize>0</dsSize>
   *     <dsVersionable>true</dsVersionable>
   *     <dsInfoType/>
   *     <dsLocation>islandora:demos+TN+TN.4</dsLocation>
   *     <dsLocationType>INTERNAL_ID</dsLocationType>
   *     <dsChecksumType>DISABLED</dsChecksumType>
   *     <dsChecksum>none</dsChecksum>
   * </datastreamProfile>
   */
  public function modifyDatastream($pid, $dsID, $file_path = NULL, $ds_string = NULL,
                            $params = array('dsLocation' => NULL, 'altIDs' => NULL, 'dsLabel' => NULL, 'versionable' => NULL, 'dsState' => NULL, 'formatURI' => NULL,
                                'checksumType' => NULL, 'checksum' => NULL, 'mimeType' => NULL, 'logMessage' => NULL, 'force' => NULL, 'ignoreContent' => NULL))  {
    $pid = drupal_encode_path($pid);
      
    foreach (array('dsLabel', 'formatURI', 'logMessage') as $string_param) {
      if (key_exists($string_param, $params) && $params[$string_param] != NULL) {
        $params[$string_param] = drupal_encode_path($params[$string_param]);
      }
    }
    
    $request_params = NULL;
    $headers = array(
   //   'Keep-Alive' => 115,
   //   'Connection' => 'keep-alive',
    );

    $fedora_url = $this->connection->requestURL(); 

    $request = $fedora_url . "/objects/$pid/datastreams/$dsID?";
    
    foreach ($params as $param_name => $param_value) {
      $request .= $param_value != NULL ? "$param_name=$param_value&" : '';
    }
    
    $data = (!empty($file_path) ? file_get_contents($file_path) : $ds_string);
    $response = drupal_http_request($request, array('headers' => $headers, 'method' => 'PUT', 'data' => $data));
    return $response;
  }

  /**
   * @todo Please document this function.
   * @see http://drupal.org/node/1354
   */
  public function modifyObject($pid, $label = NULL, $ownerId = NULL, $state = NULL, $logMessage = NULL) {
    $pid = drupal_encode_path($pid);
    $label = drupal_encode_path($label);
    $logMessage = drupal_encode_path($logMessage);
    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/$pid?";
    $request .= (!empty($label) ? "label=$label&" : '')
             . (!empty($ownerId) ? "ownerId=$ownerId&" : '')
             . (!empty($state) ? "state=$state" : '')
             . (!empty($logMessage) ? "logMessage=$logMessage" : '');
    $response = drupal_http_request($request, array('headers' => array(), 'method' => 'PUT'));
    return $response;
  }

  /**
   *
   * @param String $pid persistent identifier of the digital object
   * @param String $dsID datastream identifier
   * @param String $startDT Format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ssZ - the (inclusive) start date-time stamp of the range. If not specified, this is taken to be the lowest possible value, and thus, the entire version history up to the endDT will be purged
   * @param String $endDT Format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ssZ - the (inclusive) ending date-time stamp of the range. If not specified, this is taken to be the greatest possible value, and thus, the entire version history back to the startDT will be purged
   * @param String $logMessage a message describing the activity being performed
   * @param String $force (true|false) force the update even if it would break a data contract
   * @return StdClass HTTP response object
   * On Success $response->code will be 204 and $response->error will be 'No content'.
   */
  public function purgeDatastream($pid, $dsID, $startDT = '', $endDT = '', $logMessage = '', $force = 'false') {
    $pid = drupal_encode_path($pid);
    $logMessage = drupal_encode_path($logMessage);
    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/$pid/datastreams/$dsID?";
    $request .= (!empty($startDT) ? "startDT=$startDT&" : '')
             . (!empty($endDT) ? "endDT=$endDT&" : '')
             . (!empty($logMessage) ? "logMessage=$logMessage" : '')
             . (!empty($force) ? "force=$force" : '');
    $response = drupal_http_request($request, array('headers' => array(), 'method' => 'DELETE'));
    return $response;
  }

  /**
   *
   * @param String $pid
   * @param String $logMessage
   * @param String $force (true|false)
   * @return StdClass HTTP Response object
   * $response->code is 204 on successful delete, $response->error is "No content".
   */
  public function purgeObject($pid, $logMessage = '', $force = 'false') {
    $pid = drupal_encode_path($pid);
    $logMessage = drupal_encode_path($logMessage);
    $fedora_url = $this->connection->requestURL(); 
    $request = $fedora_url . "/objects/$pid?";
    $request .= (!empty($logMessage) ? "logMessage=$logMessage" : '')
             . (!empty($force) ? "force=$force" : '');
    $response = drupal_http_request($request, array('headers' => array(), 'method' => 'DELETE'));
    return $response;
  }
}

/************************************************************************
 * Utility functions
 ************************************************************************/



function _fedora_api_multipart_encode($boundary, $params) {
  if (empty($params)) {
    return NULL;
  }

  $output = "";
  foreach ($params as $key => $value) {
    $output .= "--$boundary\r\n";
    if ($key == 'file') {
      $output .= _fedora_api_multipart_enc_file($value);
    }
    elseif ($key == 'foxml_file') {
      $output .= _fedora_api_multipart_enc_xml_file($value);
    }
    elseif ($key == 'foxml_string') {
      $output .= _fedora_api_multipart_enc_xml_string($value);
    }
    else {
      $output .= _fedora_api_multipart_enc_text($key, $value);
    }

  }
  $output .= "--$boundary--";
  return $output;
}

function _fedora_api_multipart_enc_file($path) {
  if (substr($path, 0, 1) == "@") {
    $path = substr($path, 1);
  }
  $filename = basename($path);
  $mimetype = "application/octet-stream";
  $data = "Content-Disposition: form-data; name=\"file\"; filename=\"$filename\"\r\n";
  $data .= "Content-Transfer-Encoding: binary\r\n";
  $data .= "Content-Type: $mimetype\r\n\r\n";
  $data .= file_get_contents($path) . "\r\n";
  return $data;
}

function _fedora_api_multipart_enc_xml_file($path) {
  if (substr($path, 0, 1) == "@") {
    $path = substr($path, 1);
  }
  $filename = basename($path);
  $mimetype = "text/xml";
  $data = "Content-Disposition: form-data; name=\"file\"; filename=\"$filename\"\r\n";
  $data .= "Content-Transfer-Encoding: UTF-8\r\n";
  $data .= "Content-Type: $mimetype\r\n\r\n";
  $data .= file_get_contents($path) . "\r\n";
  return $data;
}


function _fedora_api_multipart_enc_xml_string($xml_data) { 
  $mimetype = "text/xml";
  $data = "Content-Disposition: form-data; name=\"foxml\"; filename=\"FOXML\"\r\n";
  $data .= "Content-Transfer-Encoding: UTF-8\r\n";
  $data .= "Content-Type: $mimetype\r\n\r\n";
  $data .= $xml_data . "\r\n";
  return $data;
}

function _fedora_api_multipart_enc_text($name, $value) {
  $mimeType = 'text/plain';
  $data = "Content-Disposition: form-data: name=\"$name\" filename=\"$name\"\r\n";
  $data .= "Content-Transfer-Encoding: UTF-8\r\n";
  $data .= "Content-Type: $mimeType\r\n\r\n";
  $data .= "$value\r\n";
  return $data;
  return "Content-Disposition: form-data; name=\"$name\"\r\n\r\n$value\r\n";
}