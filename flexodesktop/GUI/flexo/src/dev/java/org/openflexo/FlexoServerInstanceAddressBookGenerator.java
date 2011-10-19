package org.openflexo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.openflexo.model.factory.ModelDefinitionException;
import org.openflexo.model.xml.XMLSerializer;
import org.openflexo.view.controller.FlexoServerInstanceManager;

public class FlexoServerInstanceAddressBookGenerator {

	public static void main(String[] args) throws ModelDefinitionException, FileNotFoundException {

		XMLSerializer serializer = new XMLSerializer();
		serializer.serializeDocument(FlexoServerInstanceManager.getDefaultAddressBook(), new FileOutputStream("flexoserverinstances.xml"));
	}
}
