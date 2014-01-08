package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.widget.FIBCheckboxListWidget;
import org.openflexo.localization.FlexoLocalization;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBCheckboxList widget
 * 
 * @author sylvain
 * 
 */
public class FIBCheckboxListWidgetTest extends FIBTestCase {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel checkboxListLabel1;
	private static FIBCheckboxList checkboxList1;
	private static FIBLabel checkboxListLabel2;
	private static FIBCheckboxList checkboxList2;
	private static FIBLabel checkboxListLabel3;
	private static FIBCheckboxList checkboxList3;
	private static FIBLabel checkboxListLabel4;
	private static FIBCheckboxList checkboxList4;
	private static FIBLabel checkboxListLabel5;
	private static FIBCheckboxList checkboxList5;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	public void test1CreateComponent() {

		component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);

		checkboxListLabel1 = newFIBLabel("static_checkboxList_auto_select");
		component.addToSubComponents(checkboxListLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList1 = newFIBCheckboxList();
		checkboxList1.setStaticList("value1,value2,value3");
		checkboxList1.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		checkboxListLabel2 = newFIBLabel("static_checkboxList_no_auto_select");
		component.addToSubComponents(checkboxListLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList2 = newFIBCheckboxList();
		checkboxList2.setStaticList("value1,value2,value3");
		checkboxList2.setAutoSelectFirstRow(false);
		component.addToSubComponents(checkboxList2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		checkboxListLabel3 = newFIBLabel("dynamic_checkboxList");
		component.addToSubComponents(checkboxListLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList3 = newFIBCheckboxList();
		checkboxList3.setList(new DataBinding<List<?>>("data.children", checkboxList3, List.class, BindingDefinitionType.GET));
		checkboxList3.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(checkboxList3.getList().isValid());

		checkboxListLabel4 = newFIBLabel("dynamic_array");
		component.addToSubComponents(checkboxListLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList4 = newFIBCheckboxList();
		checkboxList4.setArray(new DataBinding<Object[]>("data.parents", checkboxList4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		checkboxList4.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(checkboxList4.getArray().isValid());

		checkboxListLabel5 = newFIBLabel("dynamic_checkboxList_bound_to_data");
		component.addToSubComponents(checkboxListLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList5 = newFIBCheckboxList();
		checkboxList5.setData(new DataBinding<List<Person>>("data.jackies", checkboxList5, List.class, BindingDefinitionType.GET_SET));
		checkboxList5.setList(new DataBinding<List<?>>("data.children", checkboxList5, List.class, BindingDefinitionType.GET));
		// checkboxList5.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(checkboxList5.getData().isValid());
		assertTrue(checkboxList5.getList().isValid());

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
		assertNotNull(controller.viewForComponent(checkboxListLabel1));
		assertNotNull(controller.viewForComponent(checkboxList1));
		assertNotNull(controller.viewForComponent(checkboxListLabel2));
		assertNotNull(controller.viewForComponent(checkboxList2));
		assertNotNull(controller.viewForComponent(checkboxListLabel3));
		assertNotNull(controller.viewForComponent(checkboxList3));
		assertNotNull(controller.viewForComponent(checkboxListLabel4));
		assertNotNull(controller.viewForComponent(checkboxList4));

		assertEquals(Collections.singletonList("value1"), controller.viewForComponent(checkboxList1).getData());
		assertEquals(null, controller.viewForComponent(checkboxList2).getData());
		assertEquals(Collections.singletonList(family.getChildren().get(0)), controller.viewForComponent(checkboxList3).getData());
		assertEquals(Collections.singletonList(family.getParents()[0]), controller.viewForComponent(checkboxList4).getData());
		assertEquals(family.getJackies(), controller.viewForComponent(checkboxList5).getData());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3ModifyValueInModel() {

		FIBCheckboxListWidget<?> w5 = (FIBCheckboxListWidget<?>) controller.viewForComponent(checkboxList5);

		assertEquals(false, w5.getCheckboxAtIndex(0).isSelected());
		assertEquals(false, w5.getCheckboxAtIndex(1).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(2).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(3).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(4).isSelected());

		System.out.println("Add to jackies: " + family.getChildren().firstElement());

		family.addToJackies(family.getChildren().firstElement());

		System.out.println("Jackies: " + family.getJackies());
		System.out.println("data: " + checkboxList5.getData());
		System.out.println("valid: " + checkboxList5.getData().isValid());

		assertEquals(true, w5.getCheckboxAtIndex(0).isSelected());
		assertEquals(false, w5.getCheckboxAtIndex(1).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(2).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(3).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(4).isSelected());

		assertEquals(family.getJackies(), controller.viewForComponent(checkboxList5).getData());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	public void test4ModifyValueInWidget() {
		FIBCheckboxListWidget<?> w5 = (FIBCheckboxListWidget<?>) controller.viewForComponent(checkboxList5);

		w5.getCheckboxAtIndex(1).doClick();

		// w5.getDynamicJComponent().setSelectedValue(family.getChildren().get(1), true);
		// w6.getDynamicJComponent().setSelectedValue(Gender.Male, true);

		// Assert jackies and children are same list (order is different)
		assertTrue(family.getChildren().containsAll(family.getJackies()));
		assertTrue(family.getJackies().containsAll(family.getChildren()));

		w5.getCheckboxAtIndex(1).doClick();
		assertEquals(4, family.getJackies().size());

		w5.getCheckboxAtIndex(0).doClick();
		assertEquals(3, family.getJackies().size());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test5ModifyListValueInModel() {

		FIBCheckboxListWidget<?> w5 = (FIBCheckboxListWidget<?>) controller.viewForComponent(checkboxList5);

		assertEquals(5, w5.getMultipleValueModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getMultipleValueModel().getSize());

		family.addToJackies(junior);

		assertEquals(false, w5.getCheckboxAtIndex(0).isSelected());
		assertEquals(false, w5.getCheckboxAtIndex(1).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(2).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(3).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(4).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(5).isSelected());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBCheckboxListWidgetTest.class.getSimpleName());
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
