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
package cb.petal;

import java.util.Collection;

/**
 * Represents top level design object.
 * 
 * @version $Id: Design.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Design extends PetalObject implements Named {
	public Design(Collection params) {
		super(null, "Design", params);
	}

	public Design() {
		super("Design");
	}

	@Override
	public void setNameParameter(String o) {
		params.set(0, o);
	}

	@Override
	public String getNameParameter() {
		return (String) params.get(0);
	}

	public boolean getIsUnit() {
		return getPropertyAsBoolean("is_unit");
	}

	public void setIsUnit(boolean o) {
		defineProperty("is_unit", o);
	}

	public boolean getIsLoaded() {
		return getPropertyAsBoolean("is_loaded");
	}

	public void setIsLoaded(boolean o) {
		defineProperty("is_loaded", o);
	}

	public Defaults getDefaults() {
		return (Defaults) getProperty("defaults");
	}

	public void setDefaults(Defaults o) {
		defineProperty("defaults", o);
	}

	/**
	 * @return Use case view
	 */
	public ClassCategory getUsecaseView() {
		return getRootUsecasePackage();
	}

	public void setUsecaseView(ClassCategory o) {
		setRootUsecasePackage(o);
	}

	/**
	 * @return Use case view
	 */
	public ClassCategory getRootUsecasePackage() {
		return (ClassCategory) getProperty("root_usecase_package");
	}

	public void setRootUsecasePackage(ClassCategory o) {
		defineProperty("root_usecase_package", o);
	}

	public ClassCategory getLogicalView() {
		return getRootCategory();
	}

	public void setLogicalView(ClassCategory o) {
		setRootCategory(o);
	}

	/**
	 * @return Logical view
	 */
	public ClassCategory getRootCategory() {
		return (ClassCategory) getProperty("root_category");
	}

	public void setRootCategory(ClassCategory o) {
		defineProperty("root_category", o);
	}

	/**
	 * @return Component view
	 */
	public SubSystem getRootSubsystem() {
		return (SubSystem) getProperty("root_subsystem");
	}

	public void setRootSubsystem(SubSystem o) {
		defineProperty("root_subsystem", o);
	}

	public Processes getProcessStructure() {
		return (Processes) getProperty("process_structure");
	}

	public void setProcessStructure(Processes o) {
		defineProperty("process_structure", o);
	}

	public Properties getProperties() {
		return (Properties) getProperty("properties");
	}

	public void setProperties(Properties o) {
		defineProperty("properties", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
