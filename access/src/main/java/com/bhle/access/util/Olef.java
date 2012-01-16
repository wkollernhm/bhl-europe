package com.bhle.access.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Olef {

	private Document doc;

	public Olef(URL url) throws IOException {
		this(url.openStream());
	}

	public Olef(InputStream inputStrem) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();

			this.doc = builder.parse(inputStrem);

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return (String) evaluateXpath("//mods:titleInfo[1]/mods:title/text()",
				XPathConstants.STRING);
	}

	public String getEntryPage() {
		return (String) evaluateXpath(
				"//reference[@type='title']/following-sibling::pages/page/name/text()",
				XPathConstants.STRING);
	}

	public String getPageName(int i) {
		// TODO need more spec
		return "Page " + (i + 1);
	}

	private Object evaluateXpath(String xpathExpression, QName returnType) {
		Object result = "";
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			xpath.setNamespaceContext(new ModsNamespaceContext());

			XPathExpression expr = xpath.compile(xpathExpression);
			Object obj = expr.evaluate(doc, returnType);
			result = obj;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}
}
