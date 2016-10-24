package iristk.kinect;

import iristk.util.Utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import sun.misc.Unsafe;

public class CameraImage extends BufferedImage {
	
	public static final int FORMAT_YUV = 0;
    public static final int FORMAT_BGRA = 1;
	
	private static Unsafe unsafe = Utils.getUnsafe();
	public int[] intPixels;
	public byte[] bytePixels;
	
	public CameraImage(int width, int height) {
		this(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	public CameraImage(int width, int height, int type) {
		super(width, height, type);
		if (type == BufferedImage.TYPE_3BYTE_BGR)
			bytePixels = ((DataBufferByte) getRaster().getDataBuffer()).getData();
		else if (type == BufferedImage.TYPE_INT_RGB)
			intPixels = ((DataBufferInt) getRaster().getDataBuffer()).getData();
		else if (type == BufferedImage.TYPE_BYTE_GRAY)
			bytePixels = ((DataBufferByte) getRaster().getDataBuffer()).getData();
	}
	
	/*
	public void update(byte[] frame, int format) {
		if (format == FORMAT_YUV) {
			for (int i = 0; i < pixels.length; i++) {
				int v = frame[i*2 + 1];
				pixels[i] = v * 65536 + v * 256 + v;
			}
		} else if (format == FORMAT_BGRA) {
			int p = 0;
			for (int i = 0; i < pixels.length; i++) {
				int v = frame[p+2] * 65536 + frame[p+1] * 256 + frame[p];
				pixels[i] = v;
				p += 4;
			}
		}
	}
	*/

	public void update(long ptr, int owidth, int oheight, int format) {
		int width = getWidth();
		int height = getHeight();
		if (owidth < width)	width = owidth;
		if (oheight < height) height = oheight;

		if (format == FORMAT_YUV) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int p = ((x * owidth) / width + ((y * oheight) / height) * owidth) * 2 + 1;
					byte v = unsafe.getByte(ptr + p);
					encode(x, y, v, v, v); 
				}
			}
		} else if (format == FORMAT_BGRA) {
			float wf = (float)owidth / (float)width;
			for (int y = 0; y < height; y++) {
				long p1 = ptr + ((y * oheight) / height) * owidth * 4;
				for (int x = 0; x < width; x++) {
					long p = p1 + (int)(x * wf) * 4;
					encode(x, y, unsafe.getByte(p), unsafe.getByte(p+1), unsafe.getByte(p+2));
				}
			}
		}
		
	}
	
	private void encode(int x, int y, byte b, byte g, byte r) {
		if (getType() == BufferedImage.TYPE_3BYTE_BGR) {
			int p = (y * getWidth() + x) * 3 ;
			bytePixels[p++] = b;
			bytePixels[p++] = g;
			bytePixels[p] = r;
		} else if (getType() == BufferedImage.TYPE_INT_RGB) {
			int v = r * 65536 + g * 256 + b;
			intPixels[y * getWidth() + x] = v;
		} else if (getType() == BufferedImage.TYPE_BYTE_GRAY) {
			bytePixels[y * getWidth() + x] = r;
		}
	}

}
