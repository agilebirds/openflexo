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
package org.openflexo.components.widget;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.toolbox.FileResource;

public class TestOntologyBrowserModel extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoResourceCenter testResourceCenter;

	public TestOntologyBrowserModel(String name) {
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

	public void test1AssertRDFOntologyNoHierarchy() {
		log("test1AssertRDFOntologyNoHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfsOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfsOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfsOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfsOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfsOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), rdfOntology);

		assertEquals(6, obm.getChildren(rdfOntology).size());
		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, rdfsOntology, typeProperty,
				valueProperty);
		assertSameList(obm.getChildren(listConcept), restProperty, firstProperty);
		assertSameList(obm.getChildren(statementConcept), subjectProperty, predicateProperty, objectProperty);

		assertEquals(4, obm.getChildren(rdfsOntology).size());
		assertSameList(obm.getChildren(rdfsOntology), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty);
		assertEquals(8, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty);
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

		obm.setDisplayPropertiesInClasses(false);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), rdfOntology);

		assertEquals(11, obm.getChildren(rdfOntology).size());
		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, rdfsOntology, typeProperty,
				valueProperty, restProperty, firstProperty, subjectProperty, predicateProperty, objectProperty);

	}

	public void test2AssertRDFOntologyHierarchy() {
		log("test2AssertRDFOntologyHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), resourceConcept);

		assertEquals(13, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), listConcept, propertyConcept, statementConcept, classConcept, containerConcept,
				literalConcept, labelProperty, isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty, valueProperty,
				typeProperty);

		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);
	}

	public void test3AssertRDFOntologyStrictMode() {
		log("test3AssertRDFOntologyStrictMode()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfOntology);
		obm.setStrictMode(true);
		obm.setHierarchicalMode(false);
		obm.recomputeStructure();

		assertEquals(5, obm.getRoots().size());
		assertSameList(obm.getRoots(), listConcept, propertyConcept, statementConcept, typeProperty, valueProperty);

	}

	public void test4AssertRDFSOntologyNoHierarchy() {
		log("test4AssertRDFSOntologyNoHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), rdfsOntology);

		assertEquals(5, obm.getChildren(rdfsOntology).size());
		assertSameList(obm.getChildren(rdfsOntology), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty, rdfOntology);

		assertEquals(8, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty, commentProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty);
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

		assertEquals(5, obm.getChildren(rdfOntology).size());
		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, typeProperty, valueProperty);
	}

	public void test5AssertRDFSOntologyHierarchy() {
		log("test5AssertRDFSOntologyHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), resourceConcept);

		assertEquals(13, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), listConcept, propertyConcept, statementConcept, classConcept, containerConcept,
				literalConcept, labelProperty, isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty, valueProperty,
				typeProperty);

		assertEquals(2, obm.getChildren(classConcept).size());
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);
	}

	public void test6AssertRDFSOntologyStrictMode() {
		log("test6AssertRDFSOntologyStrictMode()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfsOntology);
		obm.setStrictMode(true);
		obm.setHierarchicalMode(false);
		obm.recomputeStructure();

		assertEquals(4, obm.getRoots().size());
		assertSameList(obm.getRoots(), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty);

		assertEquals(8, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty);

		assertEquals(2, obm.getChildren(classConcept).size());
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

	}

	public void test7AssertOWLOntologyNoHierarchy() {
		log("test7AssertOWLOntologyNoHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyClass thingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OntologyClass allDifferentConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OntologyClass annotationPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OntologyClass owlClassConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OntologyClass restrictionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OntologyClass datatypePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OntologyClass namedIndividualConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OntologyClass negativePropertyAssertionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OntologyClass nothingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OntologyClass objectPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OntologyClass asymmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OntologyClass inverseFunctionalPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OntologyClass irreflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OntologyClass reflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OntologyClass symmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OntologyClass transitivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OntologyClass ontologyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OntologyClass ontologyPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "OntologyProperty");
		assertNotNull(ontologyPropertyConcept);
		OntologyObjectProperty topObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "topObjectProperty");
		assertNotNull(topObjectProperty);
		OntologyObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OntologyDataProperty bottomDataProperty = owlOntology
				.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OntologyDataProperty topDataProperty = owlOntology.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OntologyObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OntologyObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OntologyObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OntologyObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OntologyObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OntologyObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OntologyObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OntologyObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OntologyObjectProperty membersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OntologyObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OntologyObjectProperty oneOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OntologyObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OntologyObjectProperty unionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OntologyObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OntologyObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OntologyObjectProperty differentFromProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "differentFrom");
		assertNotNull(differentFromProperty);
		OntologyObjectProperty sameAsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OntologyObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OntologyObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OntologyObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OntologyObjectProperty complementOfProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OntologyObjectProperty disjointWithProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OntologyObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OntologyObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OntologyObjectProperty targetValueProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OntologyObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OntologyObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OntologyObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(owlOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), owlOntology);

		assertEquals(18, obm.getChildren(owlOntology).size());
		assertSameList(obm.getChildren(owlOntology), thingConcept, rdfsOntology, rdfOntology, annotatedPropertyProperty,
				annotatedSourceProperty, annotatedTargetProperty, datatypeComplementOfProperty, deprecatedProperty,
				equivalentClassProperty, equivalentPropertyProperty, intersectionOfProperty, membersProperty, onDatatypeProperty,
				oneOfProperty, propertyDisjointWithProperty, unionOfProperty, versionInfoProperty, withRestrictionsProperty);

		assertSameList(obm.getChildren(thingConcept), allDifferentConcept, annotationPropertyConcept, owlClassConcept,
				datatypePropertyConcept, namedIndividualConcept, negativePropertyAssertionConcept, nothingConcept, objectPropertyConcept,
				ontologyConcept, ontologyPropertyConcept, bottomDataProperty, topObjectProperty, topDataProperty, bottomObjectProperty,
				sameAsProperty, differentFromProperty);

		assertSameList(obm.getChildren(allDifferentConcept), distinctMembersProperty);
		assertSameList(obm.getChildren(owlClassConcept), restrictionConcept, hasKeyProperty, disjointUnionOfProperty, complementOfProperty,
				disjointWithProperty);
		assertSameList(obm.getChildren(negativePropertyAssertionConcept), sourceIndividualProperty, assertionPropertyProperty,
				targetValueProperty, targetIndividualProperty);

		assertSameList(obm.getChildren(objectPropertyConcept), asymmetricPropertyConcept, inverseFunctionalPropertyConcept,
				irreflexivePropertyConcept, reflexivePropertyConcept, symmetricPropertyConcept, transitivePropertyConcept,
				propertyChainAxiomProperty, inverseOfProperty);

		assertEquals(4, obm.getChildren(rdfsOntology).size());
		assertSameList(obm.getChildren(rdfsOntology), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty);

		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty);

		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, typeProperty, valueProperty);
	}

	public void test8AssertOWLOntologyHierarchy() {
		log("test8AssertOWLOntologyHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyClass thingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OntologyClass allDifferentConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OntologyClass annotationPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OntologyClass owlClassConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OntologyClass restrictionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OntologyClass datatypePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OntologyClass namedIndividualConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OntologyClass negativePropertyAssertionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OntologyClass nothingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OntologyClass objectPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OntologyClass asymmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OntologyClass inverseFunctionalPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OntologyClass irreflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OntologyClass reflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OntologyClass symmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OntologyClass transitivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OntologyClass ontologyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OntologyClass ontologyPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "OntologyProperty");
		assertNotNull(ontologyPropertyConcept);
		OntologyObjectProperty topObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "topObjectProperty");
		assertNotNull(topObjectProperty);
		OntologyObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OntologyDataProperty bottomDataProperty = owlOntology
				.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OntologyDataProperty topDataProperty = owlOntology.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);
		OntologyObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OntologyObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OntologyObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OntologyObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OntologyObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OntologyObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OntologyObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OntologyObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OntologyObjectProperty membersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OntologyObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OntologyObjectProperty oneOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OntologyObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OntologyObjectProperty unionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OntologyObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OntologyObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OntologyObjectProperty differentFromProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "differentFrom");
		assertNotNull(differentFromProperty);
		OntologyObjectProperty sameAsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OntologyObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OntologyObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OntologyObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OntologyObjectProperty complementOfProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OntologyObjectProperty disjointWithProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OntologyObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OntologyObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OntologyObjectProperty targetValueProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OntologyObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OntologyObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OntologyObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(owlOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thingConcept);

		assertEquals(9, obm.getChildren(thingConcept).size());
		assertSameList(obm.getChildren(thingConcept), namedIndividualConcept, nothingConcept, resourceConcept, bottomDataProperty,
				topObjectProperty, topDataProperty, bottomObjectProperty, sameAsProperty, differentFromProperty);

		assertEquals(22, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), allDifferentConcept, negativePropertyAssertionConcept, ontologyConcept,
				classConcept, containerConcept, literalConcept, listConcept, propertyConcept, statementConcept, commentProperty,
				annotatedTargetProperty, typeProperty, versionInfoProperty, memberProperty, seeAlsoProperty, membersProperty,
				annotatedPropertyProperty, valueProperty, isDefinedByProperty, annotatedSourceProperty, labelProperty, deprecatedProperty);

		assertEquals(7, obm.getChildren(classConcept).size());
		assertSameList(obm.getChildren(classConcept), owlClassConcept, datatypeConcept, unionOfProperty, oneOfProperty, subClassOfProperty,
				equivalentClassProperty, intersectionOfProperty);

		assertEquals(5, obm.getChildren(owlClassConcept).size());
		assertSameList(obm.getChildren(owlClassConcept), restrictionConcept, complementOfProperty, disjointUnionOfProperty, hasKeyProperty,
				disjointWithProperty);

		assertEquals(9, obm.getChildren(propertyConcept).size());
		assertSameList(obm.getChildren(propertyConcept), annotationPropertyConcept, datatypePropertyConcept, objectPropertyConcept,
				ontologyPropertyConcept, domainProperty, subPropertyOfProperty, propertyDisjointWithProperty, equivalentPropertyProperty,
				rangeProperty);

		assertEquals(8, obm.getChildren(objectPropertyConcept).size());
		assertSameList(obm.getChildren(objectPropertyConcept), asymmetricPropertyConcept, inverseFunctionalPropertyConcept,
				irreflexivePropertyConcept, reflexivePropertyConcept, symmetricPropertyConcept, transitivePropertyConcept,
				inverseOfProperty, propertyChainAxiomProperty);

	}

	public void test9AssertOWLOntologyStrictMode() {
		log("test9AssertOWLOntologyStrictMode()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyClass thingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OntologyClass allDifferentConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OntologyClass annotationPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OntologyClass owlClassConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OntologyClass restrictionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OntologyClass datatypePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OntologyClass namedIndividualConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OntologyClass negativePropertyAssertionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OntologyClass nothingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OntologyClass objectPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OntologyClass asymmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OntologyClass inverseFunctionalPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OntologyClass irreflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OntologyClass reflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OntologyClass symmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OntologyClass transitivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OntologyClass ontologyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OntologyClass ontologyPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "OntologyProperty");
		assertNotNull(ontologyPropertyConcept);
		OntologyObjectProperty topObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "topObjectProperty");
		assertNotNull(topObjectProperty);
		OntologyObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OntologyDataProperty bottomDataProperty = owlOntology
				.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OntologyDataProperty topDataProperty = owlOntology.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);
		OntologyObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OntologyObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OntologyObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OntologyObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OntologyObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OntologyObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OntologyObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OntologyObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OntologyObjectProperty membersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OntologyObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OntologyObjectProperty oneOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OntologyObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OntologyObjectProperty unionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OntologyObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OntologyObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OntologyObjectProperty differentFromProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "differentFrom");
		assertNotNull(differentFromProperty);
		OntologyObjectProperty sameAsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OntologyObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OntologyObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OntologyObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OntologyObjectProperty complementOfProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OntologyObjectProperty disjointWithProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OntologyObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OntologyObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OntologyObjectProperty targetValueProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OntologyObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OntologyObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OntologyObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(owlOntology);
		obm.setStrictMode(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thingConcept);

		assertSameList(obm.getChildren(thingConcept), allDifferentConcept, annotationPropertyConcept, owlClassConcept,
				datatypePropertyConcept, namedIndividualConcept, negativePropertyAssertionConcept, nothingConcept, objectPropertyConcept,
				ontologyConcept, ontologyPropertyConcept, bottomDataProperty, topObjectProperty, topDataProperty, bottomObjectProperty,
				sameAsProperty, differentFromProperty);

		assertEquals(5, obm.getChildren(owlClassConcept).size());
		assertSameList(obm.getChildren(owlClassConcept), restrictionConcept, complementOfProperty, disjointUnionOfProperty, hasKeyProperty,
				disjointWithProperty);

		assertEquals(8, obm.getChildren(objectPropertyConcept).size());
		assertSameList(obm.getChildren(objectPropertyConcept), asymmetricPropertyConcept, inverseFunctionalPropertyConcept,
				irreflexivePropertyConcept, reflexivePropertyConcept, symmetricPropertyConcept, transitivePropertyConcept,
				inverseOfProperty, propertyChainAxiomProperty);

	}

	public void test10AssertFlexoConceptOntologyNoHierarchy() {
		log("test10AssertFlexoConceptOntologyNoHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		FlexoOntology flexoConceptsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology();
		assertNotNull(flexoConceptsOntology);

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyClass thingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OntologyClass allDifferentConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OntologyClass annotationPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OntologyClass owlClassConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OntologyClass restrictionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OntologyClass datatypePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OntologyClass namedIndividualConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OntologyClass negativePropertyAssertionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OntologyClass nothingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OntologyClass objectPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OntologyClass asymmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OntologyClass inverseFunctionalPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OntologyClass irreflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OntologyClass reflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OntologyClass symmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OntologyClass transitivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OntologyClass ontologyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OntologyClass ontologyPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "OntologyProperty");
		assertNotNull(ontologyPropertyConcept);
		OntologyObjectProperty topObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "topObjectProperty");
		assertNotNull(topObjectProperty);
		OntologyObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OntologyDataProperty bottomDataProperty = owlOntology
				.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OntologyDataProperty topDataProperty = owlOntology.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OntologyClass flexoConcept = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoModelObject = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoModelObject");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessFolder = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessFolder");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcess = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoRole = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessElement = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessElement");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoActivity = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoOperation = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoEvent = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyObjectProperty inRelationWithProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "inRelationWith");
		assertNotNull(inRelationWithProperty);
		OntologyObjectProperty linkedToModelProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToModel");
		assertNotNull(linkedToModelProperty);
		OntologyObjectProperty linkedToConceptProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToConcept");
		assertNotNull(linkedToConceptProperty);
		OntologyDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "resourceName");
		assertNotNull(resourceNameProperty);
		OntologyDataProperty classNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "className");
		assertNotNull(classNameProperty);
		OntologyDataProperty flexoIDProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "flexoID");
		assertNotNull(flexoIDProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(flexoConceptsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), flexoConceptsOntology);

		assertEquals(4, obm.getChildren(flexoConceptsOntology).size());
		assertSameList(obm.getChildren(flexoConceptsOntology), flexoConceptsOntology.getThingConcept(), owlOntology, rdfOntology,
				rdfsOntology);

		assertEquals(2, obm.getChildren(flexoConceptsOntology.getThingConcept()).size());
		assertSameList(obm.getChildren(flexoConceptsOntology.getThingConcept()), flexoConcept, flexoModelObject);

		assertEquals(2, obm.getChildren(flexoConcept).size());
		assertSameList(obm.getChildren(flexoConcept), inRelationWithProperty, linkedToModelProperty);

		assertEquals(8, obm.getChildren(flexoModelObject).size());
		assertSameList(obm.getChildren(flexoModelObject), flexoProcess, flexoProcessElement, flexoProcessFolder, flexoRole,
				resourceNameProperty, classNameProperty, linkedToConceptProperty, flexoIDProperty);

		assertEquals(3, obm.getChildren(flexoProcessElement).size());
		assertSameList(obm.getChildren(flexoProcessElement), flexoActivity, flexoEvent, flexoOperation);

	}

	public void test11AssertFlexoConceptOntologyHierarchy() {
		log("test11AssertFlexoConceptOntologyHierarchy()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		FlexoOntology flexoConceptsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology();
		assertNotNull(flexoConceptsOntology);

		OntologyClass listConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OntologyClass propertyConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OntologyClass statementConcept = rdfOntology.getClass(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty restProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OntologyObjectProperty firstProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OntologyObjectProperty subjectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OntologyObjectProperty predicateProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OntologyObjectProperty objectProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OntologyClass resourceConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OntologyClass classConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OntologyClass datatypeConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OntologyClass containerConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OntologyClass literalConcept = rdfOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(OntologyLibrary.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyClass thingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OntologyClass allDifferentConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OntologyClass annotationPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OntologyClass owlClassConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OntologyClass restrictionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OntologyClass datatypePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OntologyClass namedIndividualConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OntologyClass negativePropertyAssertionConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OntologyClass nothingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OntologyClass objectPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OntologyClass asymmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OntologyClass inverseFunctionalPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OntologyClass irreflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OntologyClass reflexivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OntologyClass symmetricPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OntologyClass transitivePropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OntologyClass ontologyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OntologyClass ontologyPropertyConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "OntologyProperty");
		assertNotNull(ontologyPropertyConcept);
		OntologyObjectProperty topObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "topObjectProperty");
		assertNotNull(topObjectProperty);
		OntologyObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OntologyDataProperty bottomDataProperty = owlOntology
				.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OntologyDataProperty topDataProperty = owlOntology.getDataProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OntologyObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OntologyObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OntologyObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OntologyObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OntologyObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OntologyObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OntologyObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OntologyObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OntologyObjectProperty membersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OntologyObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OntologyObjectProperty oneOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OntologyObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OntologyObjectProperty unionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OntologyObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OntologyObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OntologyObjectProperty differentFromProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "differentFrom");
		assertNotNull(differentFromProperty);
		OntologyObjectProperty sameAsProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OntologyObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OntologyObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OntologyObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OntologyObjectProperty complementOfProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OntologyObjectProperty disjointWithProperty = owlOntology
				.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OntologyObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OntologyObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OntologyObjectProperty targetValueProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OntologyObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OntologyObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OntologyObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyClass flexoConcept = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoModelObject = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoModelObject");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessFolder = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessFolder");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcess = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoRole = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessElement = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessElement");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoActivity = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoOperation = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoEvent = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyObjectProperty inRelationWithProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "inRelationWith");
		assertNotNull(inRelationWithProperty);
		OntologyObjectProperty linkedToModelProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToModel");
		assertNotNull(linkedToModelProperty);
		OntologyObjectProperty linkedToConceptProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToConcept");
		assertNotNull(linkedToConceptProperty);
		OntologyDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "resourceName");
		assertNotNull(resourceNameProperty);
		OntologyDataProperty classNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "className");
		assertNotNull(classNameProperty);
		OntologyDataProperty flexoIDProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "flexoID");
		assertNotNull(flexoIDProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(flexoConceptsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		OntologyClass thingConceptSeenFromFlexoConceptOntology = flexoConceptsOntology.getThingConcept();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thingConceptSeenFromFlexoConceptOntology);

		assertEquals(11, obm.getChildren(thingConceptSeenFromFlexoConceptOntology).size());
		assertSameList(obm.getChildren(thingConceptSeenFromFlexoConceptOntology), flexoConcept, flexoModelObject, namedIndividualConcept,
				nothingConcept, resourceConcept, topDataProperty, topObjectProperty, bottomDataProperty, bottomObjectProperty,
				sameAsProperty, differentFromProperty);

		assertEquals(2, obm.getChildren(flexoConcept).size());
		assertSameList(obm.getChildren(flexoConcept), inRelationWithProperty, linkedToModelProperty);

		assertEquals(8, obm.getChildren(flexoModelObject).size());
		assertSameList(obm.getChildren(flexoModelObject), flexoProcess, flexoProcessElement, flexoProcessFolder, flexoRole,
				resourceNameProperty, classNameProperty, linkedToConceptProperty, flexoIDProperty);

		assertEquals(3, obm.getChildren(flexoProcessElement).size());
		assertSameList(obm.getChildren(flexoProcessElement), flexoActivity, flexoEvent, flexoOperation);

	}

	public void test12AssertO5Ontology() {
		log("test12AssertO5Ontology()");
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

		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();

		OntologyClass resourceConcept = rdfsOntology.getClass(OntologyLibrary.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);

		OntologyClass namedIndividualConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OntologyClass nothingConcept = owlOntology.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);

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

		OntologyBrowserModel obm = new OntologyBrowserModel(o5);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setDisplayPropertiesInClasses(true);
		obm.setShowOWLAndRDFConcepts(false);
		obm.setShowClasses(true);
		obm.setShowIndividuals(false);
		obm.setShowObjectProperties(false);
		obm.setShowDataProperties(false);
		obm.setShowAnnotationProperties(false);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), o5.getThingConcept());

		assertSameList(obm.getChildren(o5.getThingConcept()), a2fromO5, b2, c2, b1);

		// Showing OWL, RDF and RDFS concepts should not change hierarchy
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), o5.getThingConcept());

		assertSameList(obm.getChildren(o5.getThingConcept()), a2fromO5, b2, c2, b1, namedIndividualConcept, nothingConcept, resourceConcept);

	}

}
