package org.openflexo.foundation.viewpoint.binding;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;

public class EditionSchemeParametersBindingVariable extends BindingVariable {
	static final Logger logger = Logger.getLogger(EditionSchemeParametersBindingVariable.class.getPackage().getName());

	private EditionScheme editionScheme;

	public EditionSchemeParametersBindingVariable(EditionScheme anEditionScheme) {
		super("parameters", new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(EditionSchemeParameter.class)));
		this.editionScheme = anEditionScheme;
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}
}