package org.openflexo.fge.control.tools;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.model.factory.KeyValueCoding;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Convenient class used to manipulate mutable style
 * 
 * @author sylvain
 * 
 */
public interface StyleFactory<S extends KeyValueCoding, ST> extends HasPropertyChangeSupport {

	public InspectedStyle<? extends S> getCurrentStyle();

	public S makeNewStyle(S oldStyle);

	public ST getStyleType();

	public void setStyleType(ST styleType);

	public FGEModelFactory getFGEFactory();

	public void setFGEFactory(FGEModelFactory fgeFactory);

	public static final String STYLE_CLASS_CHANGED = "StyleClassChanged";
}