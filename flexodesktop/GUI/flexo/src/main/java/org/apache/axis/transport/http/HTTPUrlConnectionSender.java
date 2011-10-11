/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.apache.axis.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.openflexo.logging.FlexoLogger;


/**
 * @author gunsnroz@hotmail.com
 */
public class HTTPUrlConnectionSender extends BasicHandler {

	private static final Logger logger = FlexoLogger.getLogger(HTTPUrlConnectionSender.class.getPackage().getName());

	@Override
	public void invoke(MessageContext context) throws AxisFault {
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("URLConnection sender invoked for "+context.getStrProp(MessageContext.TRANS_URL));
			}
			URL targetURL = new URL(context.getStrProp(MessageContext.TRANS_URL));
			HttpURLConnection connection = createHttpConnection(context, targetURL);
			prepareContext(context);
			send(context, connection);
			receive(context, connection);
		} catch (IOException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Message context is:");
				Iterator<?> i = context.getAllPropertyNames();
				while (i.hasNext()) {
					Object prop = i.next();
					logger.fine(prop+"="+context.getProperty(prop.toString()));
				}
			}
			throw AxisFault.makeFault(e);
		} catch (SOAPException e) {
			throw AxisFault.makeFault(e);
		}
	}

	private HttpURLConnection createHttpConnection(MessageContext context, URL targetURL) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)targetURL.openConnection();
		connection.setAllowUserInteraction(true);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		String contentType = context.getRequestMessage().getContentType(context.getSOAPConstants());
		connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, contentType);
		return connection;
	}

	private void prepareContext(MessageContext context) throws SOAPException {
		SOAPMessage message = context.getRequestMessage();

		// SOAPAction
		if (message.saveRequired()) {
			message.saveChanges();
		}
		String soapAction = context.useSOAPAction() ? context.getSOAPActionURI() : "";
		message.getMimeHeaders().setHeader("SOAPAction", "\"" + soapAction + "\"");

		// USERID, PASSWORD
		String username = context.getUsername();
		String password = context.getPassword();

		if (username != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Setting credentials for username "+username);
			}
			String credentials = username + ":" + password;
			credentials = Base64.encode(credentials.getBytes());
			message.getMimeHeaders().setHeader(HTTPConstants.HEADER_AUTHORIZATION, "Basic " + credentials);
		}
	}

	private void send(MessageContext context, HttpURLConnection connection) throws IOException, SOAPException {
		writeMimeHeaders(context, connection);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("About to connect to: '"+connection.getURL()+"'.");
		}
		OutputStream os = connection.getOutputStream();
		context.getRequestMessage().writeTo(os);
		os.flush();
		os.close();
	}

	private void writeMimeHeaders(MessageContext context, HttpURLConnection connection) throws IOException {
		SOAPMessage reqMessage = context.getRequestMessage();
		MimeHeader header = null;
		for (Iterator it = reqMessage.getMimeHeaders().getAllHeaders();
				it.hasNext(); ) {
			header = (MimeHeader)it.next();
			connection.setRequestProperty(header.getName(), header.getValue());
		}

		// don't forget the cookies
		if (context.getMaintainSession()) {
			String cookie = context.getStrProp(HTTPConstants.HEADER_COOKIE);
			String cookie2 = context.getStrProp(HTTPConstants.HEADER_COOKIE2);

			if (cookie != null) {
				connection.setRequestProperty(HTTPConstants.HEADER_COOKIE, cookie);
			}
			// GPO: not sure about cookie2 here under
			if (cookie2 != null) {
				connection.setRequestProperty(HTTPConstants.HEADER_COOKIE2, cookie2);
			}
		}
	}

	private void receive(MessageContext context, HttpURLConnection connection) throws IOException {
		connection.connect();
		boolean isSuccess = checkResponseStatusCode(context, connection);
		MimeHeaders headers = getMimeHeaders(connection);

		InputStream is = null;
		if (isSuccess) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}

		Message message = new Message(is, false, headers);
		message.setMessageType(Message.RESPONSE);
		context.setResponseMessage(message);
		// is.close();	// DO NOT close input stream
	}

	private boolean checkResponseStatusCode(MessageContext context, HttpURLConnection connection) throws IOException {
		boolean isSuccess = true;
		int statusCode = connection.getResponseCode();
		String statusMessage = connection.getResponseMessage();

		if (statusCode > 199 && statusCode <300) {
			// SOAP return OK. so fall through
			context.setProperty(HTTPConstants.MC_HTTP_STATUS_CODE, Integer.valueOf(statusCode));
			context.setProperty(HTTPConstants.MC_HTTP_STATUS_CODE, statusMessage);
		} else {
			if (statusCode >= 500) {
				// SOAP Fault should be in here - so fall through
				isSuccess = false;
			} else if (statusCode>299 && statusCode<400) {
				throw new AxisFault("redirect ("+statusCode+"): " + statusMessage+" Location: "+connection.getHeaderField("Location"));
			} else if (statusCode == 401) {
				throw new AxisFault("request requires HTTP authentication: " + statusMessage);
			} else if (statusCode == 404) {
				throw new AxisFault("HTTP Status-Code 404 : NOT Found " + statusMessage);
			} else if (statusCode == 407) {
				throw new AxisFault("HTTP Status-Code 407 : Authentication required " + statusMessage);
			} else {
				throw new AxisFault("HTTP Status-Code " + "(" + statusCode + ")" + statusMessage);
			}
		}

		return isSuccess;
	}

	private MimeHeaders getMimeHeaders(HttpURLConnection connection) {
		MimeHeaders headers = new MimeHeaders();
		int i = 1;
		String name = null;
		String value = null;
		while ((name = connection.getHeaderField(i)) != null) {
			value = connection.getHeaderField(i);
			headers.addHeader(name, value);

			i++;
		}
		return headers;
	}
}
