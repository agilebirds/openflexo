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
package org.openflexo.foundation.view.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.ViewLibraryObject;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.AddShape;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.IProgress;

public class GenerateSepelCleanedUpProject extends FlexoAction<GenerateSepelCleanedUpProject, ViewLibrary, ViewLibraryObject> {

	private static final Logger logger = Logger.getLogger(GenerateSepelCleanedUpProject.class.getPackage().getName());

	public static FlexoActionType<GenerateSepelCleanedUpProject, ViewLibrary, ViewLibraryObject> actionType = new FlexoActionType<GenerateSepelCleanedUpProject, ViewLibrary, ViewLibraryObject>(
			"generate_cleaned_up_project", FlexoActionType.exportMenu, FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateSepelCleanedUpProject makeNewAction(ViewLibrary focusedObject, Vector<ViewLibraryObject> globalSelection,
				FlexoEditor editor) {
			return new GenerateSepelCleanedUpProject(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewLibrary object, Vector<ViewLibraryObject> globalSelection) {
			for (ViewDefinition v : object.getViewLibrary().getAllShemaList()) {
				if (v.getViewPoint().getURI().equals("http://www.thalesgroup.com/ViewPoints/sepel-ng/MappingSpecification.owl")) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(ViewLibrary object, Vector<ViewLibraryObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(GenerateSepelCleanedUpProject.actionType, ViewLibrary.class);
	}

	public File exportDirectory;
	private IProgress progress;

	GenerateSepelCleanedUpProject(ViewLibrary focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		exportDirectory = new File(focusedObject.getProject().getProjectDirectory().getParentFile(), focusedObject.getProject().getName()
				+ "-cleaned.prj");
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Export cleaned-up project");
		progress = makeFlexoProgress(FlexoLocalization.localizedForKey("generating_cleaned_up_project"), getFocusedObject()
				.getAllShemaList().size() + 3);

		inputPrj = getFocusedObject().getProject();

		progress.setProgress("Creating new project " + exportDirectory.getAbsolutePath());

		outputPrjEditor = FlexoResourceManager.initializeNewProject(exportDirectory, EDITOR_FACTORY, inputPrj.getResourceCenter());
		outputPrj = outputPrjEditor.getProject();

		// Initialize

		progress.setProgress("Initializing... ");

		grObjectsMapping = new Hashtable<ViewObject, ViewObject>();
		epiMapping = new Hashtable<EditionPatternInstance, EditionPatternInstance>();
		individualsMapping = new Hashtable<EditionPatternInstance, Hashtable<OntologyIndividual, OntologyIndividual>>();
		individualsMapping2 = new Hashtable<OntologyIndividual, OntologyIndividual>();

		convertedViews = new ArrayList<View>();

		inputViewLibrary = inputPrj.getShemaLibrary();
		outputViewLibrary = outputPrj.getShemaLibrary(true);

		convertFolder(inputViewLibrary.getRootFolder(), outputViewLibrary.getRootFolder());

		// Finalizing

		progress.setProgress("Finalizing... ");

		finalizeDiagramReferencing();

		progress.setProgress("Save exported project ");

		// Saving

		saveOutputProject();

		progress.hideWindow();

	}

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new DefaultFlexoEditor(project);
		}
	};

	private FlexoEditor outputPrjEditor;
	private FlexoProject inputPrj = null;
	private FlexoProject outputPrj = null;
	private ViewLibrary inputViewLibrary;
	private ViewLibrary outputViewLibrary;;

	private Hashtable<EditionPatternInstance, Hashtable<OntologyIndividual, OntologyIndividual>> individualsMapping;
	private Hashtable<OntologyIndividual, OntologyIndividual> individualsMapping2;
	private Hashtable<ViewObject, ViewObject> grObjectsMapping;
	private Hashtable<EditionPatternInstance, EditionPatternInstance> epiMapping;

	private List<View> convertedViews;

	private void storeIndividual(OntologyIndividual inputIndividual, OntologyIndividual outputIndividual, EditionPatternInstance inputEPI) {
		Hashtable<OntologyIndividual, OntologyIndividual> hashtable = individualsMapping.get(inputEPI);
		if (hashtable == null) {
			hashtable = new Hashtable<OntologyIndividual, OntologyIndividual>();
			individualsMapping.put(inputEPI, hashtable);
		}
		hashtable.put(inputIndividual, outputIndividual);
		individualsMapping2.put(inputIndividual, outputIndividual);
	}

	private OntologyIndividual retrieveIndividual(OntologyIndividual inputIndividual, EditionPatternInstance inputEPI) {
		if (individualsMapping.get(inputEPI) != null && individualsMapping.get(inputEPI).get(inputIndividual) != null) {
			return individualsMapping.get(inputEPI).get(inputIndividual);
		}
		return individualsMapping2.get(inputIndividual);
	}

	public void convertFolder(ViewFolder inputFolder, ViewFolder outputFolder) {
		for (ViewFolder f : inputFolder.getSubFolders()) {
			System.out.println("On cree un folder " + f.getName());
			AddViewFolder addViewFolderAction = AddViewFolder.actionType.makeNewAction(outputFolder, null, outputPrjEditor);
			addViewFolderAction.setNewFolderName(f.getName());
			addViewFolderAction.doAction();
			convertFolder(f, addViewFolderAction.getNewFolder());
		}
		for (ViewDefinition v : inputFolder.getViews()) {
			System.out.println("On cree une vue " + v.getName() + " title=" + v.getTitle() + " for viewpoint " + v.getViewPoint());
			AddView addViewAction = AddView.actionType.makeNewAction(outputFolder, null, outputPrjEditor);
			addViewAction.useViewPoint = (v.getViewPoint() != null);
			addViewAction.newViewName = v.getName();
			addViewAction.newViewTitle = v.getTitle();
			addViewAction.viewpoint = v.getViewPoint();
			addViewAction.doAction();
			convertView(v.getView(), addViewAction.getNewDiagram().getView());
			convertedViews.add(v.getView());
		}
	}

	public void finalizeDiagramReferencing() {
		// We are sure now that all diagrams are defined
		for (View inputView : convertedViews) {
			ViewPoint viewPoint = inputView.getViewPoint();
			for (EditionPattern ep : viewPoint.getEditionPatterns()) {
				for (EditionPatternInstance inputEPI : inputView.getEPInstances(ep)) {
					EditionPatternInstance outputEPI = epiMapping.get(inputEPI);
					for (PatternRole pr : ep.getPatternRoles()) {
						if (pr instanceof DiagramPatternRole) {
							referenceDiagram((DiagramPatternRole) pr, inputEPI, outputEPI);
						}
					}
				}
			}
		}
	}

	private void referenceDiagram(DiagramPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
		View diagram = (View) inputEPI.getPatternActor(pr);
		if (diagram != null) {
			outputEPI.setObjectForPatternRole(grObjectsMapping.get(diagram), pr);
			System.out.println(" > role " + pr + " reference diagram " + diagram);
		}
	}

	public void convertView(View inputView, View outputView) {
		System.out.println("On convertit une view");
		if (inputView.getViewPoint().getURI().equals("http://www.thalesgroup.com/ViewPoints/sepel-ng/MappingSpecification.owl")) {
			(new ViewConverter(inputView, outputView)).convert();
		}
	}

	public class ViewConverter {
		private View inputView;
		private View outputView;

		private EditionPattern genericMappingCaseEP;
		private EditionPattern exampleMappingCaseEP;
		private EditionPattern inputEP;
		private EditionPattern outputEP;

		private EditionPattern hasInputEP;
		private EditionPattern hasOutputEP;
		private EditionPattern hasConditionalOutputEP;

		private EditionPattern inputClassReferenceEP;
		private EditionPattern inputOccurenceEP;
		private EditionPattern multipleInputOccurenceEP;

		private EditionPattern outputClassReferenceEP;
		private EditionPattern outputOccurenceEP;
		private EditionPattern multipleOutputOccurenceEP;

		private EditionPattern inputAttributeReferenceEP;
		private EditionPattern outputAttributeReferenceEP;
		private EditionPattern transformationEP;
		private EditionPattern functionMappingEP;
		private EditionPattern numericMappingEP;
		private EditionPattern stringMappingEP;
		private EditionPattern enumMappingEP;
		private EditionPattern directCopyMappingEP;
		private EditionPattern noMappingEP;

		private EditionPattern transformationRuleEP;
		private EditionPattern enumValueMappingEP;
		private EditionPattern stringTruncatureEP;

		private EditionPattern hasInputClassReferenceEP;
		private EditionPattern hasAttributeReferenceAsTransformationInputEP;
		private EditionPattern hasInputOccurenceEP;

		private EditionPattern hasOutputClassReferenceEP;
		private EditionPattern hasAttributeReferenceAsTransformationOutputEP;
		private EditionPattern hasOutputOccurenceEP;

		private EditionPattern linkToSingleInputConceptEP;
		private EditionPattern linkToMultipleInputConceptEP;
		private EditionPattern linkToSingleOutputConceptEP;
		private EditionPattern linkToMultipleOutputConceptEP;
		private EditionPattern linkInputClassReferencesEP;
		private EditionPattern linkOutputClassReferencesEP;

		private ViewPoint viewPoint;

		public ViewConverter(View inputView, View outputView) {
			super();
			this.inputView = inputView;
			this.outputView = outputView;

			viewPoint = inputView.getViewPoint();

			System.out.println("Converting view " + inputView);
			System.out.println("Progress=" + progress);

			progress.setProgress("Converting view " + inputView.getName());
			progress.resetSecondaryProgress(viewPoint.getEditionPatterns().size() * 2);

			genericMappingCaseEP = viewPoint.getEditionPattern("GenericMappingCase");
			exampleMappingCaseEP = viewPoint.getEditionPattern("ExampleMappingCase");
			inputEP = viewPoint.getEditionPattern("Input");
			outputEP = viewPoint.getEditionPattern("Output");

			hasInputEP = viewPoint.getEditionPattern("HasInput");
			hasOutputEP = viewPoint.getEditionPattern("HasOutput");
			hasConditionalOutputEP = viewPoint.getEditionPattern("HasConditionalOutput");

			inputClassReferenceEP = viewPoint.getEditionPattern("InputClassReference");
			inputOccurenceEP = viewPoint.getEditionPattern("InputOccurence");
			multipleInputOccurenceEP = viewPoint.getEditionPattern("MultipleInputOccurence");

			outputClassReferenceEP = viewPoint.getEditionPattern("OutputClassReference");
			outputOccurenceEP = viewPoint.getEditionPattern("OutputOccurence");
			multipleOutputOccurenceEP = viewPoint.getEditionPattern("MultipleOutputOccurence");

			inputAttributeReferenceEP = viewPoint.getEditionPattern("InputAttributeReference");
			outputAttributeReferenceEP = viewPoint.getEditionPattern("OutputAttributeReference");

			transformationEP = viewPoint.getEditionPattern("Transformation");
			functionMappingEP = viewPoint.getEditionPattern("FunctionMapping");
			numericMappingEP = viewPoint.getEditionPattern("NumericMapping");
			stringMappingEP = viewPoint.getEditionPattern("StringMapping");
			enumMappingEP = viewPoint.getEditionPattern("EnumMapping");
			directCopyMappingEP = viewPoint.getEditionPattern("DirectCopy");
			noMappingEP = viewPoint.getEditionPattern("NoMapping");

			transformationRuleEP = viewPoint.getEditionPattern("TransformationRule");
			enumValueMappingEP = viewPoint.getEditionPattern("EnumValueMapping");
			stringTruncatureEP = viewPoint.getEditionPattern("StringTruncature");

			hasInputClassReferenceEP = viewPoint.getEditionPattern("HasInputClassReference");
			hasAttributeReferenceAsTransformationInputEP = viewPoint.getEditionPattern("HasAttributeReferenceAsTransformationInput");
			hasInputOccurenceEP = viewPoint.getEditionPattern("HasInputOccurence");
			hasOutputClassReferenceEP = viewPoint.getEditionPattern("HasOutputClassReference");
			hasAttributeReferenceAsTransformationOutputEP = viewPoint.getEditionPattern("HasAttributeReferenceAsTransformationOutput");
			hasOutputOccurenceEP = viewPoint.getEditionPattern("HasOutputOccurence");

			linkToSingleInputConceptEP = viewPoint.getEditionPattern("LinkToSingleInputConcept");
			linkToMultipleInputConceptEP = viewPoint.getEditionPattern("LinkToMultipleInputConcept");
			linkToSingleOutputConceptEP = viewPoint.getEditionPattern("LinkToSingleOutputConcept");
			linkToMultipleOutputConceptEP = viewPoint.getEditionPattern("LinkToMultipleOutputConcept");
			linkInputClassReferencesEP = viewPoint.getEditionPattern("LinkInputClassReferences");
			linkOutputClassReferencesEP = viewPoint.getEditionPattern("LinkOutputClassReferences");

			grObjectsMapping.put(inputView, outputView);

		}

		public void convert() {

			convertEPI(genericMappingCaseEP);
			convertEPI(exampleMappingCaseEP);
			convertEPI(inputEP);
			convertEPI(outputEP);

			convertEPI(hasInputEP);
			convertEPI(hasOutputEP);
			convertEPI(hasConditionalOutputEP);

			convertEPI(inputClassReferenceEP);
			convertEPI(inputOccurenceEP);
			convertEPI(multipleInputOccurenceEP);

			convertEPI(outputClassReferenceEP);
			convertEPI(outputOccurenceEP);
			convertEPI(multipleOutputOccurenceEP);

			convertEPI(inputAttributeReferenceEP);
			convertEPI(outputAttributeReferenceEP);

			convertEPI(transformationEP);
			convertEPI(functionMappingEP);
			convertEPI(numericMappingEP);
			convertEPI(stringMappingEP);
			convertEPI(enumMappingEP);
			convertEPI(directCopyMappingEP);
			convertEPI(noMappingEP);

			convertEPI(transformationRuleEP);
			convertEPI(enumValueMappingEP);
			convertEPI(stringTruncatureEP);

			convertEPI(hasInputClassReferenceEP);
			convertEPI(hasAttributeReferenceAsTransformationInputEP);
			convertEPI(hasInputOccurenceEP);
			convertEPI(hasOutputClassReferenceEP);
			convertEPI(hasAttributeReferenceAsTransformationOutputEP);
			convertEPI(hasOutputOccurenceEP);

			convertEPI(linkToSingleInputConceptEP);
			convertEPI(linkToMultipleInputConceptEP);
			convertEPI(linkToSingleOutputConceptEP);
			convertEPI(linkToMultipleOutputConceptEP);
			convertEPI(linkInputClassReferencesEP);
			convertEPI(linkOutputClassReferencesEP);

			restoreReferences(genericMappingCaseEP);
			restoreReferences(exampleMappingCaseEP);
			restoreReferences(inputEP);
			restoreReferences(outputEP);

			restoreReferences(hasInputEP);
			restoreReferences(hasOutputEP);
			restoreReferences(hasConditionalOutputEP);

			restoreReferences(inputClassReferenceEP);
			restoreReferences(inputOccurenceEP);
			restoreReferences(multipleInputOccurenceEP);

			restoreReferences(outputClassReferenceEP);
			restoreReferences(outputOccurenceEP);
			restoreReferences(multipleOutputOccurenceEP);

			restoreReferences(inputAttributeReferenceEP);
			restoreReferences(outputAttributeReferenceEP);

			restoreReferences(transformationEP);
			restoreReferences(functionMappingEP);
			restoreReferences(numericMappingEP);
			restoreReferences(stringMappingEP);
			restoreReferences(enumMappingEP);
			restoreReferences(directCopyMappingEP);
			restoreReferences(noMappingEP);

			restoreReferences(transformationRuleEP);
			restoreReferences(enumValueMappingEP);
			restoreReferences(stringTruncatureEP);

			restoreReferences(hasInputClassReferenceEP);
			restoreReferences(hasAttributeReferenceAsTransformationInputEP);
			restoreReferences(hasInputOccurenceEP);
			restoreReferences(hasOutputClassReferenceEP);
			restoreReferences(hasAttributeReferenceAsTransformationOutputEP);
			restoreReferences(hasOutputOccurenceEP);

			restoreReferences(linkToSingleInputConceptEP);
			restoreReferences(linkToMultipleInputConceptEP);
			restoreReferences(linkToSingleOutputConceptEP);
			restoreReferences(linkToMultipleOutputConceptEP);
			restoreReferences(linkInputClassReferencesEP);
			restoreReferences(linkOutputClassReferencesEP);

		}

		private void convertEPI(EditionPattern ep) {
			progress.setSecondaryProgress("Converting " + ep.getName());
			for (EditionPatternInstance inputEPI : inputView.getEPInstances(ep)) {
				System.out.println("EPI: " + inputEPI + " of " + ep);
				EditionPatternInstance outputEPI = outputPrj.makeNewEditionPatternInstance(ep);
				for (ShapePatternRole pr : sortedShapePatternRoles(ep)) {
					duplicateShape(pr, inputEPI, outputEPI);
				}
				for (PatternRole pr : ep.getPatternRoles()) {
					if (pr instanceof ConnectorPatternRole) {
						duplicateConnector((ConnectorPatternRole) pr, inputEPI, outputEPI);
					}
				}
				for (PatternRole pr : ep.getPatternRoles()) {
					if (pr instanceof ClassPatternRole) {
						referenceClass((ClassPatternRole) pr, inputEPI, outputEPI);
					} else if (pr instanceof PropertyPatternRole) {
						referenceProperty((PropertyPatternRole) pr, inputEPI, outputEPI);
					} else if (pr instanceof IndividualPatternRole) {
						duplicateIndividual((IndividualPatternRole) pr, inputEPI, outputEPI);
					}
				}
				epiMapping.put(inputEPI, outputEPI);
			}
		}

		private List<ShapePatternRole> sortedShapePatternRoles(final EditionPattern ep) {
			List<ShapePatternRole> returned = ep.getShapePatternRoles();
			Collections.sort(returned, new Comparator<ShapePatternRole>() {
				@Override
				public int compare(ShapePatternRole o1, ShapePatternRole o2) {
					if (ep.getDropSchemes().size() > 0) {
						int idx1 = -1;
						int idx2 = -1;
						DropScheme ds = ep.getDropSchemes().get(0);
						for (EditionAction a : ds.getActions()) {
							if (a instanceof AddShape) {
								if (((AddShape) a).getPatternRole() == o1) {
									idx1 = ds.getActions().indexOf(a);
								}
								if (((AddShape) a).getPatternRole() == o2) {
									idx2 = ds.getActions().indexOf(a);
								}
							}
						}
						return idx1 - idx2;
					}
					return 0;
				}
			});
			System.out.println("EditionPattern " + ep + " shapes=" + returned);
			return returned;
		}

		private void restoreReferences(EditionPattern ep) {
			progress.setSecondaryProgress("Restoring references for " + ep.getName());
			for (EditionPatternInstance inputEPI : inputView.getEPInstances(ep)) {
				System.out.println("restoreReferences for EPI: " + inputEPI + " of " + ep);
				EditionPatternInstance outputEPI = epiMapping.get(inputEPI);
				if (ep == genericMappingCaseEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
				} else if (ep == exampleMappingCaseEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingInputOntology("conformToStatement", ep, inputEPI, outputEPI);
				} else if (ep == inputEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementInternally("hasDiscriminatingCriteria", "input", "discriminatingCriteria", ep, inputEPI, outputEPI);
				} else if (ep == outputEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementInternally("hasDiscriminatingCriteria", "output", "discriminatingCriteria", ep, inputEPI, outputEPI);
				} else if (ep == hasInputEP) {
					retrieveStatementByAnalysingConnectorEndToStart("hasInputStatement", "genericMappingCase", "input", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingConnectorEndToStart("hasInputStatement", "exampleMappingCase", "input", ep, inputEPI,
							outputEPI);
				} else if (ep == hasOutputEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("hasOutputStatement", "genericMappingCase", "output", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("hasOutputStatement", "exampleMappingCase", "output", ep, inputEPI,
							outputEPI);
				} else if (ep == hasConditionalOutputEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingInputOntology("hasConditionalOutputStatement", ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingInputOntology("hasConditionStatement", ep, inputEPI, outputEPI);
					/*retrieveStatementByAnalysingConnectorStartToEnd("hasConditionalOutputStatement", "genericMappingCase", "output", ep,
							inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("hasConditionalOutputStatement", "exampleMappingCase", "output", ep,
							inputEPI, outputEPI);*/
				} else if (ep == inputClassReferenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasClassReferenceStatement", "input", "inputClassReference", ep,
							inputEPI, outputEPI);
				} else if (ep == inputOccurenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementInternally("hasDiscriminatingCriteria", "inputOccurence", "discriminatingCriteria", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasOccurence", "input", "inputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == multipleInputOccurenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementInternally("hasDiscriminatingCriteria", "multipleInputOccurence", "discriminatingCriteria", ep,
							inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasOccurence", "input", "multipleInputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == outputClassReferenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasClassReferenceStatement", "output", "outputClassReference", ep,
							inputEPI, outputEPI);
				} else if (ep == outputOccurenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementInternally("hasDiscriminatingCriteria", "outputOccurence", "discriminatingCriteria", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasOccurence", "output", "outputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == multipleOutputOccurenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
				} else if (ep == inputAttributeReferenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasAttributeReferenceStatement", "inputClassReference",
							"inputAttributeReference", ep, inputEPI, outputEPI);
				} else if (ep == outputAttributeReferenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasAttributeReferenceStatement", "outputClassReference",
							"outputAttributeReference", ep, inputEPI, outputEPI);
				} else if (ep == transformationEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == functionMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == numericMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == stringMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == enumMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == directCopyMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == noMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "genericMappingCase", "transformation", ep, inputEPI,
							outputEPI);
					retrieveStatementByAnalysingShapeContainment("hasTransformation", "exampleMappingCase", "transformation", ep, inputEPI,
							outputEPI);
				} else if (ep == transformationRuleEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("containsRuleStatement", "transformation", "transformationRule", ep,
							inputEPI, outputEPI);
				} else if (ep == enumValueMappingEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("containsRuleStatement", "transformation", "transformationRule", ep,
							inputEPI, outputEPI);
				} else if (ep == stringTruncatureEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingShapeContainment("containsRuleStatement", "transformation", "transformationRule", ep,
							inputEPI, outputEPI);
				} else if (ep == hasInputClassReferenceEP) {
					retrieveStatementByAnalysingConnectorEndToStart("hasInputClassReferenceStatement", "transformation",
							"inputClassReference", ep, inputEPI, outputEPI);
				} else if (ep == hasAttributeReferenceAsTransformationInputEP) {
					retrieveStatementByAnalysingConnectorEndToStart("hasTransformationInput", "transformation", "inputAttributeReference",
							ep, inputEPI, outputEPI);
				} else if (ep == hasInputOccurenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorEndToStart("hasInputOccurenceStatement", "transformation", "inputOccurence", ep,
							inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorEndToStart("hasInputOccurenceStatement", "transformation",
							"multipleInputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == hasOutputClassReferenceEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("hasOutputClassReferenceStatement", "transformation",
							"outputClassReference", ep, inputEPI, outputEPI);
				} else if (ep == hasAttributeReferenceAsTransformationOutputEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("hasTransformationOutput", "transformation",
							"outputAttributeReference", ep, inputEPI, outputEPI);
				} else if (ep == hasOutputOccurenceEP) {
					restoreDefaultReferences(ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("hasOutputOccurenceStatement", "transformation", "outputOccurence", ep,
							inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("hasOutputOccurenceStatement", "transformation",
							"multipleOutputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == linkToSingleInputConceptEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToSingleConceptStatement", "inputOccurence", "inputOccurence",
							ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToSingleConceptStatement", "multipleInputOccurence",
							"inputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == linkToMultipleInputConceptEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToMultipleConceptStatement", "inputOccurence",
							"multipleInputOccurence", ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToMultipleConceptStatement", "multipleInputOccurence",
							"multipleInputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == linkToSingleOutputConceptEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToSingleConceptStatement", "outputOccurence",
							"outputOccurence", ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToSingleConceptStatement", "multipleOutputOccurence",
							"outputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == linkToMultipleOutputConceptEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToMultipleConceptStatement", "outputOccurence",
							"multipleOutputOccurence", ep, inputEPI, outputEPI);
					retrieveStatementByAnalysingConnectorStartToEnd("isLinkedToMultipleConceptStatement", "multipleOutputOccurence",
							"multipleOutputOccurence", ep, inputEPI, outputEPI);
				} else if (ep == linkInputClassReferencesEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("hasRelationshipToClassReferenceStatement", "inputClassReference",
							"inputClassReference", ep, inputEPI, outputEPI);
				} else if (ep == linkOutputClassReferencesEP) {
					retrieveStatementByAnalysingConnectorStartToEnd("hasRelationshipToClassReferenceStatement", "outputClassReference",
							"outputClassReference", ep, inputEPI, outputEPI);
				}
			}
		}

		private void restoreDefaultReferences(EditionPattern ep, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			for (PatternRole pr : ep.getPatternRoles()) {
				if (pr instanceof IndividualPatternRole) {
					restoreReferences((IndividualPatternRole) pr, inputEPI, outputEPI);
				}
			}
		}

		private void retrieveStatementInternally(String statementPatternRole, String subject, String object, EditionPattern ep,
				EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ObjectPropertyStatementPatternRole statementPR = (ObjectPropertyStatementPatternRole) ep.getPatternRole(statementPatternRole);
			OntologyIndividual subjectIndividual = retrieveIndividual((OntologyIndividual) inputEPI.getPatternActor(subject), inputEPI);
			OntologyIndividual objectIndividual = retrieveIndividual((OntologyIndividual) inputEPI.getPatternActor(object), inputEPI);
			ObjectPropertyStatement newStatement = subjectIndividual.addPropertyStatement(
					(OntologyObjectProperty) statementPR.getObjectProperty(), objectIndividual);
			outputEPI.setObjectForPatternRole(newStatement, statementPR);
		}

		private void retrieveStatementByAnalysingInputOntology(String statementPatternRole, EditionPattern ep,
				EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ObjectPropertyStatementPatternRole statementPR = (ObjectPropertyStatementPatternRole) ep.getPatternRole(statementPatternRole);
			ObjectPropertyStatement inputStatement = (ObjectPropertyStatement) inputEPI.getPatternActor(statementPR);
			if (inputStatement == null) {
				logger.warning("Unexpected null inputStatement for " + statementPatternRole + " of " + ep);
				return;
			}
			if (inputStatement.getSubject() == null) {
				logger.warning("Unexpected null subject for " + statementPatternRole + " of " + ep);
				return;
			}
			if (inputStatement.getStatementObject() == null) {
				logger.warning("Unexpected null object for " + statementPatternRole + " of " + ep);
				return;
			}
			OntologyIndividual subjectIndividual = retrieveIndividual((OntologyIndividual) inputStatement.getSubject(), inputEPI);
			OntologyIndividual objectIndividual = retrieveIndividual((OntologyIndividual) inputStatement.getStatementObject(), inputEPI);
			ObjectPropertyStatement newStatement = subjectIndividual.addPropertyStatement(
					(OntologyObjectProperty) statementPR.getObjectProperty(), objectIndividual);
			outputEPI.setObjectForPatternRole(newStatement, statementPR);
		}

		private void retrieveStatementByAnalysingShapeContainment(String statementPatternRole, String subject, String object,
				EditionPattern ep, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ObjectPropertyStatementPatternRole statementPR = (ObjectPropertyStatementPatternRole) ep.getPatternRole(statementPatternRole);
			ViewShape inputShape = (ViewShape) inputEPI.getPatternActor("shape");
			if (inputShape == null) {
				logger.warning("Unexpected null shape for " + ep);
				return;
			}
			ViewShape inputContainer = (ViewShape) inputShape.getParent();
			if (inputContainer == null) {
				logger.warning("Unexpected null shape container for " + ep);
				return;
			}
			EditionPatternInstance containerEPI = inputContainer.getEditionPatternInstance();
			if (containerEPI.getPatternActor(subject) == null) {
				logger.warning("Unexpected null containerEPI.getPatternActor(subject) for " + ep);
				return;
			}
			if (inputEPI.getPatternActor(object) == null) {
				logger.warning("Unexpected null inputEPI.getPatternActor(object) for " + ep);
				return;
			}
			OntologyIndividual subjectIndividual = retrieveIndividual((OntologyIndividual) containerEPI.getPatternActor(subject),
					containerEPI);
			OntologyIndividual objectIndividual = retrieveIndividual((OntologyIndividual) inputEPI.getPatternActor(object), inputEPI);
			ObjectPropertyStatement newStatement = subjectIndividual.addPropertyStatement(
					(OntologyObjectProperty) statementPR.getObjectProperty(), objectIndividual);
			outputEPI.setObjectForPatternRole(newStatement, statementPR);
		}

		private void retrieveStatementByAnalysingConnectorStartToEnd(String statementPatternRole, String subject, String object,
				EditionPattern ep, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ObjectPropertyStatementPatternRole statementPR = (ObjectPropertyStatementPatternRole) ep.getPatternRole(statementPatternRole);
			ViewShape startInputShape = ((ViewConnector) inputEPI.getPatternActor("connector")).getStartShape();
			ViewShape endInputShape = ((ViewConnector) inputEPI.getPatternActor("connector")).getEndShape();
			EditionPatternInstance startEPI = startInputShape.getEditionPatternInstance();
			EditionPatternInstance endEPI = endInputShape.getEditionPatternInstance();
			if (startEPI.getPatternActor(subject) == null) {
				logger.warning("Unexpected null startEPI.getPatternActor(subject) for " + ep);
				return;
			}
			if (endEPI.getPatternActor(object) == null) {
				logger.warning("Unexpected null endEPI.getPatternActor(object) for " + ep);
				return;
			}
			OntologyIndividual subjectIndividual = retrieveIndividual((OntologyIndividual) startEPI.getPatternActor(subject), startEPI);
			OntologyIndividual objectIndividual = retrieveIndividual((OntologyIndividual) endEPI.getPatternActor(object), endEPI);
			ObjectPropertyStatement newStatement = subjectIndividual.addPropertyStatement(
					(OntologyObjectProperty) statementPR.getObjectProperty(), objectIndividual);
			outputEPI.setObjectForPatternRole(newStatement, statementPR);
		}

		private void retrieveStatementByAnalysingConnectorEndToStart(String statementPatternRole, String subject, String object,
				EditionPattern ep, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ObjectPropertyStatementPatternRole statementPR = (ObjectPropertyStatementPatternRole) ep.getPatternRole(statementPatternRole);
			ViewShape startInputShape = ((ViewConnector) inputEPI.getPatternActor("connector")).getStartShape();
			ViewShape endInputShape = ((ViewConnector) inputEPI.getPatternActor("connector")).getEndShape();
			EditionPatternInstance startEPI = startInputShape.getEditionPatternInstance();
			EditionPatternInstance endEPI = endInputShape.getEditionPatternInstance();
			if (endEPI.getPatternActor(subject) == null) {
				logger.warning("Unexpected null endEPI.getPatternActor(subject) for " + ep);
				return;
			}
			if (startEPI.getPatternActor(object) == null) {
				logger.warning("Unexpected null startEPI.getPatternActor(object) for " + ep);
				return;
			}
			OntologyIndividual subjectIndividual = retrieveIndividual((OntologyIndividual) endEPI.getPatternActor(subject), endEPI);
			OntologyIndividual objectIndividual = retrieveIndividual((OntologyIndividual) startEPI.getPatternActor(object), startEPI);
			ObjectPropertyStatement newStatement = subjectIndividual.addPropertyStatement(
					(OntologyObjectProperty) statementPR.getObjectProperty(), objectIndividual);
			outputEPI.setObjectForPatternRole(newStatement, statementPR);
		}

		private void restoreReferences(IndividualPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			OntologyIndividual inputIndividual = (OntologyIndividual) inputEPI.getPatternActor(pr);
			OntologyIndividual outputIndividual = retrieveIndividual(inputIndividual, inputEPI);
			for (OntologyStatement statement : inputIndividual.getStatements()) {
				if (statement instanceof ObjectPropertyStatement) {
					ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
					OntologyObjectProperty property = s.getProperty();
					OntologyIndividual inputValue = (OntologyIndividual) s.getStatementObject();
					OntologyIndividual outputValue = retrieveIndividual(inputValue, inputEPI);
					outputIndividual.addPropertyStatement(property, outputValue);
				}
			}
			// Object property statement will be added later

			outputIndividual.updateOntologyStatements();

			System.out.println(" > role " + pr + " restore references for " + outputIndividual.getURI());

		}

		private void duplicateShape(ShapePatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ViewShape inputShape = (ViewShape) inputEPI.getPatternActor(pr);
			ViewShape outputShape = new ViewShape(outputView);
			ShapeGraphicalRepresentation<ViewShape> newGR = new ShapeGraphicalRepresentation<ViewShape>();
			newGR.setsWith(inputShape.getGraphicalRepresentation());
			outputShape.setGraphicalRepresentation(newGR);
			// Register reference
			outputShape.registerEditionPatternReference(outputEPI, pr);
			ViewObject inputContainer = inputShape.getParent();
			ViewObject outputContainer = grObjectsMapping.get(inputContainer);
			outputContainer.addToChilds(outputShape);
			outputEPI.setObjectForPatternRole(outputShape, pr);
			grObjectsMapping.put(inputShape, outputShape);
			System.out.println(" > role " + pr + " add shape " + outputShape + " under " + outputContainer + " (" + inputShape
					+ " was under " + inputContainer + ")");
		}

		private void duplicateConnector(ConnectorPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			ViewConnector inputConnector = (ViewConnector) inputEPI.getPatternActor(pr);
			ViewShape outputStart = (ViewShape) grObjectsMapping.get(inputConnector.getStartShape());
			ViewShape outputEnd = (ViewShape) grObjectsMapping.get(inputConnector.getEndShape());
			ViewConnector outputConnector = new ViewConnector(outputView, outputStart, outputEnd);
			ViewObject parent = ViewObject.getFirstCommonAncestor(outputStart, outputEnd);
			if (parent == null) {
				throw new IllegalArgumentException("No common ancestor");
			}

			ConnectorGraphicalRepresentation<ViewConnector> newGR = new ConnectorGraphicalRepresentation<ViewConnector>();
			newGR.setsWith(inputConnector.getGraphicalRepresentation());
			outputConnector.setGraphicalRepresentation(newGR);

			parent.addToChilds(outputConnector);

			// Register reference
			outputConnector.registerEditionPatternReference(outputEPI, pr);

			outputEPI.setObjectForPatternRole(outputConnector, pr);
			grObjectsMapping.put(inputConnector, outputConnector);
			System.out.println(" > role " + pr + " add connector " + outputConnector + " under " + parent + " ( connect " + outputStart
					+ " to " + outputEnd + ")");
		}

		private void referenceClass(ClassPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			OntologyClass ontologyClass = (OntologyClass) inputEPI.getPatternActor(pr);
			outputEPI.setObjectForPatternRole(ontologyClass, pr);
			System.out.println(" > role " + pr + " reference class " + ontologyClass);
		}

		private void referenceProperty(PropertyPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			OntologyProperty ontologyProperty = (OntologyProperty) inputEPI.getPatternActor(pr);
			outputEPI.setObjectForPatternRole(ontologyProperty, pr);
			System.out.println(" > role " + pr + " reference property " + ontologyProperty);
		}

		private void duplicateIndividual(IndividualPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			OntologyIndividual inputIndividual = (OntologyIndividual) inputEPI.getPatternActor(pr);
			if (individualsMapping.get(inputIndividual) != null) {
				logger.warning("!!!!!!!!! Found duplicated individual : " + inputIndividual.getURI());
			}

			OntologyClass father = inputIndividual.getSuperClasses().get(0);
			String baseIndividualName = inputIndividual.getName();
			OntologyIndividual outputIndividual = null;

			String individualName = baseIndividualName;
			Integer i = null;
			while (outputPrj.getProjectOntologyLibrary().isDuplicatedURI(outputPrj.getProjectOntology().getURI(), individualName)) {
				if (i == null) {
					i = 1;
				} else {
					i++;
				}
				individualName = baseIndividualName + i;
			}
			try {
				outputIndividual = outputPrj.getProjectOntology().createOntologyIndividual(individualName, father);
				logger.info("********* Added individual " + outputIndividual.getName() + " as " + father);
				for (OntologyStatement statement : inputIndividual.getStatements()) {
					if (statement instanceof DataPropertyStatement) {
						DataPropertyStatement s = (DataPropertyStatement) statement;
						OntologyDataProperty property = s.getProperty();
						Object value = s.getValue();
						outputIndividual.addLiteral(property, value);
					}
				}
				// Object property statement will be added later

				outputIndividual.updateOntologyStatements();

				// Register reference
				outputIndividual.registerEditionPatternReference(outputEPI, pr);

			} catch (DuplicateURIException e) {
				e.printStackTrace();
			}

			outputEPI.setObjectForPatternRole(outputIndividual, pr);
			storeIndividual(inputIndividual, outputIndividual, inputEPI);
			System.out.println(" > role " + pr + " add individual " + outputIndividual.getURI());

		}
	}

	public void saveOutputProject() {
		try {
			outputPrj.save();
			logger.info("Project " + outputPrj + " has been saved");
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
	}

}
