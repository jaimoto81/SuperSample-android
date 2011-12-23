package com.quickblox.supersamples.sdk.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import android.util.Log;

import com.quickblox.supersamples.sdk.definitions.ResponseBodyType;
import com.quickblox.supersamples.sdk.objects.XMLNode;

public class XMLParser extends DefaultHandler {
	/*
	 * Fields
	 */
	private ResponseBodyType responseBodyType;
	private Object []result;
	private XMLNode root;
	private ArrayList<XMLNode> stack;
	
	private static boolean beginTagParams = false;
	
	/*
	 * Methods (object)
	 */
	public Object [] parseXmlString(String xmlString) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
		
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlString));

		try {
			parser.parse(is, this);
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
	@Override
	public void startDocument() throws SAXException {

		stack = new ArrayList<XMLNode>();
		
		// set up the root node
		root = new XMLNode();
		root.setParent(null);
		root.setText(null);
		root.setAttributes(null);
		root.setName(null);
		root.setChildren(new ArrayList<XMLNode>());
		
		// put the root on the stack
		stack.add(root);
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		
		if (localName.equalsIgnoreCase("params"))
			beginTagParams = true;
		
		XMLNode lastNode = stack.get(stack.size()-1);
		
		// create a new node
		XMLNode node = new XMLNode();
		node.setParent(lastNode);
		node.setName(localName);
		if(atts == null){
			node.setAttributes(null);
		}else{
			Map<String, String> mapAtts = new HashMap<String, String>();
			for (int i = 0; i < atts.getLength(); i++) {
				mapAtts.put(atts.getLocalName(i),atts.getValue(i));
	        }
			node.setAttributes(mapAtts);
		}
		
		// add as children to parent
		List<XMLNode> children = lastNode.getChildren();
		if(children == null){
			children = new ArrayList<XMLNode>();
			lastNode.setChildren(children);
		}
		children.add(node);

		// put this new node on top of the stack
		stack.add(node);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		XMLNode lastNode = stack.get(stack.size()-1);
		
		if (lastNode.getText() == null || beginTagParams == false )
			lastNode.setText(new String(ch, start, length));
		else
			lastNode.setText(lastNode.getText() + new String(ch, start, length));		
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (localName.equalsIgnoreCase("params"))
			beginTagParams = false;
		
		if (stack.size() > 0) {
			XMLNode lastNode = stack.get(stack.size()-1);
			stack.remove(lastNode);
		}
	}

	@Override
	public void endDocument() {
		result = new Object[2];

		XMLNode firstNode = root.getChildren().get(0);
		
		if(firstNode.getName().equals("errors")){
			responseBodyType = ResponseBodyType.ResponseBodyTypeErrors;
		}else if(firstNode.getAttributes().get("type") != null && firstNode.getAttributes().get("type").equals("array")){
			responseBodyType = ResponseBodyType.ResponseBodyTypeArray;
		}else{
			responseBodyType = ResponseBodyType.ResponseBodyTypeSingle;
		}
		
		result[0] = responseBodyType;
		result[1] = firstNode;
		
		root = null;
		stack = null;
	}
}