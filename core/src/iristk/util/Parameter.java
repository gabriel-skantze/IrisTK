package iristk.util;

public class Parameter<T> {

	T value;
	
	public Parameter(T value) {
		this.value = value;
	}

	public void set(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
}
