package org.openflexo.view.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;
import org.openflexo.AdvancedPrefs;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public class FlexoServerInstanceManager {

	private static final FlexoServerInstanceManager instance = new FlexoServerInstanceManager();

	private FlexoServerAddressBook addressBook;

	private ModelFactory factory;

	private FlexoServerInstanceManager() {
		try {
			factory = new ModelFactory(FlexoServerAddressBook.class);
		} catch (ModelDefinitionException e) {
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
		return new File(FileUtils.getApplicationDataDirectory(), "flexoserverinstances.xml");
	}

	public static FlexoServerAddressBook getDefaultAddressBook() {
		ModelFactory factory;
		try {
			factory = new ModelFactory(FlexoServerAddressBook.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new Error("FlexoServerAddressBook model is not properly configured", e);
		}
		FlexoServerAddressBook addressBook = factory.newInstance(FlexoServerAddressBook.class);
		FlexoServerInstance prod = factory.newInstance(FlexoServerInstance.class);
		prod.setID("prod");
		prod.setURL("https://server.openflexo.com/");
		prod.setRestURL("https://server.openflexo.com/Flexo/rest");
		prod.setWSURL("https://server.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
		prod.setName("Production server");
		FlexoServerInstance trial = factory.newInstance(FlexoServerInstance.class);
		trial.setID("trial");
		trial.setURL("https://trialserver.openflexo.com/");
		trial.setRestURL("https://trialserver.openflexo.com/Flexo/rest");
		trial.setWSURL("https://trialserver.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
		trial.setName("Free trial server");
		FlexoServerInstance test = factory.newInstance(FlexoServerInstance.class);
		test.setID("test");
		test.setRestURL("https://test.openflexo.com/");
		test.setURL("https://test.openflexo.com/Flexo/rest");
		test.setWSURL("https://test.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
		test.setName("Test server");
		// test.addToUserTypes(UserType.DEVELOPER.getIdentifier());
		// test.addToUserTypes(UserType.MAINTAINER.getIdentifier());
		addressBook.addToInstances(prod);
		addressBook.addToInstances(trial);
		addressBook.addToInstances(test);
		return addressBook;
	}

	public FlexoServerAddressBook getAddressBook() {
		if (addressBook == null) {
			synchronized (this) {
				if (addressBook == null) {
					URL url = null;
					try {
						url = new URL(AdvancedPrefs.getFlexoServerInstanceURL());
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
					File serverInstanceFile = getFlexoServerInstanceFile();
					String fileContent = FileUtils.createOrUpdateFileFromURL(url, serverInstanceFile);
					if (fileContent != null) {
						try {
							addressBook = (FlexoServerAddressBook) factory.deserialize(fileContent);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JDOMException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidDataException e) {
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
						filterAddressBook(defaultAddressBook);
						return defaultAddressBook;
					}
				}
			}
		}
		return addressBook;
	}

	private void filterAddressBook(FlexoServerAddressBook book) {
		for (FlexoServerInstance instance : new ArrayList<FlexoServerInstance>(book.getInstances())) {
			/*if (instance.getUserTypes().size() > 0) {
				boolean keepIt = false;
				for (String userType : instance.getUserTypes()) {
					UserType u = UserType.getUserTypeNamed(userType);
					if (UserType.getCurrentUserType().equals(u)) {
						keepIt = true;
						break;
					}
				}
				if (!keepIt) {
					book.removeFromInstances(instance);
				} else if (StringUtils.isEmpty(instance.getRestURL())) {
					if (instance.getURL() != null) {
						instance.setRestURL(instance.getURL() + "Flexo/rest");
					}
				}
			}*/
		}
	}

}
