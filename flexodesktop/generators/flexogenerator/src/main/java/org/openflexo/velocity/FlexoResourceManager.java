package org.openflexo.velocity;

import org.apache.velocity.runtime.resource.ContentResource;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;

public class FlexoResourceManager extends ResourceManagerImpl {

	@Override
	protected Resource createResource(String resourceName, int resourceType) {
		Resource resource = null;
		switch (resourceType) {
		case ResourceManager.RESOURCE_TEMPLATE:
			resource = new FlexoTemplate();
			break;
		case ResourceManager.RESOURCE_CONTENT:
			resource = new ContentResource();
			break;
		}
		return resource;
	}

}
