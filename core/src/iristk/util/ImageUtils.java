package iristk.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;

public class ImageUtils {

	public static byte[] imageToJpeg(BufferedImage image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			imageWriter.setOutput(ios);
			imageWriter.write(image);
		    ios.close();
		    imageWriter.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}   
		return os.toByteArray();
	}
	
	public static void imageToJpeg(BufferedImage image, File jpegFile) {
		try {
			JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(jpegFile));
			imageWriter.setOutput(ios);
			imageWriter.write(image);
		    ios.close();
		    imageWriter.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}
	
	public static BufferedImage cropAndScaleImage(BufferedImage image, int cropX, int cropY, int cropW, int cropH, int scaleW, int scaleH) {
		BufferedImage bimage = new BufferedImage(scaleW, scaleH, image.getType());
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(image, 0, 0, scaleW-1, scaleH-1, cropX, cropY, cropX+cropW-1, cropY+cropH-1, null); 
		bGr.dispose();
		return bimage;
	}
	
}
