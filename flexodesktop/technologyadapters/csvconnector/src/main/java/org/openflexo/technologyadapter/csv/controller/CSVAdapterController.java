/*     */ package org.openflexo.technologyadapter.csv.controller;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.ImageIcon;
/*     */ import org.openflexo.components.widget.OntologyBrowserModel;
/*     */ import org.openflexo.foundation.ontology.IFlexoOntology;
/*     */ import org.openflexo.foundation.ontology.IFlexoOntologyObject;
/*     */ import org.openflexo.foundation.viewpoint.PatternRole;
/*     */ import org.openflexo.inspector.ModuleInspectorController;
/*     */ import org.openflexo.technologyadapter.csv.CSVTechnologyAdapter;
/*     */ import org.openflexo.technologyadapter.csv.gui.CSVIconLibrary;
/*     */ import org.openflexo.toolbox.FileResource;
/*     */ import org.openflexo.view.controller.ControllerActionInitializer;
/*     */ import org.openflexo.view.controller.FlexoController;
/*     */ import org.openflexo.view.controller.TechnologyAdapterController;
/*     */ 
/*     */ public class CSVAdapterController extends TechnologyAdapterController<CSVTechnologyAdapter>
/*     */ {
/*  45 */   static final Logger logger = Logger.getLogger(CSVAdapterController.class.getPackage().getName());
/*     */ 
/*     */   public Class<CSVTechnologyAdapter> getTechnologyAdapterClass()
/*     */   {
/*  49 */     return CSVTechnologyAdapter.class;
/*     */   }
/*     */ 
/*     */   public void initializeActions(ControllerActionInitializer actionInitializer)
/*     */   {
/*  55 */     actionInitializer.getController().getModuleInspectorController()
/*  56 */       .loadDirectory(new FileResource("src/main/resources/Inspectors/CSV"));
/*     */   }
/*     */ 
/*     */   public ImageIcon getTechnologyBigIcon()
/*     */   {
/*  66 */     return CSVIconLibrary.CSV_TECHNOLOGY_BIG_ICON;
/*     */   }
/*     */ 
/*     */   public ImageIcon getTechnologyIcon()
/*     */   {
/*  76 */     return CSVIconLibrary.CSV_TECHNOLOGY_ICON;
/*     */   }
/*     */ 
/*     */   public ImageIcon getModelIcon()
/*     */   {
/*  86 */     return CSVIconLibrary.CSV_FILE_ICON;
/*     */   }
/*     */ 
/*     */   public ImageIcon getMetaModelIcon()
/*     */   {
/*  96 */     return CSVIconLibrary.CSV_FILE_ICON;
/*     */   }
/*     */ 
/*     */   public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass)
/*     */   {
/* 107 */     return CSVIconLibrary.iconForObject(objectClass);
/*     */   }
/*     */ 
/*     */   public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass)
/*     */   {
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context)
/*     */   {
/* 120 */     return null;
/*     */   }
/*     */ }

/* Location:           /u01/data/Projets/openflexo/1.6/Git/openflexo_backup/flexodesktop/technologyadapters/csvconnector/target/classes/
 * Qualified Name:     org.openflexo.technologyadapter.csv.controller.CSVAdapterController
 * JD-Core Version:    0.6.2
 */