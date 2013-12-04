/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.viewpoint.AbstractActionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeActionType;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.EditionSchemeParametersType;
import org.openflexo.foundation.viewpoint.EditionSchemeParametersValuesType;
import org.openflexo.foundation.viewpoint.EditionSchemeType;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;

public final class EditionPatternBindingFactory extends JavaBindingFactory {
	static final Logger logger = Logger.getLogger(EditionPatternBindingFactory.class.getPackage().getName());

	private Map<BindingPathElement, Map<Object, SimplePathElement>> storedBindingPathElements;
	private ViewPoint viewPoint;

	private Map<BindingPathElement, Map<EditionPattern, List<EditionSchemePathElement>>> editionSchemePathElements;

	public EditionPatternBindingFactory(ViewPoint viewPoint) {
		storedBindingPathElements = new HashMap<BindingPathElement, Map<Object, SimplePathElement>>();
		editionSchemePathElements = new HashMap<BindingPathElement, Map<EditionPattern, List<EditionSchemePathElement>>>();
		this.viewPoint = viewPoint;
	}

	protected SimplePathElement getSimplePathElement(Object object, BindingPathElement parent) {
		Map<Object, SimplePathElement> storedValues = storedBindingPathElements.get(parent);
		if (storedValues == null) {
			storedValues = new HashMap<Object, SimplePathElement>();
			storedBindingPathElements.put(parent, storedValues);
		}
		SimplePathElement returned = storedValues.get(object);
		if (returned == null) {
			returned = makeSimplePathElement(object, parent);
			storedValues.put(object, returned);
		}
		return returned;
	}

	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		if (object instanceof PatternRole) {
			return new EditionPatternPatternRolePathElement<PatternRole<?>>(parent, (PatternRole<?>) object);
		}
		if (object instanceof ModelSlot) {
			return new VirtualModelModelSlotPathElement<ModelSlot>(parent, (ModelSlot) object);
		}
		if (object instanceof EditionSchemeParameter) {
			if (parent.getType() instanceof EditionSchemeParametersType) {
				return new EditionSchemeParameterDefinitionPathElement(parent, (EditionSchemeParameter) object);
			} else if (parent.getType() instanceof EditionSchemeParametersValuesType) {
				return new EditionSchemeParameterValuePathElement(parent, (EditionSchemeParameter) object);
			}
		}
		logger.warning("Unexpected " + object + " for parent=" + parent);
		return null;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {

		Type pType = parent.getType();
		
		if (parent.getType() instanceof TechnologySpecificCustomType) {
			TechnologySpecificCustomType parentType = (TechnologySpecificCustomType) pType;
			TechnologyAdapter ta = parentType.getTechnologyAdapter();
			if (ta != null && ta.getTechnologyAdapterBindingFactory().handleType(parentType)) {
				List<? extends SimplePathElement> returned = ta.getTechnologyAdapterBindingFactory()
						.getAccessibleSimplePathElements(parent);
				Collections.sort(returned, BindingPathElement.COMPARATOR);
				return returned;
			}
		}

		if (pType instanceof EditionSchemeParametersType) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme es = ((EditionSchemeParametersType) pType).getEditionScheme();
			for (EditionSchemeParameter p : es.getParameters()) {
				returned.add(getSimplePathElement(p, parent));
			}
			Collections.sort(returned, BindingPathElement.COMPARATOR);
			return returned;
		}
		else if (pType instanceof EditionSchemeParametersValuesType) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme es = ((EditionSchemeParametersValuesType) pType).getEditionScheme();
			for (EditionSchemeParameter p : es.getParameters()) {
				returned.add(getSimplePathElement(p, parent));
			}
			Collections.sort(returned, BindingPathElement.COMPARATOR);
			return returned;
		}
		/*if (parent instanceof EditionSchemeParametersBindingVariable) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme es = ((EditionSchemeParametersBindingVariable) parent).getEditionScheme();
			for (EditionSchemeParameter p : es.getParameters()) {
				returned.add(getSimplePathElement(p, parent));
			}
			Collections.sort(returned, BindingPathElement.COMPARATOR);
			return returned;
		} else if (parent instanceof EditionSchemeParametersPathElement) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme es = ((EditionSchemeParametersPathElement) parent).getEditionScheme();
			for (EditionSchemeParameter p : es.getParameters()) {
				returned.add(getSimplePathElement(p, parent));
			}
			Collections.sort(returned, BindingPathElement.COMPARATOR);
			return returned;
		} else if (parent instanceof EditionSchemeParametersValuesPathElement) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme es = ((EditionSchemeParametersValuesPathElement) parent).getEditionScheme();
			for (EditionSchemeParameter p : es.getParameters()) {
				returned.add(getSimplePathElement(p, parent));
			}
			Collections.sort(returned, BindingPathElement.COMPARATOR);
			return returned;
		}*//*else if (TypeUtils.isTypeAssignableFrom(EditionPattern.class, pType)) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
				EditionPattern ep = (EditionPattern) pType;
				for (PatternRole<?> pr : ep.getPatternRoles()) {
					returned.add(getSimplePathElement(pr, parent));
				}
			return returned;
			} */
		else if (pType instanceof EditionPatternInstanceType) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionPattern ep = ((EditionPatternInstanceType) pType).getEditionPattern();
			if (ep instanceof VirtualModel) {
				VirtualModel<?> vm = (VirtualModel<?>) ep;
				for (ModelSlot ms : vm.getModelSlots()) {
					returned.add(getSimplePathElement(ms, parent));
				}
			}
			for (PatternRole<?> pr : ep.getPatternRoles()) {
				returned.add(getSimplePathElement(pr, parent));
			}
			// TODO: performance issue
			if (ep.getInspector().getRenderer().isSet() && ep.getInspector().getRenderer().isValid()) {
				returned.add(new EPIRendererPathElement(parent));
			}
			return returned;
		} else if (pType instanceof EditionSchemeType) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme editionScheme = ((EditionSchemeType) pType).getEditionScheme();
			returned.add(new EditionSchemeParametersValuesPathElement(parent, editionScheme));
			returned.add(new EditionSchemeParametersDefinitionsPathElement(parent, editionScheme));
			for (PatternRole<?> pr : editionScheme.getEditionPattern().getPatternRoles()) {
				returned.add(getSimplePathElement(pr, parent));
			}
			return returned;
		} else if (pType instanceof EditionSchemeActionType) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme editionScheme = ((EditionSchemeActionType) pType).getEditionScheme();
			returned.add(new EditionSchemeParametersValuesPathElement(parent, editionScheme));
			returned.add(new EditionSchemeParametersDefinitionsPathElement(parent, editionScheme));
			for (PatternRole<?> pr : editionScheme.getEditionPattern().getPatternRoles()) {
				returned.add(getSimplePathElement(pr, parent));
			}
			return returned;
		}

		// In all other cases, consider it using Java rules
		return super.getAccessibleSimplePathElements(parent);
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {

		Type pType = parent.getType();
		if (pType instanceof EditionPatternInstanceType) {
			return getEditionSchemePathElements(parent, ((EditionPatternInstanceType) pType).getEditionPattern());
		}
		return super.getAccessibleFunctionPathElements(parent);
	}

	public List<EditionSchemePathElement> getEditionSchemePathElements(BindingPathElement parent, EditionPattern ep) {
		Map<EditionPattern, List<EditionSchemePathElement>> map = editionSchemePathElements.get(parent);
		if (map == null) {
			map = new HashMap<EditionPattern, List<EditionSchemePathElement>>();
			editionSchemePathElements.put(parent, map);
		}
		List<EditionSchemePathElement> returned = map.get(ep);
		if (returned == null) {
			returned = new ArrayList<EditionSchemePathElement>();
			for (AbstractActionScheme as : ep.getAbstractActionSchemes()) {
				returned.add(new EditionSchemePathElement(parent, as, null));
			}
			map.put(ep, returned);
		}
		return returned;
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement parent, String propertyName) {
		// We want to avoid code duplication, so iterate on all accessible simple path element and choose the right one
		SimplePathElement returned = null;
		for (SimplePathElement e : getAccessibleSimplePathElements(parent)) {
			if (e.getLabel().equals(propertyName)) {
				returned = e;
			}
		}
		// We cannot find a simple path element at this level, retrieve from java
		if (returned == null) {
			returned = super.makeSimplePathElement(parent, propertyName);
		}
		// Hook to specialize type returned by EditionSchemeAction.getEditionScheme()
		// This method is used while executing DiagramElement inspectors
		if (propertyName.equals("editionScheme") && (parent.getType() instanceof EditionSchemeActionType)) {
			returned.setType(EditionSchemeType.getEditionSchemeType(((EditionSchemeActionType) parent.getType()).getEditionScheme()));
		}
		return returned;
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement parent, Function function, List<DataBinding<?>> args) {
		// System.out.println("makeFunctionPathElement with " + parent + " function=" + function + " args=" + args);
		if (parent.getType() == null) {
			return null;
		}
		if (parent.getType() instanceof EditionPatternInstanceType && function instanceof EditionScheme) {
			return new EditionSchemePathElement(parent, (EditionScheme) function, null);
		}
		FunctionPathElement returned = super.makeFunctionPathElement(parent, function, args);
		// Hook to specialize type returned by getEditionPatternInstance(String)
		// This method is used while executing DiagramElement inspectors
		if (function.getName().equals("getEditionPatternInstance")) {
			if (TypeUtils.isTypeAssignableFrom(ViewObject.class, parent.getType()) && args.size() == 1 && args.get(0).isStringConstant()) {
				String editionPatternId = ((StringConstant) args.get(0).getExpression()).getValue();
				EditionPattern ep = viewPoint.getEditionPattern(editionPatternId);
				returned.setType(EditionPatternInstanceType.getEditionPatternInstanceType(ep));
			}
		}
		return returned;
	}

	@Override
	public Function retrieveFunction(Type parentType, String functionName, List<DataBinding<?>> args) {
		if (parentType instanceof EditionPatternInstanceType) {
			return ((EditionPatternInstanceType) parentType).getEditionPattern().getEditionScheme(functionName);
		}
		return super.retrieveFunction(parentType, functionName, args);
	}
}