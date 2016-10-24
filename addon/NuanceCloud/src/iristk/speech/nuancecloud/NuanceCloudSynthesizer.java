/*
 * TTSHTTPClient.java
 *
 * This is a simple command-line java app that shows how to use the NMDP HTTP Client Interface for
 *	Text-to-Speech (TTS) requests using the POST method
 *
 * This basic java app will:
 *	1. Create an instance of an HttpClient to interact with our HTTP Client Interface for TTS
 *	2. Use some simple helper methods to setup the URI and HTTP POST parameters
 *	3. Execute the HTTP Request
 *	4. Process the HTTP Response, writing the generated audio to file
 *
 *	Output of progress of the request is logged to console
 *	Values to be passed to the HTTP Client Interface are simply hard-coded class members for demo purposes
 *
 * @copyright  Copyright (c) 2010 Nuance Communications, inc. (http://www.nuance.com)
 *
 * @Created	: April 28, 2011
 * @Author	: Peter Freshman
 */
package iristk.speech.nuancecloud;

import iristk.audio.Sound;
import iristk.speech.Synthesizer;
import iristk.speech.SynthesizerEngine;
import iristk.speech.Transcription;
import iristk.speech.Voice;
import iristk.speech.Voice.Gender;
import iristk.speech.VoiceList;
import iristk.system.InitializationException;
import iristk.util.Language;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sound.sampled.AudioFormat;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;


public class NuanceCloudSynthesizer implements Synthesizer {

	private String DEVICE_ID = "0000";
	private String CODEC = "audio/x-wav;codec=pcm;bit=16;rate=16000";	
	private String APP_ID;
	private String APP_KEY;
	private static short PORT = (short) 443;
	private static String HOSTNAME = "tts.nuancemobility.net";
	private static String TTS = "/NMDPTTSCmdServlet/tts";

	private HttpClient httpclient = null;
	private AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
	private VoiceList voices = new VoiceList();
	
	public NuanceCloudSynthesizer() throws Exception {
		Properties properties = new Properties();
		properties.load(new FileReader(NuanceCloudPackage.PACKAGE.getPath("license.properties")));
		this.APP_ID = properties.getProperty("APP_ID");
		this.APP_KEY = properties.getProperty("APP_KEY");
		httpclient = getHttpClient();
		voices.add(new Voice(this, "Samantha", Gender.FEMALE, Language.ENGLISH_US, false));
		voices.add(new Voice(this, "Tom", Gender.MALE, Language.ENGLISH_US, false));
		voices.add(new Voice(this, "Allison", Gender.FEMALE, Language.ENGLISH_US, false));
		voices.add(new Voice(this, "Ava", Gender.FEMALE, Language.ENGLISH_US, false));
		voices.add(new Voice(this, "Susan", Gender.FEMALE, Language.ENGLISH_US, false));
		voices.add(new Voice(this, "Zoe", Gender.FEMALE, Language.ENGLISH_US, false));
		voices.add(new Voice(this, "Alva", Gender.FEMALE, Language.SWEDISH, false));
		voices.add(new Voice(this, "Oskar", Gender.MALE, Language.SWEDISH, false));
	}

	@Override
	public SynthesizerEngine getEngine(Voice voice) throws InitializationException {
		return new NuanceCloudSynthesizerEngine(voice);
	}

	@Override
	public VoiceList getVoices() {
		return voices;
	}

	@Override
	public String getName() {
		return "Nuance Cloud";
	}

	
	/*
	 * This function will initialize httpclient, set some basic HTTP parameters (version, UTF),
	 *	and setup SSL settings for communication between the httpclient and our Nuance servers
	 */
	private HttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException
	{
		// Standard HTTP parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, false);

		// Initialize the HTTP client
		httpclient = new DefaultHttpClient(params);

		// Initialize/setup SSL
		TrustManager easyTrustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] arg0, String arg1)
							throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] arg0, String arg1)
							throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
		SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme sch = new Scheme("https", sf, PORT);	// PORT = 443
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);

		// Return the initialized instance of our httpclient
		return httpclient;
	}

	
	private class NuanceCloudSynthesizerEngine implements SynthesizerEngine {

		private Voice voice;

		NuanceCloudSynthesizerEngine(Voice voice) {
			this.voice = voice;
		}
		 
		@Override
		public Transcription synthesize(String text, File file) {
			try {
				HttpPost httppost = getHeader(text);
				//System.out.println("executing request " + httppost.getRequestLine());
				HttpResponse response = httpclient.execute(httppost);
				processResponse(response, file);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new Transcription();
		}

		@Override
		public Transcription transcribe(String text) {
			return new Transcription();
		}

		@Override
		public AudioFormat getAudioFormat() {
			return audioFormat;
		}

		@Override
		public Voice getVoice() {
			return voice;
		}
		
		/*
		 * This is a simpler helper function to setup the Header parameters
		 */
		private HttpPost getHeader(String text) throws URISyntaxException, UnsupportedEncodingException 
		{
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();

			qparams.add(new BasicNameValuePair("appId", APP_ID));
			qparams.add(new BasicNameValuePair("appKey", APP_KEY));
			qparams.add(new BasicNameValuePair("id",  DEVICE_ID));
			qparams.add(new BasicNameValuePair("voice", voice.getName()));
			qparams.add(new BasicNameValuePair("ttsLang", voice.getLanguage().getCode().replace("-", "_")));

			URI uri = URIUtils.createURI("https", HOSTNAME, PORT, TTS, URLEncodedUtils.format(qparams, "UTF-8"), null);

			HttpPost httppost = new HttpPost(uri);
			httppost.addHeader("Content-Type",  "text/plain");
			httppost.addHeader("Accept", CODEC);

			// We'll also set the content of the POST request now...
			HttpEntity entity = new StringEntity(text, "iso-8859-1");
			httppost.setEntity(entity);

			return httppost;
		}

		/*
		 * This function will take the HTTP response and parse out header values, write the audio that's been returned
		 *	to filed, and log details to the console
		 */
		private void processResponse(HttpResponse response, File outFile) throws IllegalStateException, IOException
		{
			HttpEntity resEntity = response.getEntity();

			//System.out.println("----------------------------------------");
			//System.out.println(response.getStatusLine());

			// The request failed. Check out the status line to see what the problem is.
			//	Typically an issue with one of the parameters passed in...
			if (resEntity == null)
				return;

			// Grab the date
			Header date = response.getFirstHeader("Date");
			//if( date != null )
			//	System.out.println("Date: " + date.getValue());

			// ALWAYS grab the Nuance-generated session id. Makes it a WHOLE LOT EASIER for us to hunt down your issues in our logs
			Header sessionid = response.getFirstHeader("x-nuance-sessionid");
			//if( sessionid != null )
			//	System.out.println("x-nuance-sessionid: " + sessionid.getValue());

			// Check to see if we have a 200 OK response. Otherwise, review the technical documentation to understand why you recieved
			//	the HTTP error code that came back
			//String status = response.getStatusLine().toString();
			//boolean okFound = ( status.indexOf("200 OK") > -1 );
			//if( okFound )
			//{
			//	System.out.println("Response content length: " + resEntity.getContentLength());
			//	System.out.println("Chunked?: " + resEntity.isChunked());
			//}

			// Grab the returned audio (or error message) returned in the body of the response
			InputStream in = resEntity.getContent();
			byte[] buffer = new byte[1024 * 16];
			int len;

			ByteArrayOutputStream bout = new ByteArrayOutputStream(); 

			// Attempt to write to file...
			try {
				while((len = in.read(buffer)) > 0){
					bout.write(buffer, 0 , len);
				}

				Sound s = new Sound(bout.toByteArray(), getAudioFormat());
				s.save(outFile);
			} catch (Exception e) {
				System.err.println("Failed to save file: " + e.getMessage());
				e.printStackTrace();
			}


			//System.out.println("----------------------------------------");

			// And we're done.
			resEntity.consumeContent();
		}

		
	}


}
