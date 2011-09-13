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

/**
 * Convenient class used to represent level of levelled objects
 * 
 * @author sguerin
 */
public abstract class FlexoLevel
{

    public static final FlexoLevel WORKFLOW = new WorkflowLevel();

    public static final FlexoLevel PROCESS = new ProcessLevel();

    public static final FlexoLevel ACTIVITY = new ActivityLevel();

    public static final FlexoLevel OPERATION = new OperationLevel();

    public static final FlexoLevel ACTION = new ActionLevel();
    
    public static final FlexoLevel PORT = new PortLevel();

    static class WorkflowLevel extends FlexoLevel
    {
        @Override
		public String getName()
        {
            return "WORKFLOW";
        }

        @Override
		public FlexoLevel increment()
        {
            return PROCESS;
        }
    }

    static class ProcessLevel extends FlexoLevel
    {
        @Override
		public String getName()
        {
            return "PROCESS";
        }

        @Override
		public FlexoLevel increment()
        {
            return ACTIVITY;
        }
    }

    static class ActivityLevel extends FlexoLevel
    {
        @Override
		public String getName()
        {
            return "ACTIVITY";
        }

        @Override
		public FlexoLevel increment()
        {
            return OPERATION;
        }
    }

    static class OperationLevel extends FlexoLevel
    {
        @Override
		public String getName()
        {
            return "OPERATION";
        }

        @Override
		public FlexoLevel increment()
        {
            return ACTION;
        }
    }

    static class ActionLevel extends FlexoLevel
    {
        @Override
		public String getName()
        {
            return "ACTION";
        }

        @Override
		public FlexoLevel increment()
        {
            return null;
        }
    }
    
    static class PortLevel extends FlexoLevel
    {
    	@Override
		public String getName()
    	{
    		return "PORT";
    	}
    	
    	@Override
		public FlexoLevel increment()
    	{
    		return null;
    	}
    }

    public abstract String getName();

    public abstract FlexoLevel increment();

    @Override
	public String toString()
    {
        return getName();
    }
}
