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
package org.openflexo.foundation.wkf;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.AlgorithmicUnit;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ProgrammingLanguage;

/**
 * This interface is implemented by objects for which a control graph is available in the context of workflow execution
 * 
 * @author sylvain
 * 
 */
public interface ExecutableWorkflowElement {

	public String getExecutableElementName();

	public void setProgrammingLanguageForControlGraphComputation(ProgrammingLanguage language);

	public void setInterproceduralForControlGraphComputation(boolean interprocedural);

	public static abstract class ControlGraphFactory<E extends ExecutableWorkflowElement> {
		public static final ProgrammingLanguage DEFAULT_LANGUAGE = ProgrammingLanguage.JAVA;
		public static final boolean DEFAULT_INTERPROCEDURAL = true;

		private String unlocalizedInfoLabel;

		private Hashtable<E, WorkflowControlGraph<E>> storedControlGraphs;

		public ControlGraphFactory(String unlocalizedInfoLabel) {
			this.unlocalizedInfoLabel = unlocalizedInfoLabel;
			storedControlGraphs = new Hashtable<E, WorkflowControlGraph<E>>();
		}

		public WorkflowControlGraph<E> getControlGraph(E object) {
			if (storedControlGraphs.get(object) == null) {
				storedControlGraphs.put(object, makeWorkflowControlGraph(object));
			}
			return storedControlGraphs.get(object);
		}

		public WorkflowControlGraph<E> makeWorkflowControlGraph(E object) {
			return new WorkflowControlGraph<E>(object, this);
		}

		public abstract AlgorithmicUnit computeAlgorithmicUnit(E object, boolean interprocedural);

		public String getInfoLabel() {
			return FlexoLocalization.localizedForKey(unlocalizedInfoLabel);
		}

		public abstract String prettyPrintedCode(AlgorithmicUnit algorithmicUnit, ProgrammingLanguage language);
	}

	public static class WorkflowControlGraph<E extends ExecutableWorkflowElement> extends KVCObject {

		private static final Logger logger = Logger.getLogger(ExecutableWorkflowElement.WorkflowControlGraph.class.getPackage().getName());

		private E object;
		private ControlGraphFactory<E> factory;
		protected AlgorithmicUnit algorithmicUnit;
		private String code;

		private ProgrammingLanguage language = ControlGraphFactory.DEFAULT_LANGUAGE;
		protected boolean interprocedural = ControlGraphFactory.DEFAULT_INTERPROCEDURAL;

		public WorkflowControlGraph(E object, ControlGraphFactory<E> factory) {
			this.object = object;
			this.factory = factory;
			refresh();
		}

		public void refresh() {
			refreshAlgorithmicUnit();
		}

		protected void refreshAlgorithmicUnit() {
			logger.info("Recomputing control flow graph for " + object + " interprocedural=" + interprocedural);
			algorithmicUnit = factory.computeAlgorithmicUnit(object, interprocedural);
			refreshPrettyPrintedCode();
		}

		protected void refreshPrettyPrintedCode() {
			logger.info("Pretty-printing code for " + object + " language=" + language);
			code = factory.prettyPrintedCode(algorithmicUnit, language);
			// logger.info("Obtaining: "+code);
		}

		public String getInfoLabel() {
			return factory.getInfoLabel();
		}

		public String getFormattedCode() {
			return code;
		}

		public E getObject() {
			return object;
		}

		public ControlGraphFactory<E> getFactory() {
			return factory;
		}

		public boolean isInterprocedural() {
			return interprocedural;
		}

		public void setInterprocedural(boolean interprocedural) {
			if (interprocedural != this.interprocedural) {
				this.interprocedural = interprocedural;
				refreshAlgorithmicUnit();
			}
		}

		public ProgrammingLanguage getProgrammingLanguage() {
			return language;
		}

		public void setProgrammingLanguage(ProgrammingLanguage language) {
			if (language != this.language) {
				this.language = language;
				refreshPrettyPrintedCode();
			}
		}

		public AlgorithmicUnit getAlgorithmicUnit() {
			return algorithmicUnit;
		}
	}

}
