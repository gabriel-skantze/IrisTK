package iristk.speech.wit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import iristk.speech.RecResult;
import iristk.speech.RecognizerListener;
import iristk.util.Record;
import iristk.util.Record.JsonToRecordException;
import iristk.util.Utils;

public class WitListener implements RecognizerListener {

	private List<String> keys = new ArrayList<>();

	public WitListener() {
	}
	
	@Override
	public void initRecognition(AudioFormat format) {
	}

	@Override
	public void startOfSpeech(float timestamp) {
	}

	@Override
	public void endOfSpeech(float timestamp) {
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
	}

	@Override
	public void recognitionResult(RecResult result) {
		if (result != null && result.text != null && keys.size() > 0) {
			try {
				Record json = Record.fromJSON(getResult(result.text, keys.get(0)));
				String intent = json.getString("outcomes:0:intent");
				// TODO: process outcomes:0:entities
				if (intent != null) {
					result.put("sem:intent", intent);
				}
			} catch (JsonToRecordException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getResult(String text, String key) throws IOException {
		long t = System.currentTimeMillis();
		URL url = new URL("https://api.wit.ai/message?v=20150929&q=" + URLEncoder.encode(text));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + key);
		
		String result = Utils.readString(conn.getInputStream());

		System.out.println("Wit.ai time: " + (System.currentTimeMillis() - t));
		return result;
	}
	
	private static String getResult2() throws IOException {
		long t = System.currentTimeMillis();
		StringBuilder result = new StringBuilder();
		URL url = new URL("http://www.google.com");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Authorization", "Bearer " + key);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		System.out.println("google.com time: " + (System.currentTimeMillis() - t));
		return result.toString();
	}
	
	private static String getResultLUIS() throws IOException {
		long t = System.currentTimeMillis();
		StringBuilder result = new StringBuilder();
		URL url = new URL("https://api.projectoxford.ai/luis/v1/application?id=c413b2ef-382c-45bd-8ff0-f76d60e2a821&subscription-key=123b937e72b0455caf0da4ee090677eb&q=call%20mom");
		//URL url = new URL("https://api.projectoxford.ai/luis/v1/application?id=454086e8-70d7-4fdf-9611-4052566bf370&subscription-key=123b937e72b0455caf0da4ee090677eb&q=what%20is%20my%20name");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		System.out.println("LUIS time: " + (System.currentTimeMillis() - t));
		return result.toString();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(getResult("turn on the lights", "ZHHJUFR6V7WMEAK2N2YH3TWOUO5Y3XUK"));
		System.out.println(getResult2());
		System.out.println(getResultLUIS());
	}

	public void activate(String key) {
		keys .add(key);
	}

	public void deactivate(String key) {
		keys.remove(key);
	}

}
