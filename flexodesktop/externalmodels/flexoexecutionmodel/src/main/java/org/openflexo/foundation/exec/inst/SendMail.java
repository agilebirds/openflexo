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
package org.openflexo.foundation.exec.inst;

import org.openflexo.foundation.wkf.node.EventNode;

public class SendMail extends CustomInstruction {

	private EventNode mailOut;

	public SendMail(EventNode mailOut) {
		super();
		this.mailOut = mailOut;
	}

	@Override
	public String toString() {
		return "[Send email '" + mailOut.getMailSubject() + "' to:" + mailOut.getToAddress() + "]";
	}

	@Override
	public String getJavaStringRepresentation() {
		return "/*\n" + "//TODO put correct sendMailCode\n" + "Mail from : " + mailOut.getFromAddress() + "\n" + "To : "
				+ mailOut.getToAddress() + "\n" + "Subject : " + mailOut.getMailSubject() + "\n" + "Body :\n" + mailOut.getMailBody()
				+ "\n" + "*/";
	}

	@Override
	public SendMail clone() {
		SendMail returned = new SendMail(mailOut);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
