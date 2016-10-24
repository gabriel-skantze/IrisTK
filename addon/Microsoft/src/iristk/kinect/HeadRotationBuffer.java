package iristk.kinect;

import java.util.*;

import iristk.situated.Body;
import iristk.situated.Rotation;

public class HeadRotationBuffer {
	
	private HashMap<String,Buffer> buffers = new HashMap<>();

	public Rotation add(Body kbody) {
		if (!buffers.containsKey(kbody.id)) {
			buffers.put(kbody.id, new Buffer());
		}
		Buffer buffer = buffers.get(kbody.id);
		return buffer.add(kbody);
	}
	
	private static class Buffer  {

		List<Rotation> buf = new LinkedList<>();
		
		public Rotation add(Body kbody) {
			buf.add(kbody.head.rotation);
			if (buf.size() > 5) {
				buf.remove(0);
			}
			return mean();
		}

		private Rotation mean() {
			int count = 0;
			Rotation sum = new Rotation(0,0,0);
			for (Rotation rot : buf) {
				if (rot != null) {
					sum.x += rot.x;
					sum.y += rot.y;
					sum.z += rot.z;
					count++;
				}
			}
			if (count == 0)
				return null;
			else {
				sum.x = sum.x / count;
				sum.y = sum.y / count;
				sum.z = sum.z / count;
				return sum;
			}
		}
		
	}


	
}
