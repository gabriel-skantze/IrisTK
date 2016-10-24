package iristk.video;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import iristk.kinect.KinectV1;
import iristk.kinect.OldKinectVideoRecorder;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class TestWebServer extends AbstractHandler {

	private OldKinectVideoRecorder videoRecorder;

	public TestWebServer() throws Exception {
		KinectV1 kinect = new KinectV1();
    	videoRecorder = new OldKinectVideoRecorder(kinect);
		Server server = new Server(8080);
		server.setHandler(this);
		server.start();
		server.join();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("video/mp4");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    	videoRecorder.startEncoding(response.getOutputStream());
	}
	
	public static void main(String[] args) throws Exception {
		new TestWebServer();
	}
	
}
