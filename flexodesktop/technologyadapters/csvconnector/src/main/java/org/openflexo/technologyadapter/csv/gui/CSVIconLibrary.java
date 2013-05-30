/*    */ package org.openflexo.technologyadapter.csv.gui;
/*    */ 
/*    */ import java.util.logging.Logger;
/*    */ import javax.swing.ImageIcon;
/*    */ import org.openflexo.foundation.ontology.IFlexoOntologyObject;
/*    */ import org.openflexo.toolbox.ImageIconResource;
/*    */ 
/*    */ public class CSVIconLibrary
/*    */ {
/* 39 */   private static final Logger logger = Logger.getLogger(CSVIconLibrary.class.getPackage().getName());
/*    */ 
/* 41 */   public static final ImageIcon CSV_TECHNOLOGY_BIG_ICON = new ImageIconResource("src/main/resources/Icons/csv-text_big.gif");
/* 42 */   public static final ImageIcon CSV_TECHNOLOGY_ICON = new ImageIconResource("src/main/resources/Icons/csv-text.gif");
/* 43 */   public static final ImageIcon CSV_FILE_ICON = new ImageIconResource("src/main/resources/Icons/csv-text.gif");
/*    */ 
/*    */   public static ImageIcon iconForObject(Class<? extends IFlexoOntologyObject> objectClass)
/*    */   {
/* 47 */     return null;
/*    */   }
/*    */ }

/* Location:           /u01/data/Projets/openflexo/1.6/Git/openflexo_backup/flexodesktop/technologyadapters/csvconnector/target/classes/
 * Qualified Name:     org.openflexo.technologyadapter.csv.gui.CSVIconLibrary
 * JD-Core Version:    0.6.2
 */