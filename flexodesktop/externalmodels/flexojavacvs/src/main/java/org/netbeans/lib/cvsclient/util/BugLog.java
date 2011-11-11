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
package org.netbeans.lib.cvsclient.util;

/**
 * Assertion tool class.
 * 
 * @author Thomas Singer
 */
public class BugLog {

	private static BugLog instance;

	public synchronized static BugLog getInstance() {
		if (instance == null) {
			instance = new BugLog();
		}
		return instance;
	}

	public synchronized static void setInstance(BugLog instance) {
		BugLog.instance = instance;
	}

	public BugLog() {
	}

	public void showException(Exception ex) {
		ex.printStackTrace();
	}

	public void assertTrue(boolean value, String message) {
		if (value) {
			return;
		}

		throw new BugException(message);
	}

	public void assertNotNull(Object obj) {
		if (obj != null) {
			return;
		}

		throw new BugException("Value must not be null!"); // NOI18N
	}

	public void bug(String message) {
		new Exception(message).printStackTrace();
	}

	public static class BugException extends RuntimeException {
		public BugException(String message) {
			super(message);
		}
	}
}
