<?php
// ********************************************
// ** FILE:    CONTENT_LIST_BUTTONS.PHP      **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GUI WORKSTEPS

/* CONTENT_LAST_SUCC_STEP
 * 
 * 0 ... no step
 * 1 ... metadata generated
 * 2 ... page images (tiffs) ok
 * 3 ... ocr or pdf2text done
 * 4 ... taxonfinding done
 * 5 ... gid (?)
 * 6 ... format specs (?)
 * 7 ... 
 * 8 ... aip ready for ingest
 * 9 ... ingested
 * 
 * 10 .. checked
 * 
 */


// METADATA (1)
$command = "onClick=\"javascript: popup_win('gm','"._SYSTEM."?menu_nav=get_metadata&content_id=".$line[0]."',1000,500);\""; 

if ($line[10]==0)        icon("metadata.png","Prepare Metadata now...",$command); 
else                     icon("metadata0.png","Metadata already prepared.",$command); 


// -------- queueable methods start -----

// IMAGES (2)
$command = "onClick=\"javascript: popup_win('pi','"._SYSTEM."?menu_nav=get_images&content_id=".$line[0]."',1000,500);\"";

if ($line[10]==0) 
    icon("tif32_00.png","Prepare Metadata first!");
else if ($line[10]==1)    
    icon("tif32.png","Prepare Page Images now...",$command); 
else 
    icon("tif32_0.png","Images already prepared.",$command); 


// OCR (3)

if (!$isPDF)   // FALLS NICHT PDF
{
    $command = "onClick=\"javascript: popup_win('po','"._SYSTEM."?menu_nav=get_ocr&content_id=".$line[0]."',1000,500);\"";
    if ($line[10]<2)
        icon("ocr00.png",          "Prepare Metadata and Page Images first!");
    else if ($line[10]==2)    
        icon("ocr.png",            "Generate OCR for all Pages now...",$command); 
    else
        icon("ocr0.png",           "OCR already prepared.",$command); 
}
else           // PDF TO TEXT & TIF HERE
{
    $command = "onClick=\"javascript: popup_win('pp','"._SYSTEM."?menu_nav=get_ocr&content_id=".$line[0]."&pdf=1',1000,500);\"";
    if ($line[10]<2)    
        icon("pdf2tiff00.png",      "Prepare Metadata and Page Images first!");
    else if ($line[10]==2)
        icon("pdf2tiff.png",        "Prepare PDF Text now...",$command); 
    else
        icon("pdf2tiff0.png",       "PDF Text already prepared.",$command); 
}


// TAXON (4)
$command = "onClick=\"javascript: popup_win('tx','"._SYSTEM."?menu_nav=get_taxons&content_id=".$line[0]."',1000,500);\"";

if ($line[10]<3) 
    icon("taxon00.png",             "Prepare (OCR) Text first!");
else if ($line[10]==3)    
    icon("taxon.png",               "Prepare/Gather Taxonometric Information now...",$command); 
else 
    icon("taxon0.png",              "Taxonometric Information already present.",$command); 


// FORMAT SPECS (5) - MEDIA ATTRIBUTES



// METS INGEST CONTROL FILES

$command = "onClick=\"javascript: popup_win('in','"._SYSTEM."?menu_nav=get_mets&content_id=".$line[0]."',1000,500);\"";

if (!isset($endJS)) $endJS = "";


// CHECK INGEST STATUS OF CURRENT CONTENT VIA FILESYSTEM
if ((file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_FINISHED))||
    (file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_READY)))
{
    $line[10]=5;    // NUR TEMP. AUF 5 SETZEN
    
    echo "<div id=\"dialog_".$line[0]."\" title=\""._APP_NAME." - Ingest \">
        <br><ul>
        <li><u><a href='#' ".$command.">Regenerate ingest package (OLEF mods, page METS)...</a></u></li><br>
	<li><u><a href='"._FEDORA_ADMIN_GUI."' target=_blank>Show Ingest Log on Fedora...</a></u></li><br>
        <li><u><a href='"._SYSTEM."?menu_nav=ingest_list&sub_action=reset_ingest&content_id=".$line[0]."' target=_blank>Reset AIP to status \"not ingested\" and remove flag \"ready for ingest\"...</a></u><br>
        <font size=1>Enables you to re-prepare steps and/or re-ingest...</font></li><br>
        </ul>
        </div>";    

$endJS .= "

jQuery.noConflict();

(function($) 
{
    $( \"#dialog_".$line[0]."\" ).dialog({  autoOpen: false, width: 600, height: 320, draggable:true, modal: false,  buttons: { \"Close\": function() { $(this).dialog(\"close\"); } }   });
    
    $('#ingestButton_".$line[0]."').click(function() 
    {
        $( \"#dialog_".$line[0]."\" ).dialog('open');
    });

})(jQuery);

";
  
} // NUR FALLS INGEST FINISHED


if ($line[10]<4)
    icon("planning00.png",             "Media not ready for release!");
else if (($line[10]==4)&&(!file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_READY)))
    icon("planning.png",               "Check & Release Media now...",$command); 
else { 
      icon("planning0.png",
              "Media was already released and ingested see Fedora logs.",
              "onClick=\"\"","","",true,false,"ingestButton_".$line[0]);
     }


// DROP INGEST

lz(1);
icon("sep.png");
lz(1);

icon("failed_30.png",   "Drop Content from Content Management (not Filesystem). Drop if ingested correctly or to re-analyze/prepare.",
        "onClick=\"nachfrage('Drop selected Content Element from Management?','"._SYSTEM."?menu_nav=".$menu_nav."&sub_action=drop_content&content_id=".$line[0]."');\"");


// VISIBLE STATUS UPDATES (GUI ONLY)

if (file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_FINISHED))
{
    $endJS .= "
        
    document.forms.form_ingest_manager.content_status_".$line[0].".options[3].selected = true;

 ";

}
else if (file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_READY))
{
        $endJS .= "
        
    document.forms.form_ingest_manager.content_status_".$line[0].".options[2].selected = true;

 ";
}

?>
