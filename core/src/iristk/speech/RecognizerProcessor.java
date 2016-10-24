package iristk.speech;

/**
 * RecognizerProcessor acts like RecognizerListener, but has to actively pass on all events (through getNext()). 
 * This way, it may intercept, modify and generate new events.  
 */
public abstract class RecognizerProcessor implements RecognizerListener {

	private RecognizerListener next = null;

	void setNext(RecognizerListener listener) {
		this.next = listener;
	}
	
	/**
	 * Returns the next listener in the pipeline. Note that it may return null, and that you must check for this.
	 */
	protected RecognizerListener getNext() {
		return next;
	}

}
