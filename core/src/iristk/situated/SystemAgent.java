package iristk.situated;

import iristk.system.Event;
import iristk.util.RandomList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SystemAgent extends Agent {

	private int maxUsers = 2;
	
	@RecordField
	public List<Space> interactionSpaces = new ArrayList<Space>();
	
	private HashMap<String,Agent> users = new HashMap<>();
	private Agent currentUser;
	private Situation situation;
	
	private HashMap<String,Item> items = new HashMap<>(); 

	public SystemAgentModule systemAgentModule = null;
	
	public SystemAgent() {
	}
	
	public SystemAgent(String id, Situation situation) {
		super(id);
		this.expire = -1;
		this.situation = situation;
	}
	
	public void setInteractionSpaces(Space... spaces) {
		interactionSpaces.clear();
		for (Space space : spaces) {
			interactionSpaces.add(space);
		}
		if (systemAgentModule != null)
			systemAgentModule.sendSenseSituation();
	}

	public void setInteractionDistance(double d) {
		//setInteractionSpaces(new Space.Sphere(getHeadLocation(), d));
		setInteractionSpaces(new Space.Sphere(new Location(0, 0, 0), d));
	}
	
	List<Space> getInteractionSpaces() {
		return interactionSpaces;
	}

	public boolean isInInteractionSpace(Body body) {
		if (interactionSpaces.size() == 0)
			return true;
		for (Space space : interactionSpaces) {
			if (space.contains(body.getHeadLocation().subtract(this.getHeadLocation()))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isUser(Agent agent) {
		return users.containsKey(agent.id);
	}
	
	public boolean isUser(String agentId) {
		return users.containsKey(agentId);
	}

	public boolean hasMaxUsers() {
		return users.size() >= maxUsers;
	}
	
	public int getMaxUsers() {
		return maxUsers;
	}
	
	public int getNumUsers() {
		return users.size();
	}
	
	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
	}
	
	public Agent getUser(String agentId) {
		if (isUser(agentId))
			return users.get(agentId);
		else
			return getNobody();
	}
	
	public Agent getUser(int i) {
		if (i < users.size())
			return new ArrayList<Agent>(users.values()).get(i);
		else
			return getNobody();
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
		if (event.get("user") != null)
			return event.getString("user");
		if (event.get("sensor") == null)
			return Agent.NOBODY;
		Sensor sensor = situation.getSensors().get(event.get("sensor"));
		Agent minAgent = null;
		double minDist = Double.MAX_VALUE;
		for (Agent user : users.values()) {
			double dist = sensor == null ? 0 : sensor.distance(this, user, event);
			if (dist < minDist) {
				minAgent = user;
				minDist = dist;
			}
		}
		if (minAgent == null)
			return Agent.NOBODY;
		else		
			return minAgent.id;
	}
	
	public Agent getUser(Event event) {
		return getUser(getUserId(event));
	}
	
	public void addUser(Agent user) {
		users.put(user.id, user);
		if (currentUser == null)
			currentUser = user;
	}

	public void removeUser(Agent user) {
		users.remove(user.id);
	}
	
	public void removeUser(String userId) {
		users.remove(userId);
	}

	/**
	 * Retrieves another user than the one provided (or the same user if there is only one)
	 * @param the agent id 
	 */
	public Agent getOtherUser(String agentId) {
		if (users.size() == 0)
			return getNobody();
		else if (users.size() == 1)
			return users.values().iterator().next();
		ArrayList<Agent> ulist = new ArrayList<Agent>(users.values());
		RandomList.shuffle(ulist);
		for (Agent user : ulist) {
			if (!user.id.equals(agentId))
				return user;
		}
		return null;
 	}
	
	/**
	 * @return a user that is currently not in focus (or the current user if there is only one)
	 */
	public Agent getOtherUser() {
		if (!hasUsers())
			return getNobody();
		else {
			return getOtherUser(currentUser.id);
		}
	}
	
	public Agent getOtherUser(Event event) {
		return getOtherUser(getUserId(event));
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
			if (agent.isAttending(id))
				return true;
		}
		return false;
	}
	
	public Agent getAttendedUser() {
		for (Agent agent : getUsers()) {
			if (isOnlyAttending(agent.id))
				return agent;
		}
		return null;
	}
	
	public Agent getCurrentUser() {
		if (currentUser != null && isUser(currentUser))
			return currentUser;
		else
			return getNobody();
	}

	public Agent getRandomUser() {
		if (users.size() == 0)
			return getNobody();
		return new ArrayList<Agent>(users.values()).get(new Random().nextInt(users.size()));
	}
	
	@Override
	public void setAttending(String target) {
		super.setAttending(target);
		if (hasUser(target))
			currentUser = getUser(target);
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

	public Location getUsersMiddleLocation() {
		List<Agent> users = getUsers();
		Location[] locations = new Location[users.size()];
		for (int i = 0; i < users.size(); i++) {
			locations[i] = users.get(i).getHeadLocation();
		}
		return Location.mean(locations);
	}

	public static String processDisplay(String text, String display) {
		if (display == null)
			display = text;
		if (display != null) {
			display = display.replaceAll("<.*?>", "");
			display = display.replaceAll("\\s+", " ");
		}
		return display;
	}
	
}
