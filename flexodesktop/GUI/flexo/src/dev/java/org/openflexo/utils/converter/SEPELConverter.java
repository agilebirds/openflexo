package org.openflexo.utils.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
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
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.action.AddView;
import org.openflexo.foundation.view.action.AddViewFolder;
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
import org.openflexo.module.FlexoResourceCenterService;

public class SEPELConverter {

	protected static final Logger logger = Logger.getLogger(SEPELConverter.class.getPackage().getName());

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new DefaultFlexoEditor(project);
		}
	};

	/*	protected FlexoEditor createProject(String projectName, FlexoResourceCenter resourceCenter) {
			ToolBox.setPlatform();
			FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
			File _projectDirectory = null;
			try {
				File tempFile = File.createTempFile(projectName, "");
				_projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
				tempFile.delete();
			} catch (IOException e) {
				fail();
			}
			logger.info("Project directory: " + _projectDirectory.getAbsolutePath());
			String _projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length() - 4);
			logger.info("Project identifier: " + _projectIdentifier);
			FlexoEditor reply = FlexoResourceManager.initializeNewProject(_projectDirectory, EDITOR_FACTORY, resourceCenter);
			logger.info("Project has been SUCCESSFULLY created");
			try {
				reply.getProject().setProjectName(_projectIdentifier);
				reply.getProject().saveModifiedResources(null);
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (SaveResourceException e) {
				e.printStackTrace();
				fail();
			}
			return reply;
		}

		protected void saveProject(FlexoProject prj) {
			try {
				prj.save();
			} catch (SaveResourceException e) {
				fail("Cannot save project");
			}
		}

		protected FlexoEditor reloadProject(File prjDir) {
			try {
				FlexoEditor _editor = null;
				assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(prjDir, EDITOR_FACTORY, null));
				_editor.getProject().setProjectName(_editor.getProject().getProjectName() + new Random().nextInt());
				return _editor;
			} catch (ProjectInitializerException e) {
				e.printStackTrace();
				fail();
			} catch (ProjectLoadingCancelledException e) {
				e.printStackTrace();
				fail();
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			}
			return null;
		}*/

	public static void main(String[] args) {
		// File inputPrjFile = new File("/users/sylvain/Documents/TestsFlexo/TestsSEPEL/ProjetCorrompus/CorruptedPrj.prj");
		File inputPrjFile = new File("/users/sylvain/Documents/TestsFlexo/TestsSEPEL/ProjetCorrompus/OpenflexoCorrompu.prj");
		File outputPrjFile = new File("/users/sylvain/tmp/Prout/test2.prj");
		SEPELConverter converter = new SEPELConverter(inputPrjFile, outputPrjFile);
		converter.startConversion();
	}

	private FlexoEditor inputPrjEditor = null;
	private FlexoEditor outputPrjEditor = null;
	private FlexoProject inputPrj = null;
	private FlexoProject outputPrj = null;
	private ViewLibrary inputViewLibrary;
	private ViewLibrary outputViewLibrary;;

	private Hashtable<OntologyIndividual, OntologyIndividual> individualsMapping;
	private Hashtable<ViewObject, ViewObject> grObjectsMapping;
	private Hashtable<EditionPatternInstance, EditionPatternInstance> epiMapping;

	private List<View> convertedViews;

	public SEPELConverter(File inputPrjFile, File outputPrjFile) {

		try {
			logger.info("Loading project " + inputPrjFile.getAbsolutePath());
			inputPrjEditor = FlexoResourceManager.initializeExistingProject(inputPrjFile, EDITOR_FACTORY, FlexoResourceCenterService
					.instance().getFlexoResourceCenter());
			inputPrj = inputPrjEditor.getProject();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
		}

		logger.info("Creating project " + outputPrjFile.getAbsolutePath());
		outputPrjEditor = FlexoResourceManager.initializeNewProject(outputPrjFile, EDITOR_FACTORY, FlexoResourceCenterService.instance()
				.getFlexoResourceCenter());
		outputPrj = outputPrjEditor.getProject();

		grObjectsMapping = new Hashtable<ViewObject, ViewObject>();
		epiMapping = new Hashtable<EditionPatternInstance, EditionPatternInstance>();
		individualsMapping = new Hashtable<OntologyIndividual, OntologyIndividual>();

		convertedViews = new ArrayList<View>();

	}

	public void startConversion() {
		inputViewLibrary = inputPrj.getShemaLibrary();
		outputViewLibrary = outputPrj.getShemaLibrary(true);

		convertFolder(inputViewLibrary.getRootFolder(), outputViewLibrary.getRootFolder());

		finalizeDiagramReferencing();

		saveOutputProject();
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
		(new ViewConverter(inputView, outputView)).convert();
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
			OntologyIndividual subjectIndividual = individualsMapping.get(inputEPI.getPatternActor(subject));
			OntologyIndividual objectIndividual = individualsMapping.get(inputEPI.getPatternActor(object));
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
			OntologyIndividual subjectIndividual = individualsMapping.get(inputStatement.getSubject());
			OntologyIndividual objectIndividual = individualsMapping.get(inputStatement.getStatementObject());
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
			OntologyIndividual subjectIndividual = individualsMapping.get(containerEPI.getPatternActor(subject));
			OntologyIndividual objectIndividual = individualsMapping.get(inputEPI.getPatternActor(object));
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
			OntologyIndividual subjectIndividual = individualsMapping.get(startEPI.getPatternActor(subject));
			OntologyIndividual objectIndividual = individualsMapping.get(endEPI.getPatternActor(object));
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
			OntologyIndividual subjectIndividual = individualsMapping.get(endEPI.getPatternActor(subject));
			OntologyIndividual objectIndividual = individualsMapping.get(startEPI.getPatternActor(object));
			ObjectPropertyStatement newStatement = subjectIndividual.addPropertyStatement(
					(OntologyObjectProperty) statementPR.getObjectProperty(), objectIndividual);
			outputEPI.setObjectForPatternRole(newStatement, statementPR);
		}

		private void restoreReferences(IndividualPatternRole pr, EditionPatternInstance inputEPI, EditionPatternInstance outputEPI) {
			OntologyIndividual inputIndividual = (OntologyIndividual) inputEPI.getPatternActor(pr);
			OntologyIndividual outputIndividual = individualsMapping.get(inputIndividual);
			for (OntologyStatement statement : inputIndividual.getStatements()) {
				if (statement instanceof ObjectPropertyStatement) {
					ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
					OntologyObjectProperty property = s.getProperty();
					OntologyIndividual inputValue = (OntologyIndividual) s.getStatementObject();
					OntologyIndividual outputValue = individualsMapping.get(inputValue);
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
			individualsMapping.put(inputIndividual, outputIndividual);
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
