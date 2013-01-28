package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.view.View;

/**
 * Default implementation for {@link ViewResource}
 * 
 * 
 * @author Sylvain
 * 
 */
public abstract class ViewResourceImpl extends FlexoXMLFileResourceImpl<View> implements ViewResource {

	@Override
	public View getView() {
		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

}
