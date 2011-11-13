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
package org.openflexo.generator.ie;

import java.util.Vector;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;

/**
 * @author gpolet
 * 
 */
public class PopupLinkComponentGenerator extends ComponentGenerator {

	/**
	 * @param projectGenerator
	 * @param componentDefinition
	 * @param componentGeneratedName
	 */
	public PopupLinkComponentGenerator(ProjectGenerator projectGenerator, PopupComponentDefinition componentDefinition) {
		super(projectGenerator, componentDefinition, nameForPopupLink(componentDefinition));
	}

	@Override
	public PopupComponentDefinition getComponentDefinition() {
		return (PopupComponentDefinition) super.getComponentDefinition();
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// Java
		javaResource = GeneratedFileResourceFactory.createNewPopupLinkComponentJavaFileResource(repository, this);
		resources.add(javaResource);

		// WO
		woResource = GeneratedFileResourceFactory.createNewPopupLinkComponentWOFileResource(repository, this);
		;
		resources.add(woResource);

		// API
		apiResource = GeneratedFileResourceFactory.createNewPopupLinkComponentAPIFileResource(repository, this);
		resources.add(apiResource);
	}

	@Override
	protected VelocityContext defaultContext() {
		VelocityContext vc = super.defaultContext();
		vc.put("isLink", Boolean.TRUE);
		return vc;
	}

	@Override
	public String getJavaTemplate() {
		return "PopupLink.java.vm";
	}

	@Override
	public String getWodTemplate() {
		return "PopupLink.wod.vm";
	}

	@Override
	public String getHtmlTemplate() {
		return "PopupLink.html.vm";
	}

	@Override
	public String getApiTemplate() {
		return "PopupLink.api.vm";
	}
}
