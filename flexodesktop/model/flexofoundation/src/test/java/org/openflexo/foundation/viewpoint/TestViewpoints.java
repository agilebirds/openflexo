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
package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.LocalResourceCenterImplementation;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.viewpoint.ViewPointObject.BindingIsRequiredAndMustBeValid;
import org.openflexo.toolbox.FileResource;

public class TestViewpoints extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoResourceCenter testResourceCenter;

	public TestViewpoints(String name) {
		super(name);
	}

	private void assertViewPointIsValid(ViewPoint vp) {

		assertNotNull(vp);

		vp.loadWhenUnloaded();

		ValidationReport report = vp.validate();

		for (ValidationError error : report.getErrors()) {
			System.out.println("Found error: " + error);
			if (error.getValidationRule() instanceof BindingIsRequiredAndMustBeValid) {
				BindingIsRequiredAndMustBeValid rule = (BindingIsRequiredAndMustBeValid) error.getValidationRule();
				System.out.println("Details: " + rule.retrieveIssueDetails((ViewPointObject) error.getObject()));
			}
		}

		assertEquals(0, report.getErrorNb());

	}

	/**
	 * Instanciate new ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testResourceCenter = new LocalResourceCenterImplementation(new FileResource("TestResourceCenter"), false);

		System.out.println(testResourceCenter.retrieveViewPointLibrary().getViewPoints());
	}

	public void test1BasicOntologyEditor() {
		log("test1BasicOntologyEditor()");

		ViewPoint basicOntologyEditor = testResourceCenter.retrieveViewPointLibrary().getViewPoint(
				"http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
		assertNotNull(basicOntologyEditor);

		/*System.out.println("basicOntologyEditor=" + basicOntologyEditor);
		System.out.println("ontology=" + basicOntologyEditor.getViewpointOntology());
		System.out.println("loaded=" + basicOntologyEditor.getViewpointOntology().isLoaded());
		for (OntologyClass c : basicOntologyEditor.getViewpointOntology().getClasses()) {
			System.out.println("> Class " + c);
		}*/

		basicOntologyEditor.loadWhenUnloaded();

		EditionPattern conceptEP = basicOntologyEditor.getEditionPattern("Concept");
		assertNotNull(conceptEP);

		DropScheme ds = (DropScheme) conceptEP.getEditionScheme("DropConceptAtTopLevel");
		assertNotNull(ds);

		assertViewPointIsValid(basicOntologyEditor);

	}

	public void test2BDN() {
		log("test2BDN()");

		assertViewPointIsValid(testResourceCenter.retrieveViewPointLibrary().getViewPoint(
				"http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/BenefitDependancyNetwork.owl"));

	}

	public void test3OrganizationalChart() {
		log("test3OrganizationalChart()");

		assertViewPointIsValid(testResourceCenter.retrieveViewPointLibrary().getViewPoint(
				"http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalChart.owl"));

	}

	public void test4OrganizationalMap() {
		log("test4OrganizationalMap()");

		assertViewPointIsValid(testResourceCenter.retrieveViewPointLibrary().getViewPoint(
				"http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl"));

	}

	public void test5OrganizationalUnitDefinition() {
		log("test5OrganizationalUnitDefinition()");

		assertViewPointIsValid(testResourceCenter.retrieveViewPointLibrary().getViewPoint(
				"http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl"));

	}

	public void test6SKOS() {
		log("test6SKOS()");

		assertViewPointIsValid(testResourceCenter.retrieveViewPointLibrary().getViewPoint(
				"http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl"));

	}

}
