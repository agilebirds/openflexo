package org.openflexo.foundation.viewpoint.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public final class EditionPatternBindingFactory extends DefaultBindingFactory {
	static final Logger logger = Logger.getLogger(EditionPatternBindingFactory.class.getPackage().getName());

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	@Override
	public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName) {
		if (father instanceof EditionPatternInstancePathElement) {
			EditionPattern ep = ((EditionPatternInstancePathElement) father).getEditionPattern();
			PatternRole pr = ep.getPatternRole(propertyName);
			if (pr != null) {
				return ((EditionPatternInstancePathElement) father).getPatternRolePathElement(pr);
			} else {
				logger.warning("Not found pattern role: " + propertyName);
			}
		} else if (father instanceof EditionPatternPathElement) {
			EditionPattern ep = ((EditionPatternPathElement) father).getEditionPattern();
			PatternRole pr = ep.getPatternRole(propertyName);
			if (pr != null) {
				return ((EditionPatternPathElement) father).getPatternRolePathElement(pr);
			} else {
				logger.warning("Not found pattern role: " + propertyName);
			}
		} else if (father instanceof PatternRolePathElement) {
			for (BindingPathElement prop : ((PatternRolePathElement<?>) father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName))
					return prop;
			}
			return null;
		} else if (father instanceof StatementPathElement) {
			for (BindingPathElement prop : ((StatementPathElement<?>) father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName))
					return prop;
			}
			return null;
		} else if (father instanceof EditionSchemeParameterListPathElement) {
			for (BindingPathElement prop : ((EditionSchemeParameterListPathElement) father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName))
					return prop;
			}
			return null;
		} else if (father instanceof GraphicalElementPathElement) {
			for (BindingPathElement prop : ((GraphicalElementPathElement<?>) father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName))
					return prop;
			}
			return null;
		} else if (father instanceof OntologyObjectPathElement) {
			for (BindingPathElement prop : ((OntologyObjectPathElement<?>) father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName))
					return prop;
			}
			return null;
		}
		return super.getBindingPathElement(father, propertyName);
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father) {
		if (father instanceof EditionPatternInstancePathElement) {
			return ((EditionPatternInstancePathElement) father).getAllPatternRoleElements();
		} else if (father instanceof EditionPatternPathElement) {
			return ((EditionPatternPathElement) father).getAllPatternRoleElements();
		} else if (father instanceof PatternRolePathElement) {
			return ((PatternRolePathElement) father).getAllProperties();
		} else if (father instanceof StatementPathElement) {
			return ((StatementPathElement) father).getAllProperties();
		} else if (father instanceof EditionSchemeParameterListPathElement) {
			return ((EditionSchemeParameterListPathElement) father).getAllProperties();
		} else if (father instanceof GraphicalElementPathElement) {
			return ((GraphicalElementPathElement) father).getAllProperties();
		} else if (father instanceof OntologyObjectPathElement) {
			return ((OntologyObjectPathElement) father).getAllProperties();
		}
		return super.getAccessibleBindingPathElements(father);
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father) {
		if (father instanceof EditionPatternInstancePathElement) {
			return EMPTY_LIST;
		} else if (father instanceof EditionPatternPathElement) {
			return EMPTY_LIST;
		} else if (father.getType() instanceof PatternRolePathElement) {
			return EMPTY_LIST;
		} else if (father.getType() instanceof StatementPathElement) {
			return EMPTY_LIST;
		} else if (father.getType() instanceof EditionSchemeParameterListPathElement) {
			return EMPTY_LIST;
		} else if (father.getType() instanceof GraphicalElementPathElement) {
			return EMPTY_LIST;
		} else if (father instanceof OntologyObjectPathElement) {
			return EMPTY_LIST;
		}
		return super.getAccessibleCompoundBindingPathElements(father);
	}

}