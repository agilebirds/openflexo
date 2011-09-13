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

import org.apache.cayenne.access.trans.SelectTranslator;

/**
 * @since 1.2
 * @author Andrus Adamchik
 */
class H2SelectTranslator extends SelectTranslator {

    static final String SELECT_PREFIX = "SELECT";

    @Override
	public String createSqlString() throws Exception {
        String sql = super.createSqlString();

        // limit results
        int limit = getQuery().getMetaData(getEntityResolver()).getFetchLimit();
        if (limit > 0 && sql.startsWith(SELECT_PREFIX)) {
            return SELECT_PREFIX
                    + " TOP "
                    + limit
                    + sql.substring(SELECT_PREFIX.length());
        }

        return sql;
    }
}
