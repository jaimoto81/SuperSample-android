package com.quickblox.supersamples.sdk.objects;

import org.apache.http.Header;

import com.quickblox.supersamples.sdk.definitions.ResponseBodyType;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;

public class RestResponse extends Object{
	private ResponseHttpStatus responseStatus;
	private Header []headers;
	private XMLNode body;
	private ResponseBodyType responseBodyType;
	
	
	public ResponseHttpStatus getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(ResponseHttpStatus responseStatus) {
		this.responseStatus = responseStatus;
	}
	
	public Header [] getHeaders() {
		return headers;
	}
	public void setHeaders(Header [] headers) {
		this.headers = headers;
	}
	
	public XMLNode getBody() {
		return body;
	}
	public void setBody(XMLNode parsedBody) {
		this.body = parsedBody;
	}
	
	public ResponseBodyType getResponseBodyType() {
		return responseBodyType;
	}
	public void setResponseBodyType(Object parserResult) {
		this.responseBodyType = (ResponseBodyType) parserResult;
	}
}
