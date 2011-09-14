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
package org.openflexo.cgmodule.controller.browser.fjp;

import javax.swing.SwingUtilities;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.javaparser.FJPJavaClass;
import org.openflexo.javaparser.FJPJavaElement;
import org.openflexo.javaparser.FJPJavaField;
import org.openflexo.javaparser.FJPJavaMethod;
import org.openflexo.javaparser.FJPJavaParseException;
import org.openflexo.javaparser.FJPJavaSource;
import org.openflexo.javaparser.FJPJavaSource.FJPImportDeclarations;
import org.openflexo.javaparser.FJPJavaSource.FJPPackageDeclaration;
import org.openflexo.javaparser.FJPJavaSource.FJPImportDeclarations.FJPImportDeclaration;


public class JavaParserBrowserConfiguration implements BrowserConfiguration
{
	private CGJavaFile _javaFile;
	private JavaParserBrowserConfigurationElementFactory _factory;
	
	protected JavaParserBrowserConfiguration(CGJavaFile javaFile)
	{
		super();
		_javaFile = javaFile;
		_factory = new JavaParserBrowserConfigurationElementFactory();
	}

	@Override
	public FlexoProject getProject() 
	{
		if (_javaFile != null)
			return _javaFile.getProject();
		return null;
	}
	
    @Override
	public void configure(final ProjectBrowser aBrowser) 
	{
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
					if (_javaFile.getParsedJavaSource() != null
							&& _javaFile.getParsedJavaSource().getClasses().length > 0)
						aBrowser.expand(_javaFile.getParsedJavaSource().getClasses()[0],true);
			}    		
    	});
 	}

	@Override
	public FJPJavaElement getDefaultRootObject()
	{
		if (_javaFile.getParsedJavaSource() != null)
			return _javaFile.getParsedJavaSource();
		else return _javaFile.getParseException();
	}

	@Override
	public BrowserElementFactory getBrowserElementFactory()
	{
		return _factory; 
	}

	class JavaParserBrowserConfigurationElementFactory implements BrowserElementFactory
	{

		JavaParserBrowserConfigurationElementFactory() {
			super();
		}

		@Override
		public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent)
		{
			if (object instanceof FJPJavaSource) {
				return new JavaSourceElement((FJPJavaSource)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPPackageDeclaration) {
				return new PackageDeclarationElement((FJPPackageDeclaration)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPImportDeclarations) {
				return new ImportDeclarationsElement((FJPImportDeclarations)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPImportDeclaration) {
				return new ImportDeclarationElement((FJPImportDeclaration)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPJavaClass) {
				return new JavaClassElement((FJPJavaClass)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPJavaMethod) {
				return new JavaMethodElement((FJPJavaMethod)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPJavaField) {
				return new JavaFieldElement((FJPJavaField)object,(JavaParserBrowser)browser,parent);
			}
			else if (object instanceof FJPJavaParseException) {
				return new JavaParseExceptionElement((FJPJavaParseException)object,(JavaParserBrowser)browser,parent);
			}
			return null;
		}

		

		

		

		

		
	}
}