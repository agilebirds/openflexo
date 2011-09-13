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

package org.openflexo.generator.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.generator.GeneratedComponent;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.generator.GeneratorFormatter;
import org.openflexo.generator.JavaCodeMerger;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.JavaAppendingException;
import org.openflexo.generator.exception.JavaFormattingException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.UtilComponentAPIFileResource;
import org.openflexo.generator.rm.UtilComponentJavaFileResource;
import org.openflexo.generator.rm.UtilComponentWOFileResource;


/**
 * Generator for Header footer.
 * 
 * @author lulu
 */
public class PopupHeaderFooterGenerator extends MetaWOGenerator
{
    private static final Logger logger = Logger.getLogger(PopupHeaderFooterGenerator.class.getPackage().getName());

	public PopupHeaderFooterGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator,null,projectGenerator.getPrefix() + "PopupHeaderFooter","");
    }

	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

    public String findMenuBgColor()
    {
        try {
            FlexoCSS css = getProject().getCssSheet();
            if (css.equals(FlexoCSS.CONTENTO)) {
                return "1D4382";
            }
            if (css.equals(FlexoCSS.OMNISCIO)) {
                return "F5790F";
            }
        } catch (Exception e) {
            // do nothing
        }
        return "A2B95E";
    }

    public String findMenuSelectedBgColor()
    {
        try {
            FlexoCSS css = getProject().getCssSheet();
            if (css.equals(FlexoCSS.CONTENTO)) {
                return "A5B3CD";
            }
            if (css.equals(FlexoCSS.OMNISCIO)) {
                return "F9BA6D";
            }
        } catch (Exception e) {
            // do nothing
        }
        return "4A7732";
    }

    @Override
	public synchronized void generate(boolean forceRegenerate)
    {
      	if (!forceRegenerate && !needsGeneration()) return;
    	try {
    		startGeneration();
    		if (logger.isLoggable(Level.INFO))
    			logger.info("Generating popup header footer");
    		VelocityContext vc = new VelocityContext();
            vc.put("project",getProject());
            vc.put("generator",this);
    		vc.put("PREFIX", getPrefix());
    		vc.put("MENU_ARRAY", "");

    		vc.put("CSS_FRAMEWORK", "DenaliWebResources");
    		vc.put("CSS_FILE", getStyleSheet());
    		vc.put("WEBRESOURCES_GENERIC_NAME", getWebResourceGenericName());
    		vc.put("MENU_BG_COLOR", findMenuBgColor());
    		vc.put("MENU_SELECTED_BG_COLOR", findMenuSelectedBgColor());

    		vc.put("WOCOMPONENTNAME", getPrefix() + "PopupHeaderFooter");
      		String javaCode = merge("PopupHeaderFooter.java.vm", vc);
    		try {
    			javaAppendingException = null;
				javaCode = JavaCodeMerger.mergeJavaCode(javaCode,getEntity(),javaResource);
    		} catch (JavaParseException e) {
    			javaAppendingException = new JavaAppendingException(this, getEntity().getFullQualifiedName(), e);
    			logger.warning("Could not parse generated code. Escape java merge.");
    		}
    		_javaFormattingException = null;
    		try {
    			javaCode = GeneratorFormatter.formatJavaCode(javaCode,getComponentFolderPath(),getComponentClassName(),this,getProject());
    		}
    		catch (JavaFormattingException javaFormattingException) {
    			_javaFormattingException = javaFormattingException;
    		}
    		String apiCode = merge("PopupHeaderFooter.api.vm", vc);
    		String htmlCode = GeneratorFormatter.formatHTMLCode(merge("PopupHeaderFooter.html.vm", vc));
    		String wodCode = GeneratorFormatter.formatWodCode(merge("PopupHeaderFooter.wod.vm", vc));

    		generatedCode = new GeneratedComponent(getComponentClassName(), javaCode, apiCode, htmlCode,
    				wodCode, GeneratorUtils.defaultWOO());
     	} catch (GenerationException e) {
    		setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e,getProjectGenerator()));
    	} finally {
    		stopGeneration();
    	}
    }

    /**
     * @return
     */
    private String getWebResourceGenericName()
    {
    	String s = getProject().getCssSheet().getName();
    	s = s.toLowerCase();
        s = (s.charAt(0) + "").toUpperCase() + s.substring(1);
        return s;
    }

    /**
     * @return
     */
    private String getStyleSheet()
    {
        FlexoCSS css = getProject().getCssSheet();
        if (css.equals(FlexoCSS.CONTENTO)) {
            return "ContentoMasterStyle.css";
        }
        if (css.equals(FlexoCSS.OMNISCIO)) {
            return "OmniscioMasterStyle.css";
        }
        return "FlexoMasterStyle.css";
    }

    /**
     * Overrides rebuildDependanciesForResource
     * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentJavaFileResource)
     */
    @Override
    public void rebuildDependanciesForResource(UtilComponentJavaFileResource java)
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * Overrides rebuildDependanciesForResource
     * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentWOFileResource)
     */
    @Override
    public void rebuildDependanciesForResource(UtilComponentWOFileResource wo)
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * Overrides rebuildDependanciesForResource
     * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentAPIFileResource)
     */
    @Override
    public void rebuildDependanciesForResource(UtilComponentAPIFileResource api)
    {
        // TODO Auto-generated method stub
        
    }

}
