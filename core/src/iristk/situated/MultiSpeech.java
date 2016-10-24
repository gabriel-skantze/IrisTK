package iristk.situated;

import java.util.*;

import iristk.system.Event;
import iristk.util.Record;

public class MultiSpeech {

	private Map<String,Boolean> attsys = new HashMap<>();
	private Map<String,Event> events = new HashMap<>();
	private List<String> started = new ArrayList<>();
	
	public int speakers = 0;
	public int runningRecognizers = 0;
	private SystemAgent systemAgent;
	
	public MultiSpeech(SystemAgent systemAgent) {
		this.systemAgent = systemAgent;
	}

	public void addStart(String agentId, boolean isAttSys) {
		speakers++;
		runningRecognizers++;
		attsys.put(agentId, isAttSys);
		started.add(agentId);
	}

	public void attendingSystem(String agentId) {
		attsys.put(agentId, true);
	}

	public boolean someAttendingSystem() {
		for (boolean is : attsys.values())
			if (is) return true;
		return false;
	}
	
	public boolean hasStarted(String userId) {
		return started.contains(userId);
	}

	private List<String> getAttendingSpeakers() {
		List<String> result = new ArrayList<>();
		for (String userId : events.keySet()) {
			if (attsys.containsKey(userId) && attsys.get(userId)) 
				result.add(userId);
 		}
		return result;
	}
	
	public void addRec(String agentId, Event event) {
		if (!event.triggers("sense.speech.rec.silence")) 
			runningRecognizers--;
		if (event.triggers("sense.speech.rec"))
			events.put(agentId, event);
	}

	public Event getEvent() {
		if (events.size() == 0) {
			return new Event("sense.user.silence");
		}
		Agent attendedUser = systemAgent.getAttendedUser();
		if (attendedUser != null) {
			// System attending one user
			if (events.size() == 1) {
				// Only one answered
				if (events.containsKey(attendedUser.id)) {
					// The one attended answered
					return singleUserEvent(attendedUser.id, false);
				} else {
					// The one not attended answered
					return singleUserEvent(events.keySet().iterator().next(), true);
				}
			} else {
				// Multiple answered
				if (!events.containsKey(attendedUser.id)) {
					// The attended user did not answer
					List<String> attending = getAttendingSpeakers();
					String speaker = attending.size() > 0 ? attending.get(0) : events.keySet().iterator().next();
					return singleUserEvent(speaker, true);
				} else {
					return singleUserEvent(attendedUser.id, false);
				}
			}
		} else {
			// System attending all
			if (events.size() == 1) {
				// Only one user replied
				return singleUserEvent(events.keySet().iterator().next(), false);
			} else {
				// Multiple user replied
				List<String> attending = getAttendingSpeakers();
				if (attending.size() == 1) {
					// Only one of them attended the system, return that one
					return singleUserEvent(attending.get(0), false);
				} else if (attending.size() > 1) {
					// Many of them attended the system, return these
					return multiUserEvent(attending);
				} else {
					// None of them attended the system, return all of them
					return multiUserEvent(new ArrayList<String>(events.keySet()));
				}
			}
		}
	}
	
	private Event singleUserEvent(String userId, boolean side) {
		Event e;
		if (side)
			e = new Event("sense.user.speak.side");
		else
			e = new Event("sense.user.speak");
		e.copyParams(events.get(userId));
		e.put("agent", systemAgent.id);
		e.put("user", userId);
		e.put("attsys", attsys.get(userId));
		if (side) 
			e.put("side", true);
		return e;
	}
	
	private Event multiUserEvent(List<String> userIds) {
		Event e = new Event("sense.user.speak.multi");
		e.put("agent", systemAgent.id);
		List multi = new ArrayList();
		// Merge the different users into one representation
		for (String userId : started) {
			if (userIds.contains(userId)) {
				e.put("user", userId);
				e.put("attsys", attsys.get(userId));
				Object sem = events.get(userId).get("sem");
				if (sem instanceof Record) {
					if (e.get("sem") instanceof Record) {
						e.getRecord("sem").adjoin((Record)sem);
					} else {
						e.put("sem", sem);
					}
				}
				Record user = new Record();
				user.put("attsys", attsys.get(userId));
				user.put("user", userId);
				user.putAllExceptNull(events.get(userId).getEventParams());
				multi.add(user);
			}
			//e.putAllExceptNull(events.get(userId).getEventParams());
		}
		// But also store all users under the field "multi"
		e.put("all", multi);
		return e;
	}
	
}
