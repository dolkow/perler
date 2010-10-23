package se.dolkow.imagefiltering.xml;

/*
	Copyright 2009 Snild Dolkow
	
	This file is part of Perler.
	
	Perler is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Perler is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Perler.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class XMLParser {
	
	private static XMLParser instance;
	
	protected final DocumentBuilder parser;
	
	private XMLParser() throws XMLParserException {
		parser = createXMLParser("/resource/chain.xsd"); //$NON-NLS-1$
	}
	
	public static XMLParser getInstance() throws XMLParserException {
		if (instance == null) {
			instance = new XMLParser();
		}
		return instance;
	}
	
	public synchronized Node parse(File f, String expectedRoot) throws SAXException, IOException, XMLParserException {
		return parse(f.toURI().toString(), expectedRoot);
	}
	
	public synchronized Node parse(File f) throws SAXException, IOException {
		return parse(f.toURI().toString());
	}
	
	public synchronized Node parse(String uri) throws SAXException, IOException {
		System.out.println(Messages.getFormatted("XMLParser.parsing_file", uri)); //$NON-NLS-1$
		Document dom = parser.parse(uri);
		return createTree(dom);
	}
	
	public synchronized Node parse(String uri, String expectedRoot) throws SAXException, IOException, XMLParserException {
		Node tree = parse(uri);
		if (!expectedRoot.equals(tree.getName())) {
			throw new XMLParserException("Root node isn't <" + expectedRoot + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return tree;
	}
	
	private Node createTree(Document dom) {
		System.out.println("Building internal tree representation..."); //$NON-NLS-1$
		org.w3c.dom.Node root = dom.getDocumentElement();
		removeWhitespaceNodes(root);
		return createTreeNode(root);
	}
	
	private void removeWhitespaceNodes(org.w3c.dom.Node from) {
		if (from == null) {
			return;
		}
		
		org.w3c.dom.Node tmp;
		org.w3c.dom.Node n = from.getFirstChild();
		while(n != null) {
			if (n.getNodeType() == org.w3c.dom.Node.TEXT_NODE && n.getTextContent().trim().length() == 0) {
				tmp = n;
				n = n.getNextSibling();
				from.removeChild(tmp);
			} else {
				removeWhitespaceNodes(n);
				n = n.getNextSibling();
			}
		}
	}
	
	private Node createTreeNode(org.w3c.dom.Node node) {
		org.w3c.dom.Node n = node.getFirstChild();
		if (n == null) {
			return new Element(node.getNodeName());
		} else if (n.getNextSibling() == null && n instanceof Text) {
			return new Leaf(node.getNodeName(), n.getTextContent().trim());
		} else {
			Element elem = new Element(node.getNodeName());
			while (n != null) {
				elem.add(createTreeNode(n));
				n = n.getNextSibling();
			}
			return elem;
		}
	}
	
	private DocumentBuilder createXMLParser(String schemaResourcePath) throws XMLParserException {
		Schema schema;
		try {
			SchemaFactory sfac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			URL schemaURL = XMLParser.class.getResource(schemaResourcePath);
			if (schemaURL == null) {
				throw new XMLParserException("Couldn't load schema resource " + schemaResourcePath); //$NON-NLS-1$
			}
			schema = sfac.newSchema(schemaURL);
		} catch (SAXException e) {
			throw new XMLParserException("Error when parsing schema: \n" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
		
		DocumentBuilder xmlparser;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setValidating(false);
			dbf.setSchema(schema);
			xmlparser = dbf.newDocumentBuilder();
			xmlparser.setErrorHandler(new DTDErrorHandler());
			return xmlparser;
		} catch (ParserConfigurationException e) {
			throw new XMLParserException("XML parser configuration error:\n" + e.getLocalizedMessage() + "\n" + "This isn't your fault... :/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	private static class DTDErrorHandler implements ErrorHandler {
		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}
		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}
		public void warning(SAXParseException exception) throws SAXException {
			System.out.println(Messages.get("XMLParser.warning") + ": " + exception.getLocalizedMessage());  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
}
