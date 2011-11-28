package com.quickblox.supersamples.sdk.objects;

import java.util.List;
import java.util.Map;

public class XMLNode extends Object{
	/*
	 * Fields
	 */
	private XMLNode parent;
	private String name;
	private Map<String, String> attributes;
	private String text;
	private List<XMLNode> children;
	
	/*
	 * Properties
	 */
	public XMLNode getParent() {
		return parent;
	}
	public void setParent(XMLNode parent) {
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public List<XMLNode> getChildren() {
		return children;
	}
	public void setChildren(List<XMLNode> children) {
		this.children = children;
	}
	
	@Override
	public String toString(){
		return  String.format("XMLNode: %s", this.name);
	}
	
	/*
	 * Methods
	 */
	public XMLNode findChild(String childName){
		for(XMLNode child : children) {
			
			//Log.i("child=", child.getName());
			
			if (child.getName().equals(childName)) {
				return child;
			}
		}
		
		return null;
	}
}
