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
package org.openflexo.generator.ie;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;

import org.openflexo.foundation.cg.generator.GeneratedComponent;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
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
import org.openflexo.generator.utils.MetaWOGenerator;
import org.openflexo.logging.FlexoLogger;


/**
 * @author gpolet
 * 
 */
public class ComponentGenerator extends MetaWOGenerator 
{

    Logger logger = FlexoLogger.getLogger(ComponentGenerator.class.getPackage().getName());

    /**
     * @param aProject
     */
    public ComponentGenerator(ProjectGenerator projectGenerator, ComponentDefinition componentDefinition, String componentGeneratedName)
    {
        super(projectGenerator,componentDefinition,componentGeneratedName,null);
    }

    public String getGeneratedComponentName()
    {
        if (getComponentDefinition() != null)
            return getComponentDefinition().getName();
        return generatedComponentName;
    }

    public void setGeneratedComponentName(String generatedComponentName)
    {
        this.generatedComponentName = generatedComponentName;
    }
 
    /**
     * Overrides defaultContext
     * @see org.openflexo.generator.CGGenerator#defaultContext()
     */
    @Override
    protected VelocityContext defaultContext()
    {
        VelocityContext vc =  super.defaultContext();
        if(getComponentDefinition() != null)
        {
	        vc.put("component", getComponentDefinition().getWOComponent());
	        vc.put("componentDefinition", getComponentDefinition());
	        vc.put("entity", getEntity());
        }
        return vc;
    }
    
    /***************************************************************************
     * Generation *
     **************************************************************************/

    public boolean isItemOfRepetition(DMProperty prop)
    {
    	Enumeration<RepetitionOperator> en = getComponentDefinition().getWOComponent().getAllRepetitionOperator().elements();
        RepetitionOperator op = null;
        while (en.hasMoreElements()) {
        	op = en.nextElement();
            if (!op.getFetchObjects() && op.getBindingItem()!=null && op.getBindingItem().isProperty(prop))
                return true;
        }
        return false;
    }

    @Override
	public synchronized void generate(boolean forceRegenerate)
    {
    	if (!forceRegenerate && !needsGeneration())
    		return;
    	startGeneration();
        try {
            VelocityContext vc = defaultContext();
            String woComponentName = getGeneratedComponentName();
			long start, end;
			start = System.currentTimeMillis();
			String javaCode = merge(getJavaTemplate(), vc);
			String apiCode = merge(getApiTemplate(), vc);
			String htmlCode = merge(getHtmlTemplate(), vc);
			String wodCode = merge(getWodTemplate(), vc);
			end = System.currentTimeMillis();
			if (logger.isLoggable(Level.INFO))
				logger.info("Generating code for " + woComponentName + " took " + (end - start) + "ms");
			javaAppendingException = null;
			try {
				javaCode = JavaCodeMerger.mergeJavaCode(javaCode,getEntity(),javaResource);
			} catch (JavaParseException e) {
				javaAppendingException = new JavaAppendingException(this, woComponentName, e);
				logger.warning("Could not parse generated code. Escape java merge.");
				if (logger.isLoggable(Level.FINE))
					logger.fine("Obtaining: "+javaCode);
			} 
            _javaFormattingException = null;
            try {
                javaCode = GeneratorFormatter.formatJavaCode(javaCode, "", getGeneratedComponentName(), this, getProject());
            } catch (JavaFormattingException javaFormattingException) {
                _javaFormattingException = javaFormattingException;
			}
			wodCode = GeneratorFormatter.formatWodCode(wodCode);
			htmlCode = GeneratorFormatter.formatHTMLCode(htmlCode);
			generatedCode = new GeneratedComponent(woComponentName, javaCode, apiCode, htmlCode, wodCode, GeneratorUtils.defaultWOO());
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) { // Catch all other kind of exceptions
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Component named: " + getGeneratedComponentName() + " was not generated properly.");
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		} finally {
			stopGeneration();
		}
	}

    public String getJavaTemplate() {
    	return "Component.java.vm";
    }
    
    public String getWodTemplate() {
    	return "Component.wod.vm";
    }
    
    public String getHtmlTemplate() {
    	return "Component.html.vm";
    }
    
    public String getApiTemplate() {
    	return "Component.api.vm";
    }
    
    protected IEWOComponent getIEWOComponent()
    {
        return component.getWOComponent();
    }

    public ComponentDMEntity getComponentEntity() {
    	return getComponentDefinition().getComponentDMEntity();
    }

    @Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	@Override
	public void rebuildDependanciesForResource(UtilComponentJavaFileResource java) {
		
	}

	@Override
	public void rebuildDependanciesForResource(UtilComponentWOFileResource wo) {
		
	}

	@Override
	public void rebuildDependanciesForResource(UtilComponentAPIFileResource api) {
		
	}
	
}