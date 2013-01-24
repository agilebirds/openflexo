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
package org.openflexo.generator;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CGTemplateSet;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.rm.FlexoMemoryResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.logging.FlexoLogger;

/**
 * This class is intended to be used by generators. As template status are involved in dependancies computation, this class is declared as a
 * MemoryResource.
 * 
 * @author Guillaume Polet, Sylvain Guerin
 * 
 */
public class TemplateLocator extends FlexoMemoryResource {

	private static final Logger logger = FlexoLogger.getLogger(TemplateLocator.class.getPackage().getName());

	private FlexoProject project;

	private TargetType target;

	private Vector<CGTemplateSet> _templateDirectories;

	private Hashtable<String, CGTemplate> _templateTable;

	private CGTemplates _templates;

	private AbstractProjectGenerator _projectGenerator;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public TemplateLocator(FlexoProjectBuilder builder) {
		super(builder.project, builder.serviceManager);
	}

	public TemplateLocator(CGTemplates templates, AbstractProjectGenerator projectGenerator) {
		super(templates.getProject(), templates.getProject().getServiceManager());
		_templateDirectories = null;
		_templateTable = new Hashtable<String, CGTemplate>();
		_templates = templates;
		_projectGenerator = projectGenerator;
		rebuildDependancies();
	}

	public void notifyTemplateModified() {
		logger.info("********* Clear TemplateLocator cache !!!!!!!!!!! for " + getFullyQualifiedName());
		_templateTable.clear();
		_templateDirectories = null;
		lastUpdate = new Date();
	}

	public CGTemplate templateWithName(String templateRelativePath) throws TemplateNotFoundException {
		if (templateRelativePath.startsWith("/")) {
			templateRelativePath = templateRelativePath.substring(1);
		}

		CGTemplate returned = _templateTable.get(templateRelativePath);
		if (returned == null) {
			returned = searchForTemplate(templateRelativePath);
			if (logger.isLoggable(Level.INFO)) {
				logger.info(templateRelativePath + "=" + returned.getRelativePath() + "[" + returned.getSet().getName() + "]");
			}
			_templateTable.put(templateRelativePath, returned);
		}
		return returned;
	}

	@SuppressWarnings("unchecked")
	private CGTemplate searchForTemplate(String templateRelativePath) throws TemplateNotFoundException {
		Enumeration<CGTemplateSet> en = templateDirectories().elements();
		CGTemplateSet set = null;
		while (en.hasMoreElements()) {
			set = en.nextElement();
			// logger.info("search in "+set.getDirectory().getAbsolutePath());
			CGTemplate answer = set.getTemplate(templateRelativePath);
			if (answer != null) {
				return answer;
			}
		}
		// Template not found : build explicit error message.
		en = templateDirectories().elements();
		StringBuffer buffer = new StringBuffer();
		buffer.append("Searched directories are:\n");
		logger.info("Templates not found :" + templateRelativePath);
		throw new TemplateNotFoundException(templateRelativePath, buffer.toString(), _projectGenerator);
	}

	/**
	 * search order is : - prj/Templates/GENERATION_TARGET - prj/Templates - Flexo/Config/Generator/Templates/GENERATION_TARGET -
	 * Flexo/Config/Generator/Templates/
	 */
	private Vector<CGTemplateSet> templateDirectories() {
		if (_templateDirectories == null || this.project != _projectGenerator.getProject() || this.target != _projectGenerator.getTarget()) {
			this.project = _projectGenerator.getProject();
			this.target = _projectGenerator.getTarget();
			_templateDirectories = new Vector<CGTemplateSet>();
			CustomCGTemplateRepository customRepository = _projectGenerator.getRepository() != null ? _projectGenerator.getRepository()
					.getPreferredTemplateRepository() : null;
			if (customRepository != null) {
				if (target != null && customRepository.getTemplateSetForTarget(target) != null) {
					_templateDirectories.add(customRepository.getTemplateSetForTarget(target));
				}
				_templateDirectories.add(customRepository.getCommonTemplates());
			}

			CGTemplateRepository applicationRepository = _templates.getApplicationRepository();
			if (target != null && applicationRepository.getTemplateSetForTarget(target) != null) {
				_templateDirectories.add(applicationRepository.getTemplateSetForTarget(target));
			}
			_templateDirectories.add(applicationRepository.getCommonTemplates());
		}
		return _templateDirectories;
	}

	private Date lastUpdate = new Date();

	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String getName() {
		if (_projectGenerator == null || _projectGenerator.getRepository() == null) {
			return null;
		}
		if (_projectGenerator.getTarget() != null) {
			return _projectGenerator.getTarget().getName() + "." + _projectGenerator.getRepository().getName();
		}
		return _projectGenerator.getRepository().getName();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.CG_TEMPLATES;
	}

	@SuppressWarnings("unchecked")
	public boolean needsUpdateForResource(GenerationAvailableFileResource resource) {
		Date requestDate = resource.getLastUpdate();
		return needsUpdateForGenerator(requestDate,
				(Generator<? extends FlexoModelObject, ? extends GenerationRepository>) resource.getGenerator());
	}

	/**
	 * @param requestDate
	 * @param generator
	 * @return
	 */
	public boolean needsUpdateForGenerator(Date requestDate, Generator<? extends FlexoModelObject, ? extends GenerationRepository> generator) {
		if (generator == null) {
			return false;
		}
		for (CGTemplate template : generator.getUsedTemplates()) {
			if (template.getLastUpdate().after(requestDate)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("template " + template + "/" + template.getLastUpdate() + " AFTER " + requestDate);
				}
				return true;
			}
		}
		return false;
	}

	public boolean needsRegenerationBecauseOfTemplateChange(Generator<? extends FlexoModelObject, ? extends GenerationRepository> generator) {
		if (generator == null) {
			return false;
		}
		for (CGTemplate template : generator.getUsedTemplates()) {
			try {
				CGTemplate file = searchForTemplate(template.getRelativePathWithoutSetPrefix());
				if (file != template) {
					return true;
				}
			} catch (TemplateNotFoundException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Template not found: " + template.getTemplateName());
				}
			}
		}
		return false;
	}

}
