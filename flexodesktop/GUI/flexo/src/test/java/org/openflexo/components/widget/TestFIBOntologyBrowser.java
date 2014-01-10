package org.openflexo.components.widget;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.fib.testutils.FIBComponentGraphicalContextDelegate;
import org.openflexo.foundation.OpenflexoTestCase;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

/**
 * Test the structural and behavioural features of FIBOntologyBrowser
 * 
 * @author sylvain
 * 
 */
public class TestFIBOntologyBrowser extends OpenflexoTestCase {

	private static FIBComponentGraphicalContextDelegate gcDelegate;

	@BeforeClass
	public static void setupClass() {
		instanciateTestServiceManager();
		initGUI();
	}

	@Test
	public void test1InstanciateWidget() {

		TechnologyAdapter ta = serviceManager.getTechnologyAdapterService().getTechnologyAdapters().get(0);

		// serviceManager.getInformationSpace().getAllModelRepositories(technologyAdapter)

		FIBOntologyBrowser browser = new FIBOntologyBrowser(null);
		gcDelegate.addTab("FIBOntologyBrowser", browser.getController());
	}

	/*
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

	@Test
	public void test3ModifyValueInModel() {
		family1.getFather().setFirstName("Roger");
		family1.getFather().setLastName("Rabbit");
		assertEquals("Roger", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Rabbit", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Roger Rabbit", controller.viewForComponent(fullNameTF).getData());
	}

	@Test
	public void test4ModifyValueInWidget() {
		FIBTextFieldWidget w1 = (FIBTextFieldWidget) controller.viewForComponent(firstNameTF);
		FIBTextFieldWidget w2 = (FIBTextFieldWidget) controller.viewForComponent(lastNameTF);
		FIBTextFieldWidget w3 = (FIBTextFieldWidget) controller.viewForComponent(fullNameTF);

		w1.getDynamicJComponent().setText("James");
		w2.getDynamicJComponent().setText("Dean");

		assertEquals("James", family1.getFather().getFirstName());
		assertEquals("Dean", family1.getFather().getLastName());
		assertEquals("James Dean", controller.viewForComponent(fullNameTF).getData());
		assertEquals("James Dean", w3.getDynamicJComponent().getText());

	}

	@Test
	public void test5InstanciateComponent() {
		component.setDataClass(Family.class);
		FIBController controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		System.out.println("controller=" + controller);
		controller.buildView();
		family2 = new Family();
		controller.setDataObject(family2);

		gcDelegate.addTab("Test3", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(firstNameLabel));
		assertNotNull(controller.viewForComponent(firstNameTF));
		assertNotNull(controller.viewForComponent(lastNameLabel));
		assertNotNull(controller.viewForComponent(lastNameTF));
		assertNotNull(controller.viewForComponent(fullNameLabel));
		assertNotNull(controller.viewForComponent(fullNameTF));

		assertEquals("Robert", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Robert Smith", controller.viewForComponent(fullNameTF).getData());
	}*/

	public static void initGUI() {
		gcDelegate = new FIBComponentGraphicalContextDelegate(TestFIBOntologyBrowser.class.getSimpleName());
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
