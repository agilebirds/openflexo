package org.openflexo.fib.sampleData;

import javax.swing.Icon;

import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.icon.UtilsIconLibrary;

public class Person extends SampleData {

	private String firstName;
	private String lastName;
	private int age;
	private Gender gender;

	public Person(String firstName, String lastName, int age, Gender gender) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName + " aged " + age + " (" + gender + ")";
	}

	public Icon getIcon() {
		return UtilsIconLibrary.OK_ICON;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		String oldFirstName = this.firstName;
		this.firstName = firstName;
		System.out.println("Notify firstName changed from " + oldFirstName + " to " + firstName);
		getPropertyChangeSupport().firePropertyChange("firstName", oldFirstName, firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		String oldLastName = this.lastName;
		this.lastName = lastName;
		getPropertyChangeSupport().firePropertyChange("lastName", oldLastName, lastName);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		Gender oldGender = this.gender;
		this.gender = gender;
		getPropertyChangeSupport().firePropertyChange("gender", oldGender, gender);
	}
}