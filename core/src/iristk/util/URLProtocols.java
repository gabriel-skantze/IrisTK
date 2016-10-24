/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

public class URLProtocols {

	private static ConfigurableStreamHandlerFactory streamHandlerFactory;
	
	static class ConfigurableStreamHandlerFactory implements URLStreamHandlerFactory {
	    private final Map<String, URLStreamHandler> protocolHandlers;

	    public ConfigurableStreamHandlerFactory(String protocol, URLStreamHandler urlHandler) {
	        protocolHandlers = new HashMap<String, URLStreamHandler>();
	        addHandler(protocol, urlHandler);
	    }

	    public ConfigurableStreamHandlerFactory() {
	    	protocolHandlers = new HashMap<String, URLStreamHandler>();
		}

		public void addHandler(String protocol, URLStreamHandler urlHandler) {
	        protocolHandlers.put(protocol, urlHandler);
	    }

	    @Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
	    	
	        return protocolHandlers.get(protocol);
	    }
	}
	
	static class Handler extends URLStreamHandler {
		
	    private final ClassLoader classLoader;

	    public Handler() {
	        this.classLoader = getClass().getClassLoader();
	    }

	    public Handler(ClassLoader classLoader) {
	        this.classLoader = classLoader;
	    }

	    @Override
	    protected URLConnection openConnection(URL u) throws IOException {
	        final URL resourceUrl = classLoader.getResource(u.getPath());
	        return resourceUrl.openConnection();
	    }
	}
	
	public static void init() {
		streamHandlerFactory = new ConfigurableStreamHandlerFactory();
		streamHandlerFactory.addHandler("classpath", new Handler());
		URL.setURLStreamHandlerFactory(streamHandlerFactory);
	}
	
	public static void main(String[] args) {
		init();
		try {
			InputStream is = new URL("classpath:iristk/flow/editor/action.speech.png").openStream();
			is.read();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
