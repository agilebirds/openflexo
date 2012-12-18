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
import org.openflexo.foundation.resource.UserResourceCenter;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.ViewPointObject.BindingIsRequiredAndMustBeValid;

public class TestViewpoints extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static UserResourceCenter resourceCenter;
	private static ViewPointLibrary viewPointLibrary;

	public TestViewpoints(String name) {
		super(name);
	}

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager();

	}

	private ViewPoint testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

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

		EditionPattern conceptEP = basicOntologyEditor.getEditionPattern("Concept");
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
