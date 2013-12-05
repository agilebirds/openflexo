package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.model.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test the structural and behavioural features of {@link FIBBrowserWidget} widget
 * 
 * @author sylvain
 * 
 */
public class FIBBrowserWidgetTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBBrowser browser;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	public void test1CreateComponent() {

		component = new FIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);

		browser = new FIBBrowser();
		browser.setRoot(new DataBinding<Object>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setBoundToSelectionManager(true);
		browser.setIteratorClass(Person.class);

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
		component.addToSubComponents(browser, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));

		assertTrue(browser.getRoot().isValid());

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

		FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);
		// assertEquals(family.getChildren(), w.getTableModel().getValues());
		// assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3ModifyValueInModel() {

		FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);

		BrowserCell root = (BrowserCell) w.getBrowserModel().getRoot();

		assertEquals(family, root.getUserObject());
		assertEquals(5, root.getChildCount());
		assertEquals(family.getChildren().get(0), ((BrowserCell) root.getChildAt(0)).getUserObject());
		assertEquals(family.getChildren().get(1), ((BrowserCell) root.getChildAt(1)).getUserObject());
		assertEquals(family.getChildren().get(2), ((BrowserCell) root.getChildAt(2)).getUserObject());
		assertEquals(family.getChildren().get(3), ((BrowserCell) root.getChildAt(3)).getUserObject());
		assertEquals(family.getChildren().get(4), ((BrowserCell) root.getChildAt(4)).getUserObject());

		Person junior = family.createChild();

		assertEquals(6, root.getChildCount());
		assertEquals(junior, ((BrowserCell) root.getChildAt(5)).getUserObject());
	}

	/**
	 * Try to select some objects, check that selection is in sync with it
	 */
	@Test
	public void test5PerfomSomeTestsWithSelection() {

		FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);

		w.resetSelection();
		w.addToSelection(family);

		// The selection is here empty because iterator class has been declared as Person, Family is not a Person, therefore the selection
		// is null
		assertEquals(Collections.emptyList()/*Collections.singletonList(family)*/, w.getSelection());

		w.resetSelection();
		w.addToSelection(family.getChildren().get(0));

		assertEquals(Collections.singletonList(family.getChildren().get(0)), w.getSelection());

		// int[] indices = new int[3];
		// indices[0] = 1;
		// indices[1] = 2;
		// indices[2] = 4;
		// w7.getDynamicJComponent().setSelectedIndices(indices);

		Person child1 = family.getChildren().get(1);
		Person child2 = family.getChildren().get(2);
		Person child4 = family.getChildren().get(4);

		w.resetSelection();
		w.addToSelection(child1);
		w.addToSelection(child2);
		w.addToSelection(child4);

		List<Person> expectedSelection = new ArrayList<Person>();
		expectedSelection.add(child1);
		expectedSelection.add(child2);
		expectedSelection.add(child4);

		assertEquals(expectedSelection, w.getSelection());

		controller.setFocusedWidget(w);
		assertEquals(expectedSelection, controller.getSelectionLeader().getSelection());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate();
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
