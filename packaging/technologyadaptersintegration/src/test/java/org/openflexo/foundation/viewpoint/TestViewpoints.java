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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.viewpoint.ViewPointObject.ViewPointObjectImpl.BindingIsRequiredAndMustBeValid;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;

public class TestViewpoints extends ViewPointTestCase {

	protected static final Logger logger = Logger.getLogger(ViewPointTestCase.class.getPackage().getName());

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// TODO: create a project where all those tests don't need a manual import of projects
		// TODO: copy all test VP in tmp dir and work with those VP instead of polling GIT workspace
		instanciateTestServiceManager();

	}

	private ViewPoint testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		System.out.println("resourceCenter=" + resourceCenter);
		System.out.println("resourceCenter.getViewPointRepository()=" + resourceCenter.getViewPointRepository());

		ViewPointResource vpRes = resourceCenter.getViewPointRepository().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		ViewPoint vp = vpRes.getViewPoint();
		assertTrue(vpRes.isLoaded());

		return vp;

	}

	private void assertViewPointIsValid(ViewPoint vp) {

		log("Testing ViewPoint" + vp.getURI());

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

	public void test1BasicOntologyEditor() {
		log("test1BasicOntologyEditor()");

		ViewPoint basicOntologyEditor = testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
		assertNotNull(basicOntologyEditor);

		System.out.println("Read resource " + basicOntologyEditor.getResource().getFile().getAbsolutePath());

		EditionPattern conceptEP = basicOntologyEditor.getDefaultDiagramSpecification().getEditionPattern("Concept");

		for (EditionPattern ep : basicOntologyEditor.getDefaultDiagramSpecification().getEditionPatterns()) {
			System.out.println("ep=" + ep);
		}

		assertNotNull(conceptEP);

		DropScheme ds = (DropScheme) conceptEP.getEditionScheme("DropConceptAtTopLevel");
		assertNotNull(ds);

		assertViewPointIsValid(basicOntologyEditor);

	}

	public void test2BDN() {
		log("test2BDN()");

		assertViewPointIsValid(testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/BenefitDependancyNetwork.owl"));

	}

	public void test3OrganizationalChart() {
		log("test3OrganizationalChart()");

		assertViewPointIsValid(testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalChart.owl"));

	}

	public void test4OrganizationalMap() {
		log("test4OrganizationalMap()");

		assertViewPointIsValid(testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl"));

	}

	public void test5OrganizationalUnitDefinition() {
		log("test5OrganizationalUnitDefinition()");

		assertViewPointIsValid(testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl"));

	}

	public void test6SKOS() {
		log("test6SKOS()");

		assertViewPointIsValid(testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl"));

	}

}
