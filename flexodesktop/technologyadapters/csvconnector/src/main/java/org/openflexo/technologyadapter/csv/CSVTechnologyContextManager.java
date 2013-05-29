/*    */ package org.openflexo.technologyadapter.csv;
/*    */ 
/*    */ import org.openflexo.foundation.resource.FlexoResourceCenterService;
/*    */ import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
/*    */ import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
/*    */ import org.openflexo.technologyadapter.csv.model.CSVMetaModel;
/*    */ import org.openflexo.technologyadapter.csv.model.CSVModel;
/*    */ 
/*    */ public class CSVTechnologyContextManager extends TechnologyContextManager<CSVModel, CSVMetaModel>
/*    */ {
/*    */   public CSVTechnologyContextManager(TechnologyAdapter<CSVModel, CSVMetaModel> adapter, FlexoResourceCenterService resourceCenterService)
/*    */   {
/* 42 */     super(adapter, resourceCenterService);
/*    */   }
/*    */ }

/* Location:           /u01/data/Projets/openflexo/1.6/Git/openflexo_backup/flexodesktop/technologyadapters/csvconnector/target/classes/
 * Qualified Name:     org.openflexo.technologyadapter.csv.CSVTechnologyContextManager
 * JD-Core Version:    0.6.2
 */