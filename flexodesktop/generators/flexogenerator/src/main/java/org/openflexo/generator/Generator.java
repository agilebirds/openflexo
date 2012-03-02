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
package org.openflexo.generator;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.TemplateDeleted;
import org.openflexo.foundation.cg.templates.TemplateFileChanged;
import org.openflexo.foundation.cg.templates.TemplateFileEdited;
import org.openflexo.foundation.cg.templates.TemplateFileEditionCancelled;
import org.openflexo.foundation.cg.templates.TemplateFileRedefined;
import org.openflexo.foundation.cg.templates.TemplateFileSaved;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.exception.VelocityException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.velocity.FlexoVelocity;
import org.openflexo.velocity.PostVelocityParser;

public abstract class Generator<T extends FlexoModelObject, R extends GenerationRepository> extends FlexoObservable implements
		DataFlexoObserver {

	public static class Holder<T> {
		private T value;

		public T get() {
			return value;
		}

		public T getValue() {
			return get();
		}

		public void set(T value) {
			this.value = value;
		}

		public void setValue(T value) {
			set(value);
		}
	}

	private static final Logger logger = FlexoLogger.getLogger(Generator.class.getPackage().getName());

	private Vector<CGTemplate> _usedTemplates = new Vector<CGTemplate>();

	private Vector<CGRepositoryFileResource<?, ?, ? extends CGFile>> generatedResources;

	private Vector<CGRepositoryFileResource> concernedResources = null;

	private T object;

	protected AbstractProjectGenerator<R> projectGenerator;

	private Date memoryLastGenerationDate;

	private GenerationException generationException;

	protected GeneratedCodeResult generatedCode;

	private boolean isGenerating = false;

	private boolean notifyObservers = true;

	// This map is static because the velocity engine is static. To avoid this we should have 1 engine velocity per repository
	private static Map<String, Long> macroLibMap = new HashMap<String, Long>(); // <Template Relative Path, Last modified date>

	public Generator(AbstractProjectGenerator<R> projectGenerator, T object) {
		this.projectGenerator = projectGenerator;
		this.object = object;
		this.generatedResources = new Vector<CGRepositoryFileResource<?, ?, ? extends CGFile>>();
		if (projectGenerator != null) {
			projectGenerator.addToGenerators(this);
		}
	}

	public AbstractProjectGenerator<R> getProjectGenerator() {
		return projectGenerator;
	}

	public R getRepository() {
		if (projectGenerator != null) {
			return projectGenerator.getRepository();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project generator is not set.");
			}
			return null;
		}
	}

	public final T getObject() {
		return object;
	}

	public FlexoProject getProject() {
		return getProjectGenerator().getProject();
	}

	public TargetType getTarget() {
		return getProjectGenerator().getTarget();
	}

	public CGRepositoryFileResource resourceForKeyWithCGFile(ResourceType type, String resourceName) {
		CGRepositoryFileResource ret = (CGRepositoryFileResource) getProject().resourceForKey(type, resourceName);
		if (ret != null && ret.getCGFile() == null) {
			ret.delete(false);
			ret = null;
		}
		return ret;
	}

	public final String getPrefix() {
		return getProject().getPrefix();
	}

	protected VelocityContext defaultContext() {
		VelocityContext context = new VelocityContext();
		if (object != null) {
			context.put("object", object);
		}
		context.put("null", null);
		context.put("project", getProject());
		context.put("projectGenerator", getProjectGenerator());
		context.put("repository", getRepository());
		context.put("generator", this);
		context.put("quote", "\"");
		context.put("backslash", "\\");
		context.put("sharp", "#");
		context.put("dollar", "$");
		context.put("prefix", getPrefix());
		context.put("n", StringUtils.LINE_SEPARATOR);
		context.put("trueValue", Boolean.TRUE);
		context.put("falseValue", Boolean.FALSE);
		context.put("toolbox", new ToolBox());
		context.put("stringUtils", new StringUtils());
		context.put("javaUtils", new JavaUtils());
		context.put("globalVariableMap", new HashMap<String, Object>() {
			@Override
			public Object put(String key, Object value) {
				if (value == null) {
					System.err.println("coucou");
				}
				return super.put(key, value);
			}
		});
		context.put("today", new Date());
        context.put("apacheStringUtils", org.apache.commons.lang.StringUtils.class);
		return context;
	}

	/**
	 * Build or retrieve all resources (eventually just created) involved in this repository generation
	 * 
	 * @param repository
	 * @return the list of resources (eventually just created) involved in this repository generation
	 */
	private Vector<CGRepositoryFileResource> buildGeneratedResourceListForRepository(R repository) {
		Vector<CGRepositoryFileResource> returned = new Vector<CGRepositoryFileResource>();
		buildResourcesAndSetGenerators(repository, returned);
		return returned;
	}

	// public Vector<CGRepositoryFileResource> getConcernedResources(R repository)
	// {
	// if (concernedResources == null) {
	// concernedResources = refreshConcernedResources();
	// }
	// return concernedResources;
	// }

	public Vector<CGRepositoryFileResource> refreshConcernedResources() {
		return refreshConcernedResources(null);
	}

	public Vector<CGRepositoryFileResource> refreshConcernedResources(
			Vector<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> forResources) {
		Vector<CGRepositoryFileResource> oldConcernedResources = concernedResources;
		concernedResources = buildGeneratedResourceListForRepository(getProjectGenerator().getRepository());
		if (oldConcernedResources != null) {
			for (CGRepositoryFileResource resource : oldConcernedResources) {
				if (!concernedResources.contains(resource) && resource.getCGFile() != null) {
					resource.getCGFile().setMarkedForDeletion(true);
				}
			}
		}
		for (CGRepositoryFileResource resource : concernedResources) {
			resource.rebuildDependancies();
		}
		for (CGRepositoryFileResource resource : concernedResources) {
			if (resource.needsGeneration() && (forResources == null || forResources.contains(resource))) {
				resource.getDependantResourcesUpToDate();
			}
		}
		return concernedResources;
	}

	public abstract void buildResourcesAndSetGenerators(R repository, Vector<CGRepositoryFileResource> resources);

	/**
	 * Generate code related to this generator. If this generator may store result, setting forceGenerate flag to false will result in
	 * giving the already generated code (cache scheme).
	 * 
	 * @param forceRegenerate
	 * @throws GenerationException
	 */
	public abstract void generate(boolean forceRegenerate) throws GenerationException;

	/**
	 * Generate code related to this generator, using cache scheme if present Equivalent for call generate(false)
	 * 
	 * @throws GenerationException
	 */
	public void generate() throws GenerationException {
		generate(false);
	}

	public String merge(String templateName) throws GenerationException {
		return merge(templateName, defaultContext());
	}

	private void updateVelocityMacroIfRequired() throws VelocityException, UnexpectedExceptionOccuredException {
		try {
			// the next lines ensures that the macro library will be added as a template used by this generator

			List<CGTemplate> macroTemplates = getProjectGenerator().getVelocityMacroTemplates();
			macroTemplates.addAll(getVelocityMacroTemplates());

			synchronized (RuntimeSingleton.getRuntimeServices()) {

				Map<String, Long> newMacroLibMap = new HashMap<String, Long>();
				for (CGTemplate template : macroTemplates) {
					newMacroLibMap.put(template.getRelativePath(), template.getLastUpdate().getTime());
					notifyTemplateRequired(template);
				}

				if (!newMacroLibMap.equals(macroLibMap)) {
					FlexoVelocity.addToVelocimacro(getTemplateLocator(), macroTemplates.toArray(new CGTemplate[0]));
					macroLibMap = newMacroLibMap;
				}
			}
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			throw new VelocityException(e, getProjectGenerator());
		} catch (ParseErrorException e) {
			e.printStackTrace();
			throw new VelocityException(e, getProjectGenerator());
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnexpectedExceptionOccuredException(e, getProjectGenerator());
		}
	}

	/**
	 * Override this if you want to use one or more velocity macro library. <br>
	 * All macro templates from the project generator and from the current generator will be added for generation. Use the project generator
	 * one to include macros for all merge. <br>
	 * Order is important in case of multiple macro are defined with the same name. In such case, the last one will be taken into account.
	 * 
	 * @return list of template to use as Macro library.
	 */
	public List<CGTemplate> getVelocityMacroTemplates() {
		return new ArrayList<CGTemplate>();
	}

	public String merge(String templateRelativePath, VelocityContext velocityContext) throws GenerationException {
		StringWriter sw = new StringWriter();
		try {
			updateVelocityMacroIfRequired();
			CGTemplate template = templateWithName(templateRelativePath);

			Velocity.setApplicationAttribute("templateLocator", getTemplateLocator());
			Velocity.mergeTemplate(template.getRelativePathWithoutSetPrefix(), "UTF-8", velocityContext, sw);
			Velocity.setApplicationAttribute("templateLocator", null);

		} catch (TemplateNotFoundException e) {
			throw e;
		} catch (Exception e) {
			if (e instanceof MethodInvocationException) {
				if (((MethodInvocationException) e).getWrappedThrowable() != null) {
					((MethodInvocationException) e).getWrappedThrowable().printStackTrace();
				}
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Could not merge template: " + e);
				e.printStackTrace();
				Throwable t = e;
				while (t.getCause() != null) {
					t = t.getCause();
				}
				if (!e.equals(t)) {
					logger.fine("Originally caused by: " + t.getMessage());
					t.printStackTrace();
				}
			}
			throw new VelocityException(e, getProjectGenerator());
		}
		return PostVelocityParser.parseAndRenderCustomTag(sw.toString());
	}

	public AbstractGCAction getAction() {
		return getProjectGenerator().getAction();
	}

	protected boolean hasProgressWindow() {
		return getAction() != null && getAction().getFlexoProgress() != null;
	}

	protected void refreshProgressWindow(String stepName, boolean localize) {
		if (hasProgressWindow()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("************** refreshProgressWindow: " + stepName);
			}
			getAction().getFlexoProgress().setProgress(localize ? FlexoLocalization.localizedForKey(stepName) : stepName);
		}
	}

	protected void resetSecondaryProgressWindow(int stepNb) {
		if (hasProgressWindow()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("**** resetSecondaryProgressWindow: " + stepNb);
			}
			getAction().getFlexoProgress().resetSecondaryProgress(stepNb);
		}
	}

	protected void refreshSecondaryProgressWindow(String stepName, boolean localize) {
		if (hasProgressWindow()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(">>>>>>>>>>>>> refreshSecondaryProgressWindow: " + stepName);
			}
			getAction().getFlexoProgress().setSecondaryProgress(localize ? FlexoLocalization.localizedForKey(stepName) : stepName);
		}
	}

	public abstract Logger getGeneratorLogger();

	public void addToGeneratedResourcesGeneratedByThisGenerator(CGRepositoryFileResource<?, ?, ? extends CGFile> resource) {
		if (!generatedResources.contains(resource)) {
			generatedResources.add(resource);
		}
	}

	public void removeFromGeneratedResourcesGeneratedByThisGenerator(CGRepositoryFileResource<?, ?, ? extends CGFile> resource) {
		generatedResources.remove(resource);
		memoryLastGenerationDate = null;
		generatedCode = null;
	}

	protected Vector<CGRepositoryFileResource<?, ?, ? extends CGFile>> getGeneratedResources() {
		return generatedResources;
	}

	public TemplateLocator getTemplateLocator() {
		return getProjectGenerator().getTemplateLocator();
	}

	public String getTemplatePath(String templateName) throws TemplateNotFoundException {
		// Legacy method for backward compatibility
		CGTemplate templateFile = getTemplateLocator().templateWithName(templateName);
		notifyTemplateRequired(templateFile);
		return templateName;
	}

	public CGTemplate templateWithName(String templateRelativePath) throws TemplateNotFoundException {
		CGTemplate templateFile = getTemplateLocator().templateWithName(templateRelativePath);
		notifyTemplateRequired(templateFile);
		return templateFile;
	}

	public void clearTemplates() {
		clearTemplates(false);
	}

	public void clearTemplates(boolean clearTemplateLocatorCache) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("clearTemplates() for " + this);
		}
		for (CGTemplate templateFile : _usedTemplates) {
			templateFile.deleteObserver(this);
		}
		_usedTemplates.clear();
		if (clearTemplateLocatorCache) {
			getTemplateLocator().notifyTemplateModified();
		}
	}

	public void notifyTemplateRequired(CGTemplate templateFile) {
		logger.info("notifyTemplateRequired " + templateFile.getRelativePath() + " for " + this);
		if (!_usedTemplates.contains(templateFile)) {
			_usedTemplates.add(templateFile);
			templateFile.addObserver(this);
		}
	}

	public void silentlyGenerateCode() {
		notifyObservers = false;
		try {
			generate(true);
		} catch (GenerationException e) {
			e.printStackTrace();
		}
		notifyObservers = true;
	}

	public void startGeneration() {
		isGenerating = true;
		// getProjectGenerator().startGeneration(this);
		if (needsRegenerationBecauseOfTemplateChange()) {
			clearTemplates(true);
		}
		generationException = null;
	}

	public void stopGeneration() {
		// getProjectGenerator().stopGeneration(this);
		memoryLastGenerationDate = new Date();
		isGenerating = false;
		if (this instanceof IFlexoResourceGenerator && notifyObservers) {
			setChanged();
			notifyObservers(new ContentRegenerated(this, ((IFlexoResourceGenerator) this).getGeneratedCode()));
		}
	}

	public Date getMemoryLastGenerationDate() {
		if (memoryLastGenerationDate == null) {
			if (generatedResources.size() == 0) {
				memoryLastGenerationDate = new Date(1); // 1 because AFTER default template last update
			} else {
				for (CGRepositoryFileResource<?, ?, ? extends CGFile> r : generatedResources) {
					Date d = r.getCGFile().getLastAcceptingDate();
					if (d != null && (memoryLastGenerationDate == null || d.before(memoryLastGenerationDate))) {
						memoryLastGenerationDate = d;
					}
				}
			}
		}
		return memoryLastGenerationDate;
	}

	public boolean isCodeAlreadyGenerated() {
		if (this instanceof IFlexoResourceGenerator) {
			return ((IFlexoResourceGenerator) this).getGeneratedCode() != null;
		}
		// Not significant
		return true;
	}

	/**
	 * @return
	 */
	public GeneratedCodeResult getGeneratedCode() {
		return generatedCode;
	}

	public boolean needsGeneration() {
		if (isGenerating) {
			return false;
		}
		if (getGenerationException() != null) {
			return true;
		}
		if (generatedResources.size() == 0) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No resource generated by this generator!");
			}
		} else {
			for (CGRepositoryFileResource<?, ?, ?> r : generatedResources) {
				if (r.retrieveNeedsMemoryGenerationFlag()) {
					return true;
				}
			}
		}
		return needsRegenerationBecauseOfTemplateUpdated() || needsRegenerationBecauseOfTemplateChange() || !isCodeAlreadyGenerated();
	}

	/**
	 * @return
	 */
	private boolean needsRegenerationBecauseOfTemplateChange() {
		return getTemplateLocator() != null && getTemplateLocator().needsRegenerationBecauseOfTemplateChange(this);
	}

	public boolean needsRegenerationBecauseOfTemplateUpdated() {
		return getTemplateLocator() != null && getTemplateLocator().needsUpdateForGenerator(getMemoryLastGenerationDate(), this);
	}

	public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate) {
		return getTemplateLocator() != null && getTemplateLocator().needsUpdateForGenerator(diskLastGenerationDate, this)
				|| needsRegenerationBecauseOfTemplateChange();
	}

	public boolean hasFormattingException() {
		return false;
	}

	public boolean hasAppendingException() {
		return false;
	}

	public GenerationException getGenerationException() {
		return generationException;
	}

	public void setGenerationException(GenerationException generationException) {
		this.generationException = generationException;
		if (projectGenerator != null) {
			projectGenerator.notifyExceptionOccured(generationException);
		}
		stopGeneration();
	}

	protected void resetGenerationException() {
		this.generationException = null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof TemplateFileChanged || dataModification instanceof TemplateFileEditionCancelled
				|| dataModification instanceof TemplateFileEdited || dataModification instanceof TemplateFileRedefined
				|| dataModification instanceof TemplateFileSaved || dataModification instanceof TemplateDeleted) {
			setChanged();
			notifyObservers(dataModification);
		}
	}

	/**
	 * Overrides getUsedTemplates
	 * 
	 * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getUsedTemplates()
	 */
	public Vector<CGTemplate> getUsedTemplates() {
		return _usedTemplates;
	}

	public void setUsedTemplates(Vector<CGTemplate> templates) {
		this._usedTemplates = templates;
	}

	public Holder<Object> getNewHolder() {
		return new Holder<Object>();
	}

	public Vector<Object> getNewVector() {
		return new Vector<Object>();
	}

	public Hashtable<Object, Object> getNewHashtable() {
		return new Hashtable<Object, Object>();
	}

	public TreeMap<Object, Object> getNewTreeMap() {
		return new TreeMap<Object, Object>();
	}

	public Properties getNewProperties() {
		return new Properties();
	}

	public TreeMap<FlexoModelObject, Object> getNewModelObjectTreeMap() {
		return new TreeMap<FlexoModelObject, Object>(new FlexoModelObject.FlexoDefaultComparator<FlexoModelObject>());
	}

	public BidiMap getNewBidiMap() {
		return new DualHashBidiMap();
	}

	public StringBuilder getNewStringBuilder() {
		return new StringBuilder();
	}

	public Stack<Object> getNewStack() {
		return new Stack<Object>();
	}

	public <C extends Comparable<C>> void sortVector(List<C> vectorToSort) {
		Collections.sort(vectorToSort);
	}

	public void sortVectorOfModelObject(List<FlexoModelObject> vectorToSort) {
		Collections.sort(vectorToSort, new FlexoModelObject.FlexoDefaultComparator<FlexoModelObject>());
	}

	public static String escapeStringForHTML(String s) {
		return HTMLUtils.escapeStringForHTML(s, false);
	}

	public static String escapeStringForHTML(String s, boolean removeNewLine) {
		return HTMLUtils.escapeStringForHTML(s, removeNewLine);
	}

	public static String extractBodyContent(String html) {
		return HTMLUtils.extractBodyContent(html);
	}

	public static String extractBodyContent(String html, boolean returnHtmlIfNoBodyFound) {
		return HTMLUtils.extractBodyContent(html, returnHtmlIfNoBodyFound);
	}

	public static String escapeStringForCsv(String s) {
		return ToolBox.escapeStringForCsv(s);
	}

	public String capitalize(String s) {
		return ToolBox.capitalize(s);
	}

	public static DateFormat getDateFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}

	public static String escapeStringForJS(String s) {
		return ToolBox.escapeStringForJS(s);
	}

	public static String escapeStringForProperties(String s) {
		return ToolBox.escapeStringForProperties(s);
	}

	public static String removeAllWhiteCharacters(String text) {
		return removeAllWhiteCharacters(text, " ");
	}

	public static String escapeStringForXML(String string) {
		String escapedString = StringEscapeUtils.escapeXml(string);
		return escapedString != null ? StringUtils.replaceBreakLinesBy(escapedString, " ") : "";
	}

	public static String removeAllWhiteCharacters(String text, String replacement) {
		if (text == null) {
			return null;
		}
		String replaced = text.replaceAll("(\\s)+", replacement);
		return replaced;
	}

	public static Integer getNumberAsInteger(Number number) {
		if (number != null) {
			return new Long(Math.round(number.doubleValue())).intValue();
		}
		return null;
	}
}
