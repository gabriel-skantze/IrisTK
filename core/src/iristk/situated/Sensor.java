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

import iristk.system.Event;
import iristk.util.Record;

public class Sensor extends Item {

	// Creates a sensor with direction and FOV
	public Sensor(String id, Location location, Rotation rotation) {
		super(id);
		this.location = location;
		this.rotation = rotation;
	}

	// Creates a sensor without location or direction
	public Sensor(String id) {
		this(id, null, null);
	}

	// Creates a sensor without direction
	public Sensor(String id, Location location) {
		this(id, location, null);
	}
	
	public Sensor() {
	}

	public double distance(Agent system, Agent user, Record params) {
		if (location != null) {
			// Pick the user closest to the sensor (if the sensor has a location)
			return location.distance(user.getHeadLocation());
		} else if (system.isOnlyAttending(user.id)) {
			// Pick the user the system is attending
			return 0;
		} else { 
			// Pick the user closest to the system agent
			return system.getHeadLocation().distance(user.getHeadLocation());
		}
	}

	public boolean onEvent(Event event) {
		return false;
	}

	public boolean hasPosition() {
		return true;
	}


}
