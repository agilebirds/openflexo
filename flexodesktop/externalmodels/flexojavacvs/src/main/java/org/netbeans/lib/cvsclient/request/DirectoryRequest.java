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
package org.netbeans.lib.cvsclient.request;

/**
 * The directory request. Tell the server which directory to use.
 * 
 * @author Robert Greig
 */
public final class DirectoryRequest extends Request {

	/**
	 * The local directory. Used as the first argument
	 */
	private final String localDirectory;

	/**
	 * The repository. The second argument
	 */
	private final String repository;

	/**
	 * Create a new DirectoryRequest
	 * 
	 * @param theLocalDirectory
	 *            the local directory argument
	 * @param theRepository
	 *            the repository argument
	 */
	public DirectoryRequest(String localDirectory, String repository) {
		if (localDirectory == null || repository == null) {
			throw new IllegalArgumentException("Both, directory and repository, must not be null!");
		}

		this.localDirectory = localDirectory;
		this.repository = repository;
	}

	/**
	 * Returns the value of used local directory.
	 */
	public String getLocalDirectory() {
		return localDirectory;
	}

	/**
	 * Returns the value of used repository directory.
	 */
	public String getRepository() {
		return repository;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 */
	@Override
	public String getRequestString() {
		return "Directory " + localDirectory + "\n" + repository + "\n"; // NOI18N
	}

	/**
	 * Is a response expected from the server?
	 * 
	 * @return true if a response is expected, false if no response if expected
	 */
	@Override
	public boolean isResponseExpected() {
		return false;
	}
}
