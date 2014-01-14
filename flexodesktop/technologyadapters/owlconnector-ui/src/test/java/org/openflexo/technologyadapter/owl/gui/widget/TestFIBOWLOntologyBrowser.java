package org.openflexo.technologyadapter.owl.gui.widget;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.OpenflexoTestCaseWithGUI;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.gui.FIBOWLOntologyBrowser;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;

/**
 * Test the structural and behavioural features of FIBOntologyBrowser
 * 
 * @author sylvain
 * 
 */
public class TestFIBOWLOntologyBrowser extends OpenflexoTestCaseWithGUI {

	private static GraphicalContextDelegate gcDelegate;

	private static OWLOntologyResource ontologyResource;

	@BeforeClass
	public static void setupClass() {
		instanciateTestServiceManager();
		initGUI();
	}

	@Test
	public void test1RetrieveOntology() {

		OWLTechnologyAdapter owlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);

		assertNotNull(owlTA);

		List<ResourceRepository<?>> owlRepositories = serviceManager.getInformationSpace().getAllRepositories(owlTA);

		ResourceRepository<OWLOntologyResource> ontologyRepository = (ResourceRepository<OWLOntologyResource>) owlRepositories.get(0);

		assertNotNull(ontologyRepository);

		ontologyResource = ontologyRepository.getResource("http://www.agilebirds.com/openflexo/ViewPoints/BasicOntology.owl");

		assertNotNull(ontologyResource);

		System.out.println("Found ontology resource " + ontologyResource);

		System.out.println("Try to load ontology resource " + ontologyResource);

		try {
			ontologyResource.loadResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (FlexoException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(ontologyResource.getLoadedResourceData());
	}

	@Test
	public void test2InstanciateWidget() {

		FIBOWLOntologyBrowser browser = new FIBOWLOntologyBrowser(ontologyResource.getLoadedResourceData());

		System.out.println("controller=" + browser.getController());

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
		gcDelegate = new GraphicalContextDelegate(TestFIBOWLOntologyBrowser.class.getSimpleName());
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
