package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.openflexo.fib.model.GridBagLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a browser widget, and where details panel are
 * multiple
 * 
 * @author sylvain
 * 
 */
public class FIBBrowserWidgetSelectionTest2 extends FIBTestCase {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsContainerPanel;
	private static FIBBrowser browser;

	private static FIBPanel detailsPanel1;
	private static FIBLabel label1;
	private static FIBLabel firstNameLabel1;
	private static FIBTextField firstNameTF1;
	private static FIBLabel lastNameLabel1;
	private static FIBTextField lastNameTF1;
	private static FIBLabel fullNameLabel1;
	private static FIBTextField fullNameTF1;

	private static FIBPanel detailsPanel2;
	private static FIBLabel label2;
	private static FIBLabel firstNameLabel2;
	private static FIBTextField firstNameTF2;
	private static FIBLabel lastNameLabel2;
	private static FIBTextField lastNameTF2;
	private static FIBLabel fullNameLabel2;
	private static FIBTextField fullNameTF2;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	public void test1CreateComponent() {

		component = newFIBPanel();
		component.setLayout(Layout.border);
		component.setDataClass(Family.class);

		browser = newFIBBrowser();
		browser.setName("browser");
		browser.setRoot(new DataBinding<Object>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setIteratorClass(Person.class);
		browser.setBoundToSelectionManager(true);
		browser.setManageDynamicModel(true);

		FIBBrowserElement rootElement = newFIBBrowserElement();
		rootElement.setName("family");
		rootElement.setDataClass(Family.class);
		rootElement.setLabel(new DataBinding<String>("\"My Family\"", browser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren parents = newFIBBrowserElementChildren();
		parents.setData(new DataBinding<Object>("family.parents", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(parents);
		FIBBrowserElementChildren children = newFIBBrowserElementChildren();
		parents.setData(new DataBinding<Object>("family.children", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(children);

		browser.addToElements(rootElement);

		FIBBrowserElement personElement = newFIBBrowserElement();
		personElement.setName("person");
		personElement.setDataClass(Person.class);
		personElement.setLabel(new DataBinding<String>("\"My relative: \"+person.toString", browser, String.class,
				BindingDefinitionType.GET));
		personElement.setEnabled(new DataBinding<Boolean>("!data.jackies.contains(person)", personElement, Boolean.class,
				BindingDefinitionType.GET));

		browser.addToElements(personElement);

		detailsPanel1 = newFIBPanel();
		detailsPanel1.setLayout(Layout.twocols);

		label1 = newFIBLabel("This detail panel represents a Jacky");
		detailsPanel1.addToSubComponents(label1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel1 = newFIBLabel("first_name");
		detailsPanel1.addToSubComponents(firstNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF1 = newFIBTextField();
		firstNameTF1.setData(new DataBinding<String>("browser.selected.firstName", firstNameTF1, String.class,
				BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(firstNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel1 = newFIBLabel("last_name");
		detailsPanel1.addToSubComponents(lastNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF1 = newFIBTextField();
		lastNameTF1.setData(new DataBinding<String>("browser.selected.lastName", lastNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(lastNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel1 = newFIBLabel("full_name");
		detailsPanel1.addToSubComponents(fullNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF1 = newFIBTextField();
		fullNameTF1.setData(new DataBinding<String>("browser.selected.firstName + ' ' + browser.selected.lastName", fullNameTF1,
				String.class, BindingDefinitionType.GET));
		detailsPanel1.addToSubComponents(fullNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel2 = newFIBPanel();
		detailsPanel2.setLayout(Layout.twocols);

		label2 = newFIBLabel("This detail panel represents a normal child");
		detailsPanel2.addToSubComponents(label2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel2 = newFIBLabel("first_name");
		detailsPanel2.addToSubComponents(firstNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF2 = newFIBTextField();
		firstNameTF2.setData(new DataBinding<String>("browser.selected.firstName", firstNameTF2, String.class,
				BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(firstNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel2 = newFIBLabel("last_name");
		detailsPanel2.addToSubComponents(lastNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF2 = newFIBTextField();
		lastNameTF2.setData(new DataBinding<String>("browser.selected.lastName", lastNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(lastNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel2 = newFIBLabel("full_name");
		detailsPanel2.addToSubComponents(fullNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF2 = newFIBTextField();
		fullNameTF2.setData(new DataBinding<String>("browser.selected.firstName + ' ' + browser.selected.lastName", fullNameTF2,
				String.class, BindingDefinitionType.GET));
		detailsPanel2.addToSubComponents(fullNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel1.setVisible(new DataBinding<Boolean>("data.jackies.contains(browser.selected)", detailsPanel1, Boolean.class,
				BindingDefinitionType.GET));
		detailsPanel2.setVisible(new DataBinding<Boolean>("!data.jackies.contains(browser.selected)", detailsPanel2, Boolean.class,
				BindingDefinitionType.GET));

		detailsContainerPanel = newFIBPanel();
		detailsContainerPanel.setLayout(Layout.gridbag);
		detailsContainerPanel.addToSubComponents(detailsPanel1, new GridBagLayoutConstraints());
		detailsContainerPanel.addToSubComponents(detailsPanel2, new GridBagLayoutConstraints());

		component.addToSubComponents(browser, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsContainerPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(browser.getRoot().isValid());
		assertTrue(firstNameTF1.getData().isValid());
		assertTrue(lastNameTF1.getData().isValid());
		assertTrue(fullNameTF1.getData().isValid());
		assertTrue(detailsPanel1.getVisible().isValid());
		assertTrue(firstNameTF2.getData().isValid());
		assertTrue(lastNameTF2.getData().isValid());
		assertTrue(fullNameTF2.getData().isValid());
		assertTrue(detailsPanel2.getVisible().isValid());

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
	 * Select some values, check that master/details scheme works
	 */
	@Test
	public void test3SelectSomeValues() {

		FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);
		FIBPanelView<?, ?> details1 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel1);
		FIBPanelView<?, ?> details2 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel2);

		w.resetSelection();
		w.addToSelection(family.getChildren().get(1));

		assertFalse(details1.isViewVisible());
		assertTrue(details2.isViewVisible());
		assertEquals("Suzy", controller.viewForComponent(firstNameTF1).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF1).getData());
		assertEquals("Suzy Smith", controller.viewForComponent(fullNameTF1).getData());

		w.resetSelection();
		w.addToSelection(family.getChildren().get(2));

		assertTrue(details1.isViewVisible());
		assertFalse(details2.isViewVisible());
		assertEquals("Jacky1", controller.viewForComponent(firstNameTF2).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF2).getData());
		assertEquals("Jacky1 Smith", controller.viewForComponent(fullNameTF2).getData());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBBrowserWidgetSelectionTest2.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}
