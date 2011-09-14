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
package org.openflexo.dm.view.popups;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;


import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.tabular.model.AbstractColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.components.widget.MultipleObjectSelector.TabularBrowserConfiguration;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMSet;
import org.openflexo.foundation.dm.LoadableDMEntity;
import org.openflexo.foundation.dm.DMSet.PackageReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.MethodReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.PropertyReference;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgress;

public class UpdateClassesPopup extends MultipleObjectSelectorPopup {

    static final Logger logger = Logger.getLogger(UpdateClassesPopup.class.getPackage().getName());

    public UpdateClassesPopup(Vector entities, FlexoProject project, DMController controller, FlexoProgress progress)
    {
        super(FlexoLocalization.localizedForKey("update_classes"),
        		FlexoLocalization.localizedForKey("please_select_properties_and_methods_to_update"),
                FlexoLocalization.localizedForKey("update_classes_description"),
                new UpdateClassesPopupBrowserConfiguration(entities,project,progress),project,controller.getFlexoFrame(),controller.getEditor());
        choicePanel.setSelectedObjects(getDMSet().getSelectedObjects());
        for (Enumeration en=entities.elements(); en.hasMoreElements();) {
            LoadableDMEntity next = (LoadableDMEntity)en.nextElement();
            
            if(next.getType()!=null && next.getJavaType()!=null){ //next.getType() is null when the class cannot be loaded (A ClassNotFoundException has been thrown)
            	ClassReference classReference = getDMSet().getClassReference(next.getJavaType());
            	if (classReference.isSelected()) {
            		choicePanel.getTreeTable().getProjectBrowser().expand(classReference,true);
            		choicePanel.addToSelected(classReference);
            	}
            }
         }
     }
    
    public DMSet getDMSet()
    {
        return ((UpdateClassesPopupBrowserConfiguration)getBrowserConfiguration()).dmSet;
    }

    protected static class UpdateClassesPopupBrowserConfiguration implements TabularBrowserConfiguration 
    {
        protected DMSet dmSet;
        private UpdateClassesBrowserElementFactory _factory;
        private FlexoProject _project;
        
        protected UpdateClassesPopupBrowserConfiguration(Vector entities, FlexoProject project,FlexoProgress progress)
        {
            _project = project;
            dmSet = new DMSet(project,"updated_classes",entities,true,progress);
              _factory = new UpdateClassesBrowserElementFactory();
        }
        
        @Override
		public FlexoProject getProject() 
        {
            return _project;
        }

        @Override
		public void configure(ProjectBrowser browser) 
        {
        }

        @Override
		public FlexoModelObject getDefaultRootObject()
        {
            return dmSet;
        }

        @Override
		public BrowserElementFactory getBrowserElementFactory()
        {
            return _factory; 
        }

         class UpdateClassesBrowserElementFactory implements BrowserElementFactory
        {
            @Override
			public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent)
            {
                if (object instanceof DMSet) {
                    return new ClassSetElement((DMSet)object,browser,parent);
                }
                else if (object instanceof PackageReference) {
                    return new PackageReferenceElement((PackageReference)object,browser,parent);
                }
                else if (object instanceof ClassReference) {
                    return new ClassReferenceElement((ClassReference)object,browser,parent);
                }
                else if (object instanceof PropertyReference) {
                    return new PropertyReferenceElement((PropertyReference)object,browser,parent);
                }
                else if (object instanceof MethodReference) {
                    return new MethodReferenceElement((MethodReference)object,browser,parent);
                }
                return null;
            }
            
            private class ClassSetElement extends BrowserElement
            {
                public ClassSetElement(DMSet classSet, ProjectBrowser browser, BrowserElement parent)
                {
                    super(classSet, BrowserElementType.DM_REPOSITORY, browser, parent);
                }
                
                @Override
				protected void buildChildrenVector()
                {
                     for (Enumeration e = ((DMSet)getObject()).getPackages(); e.hasMoreElements();) {
                        PackageReference next = (PackageReference) e.nextElement();
                        addToChilds(next);                                
                    }
                }
                
                @Override
				public String getName()
                { 
                    return ((DMSet)getObject()).getName();
                }
           }
            
            private class PackageReferenceElement extends BrowserElement
            {
                public PackageReferenceElement(PackageReference aPackage, ProjectBrowser browser, BrowserElement parent)
                {
                    super(aPackage, BrowserElementType.DM_PACKAGE, browser, parent);
                }
                
                @Override
				protected void buildChildrenVector()
                {
                    for (Enumeration e = ((PackageReference)getObject()).getClassesEnumeration(); e.hasMoreElements();) {
                        ClassReference next = (ClassReference) e.nextElement();
                        addToChilds(next);                                
                    }
               }
                
                @Override
				public String getName()
                { 
                    return ((PackageReference)getObject()).getName();
                }
           }
            
            private class ClassReferenceElement extends BrowserElement
            {
                public ClassReferenceElement(ClassReference aClass, ProjectBrowser browser, BrowserElement parent)
                {
                    super(aClass, BrowserElementType.DM_ENTITY, browser, parent);
                }
                
                @Override
				protected void buildChildrenVector()
                {
                    for (Enumeration e = ((ClassReference)getObject()).getPropertiesEnumeration(); e.hasMoreElements();) {
                        PropertyReference next = (PropertyReference) e.nextElement();
                        addToChilds(next);                                
                    }
                    for (Enumeration e = ((ClassReference)getObject()).getMethodsEnumeration(); e.hasMoreElements();) {
                        MethodReference next = (MethodReference) e.nextElement();
                        addToChilds(next);                                
                    }
                }
                
                @Override
				public String getName()
                { 
                    return ((ClassReference)getObject()).getName();
                }

                
            }
           
            private class PropertyReferenceElement extends BrowserElement
            {
                public PropertyReferenceElement(PropertyReference aProperty, ProjectBrowser browser, BrowserElement parent)
                {
                    super(aProperty, BrowserElementType.DM_PROPERTY, browser, parent);
                }
                
                @Override
				protected void buildChildrenVector()
                {
                  }
                
                @Override
				public String getName()
                { 
                    return ((PropertyReference)getObject()).getName();
                }

                
            }
           
            private class MethodReferenceElement extends BrowserElement
            {
                public MethodReferenceElement(MethodReference aMethod, ProjectBrowser browser, BrowserElement parent)
                {
                    super(aMethod, BrowserElementType.DM_METHOD, browser, parent);
                }
                
                @Override
				protected void buildChildrenVector()
                {
                  }
                
                @Override
				public String getName()
                { 
                    return ((MethodReference)getObject()).getSimplifiedSignature();
                }

                
            }
          
        }

         IconColumn<FlexoModelObject> getSetColumn;
         StringColumn<FlexoModelObject> cardinalityColumn;
         StringColumn<FlexoModelObject> typeColumn;

         @Override
		public AbstractColumn getExtraColumnAt(int index) 
         {
        	 if (index == 0) {
        		 if (getSetColumn == null) {
        			 getSetColumn = new IconColumn<FlexoModelObject>("settable", 25) {
        				 @Override
						public Icon getIcon(FlexoModelObject object)
        				 {
        					if (object instanceof PropertyReference) {
        						if (((PropertyReference)object).isSettable())
        							return DMEIconLibrary.GET_SET_ICON;
        						else return DMEIconLibrary.GET_ICON;
        					}
           					return null;
        				 }
        				 
        				 @Override
         				public String getLocalizedTooltip(FlexoModelObject object) {
         					 if (object instanceof PropertyReference) {
          						if (((PropertyReference)object).isSettable())
          							return FlexoLocalization.localizedForKey("can_be_set");
          						else
          							return FlexoLocalization.localizedForKey("cannot_be_set");
          					}
             				return null;
         				}
        			 };
        		 }
        		 return getSetColumn;
        	 }
        	 else if (index == 1) {
        		 if (cardinalityColumn == null) {
        			 cardinalityColumn = new StringColumn<FlexoModelObject>("cardinality", 60) {
        				 @Override
						public String getValue(FlexoModelObject object)
        				 {
        					if (object instanceof PropertyReference) {
        						return ((PropertyReference)object).getCardinality().getLocalizedName();
        					}
        					return "";
        				 }
        			 };
        		 }
        		 return cardinalityColumn;
        	 }
        	 else if (index == 2) {
        		 if (typeColumn == null) {
        			 typeColumn = new StringColumn<FlexoModelObject>("type", 150) {
        				 @Override
						public String getValue(FlexoModelObject object)
        				 {
         					if (object instanceof PropertyReference) {
         						return ((PropertyReference)object).getSimplifiedTypeName();
         					}
         					else if (object instanceof MethodReference) {
         						return ((MethodReference)object).getSimplifiedReturnTypeName();
         					}
         					return "";
        				 }
        			 };
        		 }
        		 return typeColumn;
        	 }
        	 return null;
         }

		@Override
		public int getExtraColumnCount()
		{
			return 3;
		}

		@Override
		public int getBrowsingColumnWidth() 
		{
			return 300;
		}

		@Override
		public boolean isSelectable(FlexoModelObject obj) 
		{
			return true;
		}

    }
    
    @Override
	public void performConfirm()
    {
        super.performConfirm();
         getDMSet().setSelectedObjects(choicePanel.getSelectedObjects());
    }


}
