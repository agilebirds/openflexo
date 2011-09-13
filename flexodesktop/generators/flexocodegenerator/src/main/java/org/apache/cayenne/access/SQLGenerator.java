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

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.dba.derby.DerbyAdapter;
import org.apache.cayenne.dba.frontbase.FrontBaseAdapter;
import org.apache.cayenne.dba.h2.H2DBAdapter;
import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.dba.mysql.MySQLAdapter;
import org.apache.cayenne.dba.oracle.OracleAdapter;
import org.apache.cayenne.dba.postgres.PostgresAdapter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.wocompat.EOModelProcessor;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.utils.MetaFileGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileFormat;


public class SQLGenerator extends MetaFileGenerator{

	protected static final Logger logger = Logger.getLogger(SQLGenerator.class.getPackage().getName());
	
	private DataMap _map;
	private DbAdapter _dbAdapter;
	private String _connectionString;
	private boolean dropTable = true;
	
	public SQLGenerator(ProjectGenerator prjgen, boolean _dropTable){
		super(prjgen, FileFormat.SQL, ResourceType.GENERATED_CODE, (_dropTable?"re-":"")+"createDB.sql", (_dropTable?"re-":"")+"createDB.sql");
		dropTable = _dropTable;
		resetGenerator();
	}
	
	private DataMap buildMergedDataMap(FlexoProject prj) throws Exception{
		DataMap datamap = new DataMap();
		Enumeration<DMEOModel> en = prj.getDataModel().getAllDMEOModel().elements();
		while(en.hasMoreElements()){
			DMEOModel dmeomodel = en.nextElement();
			DataMap dataMapToMerge = new EOModelProcessor().loadEOModel(dmeomodel.createTemporaryCopyOfMemory().getAbsolutePath());
			datamap.mergeWithDataMap(dataMapToMerge);
		}
		return datamap;
	}
	
	private void resetGenerator(){
		_connectionString = getProject().getDataModel().getGlobalDefaultConnectionString();
		_dbAdapter = findDBAdapter();
	}
	private DbAdapter findDBAdapter(){
		if(_connectionString==null) {
			return null;
		}
		if(_connectionString.toLowerCase().indexOf("oracle")>-1) {
			return new OracleAdapter();
		}
		if(_connectionString.toLowerCase().indexOf("frontbase")>-1) {
			return new FrontBaseAdapter();
		}
		if(_connectionString.toLowerCase().indexOf("postgres")>-1) {
			return new PostgresAdapter();
		}
		if(_connectionString.toLowerCase().indexOf("mysql")>-1) {
			return new MySQLAdapter();
		}
		if(_connectionString.toLowerCase().indexOf("derby")>-1) {
			return new DerbyAdapter();
		}
		if(_connectionString.toLowerCase().indexOf("hsql")>-1) {
			return new HSQLDBAdapter();
		}
		if(_connectionString.toLowerCase().indexOf("h2")>-1) {
			return new H2DBAdapter();
		}
		return null;
	}
	
	private void generateCode() throws GenerationException{
		if(getProject().getDataModel().getAllDMEOModel().size()==0) {
            generatedCode = new GeneratedTextResource(getFileName(),"");
            return;
        }      
		resetGenerator();
		if(_dbAdapter==null){
			throw new GenerationException("Cannot find a suitable DbAdapter for connection string : "+_connectionString);
		}
		try{
			_map = buildMergedDataMap(projectGenerator.getProject());
		}catch (Exception e) {
			throw new GenerationException("An exception occurs during merge of all models", "error_during_merge_models", "see console", e);
		}
		try {
            incorporatePrototypes();
            //DbGenerator dbGenerator = new DbGenerator(_dbAdapter,_map);
            MyDBGenerator dbGenerator = new MyDBGenerator(_dbAdapter,_map,Collections.EMPTY_LIST,null,new DBEntityComparator(getProject()));
            dbGenerator.setShouldDropTables(dropTable);
            dbGenerator.setShouldCreateTables(true);
            dbGenerator.setShouldCreateFKConstraints(true);
            dbGenerator.setShouldDropPKSupport(dropTable);
            dbGenerator.setShouldCreatePKSupport(true);
//		Iterator<DbEntity> en = _map.getDbEntities().iterator();
//		while(en.hasNext()){
//			System.out.println(en.next().getName());
//		}
            dbGenerator.buildStatements();
            StringBuilder buf = new StringBuilder();
            if (_dbAdapter instanceof FrontBaseAdapter) {
				buf.append("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;\n\n");
			}
            Iterator it = dbGenerator.configuredStatements().iterator();
            String batchTerminator = dbGenerator.getAdapter().getBatchTerminator();

            String lineEnd = (batchTerminator != null)
                        ? "\n" + batchTerminator + "\n\n"
                        : "\n\n";

            while (it.hasNext()) {
            	buf.append(it.next()).append(lineEnd);
            }
            generatedCode = new GeneratedTextResource(getFileName(), buf.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new GenerationException("sql_generation_error",null,null,e);
        }
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getProjectSymbolicDirectory();
	}

	@Override
	public void generate(boolean forceRegenerate) {
		try{
			if(forceRegenerate || (generatedCode==null)) {
				startGeneration();
				refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+getIdentifier(),false);
				generateCode();
				stopGeneration();
			}
		}catch (GenerationException e) {
			setGenerationException(e);
		}

	}

	@Override
	public Vector<CGTemplate> getUsedTemplates() {
		return new Vector<CGTemplate>();
	}

	@Override
	public boolean isCodeAlreadyGenerated() {
		return generatedCode!=null;
	}

	@Override
	public boolean needsGeneration() {
		return !isCodeAlreadyGenerated();
	}

	@Override
	public boolean needsRegenerationBecauseOfTemplateUpdated() {
		return false;
	}

	@Override
	public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate) {
		return false;
	}

	@Override
	public Vector<CGRepositoryFileResource> refreshConcernedResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}
	
	private void incorporatePrototypes(){
		Iterator entityIterator = _map.getDbEntities().iterator();
		DbEntity entity = null;
		DbAttribute attribute = null;
		DMEOPrototype prototype = null;
		DMEOAttribute dmeoattribute = null;
		while(entityIterator.hasNext()){
			entity = (DbEntity)entityIterator.next();
			Iterator attributeIterator = entity.getAttributes().iterator();
			while(attributeIterator.hasNext()){
				attribute = (DbAttribute)attributeIterator.next();
				dmeoattribute = matchingDMEOAttribute(entity,attribute);
				attribute.setMandatory(!dmeoattribute.getAllowsNull());
				if(dmeoattribute!=null){
					prototype = dmeoattribute.getPrototype();
					if(prototype!=null){
						String externalType = prototype.getExternalType();
						if("CHAR VARYING".equals(externalType)) {
							externalType="VARCHAR";
						}
						if("CHARACTER".equals(externalType)) {
							externalType="CHAR";
						}
						if("DOUBLE PRECISION".equals(externalType)) {
							externalType="DOUBLE";
						}
						if("INT".equals(externalType)) {
							externalType="INTEGER";
						}
						int sqlType = TypesMapping.getSqlTypeByName(externalType);
						if((attribute.getMaxLength()<1) && (prototype.getWidth()>0)) {
							attribute.setMaxLength(prototype.getWidth());
						}
						attribute.setType(sqlType);
					}
				}
			}
		}
	}
	
	private DMEOAttribute matchingDMEOAttribute(DbEntity entity,DbAttribute attribute){
		DMEOAttribute reply = null;
		DMEOEntity dmeoentity = findDMEOEntityWithName(entity.getName());
		if(dmeoentity!=null){
			reply = findDMEOAttributeWithName(dmeoentity,attribute.getName());
		}else{
			logger.warning("cannot find the matching entity for :"+entity.getName());
		}
		return reply;
	}
	
	private DMEOAttribute findDMEOAttributeWithName(DMEOEntity dmeoentity,String attributeName){
		Iterator<DMEOAttribute> en = dmeoentity.getAttributes().values().iterator();
		DMEOAttribute candidate = null;
		while(en.hasNext()){
			candidate = en.next();
			if((candidate.getColumnName()!=null) && candidate.getColumnName().equals(attributeName)) {
				return candidate;
			}
		}
		return null;
	}
	
	private DMEOEntity findDMEOEntityWithName(String entityName){
		Enumeration<DMEOModel> en = getProject().getDataModel().getAllDMEOModel().elements();
		DMEOEntity candidate = null;
		DMEOModel model = null;
		while(en.hasMoreElements()){
			model = en.nextElement();
			Enumeration<DMEOEntity> en2 = model.getEntities().elements();
			while(en2.hasMoreElements()){
				candidate = en2.nextElement();
				if((candidate.getExternalName()!=null) && candidate.getExternalName().equals(entityName)) {
					return candidate;
				}
			}
		}
		return null;
	}

 

}