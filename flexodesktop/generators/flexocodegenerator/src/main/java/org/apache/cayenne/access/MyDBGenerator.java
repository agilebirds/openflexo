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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.conn.DataSourceInfo;
import org.apache.cayenne.conn.DriverDataSource;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.PkGenerator;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.DerivedDbEntity;
import org.apache.cayenne.validation.SimpleValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

/**
 * Utility class that generates database schema based on Cayenne mapping. It is a logical counterpart of DbLoader class.
 * 
 * @author Andrus Adamchik
 */
public class MyDBGenerator {

	private static final Logger logger = Logger.getLogger(MyDBGenerator.class.getPackage().getName());

	protected DbAdapter adapter;
	protected DataMap map;

	// optional DataDomain needed for correct FK generation in cross-db situations
	protected DataDomain domain;

	// stores generated SQL statements
	protected Map<String, String> dropTables;
	protected Map<String, String> createTables;
	protected Map<String, List<String>> createFK;
	protected List<String> createPK;
	protected List<String> dropPK;

	/**
	 * Contains all DbEntities ordered considering their interdependencies. DerivedDbEntities are filtered out of this list.
	 */
	protected List dbEntitiesInInsertOrder;
	protected List dbEntitiesRequiringAutoPK;

	protected boolean shouldDropTables;
	protected boolean shouldCreateTables;
	protected boolean shouldDropPKSupport;
	protected boolean shouldCreatePKSupport;
	protected boolean shouldCreateFKConstraints;

	protected ValidationResult failures;

	private Comparator dbEntityComparator;

	/**
	 * Creates and initializes new DbGenerator.
	 */
	public MyDBGenerator(DbAdapter adapt, DataMap _map) {
		this(adapt, _map, Collections.EMPTY_LIST);
	}

	/**
	 * Creates and initializes new DbGenerator instance.
	 * 
	 * @param adapter
	 *            DbAdapter corresponding to the database
	 * @param map
	 *            DataMap whose entities will be used in schema generation
	 * @param excludedEntities
	 *            entities that should be ignored during schema generation
	 */
	public MyDBGenerator(DbAdapter _adapter, DataMap _map, Collection excludedEntities) {
		this(_adapter, _map, excludedEntities, null, null);
	}

	/**
	 * Creates and initializes new DbGenerator instance.
	 * 
	 * @param adapter
	 *            DbAdapter corresponding to the database
	 * @param map
	 *            DataMap whose entities will be used in schema generation
	 * @param excludedEntities
	 *            entities that should be ignored during schema generation
	 * @param domain
	 *            optional DataDomain used to detect cross-database relationships.
	 * @since 1.2
	 */
	public MyDBGenerator(DbAdapter _adapter, DataMap _map, Collection excludedEntities, DataDomain _domain) {
		this(_adapter, _map, excludedEntities, _domain, null);
	}

	/**
	 * Creates and initializes new DbGenerator instance.
	 * 
	 * @param excludedEntities
	 *            entities that should be ignored during schema generation
	 * @param entitySorter
	 *            TODO
	 * @param adapter
	 *            DbAdapter corresponding to the database
	 * @param map
	 *            DataMap whose entities will be used in schema generation
	 * @param domain
	 *            optional DataDomain used to detect cross-database relationships.
	 * 
	 * @since 1.2
	 */
	public MyDBGenerator(DbAdapter _adapter, DataMap _map, Collection excludedEntities, DataDomain _domain, Comparator entitySorter) {
		// sanity check
		if (_adapter == null) {
			throw new IllegalArgumentException("Adapter must not be null.");
		}

		if (_map == null) {
			throw new IllegalArgumentException("DataMap must not be null.");
		}

		this.domain = _domain;
		this.map = _map;
		this.adapter = _adapter;
		this.dbEntityComparator = entitySorter;
		prepareDbEntities(excludedEntities);
		resetToDefaults();
		buildStatements();
	}

	protected void resetToDefaults() {
		this.shouldDropTables = false;
		this.shouldDropPKSupport = false;
		this.shouldCreatePKSupport = true;
		this.shouldCreateTables = true;
		this.shouldCreateFKConstraints = true;
	}

	/**
	 * Creates and stores internally a set of statements for database schema creation, ignoring configured schema creation preferences.
	 * Statements are NOT executed in this method.
	 */
	protected void buildStatements() {
		dropTables = new HashMap<String, String>();
		createTables = new HashMap<String, String>();
		createFK = new HashMap<String, List<String>>();

		DbAdapter _adapter = getAdapter();
		Iterator it = dbEntitiesInInsertOrder.iterator();
		boolean supportsFK = adapter.supportsFkConstraints();
		while (it.hasNext()) {
			DbEntity dbe = (DbEntity) it.next();

			String name = dbe.getName();

			// build "DROP TABLE"
			dropTables.put(name, _adapter.dropTable(dbe));

			// build "CREATE TABLE"
			createTables.put(name, _adapter.createTable(dbe));

			// build "FK"
			if (supportsFK) {
				createFK.put(name, createFkConstraintsQueries(dbe));
			}
		}

		PkGenerator pkGenerator = _adapter.getPkGenerator();
		dropPK = pkGenerator.dropAutoPkStatements(dbEntitiesRequiringAutoPK);
		createPK = pkGenerator.createAutoPkStatements(dbEntitiesRequiringAutoPK);
	}

	/**
	 * Returns <code>true</code> if there is nothing to be done by this generator. If <code>respectConfiguredSettings</code> is
	 * <code>true</code>, checks are done applying currently configured settings, otherwise check is done, assuming that all possible
	 * generated objects.
	 */
	public boolean isEmpty(boolean respectConfiguredSettings) {
		if (dbEntitiesInInsertOrder.isEmpty() && dbEntitiesRequiringAutoPK.isEmpty()) {
			return true;
		}

		if (!respectConfiguredSettings) {
			return false;
		}

		return !(shouldDropTables || shouldCreateTables || shouldCreateFKConstraints || shouldCreatePKSupport || shouldDropPKSupport);
	}

	/** Returns DbAdapter associated with this DbGenerator. */
	public DbAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Returns a list of all schema statements that should be executed with the current configuration.
	 */
	public List<String> configuredStatements() {
		List<String> list = new ArrayList<String>();

		if (shouldDropTables) {
			Iterator it = dbEntitiesInInsertOrder.iterator();
			while (it.hasNext()) {
				DbEntity ent = (DbEntity) it.next();
				list.add(dropTables.get(ent.getName()));
			}
		}

		if (shouldCreateTables) {
			ListIterator it = dbEntitiesInInsertOrder.listIterator(dbEntitiesInInsertOrder.size());
			while (it.hasPrevious()) {
				DbEntity ent = (DbEntity) it.previous();
				list.add(createTables.get(ent.getName()));
			}
		}

		if (shouldCreateFKConstraints && getAdapter().supportsFkConstraints()) {
			ListIterator it = dbEntitiesInInsertOrder.listIterator(dbEntitiesInInsertOrder.size());
			while (it.hasPrevious()) {
				DbEntity ent = (DbEntity) it.previous();
				List<String> fks = createFK.get(ent.getName());
				list.addAll(fks);
			}
		}

		if (shouldDropPKSupport) {
			list.addAll(dropPK);
		}

		if (shouldCreatePKSupport) {
			list.addAll(createPK);
		}

		return list;
	}

	/**
	 * Creates a temporary DataSource out of DataSourceInfo and invokes <code>public void runGenerator(DataSource ds)</code>.
	 */
	public void runGenerator(DataSourceInfo dsi) throws Exception {
		this.failures = null;

		// do a pre-check. Maybe there is no need to run anything
		// and therefore no need to create a connection
		if (isEmpty(true)) {
			return;
		}

		Driver driver = (Driver) Class.forName(dsi.getJdbcDriver()).newInstance();
		DataSource dataSource = new DriverDataSource(driver, dsi.getDataSourceUrl(), dsi.getUserName(), dsi.getPassword());

		runGenerator(dataSource);
	}

	/**
	 * Executes a set of commands to drop/create database objects. This is the main worker method of DbGenerator. Command set is built based
	 * on pre-configured generator settings.
	 */
	public void runGenerator(DataSource ds) throws Exception {
		this.failures = null;

		Connection connection = ds.getConnection();

		try {

			// drop tables
			if (shouldDropTables) {
				ListIterator it = dbEntitiesInInsertOrder.listIterator(dbEntitiesInInsertOrder.size());
				while (it.hasPrevious()) {
					DbEntity ent = (DbEntity) it.previous();
					safeExecute(connection, dropTables.get(ent.getName()));
				}
			}

			// create tables
			List createdTables = new ArrayList();
			if (shouldCreateTables) {
				Iterator it = dbEntitiesInInsertOrder.iterator();
				while (it.hasNext()) {
					DbEntity ent = (DbEntity) it.next();

					// only create missing tables

					safeExecute(connection, createTables.get(ent.getName()));
					createdTables.add(ent.getName());
				}
			}

			// create FK
			if (shouldCreateTables && shouldCreateFKConstraints && getAdapter().supportsFkConstraints()) {
				Iterator it = dbEntitiesInInsertOrder.iterator();
				while (it.hasNext()) {
					DbEntity ent = (DbEntity) it.next();

					if (createdTables.contains(ent.getName())) {
						List fks = createFK.get(ent.getName());
						Iterator fkIt = fks.iterator();
						while (fkIt.hasNext()) {
							safeExecute(connection, (String) fkIt.next());
						}
					}
				}
			}

			// drop PK
			if (shouldDropPKSupport) {
				List dropAutoPKSQL = getAdapter().getPkGenerator().dropAutoPkStatements(dbEntitiesRequiringAutoPK);
				Iterator it = dropAutoPKSQL.iterator();
				while (it.hasNext()) {
					safeExecute(connection, (String) it.next());
				}
			}

			// create pk
			if (shouldCreatePKSupport) {
				List createAutoPKSQL = getAdapter().getPkGenerator().createAutoPkStatements(dbEntitiesRequiringAutoPK);
				Iterator it = createAutoPKSQL.iterator();
				while (it.hasNext()) {
					safeExecute(connection, (String) it.next());
				}
			}
		} finally {
			connection.close();
		}
	}

	/**
	 * Builds and executes a SQL statement, catching and storing SQL exceptions resulting from invalid SQL. Only non-recoverable exceptions
	 * are rethrown.
	 * 
	 * @since 1.1
	 */
	protected boolean safeExecute(Connection connection, String sql) throws SQLException {
		Statement statement = connection.createStatement();

		try {
			QueryLogger.logQuery(sql, null);
			statement.execute(sql);
			return true;
		} catch (SQLException ex) {
			if (this.failures == null) {
				this.failures = new ValidationResult();
			}

			failures.addFailure(new SimpleValidationFailure(sql, ex.getMessage()));
			QueryLogger.logQueryError(ex);
			return false;
		} finally {
			statement.close();
		}
	}

	/**
	 * Returns an array of queries to create foreign key constraints for a particular DbEntity. Throws CayenneRuntimeException, if called
	 * for adapter that does not support FK constraints.
	 */
	public List<String> createFkConstraintsQueries(DbEntity dbEnt) {
		if (!getAdapter().supportsFkConstraints()) {
			throw new CayenneRuntimeException("FK constraints are not supported by adapter.");
		}

		List<String> list = new ArrayList<String>();
		Iterator it = dbEnt.getRelationships().iterator();
		while (it.hasNext()) {
			DbRelationship rel = (DbRelationship) it.next();

			if (rel.isToMany()) {
				continue;
			}

			// skip FK to a different DB
			if (domain != null) {
				DataMap srcMap = rel.getSourceEntity().getDataMap();
				DataMap targetMap = rel.getTargetEntity().getDataMap();

				if (srcMap != null && targetMap != null && srcMap != targetMap) {
					if (domain.lookupDataNode(srcMap) != domain.lookupDataNode(targetMap)) {
						continue;
					}
				}
			}

			// create an FK CONSTRAINT only if the relationship is to PK
			// and if this is not a dependent PK

			// create UNIQUE CONSTRAINT on FK if reverse relationship is to-one

			if (rel.isToPK() && !rel.isToDependentPK()) {

				if (getAdapter().supportsUniqueConstraints()) {

					DbRelationship reverse = rel.getReverseRelationship();
					if (reverse != null && !reverse.isToMany() && !reverse.isToPK()) {
						list.add(getAdapter().createUniqueConstraint((DbEntity) rel.getSourceEntity(), rel.getSourceAttributes()));
					}
				}

				list.add(getAdapter().createFkConstraint(rel));
			}
		}
		return list;
	}

	/**
	 * Returns an object representing a collection of failures that occurred on the last "runGenerator" invocation, or null if there were no
	 * failures. Failures usually indicate problems with generated DDL (such as "create...", "drop...", etc.) and usually happen due to the
	 * DataMap being out of sync with the database.
	 * 
	 * @since 1.1
	 */
	public ValidationResult getFailures() {
		return failures;
	}

	/**
	 * Returns whether DbGenerator is configured to create primary key support for DataMap entities.
	 */
	public boolean shouldCreatePKSupport() {
		return shouldCreatePKSupport;
	}

	/**
	 * Returns whether DbGenerator is configured to create tables for DataMap entities.
	 */
	public boolean shouldCreateTables() {
		return shouldCreateTables;
	}

	public boolean shouldDropPKSupport() {
		return shouldDropPKSupport;
	}

	public boolean shouldDropTables() {
		return shouldDropTables;
	}

	public boolean shouldCreateFKConstraints() {
		return shouldCreateFKConstraints;
	}

	public void setShouldCreatePKSupport(boolean shouldCreatePKSupport) {
		this.shouldCreatePKSupport = shouldCreatePKSupport;
	}

	public void setShouldCreateTables(boolean shouldCreateTables) {
		this.shouldCreateTables = shouldCreateTables;
	}

	public void setShouldDropPKSupport(boolean shouldDropPKSupport) {
		this.shouldDropPKSupport = shouldDropPKSupport;
	}

	public void setShouldDropTables(boolean shouldDropTables) {
		this.shouldDropTables = shouldDropTables;
	}

	public void setShouldCreateFKConstraints(boolean shouldCreateFKConstraints) {
		this.shouldCreateFKConstraints = shouldCreateFKConstraints;
	}

	/**
	 * Returns a DataDomain used by the DbGenerator to detect cross-database relationships. By default DataDomain is null.
	 * 
	 * @since 1.2
	 */
	public DataDomain getDomain() {
		return domain;
	}

	/**
	 * Helper method that orders DbEntities to satisfy referential constraints and returns an ordered list. It also filters out
	 * DerivedDbEntities.
	 */
	private void prepareDbEntities(Collection excludedEntities) {
		if (excludedEntities == null) {
			excludedEntities = Collections.EMPTY_LIST;
		}

		// remove derived db entities
		Vector<DbEntity> tables = new Vector<DbEntity>();
		List<DbEntity> tablesWithAutoPk = new ArrayList<DbEntity>();
		Iterator it = map.getDbEntities().iterator();
		while (it.hasNext()) {
			DbEntity nextEntity = (DbEntity) it.next();

			// do sanity checks...

			// derived DbEntities are not included in generated SQL
			if (nextEntity instanceof DerivedDbEntity) {
				continue;
			}

			// tables with no columns are not included
			if (nextEntity.getAttributes().size() == 0) {
				logger.info("Skipping entity with no attributes: " + nextEntity.getName());
				continue;
			}

			// check if this entity is explicitly excluded
			if (excludedEntities.contains(nextEntity)) {
				continue;
			}

			// tables with invalid DbAttributes are not included
			boolean invalidAttributes = false;
			Iterator nextDbAtributes = nextEntity.getAttributes().iterator();
			while (nextDbAtributes.hasNext()) {
				DbAttribute attr = (DbAttribute) nextDbAtributes.next();
				if (attr.getType() == TypesMapping.NOT_DEFINED) {
					logger.info("Skipping entity, attribute type is undefined: " + nextEntity.getName() + "." + attr.getName());
					invalidAttributes = true;
					break;
				}
			}
			if (invalidAttributes) {
				continue;
			}

			tables.add(nextEntity);

			// check if an automatic PK generation can be potentailly supported
			// in this entity. For now simply check that the key is not propagated
			Iterator relationships = nextEntity.getRelationships().iterator();

			// create a copy of the original PK list,
			// since the list will be modified locally
			List pkAttributes = new ArrayList(nextEntity.getPrimaryKey());
			while (pkAttributes.size() > 0 && relationships.hasNext()) {
				DbRelationship nextRelationship = (DbRelationship) relationships.next();
				if (!nextRelationship.isToMasterPK()) {
					continue;
				}

				// supposedly all source attributes of the relationship
				// to master entity must be a part of primary key,
				// so
				Iterator joins = nextRelationship.getJoins().iterator();
				while (joins.hasNext()) {
					DbJoin join = (DbJoin) joins.next();
					pkAttributes.remove(join.getSource());
				}
			}

			// primary key is needed only if at least one of the primary key attributes
			// is not propagated via relationship

			if (pkAttributes.size() == 1) {
				// GPO add: check that the PK is an integer
				boolean needAutoPK = false;
				Iterator i = pkAttributes.iterator();
				while (i.hasNext()) {
					DbAttribute att = (DbAttribute) i.next();
					switch (att.getType()) {
					case Types.INTEGER:
					case Types.NUMERIC:
					case Types.BIGINT:
					case Types.TINYINT:
					case Types.SMALLINT:
						needAutoPK = true;
						break;
					default:
						break;
					}
				}
				if (needAutoPK) {
					tablesWithAutoPk.add(nextEntity);
				}
			}
		}

		// sort table list
		if (tables.size() > 1) {
			Collections.sort(tables, new Comparator() {

				@Override
				public int compare(Object o1, Object o2) {
					if (o1 == null) {
						return 1;
					} else if (o2 == null) {
						return -1;
					} else if (!(o1 instanceof DbEntity)) {
						return 1;
					} else if (!(o2 instanceof DbEntity)) {
						return -1;
					} else {
						return ((DbEntity) o1).getName().compareTo(((DbEntity) o2).getName());
					}
				}

			});
			if (this.dbEntityComparator != null) {
				Vector<Object> v = new Vector<Object>();
				v.addAll(tables);
				tables.clear();
				Iterator i = v.iterator();
				while (i.hasNext()) {
					DbEntity e = (DbEntity) i.next();
					if (tables.size() == 0) {
						tables.add(e);
					} else {
						Iterator i1 = tables.iterator();
						int j = 0;
						boolean inserted = false;
						while (!inserted && i1.hasNext()) {
							DbEntity e1 = (DbEntity) i1.next();
							if (dbEntityComparator.compare(e, e1) < 0) {
								tables.insertElementAt(e, j);
								inserted = true;
							}
							j++;
						}
						if (!inserted) {
							tables.add(e);
						}
					}
				}
			} else {
				DataNode node = new DataNode("temp");
				node.addDataMap(map);
				node.getEntitySorter().sortDbEntities(tables, false);
			}
		}

		this.dbEntitiesInInsertOrder = tables;
		this.dbEntitiesRequiringAutoPK = tablesWithAutoPk;
	}

}
