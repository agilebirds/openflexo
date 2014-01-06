package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.FIBDropDownColumn;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabelColumn;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBNumberColumn;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextFieldColumn;
import org.openflexo.fib.model.GridBagLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a table widget, and where details panel are
 * multiple
 * 
 * @author sylvain
 * 
 */
public class FIBTableWidgetSelectionTest2 {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsContainerPanel;
	private static FIBTable table;

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

		component = new FIBPanel();
		component.setLayout(Layout.border);
		component.setDataClass(Family.class);

		table = new FIBTable();
		table.setName("table");
		table.setData(new DataBinding<List<?>>("data.children", table, List.class, BindingDefinitionType.GET));
		table.setAutoSelectFirstRow(true);
		table.setIteratorClass(Person.class);
		table.setBoundToSelectionManager(true);
		table.setManageDynamicModel(true);

		FIBTextFieldColumn c1 = new FIBTextFieldColumn();
		c1.setData(new DataBinding<String>("iterator.firstName", c1, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c1);
		FIBTextFieldColumn c2 = new FIBTextFieldColumn();
		c2.setData(new DataBinding<String>("iterator.lastName", c2, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c2);
		FIBNumberColumn c3 = new FIBNumberColumn();
		c3.setNumberType(NumberType.IntegerType);
		c3.setData(new DataBinding<Integer>("iterator.age", c3, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c3);
		FIBDropDownColumn c4 = new FIBDropDownColumn();
		c4.setData(new DataBinding<Gender>("iterator.gender", c4, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c4);
		FIBLabelColumn c5 = new FIBLabelColumn();
		c5.setData(new DataBinding<String>("iterator.toString", c5, String.class, BindingDefinitionType.GET));
		table.addToColumns(c5);

		detailsPanel1 = new FIBPanel();
		detailsPanel1.setLayout(Layout.twocols);

		label1 = new FIBLabel("This detail panel represents a Jacky");
		detailsPanel1.addToSubComponents(label1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel1 = new FIBLabel("first_name");
		detailsPanel1.addToSubComponents(firstNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF1 = new FIBTextField();
		firstNameTF1
				.setData(new DataBinding<String>("table.selected.firstName", firstNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(firstNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel1 = new FIBLabel("last_name");
		detailsPanel1.addToSubComponents(lastNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF1 = new FIBTextField();
		lastNameTF1.setData(new DataBinding<String>("table.selected.lastName", lastNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(lastNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel1 = new FIBLabel("full_name");
		detailsPanel1.addToSubComponents(fullNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF1 = new FIBTextField();
		fullNameTF1.setData(new DataBinding<String>("table.selected.firstName + ' ' + table.selected.lastName", fullNameTF1, String.class,
				BindingDefinitionType.GET));
		detailsPanel1.addToSubComponents(fullNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel2 = new FIBPanel();
		detailsPanel2.setLayout(Layout.twocols);

		label2 = new FIBLabel("This detail panel represents a normal child");
		detailsPanel2.addToSubComponents(label2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel2 = new FIBLabel("first_name");
		detailsPanel2.addToSubComponents(firstNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF2 = new FIBTextField();
		firstNameTF2
				.setData(new DataBinding<String>("table.selected.firstName", firstNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(firstNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel2 = new FIBLabel("last_name");
		detailsPanel2.addToSubComponents(lastNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF2 = new FIBTextField();
		lastNameTF2.setData(new DataBinding<String>("table.selected.lastName", lastNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(lastNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel2 = new FIBLabel("full_name");
		detailsPanel2.addToSubComponents(fullNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF2 = new FIBTextField();
		fullNameTF2.setData(new DataBinding<String>("table.selected.firstName + ' ' + table.selected.lastName", fullNameTF2, String.class,
				BindingDefinitionType.GET));
		detailsPanel2.addToSubComponents(fullNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel1.setVisible(new DataBinding<Boolean>("data.jackies.contains(table.selected)", detailsPanel1, Boolean.class,
				BindingDefinitionType.GET));
		detailsPanel2.setVisible(new DataBinding<Boolean>("!data.jackies.contains(table.selected)", detailsPanel2, Boolean.class,
				BindingDefinitionType.GET));

		detailsContainerPanel = new FIBPanel();
		detailsContainerPanel.setLayout(Layout.gridbag);
		detailsContainerPanel.addToSubComponents(detailsPanel1, new GridBagLayoutConstraints());
		detailsContainerPanel.addToSubComponents(detailsPanel2, new GridBagLayoutConstraints());

		component.addToSubComponents(table, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsContainerPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(table.getData().isValid());
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
		assertNotNull(controller.viewForComponent(table));

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);
		assertEquals(family.getChildren(), w.getTableModel().getValues());
		assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
	}

	/**
	 * Select some values, check that master/details scheme works
	 */
	@Test
	public void test3SelectSomeValues() {

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);
		FIBPanelView<?, ?> details1 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel1);
		FIBPanelView<?, ?> details2 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel2);

		w.getDynamicJComponent().getSelectionModel().clearSelection();
		w.getDynamicJComponent().getSelectionModel().addSelectionInterval(1, 1);

		assertFalse(details1.isViewVisible());
		assertTrue(details2.isViewVisible());
		assertEquals("Suzy", controller.viewForComponent(firstNameTF1).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF1).getData());
		assertEquals("Suzy Smith", controller.viewForComponent(fullNameTF1).getData());

		w.getDynamicJComponent().getSelectionModel().clearSelection();
		w.getDynamicJComponent().getSelectionModel().addSelectionInterval(2, 2);

		assertTrue(details1.isViewVisible());
		assertFalse(details2.isViewVisible());
		assertEquals("Jacky1", controller.viewForComponent(firstNameTF2).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF2).getData());
		assertEquals("Jacky1 Smith", controller.viewForComponent(fullNameTF2).getData());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBTableWidgetSelectionTest2.class.getSimpleName());
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
