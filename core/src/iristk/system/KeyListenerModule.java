package iristk.system;

import iristk.util.NativeKey;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListenerModule extends IrisModule implements NativeKeyListener {
	
	public KeyListenerModule() {
		NativeKey.addNativeKeyListener(this);
	}
	
	@Override
	public void onEvent(Event event) {
	}

	@Override
	public void init() throws InitializationException {
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		String name = NativeKey.getName(e.getKeyCode());
		if (name != null) {
			Event event = new Event("sense.key." + name.toLowerCase());
			send(event);
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}
	
}
