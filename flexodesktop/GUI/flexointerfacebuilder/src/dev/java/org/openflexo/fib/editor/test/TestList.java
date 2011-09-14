package org.openflexo.fib.editor.test;
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



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.editor.controller.FIBEditorIconLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;


public class TestList {

	private static final Logger logger = Logger.getLogger(TestList.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("TestFIB/TestList.fib");

	public static void main(String[] args)
	{
		final User user1 = new User("John", "Doe", "john.doe@yahoo.com");
		final User user2 = new User("Thomas", "Smith", "thomas.smith@google.com");
		final User user3 = new User("Sarah", "Martins", "smartin@free.com");
		final User user4 = new User("Leonce", "Clercks", "leonceclecks@deef.org");
		final UserList userList = new UserList(user1,user2,user3);
	
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData()
			{
				return FIBAbstractEditor.makeArray(userList);
			}
			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}
		};
		editor.addAction("change_name",new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				user1.setFirstName("Johnny");
			}
		});
		editor.addAction("add_user",new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				userList.addUser(user4);
			}
		});
		editor.addAction("remove_user",new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				userList.removeUser(user4);
			}
		});
		editor.launch();
	}
	
	public static class UserList implements HasPropertyChangeSupport
	{ 
		private final PropertyChangeSupport pcSupport;
		
		public Vector<User> users;

		public UserList(User... someUsers) 
		{
			super();
			pcSupport = new PropertyChangeSupport(this);
			users = new Vector<User>();
			for (User u : someUsers) addUser(u);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() 
		{
			return pcSupport;
		}

		public void addUser(User u)
		{
			users.add(u);
			pcSupport.firePropertyChange("users",null,null);
		}

		public void removeUser(User u)
		{
			users.remove(u);
			pcSupport.firePropertyChange("users",null,null);
		}

		@Override
		public String toString()
		{
			return "UserList[]";
		}
		
		public User createNewUser()
		{
			User returned = new User("firstName","lastName","email");
			addUser(returned);
			return returned;
		}

		public User deleteUser(User u)
		{
			removeUser(u);
			return u;
		}
}

	public static class User implements HasPropertyChangeSupport
	{ 
		private final PropertyChangeSupport pcSupport;
		private String firstName;
		private String lastName;
		private String email;

		public User(String firstName, String lastName, String email)
		{
			super();
			pcSupport = new PropertyChangeSupport(this);
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() 
		{
			return pcSupport;
		}
		
		public String getFirstName()
		{
			return firstName;
		}
		
		public void setFirstName(String firstName)
		{
			String oldFirstName = this.firstName;
			this.firstName = firstName;
			pcSupport.firePropertyChange("firstName",oldFirstName,firstName);
		}
		
		public String getLastName() 
		{
			return lastName;
		}
		
		public void setLastName(String lastName) 
		{
			String oldLastName = this.lastName;
			this.lastName = lastName;
			pcSupport.firePropertyChange("lastName",oldLastName,lastName);
		}
		
		public String getEmail() 
		{
			return email;
		}
		
		public void setEmail(String email) 
		{
			String oldEmail = this.email;
			this.email = email;
			pcSupport.firePropertyChange("email",oldEmail,email);
		}

		public ImageIcon getIcon()
		{
			return FIBEditorIconLibrary.ROLE_ICON;
		}
		
		@Override
		public String toString() 
		{
			return firstName+" "+lastName;
		}
	}

}
