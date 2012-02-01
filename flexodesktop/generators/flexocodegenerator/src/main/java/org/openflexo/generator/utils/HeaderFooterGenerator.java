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
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Generator for Header footer.
 * 
 * @author lulu
 */
public class HeaderFooterGenerator extends MetaWOGenerator {

	private static final Logger logger = Logger.getLogger(HeaderFooterGenerator.class.getPackage().getName());

	public HeaderFooterGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, null, projectGenerator.getPrefix() + "HeaderFooter", "");

	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public String findMenuBgColor() {
		try {
			FlexoCSS css = getProjectGenerator().getProject().getCssSheet();
			if (css.equals(FlexoCSS.CONTENTO)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("HeaderFooter CONTENTO style");
				}
				return "1D4382";
			}
			if (css.equals(FlexoCSS.OMNISCIO)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("HeaderFooter OMNISCIO style");
				}
				return "F5790F";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("HeaderFooter FLEXO style");
		}
		return "A2B95E";
	}

	public String findMenuSelectedBgColor() {
		try {
			FlexoCSS css = getProjectGenerator().getProject().getCssSheet();
			if (css.equals(FlexoCSS.CONTENTO)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("HeaderFooter CONTENTO style");
				}
				return "A5B3CD";
			}
			if (css.equals(FlexoCSS.OMNISCIO)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("HeaderFooter OMNISCIO style");
				}
				return "F9BA6D";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("HeaderFooter FLEXO style");
		}
		return "4A7732";
	}

	private VelocityContext fillContext() throws GenerationException {
		VelocityContext context = defaultContext();
		context.put("buttonsJava", generateButtons("java"));
		context.put("buttonsHtml", generateButtons("html"));
		context.put("buttonsWod", generateButtons("wod"));
		return context;
	}

	public String findLogoName() {
		String imageRelPath = getProject().getFlexoNavigationMenu().getRootMenu().getNavigationMenu().getLogo() != null ? getProject()
				.getFlexoNavigationMenu().getRootMenu().getNavigationMenu().getLogo().getImageName() : null;
		if (imageRelPath != null && imageRelPath.length() > 0 && !getProject().getFlexoNavigationMenu().getUseDefaultImage()) {
			return imageRelPath.substring(imageRelPath.lastIndexOf('/') + 1);
		}
		return "AgileBirds_Logo.jpg";
	}

	public String findLogoFramework() {
		if (findLogoName().equals("AgileBirds_Logo.jpg")) {
			return "DenaliWebResources";
		}
		return null;
	}

	@Override
	public synchronized void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			VelocityContext context = fillContext();

			String javaCode = merge("HeaderFooter.java.vm", context);
			javaAppendingException = null;
			try {
				javaCode = JavaCodeMerger.mergeJavaCode(javaCode, getEntity(), javaResource);
			} catch (JavaParseException e) {
				javaAppendingException = new JavaAppendingException(this, getEntity().getFullQualifiedName(), e);
				logger.warning("Could not parse generated code. Escape java merge.");
			}
			_javaFormattingException = null;
			try {
				javaCode = GeneratorFormatter.formatJavaCode(javaCode, "", getComponentClassName(), this, getProject());
			} catch (JavaFormattingException javaFormattingException) {
				_javaFormattingException = javaFormattingException;
			}
			String apiCode = merge("HeaderFooter.api.vm", context);
			String htmlCode = merge("HeaderFooter.html.vm", context);
			String wodCode = GeneratorFormatter.formatWodCode(merge("HeaderFooter.wod.vm", context));
			generatedCode = new GeneratedComponent(getIdentifier(), javaCode, apiCode, htmlCode, wodCode, GeneratorUtils.defaultWOO());
			stopGeneration();
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		} finally {
			stopGeneration();
		}
	}

	private String generateButtons(String fileType) throws GenerationException {
		String buttons = getProject().getFlexoNavigationMenu().getRootMenu().getNavigationMenu().getButtons();
		String das = getProject().getFlexoNavigationMenu().getRootMenu().getNavigationMenu().getActions();
		if (buttons != null && buttons.length() > 0) {
			String[] b = buttons.split(";");

			String[] actions = das.split(";");
			StringBuffer buttonsListJav = new StringBuffer();
			StringBuffer buttonsListHtml = new StringBuffer();
			StringBuffer buttonsListWod = new StringBuffer();
			if (b.length == actions.length) {
				for (int i = 0; i < b.length; i++) {
					VelocityContext vc = new VelocityContext();
					vc.put("WOD_NAME", ToolBox.getJavaName(b[i]));
					vc.put("NAME", b[i]);
					vc.put("BUTTON_NAME", ToolBox.getJavaName(b[i]));
					vc.put("JAVA_BUTTON_NAME", ToolBox.capitalize(ToolBox.getJavaName(b[i])));
					if (actions[i].indexOf('#') > 0) {
						vc.put("CLASSNAME", actions[i].substring(0, actions[i].indexOf('#')));
						vc.put("TAB", actions[i].substring(actions[i].indexOf('#') + 1));
					} else {
						vc.put("CLASSNAME", actions[i]);
					}

					buttonsListHtml.append(merge("HFButton.html.vm", vc));
					buttonsListJav.append(merge("HFButton.java.vm", vc));
					buttonsListWod.append(merge("HFButton.wod.vm", vc));
				}
			}
			if (fileType.equals("java")) {
				return buttonsListJav.toString();
			}
			if (fileType.equals("html")) {
				return buttonsListHtml.toString();
			}
			return buttonsListWod.toString();
		} else {
			return "";
		}
	}

	/**
	 * @return
	 */
	public String getStyleSheet() {
		FlexoCSS css = getProjectGenerator().getProject().getCssSheet();
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
	 * 
	 * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentJavaFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(UtilComponentJavaFileResource java) {
		java.addToDependentResources(getProject().getFlexoNavigationMenuResource());
	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentWOFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(UtilComponentWOFileResource wo) {
		wo.addToDependentResources(getProject().getFlexoNavigationMenuResource());
	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentAPIFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(UtilComponentAPIFileResource api) {
		api.addToDependentResources(getProject().getFlexoNavigationMenuResource());
	}

}
