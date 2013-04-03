package org.openflexo.foundation.viewpoint.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;
import org.openflexo.foundation.viewpoint.ViewPoint;

public final class EditionPatternBindingFactory extends JavaBindingFactory {
	static final Logger logger = Logger.getLogger(EditionPatternBindingFactory.class.getPackage().getName());

	private Map<BindingPathElement, Map<Object, SimplePathElement>> storedBindingPathElements;
	private ViewPoint viewPoint;

	public EditionPatternBindingFactory(ViewPoint viewPoint) {
		storedBindingPathElements = new HashMap<BindingPathElement, Map<Object, SimplePathElement>>();
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
		if (object instanceof EditionSchemeParameter) {
			return new EditionSchemeParameterPathElement(parent, (EditionSchemeParameter) object);
		}
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {

		if (parent.getType() instanceof TechnologySpecificCustomType) {
			TechnologySpecificCustomType parentType = (TechnologySpecificCustomType) parent.getType();
			TechnologyAdapter<?, ?> ta = parentType.getTechnologyAdapter();
			if (ta != null && ta.getTechnologyAdapterBindingFactory().handleType(parentType)) {
				List<? extends SimplePathElement> returned = ta.getTechnologyAdapterBindingFactory()
						.getAccessibleSimplePathElements(parent);
				Collections.sort(returned, BindingPathElement.COMPARATOR);
				return returned;
			}
		}

		if (parent instanceof EditionSchemeParametersBindingVariable) {
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
		} else if (TypeUtils.isTypeAssignableFrom(EditionPattern.class, parent.getType())) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionPattern ep = (EditionPattern) parent.getType();
			for (PatternRole<?> pr : ep.getPatternRoles()) {
				returned.add(getSimplePathElement(pr, parent));
			}
			return returned;
		} else if (parent.getType() instanceof EditionPatternInstanceType) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionPattern ep = ((EditionPatternInstanceType) parent.getType()).getEditionPattern();
			for (PatternRole<?> pr : ep.getPatternRoles()) {
				returned.add(getSimplePathElement(pr, parent));
			}
			return returned;
		} else if (parent.getType() instanceof EditionSchemeAction) {
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			EditionScheme editionScheme = ((EditionSchemeAction) parent.getType()).getEditionScheme();
			returned.add(new EditionSchemeParametersPathElement(parent, editionScheme));
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
		return super.getAccessibleFunctionPathElements(parent);
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement parent, String propertyName) {
		// We want to avoid code duplication, so iterate on all accessible simple path element and choose the right one
		for (SimplePathElement e : getAccessibleSimplePathElements(parent)) {
			if (e.getLabel().equals(propertyName)) {
				return e;
			}
		}
		return super.makeSimplePathElement(parent, propertyName);
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement parent, String functionName, List<DataBinding<?>> args) {
		FunctionPathElement returned = super.makeFunctionPathElement(parent, functionName, args);
		// Hook to specialize type returned by getEditionPatternInstance(String)
		if (functionName.equals("getEditionPatternInstance")) {
			if (TypeUtils.isTypeAssignableFrom(ViewObject.class, parent.getType()) && args.size() == 1 && args.get(0).isStringConstant()) {
				String editionPatternId = ((StringConstant) args.get(0).getExpression()).getValue();
				EditionPattern ep = viewPoint.getEditionPattern(editionPatternId);
				returned.setType(EditionPatternInstanceType.getEditionPatternInstanceType(ep));
			}
		}
		return returned;
	}
}