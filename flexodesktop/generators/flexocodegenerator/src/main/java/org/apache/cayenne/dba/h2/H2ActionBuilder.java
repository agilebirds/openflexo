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

import java.sql.Connection;

import org.apache.cayenne.access.jdbc.ProcedureAction;
import org.apache.cayenne.access.jdbc.SelectAction;
import org.apache.cayenne.access.trans.ProcedureTranslator;
import org.apache.cayenne.access.trans.SelectTranslator;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.JdbcActionBuilder;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.query.ProcedureQuery;
import org.apache.cayenne.query.SQLAction;
import org.apache.cayenne.query.SelectQuery;

class H2ActionBuilder extends JdbcActionBuilder {

    H2ActionBuilder(DbAdapter adapter, EntityResolver resolver) {
        super(adapter, resolver);
    }

    @Override
	public SQLAction objectSelectAction(SelectQuery query) {
        return new SelectAction(query, adapter, entityResolver) {

            @Override
			protected SelectTranslator createTranslator(Connection connection) {
                SelectTranslator translator = new H2SelectTranslator();
                translator.setQuery(query);
                translator.setAdapter(adapter);
                translator.setEntityResolver(getEntityResolver());
                translator.setConnection(connection);
                return translator;
            }
        };
    }
    
    @Override
	public SQLAction procedureAction(ProcedureQuery query) {
        return new ProcedureAction(query, adapter, entityResolver) {

            @Override
			protected ProcedureTranslator createTranslator(Connection connection) {
                ProcedureTranslator transl = new H2DBProcedureTranslator();
                transl.setAdapter(getAdapter());
                transl.setQuery(query);
                transl.setEntityResolver(getEntityResolver());
                transl.setConnection(connection);
                return transl;
            }
        };
    }

}
