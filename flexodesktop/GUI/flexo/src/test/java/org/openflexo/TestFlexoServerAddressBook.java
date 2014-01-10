package org.openflexo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Test;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.view.controller.FlexoServerAddressBook;
import org.openflexo.view.controller.FlexoServerInstance;

public class TestFlexoServerAddressBook {

	private static final String WS_URL = "http://www.mytest.openflexo.org/ws/coucou";
	private static final String URL = "http://www.mytest.openflexo.org";
	private static final String NAME = "My super name";
	private static final String MY_ID = "MyID";
	private static final String COUCOU2 = "Coucou2";
	private static final String COUCOU = "Coucou";

	@Test
	public void testAddressBookModel() {
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(FlexoServerAddressBook.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			Assert.fail("Model definition exception: " + e.getMessage());
		}
		FlexoServerAddressBook book = factory.newInstance(FlexoServerAddressBook.class);
		FlexoServerInstance instance = factory.newInstance(FlexoServerInstance.class);
		instance.addToUserTypes(COUCOU);
		instance.addToUserTypes(COUCOU2);
		instance.setID(MY_ID);
		instance.setName(NAME);
		instance.setURL(URL);
		instance.setWSURL(WS_URL);
		book.addToInstances(instance);
		Assert.assertNotNull(book.getInstanceWithID(MY_ID));
		Assert.assertTrue(instance == book.getInstanceWithID(MY_ID));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			factory.serialize(book, baos);
		} catch (Exception e) {
			// Not sure this can happen with a BAOS
			e.printStackTrace();
			Assert.fail("Serialization failed: " + e.getMessage());
		}
		try {
			book = (FlexoServerAddressBook) factory.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Serialization failed (IO): " + e.getMessage());
		} catch (JDOMException e) {
			e.printStackTrace();
			Assert.fail("Serialization failed (JDOM): " + e.getMessage());
		} catch (InvalidDataException e) {
			e.printStackTrace();
			Assert.fail("Serialization failed (invalid XML): " + e.getMessage());
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			Assert.fail("Serialization failed (ModelDefinition): " + e.getMessage());
		}
		Assert.assertNotNull(book);
		Assert.assertEquals(1, book.getInstances().size());
		FlexoServerInstance instanceWithID = book.getInstanceWithID(MY_ID);
		Assert.assertNotNull(instanceWithID);
		Assert.assertEquals(2, instanceWithID.getUserTypes().size());
		Assert.assertEquals(COUCOU, instanceWithID.getUserTypes().get(0));
		Assert.assertEquals(COUCOU2, instanceWithID.getUserTypes().get(1));
		Assert.assertEquals(WS_URL, instanceWithID.getWSURL());
		Assert.assertEquals(URL, instanceWithID.getURL());
		Assert.assertEquals(MY_ID, instanceWithID.getID());
		Assert.assertEquals(NAME, instanceWithID.getName());
	}
}
