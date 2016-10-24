package iristk.util;

import iristk.system.IrisUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class NativeKey {
	
	private static boolean initialized = false;
	
	private static final HashMap<Integer, String> VC_FIELDS = new HashMap<Integer, String>(); 
	
	static {
		Field[] fields = NativeKeyEvent.class.getFields();
	      for (Field f : fields) {
	         if (f.getName().startsWith("VC_") && f.getType()==Integer.TYPE) {
	            try {
	               VC_FIELDS.put(f.getInt(null), f.getName().substring(3));
	            } catch (IllegalAccessException ex) {
	            }
	         }
	      }
	}
	
	public synchronized static void addNativeKeyListener(NativeKeyListener listener) {
		if (!initialized) {
			initialized = true;
			try {
				IrisUtils.addCoreLibPath();
				Locale.setDefault(new Locale("en", "us"));
				Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
				logger.setLevel(Level.WARNING);
	            GlobalScreen.registerNativeHook();
		    } catch (NativeHookException ex) {
	            System.err.println("There was a problem registering the native hook.");
	            System.err.println(ex.getMessage());
		    }
		}
        GlobalScreen.addNativeKeyListener(listener);
	}

	public static String getName(int keyCode) {
		return VC_FIELDS.get(keyCode);
	}
	
	private static class Test implements NativeKeyListener {

		@Override
		public void nativeKeyPressed(NativeKeyEvent arg0) {
			System.out.println(arg0.getKeyCode() + " " + getName(arg0.getKeyCode()));
		}

		@Override
		public void nativeKeyReleased(NativeKeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void nativeKeyTyped(NativeKeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		NativeKey.addNativeKeyListener(new Test());
		while (1==1)
			Thread.sleep(1000);
	}

}
