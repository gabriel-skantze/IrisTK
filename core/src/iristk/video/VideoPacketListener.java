package iristk.video;

import com.xuggle.xuggler.IPacket;

public interface VideoPacketListener {

	void newVideoPacket(IPacket packet, int width, int height);


}
