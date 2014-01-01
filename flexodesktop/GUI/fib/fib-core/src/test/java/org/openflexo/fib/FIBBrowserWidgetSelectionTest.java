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
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.model.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a browser widget
 * 
 * @author sylvain
 * 
 */
public class FIBBrowserWidgetSelectionTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsPanel;
	private static FIBBrowser browser;

	private static FIBLabel firstNameLabel;
	private static FIBTextField firstNameTF;
	private static FIBLabel lastNameLabel;
	private static FIBTextField lastNameTF;
	private static FIBLabel fullNameLabel;
	private static FIBTextField fullNameTF;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	public void test1CreateComponent() {

		component = new FIBPanel();
		component.setLayout(Layout.border);
		component.setDataClass(Family.class);

		browser = new FIBBrowser();
		browser.setName("browser");
		browser.setRoot(new DataBinding<Object>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setIteratorClass(Person.class);
		browser.setBoundToSelectionManager(true);
		browser.setManageDynamicModel(true);

		FIBBrowserElement rootElement = new FIBBrowserElement();
		rootElement.setName("family");
		rootElement.setDataClass(Family.class);
		rootElement.setLabel(new DataBinding<String>("\"My Family\"", browser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren parents = new FIBBrowserElementChildren();
		parents.setData(new DataBinding<Object>("family.parents", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(parents);
		FIBBrowserElementChildren children = new FIBBrowserElementChildren();
		parents.setData(new DataBinding<Object>("family.children", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(children);

		browser.addToElements(rootElement);

		FIBBrowserElement personElement = new FIBBrowserElement();
		personElement.setName("person");
		personElement.setDataClass(Person.class);
		personElement.setLabel(new DataBinding<String>("\"My relative: \"+person.toString", browser, String.class,
				BindingDefinitionType.GET));

		browser.addToElements(personElement);

		detailsPanel = new FIBPanel();
		detailsPanel.setLayout(Layout.twocols);

		firstNameLabel = new FIBLabel("first_name");
		detailsPanel.addToSubComponents(firstNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF = new FIBTextField();
		firstNameTF
				.setData(new DataBinding<String>("browser.selected.firstName", firstNameTF, String.class, BindingDefinitionType.GET_SET));
		detailsPanel.addToSubComponents(firstNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel = new FIBLabel("last_name");
		detailsPanel.addToSubComponents(lastNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF = new FIBTextField();
		lastNameTF.setData(new DataBinding<String>("browser.selected.lastName", lastNameTF, String.class, BindingDefinitionType.GET_SET));
		detailsPanel.addToSubComponents(lastNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel = new FIBLabel("full_name");
		detailsPanel.addToSubComponents(fullNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF = new FIBTextField();
		fullNameTF.setData(new DataBinding<String>("browser.selected.firstName + ' ' + browser.selected.lastName", fullNameTF,
				String.class, BindingDefinitionType.GET));
		detailsPanel.addToSubComponents(fullNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		component.addToSubComponents(browser, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(browser.getRoot().isValid());
		assertTrue(firstNameTF.getData().isValid());
		assertTrue(lastNameTF.getData().isValid());
		assertTrue(fullNameTF.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(browser));

		// FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);
		// assertEquals(family.getChildren(), w.getTableModel().getValues());
		// assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3SelectEditAndCheckValues() {

		FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);

		w.resetSelection();
		w.addToSelection(family.getBiggestChild());

		assertEquals("Jacky3", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Jacky3 Smith", controller.viewForComponent(fullNameTF).getData());

		family.getBiggestChild().setFirstName("Roger");
		family.getBiggestChild().setLastName("Rabbit");
		family.getBiggestChild().setAge(12);
		family.getBiggestChild().setGender(Gender.Female);

		assertEquals("Roger", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Rabbit", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Roger Rabbit", controller.viewForComponent(fullNameTF).getData());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3SelectMultipleValues() {

		FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);

		w.resetSelection();
		w.addToSelection(family.getChildren().get(1));
		w.addToSelection(family.getChildren().get(2));
		w.addToSelection(family.getChildren().get(3));

		assertEquals("Jacky2", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Jacky2 Smith", controller.viewForComponent(fullNameTF).getData());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBBrowserWidgetSelectionTest.class.getSimpleName());
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
