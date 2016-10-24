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
package iristk.addon.$addon$;

import iristk.system.*;

public class $Addon$Module extends IrisModule {

    public $Addon$Module() {
        // Specify which events to subscribe to. You don't need to do this, 
        // but it can improve performance when distributing the system across processes.  
        // subscribe("action.hello");
    }   
	
	@Override
    public void onEvent(Event event) {
        // We have received an event from some other module, 
        // check if we should react to it
        if (event.getName().equals("action.hello")) {
            // Do the hello here together with the parameter "text"
            System.out.println("Hello: " + event.get("text"));
            // Send a monitor event in response
            Event newEvent = new Event("monitor.hello");
            // Add the parameter text, if anyone should be interested
            newEvent.put("text", event.get("text"));
            send(newEvent);
        }
    }

    @Override
    public void init() throws InitializationException {
        // Initialize the module
    }

}
