package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.widget.FIBRadioButtonListWidget;
import org.openflexo.localization.FlexoLocalization;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBRadioButtonList widget
 * 
 * @author sylvain
 * 
 */
public class FIBRadioButtonListWidgetTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel radioButtonListLabel1;
	private static FIBRadioButtonList radioButtonList1;
	private static FIBLabel radioButtonListLabel2;
	private static FIBRadioButtonList radioButtonList2;
	private static FIBLabel radioButtonListLabel3;
	private static FIBRadioButtonList radioButtonList3;
	private static FIBLabel radioButtonListLabel4;
	private static FIBRadioButtonList radioButtonList4;
	private static FIBLabel radioButtonListLabel5;
	private static FIBRadioButtonList radioButtonList5;
	private static FIBLabel radioButtonListLabel6;
	private static FIBRadioButtonList radioButtonList6;
	private static FIBLabel radioButtonListLabel7;
	private static FIBRadioButtonList radioButtonList7;

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

		radioButtonListLabel1 = new FIBLabel("static_radioButtonList_auto_select");
		component.addToSubComponents(radioButtonListLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList1 = new FIBRadioButtonList();
		radioButtonList1.setStaticList("value1,value2,value3");
		radioButtonList1.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		radioButtonListLabel2 = new FIBLabel("static_radioButtonList_no_auto_select");
		component.addToSubComponents(radioButtonListLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList2 = new FIBRadioButtonList();
		radioButtonList2.setStaticList("value1,value2,value3");
		radioButtonList2.setAutoSelectFirstRow(false);
		component.addToSubComponents(radioButtonList2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		radioButtonListLabel3 = new FIBLabel("dynamic_radioButtonList");
		component.addToSubComponents(radioButtonListLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList3 = new FIBRadioButtonList();
		radioButtonList3.setList(new DataBinding<List<?>>("data.children", radioButtonList3, List.class, BindingDefinitionType.GET));
		radioButtonList3.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList3.getList().isValid());

		radioButtonListLabel4 = new FIBLabel("dynamic_array");
		component.addToSubComponents(radioButtonListLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList4 = new FIBRadioButtonList();
		radioButtonList4.setArray(new DataBinding<Object[]>("data.parents", radioButtonList4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		radioButtonList4.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList4.getArray().isValid());

		radioButtonListLabel5 = new FIBLabel("dynamic_radioButtonList_bound_to_data");
		component.addToSubComponents(radioButtonListLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList5 = new FIBRadioButtonList();
		radioButtonList5
				.setData(new DataBinding<Person>("data.biggestChild", radioButtonList5, Person.class, BindingDefinitionType.GET_SET));
		radioButtonList5.setList(new DataBinding<List<?>>("data.children", radioButtonList5, List.class, BindingDefinitionType.GET));
		radioButtonList5.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList5.getData().isValid());
		assertTrue(radioButtonList5.getList().isValid());

		radioButtonListLabel6 = new FIBLabel("enum");
		component.addToSubComponents(radioButtonListLabel6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList6 = new FIBRadioButtonList();
		radioButtonList6.setData(new DataBinding<Gender>("data.father.gender", radioButtonList6, Gender.class,
				BindingDefinitionType.GET_SET));
		radioButtonList6.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList6.getData().isValid());

		radioButtonListLabel7 = new FIBLabel("dynamic_radioButtonList_bound_to_selection");
		component.addToSubComponents(radioButtonListLabel7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList7 = new FIBRadioButtonList();
		radioButtonList7.setList(new DataBinding<List<?>>("data.children", radioButtonList7, List.class, BindingDefinitionType.GET));
		radioButtonList7.setAutoSelectFirstRow(true);
		// radioButtonList7.setBoundToSelectionManager(true);
		component.addToSubComponents(radioButtonList7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList7.getList().isValid());

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
		assertNotNull(controller.viewForComponent(radioButtonListLabel1));
		assertNotNull(controller.viewForComponent(radioButtonList1));
		assertNotNull(controller.viewForComponent(radioButtonListLabel2));
		assertNotNull(controller.viewForComponent(radioButtonList2));
		assertNotNull(controller.viewForComponent(radioButtonListLabel3));
		assertNotNull(controller.viewForComponent(radioButtonList3));
		assertNotNull(controller.viewForComponent(radioButtonListLabel4));
		assertNotNull(controller.viewForComponent(radioButtonList4));

		assertEquals("value1", (String) controller.viewForComponent(radioButtonList1).getData());
		assertEquals(null, controller.viewForComponent(radioButtonList2).getData());
		assertEquals(family.getChildren().get(0), controller.viewForComponent(radioButtonList3).getData());
		assertEquals(family.getParents()[0], controller.viewForComponent(radioButtonList4).getData());
		assertEquals(family.getJackies().get(2), controller.viewForComponent(radioButtonList5).getData());
		assertEquals(Gender.Male, controller.viewForComponent(radioButtonList6).getData());

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
		assertEquals(family.getChildren().get(0), controller.viewForComponent(radioButtonList5).getData());
		assertEquals(Gender.Female, controller.viewForComponent(radioButtonList6).getData());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	public void test4ModifyValueInWidget() {
		FIBRadioButtonListWidget<Person> w5 = (FIBRadioButtonListWidget<Person>) controller.viewForComponent(radioButtonList5);
		FIBRadioButtonListWidget<Gender> w6 = (FIBRadioButtonListWidget<Gender>) controller.viewForComponent(radioButtonList6);

		w5.setSelectedValue(family.getChildren().get(1));
		w6.setSelectedValue(Gender.Male);

		assertEquals(family.getChildren().get(1), family.getBiggestChild());
		assertEquals(Gender.Male, family.getFather().getGender());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test5ModifyListValueInModel() {

		FIBRadioButtonListWidget<?> w5 = (FIBRadioButtonListWidget<?>) controller.viewForComponent(radioButtonList5);

		assertEquals(5, w5.getMultipleValueModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getMultipleValueModel().getSize());

		// System.out.println("new children=" + family.getChildren());
		// System.out.println("List model = " + w5.getDynamicJComponent().getModel());

		family.setBiggestChild(junior);
		assertEquals(junior, controller.viewForComponent(radioButtonList5).getData());
		assertEquals(junior, w5.getSelectedValue());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBRadioButtonListWidgetTest.class.getSimpleName());
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
