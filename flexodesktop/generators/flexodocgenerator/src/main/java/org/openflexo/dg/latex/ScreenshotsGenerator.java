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
package org.openflexo.dg.latex;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.file.DGScreenshotFile;
import org.openflexo.dg.rm.GeneratedFileResourceFactory;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.FlexoViewResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.generator.CopiedResourceGenerator;

/**
 * @author gpolet
 * 
 */
public class ScreenshotsGenerator extends AbstractCompoundGenerator<FlexoProject> {

	private Hashtable<FlexoCopiedResource, CopiedResourceGenerator> generators;

	/**
	 * @param projectGenerator
	 * @param source
	 */
	public ScreenshotsGenerator(ProjectDocGenerator projectGenerator, FlexoProject source) {
		super(projectGenerator, source);
		generators = new Hashtable<FlexoCopiedResource, CopiedResourceGenerator>();
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.dg.DGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.DGRepository, java.util.Vector)
	 */
	@Override
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		Hashtable<FlexoCopiedResource, CopiedResourceGenerator> newGenerators = new Hashtable<FlexoCopiedResource, CopiedResourceGenerator>();
		// First Workflow
		buildResourcesAndSetGeneratorsForProject(getProject(), resources, newGenerators);
		if (getProject().getProjectData() != null && getProject().getProjectData().getImportedProjects().size() > 0) {
			for (FlexoProjectReference ref : getProject().getProjectData().getImportedProjects()) {
				buildResourcesAndSetGeneratorsForProject(ref.getReferredProject(), resources, newGenerators);
			}
		}
		if (getProject().getFlexoComponentLibrary(false) != null) {
			// Now the components
			Enumeration<ComponentDefinition> en3 = getProject().getFlexoComponentLibrary().getAllComponentList().elements();
			while (en3.hasMoreElements()) {
				ComponentDefinition cd = en3.nextElement();
				FlexoCopiedResource cdCopy = getResourceForComponent(cd, true);
				resources.add(cdCopy);
				newGenerators.put(cdCopy, (CopiedResourceGenerator) cdCopy.getGenerator());
			}
		}
		if (getProject().getDataModel(false) != null) {
			// Now the ERDiagrams
			Enumeration<ERDiagram> en4 = getProject().getDataModel().getDiagrams().elements();
			while (en4.hasMoreElements()) {
				ERDiagram cd = en4.nextElement();
				FlexoCopiedResource cdCopy = getResourceForDiagram(cd, true);
				resources.add(cdCopy);
				newGenerators.put(cdCopy, (CopiedResourceGenerator) cdCopy.getGenerator());
			}
		}

		// Now the Views
		if (getProject() != null) {
			for (FlexoViewResource vr : getProject().getViewLibrary().getAllResources()) {
				FlexoCopiedResource cdCopy = getResourceForView(vr, true);
				resources.add(cdCopy);
				newGenerators.put(cdCopy, (CopiedResourceGenerator) cdCopy.getGenerator());
			}
		}

		generators.clear(); // Frees memory
		generators = newGenerators;
	}

	public void buildResourcesAndSetGeneratorsForProject(FlexoProject project, Vector<CGRepositoryFileResource> resources,
			Hashtable<FlexoCopiedResource, CopiedResourceGenerator> newGenerators) {
		if (project.getFlexoWorkflow(false) != null) {
			FlexoCopiedResource wCopy = getResourceForFlexoModelObject(project.getWorkflow(), true);
			resources.add(wCopy);
			newGenerators.put(wCopy, (CopiedResourceGenerator) wCopy.getGenerator());
			Enumeration<FlexoProcess> en = project.getAllLocalFlexoProcesses().elements(); // Processes
			while (en.hasMoreElements()) {
				FlexoProcess p = en.nextElement();
				FlexoCopiedResource pCopy = getResourceForProcess(p, true);
				resources.add(pCopy);
				newGenerators.put(pCopy, (CopiedResourceGenerator) pCopy.getGenerator());
				Enumeration<AbstractActivityNode> en1 = p.getAllEmbeddedAbstractActivityNodes().elements();
				while (en1.hasMoreElements()) {
					AbstractActivityNode a = en1.nextElement(); // Activities
					FlexoCopiedResource aCopy = getResourceForActivity(a, true);
					resources.add(aCopy);
					newGenerators.put(aCopy, (CopiedResourceGenerator) aCopy.getGenerator());
					Enumeration<OperationNode> en2 = a.getAllEmbeddedOperationNodes().elements();
					while (en2.hasMoreElements()) {
						OperationNode o = en2.nextElement(); // Operations
						if (!o.hasWOComponent()) {
							continue;
						}
						FlexoCopiedResource oCopy = getResourceForOperation(o, true);
						resources.add(oCopy);
						newGenerators.put(oCopy, (CopiedResourceGenerator) oCopy.getGenerator());
					}
				}

				Enumeration<OperatorNode> enOperator = p.getAllEmbeddedOperatorNodes().elements();
				while (enOperator.hasMoreElements()) {
					OperatorNode operatorNode = enOperator.nextElement();
					if (operatorNode instanceof LOOPOperator) {
						FlexoCopiedResource aCopy = getResourceForFlexoModelObject(operatorNode, true);
						resources.add(aCopy);
						newGenerators.put(aCopy, (CopiedResourceGenerator) aCopy.getGenerator());
					}
				}
			}

			// Now the role list
			FlexoCopiedResource roleListCopy = getResourceForFlexoModelObject(project.getFlexoWorkflow().getRoleList(), true);
			newGenerators.put(roleListCopy, (CopiedResourceGenerator) roleListCopy.getGenerator());
			resources.add(roleListCopy);
		}
	}

	private FlexoCopiedResource getResourceForFlexoModelObject(FlexoModelObject flexoModelObject, boolean createIfNull) {
		FlexoCopiedResource pCopy = (FlexoCopiedResource) getProject().resourceForKey(
				ResourceType.COPIED_FILE,
				FlexoCopiedResource.nameForCopiedResource(projectGenerator.getRepository(), flexoModelObject.getProject()
						.getScreenshotResource(flexoModelObject, true)));
		if (pCopy != null && pCopy.getCGFile() == null) {
			pCopy.delete(false);
			pCopy = null;
		}
		if (pCopy == null || pCopy.isDeleted() || pCopy.getResourceToCopy() == null || pCopy.getResourceToCopy().isDeleted()) {
			if (pCopy != null && !pCopy.isDeleted()) {
				pCopy.delete();
			}
			if (createIfNull) {
				DGScreenshotFile file = new DGScreenshotFile(projectGenerator.getRepository().getGeneratedDoc());
				pCopy = GeneratedFileResourceFactory.createNewCopiedFileResource(projectGenerator.getRepository(), file, projectGenerator
						.getRepository().getFiguresSymbolicDirectory(),
						flexoModelObject.getProject().getScreenshotResource(flexoModelObject));
			} else {
				return null;
			}
		} else {
		}
		pCopy.setGenerator(getGenerator(pCopy));
		return pCopy;
	}

	private FlexoCopiedResource getResourceForProcess(FlexoProcess p, boolean createIfNull) {
		return getResourceForFlexoModelObject(p, createIfNull);
	}

	private FlexoCopiedResource getResourceForActivity(AbstractActivityNode a, boolean createIfNull) {
		return getResourceForFlexoModelObject(a, createIfNull);
	}

	private FlexoCopiedResource getResourceForOperation(OperationNode o, boolean createIfNull) {
		return getResourceForFlexoModelObject(o, createIfNull);
	}

	private FlexoCopiedResource getResourceForComponent(ComponentDefinition cd, boolean createIfNull) {
		return getResourceForFlexoModelObject(cd, createIfNull);
	}

	private FlexoCopiedResource getResourceForDiagram(ERDiagram cd, boolean createIfNull) {
		return getResourceForFlexoModelObject(cd, createIfNull);
	}

	private FlexoCopiedResource getResourceForView(FlexoViewResource viewResource, boolean createIfNull) {
		return getResourceForFlexoModelObject(viewResource.getView(), createIfNull);
	}

	private CopiedResourceGenerator getGenerator(FlexoCopiedResource c) {
		CopiedResourceGenerator g = generators.get(c);
		if (g == null) {
			generators.put(c, g = new CopiedResourceGenerator<DGRepository>(c, getProjectGenerator(), this));
		}
		return g;
	}

	/**
	 * Overrides generate
	 * 
	 * @see org.openflexo.dg.latex.AbstractCompoundGenerator#generate(boolean)
	 */
	@Override
	public void generate(boolean forceRegenerate) {
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("This is a meta generator, generate cannot be called on meta generators, only resource generators can generate code.");
		}
	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.dg.FlexoResourceGenerator#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "DOC_SCREENSHOTS";
	}

	/**
	 * @param o
	 * @return
	 */
	public FlexoCopiedResource getScreenshot(FlexoModelObject o) {
		if (o instanceof ComponentDefinition) {
			return getResourceForComponent((ComponentDefinition) o, false);
		} else if (o instanceof FlexoProcess) {
			return getResourceForProcess((FlexoProcess) o, false);
		} else if (o instanceof AbstractActivityNode) {
			return getResourceForActivity((AbstractActivityNode) o, false);
		} else if (o instanceof OperationNode) {
			return getResourceForOperation((OperationNode) o, false);
		} else if (o instanceof LOOPOperator) {
			return getResourceForFlexoModelObject(o, false);
		} else if (o instanceof ERDiagram) {
			return getResourceForFlexoModelObject(o, false);
		} else if (o instanceof RoleList) {
			return getResourceForFlexoModelObject(o, false);
		} else if (o instanceof FlexoWorkflow) {
			return getResourceForFlexoModelObject(o, false);
		} else if (o instanceof View) {
			return getResourceForView(((View) o).getFlexoResource(), false);
		}
		return null;
	}

}
