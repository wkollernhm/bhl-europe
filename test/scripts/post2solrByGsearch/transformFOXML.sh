#!/bin/bash -e

# XSL_POC options: saxon, xalan
#
# NOTE: gsearch comes bundeled with saxon9he which does not support
#       calling java functions by reflection from XSL stylesheets,
#       so only xalan can be used with the BHL-E XSL sheet.
#
XSL_POC="xalan"

JAVA=/usr/bin/java
FGSEARCH_BASE=/home/andreas/workspaces/bhle/opt/fedoragsearch

#
# load custom configuration from HOME/.bhl/post2solr.conf it this filed is present
#
if [ -f "$HOME/.bhl/post2solr.conf" ]
then
	. "$HOME/.bhl/post2solr.conf"
fi



if [ "$1" == "" -o "$2" == "" ]; then
	cat <<EOT
Usage: $0 <xsl-stylesheet> <foxml-file>

e.g. $0 ~/dev/opt/data-management/gsearch/foxmlToSolrDemo.xslt ~/dev/opt/test/foxml/test.xml


EOT
	exit 1
fi
if [ ! -e "$2" ]; then
	echo "$2 is not a file"
	exit 2
fi

echo "transforming foxml file  $2"

OUT_PRE=/tmp/post2solr.out-pre.xml
OUT=/tmp/post2solr.out.xml


IN=$2

#
# execute the script defined in $PRE_POCESSOR
#
if [ -f "$PRE_POCESSOR" ]
then
echo "pre processing $IN .."
	. $PRE_POCESSOR $IN > $OUT_PRE
else
	cp $IN $OUT_PRE
fi

echo "processing $IN ($OUT_PRE).."

case $XSL_POC in
	"xalan")
		XSL_POC_CLASSPATH="$FGSEARCH_BASE/WEB-INF/lib/serializer.jar:$FGSEARCH_BASE/WEB-INF/lib/xml-apis.jar:$FGSEARCH_BASE/WEB-INF/lib/xalan.jar:$FGSEARCH_BASE/WEB-INF/lib/xercesImpl.jar"
		XSL_POC_CMD="org.apache.xalan.xslt.Process -in $OUT_PRE -xsl $1 -out $OUT"
	;;
	"saxon")
		XSL_POC_CLASSPATH="$FGSEARCH_BASE/WEB-INF/lib/saxon9he.jar"
		XSL_POC_CMD="net.sf.saxon.Transform -s:$OUT_PRE -xsl:$1 -o:$OUT"
	;;
	*)
		echo "XSL prcessor $XSL_POC unsupported"
		exit 2
	;;
esac

$JAVA -classpath $FGSEARCH_BASE/WEB-INF/classes:$FGSEARCH_BASE/WEB-INF/lib/log4j-1.2.15.jar:$XSL_POC_CLASSPATH $XSL_POC_CMD

/bin/less $OUT

echo

