package org.openflexo.fge;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent an invisible background
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "NoneBackgroundStyle")
public interface NoneBackgroundStyle extends BackgroundStyle {

}
