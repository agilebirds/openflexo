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
package org.openflexo.dg.rm;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.wkf.OperationChange;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class OperationLatexFileResource extends LatexFileResource<DGLatexGenerator<OperationNode>> implements FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(OperationLatexFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public OperationLatexFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public OperationLatexFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getOperation() == null)
			return super.getName();
		registerObserverWhenRequired();
		if (super.getName() == null)
			setName(nameForRepositoryAndOperation(getCGFile().getRepository(), getOperation()));
		return nameForRepositoryAndOperation(getCGFile().getRepository(), getOperation());
	}

	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getOperation() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE))
				logger.fine("*** addObserver " + getFileName() + " for " + getProject());
			getOperation().addObserver(this);
			getOperation().getAbstractActivityNode().addObserver(this);
			getOperation().getProcess().addObserver(this);
		}
	}

	public static String nameForRepositoryAndOperation(GenerationRepository repository, OperationNode operation) {
		return repository.getName() + ".OPERATION_LATEX." + operation.getName() + "_" + operation.getFlexoID();
	}

	public OperationNode getOperation() {
		if (getGenerator() != null)
			return getGenerator().getObject();
		return null;
	}

	@Override
	protected LatexFile createGeneratedResourceData() {
		return new LatexFile(getFile(), this);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getOperation() != null)
			addToDependantResources(getOperation().getProcess().getFlexoResource());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (getOperation() == null)
			return;
		if (dataModification instanceof OperationChange)
			return;
		if (observable == getOperation() || observable == getOperation().getAbstractActivityNode()
				|| observable == getOperation().getProcess()) {
			if (dataModification instanceof AttributeDataModification) {
				if (((AttributeDataModification) dataModification).getAttributeName().equals("name")) {
					logger.info("Building new resource after " + observable + " renaming");
					DGLatexGenerator<OperationNode> generator = getGenerator();
					setGenerator(null);
					getCGFile().setMarkedForDeletion(true);
					generator.refreshConcernedResources();
					generator.getRepository().refresh();
					observable.deleteObserver(this);
					isObserverRegistered = false;
				}
			}
			if (dataModification instanceof NodeRemoved) {
				logger.info("Handle operation has been deleted");
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				if (getCGFile().getRepository() != null)
					getCGFile().getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
		ensureFileNameIsUpToDate();
	}

	/**
     *
     */
	private void ensureFileNameIsUpToDate() {
		if (getOperation() != null && getOperation().getAbstractActivityNode() != null && getOperation().getProcess() != null
				&& !getFile().getName().equals(DGLatexGenerator.nameForOperation(getOperation(), getGenerator().getRepository()))) {
			if (logger.isLoggable(Level.INFO))
				logger.info("Renaming file from " + getFileName() + " to "
						+ DGLatexGenerator.nameForOperation(getOperation(), getGenerator().getRepository()));
			try {
				renameFileTo(DGLatexGenerator.nameForOperation(getOperation(), getGenerator().getRepository()));
			} catch (InvalidFileNameException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof FlexoProcessResource) {
			if (getOperation() != null) {
				if (!requestDate.before(getOperation().getLastUpdate())) {
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for OPERATION " + getOperation().getName());
					return false;
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

}
