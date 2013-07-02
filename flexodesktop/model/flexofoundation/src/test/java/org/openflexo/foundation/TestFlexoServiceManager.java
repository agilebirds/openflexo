package org.openflexo.foundation;

import java.io.File;

import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Test purposes: implements an FlexoServiceManager with a unique ResourceCenter
 * 
 * @author sylvain
 * 
 */
public class TestFlexoServiceManager extends DefaultFlexoServiceManager {

	private File resourceCenterDirectory;

	public static class FlexoTestEditor extends DefaultFlexoEditor {
		public FlexoTestEditor(FlexoProject project) {
			super(project);
		}

	}

	public TestFlexoServiceManager(File resourceCenterDirectory) {
		super();
		this.resourceCenterDirectory = resourceCenterDirectory;
		getResourceCenterService().addToResourceCenters(new DirectoryResourceCenter(resourceCenterDirectory));
	}

	@Override
	protected FlexoEditor createApplicationEditor() {
		return new FlexoTestEditor(null);
	}

}
