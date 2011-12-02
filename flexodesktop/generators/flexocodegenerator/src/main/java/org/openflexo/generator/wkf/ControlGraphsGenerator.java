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
package org.openflexo.generator.wkf;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.exec.ControlGraphFactories;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class ControlGraphsGenerator extends MetaGenerator<FlexoModelObject, CGRepository> {
	private static final Logger logger = Logger.getLogger(ControlGraphsGenerator.class.getPackage().getName());

	public ControlGraphsGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, null);
		_generators = new Hashtable<FlexoProcess, ControlGraphGenerator>();
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public void generate(boolean forceRegenerate) throws GenerationException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called ComponentsGenerator.generate(forceRegenerate)");
		}
		resetSecondaryProgressWindow(_generators.values().size());
		startGeneration();
		for (ControlGraphGenerator generator : _generators.values()) {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + generator.getProcess().getName(), false);
			generator.generate(forceRegenerate);
		}
		stopGeneration();
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		ControlGraphFactories.init();
		Vector<FlexoProcess> allProcesses = getProject().getFlexoWorkflow().getAllLocalFlexoProcesses();
		resetSecondaryProgressWindow(allProcesses.size());
		for (FlexoProcess p : allProcesses) {
			if (p.getExecutionClassName() == null) {
				continue;
			}
			ControlGraphGenerator generator = getGenerator(p);
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + p.getName(), false);
			if (generator != null) {
				generator.buildResourcesAndSetGenerators(repository, resources);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate ControlGraph Generator for " + p.getName());
				}
			}
		}
	}

	private Hashtable<FlexoProcess, ControlGraphGenerator> _generators;

	protected ControlGraphGenerator getGenerator(FlexoProcess process) {
		ControlGraphGenerator returned = _generators.get(process);
		if (returned == null) {
			_generators.put(process, returned = new ControlGraphGenerator(getProjectGenerator(), process));
		}
		return returned;
	}
}
