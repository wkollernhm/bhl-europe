Drupal.locale = { 'pluralFormula': function ($n) { return Number((((($n%10)==1)&&(($n%100)!=11))?(0):((((($n%10)>=2)&&(($n%10)<=4))&&((($n%100)<10)||(($n%100)>=20)))?(1):2))); }, 'strings': {"":{"An AJAX HTTP error occurred.":"Objevila se AJAX HTTP chyba.","HTTP Result Code: !status":"V\u00fdsledn\u00fd k\u00f3d HTTP je: !status","An AJAX HTTP request terminated abnormally.":"AJAX HTTP po\u017eadavek skon\u010dil neobvykle.","Debugging information follows.":"N\u00e1sleduj\u00ed informace pro lad\u011bn\u00ed.","Path: !uri":"Cesta: !uri","StatusText: !statusText":"Text stavu: !statusText","ResponseText: !responseText":"Text odpov\u011bdi:  !responseText","ReadyState: !readyState":"ReadyState: !readyState","(active tab)":"(aktivn\u00ed z\u00e1lo\u017eka)","Hide":"Skr\u00fdt","Show":"Uk\u00e1zat","Not restricted":"Nen\u00ed omezeno","Restricted to certain pages":"Omezen\u00fd na ur\u010dit\u00fd str\u00e1nky","Not customizable":"Nelze p\u0159izp\u016fsobit","The changes to these blocks will not be saved until the \u003Cem\u003ESave blocks\u003C\/em\u003E button is clicked.":"Zm\u011bny v t\u011bchto bloc\u00edch se neprojev\u00ed, dokud je neulo\u017e\u00edte pomoc\u00ed tla\u010d\u00edtka \u003Cem\u003EUlo\u017eit bloky\u003C\/em\u003E.","The block cannot be placed in this region.":"Blok nem\u016f\u017ee b\u00fdt um\u00edst\u011bn do tohoto regionu.","Re-order rows by numerical weight instead of dragging.":"Zm\u011b\u0148te po\u0159ad\u00ed \u0159\u00e1dk\u016f pomoc\u00ed \u010d\u00edseln\u00e9 v\u00e1hy m\u00edsto p\u0159eta\u017een\u00ed my\u0161\u00ed.","Show row weights":"Uka\u017e v\u00e1hy \u0159\u00e1dk\u016f","Hide row weights":"Skryj v\u00e1hy \u0159\u00e1dk\u016f","Drag to re-order":"Pro p\u0159eskupen\u00ed p\u0159et\u00e1hn\u011bte my\u0161\u00ed","Changes made in this table will not be saved until the form is submitted.":"Zm\u011bny proveden\u00e9 v t\u00e9to tabulce budou ulo\u017eeny a\u017e po odesl\u00e1n\u00ed  formul\u00e1\u0159e.","Edit":"Upravit","Select all rows in this table":"Ozna\u010dit v\u0161echny \u0159\u00e1dky v t\u00e9to tabulce","Deselect all rows in this table":"Zru\u0161it ozna\u010den\u00ed v\u0161ech \u0159\u00e1dek v t\u00e9to tabulce","Please wait...":"Pros\u00edm \u010dekejte...","Autocomplete popup":"Vyskakovac\u00ed okno automatick\u00e9ho dokon\u010dov\u00e1n\u00ed","Searching for matches...":"Hled\u00e1m shody...","Show more":"Zobrazit v\u00edce","Show fewer":"Zobrazit m\u00e9n\u011b","Expand":"Rozbalit","Show shortcuts":"Zobrazit zkratky","Hide shortcuts":"Skr\u00fdt zkratky","Close":"Zav\u0159\u00edt","Insert this token into your form":"Vlo\u017eit token do formul\u00e1\u0159e","First click a text field to insert your tokens into.":"Nejprve klikn\u011bte do textov\u00e9ho pole, do kter\u00e9ho chcete tokeny vlo\u017eit.","Remove group":"Odstranit skupinu"}} };