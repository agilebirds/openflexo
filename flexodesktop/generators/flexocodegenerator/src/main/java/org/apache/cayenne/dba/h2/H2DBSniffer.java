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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.DbAdapterFactory;
import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.dba.hsqldb.HSQLDBNoSchemaAdapter;

/**
 * Detects HSQLDB database from JDBC metadata.
 * 
 * @since 1.2
 * @author Andrus Adamchik
 */
public class H2DBSniffer implements DbAdapterFactory {

    @Override
	public DbAdapter createAdapter(DatabaseMetaData md) throws SQLException {
        String dbName = md.getDatabaseProductName();
        if (dbName == null || dbName.toUpperCase().indexOf("HSQL") < 0) {
            return null;
        }

        boolean supportsSchema = false;
        if (md.getDriverMajorVersion() < 1) {
            supportsSchema = true;
        }
        else if (md.getDriverMajorVersion() == 1) {
            if (md.getDriverMinorVersion() <= 8) {
                supportsSchema = true;
            }
            else {
                supportsSchema = false;
            }
        }
        else {
            supportsSchema = false;
        }
        
        return supportsSchema
            ? new HSQLDBAdapter()
            : new HSQLDBNoSchemaAdapter();
    }
}
