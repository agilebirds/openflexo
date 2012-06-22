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
package org.openflexo.foundation.ontology;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.toolbox.FileResource;

public class TestOntologies extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoResourceCenter testResourceCenter;

	public TestOntologies(String name) {
		super(name);
	}

	/**
	 * Instanciate new ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testResourceCenter = LocalResourceCenterImplementation.instanciateTestLocalResourceCenterImplementation(new FileResource(
				"TestResourceCenter"));
	}

	public void test1AssertRDFOntologyPresentAndLoaded() {
		log("test1AssertRDFOntologyPresentAndLoaded()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		assertTrue(rdfOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfOntology.getImportedOntologies().firstElement() == testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology());
	}

	public void test2AssertRDFSOntologyPresentAndLoaded() {
		log("test2AssertRDFSOntologyPresentAndLoaded()");
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
	}

	public void test3AssertRDFAndRDFSOntologyCorrectImports() {
		log("test3AssertRDFAndRDFSOntologyCorrectImports()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertTrue(rdfOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfOntology.getImportedOntologies().firstElement() == rdfsOntology);
		assertTrue(rdfsOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfsOntology.getImportedOntologies().firstElement() == rdfOntology);
	}

	public void test4AssertOWLOntologyPresentAndLoaded() {
		log("test4AssertOWLOntologyPresentAndLoaded()");
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		assertTrue(owlOntology.getImportedOntologies().size() == 2);
		assertTrue(owlOntology.getImportedOntologies().contains(testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology()));
		assertTrue(owlOntology.getImportedOntologies().contains(testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology()));
		assertNotNull(owlOntology.getThingConcept());
	}

	public void test5AssertFlexoConceptsOntologyIsCorrect() {
		log("test5AssertFlexoConceptsOntologyIsCorrect()");
		FlexoOntology flexoConceptsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology();
		assertNotNull(flexoConceptsOntology);
		assertTrue(flexoConceptsOntology.isLoaded());
		assertEquals(10, flexoConceptsOntology.getClasses().size());

		assertNotNull(flexoConceptsOntology.getThingConcept());

		OntologyClass flexoConcept = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoModelObject = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoModelObject");
		assertNotNull(flexoModelObject);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessFolder = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessFolder");
		assertNotNull(flexoProcessFolder);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcess = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoProcess);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoRole = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoRole);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessElement = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessElement");
		assertNotNull(flexoProcessElement);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoActivity = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoActivity);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoOperation = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoOperation);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoEvent = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoEvent);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));

		OntologyDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "resourceName");
		assertNotNull(resourceNameProperty);

		OntologyClass classConcept = flexoConceptsOntology.getClass(OntologyLibrary.OWL_CLASS_URI);
		assertNotNull(classConcept);

		assertFalse(flexoConcept.redefinesOriginalDefinition());
		assertFalse(flexoModelObject.redefinesOriginalDefinition());

		assertTrue(flexoConcept.getSuperClasses().size() == 1);
		assertSameList(flexoConcept.getSuperClasses(), flexoConceptsOntology.getThingConcept());

		assertTrue(flexoModelObject.getSuperClasses().size() == 1);
		assertSameList(flexoModelObject.getSuperClasses(), flexoConceptsOntology.getThingConcept());

		assertSameList(flexoProcess.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoProcessFolder.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoRole.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoProcessElement.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoActivity.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoProcessElement);

		assertSameList(flexoEvent.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoProcessElement);

		assertSameList(flexoOperation.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoProcessElement);

		System.out.println("les statements=" + flexoModelObject.getStatements());
		System.out.println("les annotations=" + flexoModelObject.getAnnotationStatements());

		assertEquals(1, flexoModelObject.getAnnotationStatements().size());

	}

	public void test6TestMultiReferencesO3() {
		log("test6TestMultiReferencesO3()");
		FlexoOntology o1 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		FlexoOntology o2 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		FlexoOntology o3 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OntologyClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OntologyClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OntologyClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OntologyClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OntologyClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OntologyClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

	}

	public void test7TestMultiReferencesO4() {
		log("test7TestMultiReferencesO4()");
		FlexoOntology o1 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		FlexoOntology o2 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		FlexoOntology o3 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		FlexoOntology o5 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O4.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OntologyClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OntologyClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OntologyClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OntologyClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OntologyClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OntologyClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OntologyClass a1fromO4 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO4);
		assertEquals(a1, a1fromO4.getOriginalDefinition());

		OntologyClass a2fromO4 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO4);
		assertEquals(a2, a2fromO4.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getThingConcept(), a1fromO4, a2fromO4);

		assertTrue(a1fromO4.isSuperConceptOf(a2fromO4));

	}

	public void test8TestMultiReferencesO5() {
		log("test8TestMultiReferencesO5()");
		FlexoOntology o1 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		FlexoOntology o2 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		FlexoOntology o3 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		FlexoOntology o5 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O5.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OntologyClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OntologyClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OntologyClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OntologyClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OntologyClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OntologyClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OntologyClass a1fromO5 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO5);
		assertEquals(a1, a1fromO5.getOriginalDefinition());

		OntologyClass a2fromO5 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO5);
		assertEquals(a2, a2fromO5.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getThingConcept(), a1fromO5, a2fromO5);

		assertTrue(a2fromO5.isSuperConceptOf(a1fromO5));

	}

	public void test9TestInstances() {
		log("test9TestInstances()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.openflexo.org/test/TestInstances.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		assertEquals(4, ontology.getIndividuals().size());

		OntologyIndividual activity1 = ontology.getIndividual(ontology.getURI() + "#Activity1");
		assertNotNull(activity1);
		OntologyIndividual activity2 = ontology.getIndividual(ontology.getURI() + "#Activity2");
		assertNotNull(activity2);
		OntologyIndividual activity3 = ontology.getIndividual(ontology.getURI() + "#Activity3");
		assertNotNull(activity3);
		OntologyIndividual untypedIndividual = ontology.getIndividual(ontology.getURI() + "#UntypedIndividual");
		assertNotNull(untypedIndividual);

		assertSameList(ontology.getIndividuals(), activity1, activity2, activity3, untypedIndividual);

		OntologyClass redefinedFlexoActivity = ontology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(redefinedFlexoActivity);
		OntologyClass flexoActivity = ontology.getOntologyLibrary().getFlexoConceptOntology()
				.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoActivity);
		assertTrue(redefinedFlexoActivity.redefinesOriginalDefinition());
		assertEquals(flexoActivity, redefinedFlexoActivity.getOriginalDefinition());

		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity1));
		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity2));
		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity3));

		assertTrue(ontology.getThingConcept().isSuperConceptOf(untypedIndividual));
		assertTrue(ontology.getThingConcept().getOriginalDefinition().isSuperConceptOf(untypedIndividual));

		OntologyDataProperty resourceNameProperty = ontology.getOntologyLibrary().getFlexoConceptOntology()
				.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "resourceName");
		assertNotNull(resourceNameProperty);

		assertEquals("Process1", activity1.getPropertyValue(resourceNameProperty));

		assertEquals(35, ontology.getAccessibleClasses().size());

		System.out.println("les annotations=" + activity1.getAnnotationStatements());

	}

}
