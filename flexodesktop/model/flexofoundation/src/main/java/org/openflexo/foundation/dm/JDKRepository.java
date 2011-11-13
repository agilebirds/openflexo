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
package org.openflexo.foundation.dm;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.action.ImportJDKEntity;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.Duration;

/**
 * Represents a logical group of objects defined in common JDK implementation and used in the project
 * 
 * @author sguerin
 * 
 */
public class JDKRepository extends DMRepository {

	private static final Logger logger = Logger.getLogger(JDKRepository.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public JDKRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public JDKRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getInternalRepositoryFolder();
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public String getName() {
		return "jdk_repository";
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public void setName(String name) {
		// Not allowed
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static JDKRepository createNewJDKRepository(DMModel dmModel) {
		JDKRepository newJDKRepository = new JDKRepository(dmModel);
		dmModel.setJDKRepository(newJDKRepository);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Integer.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Long.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Short.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Float.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Double.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Character.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Byte.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Boolean.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Integer.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Long.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Short.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Float.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Double.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Character.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Byte.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Boolean.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Void.TYPE);
		LoadableDMEntity.createLoadableDMEntity(dmModel, String.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Vector.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Hashtable.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, File.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, URL.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Date.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Calendar.class);
		LoadableDMEntity.createLoadableDMEntity(dmModel, Duration.class);
		return newJDKRepository;
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".JDK";
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	@Override
	public boolean isUpdatable() {
		return true;
	}

	@Override
	public DMPackage packageForEntity(DMEntity entity) {
		String packageName = entity.getEntityPackageName();
		if (packageName == null) {
			if (entity instanceof JDKPrimitive) {
				DMPackage primitivePackage = packages.get("primitives");
				if (primitivePackage == null) {
					primitivePackage = new DMPackage(getDMModel(), this, "primitives");
					packages.put("primitives", primitivePackage);
				}
				return primitivePackage;
			} else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("No package for entity " + entity.getFullyQualifiedName());
			}
		}
		return super.packageForEntity(entity);
	}

	@Override
	protected Vector getSpecificActionListForThatClass() {
		Vector returned = super.getSpecificActionListForThatClass();
		returned.add(ImportJDKEntity.actionType);
		return returned;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

}
