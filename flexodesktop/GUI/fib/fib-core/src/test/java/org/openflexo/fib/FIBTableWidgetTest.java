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
import org.openflexo.fib.model.FIBDropDownColumn;
import org.openflexo.fib.model.FIBLabelColumn;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBNumberColumn;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextFieldColumn;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test the structural and behavioural features of FIBTable widget
 * 
 * @author sylvain
 * 
 */
public class FIBTableWidgetTest extends FIBTestCase {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBTable table;

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

		table = newFIBTable();
		table.setData(new DataBinding<List<?>>("data.children", table, List.class, BindingDefinitionType.GET));
		table.setAutoSelectFirstRow(true);
		table.setIteratorClass(Person.class);
		table.setBoundToSelectionManager(true);

		FIBTextFieldColumn c1 = newFIBTextFieldColumn();
		c1.setData(new DataBinding<String>("iterator.firstName", c1, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c1);
		FIBTextFieldColumn c2 = newFIBTextFieldColumn();
		c2.setData(new DataBinding<String>("iterator.lastName", c2, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c2);
		FIBNumberColumn c3 = newFIBNumberColumn();
		c3.setNumberType(NumberType.IntegerType);
		c3.setData(new DataBinding<Integer>("iterator.age", c3, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c3);
		FIBDropDownColumn c4 = newFIBDropDownColumn();
		c4.setData(new DataBinding<Gender>("iterator.gender", c4, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c4);
		FIBLabelColumn c5 = newFIBLabelColumn();
		c5.setData(new DataBinding<String>("iterator.toString", c5, String.class, BindingDefinitionType.GET));
		table.addToColumns(c5);

		component.addToSubComponents(table, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));
		assertTrue(table.getData().isValid());

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
		assertNotNull(controller.viewForComponent(table));

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);
		assertEquals(family.getChildren(), w.getTableModel().getValues());
		assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3ModifyValueInModel() {

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);

		assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
		assertEquals("Jacky3", w.getTableModel().getValueAt(4, 0));
		assertEquals("Smith", w.getTableModel().getValueAt(4, 1));
		assertEquals((long) 4, w.getTableModel().getValueAt(4, 2));
		assertEquals(Gender.Male, w.getTableModel().getValueAt(4, 3));
		assertEquals("Jacky3 Smith aged 4 (Male)", w.getTableModel().getValueAt(4, 4));
		family.getBiggestChild().setFirstName("Roger");
		family.getBiggestChild().setLastName("Rabbit");
		family.getBiggestChild().setAge(12);
		family.getBiggestChild().setGender(Gender.Female);
		assertEquals("Roger", w.getTableModel().getValueAt(4, 0));
		assertEquals("Rabbit", w.getTableModel().getValueAt(4, 1));
		assertEquals((long) 12, w.getTableModel().getValueAt(4, 2));
		assertEquals(Gender.Female, w.getTableModel().getValueAt(4, 3));
		assertEquals("Roger Rabbit aged 12 (Female)", w.getTableModel().getValueAt(4, 4));

		Person junior = family.createChild();

		assertEquals(6, w.getDynamicJComponent().getModel().getRowCount());
		assertEquals("John Jr", w.getTableModel().getValueAt(5, 0));
		assertEquals("Smith", w.getTableModel().getValueAt(5, 1));
		assertEquals((long) 0, w.getTableModel().getValueAt(5, 2));
		assertEquals(Gender.Male, w.getTableModel().getValueAt(5, 3));
		assertEquals("John Jr Smith aged 0 (Male)", w.getTableModel().getValueAt(5, 4));
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	public void test4ModifyValueInWidget() {

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);

		w.getTableModel().setValueAt("Jeannot", 2, 0);
		w.getTableModel().setValueAt("Lapin", 2, 1);
		w.getTableModel().setValueAt(6, 2, 2);
		w.getTableModel().setValueAt(Gender.Female, 2, 3);

		Person child = family.getChildren().get(2);

		assertEquals("Jeannot", child.getFirstName());
		assertEquals("Lapin", child.getLastName());
		assertEquals(6, child.getAge());
		assertEquals(Gender.Female, child.getGender());
		assertEquals("Jeannot Lapin aged 6 (Female)", w.getTableModel().getValueAt(2, 4));

	}

	/**
	 * Try to select some objects, check that selection is in sync with it
	 */
	@Test
	public void test5PerfomSomeTestsWithSelection() {

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);
		assertEquals(6, w.getDynamicJComponent().getModel().getRowCount());

		// w.getDynamicJComponent().getSelectionModel().addSelectionInterval(0, 1);
		assertEquals(Collections.singletonList(family.getChildren().get(0)), w.getSelection());

		// int[] indices = new int[3];
		// indices[0] = 1;
		// indices[1] = 2;
		// indices[2] = 4;
		// w7.getDynamicJComponent().setSelectedIndices(indices);
		w.getDynamicJComponent().getSelectionModel().clearSelection();
		w.getDynamicJComponent().getSelectionModel().addSelectionInterval(1, 2);
		w.getDynamicJComponent().getSelectionModel().addSelectionInterval(4, 4);

		List<Person> expectedSelection = new ArrayList<Person>();
		expectedSelection.add(family.getChildren().get(1));
		expectedSelection.add(family.getChildren().get(2));
		expectedSelection.add(family.getChildren().get(4));

		assertEquals(expectedSelection, w.getSelection());

		controller.setFocusedWidget(w);
		assertEquals(expectedSelection, controller.getSelectionLeader().getSelection());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBTableWidgetTest.class.getSimpleName());
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
