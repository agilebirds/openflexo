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
package org.netbeans.lib.cvsclient.response;

import java.util.Date;

import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * This response is very similar to an UpdatedResponse except that it backs up the file being merged, and the file in question will still
 * not be up-to-date after the merge.
 * 
 * @author Robert Greig
 * @see org.netbeans.lib.cvsclient.response.UpdatedResponse
 */
class MergedResponse extends UpdatedResponse {

	/**
	 * Process the data for the response.
	 * 
	 * @param r
	 *            the buffered reader allowing the client to read the server's response. Note that the actual response name has already been
	 *            read and the reader is positioned just before the first argument, if any.
	 * @param services
	 *            various services that are useful to response handlers
	 * @throws ResponseException
	 *             if something goes wrong handling this response
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		super.process(dis, services);
		EventManager manager = services.getEventManager();
		if (manager.isFireEnhancedEventSet()) {
			manager.fireCVSEvent(new EnhancedMessageEvent(this, EnhancedMessageEvent.MERGED_PATH, localFile));
		}
	}

	/**
	 * Returns the Conflict field for the file's entry. Can be overriden by subclasses. (For example the MergedResponse that sets the
	 * "result of merge" there.)
	 * 
	 * @param date
	 *            the date to put in
	 * @param hadConflicts
	 *            if there were conflicts (e.g after merge)
	 * @return the conflict field
	 */
	@Override
	protected String getEntryConflict(Date date, boolean hadConflicts) {
		if (!hadConflicts) {
			return "Result of merge"; // NOI18N
		} else {
			return "Result of merge+" + // NOI18N
					getDateFormatter().format(date);
		}
	}

}
