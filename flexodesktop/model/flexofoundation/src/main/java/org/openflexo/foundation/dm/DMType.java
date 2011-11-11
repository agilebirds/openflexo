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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dm.DMEntity.DMTypeVariable;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorObserver;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.Duration;
import org.openflexo.xmlcode.KeyValueCoder;
import org.openflexo.xmlcode.KeyValueDecoder;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.Type;

/**
 * Represents a Type in DataModel
 * 
 * Take care that a type is here mutable: don't try to implement caches !!!!
 * 
 * @author sylvain
 * 
 */
public class DMType extends Type implements FlexoObserver, StringConvertable, KeyValueCoding, DMTypeOwner, InspectableObject {

	protected static final Logger logger = Logger.getLogger(DMType.class.getPackage().getName());

	// UNRESOLVED types
	private String name;

	// RESOLVED types
	private DMEntity baseEntity;
	Vector<DMType> parameters;
	private int dimensions;

	// DKV types
	private Domain domain;

	// TYPE_VARIABLE types
	private DMTypeVariable typeVariable;

	// WILDCARD types
	Vector<WildcardBound> lowerBounds;
	Vector<WildcardBound> upperBounds;

	private Vector<Typed> typedWithThisType;

	// private DMGenericDeclaration typeVariableContext;

	private DMTypeOwner _owner;
	private FlexoProject _project;

	private Vector<ParameterizedTypeVariable> _parameterizedTypeVariables = null;

	private boolean triedToResolve = false;
	private boolean isResolved = false;

	protected org.openflexo.foundation.dm.DMType.DMTypeStringConverter.ParsedTypeInfo _pendingInformations;

	public static final String DKV_PREFIX = "DKV.";

	@Override
	public DMType clone() {
		DMType clone = new DMType(getStringRepresentation());
		// System.out.println("Cloned as "+Integer.toHexString(clone.typeId)+" from "+Integer.toHexString(typeId)+" : "+getStringRepresentation()+" which is "+getKindOfType());
		clone._owner = _owner;
		clone._project = _project;
		clone.name = name;
		clone.baseEntity = baseEntity;
		clone.parameters.addAll(parameters);
		clone.dimensions = dimensions;
		clone.domain = domain;
		clone.typeVariable = typeVariable;
		// clone.typeVariableContext = typeVariableContext;
		if (lowerBounds != null) {
			clone.lowerBounds = new Vector<WildcardBound>();
			clone.lowerBounds.addAll(lowerBounds);
		}
		if (upperBounds != null) {
			clone.upperBounds = new Vector<WildcardBound>();
			clone.upperBounds.addAll(upperBounds);
		}
		if (_parameterizedTypeVariables != null) {
			clone._parameterizedTypeVariables = new Vector<ParameterizedTypeVariable>();
			clone._parameterizedTypeVariables.addAll(_parameterizedTypeVariables);
		}
		clone.typedWithThisType.addAll(typedWithThisType);
		clone.triedToResolve = triedToResolve;
		clone.isResolved = isResolved;
		clone._pendingInformations = _pendingInformations;
		return clone;
	}

	public static EvaluationType kindOf(DMType type) {
		if (type.isBoolean()) {
			return EvaluationType.BOOLEAN;
		} else if (type.isInteger() || type.isLong() || type.isShort() || type.isChar() || type.isByte()) {
			return EvaluationType.ARITHMETIC_INTEGER;
		} else if (type.isFloat() || type.isDouble()) {
			return EvaluationType.ARITHMETIC_FLOAT;
		} else if (type.isString()) {
			return EvaluationType.STRING;
		} else if (type.isDate()) {
			return EvaluationType.DATE;
		} else if (type.isDuration()) {
			return EvaluationType.DURATION;
		} else if (type.getKindOfType() == KindOfType.DKV) {
			return EvaluationType.ENUM;
		}
		return EvaluationType.LITERAL;
	}

	// ==========================================================
	// =================== Factory methods ======================
	// ==========================================================

	public static DMType makeUnresolvedDMType(String unresolvedTypeName) {
		return new DMType(unresolvedTypeName);
	}

	public static DMType makeUnresolvedDMType(String unresolvedTypeName, int dimensions) {
		return new DMType(unresolvedTypeName, dimensions);
	}

	public static DMType makeResolvedDMType(DMEntity entity) {
		return new DMType(entity);
	}

	public static DMType makeResolvedDMType(Class typeClass, FlexoProject project) {
		DMEntity accessedEntity = project.getDataModel().getDMEntity(typeClass, true);
		if (accessedEntity != null) {
			return makeResolvedDMType(accessedEntity);
		}
		return null;
	}

	public static DMType makeObjectDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Object.class));
	}

	public static DMType makeLongDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Long.class));
	}

	public static DMType makeIntegerDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Integer.class));
	}

	public static DMType makeShortDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Short.class));
	}

	public static DMType makeDoubleDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Double.class));
	}

	public static DMType makeBooleanDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Boolean.class));
	}

	public static DMType makeStringDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(String.class));
	}

	public static DMType makeDateDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Date.class));
	}

	public static DMType makeDurationDMType(FlexoProject project) {
		return makeResolvedDMType(project.getDataModel().getDMEntity(Duration.class));
	}

	public static DMType makeVectorDMType(DMType valueType, FlexoProject project) {
		DMType returned = new DMType(project.getDataModel().getDMEntity(Vector.class));
		returned.setProject(project);
		if (valueType != null) {
			returned.setParameterAtIndex(valueType, 0);
		}
		return returned;
	}

	public static DMType makeListDMType(DMType valueType, FlexoProject project) {
		DMType returned = new DMType(project.getDataModel().getDMEntity(List.class));
		returned.setProject(project);
		if (valueType != null) {
			returned.setParameterAtIndex(valueType, 0);
		}
		return returned;
	}

	public static DMType makeHashtableDMType(DMType keyType, DMType valueType, FlexoProject project) {
		DMType returned = new DMType(project.getDataModel().getDMEntity(Hashtable.class));
		returned.setProject(project);
		if (keyType != null) {
			returned.setParameterAtIndex(keyType, 0);
		}
		if (valueType != null) {
			returned.setParameterAtIndex(valueType, 1);
		}
		return returned;
	}

	public static DMType makeResolvedDMType(DMEntity entity, int dimensions) {
		return new DMType(entity, dimensions);
	}

	public static DMType makeResolvedDMType(DMEntity entity, int dimensions, Vector<DMType> parameters) {
		return new DMType(entity, dimensions, parameters);
	}

	public static DMType makeResolvedDMType(DMEntity entity, Vector<DMType> parameters) {
		return makeResolvedDMType(entity, 0, parameters);
	}

	public static DMType makeResolvedDMType(DMEntity entity, int dimensions, DMType... parameters) {
		Vector<DMType> params = new Vector<DMType>();
		for (DMType t : parameters) {
			params.add(t);
		}
		return makeResolvedDMType(entity, dimensions, params);
	}

	public static DMType makeResolvedDMType(DMEntity entity, DMType... parameters) {
		return makeResolvedDMType(entity, 0, parameters);
	}

	public static DMType makeTypeVariableDMType(DMTypeVariable typeVariable) {
		return new DMType(typeVariable);
	}

	public static DMType makeDKVDMType(Domain aDomain) {
		return new DMType(aDomain);
	}

	public static DMType makeWildcardDMType(Vector<DMType> upperBounds, Vector<DMType> lowerBounds) {
		return new DMType(upperBounds, lowerBounds);
	}

	/**
	 * Build instanciated DMType considering supplied type is generic (contains TypeVariable definitions) Returns a clone of DMType where
	 * all references to TypeVariable are replaced by values defined in context type. For example, given type=Enumeration<E> and
	 * context=Vector<String>, returns Enumeration<String> If supplied type is not generic, return type value (without cloning!)
	 * 
	 * @param type
	 *            : type to instanciate
	 * @param context
	 *            : context used to instanciate type
	 * @return
	 */
	public static DMType makeInstantiatedDMType(DMType type, DMType context) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeInstantiatedDMType type=" + type + " context=" + context);
		}
		if (type == null) {
			return null;
		}
		if (!type.isGeneric()) {
			return type;
		}
		DMType returned = type.clone();
		if (returned.getKindOfType() == KindOfType.UNRESOLVED) {
			return returned;
		} else if ((returned.getKindOfType() == KindOfType.RESOLVED) || (returned.getKindOfType() == KindOfType.RESOLVED_ARRAY)) {
			for (int i = 0; i < returned.parameters.size(); i++) {
				DMType p = returned.parameters.elementAt(i);
				if (p.isGeneric()) {
					returned.parameters.setElementAt(makeInstantiatedDMType(p, context), i);
				}
			}
			return returned;
		}

		else if (returned.getKindOfType() == KindOfType.TYPE_VARIABLE) {
			/*
			 * logger.info("context.getKindOfType()="+context.getKindOfType());
			 * logger.info("context.getBaseEntity()="+context.getBaseEntity());
			 * logger.info("returned.getTypeVariable().getEntity()="+returned.getTypeVariable().getEntity());
			 */
			if (context.getKindOfType() == DMType.KindOfType.RESOLVED) {
				if (context.getBaseEntity() == returned.getTypeVariable().getEntity()) {
					int index = context.getBaseEntity().getTypeVariables().indexOf(returned.getTypeVariable());
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Instanciating type with type at index " + index);
					}
					if (index < context.parameters.size()) {
						return context.parameters.elementAt(index);
					}
				} else if (returned.getTypeVariable().getEntity().isAncestorOf(context.getBaseEntity())) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Delegate instanciation to parent type " + context.getBaseEntity().getParentType());
					}
					// Computing parent context
					DMType parentContext = makeInstantiatedDMType(context.getBaseEntity().getParentType(), context);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Using parent context: " + parentContext);
					}
					// And use this parent context to recursively call
					return makeInstantiatedDMType(type, parentContext);
				} else {
					logger.warning("Unexpected situation (type generics not instanciated) encountered in makeInstantiatedDMType(" + type
							+ "," + context + ")");
					return makeObjectDMType(context.getProject());
				}
			}

			return returned;
		}

		else if (returned.getKindOfType() == KindOfType.WILDCARD) {
			if (returned.getUpperBounds() != null) {
				for (int i = 0; i < returned.getUpperBounds().size(); i++) {
					WildcardBound b = returned.getUpperBounds().elementAt(i);
					if ((b.bound != null) && b.bound.isGeneric()) {
						returned.getUpperBounds().setElementAt(returned.new WildcardBound(makeInstantiatedDMType(b.bound, context)), i);
					}
				}
			}
			if (returned.getLowerBounds() != null) {
				for (int i = 0; i < returned.getLowerBounds().size(); i++) {
					WildcardBound b = returned.getLowerBounds().elementAt(i);
					if ((b.bound != null) && b.bound.isGeneric()) {
						returned.getLowerBounds().setElementAt(returned.new WildcardBound(makeInstantiatedDMType(b.bound, context)), i);
					}
				}
			}
			return returned;
		}

		else {
			logger.warning("Unexpected KindOfType: " + returned.getKindOfType());
			return returned;
		}
	}

	// ==========================================================
	// ===================== Constructors =======================
	// ==========================================================

	protected DMType(String fullName, String name, int dimensions, JavaClassParent context) {
		super(fullName, name, dimensions, context);
		this.name = name;
		init();
		this.dimensions = dimensions;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeUnresolved() '" + fullName + "'");
		}
	}

	private DMType(String fullName, int dimensions) {
		super(fullName, dimensions);
		init();
		this.dimensions = dimensions;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeUnresolved() '" + fullName + "'");
		}
	}

	DMType(String fullName) {
		super(fullName);
		init();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeUnresolved() '" + fullName + "'");
		}
	}

	private DMType(DMEntity entity) {
		super(entity != null ? entity.getFullQualifiedName() : "null");
		init();
		setBaseEntity(entity);
	}

	private DMType(DMEntity entity, int dimensions) {
		this(entity);
		setDimensions(dimensions);
	}

	private DMType(DMEntity entity, int dimensions, Vector<DMType> parameters) {
		this(entity, dimensions);
		this.parameters.addAll(parameters);
		for (DMType p : this.parameters) {
			if (p != null) {
				p.setOwner(this);
			}
		}
	}

	private DMType(DMTypeVariable typeVariable) {
		this(typeVariable.getName());
		setTypeVariable(typeVariable);
	}

	private DMType(Domain domain) {
		this(domain != null ? domain.getName() : "null");
		setDomain(domain);
	}

	private DMType(Vector<DMType> someUpperBounds, Vector<DMType> someLowerBounds) {
		this("<wilcard>");
		upperBounds = new Vector<WildcardBound>();
		lowerBounds = new Vector<WildcardBound>();
		if (someUpperBounds != null) {
			for (DMType t : someUpperBounds) {
				upperBounds.add(new WildcardBound(t));
				t.setOwner(this);
			}
		}
		if (someLowerBounds != null) {
			for (DMType t : someLowerBounds) {
				lowerBounds.add(new WildcardBound(t));
				t.setOwner(this);
			}
		}
	}

	private static int TYPEID = 0;
	public final int typeId = TYPEID++;

	private void init() {
		// logger.info("New type "+Integer.toHexString(typeId));
		parameters = new Vector<DMType>();
		typedWithThisType = new Vector<Typed>();
		dimensions = 0;
		domain = null;
		typeVariable = null;
		upperBounds = null;
		lowerBounds = null;
		_parameterizedTypeVariables = null;
	}

	// Artefact for KV-coding
	// TODO: manage 'this' in inspector
	public DMType getThis() {
		return this;
	}

	// Artefact for KV-coding
	// TODO: manage 'this' in inspector
	public void setThis(DMType t) {
	}

	public synchronized void clearUnresolved() {
		triedToResolve = false;
	}

	/**
	 * Override parent implementation by providing a cache
	 */
	@Override
	public synchronized boolean isResolved() {
		if (getKindOfType() != KindOfType.UNRESOLVED) {
			return _isNotUnresolvedTypeResolved();
		}
		if (!triedToResolve) {
			// logger.info("Is type "+name+" resolved ?");
			triedToResolve = true;
			isResolved = super.isResolved();
		}
		/*
		 * if (isResolved == false) { logger.warning("Type: "+name+" is not resolvable"); }
		 */
		return isResolved;
	}

	private boolean _isNotUnresolvedTypeResolved() {
		if ((getKindOfType() == KindOfType.RESOLVED) || (getKindOfType() == KindOfType.RESOLVED_ARRAY)) {
			for (DMType p : parameters) {
				if (!p.isResolved()) {
					return false;
				}
			}
			return true;
		}

		else if (getKindOfType() == KindOfType.TYPE_VARIABLE) {
			return true;
		}

		else if (getKindOfType() == KindOfType.DKV) {
			return true;
		}

		else if (getKindOfType() == KindOfType.WILDCARD) {
			for (WildcardBound b : getUpperBounds()) {
				if (!b.bound.isResolved()) {
					return false;
				}
			}
			for (WildcardBound b : getLowerBounds()) {
				if (!b.bound.isResolved()) {
					return false;
				}
			}
			return true;
		}

		else {
			logger.warning("Unexpected KindOfType: " + getKindOfType());
			return false;
		}
	}

	public String getName() {
		if (getBaseEntity() != null) {
			return getBaseEntity().getName();
		}
		return name;
	}

	public void resolveAs(DMEntity entity) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("resolve type " + this + " as " + entity.getFullQualifiedName());
		}
		setBaseEntity(entity);
	}

	public void resolveAs(DMEntity entity, int dimensions) {
		resolveAs(entity);
		setDimensions(dimensions);
	}

	public DMTypeOwner getOwner() {
		return _owner;
	}

	public void setOwner(DMTypeOwner owner) {
		_owner = owner;
		if (_owner != null) {
			_project = _owner.getProject();
		}
	}

	public void setProject(FlexoProject project) {
		_project = project;
	}

	@Override
	public FlexoProject getProject() {
		if (getKindOfType() == KindOfType.UNRESOLVED) {
			return _project;
		} else if ((getKindOfType() == KindOfType.RESOLVED) || (getKindOfType() == KindOfType.RESOLVED_ARRAY)) {
			return getBaseEntity().getProject();
		} else if (getKindOfType() == KindOfType.DKV) {
			return getDomain().getProject();
		} else if (getKindOfType() == KindOfType.TYPE_VARIABLE) {
			return getTypeVariable().getProject();
		} else if (getKindOfType() == KindOfType.WILDCARD) {
		} else {
			logger.warning("Unexpected KindOfType: " + getKindOfType());
		}
		return _project;
	}

	@Override
	public void setChanged() {
		clearStringRepresentationCache();
		if (getOwner() != null) {
			getOwner().setChanged();
		}
	}

	/**
	 * 
	 */
	private void clearStringRepresentationCache() {
		stringRepresentationCache = null;
		fullyQualifiedStringRepresentationCache = null;
	}

	public DMEntity getBaseEntity() {
		return baseEntity;
	}

	public void setBaseEntity(DMEntity aBaseEntity) {
		if (baseEntity != aBaseEntity) {
			if (baseEntity != null) {
				baseEntity.deleteObserver(this);
			}
			baseEntity = aBaseEntity;
			if (baseEntity != null) {
				baseEntity.addObserver(this);
				setTypeVariable(null);
				setDomain(null);
			}
			if ((baseEntity == null) || (baseEntity.getTypeVariables().size() == 0)) {
				parameters.clear();
			}
			_parameterizedTypeVariables = null;
			setChanged();
		}
	}

	@Override
	public boolean isArray() {
		return getDimensions() > 0;
	}

	@Override
	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dim) {
		dimensions = dim;
		setChanged();
	}

	/**
	 * Determines if the type represented by this <code>DMType</code> object is either the same as, or is a superclass or superinterface of,
	 * the type represented by the specified <code>DMType</code> parameter. It returns <code>true</code> if so; otherwise it returns
	 * <code>false</code>.
	 * 
	 * <p>
	 * Specifically, this method tests whether the type represented by the specified <code>DMType</code> parameter can be converted to the
	 * type represented by this <code>DMType</code> object.
	 * 
	 * @param type
	 *            the <code>DMType</code> object to be checked
	 * @return the <code>boolean</code> value indicating whether objects of the type <code>type</code> can be assigned to objects of this
	 *         type
	 * 
	 * @param type
	 * @return
	 */
	public boolean isAssignableFrom(DMType type) {
		return isAssignableFrom(type, true);
	}

	/**
	 * Determines if the type represented by this <code>DMType</code> object is either the same as, or is a superclass or superinterface of,
	 * the type represented by the specified <code>DMType</code> parameter. It returns <code>true</code> if so; otherwise it returns
	 * <code>false</code>.
	 * 
	 * <p>
	 * Specifically, this method tests whether the type represented by the specified <code>DMType</code> parameter can be converted to the
	 * type represented by this <code>DMType</code> object.
	 * 
	 * @param type
	 *            the <code>DMType</code> object to be checked
	 * @return the <code>boolean</code> value indicating whether objects of the type <code>type</code> can be assigned to objects of this
	 *         type
	 * 
	 * @param type
	 * @param permissive
	 *            is a flag used to allow casts between primitives types (eg long casted to int). In this case, require an explicit cast
	 * @return
	 */
	// Is this type a parent class or interface of type with compatible parameters ?
	public boolean isAssignableFrom(DMType type, boolean permissive) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called " + this + " isAssignableFrom(" + type + ")");
		}

		// If supplied type is null return false
		if (type == null) {
			return false;
		}

		// Everything could be assigned to Object
		if (isObject()) {
			return true;
		}

		if ((getKindOfType() == KindOfType.RESOLVED) && (type.getKindOfType() == KindOfType.RESOLVED)) {

			if (isVoid()) {
				return type.isVoid();
			}

			if (isBoolean()) {
				return type.isBoolean();
			}

			if (isDouble()) {
				return type.isDouble() || type.isFloat() || type.isLong() || type.isInteger() || type.isShort() || type.isChar()
						|| type.isByte();
			}

			if (isFloat()) {
				return (type.isDouble() && permissive) || type.isFloat() || type.isLong() || type.isInteger() || type.isShort()
						|| type.isChar() || type.isByte();
			}

			if (isLong()) {
				return type.isLong() || type.isInteger() || type.isShort() || type.isChar() || type.isByte();
			}

			if (isInteger()) {
				return (type.isLong() && permissive) || type.isInteger() || type.isShort() || type.isChar() || type.isByte();
			}

			if (isShort()) {
				return (type.isLong() && permissive) || (type.isInteger() && permissive) || type.isShort() || type.isChar()
						|| type.isByte();
			}

			if (isChar()) {
				return (type.isLong() && permissive) || (type.isInteger() && permissive) || (type.isShort() && permissive) || type.isChar()
						|| (type.isShort() && permissive);
			}

			if (isByte()) {
				return (type.isLong() && permissive) || (type.isInteger() && permissive) || (type.isShort() && permissive)
						|| (type.isChar() && permissive) || type.isByte();
			}

			if (type.isBooleanPrimitive()) {
				DMType booleanType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Boolean.class));
				try {
					return isAssignableFrom(booleanType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					booleanType.setBaseEntity(null);
				}
			}
			if (type.isIntegerPrimitive()) {
				DMType intType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Integer.class));
				try {
					return isAssignableFrom(intType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					intType.setBaseEntity(null);
				}
			}
			if (type.isLongPrimitive()) {
				DMType longType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Long.class));
				try {
					return isAssignableFrom(longType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					longType.setBaseEntity(null);
				}
			}
			if (type.isShortPrimitive()) {
				DMType shortType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Short.class));
				try {
					return isAssignableFrom(shortType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					shortType.setBaseEntity(null);
				}
			}
			if (type.isDoublePrimitive()) {
				DMType doubleType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Double.class));
				try {
					return isAssignableFrom(doubleType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					doubleType.setBaseEntity(null);
				}
			}
			if (type.isFloatPrimitive()) {
				DMType floatType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Float.class));
				try {
					return isAssignableFrom(floatType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					floatType.setBaseEntity(null);
				}
			}
			if (type.isCharPrimitive()) {
				DMType characterType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Character.class));
				try {
					return isAssignableFrom(characterType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					characterType.setBaseEntity(null);
				}
			}
			if (type.isBytePrimitive()) {
				DMType byteType = makeResolvedDMType(type.getBaseEntity().getDMModel().getDMEntity(java.lang.Byte.class));
				try {
					return isAssignableFrom(byteType, permissive);
				} finally {
					// To Remove the type from observers (otherwise it grows, grows, grows....)
					byteType.setBaseEntity(null);
				}
			}

			if (!getBaseEntity().isAncestorOf(type.getBaseEntity())) {
				return false;
			}

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Entity: " + getBaseEntity() + " is ancestor of " + type.getBaseEntity());
			}

			if (getBaseEntity() == type.getBaseEntity()) {
				// Base entities are the same, let's analyse parameters

				// If one of both paramters def is empty (parameters are not defined, as before java5)
				// accept it without performing a test which is impossible to perform
				if ((getParameters().size() == 0) || (type.getParameters().size() == 0)) {
					return true;
				}

				// Now check that parameters size are the same
				if (getParameters().size() != type.getParameters().size()) {
					return false;
				}

				// Now, we have to compare parameter per parameter
				for (int i = 0; i < getParameters().size(); i++) {
					DMType localParam = getParameters().elementAt(i);
					DMType sourceParam = type.getParameters().elementAt(i);

					if ((localParam.getKindOfType() == KindOfType.WILDCARD) && (localParam.getUpperBounds().size() == 1)) {
						DMType resultingSourceParamType;
						if ((sourceParam.getKindOfType() == KindOfType.WILDCARD) && (sourceParam.getUpperBounds().size() == 1)) {
							resultingSourceParamType = sourceParam.getUpperBounds().firstElement().bound;
						} else {
							resultingSourceParamType = sourceParam;
						}
						if (!localParam.getUpperBounds().firstElement().bound.isAssignableFrom(resultingSourceParamType, permissive)) {
							return false;
						}
					} else if (!localParam.equals(sourceParam)) {
						return false;
					}
				}
				return true;
			}

			// Else it's a true ancestor
			else {
				// DMType parentType = makeInstantiatedDMType(type.getBaseEntity().getParentType(),type);
				DMType parentType = makeInstantiatedDMType(getBaseEntity().getClosestAncestorOf(type.getBaseEntity()), type);
				return isAssignableFrom(parentType, permissive);
			}

		}

		if ((getKindOfType() == KindOfType.DKV) && (type.getKindOfType() == KindOfType.DKV)) {
			return getDomain() == type.getDomain();
		}

		else if (getKindOfType() == KindOfType.WILDCARD) {
			// Not implemented yet, perform check on bounds here
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification instanceof DMEntityClassNameChanged) && (observable == getBaseEntity())) {
			// TODO: handle class name changed
			// Then forward this notification to all Typed declared to have this type
			Vector<Typed> typedWithThisTypeClone = (Vector<Typed>) typedWithThisType.clone();
			for (Typed t : typedWithThisTypeClone) {
				t.update(observable, dataModification);
			}
			clearStringRepresentationCache();
		} else if ((dataModification instanceof EntityDeleted) && (observable == getBaseEntity())) {
			// TODO: handle entity deleted
			// Then forward this notification to all Typed declared to have this type
			Vector<Typed> typedWithThisTypeClone = (Vector<Typed>) typedWithThisType.clone();
			for (Typed t : typedWithThisTypeClone) {
				t.update(observable, dataModification);
			}
			clearStringRepresentationCache();
		}
	}

	public Vector<Typed> getTypedWithThisType() {
		return typedWithThisType;
	}

	public void addToTypedWithThisType(Typed aTyped) {
		if (aTyped.getType().equals(this)) {
			typedWithThisType.add(aTyped);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Type doesn't match !");
			}
		}
	}

	public void removeFromTypedWithThisType(Typed aTyped) {
		typedWithThisType.remove(aTyped);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof DMType) {
			String stringRepresentation = getStringRepresentation();
			if (stringRepresentation == null) {
				return ((DMType) object).getStringRepresentation() == null;
			}
			return stringRepresentation.equals(((DMType) object).getStringRepresentation());
		} else if (object instanceof Type) {
			return super.equals(object);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (getStringRepresentation() != null) {
			return getStringRepresentation().hashCode();
		}
		return super.hashCode();
	}

	void notifyTypeResolved() {
		for (Typed t : getTypedWithThisType()) {
			t.update(null, new Typed.TypeResolved(this));
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Type: " + this + " is resolved: notify " + t);
			}
		}
		if (getRootType() != this) {
			if (getRootType().getKindOfType() != KindOfType.UNRESOLVED) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Type resolved: " + this + " notify root type: " + getRootType());
				}
				getRootType().notifyTypeResolved();
			}
		}
	}

	public DMType getRootType() {
		while (getOwner() instanceof DMType) {
			return ((DMType) getOwner()).getRootType();
		}
		return this;
	}

	public String getStringRepresentation() {
		return getStringRepresentation(true);
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public String getSimplifiedStringRepresentation() {
		return getStringRepresentation(false);
	}

	private String stringRepresentationCache;
	private String fullyQualifiedStringRepresentationCache;

	private String getStringRepresentation(boolean fullyQualified) {
		if (getKindOfType() == KindOfType.UNRESOLVED) {
			// logger.info("Unresolved: "+getValue());
			return getValue();
		}

		else if ((getKindOfType() == KindOfType.RESOLVED) || (getKindOfType() == KindOfType.RESOLVED_ARRAY)) {
			if (((stringRepresentationCache == null) && !fullyQualified)
					|| ((fullyQualifiedStringRepresentationCache == null) && fullyQualified)) {
				StringBuilder returned = new StringBuilder();
				returned.append(fullyQualified ? getBaseEntity().getFullQualifiedName() : getBaseEntity().getEntityClassName());
				if (parameters.size() > 0) {
					returned.append("<");
					boolean isFirst = true;
					for (DMType p : parameters) {
						if (p != null) {
							returned.append((isFirst ? "" : ", ") + p.getStringRepresentation(fullyQualified));
						}
						isFirst = false;
					}
					returned.append(">");
				}
				for (int i = 0; i < getDimensions(); i++) {
					returned.append("[]");
				}
				if (fullyQualified) {
					fullyQualifiedStringRepresentationCache = returned.toString();
				} else {
					stringRepresentationCache = returned.toString();
				}
			}
			if (fullyQualified) {
				return fullyQualifiedStringRepresentationCache;
			} else {
				return stringRepresentationCache;
			}
		}

		else if (getKindOfType() == KindOfType.DKV) {
			if (fullyQualified) {
				return DKV_PREFIX + getDomain().getName();
			} else {
				return getDomain().getName();
			}
		}

		else if (getKindOfType() == KindOfType.TYPE_VARIABLE) {
			return getTypeVariable().getName();
		}

		else if (getKindOfType() == KindOfType.WILDCARD) {
			if (((stringRepresentationCache == null) && !fullyQualified)
					|| ((fullyQualifiedStringRepresentationCache == null) && fullyQualified)) {
				StringBuilder returned = new StringBuilder();
				returned.append("?");
				if ((getUpperBounds() != null) && (getUpperBounds().size() > 0)) {
					boolean isFirst = true;
					for (WildcardBound b : getUpperBounds()) {
						if (!b.bound.isObject()) {
							returned.append((isFirst ? " extends " : " & ") + b.bound.getStringRepresentation(fullyQualified));
							isFirst = false;
						}
					}
				}
				if ((getLowerBounds() != null) && (getLowerBounds().size() > 0)) {
					boolean isFirst = true;
					for (WildcardBound b : getLowerBounds()) {
						returned.append((isFirst ? " super " : " & ") + b.bound.getStringRepresentation(fullyQualified));
						isFirst = false;
					}
				}
				if (fullyQualified) {
					fullyQualifiedStringRepresentationCache = returned.toString();
				} else {
					stringRepresentationCache = returned.toString();
				}
			}
			if (fullyQualified) {
				return fullyQualifiedStringRepresentationCache;
			} else {
				return stringRepresentationCache;
			}
		}

		else {
			logger.warning("Unexpected KindOfType: " + getKindOfType());
			return "<unexpected>";
		}
	}

	/**
	 * Return flag indicating if this type is considered as generic A generic type is a type that is parameterized with type variable(s). If
	 * this type is resolved but contains a type in it definition containing itself a generic definition, then this type is also generic
	 * (this 'isGeneric' property is recursively transmissible).
	 * 
	 * @return a flag indicating whether this type is resolved or not
	 */
	public boolean isGeneric() {
		if (getKindOfType() == KindOfType.UNRESOLVED) {
			return false;
		} else if ((getKindOfType() == KindOfType.RESOLVED) || (getKindOfType() == KindOfType.RESOLVED_ARRAY)) {
			if (parameters.size() > 0) {
				for (DMType p : parameters) {
					if (p.isGeneric()) {
						return true;
					}
				}
			}
			return false;
		}

		else if (getKindOfType() == KindOfType.DKV) {
			return false;
		}

		else if (getKindOfType() == KindOfType.TYPE_VARIABLE) {
			return true;
		}

		else if (getKindOfType() == KindOfType.WILDCARD) {
			if ((getUpperBounds() != null) && (getUpperBounds().size() > 0)) {
				for (WildcardBound b : getUpperBounds()) {
					if (b.bound.isGeneric()) {
						return true;
					}
				}
			}
			if ((getLowerBounds() != null) && (getLowerBounds().size() > 0)) {
				for (WildcardBound b : getLowerBounds()) {
					if (b.bound.isGeneric()) {
						return true;
					}
				}
			}
			return false;
		}

		else {
			logger.warning("Unexpected KindOfType: " + getKindOfType());
			return false;
		}
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain aDomain) {
		if (domain != aDomain) {
			this.domain = aDomain;
			if (domain != null) {
				setBaseEntity(null);
				setTypeVariable(null);
			}
			setChanged();
		}
	}

	public DMTypeVariable getTypeVariable() {
		return typeVariable;
	}

	public void setTypeVariable(DMTypeVariable typeVariable) {
		if (this.typeVariable != typeVariable) {
			this.typeVariable = typeVariable;
			if (typeVariable != null) {
				setBaseEntity(null);
				setDomain(null);
			}
			setChanged();
		}
	}

	public DMGenericDeclaration getTypeVariableContext() {
		// logger.info("Type: "+this+" owner="+getOwner()+" "+(getOwner()!=null && getOwner() instanceof
		// DMType?"owner context="+((DMType)getOwner()).getTypeVariableContext():null));
		if ((getOwner() != null) && (getOwner() instanceof DMGenericDeclaration)) {
			return (DMGenericDeclaration) getOwner();
		}
		if (getOwner() instanceof DMType) {
			return ((DMType) getOwner()).getTypeVariableContext();
		}
		return null;
		// return typeVariableContext;
	}

	/*
	 * public void setTypeVariableContext(DMGenericDeclaration typeVariableContext) { this.typeVariableContext = typeVariableContext; }
	 */

	public class WildcardBound extends TemporaryFlexoModelObject implements InspectableObject, DMTypeOwner {
		public WildcardBound(DMType aType) {
			super();
			bound = aType;
		}

		public DMType bound;

		@Override
		public String getInspectorName() {
			// never inspected by its own
			return null;
		}

		public String getSuperLabel() {
			return "? super ";
		}

		public String getExtendsLabel() {
			return "? extends ";
		}

		public DMTypeOwner getOwner() {
			if (bound != null) {
				return bound.getOwner();
			}
			return DMType.this.getOwner();
		}

		@Override
		public FlexoProject getProject() {
			return DMType.this.getProject();
		}

	}

	public Vector<WildcardBound> getLowerBounds() {
		return lowerBounds;
	}

	public Vector<WildcardBound> getUpperBounds() {
		return upperBounds;
	}

	public void addToUpperBounds(DMType type) {
		addToUpperBounds(new WildcardBound(type));
	}

	public void addToUpperBounds(WildcardBound bound) {
		upperBounds.add(bound);
		setChanged();
	}

	public void setLowerBounds(Vector<WildcardBound> lowerBounds) {
		this.lowerBounds = lowerBounds;
		setChanged();
	}

	public void setUpperBounds(Vector<WildcardBound> upperBounds) {
		this.upperBounds = upperBounds;
		setChanged();
	}

	public void addToLowerBounds(DMType type) {
		addToLowerBounds(new WildcardBound(type));
		setChanged();
	}

	public void addToLowerBounds(WildcardBound bound) {
		lowerBounds.add(bound);
	}

	public Vector<DMType> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<DMType> parameters) {
		this.parameters = parameters;
		setChanged();
	}

	public void setParameterAtIndex(DMType parameter, int index) {
		while (parameters.size() <= index) {
			DMType newObjectType;
			if (getProject() != null) {
				newObjectType = makeResolvedDMType(getProject().getDataModel().getDMEntity(Object.class));
			} else {
				newObjectType = makeUnresolvedDMType("java.lang.Object");
			}
			newObjectType.setOwner(DMType.this);
			parameters.add(newObjectType);
		}
		if ((parameters.elementAt(index) == null) || (parameters.elementAt(index) != parameter)) {
			parameters.setElementAt(parameter, index);
			parameter.setOwner(DMType.this);
			setChanged();
		}
	}

	public Vector<ParameterizedTypeVariable> getParameterizedTypeVariables() {
		if (_parameterizedTypeVariables == null) {
			_parameterizedTypeVariables = new Vector<ParameterizedTypeVariable>();
			if (getBaseEntity() != null) {
				for (DMTypeVariable tv : getBaseEntity().getTypeVariables()) {
					_parameterizedTypeVariables.add(new ParameterizedTypeVariable(tv));
				}
			}
		}
		return _parameterizedTypeVariables;
	}

	public class ParameterizedTypeVariable extends TemporaryFlexoModelObject implements InspectableObject {
		private final DMTypeVariable _typeVariable;

		protected ParameterizedTypeVariable(DMTypeVariable typeVariable) {
			super();
			_typeVariable = typeVariable;
		}

		@Override
		public String getInspectorName() {
			// Never inspected by its own
			return null;
		}

		public DMTypeVariable getTypeVariable() {
			return _typeVariable;
		}

		public DMType getValue() {
			int index = -1;
			if (getBaseEntity() != null) {
				index = getBaseEntity().getTypeVariables().indexOf(_typeVariable);
			}
			if (index < 0) {
				return null;
			} else {
				if (getParameters().size() > index) {
					return getParameters().elementAt(index);
				} else {
					return null;
				}
			}
		}

		public void setValue(DMType aType) {
			int index = -1;
			if (getBaseEntity() != null) {
				index = getBaseEntity().getTypeVariables().indexOf(_typeVariable);
			}
			if (index < 0) {
				return;
			} else {
				setParameterAtIndex(aType, index);
				/*
				 * while(getParameters().size() <= index) { DMType newObjectType =
				 * makeResolvedDMType(getBaseEntity().getDMModel().getDMEntity(Object.class)); newObjectType.setOwner(DMType.this);
				 * getParameters().add(newObjectType); } getParameters().setElementAt(aType,index); aType.setOwner(DMType.this);
				 * setChanged();
				 */
			}
		}

		@Override
		public FlexoProject getProject() {
			return DMType.this.getProject();
		}

		public DMType getOwner() {
			return DMType.this;
		}
	}

	public KindOfType getKindOfType() {
		if (getBaseEntity() != null) {
			if (getDimensions() == 0) {
				return KindOfType.RESOLVED;
			} else {
				return KindOfType.RESOLVED_ARRAY;
			}
		}
		if (getDomain() != null) {
			return KindOfType.DKV;
		}
		if (getTypeVariable() != null) {
			return KindOfType.TYPE_VARIABLE;
		}
		if ((getLowerBounds() != null) || (getUpperBounds() != null)) {
			return KindOfType.WILDCARD;
		}
		return KindOfType.UNRESOLVED;
	}

	public enum KindOfType implements StringConvertable {
		UNRESOLVED, RESOLVED, RESOLVED_ARRAY, DKV, TYPE_VARIABLE, WILDCARD;

		public String getUnlocalizedStringRepresentation() {
			if (this == UNRESOLVED) {
				return "unresolved_type";
			} else if (this == RESOLVED) {
				return "basic_type";
			} else if (this == RESOLVED_ARRAY) {
				return "array_type";
			} else if (this == DKV) {
				return "dkv_type";
			} else if (this == TYPE_VARIABLE) {
				return "type_variable_type";
			} else if (this == WILDCARD) {
				return "wildcard_type";
			}
			return "???";
		}

		public String getStringRepresentation() {
			return FlexoLocalization.localizedForKey(getUnlocalizedStringRepresentation());
		}

		@Override
		public StringEncoder.Converter<KindOfType> getConverter() {
			return kindOfTypeConverter;
		}

		public static final StringEncoder.Converter<KindOfType> kindOfTypeConverter = new Converter<KindOfType>(KindOfType.class) {

			@Override
			public KindOfType convertFromString(String value) {
				for (KindOfType cs : values()) {
					if (cs.getStringRepresentation().equals(value)) {
						return cs;
					}
				}
				return null;
			}

			@Override
			public String convertToString(KindOfType value) {
				return value.getStringRepresentation();
			}

		};

	}

	public boolean isDKV() {
		return getKindOfType() == KindOfType.DKV;
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static class DMTypeStringConverter extends StringEncoder.Converter<DMType> {
		private DMModel _dataModel;
		private final Vector<DMType> _pendingDeserializedTypes;
		private DMGenericDeclaration _converterTypeVariableContext;

		public DMTypeStringConverter(DMModel model) {
			super(DMType.class);
			_dataModel = model;
			_pendingDeserializedTypes = new Vector<DMType>();
		}

		protected void dataModelStartDeserialization(DMModel dataModel) {
			_dataModel = dataModel;
			_pendingDeserializedTypes.clear();
		}

		protected void dataModelFinishDeserialization(DMModel dataModel) {
			logger.fine("BEGIN dataModelFinishDeserialization");
			int stillToDecode = _pendingDeserializedTypes.size();
			while (stillToDecode > 0) {
				logger.fine("Trying to decode remaining " + stillToDecode + " types");
				tryToDecodeTypes();
				if (stillToDecode <= _pendingDeserializedTypes.size()) {
					logger.warning("Some types could not be decoded. Abort");
					logger.info("stillToDecode was:" + stillToDecode + " is now " + _pendingDeserializedTypes.size());
					for (DMType t : _pendingDeserializedTypes) {
						logger.warning("Undecoded type: " + t.getStringRepresentation());
					}
					return;
				}
				stillToDecode = _pendingDeserializedTypes.size();
			}
			logger.fine("END dataModelFinishDeserialization, stillToDecode=" + stillToDecode);
		}

		@Override
		public DMType convertFromString(String aValue) {
			return convertFromString(aValue, null, _dataModel.getProject());
		}

		public DMType convertFromString(String aValue, DMTypeOwner owner, FlexoProject project) {

			ParsedTypeInfo infos = new ParsedTypeInfo(aValue);
			DMType returned = new DMType(infos.baseString);
			if (project != null) {
				returned.setProject(project);
			}
			if (owner != null) {
				returned.setOwner(owner);
			}
			returned._pendingInformations = infos;

			if (!decodeType(returned)) {
				_pendingDeserializedTypes.add(returned);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Decoding failed for " + aValue);
				}
			}
			return returned;
		}

		@SuppressWarnings("unchecked")
		private void tryToDecodeTypes() {
			Vector<DMType> decodedTypes = new Vector<DMType>();
			Vector<DMType> typesToDecode = new Vector<DMType>(_pendingDeserializedTypes);
			for (DMType t : typesToDecode) {
				if (decodeType(t)) {
					decodedTypes.add(t);
				}
			}
			_pendingDeserializedTypes.removeAll(decodedTypes);
		}

		private boolean decodeType(DMType type) {

			boolean returned = true;
			if (type._pendingInformations == null) {
				return false;
			}

			if (type._pendingInformations.dkvDomain != null) {
				if (_dataModel == null) {
					returned = false;
				} else {
					DKVModel dkvModel = _dataModel.getProject().getDKVModel();
					if (dkvModel == null) {
						returned = false;
					} else {
						Domain domain = dkvModel.getDomainNamed(type._pendingInformations.dkvDomain);
						if (domain == null) {
							returned = false;
						} else {
							type.setDomain(domain);
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Successfully decoded " + type + " as DKV");
							}
							returned = true;
						}
					}
				}
			} else {
				if (_dataModel == null) {
					returned = false;
				} else {
					DMEntity entity = _dataModel.getDMEntity(type._pendingInformations.baseString);
					if (entity == null) {
						returned = false;
					} else {
						type.setBaseEntity(entity);
					}
				}
				type.setDimensions(type._pendingInformations.dimensions);
				if (!type._pendingInformations.parametersAdded) {
					for (String param : type._pendingInformations.parameters) {
						DMType t = convertFromString(param);
						type.parameters.add(t);
						t.setOwner(type);
					}
					type._pendingInformations.parametersAdded = true;
				}

				// May be it's a type variable ???
				if ((returned == false) && (type.getTypeVariableContext() != null)) {
					for (DMTypeVariable tv : type.getTypeVariableContext().getTypeVariables()) {
						if (type._pendingInformations.baseString.equals(tv.getName())) {
							type.setTypeVariable(tv);
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Successfully decoded " + type + " as TypeVariable");
							}
							returned = true;
						}
					}
				}

				// Or may be it's a wildcard ???
				if (returned == false) {
					String supposedWildcard = type._pendingInformations.baseString.trim();
					if (supposedWildcard.indexOf("?") == 0) {
						String extendsString = "";
						String superString = "";
						int extendsBeginIndex = supposedWildcard.indexOf("extends");
						int superBeginIndex = supposedWildcard.indexOf("super");
						if (extendsBeginIndex >= 0) { // extends found
							if (superBeginIndex >= 0) { // super found
								extendsString = supposedWildcard.substring(extendsBeginIndex + 7, superBeginIndex).trim();
								superString = supposedWildcard.substring(superBeginIndex + 5).trim();
							} else {
								extendsString = supposedWildcard.substring(extendsBeginIndex + 7).trim();
							}
						} else {
							if (superBeginIndex >= 0) { // super found
								superString = supposedWildcard.substring(superBeginIndex + 5).trim();
							}
						}

						type.upperBounds = new Vector<WildcardBound>();
						type.lowerBounds = new Vector<WildcardBound>();
						StringTokenizer st = new StringTokenizer(extendsString, "&");
						while (st.hasMoreElements()) {
							DMType t = convertFromString(st.nextToken().trim());
							t.setOwner(type);
							type.addToUpperBounds(t);
						}
						st = new StringTokenizer(superString, "&");
						while (st.hasMoreElements()) {
							DMType t = convertFromString(st.nextToken().trim());
							t.setOwner(type);
							type.addToLowerBounds(t);
						}
						if (type.upperBounds.size() == 0) {
							DMType newObjectType = convertFromString("java.lang.Object");
							newObjectType.setOwner(type);
							type.addToUpperBounds(newObjectType);
						}

						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Successfully decoded " + type + " as WildcardType");
						}
						returned = true;

					}
				}
			}

			if (returned) {

				type._pendingInformations = null;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Type " + type + " successfully decoded");
				}

				type.notifyTypeResolved();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Type " + type._pendingInformations + " is pending owner=" + type.getOwner() + "variableContext="
							+ type.getTypeVariableContext() + " " + Integer.toHexString(type.hashCode()));
				}
			}
			return returned;
		}

		@Override
		public String convertToString(DMType value) {
			return value.getStringRepresentation();
		}

		public DMGenericDeclaration getConverterTypeVariableContext() {
			return _converterTypeVariableContext;
		}

		public void setConverterTypeVariableContext(DMGenericDeclaration typeVariableContext) {
			this._converterTypeVariableContext = typeVariableContext;
		}

		protected class ParsedTypeInfo {
			protected String baseString;
			protected int dimensions;
			Vector<String> parameters;
			protected String dkvDomain;

			boolean parametersAdded = false;

			public ParsedTypeInfo(String value) {
				String parsed = value;
				dimensions = 0;
				parameters = new Vector<String>();

				if (value.startsWith(DKV_PREFIX)) {
					dkvDomain = value.substring(DKV_PREFIX.length());
					return;
				}

				while (parsed.endsWith("[]")) {
					dimensions++;
					parsed = parsed.substring(0, parsed.length() - 2);
				}

				if ((parsed.indexOf("<") > -1) && (parsed.lastIndexOf(">") > parsed.indexOf("<"))) {
					String params = parsed.substring(parsed.indexOf("<") + 1, parsed.lastIndexOf(">"));

					DMTypeTokenizer tt = new DMTypeTokenizer(params);
					while (tt.hasMoreTokens()) {
						parameters.add(tt.nextToken());
					}

					parsed = parsed.substring(0, parsed.indexOf("<"));
				}

				baseString = parsed.trim();
			}

			@Override
			public String toString() {
				return "base: " + baseString + " dims=" + dimensions + " parameters(" + parameters.size() + ")=" + parameters;
			}

		}
	}

	@Override
	public DMTypeStringConverter getConverter() {
		if (getProject() != null) {
			return getProject().getDataModel().getDmTypeConverter();
		}
		if (_project != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Weird type: getProject returns null but _project is not null!");
			}
			return _project.getDataModel().getDmTypeConverter();
		}
		return null;
	}

	// ==========================================================
	// ===================== Utilities ==========================
	// ==========================================================

	public static int arrayDepth(Class c) {
		Class current = c;
		int depth = 0;
		current = current.getComponentType();
		while (current != null) {
			current = current.getComponentType();
			depth++;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Class " + c + " array depth = " + depth);
		}
		return depth;
	}

	@Override
	public boolean isVoid() {
		if (isResolved() && (getKindOfType() == KindOfType.RESOLVED)) {
			return getBaseEntity().isVoid();
		}
		return super.isVoid();
	}

	public boolean isBoolean() {
		return _isBasicType()
				&& (isBooleanPrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Boolean.class)));
	}

	public boolean isBooleanPrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("boolean"));
	}

	public boolean isInteger() {
		return _isBasicType()
				&& (isIntegerPrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Integer.class)));
	}

	public boolean isIntegerPrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("int"));
	}

	public boolean isLong() {
		return _isBasicType() && (isLongPrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Long.class)));
	}

	public boolean isLongPrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("long"));
	}

	public boolean isShort() {
		return _isBasicType()
				&& (isShortPrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Short.class)));
	}

	public boolean isShortPrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("short"));
	}

	public boolean isChar() {
		return _isBasicType()
				&& (isCharPrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Character.class)));
	}

	public boolean isCharPrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("char"));
	}

	public boolean isByte() {
		return _isBasicType() && (isBytePrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Byte.class)));
	}

	public boolean isBytePrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("byte"));
	}

	public boolean isFloat() {
		return _isBasicType()
				&& (isFloatPrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Float.class)));
	}

	public boolean isFloatPrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("float"));
	}

	public boolean isDouble() {
		return _isBasicType()
				&& (isDoublePrimitive() || (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Double.class)));
	}

	public boolean isDoublePrimitive() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getEntityNamed("double"));
	}

	public boolean isString() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.String.class));
	}

	public boolean isObject() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(java.lang.Object.class));
	}

	public boolean isDuration() {
		return _isBasicType() && (getBaseEntity() == getBaseEntity().getDMModel().getDMEntity(org.openflexo.toolbox.Duration.class));
	}

	/**
	 * @return
	 */
	public boolean isDate() {
		return _isBasicType() && getBaseEntity().getDMModel().getDMEntity(Date.class).isAncestorOf(getBaseEntity());
	}

	public boolean isEOEntity() {
		return _isBasicType() && (getBaseEntity() instanceof DMEOEntity);
	}

	public boolean isBasicType() {
		return _isBasicType() && (getBaseEntity().getTypeVariables().size() == 0);
	}

	private boolean _isBasicType() {
		return (getKindOfType() == KindOfType.RESOLVED) && (getDimensions() == 0);
	}

	@Override
	public boolean isPrimitive() {
		return isBooleanPrimitive() || isIntegerPrimitive() || isCharPrimitive() || isFloatPrimitive() || isDoublePrimitive()
				|| isLongPrimitive() || isBytePrimitive() || isShortPrimitive();

	}

	// ==========================================================
	// ============ Key/Value coding implementation =============
	// ==========================================================

	@Override
	public String valueForKey(String key) {
		if (getProject() != null) {
			return KeyValueDecoder.valueForKey(this, key, getProject().getStringEncoder());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null for " + this);
			}
			return KeyValueDecoder.valueForKey(this, key);
		}
	}

	@Override
	public void setValueForKey(String valueAsString, String key) {
		if (getProject() != null) {
			KeyValueCoder.setValueForKey(this, valueAsString, key, getProject().getStringEncoder());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null for " + this);
			}
			KeyValueCoder.setValueForKey(this, valueAsString, key);
		}
	}

	@Override
	public boolean booleanValueForKey(String key) {
		return KeyValueDecoder.booleanValueForKey(this, key);
	}

	@Override
	public byte byteValueForKey(String key) {
		return KeyValueDecoder.byteValueForKey(this, key);
	}

	@Override
	public char characterForKey(String key) {
		return KeyValueDecoder.characterValueForKey(this, key);
	}

	@Override
	public double doubleValueForKey(String key) {
		return KeyValueDecoder.doubleValueForKey(this, key);
	}

	@Override
	public float floatValueForKey(String key) {
		return KeyValueDecoder.floatValueForKey(this, key);
	}

	@Override
	public int integerValueForKey(String key) {
		return KeyValueDecoder.integerValueForKey(this, key);
	}

	@Override
	public long longValueForKey(String key) {
		return KeyValueDecoder.longValueForKey(this, key);
	}

	@Override
	public short shortValueForKey(String key) {
		return KeyValueDecoder.shortValueForKey(this, key);
	}

	@Override
	public void setBooleanValueForKey(boolean value, String key) {
		KeyValueCoder.setBooleanValueForKey(this, value, key);
	}

	@Override
	public void setByteValueForKey(byte value, String key) {
		KeyValueCoder.setByteValueForKey(this, value, key);
	}

	@Override
	public void setCharacterForKey(char value, String key) {
		KeyValueCoder.setCharacterValueForKey(this, value, key);
	}

	@Override
	public void setDoubleValueForKey(double value, String key) {
		KeyValueCoder.setDoubleValueForKey(this, value, key);
	}

	@Override
	public void setFloatValueForKey(float value, String key) {
		KeyValueCoder.setFloatValueForKey(this, value, key);
	}

	@Override
	public void setIntegerValueForKey(int value, String key) {
		KeyValueCoder.setIntegerValueForKey(this, value, key);
	}

	@Override
	public void setLongValueForKey(long value, String key) {
		KeyValueCoder.setLongValueForKey(this, value, key);
	}

	@Override
	public void setShortValueForKey(short value, String key) {
		KeyValueCoder.setShortValueForKey(this, value, key);
	}

	@Override
	public Object objectForKey(String key) {
		return KeyValueDecoder.objectForKey(this, key);
	}

	@Override
	public void setObjectForKey(Object value, String key) {
		KeyValueCoder.setObjectForKey(this, value, key);
	}

	// Retrieving type

	@Override
	public Class getTypeForKey(String key) {
		return KeyValueDecoder.getTypeForKey(this, key);
	}

	@Override
	public boolean isSingleProperty(String key) {
		return KeyValueDecoder.isSingleProperty(this, key);
	}

	@Override
	public boolean isArrayProperty(String key) {
		return KeyValueDecoder.isArrayProperty(this, key);
	}

	@Override
	public boolean isVectorProperty(String key) {
		return KeyValueDecoder.isVectorProperty(this, key);
	}

	@Override
	public boolean isHashtableProperty(String key) {
		return KeyValueDecoder.isHashtableProperty(this, key);
	}

	// ==========================================================
	// =================== InspectableObject ====================
	// ==========================================================

	@Override
	public void addInspectorObserver(InspectorObserver obs) {
		// useless
	}

	@Override
	public void deleteInspectorObserver(InspectorObserver obs) {
		// useless
	}

	@Override
	public String getInspectorName() {
		// useless
		return null;
	}

	/**
	 * Tokenizer for DMType Tokenize a String using ',' (comma) while escaping < > We make here complex parsing maintaining nested level
	 * 
	 * @author sylvain
	 * 
	 */
	public static class DMTypeTokenizer {
		private final Enumeration<String> tokens;

		public DMTypeTokenizer(String stringToParse) {
			Vector<String> computedTokens = new Vector<String>();
			// Note: it's not enough to StringTokenize commas (,) since nested <> levels may occur
			// Some we have to make complex parsing maintaining nested level
			StringTokenizer st = new StringTokenizer(stringToParse, ",<>", true);
			String nextTokenToConsider = "";
			int nestedLevel = 0;
			while (st.hasMoreTokens()) {
				String nextToken = st.nextToken();
				if (nestedLevel > 0) {
					nextTokenToConsider += nextToken;
				} else {
					if (nextToken.equals("<")) {
						nextTokenToConsider += "<";
					} else if (nextToken.equals(">")) {
						nextTokenToConsider += ">";
					} else if (nextToken.equals(",")) {
						computedTokens.add(nextTokenToConsider);
						nextTokenToConsider = "";
					} else {
						nextTokenToConsider += nextToken;
					}
				}
				if (nextToken.equals("<")) {
					nestedLevel++;
				}
				if (nextToken.equals(">")) {
					nestedLevel--;
				}
			}
			if (!nextTokenToConsider.equals("")) {
				computedTokens.add(nextTokenToConsider);
				nextTokenToConsider = "";
			}
			tokens = computedTokens.elements();
		}

		public boolean hasMoreTokens() {
			return tokens.hasMoreElements();
		}

		public String nextToken() {
			return tokens.nextElement();
		}

	}

	public String getDefaultValue() {
		if (isPrimitive() && (getDimensions() == 0)) {
			if (isBooleanPrimitive()) {
				return "false";
			} else if (isBytePrimitive()) {
				return "0";
			} else if (isCharPrimitive()) {
				return "' '";
			} else if (isDoublePrimitive()) {
				return "0.0d";
			} else if (isFloatPrimitive()) {
				return "0.0f";
			} else if (isIntegerPrimitive()) {
				return "0";
			} else if (isLongPrimitive()) {
				return "0L";
			} else if (isShortPrimitive()) {
				return "0";
			} else {
				return "null";
			}
		} else {
			return "null";
		}
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public String getInspectorTitle() {
		return null;
	}

	@Override
	public Vector<TabModel> inspectionExtraTabs() {
		// TODO Auto-generated method stub
		return null;
	}

}
