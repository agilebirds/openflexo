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
package org.openflexo.foundation.dm;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMEntity.DMTypeVariable;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMMethodNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.dm.MethodDeleted;
import org.openflexo.foundation.dm.javaparser.MethodSourceCode;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod;
import org.openflexo.foundation.dm.javaparser.ParsedJavadoc;
import org.openflexo.foundation.dm.javaparser.ParsedJavadocItem;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.dm.javaparser.SourceCodeOwner;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a method (aka action or execution primitive) related to an entity
 * 
 * @author sguerin
 * 
 */
public class DMMethod extends DMObject implements Typed, DMGenericDeclaration, DMTypeOwner, DMMember, SourceCodeOwner {

	static final Logger logger = Logger.getLogger(DMMethod.class.getPackage().getName());

	private static final String VOID_DEFAULT_CODE = "    //TODO: Implement this method";
	private static final String DOUBLE_DEFAULT_CODE = "    //TODO: Implement this method" + StringUtils.LINE_SEPARATOR + "    return 0.0d;";
	private static final String FLOAT_DEFAULT_CODE = "    //TODO: Implement this method" + StringUtils.LINE_SEPARATOR + "    return 0.0f;";
	private static final String INT_DEFAULT_CODE = "    //TODO: Implement this method" + StringUtils.LINE_SEPARATOR + "    return 0;";
	private static final String CHAR_DEFAULT_CODE = "    //TODO: Implement this method" + StringUtils.LINE_SEPARATOR + "    return ' ';";
	private static final String BOOLEAN_DEFAULT_CODE = "    //TODO: Implement this method" + StringUtils.LINE_SEPARATOR
			+ "    return false;";
	private static final String DEFAULT_CODE = "    //TODO: Implement this method" + StringUtils.LINE_SEPARATOR + "    return null;";
	private static final String COMPILED_CODE = "    /* compiled code not available */";
	private static final String COMPILED_CODE_IN_JAVADOC = "< compiled code >";

	// ==========================================================================
	// ============================= Instance variables =========================
	// ==========================================================================

	protected String name;
	private DMType _returnType;
	private Vector<DMMethodParameter> _parameters;
	private String _signatureNFQ = null;
	private String _signatureFQ = null;
	private String _parameterListAsString = null;
	private String _parameterListAsStringFQ = null;
	String _parameterNamedListAsString = null;

	private DMVisibilityType _visibilityModifier;
	private boolean isStatic;
	private boolean isAbstract;
	private boolean isSynchronized;

	protected DMEntity entity;

	private boolean _isStaticallyDefinedInTemplate = false;

	// ==========================================================================
	// ============================= Constructor ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMMethod(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMMethod(DMModel dmModel) {
		super(dmModel);
		_parameters = new Vector<DMMethodParameter>();
		_visibilityModifier = DMVisibilityType.PUBLIC;
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public DMMethod(DMModel dmModel, String name) {
		this(dmModel);
		this.name = name;
		entity = null;
		_returnType = null;
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		_isRegistered = true;
		preventFromModifiedPropagation();
		try {
			updateSignature();
		} catch (DuplicateMethodSignatureException e) {
			logger.warning("Unexpected DuplicateMethodSignatureException for " + this);
		}
		updateCode();
		allowModifiedPropagation();
		super.finalizeDeserialization(builder);
	}

	/**
	 * Update this property given an other property. This method updates only data extracted from LoadableDMEntity features and exclude many
	 * properties such as description.
	 * 
	 * @throws DuplicateMethodSignatureException
	 */
	public void update(DMMethod method, boolean updateDescription) throws DuplicateMethodSignatureException {
		if (getEntity() != null && getEntity().getMethod(method.getSignature()) != null
				&& getEntity().getMethod(method.getSignature()) != this) {
			throw new DuplicateMethodSignatureException(method.getSignature());
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update " + getSignature() + " with " + method.getSignature());
		}

		String oldSignature = _getRegisteredWithSignature(); // getSignature();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Old signature was: " + oldSignature);
		}

		// Name is supposed to be the same, but check anyway
		if (!getName().equals(method.getName())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update name");
			}
			setName(method.getName());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Name is up-to-date");
			}
		}
		// Type
		if (getReturnType() == null || !getReturnType().equals(method.getReturnType())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update ReturnType");
			}
			setType(method.getReturnType());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("ReturnType is up-to-date");
			}
		}
		// Visibility modifier
		if (!getVisibilityModifier().equals(method.getVisibilityModifier())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update VisibilityModifier");
			}
			setVisibilityModifier(method.getVisibilityModifier());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("VisibilityModifier is up-to-date");
			}
		}
		// IsAbstract
		if (getIsAbstract() != method.getIsAbstract()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update IsAbstract");
			}
			setIsAbstract(method.getIsAbstract());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("IsAbstract is up-to-date");
			}
		}
		// IsStatic
		if (getIsStatic() != method.getIsStatic()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update IsStatic");
			}
			setIsStatic(method.getIsStatic());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("IsStatic is up-to-date");
			}
		}
		// IsSynchronized
		if (getIsSynchronized() != method.getIsSynchronized()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update IsSynchronized");
			}
			setIsSynchronized(method.getIsSynchronized());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("IsSynchronized is up-to-date");
			}
		}

		// Parameters
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Before updating parameters, signature is " + getSignature());
		}
		Vector<DMMethodParameter> paramsToRemove = new Vector<DMMethodParameter>();
		paramsToRemove.addAll(getParameters());
		int index = 0;
		for (DMMethodParameter param : (Vector<DMMethodParameter>) method.getParameters().clone()) {
			// DMMethodParameter existingParam = getDMParameter(param.getName());
			DMMethodParameter existingParam = index < getParameters().size() ? getParameters().elementAt(index) : null;
			if (existingParam != null) {
				// Update param
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Update param " + existingParam.getName());
				}
				existingParam.update(param, updateDescription);
				paramsToRemove.remove(existingParam);
			} else {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Add param " + param.getName());
				}
				addToParametersNoCheck(param);
			}
			index++;
		}
		for (DMMethodParameter paramToRemove : paramsToRemove) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Remove param " + paramToRemove.getName());
			}
			removeFromParametersNoCheck(paramToRemove);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("After updating parameters, signature is " + getSignature());
		}

		// Code
		try {
			// logger.info("method.getSourceCode().getCode()=["+method.getSourceCode().getCode()+"]");
			if (getSourceCode().getCode() == null && method.getSourceCode().getCode() != null) {
				getSourceCode().setCode(method.getSourceCode().getCode(), false);
			}
			if (getSourceCode().getCode() != null && !getSourceCode().getCode().equals(method.getSourceCode().getCode())) {
				getSourceCode().setCode(method.getSourceCode().getCode(), false);
			}
		} catch (ParserNotInstalledException e) {
			e.printStackTrace();
		}

		// Descriptions
		if (updateDescription) {
			if (getDescription() == null && method.getDescription() != null || getDescription() != null
					&& !getDescription().equals(method.getDescription())) {
				setDescription(method.getDescription());
			}
			for (String descriptionKey : method.getSpecificDescriptions().keySet()) {
				String description = method.getSpecificDescriptionForKey(descriptionKey);
				if (description == null && getSpecificDescriptionForKey(descriptionKey) != null || description != null
						&& !description.equals(getSpecificDescriptionForKey(descriptionKey))) {
					setSpecificDescriptionsForKey(description, descriptionKey);
				}
			}
		}

		if (getEntity() != null && !getSignature().equals(oldSignature)) {
			/*
			 * if (getEntity().getMethod(getSignature()) != null) { throw new DuplicateMethodSignatureException(getSignature()); } else {
			 */
			if (oldSignature != null) {
				getEntity().removeMethodWithKey(oldSignature);
			}
			getEntity().setMethodForKey(this, getSignature());
			// }
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update " + getSignature() + " with " + method.getSignature() + " DONE");
		}

	}

	/**
	 * Overrides allowModifiedPropagation
	 * 
	 * @see org.openflexo.foundation.dm.DMObject#allowModifiedPropagation()
	 */
	@Override
	public void allowModifiedPropagation() {
		super.allowModifiedPropagation();
		for (DMMethodParameter param : _parameters) {
			param.allowModifiedPropagation();
		}
	}

	@Override
	public void setIsModified() {
		if (ignoreNotifications()) {
			return;
		}
		super.setIsModified();
		if (getEntity() != null) {
			getEntity().setIsModified();
		}
	}

	@Override
	public void delete() {
		isDeleted = true;
		setReturnType(null, false);
		getEntity().unregisterMethod(this);
		setChanged();
		notifyObservers(new MethodDeleted(this));
		name = null;
		entity = null;
		_returnType = null;
		_parameters.clear();
		_parameters = null;
		super.delete();
		deleteObservers();
	}

	@Override
	public boolean isDeletable() {
		if (getEntity() == null) {
			return true;
		}
		return !getIsReadOnly() && !getEntity().getIsReadOnly();
	}

	@Override
	public String getFullyQualifiedName() {
		if (getEntity() != null) {
			return getEntity().getFullyQualifiedName() + "." + getSignature();
		}
		return "NULL." + name;
	}

	@Override
	public void setDescription(String aDescription) {
		super.setDescription(aDescription);
		updateCode();
	}

	@Override
	public void setSpecificDescriptionsForKey(String description, String key) {
		super.setSpecificDescriptionsForKey(description, key);
		updateCode();
	}

	public String getSimplifiedSignature() {
		if (_signatureNFQ == null) {
			StringBuffer signature = new StringBuffer();
			signature.append(name);
			signature.append("(");
			signature.append(getParameterListAsString(false));
			signature.append(")");
			_signatureNFQ = signature.toString();
		}
		return _signatureNFQ;
	}

	public String getSignature() {
		if (_signatureFQ == null) {
			StringBuffer signature = new StringBuffer();
			signature.append(name);
			signature.append("(");
			signature.append(getParameterListAsString(true));
			signature.append(")");
			_signatureFQ = signature.toString();
		}
		return _signatureFQ;
	}

	public String getSimplifiedSignatureInContext(DMType context) {
		StringBuffer signature = new StringBuffer();
		signature.append(name);
		signature.append("(");
		signature.append(getParameterListAsStringInContext(context, false));
		signature.append(")");
		return signature.toString();
	}

	public String getSignatureInContext(DMType context) {
		StringBuffer signature = new StringBuffer();
		signature.append(name);
		signature.append("(");
		signature.append(getParameterListAsStringInContext(context, true));
		signature.append(")");
		return signature.toString();
	}

	public String getParameterListAsString(boolean fullyQualified) {
		String _searched = fullyQualified ? _parameterListAsStringFQ : _parameterListAsString;
		if (_searched == null) {
			StringBuffer returned = new StringBuffer();
			boolean isFirst = true;
			if (_parameters != null && _parameters.size() > 0) {
				for (Enumeration en = _parameters.elements(); en.hasMoreElements();) {
					DMMethodParameter next = (DMMethodParameter) en.nextElement();
					String typeName = "";
					if (next.getType() != null) {
						typeName = fullyQualified ? next.getType().getStringRepresentation() : next.getType()
								.getSimplifiedStringRepresentation();
					}
					returned.append((isFirst ? "" : ",") + typeName);
					isFirst = false;
				}
			}
			if (fullyQualified) {
				_parameterListAsStringFQ = returned.toString();
			} else {
				_parameterListAsString = returned.toString();
			}
		}
		return fullyQualified ? _parameterListAsStringFQ : _parameterListAsString;
	}

	// Warning: no cache for this method
	String getParameterListAsStringInContext(DMType context, boolean fullyQualified) {
		if (_parameters == null) {
			return "";
		}
		StringBuffer returned = new StringBuffer();
		boolean isFirst = true;
		for (Enumeration en = _parameters.elements(); en.hasMoreElements();) {
			DMMethodParameter next = (DMMethodParameter) en.nextElement();
			String typeName = "";
			DMType typeInContext = DMType.makeInstantiatedDMType(next.getType(), context);
			if (typeInContext != null) {
				typeName = fullyQualified ? typeInContext.getStringRepresentation() : typeInContext.getSimplifiedStringRepresentation();
			}
			returned.append((isFirst ? "" : ",") + typeName);
			isFirst = false;
		}
		return returned.toString();
	}

	public String getParameterNamedListAsString() {
		if (_parameterNamedListAsString == null) {
			StringBuffer returned = new StringBuffer();
			boolean isFirst = true;
			for (Enumeration en = _parameters.elements(); en.hasMoreElements();) {
				DMMethodParameter next = (DMMethodParameter) en.nextElement();
				String paramName = next.getName();
				String typeName = "";
				if (next.getType() != null) {
					typeName = next.getType().getSimplifiedStringRepresentation();
				}
				returned.append((isFirst ? "" : ",") + typeName + " " + paramName);
				isFirst = false;
			}
			_parameterNamedListAsString = returned.toString();
		}
		return _parameterNamedListAsString;
	}

	String getMethodHeader() {
		StringBuffer methodHeader = new StringBuffer();
		methodHeader.append(getModifiersAsString());
		methodHeader.append(getReturnType() != null ? getReturnType().getSimplifiedStringRepresentation() : "void");
		methodHeader.append(" ");
		methodHeader.append(name);
		methodHeader.append("(");
		methodHeader.append(getParameterNamedListAsString());
		methodHeader.append(")");
		return methodHeader.toString();
	}

	private String _registeredWithSignature = null;

	protected void _setRegisteredWithSignature(String aSignature) {
		_registeredWithSignature = aSignature;
		// _isRegistered = true;
	}

	protected String _getRegisteredWithSignature() {
		return _registeredWithSignature;
	}

	protected void updateSignature() throws DuplicateMethodSignatureException {
		_signatureFQ = null;
		_signatureNFQ = null;
		_parameterListAsStringFQ = null;
		_parameterListAsString = null;
		_parameterNamedListAsString = null;

		if (!_isRegistered) {
			return;
		}

		String oldSignature = _registeredWithSignature;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Old signature was: " + oldSignature);
		}

		if (getEntity() != null && !getSignature().equals(oldSignature)) {
			if (getEntity().getDeclaredMethod(getSignature()) != null) {
				// TODO: s'occuper de ce probleme un jour
				throw new DuplicateMethodSignatureException(getSignature());
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("New signature is: " + getSignature());
				}

				if (oldSignature != null) {
					getEntity().removeMethodWithKey(oldSignature);
				}
				getEntity().setMethodForKey(this, getSignature());
			}
		}

	}

	protected boolean _isRegistered = false;

	protected void updateCode() {
		if (isDeserializing()) {
			return;
		}

		String oldCode = getSourceCode().getCode();

		if (getSourceCode().isEdited()) {
			getSourceCode().replaceMethodDeclarationInEditedCode(getMethodHeader());
			ParsedJavadoc jd;
			try {
				jd = getSourceCode().parseJavadoc(oldCode);

				if (jd != null) {

					jd.setComment(getDescription());

					Map<String, String> specificDescriptions = getSpecificDescriptions();
					if (specificDescriptions != null && specificDescriptions.size() > 0) {
						for (String key : specificDescriptions.keySet()) {
							String specificDescription = ToolBox.getJavaDocString(specificDescriptions.get(key));
							ParsedJavadocItem jdi = jd.getTagByName("doc", key);
							if (jdi != null) {
								jdi.setParameterValue(specificDescription);
							} else {
								jd.addTagForNameAndValue("doc", key, specificDescription, true);
							}
						}
					}

					if (getParameters().size() > 0) {
						for (DMMethodParameter param : getParameters()) {
							ParsedJavadocItem jdi = jd.getTagByName("param", param.getName());
							if (jdi != null) {
								jdi.setParameterValue(ToolBox.getJavaDocString(param.getDescription()));
							} else {
								jd.addTagForNameAndValue("param", param.getName(), ToolBox.getJavaDocString(param.getDescription()), false);
							}
						}
					}

					if (!isVoid()) {
						ParsedJavadocItem jdi = jd.getTagByName("return");
						if (jdi != null) {
							jdi.setParameterValue(getReturnType().getStringRepresentation());
						} else {
							jd.addTagForNameAndValue("return", getReturnType().getSimplifiedStringRepresentation(), "", true);
						}
					}

					// logger.info("Replacing javadoc with: "+jd.getStringRepresentation());

					getSourceCode().replaceJavadocInEditedCode(jd);
				}

				else {
					getSourceCode().replaceJavadocInEditedCode(getJavadoc() + StringUtils.LINE_SEPARATOR);
				}
			} catch (ParserNotInstalledException e) {
				logger.warning("JavaParser not installed");
			}
		} else {
			getSourceCode().updateComputedCode();
		}

		setChanged();
		notifyObservers(new DMAttributeDataModification("code", oldCode, getCode()));
	}

	/*
	 * public String getCode() { return _code; }
	 * 
	 * public void setCode(String code) { if (code.indexOf("{") != 0) code = "{"+StringUtils.LINE_SEPARATOR+code; if (!code.endsWith("}"))
	 * code = code+StringUtils.LINE_SEPARATOR+"}"; _code = code; try { updateSignature(); } catch (DuplicateMethodSignatureException e) { //
	 * Warns about the exception logger.warning ("Exception raised: "+e.getClass().getName()+". See console for details.");
	 * e.printStackTrace(); } setChanged(); }
	 */

	String getDefaultCoreCode() {
		if (getEntity() instanceof LoadableDMEntity) {
			return COMPILED_CODE;
		}
		if (getReturnType() == null || getReturnType().isVoid()) {
			return VOID_DEFAULT_CODE;
		} else if (getReturnType().isBoolean()) {
			return BOOLEAN_DEFAULT_CODE;
		} else if (getReturnType().isInteger()) {
			return INT_DEFAULT_CODE;
		} else if (getReturnType().isLong()) {
			return BOOLEAN_DEFAULT_CODE;
		} else if (getReturnType().isChar()) {
			return CHAR_DEFAULT_CODE;
		} else if (getReturnType().isFloat()) {
			return FLOAT_DEFAULT_CODE;
		} else if (getReturnType().isDouble()) {
			return DOUBLE_DEFAULT_CODE;
		} else {
			return DEFAULT_CODE;
		}
	}

	String getJavadoc() {
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/**" + StringUtils.LINE_SEPARATOR);
		if (getEntity() instanceof LoadableDMEntity && (getDescription() == null || getDescription().equals(""))) {
			javadoc.append("  * " + COMPILED_CODE_IN_JAVADOC + StringUtils.LINE_SEPARATOR);
		} else if (getDescription() != null && getDescription().trim().length() > 0) {
			javadoc.append("  * " + ToolBox.getJavaDocString(getDescription(), "  "));
		}
		/*
		 * else if (getDescription() != null) { BufferedReader rdr = new BufferedReader(new StringReader(getDescription())); boolean
		 * hasMoreLines = true; while (hasMoreLines) { String currentLine = null; try { currentLine = rdr.readLine(); } catch (IOException
		 * e) {} if (currentLine != null) { currentLine = ToolBox.getJavaDocString(currentLine);
		 * javadoc.append("  * "+currentLine+StringUtils.LINE_SEPARATOR); } hasMoreLines = (currentLine != null); } }
		 */
		javadoc.append("  *" + StringUtils.LINE_SEPARATOR);

		Map<String, String> specificDescriptions = getSpecificDescriptions();
		if (specificDescriptions != null && specificDescriptions.size() > 0) {
			for (String key : specificDescriptions.keySet()) {
				String specificDescription = ToolBox.getJavaDocString(specificDescriptions.get(key));
				// javadoc.append("  * @doc "+key+" "+specificDescription+StringUtils.LINE_SEPARATOR);
				javadoc.append(getTagAndParamRepresentation("doc", key, specificDescription));
			}
			javadoc.append("  *" + StringUtils.LINE_SEPARATOR);
		}

		if (getParameters().size() > 0) {
			for (DMMethodParameter param : getParameters()) {
				javadoc.append(getTagAndParamRepresentation("param", param.getName(), ToolBox.getJavaDocString(param.getDescription())));
				// javadoc.append("  * @param "+param.getName()+" "+param.getDescription()+StringUtils.LINE_SEPARATOR);
			}
		}

		if (!isVoid()) {
			javadoc.append("  * @return " + getReturnType().getSimplifiedStringRepresentation() + StringUtils.LINE_SEPARATOR);
		}

		javadoc.append("  */");
		return javadoc.toString();
	}

	private MethodSourceCode sourceCode;

	@Override
	public void resetSourceCode() throws ParserNotInstalledException, DuplicateMethodSignatureException {
		if (sourceCode != null) {
			sourceCode.setCode("");
		}
	}

	public MethodSourceCode getSourceCode() {
		if (sourceCode == null) {
			sourceCode = new MethodSourceCode(this/* ,"code","hasParseError","parseErrorWarning" */) {
				@Override
				public String makeComputedCode() {
					return getJavadoc() + StringUtils.LINE_SEPARATOR + getMethodHeader() + " {" + StringUtils.LINE_SEPARATOR
							+ getDefaultCoreCode() + StringUtils.LINE_SEPARATOR + "}";
				}

				@Override
				public void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException {

					logger.info(">>>>>>>>>>>> Interpret " + javaMethod);
					try {
						getJavaMethodParser().updateWith(DMMethod.this, javaMethod);

						if (!isResolvable()) {
							setHasParseErrors(true);
							setParseErrorWarning("<html><font color=\"red\">" + FlexoLocalization.localizedForKey("unresolved_type(s)")
									+ " : " + getUnresolvedTypes() + "</font></html>");
						}
						DMMethod.this.setChanged();
						DMMethod.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("code", null, getCode()));
					} catch (DuplicateMethodSignatureException e) {
						setHasParseErrors(true);
						setParseErrorWarning("<html><font color=\"red\">"
								+ FlexoLocalization.localizedForKey("duplicated_method_signature") + "</font></html>");
						throw e;
					}
				}

			};
		}
		return sourceCode;
	}

	public boolean hasParseErrors() {
		return getSourceCode().hasParseErrors();
	}

	public String getParseErrorWarning() {
		return getSourceCode().getParseErrorWarning();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getCoreCode() {
		if (isSerializing()) {
			return null;
		}
		return getSourceCode().getCoreCode();
	}

	/**
	 * @deprecated
	 * @param someCode
	 */
	@Deprecated
	public void setCoreCode(String someCoreCode) {
		getSourceCode().updateComputedCode(
				getJavadoc() + StringUtils.LINE_SEPARATOR + getMethodHeader() + " { " + StringUtils.LINE_SEPARATOR + someCoreCode
						+ StringUtils.LINE_SEPARATOR + "}");
	}

	public String getCode() {
		return getSourceCode().getCode();
	}

	public void setCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException {
		getSourceCode().setCode(someCode);
		setChanged();
		notifyObservers(new DMAttributeDataModification("code", null, someCode));
	}

	public String getModifiersAsString() {
		return (getVisibilityModifier() != null ? getVisibilityModifier() != DMVisibilityType.NONE ? getVisibilityModifier().getName()
				+ " " : "" : "")
				+ (getIsStatic() ? "static" + " " : "")
				+ (getIsAbstract() ? "abstract" + " " : "")
				+ (getIsSynchronized() ? "synchronized" + " " : "");
	}

	public DMVisibilityType getVisibilityModifier() {
		return _visibilityModifier;
	}

	public void setVisibilityModifier(DMVisibilityType visibilityModifier) {
		if (visibilityModifier != _visibilityModifier) {
			_visibilityModifier = visibilityModifier;
			try {
				updateSignature();
			} catch (DuplicateMethodSignatureException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(new DMAttributeDataModification("visibilityModifier", !isAbstract, isAbstract));
			updateCode();
		}
	}

	public boolean getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(boolean isAbstract) {
		if (isAbstract != this.isAbstract) {
			this.isAbstract = isAbstract;
			try {
				updateSignature();
			} catch (DuplicateMethodSignatureException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(new DMAttributeDataModification("isAbstract", !isAbstract, isAbstract));
			updateCode();
		}
	}

	public boolean getIsStatic() {
		return isStatic;
	}

	public void setIsStatic(boolean isStatic) {
		if (isStatic != this.isStatic) {
			this.isStatic = isStatic;
			try {
				updateSignature();
			} catch (DuplicateMethodSignatureException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(new DMAttributeDataModification("isStatic", !isStatic, isStatic));
			updateCode();
		}
	}

	public boolean getIsSynchronized() {
		return isSynchronized;
	}

	public void setIsSynchronized(boolean isSynchronized) {
		if (isSynchronized != this.isSynchronized) {
			this.isSynchronized = isSynchronized;
			try {
				updateSignature();
			} catch (DuplicateMethodSignatureException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(new DMAttributeDataModification("isSynchronized", !isSynchronized, isSynchronized));
			updateCode();
		}
	}

	public Vector<DMMethodParameter> getParameters() {
		return _parameters;
	}

	public void setParameters(Vector<DMMethodParameter> someParameters) throws DuplicateMethodSignatureException {
		_parameters = someParameters;
		updateSignature();
		setChanged();
		updateCode();
	}

	public void addToParameters(DMMethodParameter param) throws DuplicateMethodSignatureException {
		param.setMethod(this);
		_parameters.add(param);
		updateSignature();
		setChanged();
		updateCode();
	}

	public void addToParametersNoCheck(DMMethodParameter param) {
		param.setMethod(this);
		_parameters.add(param);
		_signatureFQ = null;
		_signatureNFQ = null;
		_parameterListAsStringFQ = null;
		_parameterListAsString = null;
		_parameterNamedListAsString = null;
		setChanged();
	}

	public void removeFromParameters(DMMethodParameter param) throws DuplicateMethodSignatureException {
		param.setMethod(null);
		_parameters.remove(param);
		updateSignature();
		updateCode();
		setChanged();
	}

	public void removeFromParametersNoCheck(DMMethodParameter param) {
		param.setMethod(null);
		_parameters.remove(param);
		_signatureFQ = null;
		_signatureNFQ = null;
		_parameterListAsStringFQ = null;
		_parameterListAsString = null;
		_parameterNamedListAsString = null;
		setChanged();
	}

	public DMMethodParameter getDMParameter(String aParamName) {
		for (Enumeration en = _parameters.elements(); en.hasMoreElements();) {
			DMMethodParameter next = (DMMethodParameter) en.nextElement();
			if (next.getName().equals(aParamName)) {
				return next;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getNextDefautParameterName() {
		String baseName = FlexoLocalization.localizedForKey("default_new_parameter_name");
		return getNextDefautParameterName(baseName);
	}

	/**
	 * @return
	 */
	public String getNextDefautParameterName(String baseName) {
		String testMe = baseName;
		int test = 0;
		while (getDMParameter(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	public DMMethodParameter createNewParameter() throws DuplicateMethodSignatureException {
		DMMethodParameter newParam = new DMMethodParameter(getDMModel(), this);
		newParam.setName(getNextDefautParameterName());
		addToParameters(newParam);
		return newParam;
	}

	public void deleteParameter(DMMethodParameter param) throws DuplicateMethodSignatureException {
		removeFromParameters(param);
	}

	public boolean isParameterDeletable(DMMethodParameter param) {
		return true;
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.DM.DM_RO_METHOD_INSPECTOR;
		}
		if (getDMRepository() == null || getDMRepository().isReadOnly()) {
			return Inspectors.DM.DM_RO_METHOD_INSPECTOR;
		} else {
			return Inspectors.DM.DM_METHOD_INSPECTOR;
		}
	}

	/**
	 * Return a Vector of embedded DMObjects at this level.
	 * 
	 * @return null
	 */
	@Override
	public Vector<DMObject> getEmbeddedDMObjects() {
		return EMPTY_VECTOR;
	}

	public DMRepository getDMRepository() {
		if (getEntity() != null) {
			return getEntity().getRepository();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String newName) throws DuplicateMethodSignatureException {
		if (name == null || !name.equals(newName)) {
			DMEntity containerEntity = getEntity();
			/*
			 * if (containerEntity != null) { containerEntity.unregisterMethod(this, false); }
			 */
			String oldName = name;
			name = newName;
			/*
			 * if (containerEntity != null) { containerEntity.registerMethod(this, false); }
			 */
			_signatureFQ = null;
			_signatureNFQ = null;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Change from " + oldName + " to " + newName + " new signature is " + getSignature());
			}
			if (!isDeserializing() && getEntity() != null && getEntity().getDeclaredMethod(getSignature()) != null) {
				name = oldName;
				throw new DuplicateMethodSignatureException(getSignature());
			}
			try {
				updateSignature();
				setChanged();
				notifyObservers(new DMMethodNameChanged(this, oldName, newName));
				if (containerEntity != null) {
					containerEntity.notifyReordering(this);
				}
			} catch (DuplicateMethodSignatureException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			updateCode();
		}
	}

	@Override
	public DMEntity getEntity() {
		return entity;
	}

	public void setEntity(DMEntity entity) {
		this.entity = entity;
		setChanged();
	}

	public boolean getIsReadOnly() {
		if (getIsStaticallyDefinedInTemplate()) {
			return true;
		}
		if (getEntity() != null) {
			return getEntity().getIsReadOnly();
		}
		return false;
	}

	@Override
	public DMType getType() {
		return getReturnType();
	}

	@Override
	public void setType(DMType type) {
		setReturnType(type, true);
	}

	// private String returnTypeAsString;

	public DMType getReturnType() {
		/*
		 * if (_returnType==null && returnTypeAsString!=null) {
		 * setReturnType(getDMModel().getDmTypeConverter().convertFromString(returnTypeAsString), false); returnTypeAsString = null; }
		 */
		return _returnType;
	}

	public void setReturnType(DMType type) {
		setReturnType(type, true);
	}

	public void setReturnType(DMType type, boolean notify) {
		if (type == null && _returnType != null || type != null && !type.equals(_returnType)) {
			DMType oldType = _returnType;
			if (oldType != null) {
				oldType.removeFromTypedWithThisType(this);
			}
			_returnType = type;
			if (_returnType != null) {
				_returnType.setOwner(this);
			}
			if (type != null) {
				type.addToTypedWithThisType(this);
			}
			getSourceCode().updateComputedCode();
			try {
				updateSignature();
			} catch (DuplicateMethodSignatureException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			if (notify) {
				setChanged();
				notifyObservers(new DMAttributeDataModification("returnType", oldType, type));
				updateCode();
			}
		}
	}

	/*
	 * public String getReturnTypeAsString() { if (getReturnType()!=null) return
	 * getDMModel().getDmTypeConverter().convertToString(getReturnType()); else return null; }
	 * 
	 * public void setReturnTypeAsString(String returnType) { returnTypeAsString = returnType; }
	 */

	/**
	 * @deprecated Use getReturnType() instead, kept for backward compatibility in XML mappings
	 * @return DMEntity
	 */
	@Deprecated
	public DMEntity getReturnTypeBaseEntity() {
		if (getReturnType() != null) {
			return getReturnType().getBaseEntity();
		}
		return null;
	}

	/**
	 * @deprecated Use setReturnType(DMType) instead, kept for backward compatibility in XML mappings
	 * @param anEntity
	 */
	@Deprecated
	public void setReturnTypeBaseEntity(DMEntity anEntity) {
		setReturnType(DMType.makeResolvedDMType(anEntity), true);
	}

	public boolean isVoid() {
		return getReturnType() == null || getReturnType().isVoid();
	}

	public boolean overrides(DMMethod method) {
		if (method == null || method.getEntity() == null || method.getSignature() == null) {
			return false;
		}
		return method.getEntity().isAncestorOf(getEntity()) && method.getSignature().equals(getSignature());
	}

	public String getStringRepresentation() {
		return getSignature();
	}

	// ==========================================================================
	// ======================== TreeNode implementation =========================
	// ==========================================================================

	@Override
	public synchronized Vector<DMObject> getOrderedChildren() {
		return EMPTY_VECTOR;
	}

	@Override
	public TreeNode getParent() {
		return getEntity();
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public Vector<DMTypeVariable> getTypeVariables() {
		if (getEntity() != null) {
			return getEntity().getTypeVariables();
		}
		return null;
	}

	// ==========================================================================
	// ==================== DMMethodParameter implementation ====================
	// ==========================================================================

	public static class DMMethodParameter extends DMObject implements Typed, DMGenericDeclaration, DMTypeOwner {
		private DMType _type;
		private DMMethod _method;
		private String _name;

		/**
		 * Constructor used during deserialization
		 */
		public DMMethodParameter(FlexoDMBuilder builder) {
			this(builder.dmModel);
			initializeDeserialization(builder);
		}

		/**
		 * Default constructor
		 */
		public DMMethodParameter(DMModel dmModel) {
			super(dmModel);
		}

		/**
		 * Constructor used for dynamic creation
		 */
		public DMMethodParameter(DMModel dmModel, DMMethod method) {
			this(dmModel);
			this._method = method;
		}

		@Override
		public void setIsModified() {
			if (ignoreNotifications()) {
				return;
			}
			super.setIsModified();
			if (getMethod() != null) {
				getMethod().setIsModified();
			}
		}

		public void update(DMMethodParameter methodParameter, boolean updateDescription) {
			// Name is supposed to be the same, but check anyway
			if (!getName().equals(methodParameter.getName())) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update name");
				}
				_name = methodParameter.getName();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Name is up-to-date");
				}
			}

			// Type
			if (_type != methodParameter.getType()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update type");
				}
				_type = methodParameter.getType();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Type is up-to-date");
				}
			}

			// Description
			if (getDescription() == null && methodParameter.getDescription() != null || getDescription() != null
					&& !getDescription().equals(methodParameter.getDescription())) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update description");
				}
				setDescription(methodParameter.getDescription());
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Description is up-to-date");
				}
			}
		}

		@Override
		public String getName() {
			return _name;
		}

		@Override
		public void setName(String name) {
			if (name == null && _name != null || name != null && !name.equals(_name)) {
				String oldName = _name;
				_name = name;
				if (_method != null) {
					_method._parameterNamedListAsString = null;
					_method.updateCode();
				}
				setChanged();
				notifyObservers(new DMAttributeDataModification("name", oldName, name));
			}
		}

		@Override
		public void setDescription(String aDescription) {
			super.setDescription(aDescription);
			if (_method != null) {
				_method.updateCode();
			}
		}

		@Override
		public Vector<DMTypeVariable> getTypeVariables() {
			if (getMethod() != null) {
				return getMethod().getTypeVariables();
			}
			return null;
		}

		@Override
		public boolean isDeletable() {
			return _method.isDeletable();
		}

		// private String typeAsString;

		@Override
		public DMType getType() {
			/*
			 * if (_type==null && typeAsString!=null) { setType(getDMModel().getDmTypeConverter().convertFromString(typeAsString),false);
			 * typeAsString = null; }
			 */
			return _type;
		}

		@Override
		public void setType(DMType type) {
			setType(type, true);
		}

		public void setType(DMType type, boolean notify) {
			if (type == null && _type != null || type != null && !type.equals(_type)) {
				DMType oldType = _type;
				if (oldType != null) {
					oldType.removeFromTypedWithThisType(this);
				}
				_type = type;
				if (_type != null) {
					_type.setOwner(this);
					_type.addToTypedWithThisType(this);
				}
				if (_method != null) {
					try {
						_method.updateSignature();
					} catch (DuplicateMethodSignatureException e) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						e.printStackTrace();
					}
					_method._parameterNamedListAsString = null;
					_method.updateCode();
				}
				if (notify) {
					setChanged();
					notifyObservers(new DMAttributeDataModification("type", oldType, type));
				}
			}
		}

		/*
		 * public String getTypeAsString() { if (getType()!=null) return getDMModel().getDmTypeConverter().convertToString(getType()); else
		 * return null; }
		 * 
		 * public void setTypeAsString(String typeAsString) { this.typeAsString = typeAsString; }
		 */

		/**
		 * @deprecated Use getType() instead, kept for backward compatibility in XML mappings
		 * @return DMEntity
		 */
		@Deprecated
		public DMEntity getTypeBaseEntity() {
			if (getType() != null) {
				return getType().getBaseEntity();
			}
			return null;
		}

		/**
		 * @deprecated Use setType(DMType) instead, kept for backward compatibility in XML mappings
		 * @param anEntity
		 */
		@Deprecated
		public void setTypeBaseEntity(DMEntity anEntity) {
			setType(DMType.makeResolvedDMType(anEntity));
		}

		public DMMethod getMethod() {
			return _method;
		}

		public void setMethod(DMMethod method) {
			_method = method;
		}

		@Override
		public String getFullyQualifiedName() {
			if (getMethod() != null) {
				return getMethod().getFullyQualifiedName() + "." + getName();
			}
			return "NULL." + getName();
		}

		@Override
		public Vector<DMObject> getOrderedChildren() {
			return EMPTY_VECTOR;
		}

		@Override
		public TreeNode getParent() {
			return getMethod();
		}

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		/**
		 * Return null since parameter is never inspected by its own
		 * 
		 * @return null
		 */
		@Override
		public String getInspectorName() {
			return null;
		}

		/**
		 * Return a Vector of embedded DMObjects at this level.
		 * 
		 * @return null
		 */
		@Override
		public Vector<DMObject> getEmbeddedDMObjects() {
			return EMPTY_VECTOR;
		}

		/*
		 * private Class _unresolvedTypeClass;
		 * 
		 * public void setUnresolvedTypeClass(Class aClass) { _unresolvedTypeClass = aClass; }
		 * 
		 * public Class getUnresolvedTypeClass() { return _unresolvedTypeClass; }
		 */

		/**
		 * Overrides getClassNameKey
		 * 
		 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
		 */
		@Override
		public String getClassNameKey() {
			return "dm_methode_parameter";
		}

		/*
		 * private DMType _unresolvedType;
		 * 
		 * public DMType getUnresolvedType() { return _unresolvedType; }
		 * 
		 * public void setUnresolvedType(DMType unresolvedType2) { _unresolvedType = unresolvedType2; }
		 */

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof DMEntityClassNameChanged && observable == getType().getBaseEntity()) {
				// Handle class name changed
				updateTypeClassNameChange((String) ((DMEntityClassNameChanged) dataModification).oldValue(),
						(String) ((DMEntityClassNameChanged) dataModification).newValue());
			} else if (dataModification instanceof EntityDeleted && observable == getType().getBaseEntity()) {
				DMEntity parent = getType().getBaseEntity();
				while (parent != null && parent.isDeleted()) {
					parent = parent.getParentBaseEntity();
				}
				if (parent != null) {
					setType(DMType.makeResolvedDMType(parent));
				} else {
					setType(DMType.makeResolvedDMType(Object.class, getProject()));
				}
			} else if (dataModification instanceof TypeResolved) {
				setChanged();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Type resolved in DMMethodParameter: " + ((TypeResolved) dataModification).getType());
				}
				try {
					getMethod().updateSignature();
				} catch (DuplicateMethodSignatureException e) {
					logger.warning("Inconsistent data: duplicate method signature for " + getMethod().getSignature());
					e.printStackTrace();
				}
				getMethod().updateCode();
			} else {
				super.update(observable, dataModification);
			}
		}

		private void updateTypeClassNameChange(String oldClassName, String newClassName) {
			try {
				_method.updateSignature();
				_method.updateCode();
				if (_method.getSourceCode().getCode() != null) {
					_method.getSourceCode().setCode(
							ToolBox.replaceStringByStringInString(oldClassName, newClassName, _method.getSourceCode().getCode()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			_method.setChanged(true);
		}
	}

	public static String signatureForMethod(Method aMethod, boolean fullyQualified) {
		StringBuffer returned = new StringBuffer();
		returned.append(aMethod.getName());
		returned.append("(");
		boolean isFirst = true;
		for (int i = 0; i < aMethod.getGenericParameterTypes().length; i++) {
			returned.append(isFirst ? "" : ",");
			isFirst = false;
			String parameterAsString;
			if (fullyQualified) {
				if (aMethod.getGenericParameterTypes()[i] instanceof Class) {
					parameterAsString = ((Class) aMethod.getGenericParameterTypes()[i]).getCanonicalName();
				} else {
					parameterAsString = aMethod.getGenericParameterTypes()[i].toString();
				}
			} else {
				if (aMethod.getGenericParameterTypes()[i] instanceof Class) {
					parameterAsString = ((Class) aMethod.getGenericParameterTypes()[i]).getSimpleName();
				} else {
					parameterAsString = aMethod.getGenericParameterTypes()[i].toString();
				}
			}
			// returned.append((fullyQualified?aMethod.getGenericParameterTypes()[i]:classNameForClass(aMethod.getParameterTypes()[i])));
			returned.append(parameterAsString);
		}
		returned.append(")");
		return returned.toString();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dm_method";
	}

	// private DMType _unresolvedReturnType;

	/*
	 * public DMType getUnresolvedReturnType() { return _unresolvedReturnType; }
	 * 
	 * public void setUnresolvedReturnType(DMType unloadedReturnType) { _unresolvedReturnType = unloadedReturnType; }
	 * 
	 * public String getUnresolvedReturnTypeName() { if (_unresolvedReturnType != null) return _unresolvedReturnType.getValue(); return
	 * "???"; }
	 */

	// private Vector<DMType> _unresolvedTypes = new Vector<DMType>();

	/*
	 * public boolean isResolvable() { return (_unresolvedTypes.size() == 0); }
	 * 
	 * public void addToUnresolvedTypes(DMType type) { logger.info(">>>> addToUnresolvedTypes() "+type); _unresolvedTypes.add(type); }
	 * 
	 * public Vector<DMType> getUnresolvedTypes() { return _unresolvedTypes; }
	 */

	public boolean isResolvable() {
		if (getReturnType() == null) {
			return false;
		}
		if (!getReturnType().isResolved()) {
			return false;
		}
		for (DMMethodParameter param : _parameters) {
			if (param.getType() == null) {
				return false;
			}
			if (!param.getType().isResolved()) {
				return false;
			}
		}
		return true;
	}

	public Vector<DMType> getUnresolvedTypes() {
		Vector<DMType> unresolvedTypes = new Vector<DMType>();
		if (!getReturnType().isResolved()) {
			unresolvedTypes.add(getReturnType());
		}
		for (DMMethodParameter param : _parameters) {
			if (param.getType() != null && !param.getType().isResolved()) {
				unresolvedTypes.add(param.getType());
			}
		}
		return unresolvedTypes;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof DMEntityClassNameChanged && observable == getType().getBaseEntity()) {
			// Handle class name changed
			updateTypeClassNameChange((String) ((DMEntityClassNameChanged) dataModification).oldValue(),
					(String) ((DMEntityClassNameChanged) dataModification).newValue());
		} else if (dataModification instanceof EntityDeleted && observable == getType().getBaseEntity()) {
			DMEntity parent = getType().getBaseEntity();
			while (parent != null && parent.isDeleted()) {
				parent = parent.getParentBaseEntity();
			}
			if (parent != null) {
				setType(DMType.makeResolvedDMType(parent));
			} else {
				setType(DMType.makeResolvedDMType(Object.class, getProject()));
			}
		} else if (dataModification instanceof TypeResolved) {
			setChanged();
			// logger.info("Type resolved !!!!! "+((TypeResolved)dataModification).getType());
			try {
				updateSignature();
			} catch (DuplicateMethodSignatureException e) {
				logger.warning("Inconsistent data: duplicate method signature for " + getSignature());
				e.printStackTrace();
			}
			updateCode();
		} else {
			super.update(observable, dataModification);
		}
	}

	private void updateTypeClassNameChange(String oldClassName, String newClassName) {
		if (getSourceCode().getCode() != null) {
			try {
				getSourceCode().setCode(ToolBox.replaceStringByStringInString(oldClassName, newClassName, getSourceCode().getCode()));
			} catch (ParserNotInstalledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateMethodSignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setChanged(true);
	}

	public boolean getIsStaticallyDefinedInTemplate() {
		return _isStaticallyDefinedInTemplate;
	}

	public void setIsStaticallyDefinedInTemplate(boolean isStaticallyDefinedInTemplate) {
		if (_isStaticallyDefinedInTemplate != isStaticallyDefinedInTemplate) {
			_isStaticallyDefinedInTemplate = isStaticallyDefinedInTemplate;
			setChanged();
			notifyObservers(new DMAttributeDataModification("isStaticallyDefinedInTemplate", !isStaticallyDefinedInTemplate,
					isStaticallyDefinedInTemplate));
		}
	}

	@Override
	public boolean codeIsComputable() {
		return true;
	}

}
