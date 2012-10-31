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
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Integer.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Long.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Short.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Float.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Double.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Character.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Byte.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Boolean.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Integer.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Long.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Short.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Float.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Double.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Character.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Byte.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Boolean.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Void.TYPE);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, String.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Vector.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Hashtable.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, File.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, URL.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Date.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Calendar.class);
		LoadableDMEntity.createLoadableDMEntity(newJDKRepository, Duration.class);
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
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No package for entity " + entity.getFullyQualifiedName());
				}
			}
		}
		return super.packageForEntity(entity);
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
