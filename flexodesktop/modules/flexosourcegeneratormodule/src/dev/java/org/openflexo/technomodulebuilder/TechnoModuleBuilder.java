package org.openflexo.technomodulebuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openflexo.foundation.sg.implmodel.enums.TechnologyLayer;
import org.openflexo.toolbox.FileResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class TechnoModuleBuilder {

    private static String artifactId;
    private static String moduleLayer;
    private static String moduleName;
    private static File templateDir;
    private static File technologyModuleDirectory;

      public static void main(String[] args) throws IOException {
          moduleName = StringUtils.capitalize(args[0]);
          moduleLayer = args[1].toUpperCase();
          TechnologyLayer technologyLayer = TechnologyLayer.valueOf(moduleLayer);
          if(technologyLayer==null){
              throw new IllegalArgumentException("Technology Layer must of the enum TechnologyLayer");
          }
          File technologyModulesDirectory = new File("flexodesktop/technologymodules/");
          File flexosourcegeneratormoduleDirectory = new File("flexodesktop/modules/flexosourcegeneratormodule");
          if(!technologyModulesDirectory.exists()){
              throw new IllegalStateException("Please use the directory openflexo as working directory");
          }
           artifactId = moduleName.toLowerCase()+"technologymodule";
          technologyModuleDirectory = new File(technologyModulesDirectory,artifactId);
          if(technologyModuleDirectory.exists()){
              throw new IllegalStateException("Technology module "+moduleName+" already exists:"+technologyModuleDirectory.getAbsolutePath());
          }
          System.out.println("Creating module :"+technologyModuleDirectory.getAbsolutePath());
          technologyModuleDirectory.mkdir();
          templateDir = new File(flexosourcegeneratormoduleDirectory,"src/dev/resources/technomodulebuilder/template");
          if(!templateDir.exists()){
              throw new IllegalStateException("Cannot find template directory :"+templateDir.getAbsolutePath());
          }
          generate("pom.xml.vm",
                   "pom.xml",
                   defaultContext());
          generate("TechnologyDefinition.java.vm",
                  "src/main/java/org/openflexo/tm/"+moduleName.toLowerCase()+"/impl/"+moduleName+"TechnologyDefinition.java",
                  defaultContext());
          generate("Implementation.java.vm",
                  "src/main/java/org/openflexo/tm/"+moduleName.toLowerCase()+"/impl/"+moduleName+"Implementation.java",
                  defaultContext());
          generate("service.txt.vm",
                  "src/main/resources/META-INF/services/org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition",
                  defaultContext());
          generate("inspector.vm",
                  "src/main/resources/"+moduleName+"/Inspector/"+moduleName+"Implementation.inspector",
                  defaultContext());
          generate("main.vm",
                  "src/main/resources/"+moduleName+"/main.xml.vm",
                  defaultContext());
          generate("module.xml.vm",
                  "src/main/resources/"+moduleName+"/module.xml",
                  defaultContext());
          File VM_global_library = new File(technologyModuleDirectory,"src/main/resources/"+moduleName+"/VM_global_library.vm");
          VM_global_library.createNewFile();

          System.out.println("DON'T FORGET TO INCLUDE THE nEW MODULE IN PARENT POM !!!");
          System.out.println("DON'T FORGET TO MODULE AND VERSION OPENFLEXO PARENT POM !!!");
          System.out.println("DON'T FORGET TO MODULE IN THE FLEXOENTERPRISE RUNTIME DEPENDENCIES !!!");
          System.out.println("CONTENT TO INCLUDE IN im_model_O.1.xml");
          System.out.println(merge(new File(templateDir,"xmlcode.xml.vm"), defaultContext()));
          
          
//          File pomTemplate = new File(templateDir,"pom.xml.vm");
//          String pomContent = merge(pomTemplate,defaultContext());
//          System.out.println(pomContent);
      }
    
    private static void generate(String templateName, String targetFilePath, VelocityContext context) throws IOException {
       File targetFile = new File(technologyModuleDirectory, targetFilePath);
        File parentDir = targetFile.getParentFile();
        parentDir.mkdirs();
        targetFile.createNewFile();
        File template = new File(templateDir,templateName);
        generate(targetFile,template,context);
    }
    
    private static boolean debug = false;
    private static void generate(File targetFile, File templateFile, VelocityContext context) throws IOException {
        Writer sw = new FileWriter(targetFile);
        Reader readr = new FileReader(templateFile);
        if(debug){
            System.out.println("TARGET : "+targetFile.getAbsolutePath());
            System.out.println("CONTENT :");
            System.out.println(merge(templateFile, context));
        }else{
            Velocity.evaluate(context, sw, templateFile.getName(), readr);
        }
        sw.flush();
        sw.close();
    }
    
    private static VelocityContext defaultContext(){
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("artifactId",artifactId);
        velocityContext.put("moduleName",moduleName);
        velocityContext.put("moduleLayer",moduleLayer);
        return velocityContext;
    }
    
    private static String merge(File templateFile, VelocityContext velocityContext) throws FileNotFoundException {
        StringWriter sw = new StringWriter();
        Reader readr = new FileReader(templateFile);
        Velocity.evaluate(velocityContext, sw, templateFile.getName(), readr);
        return sw.toString();
    }
}
