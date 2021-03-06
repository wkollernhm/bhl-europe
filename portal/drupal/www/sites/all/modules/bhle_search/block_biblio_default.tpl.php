<?php
/**
* @file
* Default theme implementation for displaying the biblio block of solr queries.
*
* Available variables:
*
*/

?>

<p id="search-step-back-link"><a href="/search/bhle" title="<?php print t('step back'); ?>"><?php print t('STEP BACK'); ?></a></p>

<div class="biblio-context-panel">

  <div class="collection-id">
    <p><label><?php print t('Collection'); ?></label> <a href="#" title="<?php print t('Collection'); ?>"> <?php print $collection; ?></a></p>
   <?php $str = arg(1); $pos = strpos($str, '-'); $pid = substr($str, $pos+1);?><p><label><?php print t('intern BHLE ID'); ?></label><?php print $pid; ?></p>
  </div>

  <div class="content-preview"><img src="<?php print $content_preview; ?>" alt="" /></div>

  <div class="biblio-read-links-block">
    <div class="biblio-read-years">

	    <?php foreach (array() as $nothing) : // dummy code ?>
	      <a href="#"></a>
      <?php endforeach; ?>

    </div>
<?php $str = arg(1); $pos = strpos($str, '-'); $pid = substr($str, $pos+1);?><div class="biblio-read-link"><a href="http://bhl-int.nhm.ac.uk/access/stream/<?php print $pid; ?>/<?php print $pid; ?>_mods.xml" " title="<?php print t('Read selected volume'); ?>"><?php print t('READ SELECTED VOLUME'); ?></a></div>
</div>
  <div class="biblio-action-block-wrapper">
    <div class="biblio-action-block">
      <div class="biblio-action-radios">
        <label for="biblio-action-download"><?php print t('Download'); ?></label><input type="radio" volue="" id ="biblio-actions-download" name="biblio-actions-download">
        <label for="biblio-action-basket"><?php print t('Add to basket'); ?></label><input type="radio" volue="" id ="biblio-actions-basket" name="biblio-actions-basket">
      </div>
      <div class="biblio-action-buttons">
        <a href="#" title="<?php print t('PDF'); ?>" class="biblio-action-pdf first"><?php print t('PDF'); ?></a>
        <a href="#" title="<?php print t('OCR'); ?>" class="biblio-action-ocr"><?php print t('OCR'); ?></a>
        <a href="#" title="<?php print t('JP2'); ?>" class="biblio-action-jp2 last"><?php print t('JP2'); ?></a>
      </div>
    </div>
  </div>

</div>
