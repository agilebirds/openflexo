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
package org.openflexo.foundation.exec;

/**
 * this_method_represents_all_code_to_be_executed_in_the_context_of_process_TestExecutionModel_execution
 */
public class TestExecutionModelProcessor {

	public Object getProcessInstance()
	{
		return null;
	}

	public Object getActivityTask()
	{
		return null;
	}

	public Object getOperationTask()
	{
		return null;
	}

	public void createOperationTask(int i, Object processInstance) {
		// TODO Auto-generated method stub
	}

	public void createActivityTask(int i, Object processInstance) {
		// TODO Auto-generated method stub	
	}

	public void deleteActivityTask(int i, Object processInstance) {
		// TODO Auto-generated method stub
	}

	public void destroyRemainingTokensForActivity(Object activityTask) {
		// TODO Auto-generated method stub
	}

	public void deleteOperationTask(int i, Object processInstance) {
		// TODO Auto-generated method stub
	}

	public void destroyRemainingTokensForOperation(Object operationTask) {
		// TODO Auto-generated method stub
	}


	public void storeTokenOnPrecondition(int i, Object processInstance) {
		// TODO Auto-generated method stub

	}

	public int getNumberOfTokensStoredOnPrecondition(int i, Object processInstance) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void destroyRemainingTokensForOperator(int i, Object processInstance) {
		// TODO Auto-generated method stub
	}

	public boolean hasWaitingTokensOnOperator(int i, int j, Object processInstance) {
		// TODO Auto-generated method stub
		return false;
	}

	public void storeTokenOnOperator(int i, int j, Object processInstance) {
		// TODO Auto-generated method stub
	}


}
