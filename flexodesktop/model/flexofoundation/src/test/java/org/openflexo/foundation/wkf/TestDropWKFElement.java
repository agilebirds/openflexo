/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.wkf;

import java.util.Collection;
import java.util.Vector;

import junit.framework.AssertionFailedError;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.NodeCompound;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.toolbox.FileUtils;

public class TestDropWKFElement extends FlexoWKFTestCase {

	public static FlexoProject project;
	private static FlexoEditor editor;

	public TestDropWKFElement() {
		super("DropWKF");
	}

	// will create a project with the following processes :
	// PROCESS:rootProcess
	// PROCESS:subProcessFork
	// PROCESS:subProcessLoop
	// PROCESS:subProcessSingle

	// structure of rootProcess
	// PROCESS:rootProcess
	// SUBPROCESSACTIVITYNODE:forkNode (instanciate subProcessFork)
	// OPERATIONNODE:ForkList
	// SUBPROCESSACTIVITYNODE:loopNode (instanciate subProcessLoop)
	// SUBPROCESSACTIVITYNODE:singleNode (instanciate subProcessSingle)

	// structure of subProcessFork
	// PROCESS:subProcessFork
	// ACTIVITYNODE:activity1InForkProcess
	// ACTIVITYNODE:activity2InForkProcess

	public void test0Drop() {
		editor = createProject("TestDrop");
		project = editor.getProject();
		FlexoProcess rootProcess = project.getRootFlexoProcess();
		assertNotNull(rootProcess);
		rootProcess.getAllAbstractNodes();// Creates the node cache.
		FlexoProcess subProcessFork = createSubProcess("subProcessFork", rootProcess, editor);
		FlexoProcess subProcessLoop = createSubProcess("subProcessLoop", rootProcess, editor);
		FlexoProcess subProcessSingle = createSubProcess("subProcessSingle", rootProcess, editor);
		assertNotNull(subProcessSingle);
		assertEquals(rootProcess, subProcessSingle.getParentProcess());
		assertEquals(subProcessSingle, project.getLocalFlexoProcess("subProcessSingle"));
		subProcessFork.getAllAbstractNodes(); // Create the node cache.
		SubProcessNode forkNode = instanciateForkSubProcess(subProcessFork, rootProcess, 100, 100, editor);
		assertTrue(rootProcess.getAllAbstractNodes().contains(forkNode));
		SubProcessNode loopNode = instanciateLoopSubProcess(subProcessLoop, rootProcess, 100, 300, editor);
		assertTrue(rootProcess.getAllAbstractNodes().contains(loopNode));
		SubProcessNode singleNode = instanciateSingleSubProcess(subProcessSingle, rootProcess, 100, 500, editor);
		assertTrue(rootProcess.getAllAbstractNodes().contains(singleNode));
		OperationNode forkListNode = createOperationNode("ForkList", forkNode, 100, 100, editor);
		assertTrue(rootProcess.getAllAbstractNodes().contains(forkListNode));
		ActivityNode activity1InForkProcess = (ActivityNode) createActivityNode(subProcessFork, 100, 100, "activity1InForkProcess", editor);
		ActivityNode activity2InForkProcess = (ActivityNode) createActivityNode(subProcessFork, 100, 100, "activity2InForkProcess", editor);
		assertTrue(subProcessFork.getAllAbstractNodes().contains(activity1InForkProcess));
		assertTrue(subProcessFork.getAllAbstractNodes().contains(activity2InForkProcess));
	}

	public void test1NodeCompountConnexity() {
		FlexoProcess testCopyPaste = createSubProcess("testCopyPaste", project.getLocalFlexoProcess("subProcessFork"), editor);
		ActivityNode actA = (ActivityNode) createActivityNode(testCopyPaste, 100, 100, "ActivityA", editor);
		ActivityNode actB = (ActivityNode) createActivityNode(testCopyPaste, 250, 100, "ActivityB", editor);
		createPetriGraph(actA, editor);
		createPetriGraph(actB, editor);
		OperationNode opC = createOperationNode("OperationC", actA, 0, 0, editor);
		OperationNode opD = createOperationNode("OperationD", actA, 0, 0, editor);
		// FlexoPreCondition preD = createPreCondition(opD, null, editor);
		OperationNode opE = createOperationNode("OperationE", actB, 0, 0, editor);
		// FlexoPreCondition preE = createPreCondition(opE, null, editor);
		OperationNode opF = createOperationNode("OperationF", actB, 0, 0, editor);
		// FlexoPreCondition preF = createPreCondition(opF, null, editor);
		FlexoPostCondition cd = createEdge(opC, opD, editor);
		FlexoPostCondition de = createEdge(opD, opE, editor);
		FlexoPostCondition ef = createEdge(opE, opF, editor);
		Vector<PetriGraphNode> nodes = new Vector<PetriGraphNode>();
		nodes.add(actA);
		NodeCompound compound = new NodeCompound(testCopyPaste, nodes, new Vector<WKFArtefact>());
		// First we check that indeed, we have something that is not connex
		Collection<FlexoModelObject> allEmbedded = actA.getAllRecursivelyEmbeddedObjects();
		assertTrue(allEmbedded.contains(actA));
		assertTrue(allEmbedded.contains(opC));
		assertTrue(allEmbedded.contains(opD));
		assertTrue(allEmbedded.contains(de.getEndNode()));
		// assertTrue(allEmbedded.contains(opF));
		assertTrue(allEmbedded.contains(cd));
		assertTrue(allEmbedded.contains(de));
		// assertTrue(allEmbedded.contains(ef));

		// Then we check that the restricted embedding is connex
		Collection<FlexoModelObject> allEmbeddedRestricted = actA.getXMLMapping().getRestrictedEmbeddedObjectsForObject(actA,
				FlexoModelObject.class, false, true);
		assertTrue(allEmbeddedRestricted.contains(actA));
		assertTrue(allEmbeddedRestricted.contains(opC));
		assertTrue(allEmbeddedRestricted.contains(opD));
		assertFalse(allEmbeddedRestricted.contains(opE));
		assertFalse(allEmbeddedRestricted.contains(opF));
		assertTrue(allEmbeddedRestricted.contains(cd));
		assertTrue(allEmbeddedRestricted.contains(de)); // This edge is embedded by restricted because restricted follows outgoing edges
		assertFalse(allEmbeddedRestricted.contains(de.getEndNode()));
		assertFalse(allEmbeddedRestricted.contains(ef));
		assertFalse(allEmbedded.contains(ef.getEndNode()));

		Collection<FlexoModelObject> copyAllEmbedded = compound.getAllRecursivelyEmbeddedObjects();
		for (FlexoModelObject object : copyAllEmbedded) {
			if (object instanceof OperationNode) {
				// Only C and D must be embedded + Begin/End node of operation petri graph of A
				OperationNode node = (OperationNode) object;
				if (node.isBeginOrEndNode()) {
					assertEquals("ActivityA", node.getParentPetriGraph().getContainer().getName());
				} else {
					try {
						assertEquals("OperationC", node.getName());
					} catch (AssertionFailedError e) {
						assertEquals("OperationD", node.getName());
					}
				}
			} else if (object instanceof FlexoPostCondition) {
				FlexoPostCondition<AbstractNode, AbstractNode> post = (FlexoPostCondition<AbstractNode, AbstractNode>) object;
				assertEquals("OperationC", post.getStartNode().getName());
				assertEquals("OperationD", post.getEndNode().getNode().getName());
			}
		}
		// This test must call this to stop the RM checking of project
		project.close();
		FileUtils.deleteDir(project.getProjectDirectory());
		project = null;
		editor = null;
	}
}
