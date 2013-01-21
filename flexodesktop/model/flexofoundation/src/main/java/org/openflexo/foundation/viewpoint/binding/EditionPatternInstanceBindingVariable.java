package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPattern.EditionPatternInstanceType;

public class EditionPatternInstanceBindingVariable extends BindingVariable {
	static final Logger logger = Logger.getLogger(EditionPatternInstanceBindingVariable.class.getPackage().getName());

	private EditionPattern editionPattern;
	private int index;

	public EditionPatternInstanceBindingVariable(EditionPattern anEditionPattern, int index) {
		super(anEditionPattern.getViewPoint().getName() + "_" + anEditionPattern.getName() + "_" + index, anEditionPattern);
		this.editionPattern = anEditionPattern;
	}

	@Override
	public Type getType() {
		return EditionPatternInstanceType.getEditionPatternInstanceType(editionPattern);
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return editionPattern.getDescription();
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public int getIndex() {
		return index;
	}
}