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
package org.openflexo.foundation.dm.javaparser;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public abstract class MethodSourceCode extends AbstractSourceCode 
{
	   private static final Logger logger = Logger.getLogger(MethodSourceCode.class.getPackage().getName());

	protected MethodSourceCode(
			SourceCodeOwner owner, 
			String propertyName, 
			String hasParseErrorPropertyName, 
			String parseErrorWarningPropertyName)
	{
		super(owner, propertyName, hasParseErrorPropertyName, parseErrorWarningPropertyName);
	}
	
	protected MethodSourceCode(
			SourceCodeOwner owner)
	{
		super(owner);
	}
	
	@Override
	public abstract String makeComputedCode();

	public abstract void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException;

	@Override
	public void interpretEditedCode(ParsedJavaElement javaElement) throws DuplicateMethodSignatureException
	{
		interpretEditedJavaMethod((ParsedJavaMethod)javaElement);
	}

	public ParsedJavaMethod getParsedMethod() throws ParserNotInstalledException
	{
		return parseCode(getCode());
	}

	@Override
	protected ParsedJavaMethod parseCode (final String qualifiedCode) throws ParserNotInstalledException 
	{
    	if (_javaMethodParser == null) throw new ParserNotInstalledException();
    	
    	try {
    		// Try to parse
    		ParsedJavaMethod parsedJavaMethod = _javaMethodParser.parseMethod(qualifiedCode, getOwner().getDMModel());
       		setHasParseErrors(false);
       		return parsedJavaMethod;
    	}
    	catch (JavaParseException e) {
    		setHasParseErrors(true);
    		setParseErrorWarning("<html><font color=\"red\">"
    				+FlexoLocalization.localizedForKey("parse_error_warning")
    				//+" method: "+qualifiedCode
    				+"</font></html>");
    		if (logger.isLoggable(Level.FINE)) logger.fine("Parse error while parsing method: "+qualifiedCode);
    		return null;
    	}    	
	}

	@Override
	public ParsedJavadoc parseJavadoc (final String qualifiedCode) throws ParserNotInstalledException
	{
		if (_javaMethodParser == null) throw new ParserNotInstalledException();
		try {
			return _javaMethodParser.parseJavadocForMethod(qualifiedCode, getOwner().getDMModel());
		} catch (JavaParseException e) {
    		setHasParseErrors(true);
    		setParseErrorWarning("<html><font color=\"red\">"
        			+FlexoLocalization.localizedForKey("parse_error_warning")
        			+"</font></html>");
    		return null;
		}
	}

	public String getCoreCode() 
	{
		String code = getCode();
		return code.substring(code.indexOf("{"),code.lastIndexOf("}"));
	}
    
	public void replaceMethodDeclarationInEditedCode (String newMethodDeclaration)
	{
		int beginIndex;
		int endIndex;
		
		//logger.info("Called replaceMethodDeclarationInEditedCode() with "+newMethodDeclaration);
		
		// First look javadoc
		int javadocBeginIndex = _editedCode.indexOf("/**");
		if (javadocBeginIndex > -1) {
			beginIndex = _editedCode.indexOf("*/")+2+StringUtils.LINE_SEPARATOR.length();
		}
		else {
			beginIndex = 0;
		}
		endIndex = _editedCode.indexOf(")",beginIndex)+1;
		
		//logger.info("Called replaceMethodDeclarationInEditedCode() beginIndex="+beginIndex+" endIndex="+endIndex);

		if (endIndex > beginIndex) {
			_editedCode 
			= _editedCode.substring(0,beginIndex)
			+ newMethodDeclaration
			+ _editedCode.substring(endIndex);
		}
		
	}

    private static JavaMethodParser _javaMethodParser;

    public static void setJavaMethodParser(JavaMethodParser javaMethodParser)
    {
    	_javaMethodParser = javaMethodParser;
    }
    
    public static JavaMethodParser getJavaMethodParser()
    {
    	return _javaMethodParser;
    }

    /**
     * Overrides isJavaParserInstalled
     * @see org.openflexo.foundation.dm.javaparser.AbstractSourceCode#isJavaParserInstalled()
     */
    @Override
    protected boolean isJavaParserInstalled()
    {
        return _javaMethodParser!=null;
    }

}
