/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.dg.test;

import java.util.logging.Logger;

import junit.framework.AssertionFailedError;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;

public abstract class DGTestCase extends FlexoTestCase {

	public DGTestCase(String name) {
		super(name);
	}

	protected static final Logger logger = Logger.getLogger(DGTestCase.class.getPackage().getName());

	@Override
	protected void assertSynchonized(FlexoResource resource1, FlexoResource resource2) {
		try {
			assertTrue(resource1.getSynchronizedResources().contains(resource2));
			assertTrue(resource2.getSynchronizedResources().contains(resource1));
		} catch (AssertionFailedError e) {
			logger.severe("RESOURCE synchonization problem: " + resource1 + " MUST be synchronized with " + resource2);
			throw e;
		}
	}

	/**
	 * Assert resource1 depends of resource2
	 * 
	 * @param resource1
	 * @param resource2
	 */
	@Override
	protected void assertDepends(FlexoResource resource1, FlexoResource resource2) {
		try {
			assertTrue(resource1.getDependantResources().contains(resource2));
			assertTrue(resource2.getAlteredResources().contains(resource1));
		} catch (AssertionFailedError e) {
			logger.severe("RESOURCE synchonization problem: " + resource1 + " MUST depends on " + resource2);
			throw e;
		}
	}

	/**
	 * Assert resource1 depends of resource2
	 * 
	 * @param resource1
	 * @param resource2
	 */
	@Override
	protected void assertNotDepends(FlexoResource resource1, FlexoResource resource2) {
		try {
			assertFalse(resource1.getDependantResources().contains(resource2));
			assertFalse(resource2.getAlteredResources().contains(resource1));
		} catch (AssertionFailedError e) {
			logger.severe("RESOURCE synchonization problem: " + resource1 + " SHOULD NOT depends on " + resource2);
			throw e;
		}
	}

	@Override
	protected void assertNotModified(FlexoStorageResource resource) {
		try {
			assertFalse(resource.isModified());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be NOT modified (" + resource.getFileName() + ")");
			throw e;
		}
	}

	@Override
	protected void assertModified(FlexoStorageResource resource) {
		try {
			assertTrue(resource.isModified());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be modified");
			throw e;
		}
	}

	@Override
	protected void assertNotLoaded(FlexoStorageResource resource) {
		try {
			assertFalse(resource.isLoaded());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be NOT loaded");
			throw e;
		}
	}

	@Override
	protected void assertLoaded(FlexoStorageResource resource) {
		try {
			assertTrue(resource.isLoaded());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be loaded");
			throw e;
		}
	}

}
