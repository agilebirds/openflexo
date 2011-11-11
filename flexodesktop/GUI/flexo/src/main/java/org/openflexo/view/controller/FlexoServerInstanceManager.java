package org.openflexo.view.controller;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jdom.JDOMException;
import org.openflexo.AdvancedPrefs;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.factory.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.xml.InvalidXMLDataException;
import org.openflexo.model.xml.XMLDeserializer;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.toolbox.FileUtils;

public class FlexoServerInstanceManager {

	private static final FlexoServerInstanceManager instance = new FlexoServerInstanceManager();

	private FlexoServerAddressBook addressBook;

	private ModelFactory factory;

	private FlexoServerInstanceManager() {
		factory = new ModelFactory();
		try {
			factory.importClass(FlexoServerAddressBook.class);
		} catch (ModelDefinitionException e) {
			// OK this sucks.
			e.printStackTrace();
		}
	}

	public static FlexoServerInstanceManager getInstance() {
		return instance;
	}

	public List<FlexoServerInstance> getInstances() {
		return getAddressBook().getInstances();
	}

	public FlexoServerInstance getOtherInstance() {
		FlexoServerInstance other = factory.newInstance(FlexoServerInstance.class);
		other.setID(FlexoServerInstance.OTHER_ID);
		other.setName("Other");
		other.setURL(FlexoLocalization.localizedForKey("manual_entry"));
		other.setWSURL("");
		return other;
	}

	public File getFlexoServerInstanceFile() {
		return new File(FlexoPreferences.getApplicationDataDirectory(), "flexoserverinstances.xml");
	}

	public static FlexoServerAddressBook getDefaultAddressBook() {
		ModelFactory factory = new ModelFactory();
		try {
			factory.importClass(FlexoServerAddressBook.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		FlexoServerAddressBook addressBook = factory.newInstance(FlexoServerAddressBook.class);
		FlexoServerInstance prod = factory.newInstance(FlexoServerInstance.class);
		prod.setID("prod");
		prod.setURL("https://server.openflexo.com/");
		prod.setWSURL("https://server.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
		prod.setName("Production server");
		FlexoServerInstance trial = factory.newInstance(FlexoServerInstance.class);
		trial.setID("trial");
		trial.setURL("https://trialserver.openflexo.com/");
		trial.setWSURL("https://trialserver.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
		trial.setName("Free trial server");
		FlexoServerInstance test = factory.newInstance(FlexoServerInstance.class);
		test.setID("test");
		test.setURL("https://test.openflexo.com/");
		test.setWSURL("https://test.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
		test.setName("Test server");
		test.addToUserTypes(UserType.DEVELOPER.getIdentifier());
		test.addToUserTypes(UserType.MAINTAINER.getIdentifier());
		addressBook.addToInstances(prod);
		addressBook.addToInstances(trial);
		addressBook.addToInstances(test);
		return addressBook;
	}

	public FlexoServerAddressBook getAddressBook() {
		if (addressBook == null) {
			File serverInstanceFile = getFlexoServerInstanceFile();
			long lastModified = 0;
			String fileContent = null;
			if (serverInstanceFile.exists()) {
				lastModified = serverInstanceFile.lastModified();
				try {
					fileContent = FileUtils.fileContents(serverInstanceFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				URL url = new URL(AdvancedPrefs.getFlexoServerInstanceURL());
				URLConnection c = url.openConnection();
				if (c instanceof HttpURLConnection) {
					HttpURLConnection connection = (HttpURLConnection) c;
					connection.setIfModifiedSince(lastModified);
					connection.connect();
					if (connection.getResponseCode() == 200) {
						fileContent = FileUtils.fileContents(connection.getInputStream(), "UTF-8");
						FileUtils.saveToFile(serverInstanceFile, fileContent);
					}
				} else {
					if (c.getDate() == 0 || c.getDate() > lastModified) {
						fileContent = FileUtils.fileContents(c.getInputStream(), "UTF-8");
						FileUtils.saveToFile(serverInstanceFile, fileContent);
					}
				}
			} catch (MalformedURLException e) {
				// Should never happen
				e.printStackTrace(); // just in case
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fileContent != null) {
				try {
					addressBook = (FlexoServerAddressBook) new XMLDeserializer(factory).deserializeDocument(fileContent);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidXMLDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ModelDefinitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (addressBook != null) {
				FlexoServerInstance other = getOtherInstance();
				addressBook.addToInstances(other);
				filterAddressBook(addressBook);
			} else {
				FlexoServerAddressBook defaultAddressBook = getDefaultAddressBook();
				FlexoServerInstance other = getOtherInstance();
				defaultAddressBook.addToInstances(other);
				filterAddressBook(addressBook);
				return defaultAddressBook;
			}
		}
		return addressBook;
	}

	private void filterAddressBook(FlexoServerAddressBook book) {
		if (ModuleLoader.getUserType() == null) {
			return;
		}
		for (FlexoServerInstance instance : new ArrayList<FlexoServerInstance>(book.getInstances())) {
			if (instance.getUserTypes().size() > 0) {
				boolean keepIt = false;
				for (String userType : instance.getUserTypes()) {
					UserType u = UserType.getUserTypeNamed(userType);
					if (ModuleLoader.getUserType().equals(u)) {
						keepIt = true;
						break;
					}
				}
				if (!keepIt) {
					book.removeFromInstances(instance);
				}
			}
		}
	}

}
