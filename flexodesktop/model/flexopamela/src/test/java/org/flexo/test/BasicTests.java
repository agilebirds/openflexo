package org.flexo.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.flexo.model.AbstractNode;
import org.flexo.model.ActivityNode;
import org.flexo.model.Edge;
import org.flexo.model.EndNode;
import org.flexo.model.FlexoProcess;
import org.flexo.model.StartNode;
import org.flexo.model.TokenEdge;
import org.flexo.model.WKFAnnotation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openflexo.model.AbstractPAMELATest;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.UnitializedEntityException;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.Clipboard;
import org.openflexo.model.factory.EmbeddingType;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public class BasicTests extends AbstractPAMELATest {

	private ModelFactory factory;
	private ModelContext modelContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		new File("/tmp").mkdirs();
		modelContext = new ModelContext(FlexoProcess.class);
		factory = new ModelFactory(modelContext);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * We declare here a basic mapping model, and we check that the model construction is right
	 * 
	 * @throws Exception
	 */
	public void test1() throws Exception {

		System.out.println(modelContext.debug());

		assertEquals(11, modelContext.getEntityCount());

		validateBasicModelContext(modelContext);
	}

	public void test2() throws Exception {

		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		assertTrue(process instanceof FlexoProcess);
		try {
			process.getName();
			fail("getName() should not be invokable until init() has been called");
		} catch (UnitializedEntityException e) {
			// OK this on purpose.
		}
		process.init("234XX");
		System.out.println("process=" + process);
		System.out.println("Id=" + process.getFlexoID());
		process.setName("NewProcess");
		process.setFoo(8);
		assertEquals("NewProcess", process.getName());
		assertEquals("234XX", process.getFlexoID());
		assertEquals(8, process.getFoo());

		ActivityNode activityNode = factory.newInstance(ActivityNode.class);
		activityNode.init();
		assertTrue(activityNode instanceof ActivityNode);
		assertEquals("0000", activityNode.getFlexoID());

		activityNode.setName("MyActivity");
		process.addToNodes(activityNode);

		System.out.println("activityNode=" + activityNode);
		assertEquals("MyActivity", activityNode.getName());
		assertTrue(process.getNodes().contains(activityNode));
		assertEquals(process, activityNode.getProcess());
		System.out.println("process: " + activityNode.getProcess());

		StartNode startNode = factory.newInstance(StartNode.class);
		startNode.setName("Start");
		process.addToNodes(startNode);

		EndNode endNode = factory.newInstance(EndNode.class);
		endNode.init();
		endNode.setName("End");
		process.addToNodes(endNode);

		System.out.println("process=" + process);

		TokenEdge edge1 = (TokenEdge) factory.newInstance(TokenEdge.class).init(startNode, activityNode);
		edge1.setName("edge1");
		// startNode.addToOutgoingEdges(edge1);
		// activityNode.addToIncomingEdges(edge1);
		System.out.println("edge1=" + edge1);
		assertEquals(process, edge1.getProcess());

		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);
		// edge2.setStartNode(activityNode);
		// edge2.setEndNode(endNode);

		System.out.println("edge2=" + edge2);
		assertEquals(process, edge2.getProcess());

		try {
			FileOutputStream fos = new FileOutputStream("/tmp/TestFile.xml");
			factory.serialize(process, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	public void test3() throws Exception {
		File file = File.createTempFile("Pamela.test3TestFile", ".xml");
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		assertEquals("MyActivity", activityNode.getName());

		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		assertEquals("Start", startNode.getName());

		EndNode endNode = factory.newInstance(EndNode.class, "End");
		endNode.init();
		process.addToNodes(endNode);
		assertEquals("End", endNode.getName());

		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);
		assertEquals(activityNode, edge2.getStartNode());
		assertTrue(activityNode.getOutgoingEdges().contains(edge2));
		assertEquals(1, startNode.getOutgoingEdges().size());
		assertEquals(1, activityNode.getOutgoingEdges().size());

		WKFAnnotation annotation1 = factory.newInstance(WKFAnnotation.class, "Annotation 1");
		WKFAnnotation annotation2 = factory.newInstance(WKFAnnotation.class, "Annotation 2");
		startNode.setMasterAnnotation(annotation1);
		startNode.addToOtherAnnotations(annotation2);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			factory.serialize(process, fos);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}
		String xml1 = FileUtils.fileContents(file);

		edge2.setStartNode(startNode);
		assertEquals(startNode, edge2.getStartNode());
		assertTrue(startNode.getOutgoingEdges().contains(edge2));
		assertEquals(2, startNode.getOutgoingEdges().size());
		assertEquals(0, activityNode.getOutgoingEdges().size());

		try {
			fos = new FileOutputStream(file);
			factory.serialize(process, fos);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}

		activityNode.addToOutgoingEdges(edge2);
		assertEquals(activityNode, edge2.getStartNode());
		assertFalse(startNode.getOutgoingEdges().contains(edge2));
		assertTrue(activityNode.getOutgoingEdges().contains(edge2));
		assertEquals(1, startNode.getOutgoingEdges().size());
		assertEquals(1, activityNode.getOutgoingEdges().size());

		try {
			fos = new FileOutputStream(file);
			factory.serialize(process, fos);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}

		String xml3 = FileUtils.fileContents(file, "UTF-8");
		assertEquals(xml1, xml3);

		// test4
		process = loadProcessFromFile(file);

		assertTrue(process instanceof FlexoProcess);

		assertEquals("NewProcess", process.getName());
		assertEquals("234XX", process.getFlexoID());
		assertEquals(8, process.getFoo());
		activityNode = (ActivityNode) process.getNodeNamed("MyActivity");
		assertNotNull(activityNode);
		assertEquals("MyActivity", activityNode.getName());
		assertTrue(process.getNodes().contains(activityNode));
		assertEquals(process, activityNode.getProcess());
		startNode = (StartNode) process.getNodeNamed("Start");
		assertNotNull(startNode);
		assertNotNull(startNode.getMasterAnnotation());
		assertEquals("Annotation 1", startNode.getMasterAnnotation().getText());
		assertEquals(1, startNode.getOtherAnnotations().size());
		endNode = (EndNode) process.getNodeNamed("End");
		assertNotNull(endNode);
		edge1 = (TokenEdge) process.getEdgeNamed("edge1");
		assertNotNull(edge1);
		edge2 = (TokenEdge) process.getEdgeNamed("edge2");
		assertNotNull(edge2);
		assertEquals(process, edge1.getProcess());
		assertEquals(process, edge2.getProcess());
		assertEquals(activityNode, edge2.getStartNode());
		assertTrue(activityNode.getOutgoingEdges().contains(edge2));
		assertEquals(1, startNode.getOutgoingEdges().size());
		assertEquals(1, activityNode.getOutgoingEdges().size());
		file.delete();
	}

	private FlexoProcess loadProcessFromFile(File file) throws ModelDefinitionException {

		FlexoProcess process = null;

		FileOutputStream fos = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			try {
				process = (FlexoProcess) factory.deserialize(fis);
			} finally {
				IOUtils.closeQuietly(fis);
			}
			System.out.println("Read: " + process);
			fos = new FileOutputStream(file);
			factory.serialize(process, fos);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}
		return process;
	}

	public void test5() throws Exception {
		/*
		 * FlexoProcess process = loadProcessFromFile(); ActivityNode activityNode = (ActivityNode) process.getNodeNamed("MyActivity");
		 * StartNode startNode = (StartNode) process.getNodeNamed("Start"); WKFAnnotation annotation1 = startNode.getMasterAnnotation();
		 * WKFAnnotation annotation2 = startNode.getOtherAnnotations().get(0); TokenEdge edge1 = (TokenEdge) process.getEdgeNamed("edge1");
		 * TokenEdge edge2 = (TokenEdge) process.getEdgeNamed("edge2"); assertTrue(activityNode.getIncomingEdges().contains(edge1));
		 * startNode.delete(); assertTrue(startNode.isDeleted()); assertTrue(edge1.isDeleted()); assertTrue(annotation1.isDeleted());
		 * assertTrue(annotation2.isDeleted()); assertNull(process.getNodeNamed("Start")); assertNull(process.getEdgeNamed("edge1"));
		 * assertTrue(!activityNode.getIncomingEdges().contains(edge1));
		 */
	}

	/**
	 * Testing getEmbeddedObjects()
	 * 
	 * @throws Exception
	 */
	public void test6() throws Exception {
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);

		// No embedded objects for a simple node (edges let the closure fail)
		List<Object> embeddedObjects1 = factory.getEmbeddedObjects(startNode, EmbeddingType.CLOSURE);
		System.out.println("Embedded: " + embeddedObjects1);
		assertEquals(0, embeddedObjects1.size());

		// The 3 nodes and the 2 edges belongs to same closure, take them
		List<Object> embeddedObjects2 = factory.getEmbeddedObjects(process, EmbeddingType.CLOSURE);
		System.out.println("Embedded: " + embeddedObjects2);
		assertEquals(5, embeddedObjects2.size());
		assertTrue(embeddedObjects2.contains(activityNode));
		assertTrue(embeddedObjects2.contains(startNode));
		assertTrue(embeddedObjects2.contains(endNode));
		assertTrue(embeddedObjects2.contains(edge1));
		assertTrue(embeddedObjects2.contains(edge2));

		// Computes embedded objects for activity node in the context of process
		// In this case, edge1 and edge2 are also embedded because belonging to supplied
		// context which is the process itself
		List<Object> embeddedObjects3 = factory.getEmbeddedObjects(activityNode, EmbeddingType.CLOSURE, process);
		System.out.println("Embedded: " + embeddedObjects3);
		assertEquals(2, embeddedObjects3.size());
		assertTrue(embeddedObjects3.contains(edge1));
		assertTrue(embeddedObjects3.contains(edge2));

		// Computes embedded objects for activity node in the context of node startNode
		// In this case, edge1 is also embedded because belonging to supplied
		// context which is the opposite node startNode
		List<Object> embeddedObjects4 = factory.getEmbeddedObjects(activityNode, EmbeddingType.CLOSURE, startNode);
		System.out.println("Embedded: " + embeddedObjects4);
		assertEquals(1, embeddedObjects4.size());
		assertTrue(embeddedObjects4.contains(edge1));
	}

	/**
	 * Testing cloning
	 * 
	 * @throws Exception
	 */
	public void test7() throws Exception {
		File file = File.createTempFile("PAMELA.test7", ".xml");
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);

		FlexoProcess processCopy = (FlexoProcess) process.cloneObject();
		System.out.println("processCopy=" + processCopy);

		assertNotNull(processCopy);
		assertTrue(processCopy instanceof FlexoProcess);
		// TODO: Uncomment next line when FACTORY strategy will be implemented
		// assertEquals("NewProcess1", processCopy.getName());
		assertEquals("234XX", processCopy.getFlexoID());
		assertEquals(8, processCopy.getFoo());

		ActivityNode activityNodeCopy = (ActivityNode) processCopy.getNodeNamed("MyActivity");
		assertNotNull(activityNodeCopy);
		assertEquals("MyActivity", activityNodeCopy.getName());
		assertTrue(processCopy.getNodes().contains(activityNodeCopy));
		assertEquals(processCopy, activityNodeCopy.getProcess());
		StartNode startNodeCopy = (StartNode) processCopy.getNodeNamed("Start");
		assertNotNull(startNodeCopy);
		EndNode endNodeCopy = (EndNode) processCopy.getNodeNamed("End");
		assertNotNull(endNodeCopy);
		TokenEdge edge1Copy = (TokenEdge) processCopy.getEdgeNamed("edge1");
		assertNotNull(edge1Copy);
		TokenEdge edge2Copy = (TokenEdge) processCopy.getEdgeNamed("edge2");
		assertNotNull(edge2Copy);
		assertEquals(processCopy, edge1Copy.getProcess());
		assertEquals(processCopy, edge2Copy.getProcess());
		assertEquals(startNodeCopy, edge1Copy.getStartNode());
		assertEquals(activityNodeCopy, edge1Copy.getEndNode());
		assertEquals(activityNodeCopy, edge2Copy.getStartNode());
		assertEquals(endNodeCopy, edge2Copy.getEndNode());
		assertTrue(activityNodeCopy.getOutgoingEdges().contains(edge2Copy));
		assertEquals(1, startNodeCopy.getOutgoingEdges().size());
		assertEquals(1, activityNodeCopy.getOutgoingEdges().size());

		assertNotSame(edge1, edge1Copy);
		assertNotSame(edge2, edge2Copy);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			factory.serialize(processCopy, fos);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
			file.delete();
		}

	}

	/**
	 * Testing cloning (with and without context)
	 * 
	 * @throws Exception
	 */
	public void test8() throws Exception {
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);

		// Clone activityNode, edge1 and edge2 will be cloned as their
		// related property @CloningStrategy is flagged as CLONE
		ActivityNode activityNodeCopy = (ActivityNode) activityNode.cloneObject();
		System.out.println("activityNodeCopy=" + activityNodeCopy);
		System.out.println(debug(activityNodeCopy));

		assertEquals(1, activityNodeCopy.getIncomingEdges().size());
		TokenEdge edge1Copy = (TokenEdge) activityNodeCopy.getIncomingEdges().get(0);
		assertEquals("edge1", edge1Copy.getName());

		assertEquals(1, activityNodeCopy.getOutgoingEdges().size());
		TokenEdge edge2Copy = (TokenEdge) activityNodeCopy.getOutgoingEdges().get(0);
		assertEquals("edge2", edge2Copy.getName());

		// Clone activityNode in the context of process, edge1 and edge2 will be cloned
		// because they belong to process' context
		ActivityNode activityNodeCopy2 = (ActivityNode) activityNode.cloneObject(process);
		System.out.println("activityNodeCopy2=" + activityNodeCopy2);
		System.out.println(debug(activityNodeCopy2));

		assertEquals(1, activityNodeCopy2.getIncomingEdges().size());
		TokenEdge edge1Copy2 = (TokenEdge) activityNodeCopy2.getIncomingEdges().get(0);
		assertEquals("edge1", edge1Copy2.getName());

		assertEquals(1, activityNodeCopy2.getOutgoingEdges().size());
		TokenEdge edge2Copy2 = (TokenEdge) activityNodeCopy2.getOutgoingEdges().get(0);
		assertEquals("edge2", edge2Copy2.getName());

		// Clone activityNode in the context of startNode, only edge1 will be cloned
		ActivityNode activityNodeCopy3 = (ActivityNode) activityNode.cloneObject(startNode);
		System.out.println("activityNodeCopy3=" + activityNodeCopy3);
		System.out.println(debug(activityNodeCopy3));

		assertEquals(1, activityNodeCopy3.getIncomingEdges().size());
		TokenEdge edge1Copy3 = (TokenEdge) activityNodeCopy3.getIncomingEdges().get(0);
		assertEquals("edge1", edge1Copy3.getName());

		assertEquals(0, activityNodeCopy3.getOutgoingEdges().size());
	}

	/**
	 * Testing copy paste
	 * 
	 * @throws Exception
	 */
	public void test9() throws Exception {
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);

		Clipboard clipboard = factory.copy(activityNode);
		System.out.println("Clipboard 1");
		System.out.println(debug(clipboard.getSingleContents()));
		assertTrue(clipboard.isSingleObject());
		assertTrue(clipboard.getSingleContents() instanceof ActivityNode);
		assertEquals("MyActivity", ((ActivityNode) clipboard.getSingleContents()).getName());
		assertEquals(0, ((ActivityNode) clipboard.getSingleContents()).getIncomingEdges().size());
		assertEquals(0, ((ActivityNode) clipboard.getSingleContents()).getOutgoingEdges().size());

		Object pasted = factory.paste(clipboard, process);
		assertNotNull(pasted);
		assertTrue(pasted instanceof ActivityNode);
		System.out.println(debug(process));
		assertEquals(4, process.getNodes().size());
		assertTrue(((List<?>) process.getNodesNamed("MyActivity")).contains(pasted));
		ActivityNode newNode = (ActivityNode) pasted;
		assertEquals(0, newNode.getIncomingEdges().size());
		assertEquals(0, newNode.getOutgoingEdges().size());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("/tmp/TestFile.xml");
			factory.serialize(process, fos);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}

	}

	/**
	 * Testing copy paste
	 * 
	 * @throws Exception
	 */
	public void test10() throws Exception {
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);

		Clipboard clipboard = factory.copy(startNode, activityNode);
		System.out.println("Clipboard");
		System.out.println(debug(clipboard.getMultipleContents()));
		assertFalse(clipboard.isSingleObject());
		assertTrue(clipboard.getMultipleContents() instanceof List);
		assertEquals(2, (clipboard.getMultipleContents()).size());
		assertTrue(clipboard.getMultipleContents().get(0) instanceof StartNode);
		assertTrue(clipboard.getMultipleContents().get(1) instanceof ActivityNode);
		StartNode copiedStartNode = (StartNode) clipboard.getMultipleContents().get(0);
		ActivityNode copiedActivityNode = (ActivityNode) clipboard.getMultipleContents().get(1);
		assertEquals("Start", copiedStartNode.getName());
		assertEquals("MyActivity", copiedActivityNode.getName());
		assertEquals(0, copiedStartNode.getIncomingEdges().size());
		assertEquals(1, copiedStartNode.getOutgoingEdges().size());
		assertEquals(1, copiedActivityNode.getIncomingEdges().size());
		assertEquals(0, copiedActivityNode.getOutgoingEdges().size());
		assertSame(copiedStartNode.getOutgoingEdges().get(0), copiedActivityNode.getIncomingEdges().get(0));

		Object pasted = factory.paste(clipboard, process);
		assertNotNull(pasted);
		assertTrue(pasted instanceof List);

		System.out.println(clipboard.debug());

		System.out.println(debug(process));
		assertEquals(5, process.getNodes().size());
		ActivityNode newActivity = null;
		StartNode newStartNode = null;
		for (Object o : (List<?>) pasted) {
			if (o instanceof ActivityNode) {
				newActivity = (ActivityNode) o;
			} else if (o instanceof StartNode) {
				newStartNode = (StartNode) o;
			}
		}
		assertEquals(1, newActivity.getIncomingEdges().size());
		assertEquals(0, newActivity.getOutgoingEdges().size());
		assertEquals(0, newStartNode.getIncomingEdges().size());
		assertEquals(1, newStartNode.getOutgoingEdges().size());
		assertSame(newStartNode.getOutgoingEdges().get(0), newActivity.getIncomingEdges().get(0));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("/tmp/TestFile.xml");
			factory.serialize(process, fos);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	public void testModify() {
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		assertTrue(process.isModified());
		process.setName("NewProcess");
		assertTrue(process.isModified());
		process.setFoo(8);
		assertTrue(process.isModified());

		serializeObject(process);
		assertFalse(process.isModified());

		ActivityNode activityNode = factory.newInstance(ActivityNode.class);
		assertFalse(activityNode.isModified());
		// Here we verify that if we use an initializer, the object is marked as modified
		ActivityNode activityNode2 = factory.newInstance(ActivityNode.class, "MyActivity");
		assertTrue(activityNode2.isModified());

		process.addToNodes(activityNode);
		assertTrue(process.isModified());
		serializeObject(process);
		assertFalse(process.isModified());
		assertFalse(activityNode.isModified());
		activityNode.setName("Coucou");
		assertTrue(activityNode.isModified());
		assertTrue(process.isModified());// Here we verify the forward state
		serializeObject(process);
		assertFalse(activityNode.isModified());// Here we verify the synch forward state

		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		serializeObject(process);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		assertTrue(process.isModified());// Here we verify the forward state
		assertTrue(activityNode.isModified());
		activityNode.removeFromIncomingEdges(edge1);
		process.removeFromNodes(activityNode);
		serializeObject(process);

		assertFalse(process.isModified());// Here we verify that process has been marked as not-modified (by the serialization mechanism)
		assertTrue(activityNode.isModified()); // And that activity node is no longer synched with its previous container process.
	}

	public void testDeletion() {
		FlexoProcess process = factory.newInstance(FlexoProcess.class);
		process.init("234XX");
		process.setName("NewProcess");
		process.setFoo(8);

		ActivityNode activityNode = factory.newInstance(ActivityNode.class, "MyActivity");
		process.addToNodes(activityNode);
		StartNode startNode = factory.newInstance(StartNode.class, "Start");
		process.addToNodes(startNode);
		EndNode endNode = factory.newInstance(EndNode.class, "End");
		process.addToNodes(endNode);
		TokenEdge edge1 = factory.newInstance(TokenEdge.class, "edge1", startNode, activityNode);
		System.out.println("Deleting " + startNode);
		try {
			startNode.delete();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
		assertTrue(startNode.isDeleted());
		assertFalse(edge1.isDeleted());
		activityNode.delete();
		assertTrue(edge1.isDeleted());
		assertTrue(activityNode.isDeleted());
	}

	private void serializeObject(AccessibleProxyObject object) {
		try {
			factory.serialize(object, new ByteArrayOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	public String debug(Object o) {
		if (o instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) o;
			StringBuffer returned = new StringBuffer();
			returned.append("------------------- " + o + " -------------------\n");
			List<Edge> inEdges = node.getIncomingEdges();
			if (inEdges != null) {
				for (Object e : inEdges) {
					if (e == null) {
						returned.append("null     Incoming: " + null + "\n");
					} else {
						returned.append(Integer.toHexString(e.hashCode()) + " Incoming: " + e + "\n");
					}
				}
			}
			List<Edge> outEdges = node.getOutgoingEdges();
			if (outEdges != null) {
				for (Object e : outEdges) {
					if (e == null) {
						returned.append("null     Outgoing: " + null + "\n");
					} else {
						returned.append(Integer.toHexString(e.hashCode()) + " Outgoing: " + e + "\n");
					}
				}
			}
			return returned.toString();
		}

		if (o instanceof Edge) {
			Edge edge = (Edge) o;
			StringBuffer returned = new StringBuffer();
			returned.append("------------------- " + edge + " -------------------\n");
			returned.append("From: " + edge.getStartNode() + "\n");
			returned.append("To: " + edge.getEndNode() + "\n");
			return returned.toString();
		}

		if (o instanceof FlexoProcess) {
			FlexoProcess process = (FlexoProcess) o;
			StringBuffer returned = new StringBuffer();
			returned.append("=================== " + process + " ===================\n");
			for (AbstractNode node : process.getNodes()) {
				returned.append(debug(node));
			}
			return returned.toString();
		}

		if (o instanceof List) {
			StringBuffer returned = new StringBuffer();
			for (Object o2 : (List) o) {
				returned.append(debug(o2));
			}
			return returned.toString();
		}

		return o.toString();
	}

}
