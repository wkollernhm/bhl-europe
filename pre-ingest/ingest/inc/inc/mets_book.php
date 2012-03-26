<?php

// PARSE + EDIT BOOK XML

// INCOMING: $curElement, $nodeAttributes, $nodeValue




// ATTRIBUTE DES AKTUELLEN NODES BEARBEITEN
foreach ($nodeAttributes as $nodeAttribute)
{
    if ($nodeName=='METS:mets')
    {
        if ($nodeAttribute->name == 'OBJID')
            $curElement->setAttribute('OBJID',$cleanObjID);
    }
    
    // rdf:Description --> rdf:about="info:fedora/bhle:a0hhmgs0"
    if ($nodeName=='rdf:Description')
    {
        if ($nodeAttribute->name == 'about')
        $curElement->setAttribute('rdf:about','info:fedora/'.$cleanObjID);
    }
}



// VALUES DES AKTUELLEN NODES BEARBEITEN

if ($nodeName=='dc:identifier')
{
    $curElement->nodeValue = $cleanObjID;
}


// ADD OLEF
if ($nodeValue=='*olefdata')
{
    /*
    <METS:amdSec ID="OLEF">
    <METS:techMD ID="OLEF.0">
    <METS:mdWrap MIMETYPE="text/xml" MDTYPE="OTHER" LABEL="OLEF Metadata">
    <METS:xmlData>
    <olef></olef>
    </METS:xmlData></METS:mdWrap>
    </METS:techMD>
    </METS:amdSec>
     
    <olef>
    <olef:olef xmlns:olef="http://www.bhl-europe.eu/bhl-schema/v0.3/">
    <olef:element>
    ......
    </olef:olef>
    </olef> 
     
    */
    if ($nodeName=='METS:xmlData')
    {
        $arrAway = array("<?xml version=\"1.0\" encoding=\"utf-8\"?>"," ","\n");   // "<olef>","</olef>",

        $olefXML = html_entity_decode(implode("\n",
                file_get_content_filtered(_OLEF_FILE, $arrAway, "", true)));
        
        // OLEF PLATZHALTER NODE IM TEMPLATE ENTFERNEN
        $removeNode = $docRoot->getElementsByTagName('olef')->item(0);
        if ($removeNode!=null)
        $oldnode    = $curElement->removeChild($removeNode);           // VOM PARENT WEG MUSS DAS CHILD GELOESCHT WERDEN
        
        $removeNode = $docRoot->getElementsByTagName('olef:olef')->item(0);
        if ($removeNode!=null)
        $oldnode    = $curElement->removeChild($removeNode);           // VOM PARENT WEG MUSS DAS CHILD GELOESCHT WERDEN

        // OLEF EINFUEGEN
        // $node    = $domDoc->createTextNode("olef","\n".$olefXML."\n");
        $node    = $domDoc->createTextNode($olefXML."\n");
        $newnode = $curElement->appendChild($node);
     }
}


?>
