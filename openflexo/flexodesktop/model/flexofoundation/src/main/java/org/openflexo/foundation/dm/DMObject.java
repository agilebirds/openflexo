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
package org.openflexo.foundation.dm;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.action.DMCopy;
import org.openflexo.foundation.dm.action.DMCut;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.action.DMPaste;
import org.openflexo.foundation.dm.action.ResetSourceCode;
import org.openflexo.foundation.dm.dm.ChildrenReordered;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEOObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.XMLMapping;


/**
 * Abstract representation of an object defined in the data model
 *
 * @author sguerin
 *
 */
public abstract class DMObject extends RepresentableFlexoModelObject implements InspectableObject, DeletableObject, Validable, DataFlexoObserver,
        TreeNode
{

    private static final Logger logger = Logger.getLogger(DMObject.class.getPackage().getName());

    protected static final Vector<DMObject> EMPTY_VECTOR = EmptyVector.EMPTY_VECTOR(DMObject.class);
    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    protected DMModel _dmModel;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public DMObject(DMModel dmModel)
    {
        this(dmModel.getProject());
        _dmModel = dmModel;
    }

    public DMObject(FlexoProject project)
    {
        super(project);
    }

    // ==========================================================================
    // ========================= XML Serialization ============================
    // ==========================================================================

    @Override
    public XMLMapping getXMLMapping()
    {
        return getDMModel().getXMLMapping();
    }

    private DMGenericDeclaration _previousDMGenericDeclaration = null;

    @Override
    public void initializeDeserialization(Object builder)
    {
    	super.initializeDeserialization(builder);
    	if (this instanceof DMGenericDeclaration) {
    		_previousDMGenericDeclaration = getDMModel().getDmTypeConverter().getConverterTypeVariableContext();
    		getDMModel().getDmTypeConverter().setConverterTypeVariableContext((DMGenericDeclaration)this);
    	}
    }

    @Override
    public void finalizeDeserialization(Object builder)
    {
    	super.finalizeDeserialization(builder);
       	if (this instanceof DMGenericDeclaration) {
       		getDMModel().getDmTypeConverter().setConverterTypeVariableContext(_previousDMGenericDeclaration);
    	}
   }

    // ==========================================================================
    // ========================== Instance methods
    // ==============================
    // ==========================================================================

    /**
     * Returns reference to the main object in which this XML-serializable
     * object is contained relating to storing scheme: here it's the data model
     * itself
     *
     * @return the data model this object is relating to
     */
    @Override
    public XMLStorageResourceData getXMLResourceData()
    {
        return getDMModel();
    }

    public DMModel getDMModel()
    {
        return _dmModel;
    }

    @Override
    public FlexoProject getProject()
    {
        if (getDMModel() != null) {
            return getDMModel().getProject();
        }
        return null;
    }

    @Override
	public abstract String getName();

    @Override
	public abstract void setName(String aName) throws InvalidNameException, FlexoException;

    @Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
        Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
        returned.add(ResetSourceCode.actionType);
        returned.add(DMCut.actionType);
        returned.add(DMCopy.actionType);
        returned.add(DMPaste.actionType);
        returned.add(DMDelete.actionType);
        return returned;
    }

    /**
     * used by velocity (dynamic invacation: DON'T delete !)
     *
     * @return Name
     */
    public String getCapitalizedName()
    {
        return getCapitalizedName(getName());
    }

    public static String getCapitalizedName(String aName)
    {
        return aName.substring(0, 1).toUpperCase() + aName.substring(1);
    }

    public String getNameAsMethodArgument()
    {
        return getNameAsMethodArgument(getName());
    }

    public static String getNameAsMethodArgument(String aName)
    {
        String cap = getCapitalizedName(aName);
        if (cap.startsWith("A") || cap.startsWith("E") || cap.startsWith("I") || cap.startsWith("O") || cap.startsWith("U")
                || cap.startsWith("Y")) {
            return "an" + cap;
        }
        return "a" + cap;
    }

    // ==========================================================================
    // ============================ FlexoObserver
    // ===============================
    // ==========================================================================

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        // Not implemented
    }

    // ==========================================================================
    // ======================== Embedding Management
    // ============================
    // ==========================================================================

    private void processToAdditionOfEmbedded(DMObject object, Collection<DMObject> queue)
    {
        if (!queue.contains(object)) {
            queue.add(object);
        }
        if (object.getEmbeddedDMObjects() != null) {
            Enumeration<? extends DMObject> en = object.getEmbeddedDMObjects().elements();
            DMObject candidate = null;
            while (en.hasMoreElements()) {
                candidate = en.nextElement();
                if (!queue.contains(candidate)) {
                    queue.add(candidate);
                    processToAdditionOfEmbedded(candidate, queue);
                }
            }
        }

    }

    /**
     * Return a Vector of all embedded IEObjects: recursive method (Note must
     * include itself in this vector)
     *
     * @return a Vector of IEObject instances
     */
    public Vector<DMObject> getAllEmbeddedDMObjects()
    {
        HashSet<DMObject> returned = new HashSet<DMObject>();
        processToAdditionOfEmbedded(this, returned);
        return new Vector<DMObject>(returned);
    }

    /**
     * Return a Vector of embedded DMObjects at this level. NOTE that this is
     * NOT a recursive method
     *
     * @return a Vector of IEObject instances
     */
    public abstract Vector<? extends DMObject> getEmbeddedDMObjects();

    @Override
    public void setChanged()
    {
    	super.setChanged(propagateModified);
    	if (propagateModified) {
    		if ((!isDeserializing()) && (!isSerializing())) {
    			if (this instanceof DMEOObject) {
    				DMEOModel dmEOModel = ((DMEOObject) this).getDMEOModel();
    				if ((dmEOModel != null) && (dmEOModel.getEOModelResourceData() != null)) {
    					dmEOModel.getEOModelResourceData().setIsModified();
    				}
    			}
    		}
    	}
    }

    private boolean propagateModified = true;

    public void preventFromModifiedPropagation()
    {
    	propagateModified = false;
    }

    public void allowModifiedPropagation()
    {
    	propagateModified = true;
    }

    // ==========================================================================
    // ========================= Validable interface
    // ============================
    // ==========================================================================

    /**
     * Return default validation model for this object
     *
     * @return
     */
    @Override
	public ValidationModel getDefaultValidationModel()
    {
        if (getDMModel() != null) {
            if (getDMModel().getProject() != null) {
                return getDMModel().getProject().getDMValidationModel();
            } else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not access to project !");
            }
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not access to process !");
        }
        return null;
    }

    /**
     * Returns a flag indicating if this object is valid according to default
     * validation model
     *
     * @return boolean
     */
    @Override
	public boolean isValid()
    {
        return isValid(getDefaultValidationModel());
    }

    /**
     * Returns a flag indicating if this object is valid according to specified
     * validation model
     *
     * @return boolean
     */
    @Override
	public boolean isValid(ValidationModel validationModel)
    {
        return validationModel.isValid(this);
    }

    /**
     * Validates this object by building new ValidationReport object Default
     * validation model is used to perform this validation.
     */
    @Override
	public ValidationReport validate()
    {
        return validate(getDefaultValidationModel());
    }

    /**
     * Validates this object by building new ValidationReport object Supplied
     * validation model is used to perform this validation.
     */
    @Override
	public ValidationReport validate(ValidationModel validationModel)
    {
        return validationModel.validate(this);
    }

    /**
     * Validates this object by appending eventual issues to supplied
     * ValidationReport. Default validation model is used to perform this
     * validation.
     *
     * @param report,
     *            a ValidationReport object on which found issues are appened
     */
    @Override
	public void validate(ValidationReport report)
    {
        validate(report, getDefaultValidationModel());
    }

    /**
     * Validates this object by appending eventual issues to supplied
     * ValidationReport. Supplied validation model is used to perform this
     * validation.
     *
     * @param report,
     *            a ValidationReport object on which found issues are appened
     */
    @Override
	public void validate(ValidationReport report, ValidationModel validationModel)
    {
        validationModel.validate(this, report);
    }

    /**
     * Return a vector of all embedded objects on which the validation will be
     * performed
     *
     * @return a Vector of Validable objects
     */
    @Override
	public Vector<Validable> getAllEmbeddedValidableObjects()
    {
        return new Vector<Validable>(getAllEmbeddedDMObjects());
    }

    // ==========================================================================
    // ======================== Deletion implementation
    // =========================
    // ==========================================================================

    /**
     * Build and return a vector of all the objects that will be deleted during
     * this deletion
     *
     * @param aVector
     *            of DeletableObject
     */
    @Override
	public Vector<DMObject> getAllEmbeddedDeleted()
    {
        return getAllEmbeddedDMObjects();
    }

    public abstract boolean isDeletable();

    // ==========================================================================
    // ======================== TreeNode implementation
    // =========================
    // ==========================================================================

    public abstract Vector<? extends DMObject> getOrderedChildren();

    @Override
	public abstract TreeNode getParent();

    @Override
	public abstract boolean getAllowsChildren();

    @Override
	public int getIndex(TreeNode node)
    {
        for (int i = 0; i < getChildCount(); i++) {
            if (node == getChildAt(i)) {
                return i;
            }
        }
        return 0;
    }

    @Override
	public boolean isLeaf()
    {
        return getChildCount() == 0;
    }

    @Override
	public TreeNode getChildAt(int childIndex)
    {
        return getOrderedChildren().get(childIndex);
    }

    @Override
	public int getChildCount()
    {
        return getOrderedChildren().size();
    }

    @Override
	public Enumeration children()
    {
        return getOrderedChildren().elements();
    }

    @Override
    public String toString()
    {
        return getFullyQualifiedName();
    }

    public String getLocalizedName()
    {
        return getName();
    }

    public void notifyReordering(DMObject cause)
    {
        setChanged();
        notifyObservers(new ChildrenReordered(cause));
    }

    public boolean isNameValid()
    {
		return getName() != null && getName().matches(JavaUtils.JAVA_CLASS_NAME_REGEXP);
    }
    
	 public static String getTagAndParamRepresentation(String tag, String param, String tagValue)
	 {
		 if (tagValue == null 
				 || tagValue.trim().equals(""))
			 return "  * @"+tag+" "+param+StringUtils.LINE_SEPARATOR;
		 
		 StringBuffer returned = new StringBuffer();

		 int indentLength = (" @"+tag+" "+param).length();
		 String indent = null;
		 StringTokenizer st2 = new StringTokenizer(tagValue,StringUtils.LINE_SEPARATOR);
		 boolean isFirst = true;
		 while (st2.hasMoreTokens()) {
			 if (isFirst) {
				 returned.append("  * @"+tag+" "+param+" "+st2.nextToken()+StringUtils.LINE_SEPARATOR);
			 }
			 else {
				 if (indent == null) indent = StringUtils.buildWhiteSpaceIndentation(indentLength);
				 returned.append("  *"+indent+" "+st2.nextToken()+StringUtils.LINE_SEPARATOR);
			 }
			 isFirst = false;
		 }
		 return returned.toString();
	 }




    public static class DataModelObjectNameMustBeValid extends ValidationRule
    {

        /**
         * @param objectType
         * @param ruleName
         */
        public DataModelObjectNameMustBeValid()
        {
            super(DMObject.class, "data_model_objects_name_must_be_valid");
        }

        /**
         * Overrides applyValidation
         *
         * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
         */
        @Override
        public ValidationIssue applyValidation(Validable object)
        {
            DMObject o = (DMObject) object;
            if (!o.isNameValid()){
                ValidationError err = new ValidationError(this, o,
                        "data_model_objects_can_contain_only_alphanumeric_characters_or_underscore_and_must_start_with_a_letter");
                if (o.getName() != null)
                    err.addToFixProposals(new CleanDataModelObjectName(ToolBox.cleanStringForJava(o.getName())));
                err.addToFixProposals(new SetCustomName());
                return err;
            }
            return null;
        }

        public static class CleanDataModelObjectName extends FixProposal
        {

            private String newName;

            /**
             * @param aMessage
             */
            public CleanDataModelObjectName(String newName)
            {
            	super("set_name_to_($newName)");
                this.newName = newName;
            }

            /**
             * Overrides fixAction
             *
             * @see org.openflexo.foundation.validation.FixProposal#fixAction()
             */
            @Override
            protected void fixAction()
            {
                int i = 0;
                boolean ok = false;
                String attempt = newName;
                while (!ok) {
                    try {
                    	if(ReservedKeyword.contains(attempt))
                    		throw new InvalidNameException();
                        ((DMObject) getObject()).setName(attempt);
                        ok = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        attempt = newName+"_"+i++;
                        if (i > 100)
                            ok = true;
                    }
                }
            }

			public String getNewName() {
				return newName;
			}

        }

        public static class SetCustomName extends ParameteredFixProposal
        {

            /**
             * @param aMessage
             */
            public SetCustomName()
            {
                super("set_custom_name", "customName", "customName", "");
            }

            /**
             * Overrides fixAction
             *
             * @see org.openflexo.foundation.validation.FixProposal#fixAction()
             */
            @Override
            protected void fixAction()
            {
                String s = (String) getValueForParameter("customName");
                try {
                	if(ReservedKeyword.contains(s))
                		throw new InvalidNameException();
                    ((DMObject) getObject()).setName(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
