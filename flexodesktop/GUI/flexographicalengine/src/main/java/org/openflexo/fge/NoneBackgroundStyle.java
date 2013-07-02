package org.openflexo.fge;

import org.openflexo.fge.impl.NoneBackgroundStyleImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent an invisible background
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(NoneBackgroundStyleImpl.class)
@XMLElement(xmlTag = "NoneBackgroundStyle")
public interface NoneBackgroundStyle extends BackgroundStyle {

}
