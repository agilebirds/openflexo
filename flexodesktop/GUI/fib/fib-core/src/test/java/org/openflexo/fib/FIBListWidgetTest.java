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
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.view.widget.FIBListWidget;
import org.openflexo.localization.FlexoLocalization;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBList widget
 * 
 * @author sylvain
 * 
 */
public class FIBListWidgetTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel listLabel1;
	private static FIBList list1;
	private static FIBLabel listLabel2;
	private static FIBList list2;
	private static FIBLabel listLabel3;
	private static FIBList list3;
	private static FIBLabel listLabel4;
	private static FIBList list4;
	private static FIBLabel listLabel5;
	private static FIBList list5;
	private static FIBLabel listLabel6;
	private static FIBList list6;
	private static FIBLabel listLabel7;
	private static FIBList list7;

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

		listLabel1 = new FIBLabel("static_list_auto_select");
		component.addToSubComponents(listLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list1 = new FIBList();
		list1.setStaticList("value1,value2,value3");
		list1.setAutoSelectFirstRow(true);
		component.addToSubComponents(list1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		listLabel2 = new FIBLabel("static_list_no_auto_select");
		component.addToSubComponents(listLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list2 = new FIBList();
		list2.setStaticList("value1,value2,value3");
		list2.setAutoSelectFirstRow(false);
		component.addToSubComponents(list2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		listLabel3 = new FIBLabel("dynamic_list");
		component.addToSubComponents(listLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list3 = new FIBList();
		list3.setList(new DataBinding<List<?>>("data.children", list3, List.class, BindingDefinitionType.GET));
		list3.setAutoSelectFirstRow(true);
		component.addToSubComponents(list3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list3.getList().isValid());

		listLabel4 = new FIBLabel("dynamic_array");
		component.addToSubComponents(listLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list4 = new FIBList();
		list4.setArray(new DataBinding<Object[]>("data.parents", list4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		list4.setAutoSelectFirstRow(true);
		component.addToSubComponents(list4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list4.getArray().isValid());

		listLabel5 = new FIBLabel("dynamic_list_bound_to_data");
		component.addToSubComponents(listLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list5 = new FIBList();
		list5.setData(new DataBinding<Person>("data.biggestChild", list5, Person.class, BindingDefinitionType.GET_SET));
		list5.setList(new DataBinding<List<?>>("data.children", list5, List.class, BindingDefinitionType.GET));
		list5.setAutoSelectFirstRow(true);
		component.addToSubComponents(list5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list5.getData().isValid());
		assertTrue(list5.getList().isValid());

		listLabel6 = new FIBLabel("enum");
		component.addToSubComponents(listLabel6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list6 = new FIBList();
		list6.setData(new DataBinding<Gender>("data.father.gender", list6, Gender.class, BindingDefinitionType.GET_SET));
		list6.setAutoSelectFirstRow(true);
		component.addToSubComponents(list6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list6.getData().isValid());

		listLabel7 = new FIBLabel("dynamic_list_bound_to_selection");
		component.addToSubComponents(listLabel7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list7 = new FIBList();
		list7.setList(new DataBinding<List<?>>("data.children", list7, List.class, BindingDefinitionType.GET));
		list7.setAutoSelectFirstRow(true);
		list7.setBoundToSelectionManager(true);
		component.addToSubComponents(list7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list7.getList().isValid());

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
		assertNotNull(controller.viewForComponent(listLabel1));
		assertNotNull(controller.viewForComponent(list1));
		assertNotNull(controller.viewForComponent(listLabel2));
		assertNotNull(controller.viewForComponent(list2));
		assertNotNull(controller.viewForComponent(listLabel3));
		assertNotNull(controller.viewForComponent(list3));
		assertNotNull(controller.viewForComponent(listLabel4));
		assertNotNull(controller.viewForComponent(list4));

		assertEquals("value1", (String) controller.viewForComponent(list1).getData());
		assertEquals(null, controller.viewForComponent(list2).getData());
		assertEquals(family.getChildren().get(0), controller.viewForComponent(list3).getData());
		assertEquals(family.getParents()[0], controller.viewForComponent(list4).getData());
		assertEquals(family.getJackies().get(2), controller.viewForComponent(list5).getData());
		assertEquals(Gender.Male, controller.viewForComponent(list6).getData());

		// assertEquals("Robert", controller.viewForComponent(firstNameTF).getData());
		// assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		// assertEquals("Robert Smith", controller.viewForComponent(fullNameTF).getData());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3ModifyValueInModel() {

		family.setBiggestChild(family.getChildren().get(0));
		family.getFather().setGender(Gender.Female);
		assertEquals(family.getChildren().get(0), controller.viewForComponent(list5).getData());
		assertEquals(Gender.Female, controller.viewForComponent(list6).getData());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	public void test4ModifyValueInWidget() {
		FIBListWidget<?> w5 = (FIBListWidget<?>) controller.viewForComponent(list5);
		FIBListWidget<?> w6 = (FIBListWidget<?>) controller.viewForComponent(list6);

		w5.getDynamicJComponent().setSelectedValue(family.getChildren().get(1), true);
		w6.getDynamicJComponent().setSelectedValue(Gender.Male, true);

		assertEquals(family.getChildren().get(1), family.getBiggestChild());
		assertEquals(Gender.Male, family.getFather().getGender());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test5ModifyListValueInModel() {

		FIBListWidget<?> w5 = (FIBListWidget<?>) controller.viewForComponent(list5);

		assertEquals(5, w5.getDynamicJComponent().getModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getDynamicJComponent().getModel().getSize());

		// System.out.println("new children=" + family.getChildren());
		// System.out.println("List model = " + w5.getDynamicJComponent().getModel());

		family.setBiggestChild(junior);
		assertEquals(junior, controller.viewForComponent(list5).getData());
		assertEquals(junior, w5.getDynamicJComponent().getSelectedValue());

	}

	/**
	 * Try to select some objects, check that selection is in sync with it
	 */
	@Test
	public void test6PerfomSomeTestsWithSelection() {

		FIBListWidget<?> w7 = (FIBListWidget<?>) controller.viewForComponent(list7);
		assertEquals(6, w7.getDynamicJComponent().getModel().getSize());

		assertEquals(Collections.singletonList(family.getChildren().get(0)), w7.getSelection());

		// int[] indices = new int[3];
		// indices[0] = 1;
		// indices[1] = 2;
		// indices[2] = 4;
		// w7.getDynamicJComponent().setSelectedIndices(indices);
		w7.getDynamicJComponent().getSelectionModel().addSelectionInterval(1, 2);
		w7.getDynamicJComponent().getSelectionModel().addSelectionInterval(4, 4);

		List<Person> expectedSelection = new ArrayList<Person>();
		expectedSelection.add(family.getChildren().get(1));
		expectedSelection.add(family.getChildren().get(2));
		expectedSelection.add(family.getChildren().get(4));

		assertEquals(expectedSelection, w7.getSelection());

		controller.setFocusedWidget(w7);
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
