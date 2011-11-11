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
package org.openflexo.foundation.dm.eo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.eo.model.EOProperty;
import org.openflexo.foundation.dm.javaparser.ParsedJavaClass;
import org.openflexo.foundation.dm.javaparser.ParsedJavaField;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

/**
 * Represents an accessor as a get/set key-value pair mapping a data stored in a database
 * 
 * @author sguerin
 * 
 */
public abstract class DMEOProperty extends DMProperty implements DMEOObject {

	private static final Logger logger = Logger.getLogger(DMEOProperty.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	public String columnName;

	public String prototypeName;

	protected EOProperty eoProperty;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMEOProperty(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMEOProperty(DMModel dmModel) {
		super(dmModel);
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (getDMRepository().isReadOnly())
			return Inspectors.DM.DM_RO_EO_PROPERTY_INSPECTOR;
		else
			return Inspectors.DM.DM_EO_PROPERTY_INSPECTOR;
	}

	public DMEOEntity getDMEOEntity() {
		return (DMEOEntity) getEntity();
	}

	@Override
	public DMEOModel getDMEOModel() {
		if (getDMEOEntity() != null) {
			return getDMEOEntity().getDMEOModel();
		}
		return null;
	}

	@Override
	public void delete() {
		if (getDMEOEntity() != null)
			getDMEOEntity().removePropertyWithKey(getName());
		super.delete();
	}

	public DMEORepository getDMEORepository() {
		return (DMEORepository) getDMRepository();
	}

	public abstract EOProperty getEOProperty();

	public boolean getIsClassProperty() {
		if (getDMEOEntity() != null) {
			return getDMEOEntity().getClassProperties().contains(this);
		}
		return false;
	}

	public void setIsClassProperty(boolean aBoolean) {
		if (getIsClassProperty() != aBoolean) {
			if ((getDMEOEntity() != null) && (getDMEOEntity().getEOEntity() != null) && (getEOProperty() != null)) {
				List<EOProperty> arrayOfClassProperties = getDMEOEntity().getEOEntity().getClassProperties();
				if (aBoolean) {
					arrayOfClassProperties.add(getEOProperty());
				} else {
					arrayOfClassProperties.remove(getEOProperty());
				}
				getDMEOEntity().rebuildClassProperties();
				getDMEOEntity().setPropertiesNeedsReordering();
				setChanged();
				notifyObservers(new DMAttributeDataModification("isClassProperty", new Boolean(!aBoolean), new Boolean(aBoolean)));
			}
		}
	}

	@Override
	public boolean getIsStatic() {
		return false;
	}

	@Override
	public void setIsStatic(boolean isStatic) {
		// Not allowed
	}

	@Override
	public boolean mustGenerateCode() {
		return getIsClassProperty();
	}

	/**
	 * Overrides getIsStaticallyDefinedInTemplate
	 * 
	 * @see org.openflexo.foundation.dm.DMProperty#getIsStaticallyDefinedInTemplate()
	 */
	@Override
	public boolean getIsStaticallyDefinedInTemplate() {
		// SGU: NO. This is the contrary.
		// DMEOProperties are never statically defined in template, and thus, always available for edition
		// GPO: Well, now that you have changed everything, of course, it is changed, but before it was mandatory that this method returned
		// true, trust me.
		return false;
	}

	/*public boolean getIsStaticallyDefinedInTemplate()
	{
	    // GPO: EOAttributes and EORelationship are defined in templates and
	    // require a specific code which is not very compatible with the actual
	    // model.
	    return true;
	}*/

	@Override
	public DMPropertyImplementationType getImplementationType() {
		return DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY;
	}

	// =============================================================
	// ===================== Code generation =======================
	// =============================================================

	@Override
	public String getCodeGenerationNotApplicableLabel() {
		if (getDMRepository() != null && getDMRepository() == getDMModel().getExecutionModelRepository()) {
			return FlexoLocalization.localizedForKey("built_in_code_embedded_in_run_time_libraries");
		}
		if (!getDMModel().getEOCodeGenerationAvailable()) {
			return FlexoLocalization.localizedForKey("sorry_EO_code_generation_is_not_available_in_this_flexo_edition");
		} else if (!getDMModel().getEOCodeGenerationActivated()) {
			return FlexoLocalization.localizedForKey("sorry_EO_code_generation_is_not_activated_for_this_project");
		}
		return "???";
	}

	public boolean isCodeGenerationAvailable() {
		if (getDMModel() != null)
			return getDMModel().getEOCodeGenerationAvailable();
		return false;
	}

	public boolean isCodeGenerationActivated() {
		if (getDMModel() != null)
			return getDMModel().getEOCodeGenerationActivated();
		return false;
	}

	public void activateEOCodeGeneration() {
		if (getDMModel() != null)
			getDMModel().activateEOCodeGeneration();
	}

	public void desactivateEOCodeGeneration() {
		if (getDMModel() != null)
			getDMModel().desactivateEOCodeGeneration();
	}

	@Override
	public void setIsModified() {
		// When a DMEOProperty is modified, reset needsRegeneration flag
		codeNeedsToBeRegenerated = true;
		super.setIsModified();
	}

	public void codeGenerationHasBeenActivated() {
		setIsModified();
		updateCode();
	}

	private boolean codeNeedsToBeRegenerated = true;
	private ParsedJavaField generatedCodeField = null;
	private ParsedJavaMethod generatedCodeGetter = null;
	private ParsedJavaMethod generatedCodeSetter = null;
	private ParsedJavaMethod generatedCodeAdditionMethod = null;
	private ParsedJavaMethod generatedCodeRemovalMethod = null;

	private void regenerateCode() {
		generatedCodeField = null;
		generatedCodeGetter = null;
		generatedCodeSetter = null;
		generatedCodeAdditionMethod = null;
		generatedCodeRemovalMethod = null;

		if (getDMRepository() != null && getDMRepository() == getDMModel().getExecutionModelRepository()) {
			return;
		}

		if (getDMRepository() != null && getDMRepository() == getDMModel().getEOPrototypeRepository()) {
			return;
		}

		try {
			if (getDMEOEntity().getGeneratedCode() != null) {
				if (getDMEOEntity().getGeneratedCode().getParsedClass() != null) {
					if (getImplementationType().requiresField()) {
						generatedCodeField = getDMEOEntity().getGeneratedCode().getParsedClass().getFieldByName(getFieldName());
						if (logger.isLoggable(Level.FINE))
							logger.fine("Tried to retrieve field " + getFieldName() + " FOUND: " + generatedCodeField);
					}
					if (getImplementationType().requiresAccessors()) {
						generatedCodeGetter = searchMethod(getDMEOEntity().getGeneratedCode().getParsedClass(),
								getGetterSignatureCandidates());
						if (logger.isLoggable(Level.FINE))
							logger.fine("Tried to retrieve getter, FOUND: " + generatedCodeGetter);
						if (getIsSettable()) {
							generatedCodeSetter = searchMethod(getDMEOEntity().getGeneratedCode().getParsedClass(),
									getSetterSignatureCandidates());
							if (logger.isLoggable(Level.FINE))
								logger.fine("Tried to retrieve setter, FOUND: " + generatedCodeSetter);
							if (getCardinality().isMultiple()) {
								generatedCodeAdditionMethod = searchMethod(getDMEOEntity().getGeneratedCode().getParsedClass(),
										getAdditionAccessorSignatureCandidates());
								if (logger.isLoggable(Level.FINE))
									logger.fine("Tried to retrieve addition method, FOUND: " + generatedCodeAdditionMethod);
								generatedCodeRemovalMethod = searchMethod(getDMEOEntity().getGeneratedCode().getParsedClass(),
										getRemovalAccessorSignatureCandidates());
								if (logger.isLoggable(Level.FINE))
									logger.fine("Tried to retrieve removal method, FOUND: " + generatedCodeRemovalMethod);
							}
						}
					}
					codeNeedsToBeRegenerated = false;
				} else {
					logger.warning("Could not parse generated code in DMEOEntity");
				}
			} else {
				logger.warning("Could not access to generated code in DMEOEntity");
			}
		} catch (ParserNotInstalledException e) {
			logger.warning("JavaParser not installed !");
		}
	}

	private static ParsedJavaMethod searchMethod(ParsedJavaClass aParsedClass, String[] signatureCandidates) {
		for (String testThisSignature : signatureCandidates) {
			ParsedJavaMethod tryThis = aParsedClass.getMethodBySignature(testThisSignature);
			if (tryThis != null)
				return tryThis;
		}
		return null;
	}

	@Override
	protected String getFieldDefaultInitializationExpression() {
		if (getDMModel().getEOEntityCodeGenerator() == null || getDMEOEntity() == null) {
			return "{" + StringUtils.LINE_SEPARATOR + "// EOEntityCodeGenerator not installed: code not available"
					+ StringUtils.LINE_SEPARATOR + "}";
		} else {
			if (!isCodeGenerationActivated())
				return "{" + StringUtils.LINE_SEPARATOR + "// EOF code generation not activated" + StringUtils.LINE_SEPARATOR + "}";
			if (codeNeedsToBeRegenerated)
				regenerateCode();
			if (generatedCodeField != null)
				return generatedCodeField.getInitializationExpression();
			return "null; // WARNING: could not retrieve initialization expression for field";
		}
	}

	@Override
	protected String getGetterDefaultCoreCode() {
		if (getDMModel().getEOEntityCodeGenerator() == null || getDMEOEntity() == null) {
			return "{" + StringUtils.LINE_SEPARATOR + "// EOEntityCodeGenerator not installed: code not available"
					+ StringUtils.LINE_SEPARATOR + "}";
		} else {
			if (!isCodeGenerationActivated())
				return "{" + StringUtils.LINE_SEPARATOR + "// EOF code generation not activated" + StringUtils.LINE_SEPARATOR + "}";
			if (codeNeedsToBeRegenerated)
				regenerateCode();
			if (generatedCodeGetter != null)
				return generatedCodeGetter.getCoreCode();
			return "{" + StringUtils.LINE_SEPARATOR + "// WARNING: could not retrieve generated code for getter"
					+ StringUtils.LINE_SEPARATOR + "}";
		}
	}

	@Override
	protected String getSetterDefaultCoreCode() {
		if (getDMModel().getEOEntityCodeGenerator() == null || getDMEOEntity() == null) {
			return "{" + StringUtils.LINE_SEPARATOR + "// EOEntityCodeGenerator not installed: code not available"
					+ StringUtils.LINE_SEPARATOR + "}";
		} else {
			if (!isCodeGenerationActivated())
				return "{" + StringUtils.LINE_SEPARATOR + "// EOF code generation not activated" + StringUtils.LINE_SEPARATOR + "}";
			if (codeNeedsToBeRegenerated)
				regenerateCode();
			if (generatedCodeSetter != null)
				return generatedCodeSetter.getCoreCode();
			return "{" + StringUtils.LINE_SEPARATOR + "// WARNING: could not retrieve generated code for setter"
					+ StringUtils.LINE_SEPARATOR + "}";
		}
	}

	@Override
	protected String getAdditionAccessorDefaultCoreCode() {
		if (getDMModel().getEOEntityCodeGenerator() == null || getDMEOEntity() == null) {
			return "{" + StringUtils.LINE_SEPARATOR + "// EOEntityCodeGenerator not installed: code not available"
					+ StringUtils.LINE_SEPARATOR + "}";
		} else {
			if (!isCodeGenerationActivated())
				return "{" + StringUtils.LINE_SEPARATOR + "// EOF code generation not activated" + StringUtils.LINE_SEPARATOR + "}";
			if (codeNeedsToBeRegenerated)
				regenerateCode();
			if (generatedCodeAdditionMethod != null)
				return generatedCodeAdditionMethod.getCoreCode();
			return "{" + StringUtils.LINE_SEPARATOR + "// WARNING: could not retrieve generated code for addition method"
					+ StringUtils.LINE_SEPARATOR + "}";
		}
	}

	@Override
	protected String getRemovalAccessorDefaultCoreCode() {
		if (getDMModel().getEOEntityCodeGenerator() == null || getDMEOEntity() == null) {
			return "{" + StringUtils.LINE_SEPARATOR + "// EOEntityCodeGenerator not installed: code not available"
					+ StringUtils.LINE_SEPARATOR + "}";
		} else {
			if (!isCodeGenerationActivated())
				return "{" + StringUtils.LINE_SEPARATOR + "// EOF code generation not activated" + StringUtils.LINE_SEPARATOR + "}";
			if (codeNeedsToBeRegenerated)
				regenerateCode();
			if (generatedCodeRemovalMethod != null)
				return generatedCodeRemovalMethod.getCoreCode();
			return "{" + StringUtils.LINE_SEPARATOR + "// WARNING: could not retrieve generated code for removal method"
					+ StringUtils.LINE_SEPARATOR + "}";
		}
	}

	// Following methods are overriden to be conform with EOF template generation
	// Note that this is not conform to the norm provided by Flexo DM-model.
	// Anyway, take care to have template synchronized this this code !!!!!!!!!

	@Override
	public String getGetterName() {
		return (getIsUnderscoredAccessors() ? "_" : "") + getName();
	}

	@Override
	public String getNameAsMethodArgument() {
		return "value";
	}

}
