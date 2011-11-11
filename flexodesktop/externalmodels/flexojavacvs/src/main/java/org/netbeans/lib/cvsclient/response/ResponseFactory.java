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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Create response objects appropriate for handling different types of response
 * 
 * @author Robert Greig
 */
public class ResponseFactory {

	private final Map responseInstancesMap;
	private String previousResponse = null;

	public ResponseFactory() {
		responseInstancesMap = new HashMap();
		responseInstancesMap.put("E", new ErrorMessageResponse()); // NOI18N
		responseInstancesMap.put("M", new MessageResponse()); // NOI18N
		responseInstancesMap.put("Mbinary", new MessageBinaryResponse()); // NOI18N
		responseInstancesMap.put("MT", new MessageTaggedResponse()); // NOI18N
		responseInstancesMap.put("Updated", new UpdatedResponse()); // NOI18N
		responseInstancesMap.put("Update-existing", new UpdatedResponse()); // NOI18N
		responseInstancesMap.put("Created", new CreatedResponse()); // NOI18N
		responseInstancesMap.put("Rcs-diff", new RcsDiffResponse()); // NOI18N
		responseInstancesMap.put("Checked-in", new CheckedInResponse()); // NOI18N
		responseInstancesMap.put("New-entry", new NewEntryResponse()); // NOI18N
		responseInstancesMap.put("ok", new OKResponse()); // NOI18N
		responseInstancesMap.put("error", new ErrorResponse()); // NOI18N
		responseInstancesMap.put("Set-static-directory", new SetStaticDirectoryResponse()); // NOI18N
		responseInstancesMap.put("Clear-static-directory", new ClearStaticDirectoryResponse()); // NOI18N
		responseInstancesMap.put("Set-sticky", new SetStickyResponse()); // NOI18N
		responseInstancesMap.put("Clear-sticky", new ClearStickyResponse()); // NOI18N
		responseInstancesMap.put("Valid-requests", new ValidRequestsResponse()); // NOI18N
		responseInstancesMap.put("Merged", new MergedResponse()); // NOI18N
		responseInstancesMap.put("Notified", new NotifiedResponse()); // NOI18N
		responseInstancesMap.put("Removed", new RemovedResponse()); // NOI18N
		responseInstancesMap.put("Remove-entry", new RemoveEntryResponse()); // NOI18N
		responseInstancesMap.put("Copy-file", new CopyFileResponse()); // NOI18N
		responseInstancesMap.put("Mod-time", new ModTimeResponse()); // NOI18N
		responseInstancesMap.put("Template", new TemplateResponse()); // NOI18N
		responseInstancesMap.put("Module-expansion", new ModuleExpansionResponse()); // NOI18N
		responseInstancesMap.put("Wrapper-rcsOption", new WrapperSendResponse()); // NOI18N

	}

	public Response createResponse(String responseName) {
		Response response = (Response) responseInstancesMap.get(responseName);
		if (response != null) {
			previousResponse = responseName;
			return response;
		}
		if (previousResponse != null && previousResponse.equals("M")) { // NOI18N
			return new MessageResponse(responseName);
		}
		previousResponse = null;
		IllegalArgumentException2 ex = new IllegalArgumentException2("Unhandled response: " + // NOI18N
				responseName + "."); // NOI18N

		// assemble reasonable localized message

		String cvsServer = System.getenv("CVS_SERVER"); // NOI18N
		if (cvsServer == null) {
			cvsServer = ""; // NOI18N
		} else {
			cvsServer = "=" + cvsServer; // NOI18N
		}

		String cvsExe = System.getenv("CVS_EXE"); // NOI18N
		if (cvsExe == null) {
			cvsExe = ""; // NOI18N
		} else {
			cvsExe = "=" + cvsExe; // NOI18N
		}

		ResourceBundle bundle = ResourceBundle.getBundle("org.netbeans.lib.cvsclient.response.Bundle"); // NOI18N
		String msg = bundle.getString("BK0001");
		msg = MessageFormat.format(msg, new Object[] { responseName, cvsServer, cvsExe });
		ex.setLocalizedMessage(msg);
		throw ex;
	}

	private static class IllegalArgumentException2 extends IllegalArgumentException {

		private String localizedMessage;

		public IllegalArgumentException2(String s) {
			super(s);
		}

		@Override
		public String getLocalizedMessage() {
			return localizedMessage;
		}

		private void setLocalizedMessage(String localizedMessage) {
			this.localizedMessage = localizedMessage;
		}

	}
}
