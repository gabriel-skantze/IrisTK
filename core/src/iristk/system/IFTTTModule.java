package iristk.system;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import iristk.util.Record;
import iristk.util.Record.JsonToRecordException;
import iristk.util.Utils;

public class IFTTTModule extends IrisModule {

	private static org.slf4j.Logger logger = IrisUtils.getLogger(IFTTTModule.class);
	
	private String key = null;

	private Handler webHandler;

	public IFTTTModule() {
	}
	
	public IFTTTModule(String key) {
		setKey(key);
	}
	
	public void setKey(String key) {
		this.key  = key;
	}
	
	@Override
	public void onEvent(Event event) {
		if (event.triggers("action.ifttt**")) {
			if (event.has("key")) {
				setKey(event.getString("key"));
			} 
			if (event.triggers("action.ifttt.**")) {
				sendIFTTT(event.getName().replace("action.ifttt.", ""), event.getEventParams());
			} else if (event.has("name")) {
				sendIFTTT(event.getString("name"), event.getEventParams());
			}
		}
	}

	private void sendIFTTT(String command, Record params) {
		if (key == null) {
			logger.error("No key provided");
			return;
		}
		try {
			StringBuilder json = new StringBuilder();
			json.append("{");
			for (String param : params.getFields()) {
				if (json.length() > 1)
					json.append(",");
				json.append("\"" + param + "\":\"" + params.getString(param) + "\"");
			}
			json.append("}");
			byte[] postData = json.toString().getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			String request = "https://maker.ifttt.com/trigger/" + command + "/with/key/" + key;
			URL url = new URL(request);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			conn.setUseCaches(false);
			try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write(postData);
			}
			Utils.copyStream(conn.getInputStream(), System.out);
		} catch (Exception e) {
			logger.error(e.getClass().getName(), e);
		}
	}

	public Handler getWebHandler() {
		if (webHandler == null)
			webHandler = new WebHandler();
		return webHandler;
	}
	
	private class WebHandler extends AbstractHandler {

		@Override
		public void handle(String target,
				Request baseRequest,
				HttpServletRequest request,
				HttpServletResponse response) throws IOException, ServletException {
			if (target.startsWith("/ifttt/")) {
				baseRequest.setHandled(true);
				String name = target.replace("/ifttt/", "");
				String json = Utils.readString(request.getInputStream()).trim();
				Event event = new Event("sense.ifttt." + name);
				if (json.length() > 0) {
					try {
						event.putAll(Record.fromJSON(json));
					} catch (JsonToRecordException e) {
						logger.error("Cannot read JSON " + json);
					}
				}
				send(event);
			}
		}
		
	}
	
	public void startWebServer() {
		Server server = new Server(80);
		server.setHandler(getWebHandler());
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() throws InitializationException {
	}

	public static void main(String[] args) throws Exception {
		IrisSystem system = new IrisSystem(IFTTTModule.class);
		IFTTTModule ifttt = new IFTTTModule("bIm_uv5zT_JFYiwHTqOaZo");
		ifttt.startWebServer();
		system.addModule(ifttt);
		system.sendStartSignal();
		//Event event = new Event("action.ifttt.email");
		//event.put("value1", "Testing");
		//system.send(event);
	}
	
}
