package org.openflexo.fge.control.tools;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.model.factory.KeyValueCoding;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Convenient class used to manipulate BackgroundStyle
 * 
 * @author sylvain
 * 
 */
public interface StyleFactory<S extends KeyValueCoding> extends HasPropertyChangeSupport {

	public InspectedStyle<? extends S> getCurrentStyle();

	public S makeNewStyle();

	public FGEModelFactory getFGEFactory();

	public void setFGEFactory(FGEModelFactory fgeFactory);

	public static final String STYLE_CLASS_CHANGED = "StyleClassChanged";
}