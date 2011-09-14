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

package org.apache.cayenne.dba.h2;

import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.map.DbEntity;


/**
 * A flavor of HSQLDBAdapter that implements workarounds for some old driver limitations.
 * 
 * @since 1.2
 * @author Mike Kienenberger
 */
public class H2DBNoSchemaAdapter extends HSQLDBAdapter {
    /**
     * Generate unqualified name without schema.
     * 
     * @since 1.2
     */
    @Override
	protected String getTableName(DbEntity entity)
    {
        return  entity.getName();
    }

    /**
     * Generate unqualified name.
     * 
     * @since 1.2
     */
    @Override
	protected String getSchemaName(DbEntity entity) {
        return "";
    }

    /**
     * Returns a SQL string to drop a table corresponding to <code>ent</code> DbEntity.
     * 
     * @since 1.2
     */
    @Override
	public String dropTable(DbEntity ent) {
        // hsqldb doesn't support schema namespaces, so remove if found
        return "DROP TABLE " + getTableName(ent);
    }

    /**
     * Uses unqualified entity names.
     * 
     * @since 1.2
     */
    @Override
	public String createTable(DbEntity ent) {
        String sql = super.createTable(ent);

        // hsqldb doesn't support schema namespaces, so remove if found
        String fqnCreate = "CREATE CACHED TABLE " + super.getTableName(ent) + " (";
        if (sql != null && sql.toUpperCase().startsWith(fqnCreate)) {
            sql = "CREATE CACHED TABLE "
                    + getTableName(ent)
                    + " ("
                    + sql.substring(fqnCreate.length());
        }

        return sql;
    }
}
