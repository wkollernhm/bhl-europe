<?php

// ********************************************
// ** FILE:    GET_TAXONS.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GET TAXONS VIA WEBSERVICE
// ********************************************
// 
// TEST-URL: http://localhost/index.php?menu_nav=get_taxons&contentDir=


echo "<h1 style='margin-top: 3px;'>Run Taxon Finder Service for pages text</h1>";


// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?
$arrTaxons = getContentFiles($contentDir, 'single_suffix', true,'.tax'); 
$nTaxons   = count($arrTaxons);
$nTextFiles= 0;


if ($nTaxons > 0)   echo "Taxon files present - nothing to do!\n";
else                include("inc/taxonfinder.php");



// NEU ZAEHLEN
$nTaxons  = count($arrTaxons);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTaxons > 0) 
{
    // IF SUCCESSFUL SET STATE TO 4
    if (getContentSteps($content_id)<4) setContentSteps($content_id, 4);
    
    $csvTextfiles = implode(_TRENNER, $arrTaxons);
    $csvTextfiles = str_replace(_CONTENT_ROOT, "", $csvTextfiles);
    mysql_select("update content set content_pages_taxon='" . $csvTextfiles . "' where content_id=" . $content_id);

    // $endmsg .= $nTaxons . " files generated and database updated successfully.";
    $endmsg .= "For ".$nTextFiles." text files ".$nTaxons." taxon files (.tax) generated and database updated successfully.";
} 
else if (!_QUEUE_MODE) echo _ERR . "Necessary files could not be prepared!";


?>
