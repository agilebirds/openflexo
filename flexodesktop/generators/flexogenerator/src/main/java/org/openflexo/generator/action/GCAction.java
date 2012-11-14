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
package org.openflexo.generator.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GeneratedOutput.GeneratorFactory;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.file.AbstractCGFile;

public abstract class GCAction<A extends GCAction<A, T1>, T1 extends CGObject> extends AbstractGCAction<A, T1> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GCAction.class.getPackage().getName());

	public static interface ProjectGeneratorFactory extends GeneratorFactory {
		public AbstractProjectGenerator<? extends GenerationRepository> generatorForRepository(GenerationRepository repository);
	}

	public static ProjectGeneratorFactory getProjectGeneratorFactory(GenerationRepository repository) {
		return (ProjectGeneratorFactory) repository.getGeneratedCode().getFactory();
	}

	private AbstractProjectGenerator<? extends GenerationRepository> _projectGenerator;

	protected GCAction(FlexoActionType<A, T1, CGObject> actionType, T1 focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public GenerationRepository getRepository() {
		return getRepository(getFocusedObject(), getGlobalSelection());
	}

	public AbstractProjectGenerator<? extends GenerationRepository> getProjectGenerator() {
		GenerationRepository repository = getRepository();
		if (_projectGenerator == null && repository != null) {
			if (repository.getProjectGenerator() instanceof AbstractProjectGenerator) {
				_projectGenerator = (AbstractProjectGenerator<? extends GenerationRepository>) repository.getProjectGenerator();
			}
			if (_projectGenerator == null) {
				if (getProjectGeneratorFactory(repository) != null) {
					_projectGenerator = getProjectGeneratorFactory(repository).generatorForRepository(getRepository());
				}
			}
		}
		if (_projectGenerator != null) {
			_projectGenerator.setAction(this);
		}
		return _projectGenerator;
	}

	public static AbstractProjectGenerator<? extends GenerationRepository> getProjectGenerator(GenerationRepository repository) {
		if (repository == null) {
			return null;
		}
		return getProjectGeneratorFactory(repository) != null ? getProjectGeneratorFactory(repository).generatorForRepository(repository)
				: null;
	}

	public void setProjectGenerator(AbstractProjectGenerator<? extends GenerationRepository> aProjectGenerator) {
		_projectGenerator = aProjectGenerator;
	}

	/**
	 * Return all AbstractCGFile concerned by current selection
	 * 
	 * @return
	 */
	protected static Vector<AbstractCGFile> getSelectedCGFiles(CGObject focusedObject, Vector<CGObject> globalSelection) {
		return getSelectedCGFiles(getRepository(focusedObject, globalSelection), getSelectedTopLevelObjects(focusedObject, globalSelection));
	}

	/**
	 * Return all AbstractCGFile concerned by current selection
	 * 
	 * @return
	 */
	protected static Vector<AbstractCGFile> getSelectedCGFiles(GenerationRepository repository, Vector<CGObject> selectedTopLevelObject) {
		Vector<AbstractCGFile> returned = new Vector<AbstractCGFile>();
		for (CGFile file : repository.getFiles()) {
			for (CGObject obj : selectedTopLevelObject) {
				if (obj.contains(file) && file instanceof AbstractCGFile) {
					returned.add((AbstractCGFile) file);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all AbstractCGFile concerned by current selection
	 * 
	 * @return
	 */
	protected Vector<AbstractCGFile> getSelectedCGFiles() {
		return getSelectedCGFiles(getRepository(), getSelectedTopLevelObjects());
	}

	private boolean _saveBeforeGenerating = true;

	public boolean getSaveBeforeGenerating() {
		return _saveBeforeGenerating;
	}

	public void setSaveBeforeGenerating(boolean saveBeforeGenerating) {
		_saveBeforeGenerating = saveBeforeGenerating;
	}

}
