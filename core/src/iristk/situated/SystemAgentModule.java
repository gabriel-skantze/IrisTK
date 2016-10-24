package iristk.situated;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.util.ListMap;
import iristk.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SystemAgentModule extends SituationModule implements SituationListener {

	private SystemAgent systemAgent;
	private ListMap<String,Agent> bodyUserMap = new ListMap<>();
	private int userIdCount = 0;

	public SystemAgentModule(String id) {
		this.systemAgent = new SystemAgent(id, situation);
		situation.getSystemAgents().put(id, systemAgent);
		situation.addSituationListener(this);
	}

	@Override
	public void init() throws InitializationException {
		systemAgent.systemAgentModule = this;
		super.init();
	}

	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (event.triggers("action.situation.detect")) {
			sendSenseSituation();
		}
	}
	
	@Override
	public String getDefaultName() {
		return "agent-" + systemAgent.id;
	}

	void sendSenseSituation() {
		Event event = new Event("sense.situation");
		event.put(systemAgent.id, systemAgent);
		send(event);
	}

	private class BodyCluster extends ArrayList<String> implements Comparable {

		public Agent user = null;

		public boolean isClose(String id2) {
			Body body2 = situation.getBodies().get(id2);
			for (String id1 : this) {
				Body body1 = situation.getBodies().get(id1);
				if (body1.isCloseTo(body2)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int compareTo(Object obj) {
			double diff = this.getMinDistance(systemAgent.getHeadLocation()) - 
					((BodyCluster) obj).getMinDistance(systemAgent.getHeadLocation());
			if (diff < 0) return -1;
			else if (diff > 0) return 1;
			else return 0;
		}

		private double getMinDistance(Location location) {
			double min = Double.MAX_VALUE;
			for (String id1 : this) {
				Body body1 = situation.getBodies().get(id1);
				min = Math.min(min, body1.getHeadLocation().distance(location));
			}
			return min;
		}

		public boolean isCloseTo(Agent user) {
			for (String id : this) {
				if (situation.getBodies().get(id).isCloseTo(user))
					return true;
			}
			return false;
		}

	}

	private List<BodyCluster> getBodyClusters() {
		ArrayList<String> bodies = new ArrayList<>();
		// Collect all bodies that are in interaction space
		for (String bodyId : new ArrayList<>(situation.getBodies().keySet())) {
			Body body = situation.getBodies().get(bodyId);
			if (!bodyId.equals(systemAgent.id) && systemAgent.isInInteractionSpace(body)) {
				bodies.add(bodyId);
			}
		}
		ArrayList<String> bodies2 = new ArrayList<>(bodies);
		ArrayList<BodyCluster> clusters = new ArrayList<>();
		// Cluster bodies
		for (String id1 : bodies) {
			if (!bodies2.contains(id1))
				continue;
			BodyCluster bc = new BodyCluster();
			bc.add(id1);
			bodies2.remove(id1);
			for (String id2 : new ArrayList<>(bodies2)) {
				if (bc.isClose(id2) && !sameSensor(id1, id2)) {
					INSERT: {
					for (int i = 0; i < bc.size(); i++) {
						if (situation.getBodies().get(id2).priority > situation.getBodies().get(bc.get(i)).priority) {
							bc.add(i, id2);
							break INSERT;
						}
					}
					bc.add(id2);
				}
				bodies2.remove(id2);
				}
			}
			clusters.add(bc);
		}
		// Sort the clusters by proximity to the system agent
		Collections.sort(clusters);
		// Remove clusters we are not interested in
		while (clusters.size() > systemAgent.getMaxUsers())
			clusters.remove(clusters.size() - 1);
		return clusters;
	}

	private static boolean sameSensor(String id1, String id2) {
		return id1.substring(0, id1.indexOf("-")).equals(id2.substring(0, id2.indexOf("-")));
	}

	private void fillUser(Agent user, BodyCluster cluster) {
		user.head.location = null;
		user.head.rotation = null;
		user.gaze = null;
		for (String id : cluster) {
			Body body = situation.getBodies().get(id);
			if (body.head.location != null && user.head.location == null) {
				user.head.location = body.head.location;
			}
			if (body.head.rotation != null && user.head.rotation == null) {
				user.head.rotation = body.head.rotation;
			}
			if (body.gaze != null && user.gaze == null) {
				user.gaze = body.gaze;
			}
		}
	}

	@Override
	public void situationUpdated() {
		ArrayList<Agent> moved = new ArrayList<>();
		List<BodyCluster> clusters = getBodyClusters();
		ArrayList<String> mappedUserIds = new ArrayList<>();
		// Map clusters to previously seen user agents
		for (String bodyId : situation.getBodyIds()) {
			if (bodyUserMap.containsKey(bodyId)) {
				for (Agent user : bodyUserMap.get(bodyId)) {
					for (BodyCluster cluster : clusters) {
						if (!mappedUserIds.contains(user.id) && cluster.contains(bodyId) && cluster.user == null) {
							//System.out.println("A: " + user.id + " to " + cluster);
							cluster.user = user;
							mappedUserIds.add(user.id);
						}
					}
				}
			}
		}
		// Check unmapped clusters and see if there are any user agents nearby
		for (BodyCluster cluster : clusters) {
			if (cluster.user == null) {
				for (Agent user : systemAgent.getUsers()) {
					if (cluster.isCloseTo(user) && !mappedUserIds.contains(user.id)) {
						cluster.user = user;
						//System.out.println("B: " + user.id + " to " + cluster);
						mappedUserIds.add(user.id);
					}
				}
			}
		}
		ArrayList<String> leavingUsers = new ArrayList<>();
		for (Agent a : systemAgent.getUsers())
			leavingUsers.add(a.id);
		
		for (BodyCluster cluster : clusters) {
			Agent oldUser = cluster.user;
			if (oldUser == null || !systemAgent.hasUser(oldUser.id)) {
				// New user (or old user re-entering)
				Agent user = oldUser != null ? oldUser : new Agent(systemAgent.id + "-user-" + userIdCount++);
				for (String id : cluster) {
					bodyUserMap.addUnique(id, user);
				}
				fillUser(user, cluster);
				systemAgent.addUser(user);
				Event event = new Event("sense.user.enter");
				event.put("user", user.id);
				event.put("agent", systemAgent.id);
				event.put("head:location", user.head.location.clone());
				if (user.head.rotation != null)
					event.put("head:rotation", user.head.rotation.clone());
				send(event);
			} else {
				// Updated user
				leavingUsers.remove(oldUser.id);
				for (String id : cluster) {
					bodyUserMap.addUnique(id, oldUser);
				}
				Agent clone = (Agent) oldUser.deepClone();
				fillUser(oldUser, cluster);
				if (!Utils.equals(oldUser.head.location, clone.head.location) || !Utils.equals(oldUser.head.rotation, clone.head.rotation)) {
					moved.add(oldUser);
				}
				String oldAttending = oldUser.attending;
				oldUser.attending = Agent.UNKNOWN;
				if (oldUser.head.rotation != null) {
					double minangle = 20;	
					List<Agent> targets = systemAgent.getUsers();
					targets.remove(oldUser);
					targets.add(systemAgent);
					for (Agent target : targets) {
						double angle = oldUser.gazeAngle(target.getHeadLocation());
						if (angle < minangle) {
							oldUser.attending = target.id;
							minangle = angle;
						}
					}
				}
				if (!oldUser.attending.equals(oldAttending)) {
					Event event = new Event("sense.user.attend");
					event.put("user", oldUser.id);
					event.put("target", oldUser.attending);
					event.put("agent", systemAgent.id);
					send(event);
				}

			}
		}
		for (String userId : leavingUsers) {
			systemAgent.removeUser(userId);
			Event event = new Event("sense.user.leave");
			event.put("user", userId);
			event.put("agent", systemAgent.id);
			send(event);
			//System.out.println("Leave: " + userId);
		}
		if (moved.size() > 0) {
			Event event = new Event("sense.user.move");
			for (Agent user : moved) {
				//Agent agent = new Agent(user.id);
				//agent.head.location = user.head.location;
				//agent.head.rotation = user.head.rotation;
				//agent.body = user.body;
				event.put(user.id, user.deepClone());
			}
			event.put("agent", systemAgent.id);
			send(event);
		}
		Event moveEvent = new Event("sense.item.move");
		String prominent = null;
		double maxdist = 0;
		for (Item item : situation.getItems().values()) {
			Item oldItem = systemAgent.getItems().get(item.id);
			if (oldItem == null || !oldItem.getPosition().equals(item.getPosition())) {
				double dist = (oldItem == null ? 1 : oldItem.location.distance(item.location));
				moveEvent.put("items:" + item.id, dist);
				if (dist > maxdist) {
					maxdist = dist;
					prominent = item.id;
				}
				if (oldItem == null)
					systemAgent.getItems().put(item.id, (Item) item.deepClone());
				else
					systemAgent.getItems().get(item.id).putAllExceptNull(item);
			} 
		}
		for (String itemId : new ArrayList<String>(systemAgent.getItems().keySet())) {
			if (situation.getItem(itemId) == null)
				systemAgent.getItems().remove(itemId);
		}
		if (moveEvent.has("items")) {
			moveEvent.put("prominent", prominent);
			send(moveEvent);
		}
	}

	public SystemAgent getSystemAgent() {
		return systemAgent;
	}

}
