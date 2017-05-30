package iristk.situated;

import iristk.system.Event;
import iristk.util.RandomList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sun.media.jfxmedia.logging.Logger;

public class SystemAgent extends Agent {
	/** Maximum number of users the system will interact with.*/
	private int maxUsers = 2;
	
	@RecordField
	public List<Space> interactionSpaces = new ArrayList<Space>();
	
	private HashMap<String,Agent> users = new HashMap<>();
	private Agent currentUser;
	private Situation situation;
	
	private File staticFolder;
	
	private HashMap<String,Item> items = new HashMap<>(); 

	public SystemAgentModule systemAgentModule = null;
		
	public SystemAgent() {
	}
	
	public SystemAgent(String id, Situation situation) {
		super(id);
		this.expire = -1;
		this.situation = situation;
	}
	
	/**
	 * 
	 * @param id - identification of this SystemAgent.
	 * @param situation - as situation model this SystemAgent exists in.
	 * @param staticFolder - folder where user data shall be stored.
	 */
	public SystemAgent(String id, Situation situation, File staticFolder) {
		super(id);
		this.expire = -1;
		this.situation = situation;
		this.staticFolder = staticFolder;
	}
	
	public void setInteractionSpaces(Space... spaces) {
		interactionSpaces.clear();
		for (Space space : spaces) {
			interactionSpaces.add(space);
		}
		if (systemAgentModule != null) { 
			systemAgentModule.sendSenseSituation();
		}
	}

	public void setInteractionDistance(double d) {
		//setInteractionSpaces(new Space.Sphere(getHeadLocation(), d));
		setInteractionSpaces(new Space.Sphere(new Location(0, 0, 0), d));
	}
	
	List<Space> getInteractionSpaces() {
		return interactionSpaces;
	}

	/**
	 * Determines whether a body is in a specific interaction space
	 */
	public boolean isInInteractionSpace(String spaceName, Body body) {
		if (interactionSpaces.size() == 0 || spaceName == null) {
			return true;
		}
		for (Space space : interactionSpaces) {
			if (space.name.equals(spaceName) && space.contains(body.getHeadLocation().subtract(this.getHeadLocation()))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines whether a body is in any interaction space
	 */
	public boolean isInInteractionSpace(Body body) {
		if (interactionSpaces.size() == 0) {
			return true;
		}
		for (Space space : interactionSpaces) {
			if (space.contains(body.getHeadLocation().subtract(this.getHeadLocation()))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return A list with the interaction space names
	 */
	public List<String> getInteractionSpaceNames() {
		List<String> names = new ArrayList<>();
		for (Space space : interactionSpaces) {
			if (!names.contains(space.name)) {
				names.add(space.name);
			}
		}
		return names;
	}
	
	/**
	 * @return A list with the names of the interaction spaces a certain user is in
	 * @param userId The id of the user 
	 */
	public List<String> getInteractionSpaceNames(String userId) {
		List<String> names = new ArrayList<>();
		Agent user = getUser(userId);
		for (Space space : interactionSpaces) {
			if (!names.contains(space.name) && isInInteractionSpace(space.name, user)) {
				names.add(space.name);
			}
		}
		return names;
	}
	
	/**
	 * Verifies if agent exists in SystemAgens users database.
	 * @param agent 
	 * @return true if the agent is in SystemAgent's 'users' database.
	 */
	public boolean isUser(Agent agent) {
		return users.containsKey(agent.id);
	}
	/**
	 * Verifies if agentId matches an agentID in the {@link users} database.
	 * @param agentId String identification of the agent. Usually agent.id
	 * @return true if the agent is in SystemAgent's 'users' database.
	 */
	public boolean isUser(String agentId) {
		return users.containsKey(agentId);
	}
	/**
	 * Checks if current amount of {@link users} exceeds  {@link #maxUsers} 
	 * @return 
	 */
	public boolean hasMaxUsers() {
		return users.size() >= maxUsers;
	}
	
	public int getMaxUsers() {
		return maxUsers;
	}
	
	/**
	 * Determines whether a specific agent fits a selection string
	 * @param selection A space-separated list of selection tags. If there are 
	 * several tags, only one of them must match. If a tag has a + prefix, 
	 * it must match. If a tag has a - prefix, it must not match. 
	 */
	public boolean isSelected(Agent agent, String selection) {
		if (selection == null) {
			return true;
		}
		selection = selection.trim();
		if (selection.isEmpty()) {
			return true;
		}
		boolean oneof = false;
		boolean onehit = false;
		for (String tag : selection.split(" ")) {
			if (tag.isEmpty()) {
				continue;
			}
			if (tag.startsWith("+")) {
				tag = tag.substring(1);
				if (!hasTag(agent, tag)) {
					return false;
				}
			} else if (tag.startsWith("-")) {
				tag = tag.substring(1);
				if (hasTag(agent, tag)) {
					return false;
				}
			} else {
				oneof = true;
				if (hasTag(agent, tag)) {
					onehit = true;
				}
			}
		}
		if (oneof) {
			return onehit;
		} else {
			return true;
		}
	}
	
	/**
	 * Determines whether a specific agent matches a selection tag. 
	 * Each tag can either be the name of an interaction space 
	 * (matches if the agent is in it), or a boolean field of the 
	 * agent (matches if it evaluated to true). 
	 */
	public boolean hasTag(Agent agent, String tag) {
		if (agent.getBoolean(tag)) {
			return true;
		}
		if (isInInteractionSpace(tag, agent)) {
			return true; 
		}
		return false;
	}

	/**
	 * @return The total number of users in any interaction space.
	 */
	public int getNumUsers() {
		return users.size();
	}
	
	/**
	 * @return The total number of users in interaction spaces with a certain name.
	 */
	public int getNumUsers(String selection) {
		int num = 0;
		for (Agent user : users.values()) {
			if (isSelected(user, selection)) {
				num++;
			}
		}
		return num;
	}
	
	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
	}
	
	public Agent getUser(String agentId) {
		if (isUser(agentId)) {
			return users.get(agentId);
		} else {
			return getNobody(); 
		}
	}
	
	public Agent getUser(int i) {
		if (i < users.size()) {
			return new ArrayList<Agent>(users.values()).get(i);
		} else {
			return getNobody();
		}
	}

	public Agent getNobody() {
		Agent agent = new Agent(Agent.NOBODY);
		agent.head = new BodyPart(getAbsolute(new Location(0, -0.14, 1)));
		return agent;
	}
	
	/**
	 * Returns the user that is the source of an event.
	 * If the "user" parameter is provided, it is used directly.
	 * If the "sensor" parameter is provided, it is used to identify the most likely agent through the situation model.
	 */
	public String getUserId(Event event) {
		if (event.get("user") != null) {
			return event.getString("user");
		}
		if (event.get("sensor") == null) {
			return Agent.NOBODY;
		}
		//if the we have a currently selected user, assume they are responsible for the event.
		if (this.getCurrentUser() != null && !isAttendingNobody()) {
			return this.getCurrentUser().id;
		}
		//TODO: change this code. Currently picks user closest to the sensor.
		Sensor sensor = situation.getSensors().get(event.get("sensor"));
		Agent minAgent = null;
		double minDist = Double.MAX_VALUE;
		// Find the user closest to the system agent
		for (Agent user : users.values()) {
			double dist = sensor == null ? 0 : sensor.distance(this, user, event);
			if (dist < minDist) {
				minAgent = user;
				minDist = dist;
			}
		}
		if (minAgent == null) {
			return Agent.NOBODY;
		} else {		
			return minAgent.id;
		}
	}
	
	public Agent getUser(Event event) {
		return getUser(getUserId(event));
	}
	
	public void addUser(Agent user) {
		users.put(user.id, user);
		if (currentUser == null) {
			currentUser = user;
		}
	}

	/** 
	 * Removes the agent from the users HashMap and saves the personal data of the agent.
	 * @param user The user leaving
	 */
	public void removeUser(Agent user) {
		if (user.has("AgentData")) {
			user.agentdata.setLastSeen(); // sets the field lastSeen to the current timestamp
			user.agentdata.save(staticFolder); // saves the user data
		}
		users.remove(user.id);
	}
	
	/**
	 * Removes an agent from the current list of users when the agent leaves.
	 * @param userId The id of the user leaving
	 */
	public void removeUser(String userId) {
		if (users.containsKey(userId)) {
			Agent user = users.get(userId);
			if (user.agentdata != null) {
				user.agentdata.setLastSeen(); // sets the field lastSeen to the current timestamp
				user.agentdata.save(staticFolder); // saves the user data
			}
			users.remove(userId);
		}
	}

	/**
	 * Retrieves another user than the one provided (or the same user if there is only one)
	 * @param the agent id 
	 */
	public Agent getOtherUserThan(String agentId) {
		return getOtherUserThan(agentId, null);
 	}
	
	/**
	 * Retrieves another user than the one provided (or the same user if there is only one)
	 * @param agentId the agent id 
	 * @param selection a selection of users using tags
	 */
	public Agent getOtherUserThan(String agentId, String selection) {
		List<Agent> users = getUsers(selection);
		if (users.size() == 0) {
			return getNobody();
		} else if (users.size() == 1) {
			return users.get(0);
		}
		RandomList.shuffle(users);
		for (Agent user : users) {
			if (!user.id.equals(agentId)) {
				return user;
			}
		}
		return null;
 	}
	
	/**
	 * @return a user that is currently not in focus (or the current user if there is only one)
	 */
	public Agent getOtherUser() {
		return getOtherUserThan(currentUser.id, null);
	}
	
	/**
	 * @return a user that is currently not in focus (or the current user if there is only one)
	 * @param space the space to search in
	 */
	public Agent getOtherUser(String selection) {
		return getOtherUserThan(currentUser.id, selection);
	}
	
	public Agent getOtherUserThan(Event event) {
		return getOtherUserThan(getUserId(event));
	}

	/**
	 * @return whether there are any users interacting with the system agent
	 */
	public boolean hasUsers() {
		return users.size() > 0;
	}

	/**
	 * @return whether there are more than one user interacting with the system agent
	 */
	public boolean hasManyUsers() {
		return users.size() > 1;
	}
	
	/**
	 * @param agent an Agent, or an agent id (String)
	 * @return whether the agent is interacting with the system
	 */
	public boolean hasUser(String agentId) {
		return (users.containsKey(agentId));
	}

	public boolean hasUserAttending() {
		for (Agent agent : getUsers()) {
			if (agent.isAttending(id)) {
				return true;
			}
		}
		return false;
	}
	
	public Agent getAttendedUser() {
		for (Agent agent : getUsers()) {
			if (isOnlyAttending(agent.id)) {
				return agent;
			}
		}
		return null;
	}
	
	public Agent getCurrentUser() {
		if (currentUser != null && isUser(currentUser)) {
			return currentUser;
		} else {
			return getNobody();
		}
	}

	public Agent getRandomUser() {
		if (users.size() == 0) {
			return getNobody();
		}
		return new ArrayList<Agent>(users.values()).get(new Random().nextInt(users.size()));
	}
	
	public Agent getRandomUser(String selection) {
		List<Agent> users = getUsers(selection);
		if (users.size() == 0) {
			return getNobody();
		}
		return new ArrayList<Agent>(users).get(new Random().nextInt(users.size()));
	}
	
	@Override
	public void setAttending(String target) {
		super.setAttending(target);
		if (hasUser(target)) {
			currentUser = getUser(target);
		}
	}
	
	public boolean isAttending(Event event) {
		String userId = getUserId(event);
		return isAgent(event) && isAttending(userId);
	}
	
	public boolean isAgent(Event event) {
		if (event.has("agent")) {
			return event.getString("agent").equals(id);
		} else {
			return true;
		}
	}

	public void putUsers(String key, Object value) {
		for (Agent user : users.values()) {
			user.put(key, value);
		}
	}

	public List<Agent> getUsers() {
		return new ArrayList<Agent>(users.values());
	}

	public List<Agent> getUsers(String selection) {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent user : users.values()) {
			if (isSelected(user, selection)) {
				list.add(user);
			}
		}
		return list;
	}
	
	public Map<String,Item> getItems() {
		return items;
	}
	
	public Item getItem(String id) {
		return items.get(id);
	}
	
	public boolean hasItem(String id) {
		return items.containsKey(id);
	}

	public Situation getSituation() {
		return situation;
	}
	
	@Override
	public boolean isHuman() {
		return false;
	}

	public static String processDisplay(String text, String display) {
		if (display == null) {
			display = text;
		}
		if (display != null) {
			display = display.replaceAll("<.*?>", "");
			display = display.replaceAll("\\s+", " ");
		}
		return display;
	}

	
}
