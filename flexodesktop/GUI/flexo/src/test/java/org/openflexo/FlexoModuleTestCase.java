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
package org.openflexo;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.view.controller.InteractiveFlexoEditor;


public abstract class FlexoModuleTestCase extends FlexoTestCase {

	public FlexoModuleTestCase(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void assertNoObservers(FlexoObservable observable){
		assertEquals("The following observers remain : "+printObservers(observable.getAllObservers()), 0, observable.getAllObservers().size());
	}
	private static String printObservers(Vector allObservers) {
		StringBuffer reply = new StringBuffer();
		Enumeration<Object> en = allObservers.elements();
		while(en.hasMoreElements()){
			reply.append(en.nextElement().toString());
			if(en.hasMoreElements())reply.append(",");
		}
		return reply.toString();
	}
	protected static final FlexoEditorFactory INTERACTIVE_EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public InteractiveFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new InteractiveFlexoTestEditor(project);
		}
    };
    
    
    
    public static class InteractiveFlexoTestEditor extends InteractiveFlexoEditor {
		public InteractiveFlexoTestEditor(FlexoProject project) {
			super(project);
		}
		
		@Override
        public boolean isTestEditor(){
    		return true;
    	}
//		@Override
//		public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionInitializer<? super A> getInitializerFor(FlexoActionType<A, T1, T2> actionType) {
//			FlexoActionInitializer<A> init = new FlexoActionInitializer<A>() {
//
//				@Override
//				public boolean run(ActionEvent event, A action) {
//					boolean reply = action.getActionType().isEnabled(action.getFocusedObject(), action.getGlobalSelection(),InteractiveFlexoTestEditor.this);
//					if(!reply){
//						System.err.println("ACTION NOT ENABLED :"+action.getClass()+" on object "+(action.getFocusedObject()!=null?action.getFocusedObject().getClass():"null focused object"));
//					}
//					return reply;
//				}
//
//			};
//			return init;
//		}

		@Override
		public boolean isAutoSaveEnabledByDefault() {
			return true;
		}
	}
    
}
