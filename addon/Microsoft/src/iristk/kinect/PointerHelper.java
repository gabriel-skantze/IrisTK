package iristk.kinect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.sun.jna.Pointer;

public class PointerHelper {

	public static Pointer getPointer(long addr) {
		try {
			Constructor<Pointer> constr = Pointer.class.getConstructor(long.class);
			constr.setAccessible(true);
			return constr.newInstance(addr);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
