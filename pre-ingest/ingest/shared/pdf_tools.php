<?php
// ***************************************
// ** FILE:    PDF.PHP                  **
// ** PURPOSE: TA ORACLE SYSTEM VIEW    **
// ** DATE:    25.10.2007               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ** AUTHOR:  WOLFGANG KOLLER          **
// ***************************************



// *****************************************************
function button_pdf($abs_file_name,$orientation="right")
// *****************************************************
{
    if ($orientation=="right") abs_r(10,0,10);

    icon(_SHARED_IMG."pdf.gif","Download PDF...","onClick=\"javascript: document.location='"._SHARED_URL."file_download.php?srcFile=".urlencode($abs_file_name)."';\"");

    if ($orientation=="right") abs_e();
}



// **************************************************
function getNumPagesInPDF(array $arguments = array()) 
// **************************************************
{
    @list($PDFPath) = $arguments;
    $output = array();
    $return_val = -1;
    
    // Execute PDF-Info to get details
    exec( _PDFINFO . " " . escapeshellarg($PDFPath), $output, $return_val );
    // Check if reading PDF-Info was successfull
    if( $return_val != 0 ) return false;
    
    // Find page info
    foreach( $output as $line ) {
        list($info, $value) = explode(':', $line);
        
        if( trim($info) == 'Pages' ) {
            return trim($value);
        }
    }
    
    return false;
}

/**
 * Cleanup name of PDF and remove all invalid characters
 * @param string $pdfName Name of PDF to clean up
 * @return string cleaned PDF name 
 */
function cleanPDFName( $pdfName ) {
    return str_replace(array('_', '-'), '', $pdfName);
}

function readPDFStructure( $pdfName ) {
    $output = array();
    $return_val = -1;
    $pdfStructure = array();
    
    // Prepare dump_data command
    $cmd = _PDFTK . ' ' . escapeshellarg($pdfName) . ' ' . _PDFTK_DATA;
    
    // Run dump_data
    exec($cmd, $output, $return_val);
    
    // Check if we got some output
    if( $return_val != 0 ) return false;
    
    // Now parse the content
    $currStructure = array();
    for( $j = 0; $j < count($output); $j++ ) {
        list($info, $value) = split($output[$j]);
        $info = trim($info);
        $value = trim($value);
        
        // Check if a new section is starting, or this is the last line
        if( $info == 'PageLabelNewIndex' || $j == (count($output) - 1) ) {
            // Check if we have a valid last entry
            if( isset($currStructure['PageLabelNumStyle']) ) {
                $pdfStructurePart = array();
                $pageCount = $value - $currStructure['PageLabelNewIndex'];
                switch($currStructure['PageLabelNumStyle']) {
                    case 'NoNumber':
                        $pdfStructurePart = array_fill($currStructure['PageLabelNewIndex'], $pageCount, $currStructure['PageLabelPrefix']);
                        break;
                    case 'LowercaseLetters':
                        break;
                    case 'UppercaseLetters':
                        break;
                    case 'LowercaseRomanNumerals':
                        break;
                    case 'UppercaseRomanNumerals':
                        break;
                    case 'DecimalArabicNumerals':
                        for( $i = 0; $i < $pageCount; $i++ ) {
                            $pdfStructurePart[$currStructure['PageLabelNewIndex'] + $i] = $currStructure['PageLabelStart'] + $i;
                        }
                        break;
                    default:
                        break;
                }
                
                // Finally merge the partial structure into the complete pdf-Structure
                $pdfStructure = array_merge($pdfStructure, $pdfStructurePart);
            }

            // Reset current structure information
            $currStructure = array();
        }
        
        // Remember info in current structure
        $currStructure[$info] = $value;
    }
    
    // Check if we were able to analyze the structure
    if( count($pdfStructure) <= 0 ) return false;
    
    // Return PDF-Structure
    return $pdfStructure;
}



// ***********************************
function php2pdf($url)
// ***********************************
{

 $tempDir = _SHARED."html2pdf/temp/";
 $success = false;

 


 // *******************************
 // 1. wget .php --> .html
 // *******************************
 
 @unlink($tempDir."temp.html");

 system("/usr/bin/wget --quiet --tries=1 --timeout=60 --output-document=".$tempDir."temp.html \"".$url."\"");


 // error
 if (!file_exists($tempDir."temp.html") )
	 return $success;




 // *******************************
 // 2. html2pdf    .html --> ps/pdf
 // *******************************
 require_once(_SHARED."html2pdf/HTML_ToPDF.php");


 // full path to the file to be converted
 $htmlFile = $tempDir.'temp.html';


 // the default domain for images that use a relative path
 // (you'll need to change the paths in the test.html page 
 // to an image on your server)
 $defaultDomain = 'www.mindcatch.at';


 // full path to the PDF we are creating
 $pdfFile = $tempDir.'temp.pdf';


 // remove old one, just to make sure we are making it afresh
 @unlink($pdfFile);

 
 // instnatiate the class with our variables
 // $pdf =& new HTML_ToPDF($htmlFile, $defaultDomain, $pdfFile);
 $pdf = new HTML_ToPDF($htmlFile, $defaultDomain, $pdfFile);

 // set headers/footers
 // $pdf->setHeader('color', 'blue');
 // $pdf->setFooter('left',  'Generated by HTML_ToPDF');
 // $pdf->setFooter('right', '$D');

 $result = $pdf->convert();				// returns filename of pdf in  basename($result) 

 // check if the result was an error
 if (PEAR::isError($result)) 
	{
     echo "<font color=red>".$result->getMessage()."</font>";
	 return $success;											// false
	}
 else 
	{
     $success = true; 
	}


 // echo "PDF file created successfully: $result";
 // echo "<br />Click <a href='"._SHARED_URL."html2pdf/temp/temp.pdf'>here</a> to view the PDF file.";

 return $success;

}

