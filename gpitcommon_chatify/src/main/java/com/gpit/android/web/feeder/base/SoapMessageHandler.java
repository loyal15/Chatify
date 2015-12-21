package com.gpit.android.web.feeder.base;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;

public class SoapMessageHandler {
	private final static String HTTP_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private final static String SOAP_HEADER = "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
			"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">";
	private final static String SOAP_BODY_START = "<soap12:Body>";
	private final static String SOAP_BODY_END = "</soap12:Body>";
	
	private final static String SOAP_ACTION_START = "<%s xmlns=\"%s\">";
	private final static String SOAP_ACTION_END = "</%s>";
	private final static String SOAP_ACTION_PARM_FORMAT = "<%s>%s</%s>";
	private final static String SOAP_FOOTER = "</soap12:Envelope>"; 
	
	private static StringBuffer soapMsg = new StringBuffer(1024); 
	public static synchronized String createSoapMessage(SoapQueueItem item) {
		String header, footer, body;
		String xmlns;
		String action;
		List<NameValuePair> pairs;
		
		header = item.header;
		footer = item.footer;
		body = item.body;
		xmlns = item.xmlns;
		action = item.method;
		pairs = item.postParms;
		
		soapMsg.setLength(0);
		
		// Add HTTP Header (1.0)
		soapMsg.append(HTTP_HEADER);
		
		if (item.isNativeMode) {
			if (header != null) {
				// Add SOAP Header
				soapMsg.append(header);
			}
			
			if (body != null) {
				// Add SOAP Body
				soapMsg.append(body);
			}
			
			if (footer != null) {
				// Add SOAP Footer
				soapMsg.append(footer);
			}
		} else {
			// Add SOAP Header
			soapMsg.append(SOAP_HEADER);
			
			// Add SOAP Body-Start Tag
			soapMsg.append(SOAP_BODY_START);
			
			// Add Action Part
			soapMsg.append(String.format(SOAP_ACTION_START, action, xmlns));
			
			// Add Action Parms
			if (pairs != null) {
				for (NameValuePair pair : pairs) {
					soapMsg.append(String.format(SOAP_ACTION_PARM_FORMAT, pair.getName(), pair.getValue(), pair.getName()));
				}
			}
			
			soapMsg.append(String.format(SOAP_ACTION_END, action));
			
			// Add SOAP Body-End Tag
			soapMsg.append(SOAP_BODY_END);
			
			// Add SOAP Footer
			soapMsg.append(SOAP_FOOTER);
		}
		
		Log.v("Soap", soapMsg.toString());
		
		return soapMsg.toString();
	}
}
