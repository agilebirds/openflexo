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

import org.apache.cayenne.access.trans.ProcedureTranslator;
import org.apache.cayenne.map.Procedure;

/**
 * Works around HSQLDB's pickiness about stored procedure syntax.
 * 
 * @since 1.2
 * @author Cristopher Daniluk
 */
public class H2DBProcedureTranslator extends ProcedureTranslator {

    /**
     * Creates HSQLDB-compliant SQL to execute a stored procedure.
     */
    @Override
	protected String createSqlString() {
        Procedure procedure = getProcedure();

        StringBuffer buf = new StringBuffer();

        int totalParams = callParams.size();

        // check if procedure returns values
        if (procedure.isReturningValue()) {
            totalParams--;

            // HSQL won't accept "? =". The parser only recognizes "?="

            // TODO: Andrus, 12/12/2005 - this is kind of how it is in the
            // CallableStatement javadocs, so we may need to make "?=" a default ... this
            // requires testing on Oracle/PostgreSQL/Sybase/SQLServer.
            buf.append("{?= call ");
        }
        else {
            buf.append("{call ");
        }

        // HSQLDB requires that procedures with periods (referring to Java packages)
        // be enclosed in quotes. It is not clear that quotes can always be used, though
        if (procedure.getFullyQualifiedName().indexOf('.') > -1) {
            buf.append("\"").append(procedure.getFullyQualifiedName()).append("\"");
        }
        else {
            buf.append(procedure.getFullyQualifiedName());
        }

        if (totalParams > 0) {
            // unroll the loop
            buf.append("(?");

            for (int i = 1; i < totalParams; i++) {
                buf.append(", ?");
            }

            buf.append(")");
        }

        buf.append("}");
        return buf.toString();
    }
}
