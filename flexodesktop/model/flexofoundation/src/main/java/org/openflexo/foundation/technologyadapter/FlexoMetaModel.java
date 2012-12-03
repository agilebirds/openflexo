package org.openflexo.foundation.technologyadapter;

/**
 * Flexo MetaModel
 * 
 * @author sguerin
 */
public interface FlexoMetaModel {

	/**
	 * MetaModel URI.
	 * 
	 * @return
	 */
	public String getURI();

	/**
	 * Is Read Only.
	 * 
	 * @return
	 */
	public boolean isReadOnly();

	/**
	 * Set Is Read Only.
	 * 
	 * @param b
	 */
	public void setIsReadOnly(boolean b);

}
