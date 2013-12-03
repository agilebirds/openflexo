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
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.view.widget.FIBDropDownWidget;
import org.openflexo.localization.FlexoLocalization;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBDropDown widget
 * 
 * @author sylvain
 * 
 */
public class FIBDropDownWidgetTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel dropDownLabel1;
	private static FIBDropDown dropDown1;
	private static FIBLabel dropDownLabel2;
	private static FIBDropDown dropDown2;
	private static FIBLabel dropDownLabel3;
	private static FIBDropDown dropDown3;
	private static FIBLabel dropDownLabel4;
	private static FIBDropDown dropDown4;
	private static FIBLabel dropDownLabel5;
	private static FIBDropDown dropDown5;
	private static FIBLabel dropDownLabel6;
	private static FIBDropDown dropDown6;

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

		dropDownLabel1 = new FIBLabel("static_list_auto_select");
		component.addToSubComponents(dropDownLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		dropDown1 = new FIBDropDown();
		dropDown1.setStaticList("value1,value2,value3");
		dropDown1.setAutoSelectFirstRow(true);
		component.addToSubComponents(dropDown1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		dropDownLabel2 = new FIBLabel("static_list_no_auto_select");
		component.addToSubComponents(dropDownLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		dropDown2 = new FIBDropDown();
		dropDown2.setStaticList("value1,value2,value3");
		dropDown2.setAutoSelectFirstRow(false);
		component.addToSubComponents(dropDown2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		dropDownLabel3 = new FIBLabel("dynamic_list");
		component.addToSubComponents(dropDownLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		dropDown3 = new FIBDropDown();
		dropDown3.setList(new DataBinding<List<?>>("data.children", dropDown3, List.class, BindingDefinitionType.GET));
		dropDown3.setAutoSelectFirstRow(true);
		component.addToSubComponents(dropDown3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown3.getList().isValid());

		dropDownLabel4 = new FIBLabel("dynamic_array");
		component.addToSubComponents(dropDownLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		dropDown4 = new FIBDropDown();
		dropDown4.setArray(new DataBinding<Object[]>("data.parents", dropDown4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		dropDown4.setAutoSelectFirstRow(true);
		component.addToSubComponents(dropDown4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown4.getArray().isValid());

		dropDownLabel5 = new FIBLabel("dynamic_list_bound_to_data");
		component.addToSubComponents(dropDownLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		dropDown5 = new FIBDropDown();
		dropDown5.setData(new DataBinding<Person>("data.biggestChild", dropDown5, Person.class, BindingDefinitionType.GET_SET));
		dropDown5.setList(new DataBinding<List<?>>("data.children", dropDown5, List.class, BindingDefinitionType.GET));
		dropDown5.setAutoSelectFirstRow(true);
		component.addToSubComponents(dropDown5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown5.getData().isValid());
		assertTrue(dropDown5.getList().isValid());

		dropDownLabel6 = new FIBLabel("enum");
		component.addToSubComponents(dropDownLabel6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		dropDown6 = new FIBDropDown();
		dropDown6.setData(new DataBinding<Gender>("data.father.gender", dropDown6, Gender.class, BindingDefinitionType.GET_SET));
		dropDown6.setAutoSelectFirstRow(true);
		component.addToSubComponents(dropDown6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown6.getData().isValid());

		// dropDown1.setData(new DataBinding<String>("data.father.firstName", firstNameTF, String.class, BindingDefinitionType.GET_SET));
		// component.addToSubComponents(dropDown1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		// assertTrue(firstNameTF.getData().isValid());

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
		assertNotNull(controller.viewForComponent(dropDownLabel1));
		assertNotNull(controller.viewForComponent(dropDown1));
		assertNotNull(controller.viewForComponent(dropDownLabel2));
		assertNotNull(controller.viewForComponent(dropDown2));
		assertNotNull(controller.viewForComponent(dropDownLabel3));
		assertNotNull(controller.viewForComponent(dropDown3));
		assertNotNull(controller.viewForComponent(dropDownLabel4));
		assertNotNull(controller.viewForComponent(dropDown4));

		assertEquals("value1", (String) controller.viewForComponent(dropDown1).getData());
		assertEquals(null, controller.viewForComponent(dropDown2).getData());
		assertEquals(family.getChildren().get(0), controller.viewForComponent(dropDown3).getData());
		assertEquals(family.getParents()[0], controller.viewForComponent(dropDown4).getData());
		assertEquals(family.getJackies().get(2), controller.viewForComponent(dropDown5).getData());
		assertEquals(Gender.Male, controller.viewForComponent(dropDown6).getData());

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
		assertEquals(family.getChildren().get(0), controller.viewForComponent(dropDown5).getData());
		assertEquals(Gender.Female, controller.viewForComponent(dropDown6).getData());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	public void test4ModifyValueInWidget() {
		FIBDropDownWidget<?> w5 = (FIBDropDownWidget<?>) controller.viewForComponent(dropDown5);
		FIBDropDownWidget<?> w6 = (FIBDropDownWidget<?>) controller.viewForComponent(dropDown6);

		w5.getDynamicJComponent().setSelectedItem(family.getChildren().get(1));
		w6.getDynamicJComponent().setSelectedItem(Gender.Male);

		assertEquals(family.getChildren().get(1), family.getBiggestChild());
		assertEquals(Gender.Male, family.getFather().getGender());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test5ModifyListValueInModel() {

		FIBDropDownWidget<?> w5 = (FIBDropDownWidget<?>) controller.viewForComponent(dropDown5);

		assertEquals(5, w5.getDynamicJComponent().getModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getDynamicJComponent().getModel().getSize());

		// System.out.println("new children=" + family.getChildren());
		// System.out.println("List model = " + w5.getDynamicJComponent().getModel());

		family.setBiggestChild(junior);
		assertEquals(junior, controller.viewForComponent(dropDown5).getData());
		assertEquals(junior, w5.getDynamicJComponent().getSelectedItem());

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
