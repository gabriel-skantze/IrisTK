package iristk.system;

/**
 * 
 *An <code>EventFilter</code> filters events sent through the Furhat system. It is to be expected that nearly all events will pass through an <code>EventFilter</code>,
 * which filter certain events by modifying their behaviour through the Furhat system in some way. For example, an <code>EventFilter</code> might give Furhat a new behaviour by editing
 * all action.speech events. Or an <code>EventFilter</code> may turn one kind of event into another. EventFilters added from a Skill will be removed when Furhat switches to another Skill.
 *
 */
public interface EventFilter {
	
	/**
	 * Returns an <code>Event</code> based on the input <code>Event</code>.
	 * <p> The exact implementation of filter will differ depending on each <code>EventFilter</code>,
	 * yet most filters will edit an return certain events and return the rest unedited.
	 * @param event - current <code>Event</code> to be filtered
 	 * @return a filtered <code>Event</code> 
	 */
	Event filter(Event event);
	
}
