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
package org.apache.cayenne.access;

import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.wocompat.EOObjEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class DBEntityComparator implements Comparator<DbEntity> {
	private static final Logger logger = FlexoLogger.getLogger(DBEntityComparator.class.getPackage().getName());

	private FlexoProject project;

	protected DBEntityComparator(FlexoProject p) {
		this.project = p;
	}

	/**
	 * Overrides compare
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(DbEntity o1, DbEntity o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return 1;
		} else if (o2 == null) {
			return -1;
		}
		DMEntity e1 = null;
		DMEntity e2 = null;
		Iterator i = ((DataMap) o1.getParent()).getObjEntities().iterator();
		while (i.hasNext() && (e1 == null || e2 == null)) {
			EOObjEntity e = (EOObjEntity) i.next();
			if (e.getDbEntityName().equals(o1.getName()) && e.getClassName() != null) {
				e1 = project.getDataModel().getDMEntity(null, e.getClassName());
			} else if (e.getDbEntityName().equals(o2.getName()) && e.getClassName() != null) {
				e2 = project.getDataModel().getDMEntity(null, e.getClassName());
			}
		}
		if (e1 == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not resolve table named: " + o1.getName());
			}
			return 1;
		} else if (e2 == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not resolve table named: " + o1.getName());
			}
			return -1;
		} else if (!(e1 instanceof DMEOEntity)) {
			return 1;
		} else if (!(e2 instanceof DMEOEntity)) {
			return -1;
		} else {
			return ((DMEOEntity) e1).compareTo((DMEOEntity) e2);
		}
	}

}
