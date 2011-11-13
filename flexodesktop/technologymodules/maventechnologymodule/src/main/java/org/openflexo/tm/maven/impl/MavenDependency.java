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
package org.openflexo.tm.maven.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Maven Dependency.
 * 
 * @author Emanuel Koch, Blue Pimento Services SPRL
 */
public class MavenDependency {

	private String groupId;
	private String artifactId;
	private String version;
	private String scope;
	private List<MavenDependency> exclusions = new ArrayList<MavenDependency>();

	@Override
	public String toString() {
		return "MavenDependency<" + groupId + "," + artifactId + "," + version + ">";
	}

	/**
	 * Creates a dependency with the specified name and version.<br/>
	 * groupId and artifactId will be the specified name.
	 * 
	 * @param name
	 *            groupId and artifactId name.
	 * @param version
	 *            the dependence version.
	 */
	public MavenDependency(String name, String version) {
		this(name, name, version);
	}

	/**
	 * Creates a dependency with the specified name and version.
	 * 
	 * @param name
	 *            groupId and artifactId name.
	 * @param version
	 *            the dependence version.
	 */
	public MavenDependency(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	/**
	 * Creates a dependency with the specified name, scope and version.<br/>
	 * 
	 * @param scope
	 *            the scope of this dependency (build, test, provided...)
	 * @param name
	 *            groupId and artifactId name.
	 * @param version
	 *            the dependence version.
	 */
	public MavenDependency(String groupId, String artifactId, String version, String scope) {
		this(groupId, artifactId, version);
		this.scope = scope;
	}

	// =================== //
	// = Getter / Setter = //
	// =================== //

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getScope() {
		return scope;
	}

	public boolean addExclusion(MavenDependency mavenDependency) {
		return exclusions.add(mavenDependency);
	}

	public List<MavenDependency> getExclusions() {
		return exclusions;
	}

}
