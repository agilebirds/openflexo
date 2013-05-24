package org.openflexo.foundation.ontology.xsd;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

public class XSOMUtils {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOMUtils.class.getPackage()
			.getName());

	public static XSSchemaSet read(File xsdFile) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Loading a XML Schema from file " + xsdFile.getName());
		}
		XSOMParser parser = new XSOMParser();
		parser.setErrorHandler(new ErrorHandler() {

			@Override
			public void error(SAXParseException exception) throws SAXException {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning(exception.getMessage());
				}
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning(exception.getMessage());
				}
			}

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning(exception.getMessage());
				}
			}

		});

		try {
			parser.parse(xsdFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSchemaSet result = null;
		try {
			result = parser.getResult();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return result;
	}

}
