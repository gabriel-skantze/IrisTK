/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.situated;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.Timer;

import iristk.system.Event;
import iristk.util.Record;

public class Situation {

	private int bodyIdCount = 1;

	private final List<SituationListener> listeners = new ArrayList<>();
	private final HashMap<String,Sensor> sensors = new HashMap<>();
	
	private final HashMap<String,Item> items = new HashMap<>();

	private final HashMap<String,Body> bodies = new HashMap<>();

	//private HashMap<String,Record> persistentUsers = new HashMap<>();
	
	private int defaultExpire = 2000;

	private final long startTime = System.currentTimeMillis();

	private boolean updated = false;

	private HashMap<String,SystemAgent> systemAgents = new HashMap<>();

	//private File persistentUsersFile;
	private Timer timer;

	private List<String> bodyIds = new ArrayList<String>();
	
	public Situation() {
		timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanBodies();
			}
		});
		timer.start();
	}

	private int currentTime() {
		return (int) (System.currentTimeMillis() - startTime);
	}

	public HashMap<String,Sensor> getSensors() {
		return sensors;
	}
	
	public HashMap<String,Body> getBodies() {
		return bodies;
	}
	
	public List<String> getBodyIds() {
		return bodyIds;
	}
	
	public HashMap<String,Item> getItems() {
		return items;
	}

	private void senseSituation(Event event) {
		//System.out.println(event);
		for (String id : event.getFields()) {
			if (sensors.get(id) != null) {
				Record sensorInfo = event.getRecord(id);
				if (sensorInfo.getInteger("expire", -1) == 0) {
					sensors.remove(id);
				} else {
					sensors.get(id).putAllExceptNull(sensorInfo);
				}
			} else if (systemAgents.get(id) != null) {
				systemAgents.get(id).putAllExceptNull(event.getRecord(id));
			} else {
				Record record = event.getRecord(id);
				if (record instanceof Sensor && ((Sensor)record).id != null) {
					sensors.put(id, (Sensor)record.deepClone());
				} else if (record instanceof SystemAgent && ((SystemAgent)record).id != null) {
					SystemAgent clone = (SystemAgent)record.deepClone();
					systemAgents.put(id, clone);
				}
			}
		}
		updated();
	}
	
	//public Body getBodyBySensorId(String sensorId, String bodyId) {
	//	return sensorBodies.get(sensorId + "-" + bodyId);
	//}
	
	//public Body getBody(String id) {
	//	if (id == null)
	//		return null;
	//	else
	//		return userBodies.get(id);
	//}

	private void senseItem(Event event) {
		String sensorId = event.getString("sensor");
		Sensor sensor = sensors.get(sensorId);
		for (String itemId : event.getRecord("items").getFields()) {
			Item item = (Item) event.getRecord("items:" + itemId).deepClone();
			int expire = item.getInteger("expire", -1);
			if (expire == 0) {
				items.remove(itemId);
			} else {
				//System.out.println(item.id + " " + item.location);
				//System.out.println(sensor.id + " " + sensor.location);
				item.location = locate(sensor, item.location);
				if (sensor != null)
					item.rotation = item.rotation.add(sensor.rotation);
				items.put(itemId, item);
			}
		}
		updated();
	}
	
	private void senseBody(Record bodyevent) {
		String sensorId = bodyevent.getString("sensor");
		Sensor sensor = sensors.get(sensorId);
		
		// Look for updates to old bodies
		for (String bodyId : bodyevent.getRecord("bodies").getFields()) {
						
			Body eventBody = (Body) bodyevent.getRecord("bodies:" + bodyId);
			
			Body oldBody = bodies.get(bodyId);
			
			if (oldBody != null) {
				int expire = eventBody.getInteger("expire", defaultExpire);
				if (expire == 0) {
					removeBody(oldBody.id);
				} else {
					fillBody(oldBody, eventBody, sensor);
				}
			} else {
				int expire = eventBody.getInteger("expire", defaultExpire);
				if (expire != 0) {
					Body newBody  = new Body(bodyId);
					fillBody(newBody, eventBody, sensor);
					bodies.put(newBody.id, newBody);
					if (!bodyIds.contains(newBody.id))
						bodyIds.add(newBody.id);
				}
			}
		}
		
		updated();
		
	}

	private void senseSpeech(Event event) {
		boolean speaking = event.triggers("sense.user.speech.start");
		try {
			getSystemAgents().get(event.getString("agent")).getUser(event.getString("user")).speaking = speaking;
			updated();
		} catch (NullPointerException e) {
		}
	}
	
	private void monitorSpeech(Event event) {
		String agentId = event.getString("agent", "system");
		SystemAgent agent = getSystemAgents().get(agentId);
		if (agent != null) {
			if (event.triggers("monitor.speech.start")) {
				agent.speaking = true;
			} else {
				agent.speaking = false;
			}
			updated();
		}
	}
	
	private void fillBody(Body body, Body newInfo, Sensor sensor) {
		
		int expire = newInfo.getInteger("expire", defaultExpire);
				
		if (expire < 0) {
			body.expire = expire;
		} else {
			body.expire = currentTime() + expire;
		}
		
		body.head = new BodyPart(locate(sensor, newInfo.head.location));
		if (newInfo.has("handLeft:location")) 
			body.handLeft = new BodyPart(locate(sensor, newInfo.handLeft.location));
		else 
			body.handLeft = null;
		if (newInfo.has("handRight:location"))
			body.handRight = new BodyPart(locate(sensor, newInfo.handRight.location));
		else
			body.handRight = null;
				
		if (newInfo.gaze != null) {
			body.gaze = locate(sensor, newInfo.gaze);
		} else {
			body.gaze = null;
		}
		
		if (newInfo.getRecord("head:rotation") != null) {
			Rotation rot = new Rotation(newInfo.head.rotation);
			if (sensor != null) {
				rot = rot.add(new Rotation(-sensor.rotation.x, sensor.rotation.y + 180, sensor.rotation.z));
			}
			body.head.rotation = rot;
			//agent.attending = Agent.NOBODY;
		} else {
			body.head.rotation = null;
			//agent.attending = Agent.UNKNOWN;
		}
		
		/*
		if (agent.head.rotation != null) {
			double minangle = 20;	
			for (String agentId : new ArrayList<String>(agents.keySet())) {
				if (!agentId.equals(agent.id)) {
					double angle = agent.gazeAngle(agents.get(agentId).head.location);
					//if (agentId.equals("system"))
					//	System.out.println(angle);
					if (angle < minangle) {
						agent.attending = agentId;
						minangle = angle;
					}
				}
			}
			//TODO: should really check for items also
			//TODO: maybe we should compute the distance to all agents and items (at least system)
			//for (String itemId : new ArrayList<String>(items.getFields())) {
			//	double angle = agent.gazeAngle(((Item)items.get(itemId)).location);
			//	if (angle < minangle) {
			//		agent.attending = itemId;
			//		minangle = angle;
			//	}
			//}
		}
		*/

		body.priority = newInfo.priority;
		body.proximity = newInfo.proximity;
		
		if (newInfo.has("recId"))
			body.recId = newInfo.getString("recId");
		
		if (newInfo.has("extra"))
			body.putAllExceptNull(newInfo.getRecord("extra"));
		
		//if (persistentUsersFile != null)
		//	restorePersistentUserData(agent);
	}

	public Location locate(Sensor sensor, Location location) {
		if (sensor != null && sensor.rotation != null) {
			location = location.rotate(sensor.rotation);
		}
		if (sensor != null && sensor.location != null) {
			location = location.add(sensor.location);
		}
		return location;
	}
	
	public Location locate(String sensorId, Location location) {
		Sensor sensor = sensors.get(sensorId);
		if (sensor == null) {
			System.err.println("Could not find sensor " + sensorId);
			return location;
		} else {
			return locate(sensor, location);
		}
	}

	private void monitorGaze(Event event) {
		String agentId = event.getString("agent", "system");
		SystemAgent agent = systemAgents.get(agentId);
		if (agent != null) {
			agent.gaze = ((Location)event.get("location")).rotate(agent.rotation).add(agent.getHeadLocation());
			if (event.has("head:rotation")) {
				agent.head.rotation = (Rotation) event.get("head:rotation");
			}
			updated();
		}
	}
	
	private void monitorAttend(Event event) {
		String agentId = event.getString("agent", "system");
		SystemAgent system = systemAgents.get(agentId);
		if (system != null) {
			system.setAttending(event.getString("target"));
			updated();
		}
	}
	
	public void addSituationListener(SituationListener listener) {
		listeners.add(listener);
	}
	
	private synchronized void cleanBodies() {
		for (String agentId : new ArrayList<String>(bodies.keySet())) {
			Body agent = bodies.get(agentId);
			if (agent.expire >= 0 && agent.expire <= currentTime()) {
				//System.out.println("Cleaning body");
				removeBody(agentId);
				notifyUpdated();
			}
		}
	}

	private void removeBody(String bodyId) {
		//System.out.println("Removing body");
		Body agent = bodies.remove(bodyId);
		bodyIds.remove(bodyId);
		if (agent != null) {
			/*
			for (String systemAgentId : agents.keySet()) {
				if (agents.get(systemAgentId) instanceof SystemAgent) {
					SystemAgent systemAgent = (SystemAgent) agents.get(systemAgentId);
					if (systemAgent.isInInteractionSpace(agent)) {
						leaveAgent(systemAgent, agent);
					}
				}
			}
			*/
			updated();
		}
	}
	
	/*
	public synchronized void addAgent(Agent agent) {
		agent.situation = this;
		agents.put(agent.id, agent);
		updated();
	}
	*/

	private void updated() {
		updated = true;
	}

	public synchronized void onEvent(Event event) {
		updated = false;
		if (event.getName().equals("sense.body")) {
			senseBody(event);
		} else if (event.getName().equals("sense.item")) {
			senseItem(event);
		} else if (event.getName().equals("monitor.gaze")) {
			monitorGaze(event);
		} else if (event.getName().equals("sense.situation")) {
			senseSituation(event);
		} else if (event.getName().equals("monitor.attend")) {
			monitorAttend(event);
		} else if (event.getName().equals("monitor.speech.start")) {
			monitorSpeech(event);
		} else if (event.getName().equals("monitor.speech.end")) {
			monitorSpeech(event);
		} else if (event.triggers("sense.user.enter")) {
			if (event.getBoolean("new")) {
				String systemId = event.getString("agent");
				SystemAgent system = systemAgents.get(systemId);
				Agent user = new Agent(event.getString("user"));
				user.head.location = (Location) event.getRecord("head:location");
				user.head.rotation = (Rotation) event.getRecord("head:rotation");
				system.addUser(user);
				updated();
			}
		} else if (event.triggers("sense.user.leave")) {
			if (event.getBoolean("lost")) {
				String systemId = event.getString("agent");
				SystemAgent system = systemAgents.get(systemId);
				system.removeUser(event.getString("user"));
				updated();
			}
		} else if (event.triggers("sense.user.attend")) {
			String systemId = event.getString("agent");
			SystemAgent system = systemAgents.get(systemId);
			Agent user = system.getUser(event.getString("user"));
			user.attending = event.getString("target");
			updated();
		} else if (event.triggers("sense.user.move")) {
			String systemId = event.getString("agent");
			if (systemAgents.containsKey(systemId)) {
				SystemAgent system = systemAgents.get(systemId);
				for (Agent user : system.getUsers()) {
					if (event.has(user.id)) {
						Body body = (Body) event.getRecord(user.id);
						user.head.location = body.head.location;
						user.head.rotation = body.head.rotation;
					}
				}
				updated();
			}
		} else if (event.triggers("sense.user.speech.*")) {
			senseSpeech(event);
		}
		for (Sensor sensor : sensors.values()) {
			if (sensor.onEvent(event)) {
				updated = true;
			}
		}
		if (updated) {
			notifyUpdated();
		}
	}

	private void notifyUpdated() {
		for (SituationListener listener : listeners) {
			listener.situationUpdated();
		}
	}

	public boolean hasItem(String id) {
		return items.containsKey(id);
	}
	
	public Item getItem(String id) {
		return items.get(id);
	}
	
	/*
	public static void addSensor(IrisSystem system,	Sensor sensor) {
		Event event = new Event("sense.sensor");
		event.put("sensors:" + sensor.id, sensor);
		system.send(event, "system");
	}
	*/

	public Map<String,SystemAgent> getSystemAgents() {
		return systemAgents;
	}

	public void savePositions(File file) {
		try {
			Record save = new Record();
			//save.put("sensors", new Record(situation.getSensors()));
			
			for (String sensorId : getSensors().keySet()) {
				Sensor sensor = getSensors().get(sensorId);
				if (sensor.hasPosition())
					save.put(sensorId, sensor.getPosition());
			}
			
			for (SystemAgent agent : getSystemAgents().values()) {
				save.put(agent.id, agent.getPosition());
			}
			save.toProperties().store(new FileOutputStream(file), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	public void setPersistentUsers(File usersFile) {
		this.persistentUsersFile = usersFile;
		if (usersFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(usersFile));
				String line;
				while ((line = br.readLine()) != null) {
					Record userData = Record.fromJSON(line);
					persistentUsers.put(userData.getString("recId"), userData);
				}
			} catch (JsonToRecordException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void restorePersistentUserData(Agent agent) {
		if (agent.recId != null && persistentUsers.containsKey(agent.recId) && !agent.has("restored")) {
			Record data = persistentUsers.get(agent.recId);
			agent.putAll(data);
			agent.put("restored", true);
		}
	}
	
	private void savePersistentUserData(Agent agent) {
		if (persistentUsersFile != null && agent.recId != null) {
			Record data = new Record();
			Agent test = new Agent("test");
			Set<String> ignoreFields = test.getFields();
			for (String field : agent.getFields()) {
				if (!ignoreFields.contains(field) || field.equals("recId")) {
					data.put(field, agent.get(field));
				}
			}
			persistentUsers.put(agent.recId, data);
			try {
				PrintWriter pw = new PrintWriter(persistentUsersFile);
				for (Record rec : persistentUsers.values()) {
					pw.println(rec.toJSON());
				}
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	*/

}
