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
package org.openflexo.foundation.sg;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

/**
 * @author sylvain
 * 
 */
public class SourceRepository extends GenerationRepository implements ReferenceOwner {

	private static final Logger logger = FlexoLogger.getLogger(SourceRepository.class.getPackage().getName());

	private CodeType target;

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public SourceRepository(GeneratedSourcesBuilder builder) {
		this(builder.generatedSources);
		initializeDeserialization(builder);
	}

	/**
	 * @throws DuplicateCodeRepositoryNameException
	 * 
	 */
	public SourceRepository(GeneratedSources generatedSources, String name, File directory) throws DuplicateCodeRepositoryNameException {
		super(generatedSources, name, directory);
		target = new CodeType(generatedSources.getProject()) {
			@Override
			public Vector<Format> getAvailableFormats() {
				return null;
			}

			@Override
			public String getName() {
				return "CONFIGURABLE";
			}

			@Override
			public String getTemplateFolderName() {
				return null;
			}
		};
	}

	/**
	 * @param generatedCode
	 */
	public SourceRepository(GeneratedSources generatedSources) {
		super(generatedSources);
		target = new CodeType(generatedSources.getProject()) {
			@Override
			public Vector<Format> getAvailableFormats() {
				return null;
			}

			@Override
			public String getName() {
				return "CONFIGURABLE";
			}

			@Override
			public String getTemplateFolderName() {
				return null;
			}
		};
	}

	@Override
	public void delete(FlexoProgress progress, boolean deleteFiles) {
		super.delete(progress, deleteFiles);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	@Override
	public boolean connect() {
		return super.connect();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "generated_sources_repository";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.SG.SOURCE_REPOSITORY_INSPECTOR;
	}

	@Override
	protected void deleteExternalRepositories() {
		super.deleteExternalRepositories();
	}

	public void clearAllJavaParsingData() {
		for (CGFile file : getFiles()) {
			file.clearParsingData();
		}
		getProject().getDataModel().getClassLibrary().clearLibrary();
	}

	@Override
	public void update(FlexoObservable observable, DataModification obj) {
		super.update(observable, obj);
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {

	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {

	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference reference) {
		setChanged();
	}

	// Should not be used anymore !
	@Override
	public Format getFormat() {
		return null;
	}

	// Should not be used anymore !
	@Override
	public void setFormat(Format format) {
	}

	// Should not be used anymore !
	@Override
	public CodeType getTarget() {
		return null;
	}

	@Override
	public GeneratedSources getGeneratedCode() {
		return (GeneratedSources) super.getGeneratedCode();
	}

	public GeneratedSources getGeneratedSources() {
		return getGeneratedCode();
	}

	private String _implementationModelName;
	private ImplementationModel _implementationModel = null;

	// Serialization only
	public String _getImplementationModelName() {
		return _implementationModelName;
	}

	// Serialization only
	public void _setImplementationModelName(String implementationModelName) {
		_implementationModelName = implementationModelName;
		_implementationModel = null;
	}

	public ImplementationModel getImplementationModel() {
		if (_implementationModel == null && StringUtils.isNotEmpty(_implementationModelName)) {
			_implementationModel = getGeneratedSources().getImplementationModelNamed(_implementationModelName).getImplementationModel();
		}
		return _implementationModel;
	}

	public void setImplementationModel(ImplementationModel anImplementationModel) {
		_implementationModelName = anImplementationModel.getName();
		_implementationModel = anImplementationModel;
	}

}
