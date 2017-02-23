package iristk.util;

import iristk.system.Event;
import iristk.system.EventListener;
import iristk.system.IrisSystem;
import iristk.system.KeyListenerModule;

public class KeySenderUtil implements EventListener {
	
	public KeySenderUtil(String address) throws Exception {
		IrisSystem system = new IrisSystem(this.getClass());
		system.connectToBroker("furhat", address);
		system.addModule(new KeyListenerModule());
		system.addEventListener(this);
		system.sendStartSignal();
	}

	public static void main(String[] args) throws Exception {
		new KeySenderUtil(args[0]);
	}

	@Override
	public void onEvent(Event event) {
		if (event.triggers("sense.key**")) {
			System.out.println(event.getName());
		}
	}
	
}
