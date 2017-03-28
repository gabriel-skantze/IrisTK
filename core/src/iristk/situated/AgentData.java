/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Maike Paetzel
 ******************************************************************************/

/**
 * This class saves the personal data of an agent, like the name, age, the time the agent
 * was last seen and the time the agent was first seen in a Record file. The class extends
 * Record, so even fields other than the predefined ones can easily be set and saved for a
 * user
 * 
 * The saving is currently implemented using one properties file per user and saving the file
 * in static/users
 */

package iristk.situated;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import iristk.util.Record;

public class AgentData extends Record {

	@RecordField
	public String firstname;

	@RecordField
	public String surname;

	@RecordField
	public LocalDate birthday;

	@RecordField
	public LocalDateTime knownSince;

	@RecordField
	public LocalDateTime lastSeen;

	@RecordField
	public int agentID;

	@RecordField
	public String affiliation;
	
	@RecordField
	public String slackName;

	public AgentData(String id) {
		super(id);
	}

	public AgentData(int agentID) {
		this.agentID = agentID;
	}

	public AgentData() {
	}

	/**
	 * Returns the full name of the agent with a space in between the first and last name.
	 * 
	 * @return firstname surname
	 */
	public String getFullName() {
		return firstname + " " + surname;

	}

	/**
	 * Returns the first name of the agent.
	 * 
	 * @return first name
	 */
	public String getFirstName() {
		return firstname;
	}

	/**
	 * Sets the first name of the agent.
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstname = firstName;
	}

	/**
	 * Returns the surname of the agent.
	 * 
	 * @return surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Sets the surname of the agent.
	 * 
	 * @param surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * Saves the last LocalDateTime it has seen the agent. 
	 * The value is set automatically if an agent leaves the scene and should not be set somewhere else.
	 */
	public void setLastSeen() {
		this.lastSeen =  LocalDateTime.now();
	}

	/**
	 * LocalDateTime the user was last seen before this instance.
	 * 
	 * @return Returns the LocalDateTime it has last seen the user as a Calendar Object
	 */
	public LocalDateTime getLastSeen() {
		return lastSeen;
	}

	/**
	 * Returns the time difference between it has last seen the user and now in Duration format.
	 * 
	 * @return Duration since it has last seen this user
	 */
	public Duration getTimeSinceLastSeen() {
		return Duration.between(lastSeen, LocalDateTime.now());
	}

	/**
	 * Saves the LocalDateTime it first met a user. 
	 * The value is set automatically the first time the AgentData is created for a new user and should not be set somewhere else.
	 */
	public void setKnownSince() {
		this.knownSince =  LocalDateTime.now();
	}

	/**
	 * LocalDateTime object when the user was first introduced to the system.
	 * 
	 * @return Returns the LocalDateTime when the user first met the system
	 */
	public LocalDateTime getKnownSince() {
		return knownSince;
	}

	/**
	 * Returns the time difference between it has first met the user and now in the Duration format.
	 * 
	 * @return Time since the system has first met this user
	 */
	public Duration getTimeSinceKnown() {
		return Duration.between(knownSince, LocalDateTime.now());
	}

	/**
	 * Set the user's birthday
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setBirthday(int year, int month, int day) {
		this.birthday = LocalDate.of(year, month, day);
	}

	/**
	 * The users brithday.
	 * 
	 * @return LocalDate Object of the user's birthday
	 */
	public LocalDate getBirthday() {
		return birthday;
	}

	/**
	 * Returns the user's current age.
	 * 
	 * @return int age in years
	 */
	public int getAge() {
		return Period.between(birthday, LocalDate.now()).getYears();
	}

	/**
	 * Returns if today is the user's birthday or not.
	 * 
	 * @return True if today is the user's birthday, false otherwise
	 */
	public boolean isTodayBirthday() {
		return birthday.getDayOfMonth() == LocalDate.now().getDayOfMonth() && birthday.getMonth() == LocalDate.now().getMonth();
	}

	/**
	 * Returns the affiliation of the user
	 * @return affiliation
	 */
	public String getAffiliation() {
		return affiliation;
	}

	/**
	 * Sets the affiliation of the user
	 * @param affiliation
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	/**
	 * Saves the AgentData as JSON to the "static/users" folder with the name <id>.json
	 */
	public void save(File staticFolder) {
		File userFile = new File(staticFolder.getAbsolutePath() + "/users/" + agentID + ".json");
		try {
			toJSON(userFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the slack user name of the user
	 * @return slackName
	 */
	public String getSlackName() {
		return slackName;
	}

	/**
	 * Sets the slack user name of the user
	 * @param slackName
	 */
	public void setSlackName(String slackName) {
		this.slackName = slackName;
	}

}
