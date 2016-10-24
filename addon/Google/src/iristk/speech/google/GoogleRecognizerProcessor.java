package iristk.speech.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.SpeechContext;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.SpeechGrpc.SpeechStub;
import com.google.cloud.speech.v1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.stub.StreamObserver;
import iristk.audio.AudioUtil;
import iristk.audio.Sound;
import iristk.speech.RecHyp;
import iristk.speech.RecResult;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerProcessor;
import iristk.system.IrisUtils;
import iristk.util.BlockingByteQueue;
import iristk.util.Language;

public class GoogleRecognizerProcessor extends RecognizerProcessor {

	private static final boolean LOG_AUDIO_FILES = false;

	private static Logger logger = IrisUtils.getLogger(GoogleRecognizerProcessor.class);

	private ManagedChannel channel;

	private SpeechStub speechClient;

	private StreamObserver<StreamingRecognizeRequest> requestObserver;

	private CountDownLatch finishLatch;

	private HashMap<Integer,StreamingRecognizeResponse> responses = new HashMap<>();
	private int responseCount = 0;

	private String lastPartialResult = "";

	private Language lang = Language.ENGLISH_US;

	private boolean active = true;

	private boolean inSpeech = false;

	private int maxAlternatives = 1;

	private boolean partialResults = false;

	private List<String> phrases = new ArrayList<String>();

	private BlockingByteQueue speechQueue = new BlockingByteQueue();

	private BlockingByteQueue testFileBuffer = new BlockingByteQueue();
	private int testFileN = 0;

	private StreamObserver<StreamingRecognizeResponse> responseObserver =
			new StreamObserver<StreamingRecognizeResponse>() {

		@Override
		public void onNext(StreamingRecognizeResponse response) {
			if (response.getResultsCount() > 0) {
				responseCount = response.getResultIndex() + 1;
				responses.put(response.getResultIndex(), response);
				if (partialResults) {
					generatePartialResult();
				}
			}
			//logger.info("Received response: " + response.getResultsCount() + " " + TextFormat.printToString(response));
		}

		@Override
		public void onError(Throwable error) {
			logger.error("Google recognizer failed");
			if (finishLatch != null)
				finishLatch.countDown();
		}

		@Override
		public void onCompleted() {
			//logger.info("recognize completed.");
			finishLatch.countDown();
		}
	};


	public GoogleRecognizerProcessor(File credentials) throws RecognizerException {
		try {
			GoogleCredentials creds;
			creds = GoogleCredentials.fromStream(new FileInputStream(credentials));
			creds = creds.createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
			channel =
					ManagedChannelBuilder.forAddress("speech.googleapis.com", 443)
					.intercept(new ClientAuthInterceptor(creds, Executors.newSingleThreadExecutor()))
					.build();

			speechClient = SpeechGrpc.newStub(channel);

			// We do this once here because it takes time the first time it is done
			dummyRequest();

		} catch (IOException e) {
			throw new RecognizerException("Could not read Google credentials: " + credentials.getAbsolutePath());
		} catch (Exception e) {
			throw new RecognizerException("Problem initializing Google recognizer");
		}

		//TODO: should we shut down at some point?
		//channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	private void dummyRequest() {
		StreamObserver<StreamingRecognizeResponse> dummyObserver = 
				new StreamObserver<StreamingRecognizeResponse>() {
			@Override
			public void onCompleted() {
			}
			@Override
			public void onError(Throwable arg0) {
			}
			@Override
			public void onNext(StreamingRecognizeResponse arg0) {
			}
		};
		StreamObserver<StreamingRecognizeRequest> requestObserver = speechClient.streamingRecognize(dummyObserver);
		RecognitionConfig config = RecognitionConfig.newBuilder().build();
		StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder().setConfig(config).build();
		StreamingRecognizeRequest initial = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
		requestObserver.onNext(initial);
		requestObserver.onCompleted();
	}

	public GoogleRecognizerProcessor() throws RecognizerException {
		this(GooglePackage.PACKAGE.getPath("credentials.json"));
	}

	@Override
	public void initRecognition(AudioFormat format) {
		inSpeech = false;
		responses.clear();
		responseCount = 0;
		lastPartialResult = "";
		if (LOG_AUDIO_FILES)
			testFileBuffer.reset();
		if (getNext() != null)
			getNext().initRecognition(format);
	}

	private class RecognitionThread implements Runnable {

		public RecognitionThread() {
			finishLatch = new CountDownLatch(1);
			new Thread(this).start();
		}

		@Override
		public void run() {
			requestObserver = speechClient.streamingRecognize(responseObserver);
			// Build and send a StreamingRecognizeRequest containing the parameters for
			// processing the audio.

			try {
				RecognitionConfig.Builder configBuilder = RecognitionConfig.newBuilder()
						.setEncoding(AudioEncoding.LINEAR16)
						.setSampleRate(16000)
						.setLanguageCode(lang.getCode())
						.setMaxAlternatives(maxAlternatives);
				if (phrases.size() > 0) {
					SpeechContext.Builder contextBuilder = SpeechContext.newBuilder();
					contextBuilder.addAllPhrases(phrases);
					//System.out.println(phrases);
					configBuilder.setSpeechContext(contextBuilder);
				}
				RecognitionConfig config = configBuilder.build();

				StreamingRecognitionConfig streamingConfig =
						StreamingRecognitionConfig.newBuilder()
						.setConfig(config)
						.setInterimResults(partialResults)
						.setSingleUtterance(false)
						.build();

				StreamingRecognizeRequest initial =
						StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
				requestObserver.onNext(initial);

				//int len;
				byte[] buf = new byte[3200];
				while (speechQueue.read(buf, 0, buf.length) == buf.length) {
					StreamingRecognizeRequest request =
							StreamingRecognizeRequest.newBuilder()
							.setAudioContent(ByteString.copyFrom(buf, 0, buf.length))
							.build();
					requestObserver.onNext(request);
				}
				requestObserver.onCompleted();
			} catch (Exception e) {
				requestObserver.onError(e);
				logger.error("Error streaming audio: " + e.getMessage());
			}
		}
	}

	@Override
	public void startOfSpeech(float timestamp) {
		speechQueue.reset();
		if (!active)
			return;
		inSpeech = true;
		new RecognitionThread();
		if (getNext() != null)
			getNext().startOfSpeech(timestamp);
	}

	@Override
	public void endOfSpeech(float timestamp) {
		speechQueue.endWrite();
		if (LOG_AUDIO_FILES) {
			byte[] bytes = new byte[testFileBuffer.available()];
			try {
				testFileBuffer.read(bytes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Sound(bytes, AudioUtil.getAudioFormat(16000, 1)).save(new File("googleASR." + testFileN++ + ".wav"));
		}
		if (getNext() != null)
			getNext().endOfSpeech(timestamp);
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
		if (LOG_AUDIO_FILES)
			testFileBuffer.write(samples, pos, len);
		speechQueue.write(samples, pos, len);
		if (getNext() != null)
			getNext().speechSamples(samples, pos, len);
	}

	@Override
	public void recognitionResult(RecResult result) {
		if (inSpeech) {
			try {
				finishLatch.await(2, TimeUnit.SECONDS);
				if (responseCount > 0) {
					generateResult(result);
				}
			} catch (InterruptedException e) {
				result.type = RecResult.FAILED;
				result.put("reason", "service timeout");
				e.printStackTrace();
			}
		}
		if (getNext() != null)
			getNext().recognitionResult(result);
	}

	private void generateResult(RecResult recresult) {
		String text = new String();
		float conf = 1.0f;
		for (int i = 0; i < responseCount; i++) {
			StreamingRecognizeResponse response = responses.get(i);
			for (int j = 0; j < response.getResultsCount(); j++) {
				StreamingRecognitionResult result = response.getResults(j);
				if (text.length() > 0)
					text += " ";
				text += result.getAlternatives(0).getTranscript().trim();
				conf = Math.min(conf, result.getAlternatives(0).getConfidence());
			}
		}
		if (maxAlternatives > 1 && recresult.isFinal()) {
			// TODO: make better handling of several results
			List<RecHyp> hypList = new ArrayList<>();
			StreamingRecognitionResult result = responses.get(0).getResults(0);
			for (int i = 0; i < result.getAlternativesCount(); i++) {
				RecHyp hyp = new RecHyp();
				hyp.text = result.getAlternatives(i).getTranscript();
				hyp.conf = result.getAlternatives(i).getConfidence();
				hypList.add(hyp);
			}
			recresult.nbest = hypList;
		}
		recresult.put("text", text.toString());
		recresult.put("conf", conf);
	}

	private void generatePartialResult() {
		RecResult result = new RecResult(RecResult.PARTIAL);
		generateResult(result);
		if (!lastPartialResult.equals(result.text)) {
			lastPartialResult = result.text;
			if (getNext() != null)
				getNext().recognitionResult(result);
		}
	}
	
	public void setActive(boolean b) {
		this.active  = b;
	}

	public void setLanguage(Language language) {
		this.lang = language;
	}

	public void setNbestLength(int length) {
		this.maxAlternatives = length;
	}

	public void setPartialResults(boolean cond) {
		this.partialResults = cond;
	}

	public void setPhrases(List phrases) {
		this.phrases.clear();
		if (phrases != null) {
			for (Object phrase : phrases)
				this.phrases.add(phrase.toString());
		}
	}

}
