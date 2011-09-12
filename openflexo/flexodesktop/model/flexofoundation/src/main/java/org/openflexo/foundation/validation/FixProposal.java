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
package org.openflexo.foundation.validation;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Abstract automatic fix proposal for a validation issue
 *
 * @author sguerin
 *
 */
public abstract class FixProposal<R extends ValidationRule<R,V>, V extends Validable> extends FlexoObject
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FixProposal.class.getPackage().getName());

    private String _message;

    private String _localizedMessage;

    private ProblemIssue<R,V> _issue;

    public FixProposal(String aMessage)
    {
        super();
        _message = aMessage;
    }

    public FixProposal(String aMessage, boolean localizedMessage)
    {
        this(aMessage);
        if (localizedMessage) {
			_localizedMessage = aMessage;
		}
    }

    public String getMessage()
    {
        return _message;
    }

    public String getLocalizedMessage()
    {
        if ((_localizedMessage == null) && (getProblemIssue() != null) && (getObject() != null)) {
            _localizedMessage = FlexoLocalization.localizedForKeyWithParams(_message, this);
        }
        return _localizedMessage;
    }

    public void setMessage(String message)
    {
        this._message = message;
    }

    public V getObject()
    {
        return getProblemIssue().getObject();
    }

    public void apply()
    {
        apply(true);
    }

    public void apply(boolean revalidateAfterFixing)
    {
        fixAction();
        if (revalidateAfterFixing) {
			getProblemIssue().revalidateAfterFixing(false);
		}
    }

    protected abstract void fixAction();

    public void setProblemIssue(ProblemIssue<R,V> issue)
    {
        _issue = issue;
    }

    public ProblemIssue<R,V> getProblemIssue()
    {
        return _issue;
    }

    public boolean askConfirmation()
    {
        return false;
    }

    public FlexoProject getProject()
    {
        if (getProblemIssue() != null) {
            return getProblemIssue().getProject();
        }
        return null;
    }


}
