package org.openflexo.foundation.viewpoint.inspector;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public final class EditionPatternInspectorBindingFactory extends DefaultBindingFactory 
{
	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	@Override
	public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName) 
	{
		if (father instanceof EditionPatternPathElement) {
			EditionPattern ep = ((EditionPatternPathElement) father).editionPattern;
			PatternRole pr = ep.getPatternRole(propertyName);
			if (pr != null) {
				return ((EditionPatternPathElement) father).getPatternRolePathElement(pr);
			}
			else {
				InspectorEntry.logger.warning("Not found pattern role: "+propertyName);
			}
		}
		else if (father instanceof PatternRolePathElement) {
			for (BindingPathElement prop : ((PatternRolePathElement)father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName)) return prop;
			}
			return null;
		}
		else if (father instanceof StatementPathElement) {
			for (BindingPathElement prop : ((StatementPathElement)father).getAllProperties()) {
				if (prop.getLabel().equals(propertyName)) return prop;
			}
			return null;
		}
		return super.getBindingPathElement(father, propertyName);
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father) 
	{
		if (father instanceof EditionPatternPathElement) {
			return ((EditionPatternPathElement) father).getAllPatternRoleElements();
		}
		else if (father instanceof PatternRolePathElement) {
			return ((PatternRolePathElement) father).getAllProperties();
		}
		else if (father instanceof StatementPathElement) {
			return ((StatementPathElement) father).getAllProperties();
		}
		return super.getAccessibleBindingPathElements(father);
	}
	
	@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father) 
	{
		if (father instanceof EditionPatternPathElement) {
			return EMPTY_LIST;
		}
		else if (father.getType() instanceof PatternRolePathElement) {
			return EMPTY_LIST;
		}
		else if (father.getType() instanceof StatementPathElement) {
			return EMPTY_LIST;
		}
		return super.getAccessibleCompoundBindingPathElements(father);
	}
	


}