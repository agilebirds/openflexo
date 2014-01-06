package org.openflexo.foundation.converter;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.utils.FlexoObjectReference;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

/**
 * String converter from/to {@link FlexoObjectReference} objects
 * 
 * @author sylvain
 * 
 */
public class FlexoObjectReferenceConverter extends Converter<FlexoObjectReference<?>> {

	/**
	 * 
	 */
	private final FlexoProject flexoProject;

	public FlexoObjectReferenceConverter(FlexoProject flexoProject) {
		super(FlexoObjectReference.class);
		this.flexoProject = flexoProject;
	}

	@Override
	public FlexoObjectReference<?> convertFromString(String value, ModelFactory factory) {
		return new FlexoObjectReference<FlexoObject>(value, this.flexoProject);
	}

	@Override
	public String convertToString(FlexoObjectReference value) {
		return value.getStringRepresentation();
	}

}