package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test some data manipulation
 * 
 * @author sylvain
 * 
 */
public class DataManagingTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel firstNameLabel;
	private static FIBTextField firstNameTF;
	private static FIBLabel lastNameLabel;
	private static FIBTextField lastNameTF;
	private static FIBLabel fullNameLabel;
	private static FIBTextField fullNameTF;

	private static FIBController controller;
	private static Family family1;
	private static Family family2;
	private static Family family3;

	/**
	 * Create an initial component
	 */
	@Test
	public void test1CreateComponent() {

		component = new FIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);

		firstNameLabel = new FIBLabel("first_name");
		component.addToSubComponents(firstNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF = new FIBTextField();
		firstNameTF.setData(new DataBinding<String>("data.father.firstName", firstNameTF, String.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(firstNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(firstNameTF.getData().isValid());

		lastNameLabel = new FIBLabel("last_name");
		component.addToSubComponents(lastNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF = new FIBTextField();
		lastNameTF.setData(new DataBinding<String>("data.father.lastName", lastNameTF, String.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(lastNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(lastNameTF.getData().isValid());

		fullNameLabel = new FIBLabel("full_name");
		component.addToSubComponents(fullNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF = new FIBTextField();
		fullNameTF.setData(new DataBinding<String>("data.father.firstName + ' ' + data.father.lastName", fullNameTF, String.class,
				BindingDefinitionType.GET));
		component.addToSubComponents(fullNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(fullNameTF.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family1 = new Family();
		controller.setDataObject(family1);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(firstNameLabel));
		assertNotNull(controller.viewForComponent(firstNameTF));
		assertNotNull(controller.viewForComponent(lastNameLabel));
		assertNotNull(controller.viewForComponent(lastNameTF));
		assertNotNull(controller.viewForComponent(fullNameLabel));
		assertNotNull(controller.viewForComponent(fullNameTF));

		// controller.viewForComponent(firstNameTF).update();

		assertEquals("Robert", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Robert Smith", controller.viewForComponent(fullNameTF).getData());
	}

	/**
	 * Update data object, sets new data and then update controller <br>
	 * Check that widgets have well reacted
	 */
	@Test
	public void test3ModifyValueInModel() {

		family2 = new Family();
		family2.getFather().setFirstName("Roger");
		family2.getFather().setLastName("Rabbit");
		controller.setDataObject(family2);
		assertEquals("Roger", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Rabbit", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Roger Rabbit", controller.viewForComponent(fullNameTF).getData());
	}

	/**
	 * Update data object, update controller with new data and then modify new data <br>
	 * Check that widgets have well reacted
	 */
	@Test
	public void test4ModifyValueInModel() {

		family3 = new Family();

		controller.setDataObject(family3);

		family3.getFather().setFirstName("Jeannot");
		family3.getFather().setLastName("Lapin");
		assertEquals("Jeannot", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Lapin", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Jeannot Lapin", controller.viewForComponent(fullNameTF).getData());
	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(DataManagingTest.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}
