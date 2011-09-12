package org.openflexo.inspector.model;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;

public class InspectorMapping extends XMLMapping {

	private static final Logger logger = FlexoLogger.getLogger(InspectorMapping.class.getPackage().getName());

	private static InspectorMapping instance;
	static {
		try {
			instance = new InspectorMapping();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private InspectorMapping() throws InvalidModelException, IOException, SAXException, ParserConfigurationException {
		super(new FileResource("Models/InspectorModel.xml"));
	}

	public static InspectorMapping getInstance() {
		return instance;
	}
}
