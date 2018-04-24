package org.tensorflow.demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import org.tensorflow.demo.R;

import org.tensorflow.demo.util.DialogRateUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;
import static android.os.SystemClock.sleep;

public class DialogRateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
	// Constants that control the behavior of the recognition code and model
	// settings. See the audio recognition tutorial for a detailed explanation of
	// all these, but you should customize them to match your training settings if
	// you are running your own model.
	private static final int SAMPLE_RATE = 16000;
	private static final int SAMPLE_DURATION_MS = 1000;
	private static final int RECORDING_LENGTH = (int) (SAMPLE_RATE * SAMPLE_DURATION_MS / 1000);
	private static final long AVERAGE_WINDOW_DURATION_MS = 500;
	private static final float DETECTION_THRESHOLD = 0.70f;
	private static final int SUPPRESSION_MS = 1500;
	private static final int MINIMUM_COUNT = 3;
	private static final long MINIMUM_TIME_BETWEEN_SAMPLES_MS = 30;
	private static final String LABEL_FILENAME = "file:///android_asset/conv_actions_labels.txt";
	private static final String MODEL_FILENAME = "file:///android_asset/conv_actions_frozen.pb";
	private static final String INPUT_DATA_NAME = "decoded_sample_data:0";
	private static final String SAMPLE_RATE_NAME = "decoded_sample_data:1";
	private static final String OUTPUT_SCORES_NAME = "labels_softmax";

	// UI elements.
	private static final int REQUEST_RECORD_AUDIO = 13;
	private Button quitButton;
	private ListView labelsListView;
	private static final String LOG_TAG = SplashScreensActivity.class.getSimpleName();

	// Working variables.
	short[] recordingBuffer = new short[RECORDING_LENGTH];
	int recordingOffset = 0;
	boolean shouldContinue = true;
	private Thread recordingThread;
	boolean shouldContinueRecognition = true;
	private Thread recognitionThread;
	private final ReentrantLock recordingBufferLock = new ReentrantLock();
	private TensorFlowInferenceInterface inferenceInterface;
	private List<String> labels = new ArrayList<String>();
	private List<String> displayedLabels = new ArrayList<>();
	private RecognizeCommands recognizeCommands = null;
	/***************************************************/
	private Button mBlindButton;
	private Button mVolunteerButton;
	/******************/
	private String toSpeak ;
	private TextToSpeech tts;
	// status check code
	private int MY_DATA_CHECK_CODE = 0;

	Thread logoTimer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		/******************************************/
		toSpeak = getResources().getString(R.string.rate_msg) ;
		tts = new TextToSpeech(this, this);
		logoTimer = new Thread() {
			public void run() {
				try {
					sleep(500);
					speakOut(toSpeak);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};
		logoTimer.start();
		/*****************************************/
		DialogRateUtils dialog = new DialogRateUtils(this);
		dialog.showDialog();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.rate);
		/**********************************************/
		// Load the labels for the model, but only display those that don't start
		// with an underscore.
		String actualFilename = LABEL_FILENAME.split("file:///android_asset/")[1];
		Log.i(LOG_TAG, "Reading labels from: " + actualFilename);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open(actualFilename)));
			String line;
			while ((line = br.readLine()) != null) {
				labels.add(line);
				if (line.charAt(0) != '_') {
					displayedLabels.add(line.substring(0, 1).toUpperCase() + line.substring(1));
				}
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("Problem reading label file!", e);
		}

		// Set up an object to smooth recognition results to increase accuracy.
		recognizeCommands =
				new RecognizeCommands(
						labels,
						AVERAGE_WINDOW_DURATION_MS,
						DETECTION_THRESHOLD,
						SUPPRESSION_MS,
						MINIMUM_COUNT,
						MINIMUM_TIME_BETWEEN_SAMPLES_MS);

		// Load the TensorFlow model.
		inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILENAME);

		// Start the recording and recognition threads.
		requestMicrophonePermission();
		startRecording();
		startRecognition();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void requestMicrophonePermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(
					new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
		}
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_RECORD_AUDIO
				&& grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			startRecording();
			startRecognition();
		}
	}

	public synchronized void startRecording() {
		if (recordingThread != null) {
			return;
		}
		shouldContinue = true;
		recordingThread =
				new Thread(
						new Runnable() {
							@Override
							public void run() {
								sleep(4500);
								record();
							}
						});
		recordingThread.start();
	}

	public synchronized void stopRecording() {
		if (recordingThread == null) {
			return;
		}
		shouldContinue = false;
		recordingThread = null;
	}

	private void record() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

		// Estimate the buffer size we'll need for this device.
		int bufferSize =
				AudioRecord.getMinBufferSize(
						SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
			bufferSize = SAMPLE_RATE * 2;
		}
		short[] audioBuffer = new short[bufferSize / 2];

		AudioRecord record =
				new AudioRecord(
						MediaRecorder.AudioSource.DEFAULT,
						SAMPLE_RATE,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT,
						bufferSize);

		if (record.getState() != AudioRecord.STATE_INITIALIZED) {
			Log.e(LOG_TAG, "Audio Record can't initialize!");
			return;
		}

		record.startRecording();

		Log.v(LOG_TAG, "Start recording");

		// Loop, gathering audio data and copying it to a round-robin buffer.
		while (shouldContinue) {
			int numberRead = record.read(audioBuffer, 0, audioBuffer.length);
			int maxLength = recordingBuffer.length;
			int newRecordingOffset = recordingOffset + numberRead;
			int secondCopyLength = Math.max(0, newRecordingOffset - maxLength);
			int firstCopyLength = numberRead - secondCopyLength;
			// We store off all the data for the recognition thread to access. The ML
			// thread will copy out of this buffer into its own, while holding the
			// lock, so this should be thread safe.
			recordingBufferLock.lock();
			try {
				System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength);
				System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength);
				recordingOffset = newRecordingOffset % maxLength;
			} finally {
				recordingBufferLock.unlock();
			}
		}

		record.stop();
		record.release();
	}

	public synchronized void startRecognition() {
		if (recognitionThread != null) {
			return;
		}
		shouldContinueRecognition = true;
		recognitionThread =
				new Thread(
						new Runnable() {
							@Override
							public void run() {
								sleep(4500);
								recognize();
							}
						});
		recognitionThread.start();
	}

	public synchronized void stopRecognition() {
		if (recognitionThread == null) {
			return;
		}
		shouldContinueRecognition = false;
		recognitionThread = null;
	}

	private void recognize() {
		Log.v(LOG_TAG, "Start recognition");

		short[] inputBuffer = new short[RECORDING_LENGTH];
		float[] floatInputBuffer = new float[RECORDING_LENGTH];
		float[] outputScores = new float[labels.size()];
		String[] outputScoresNames = new String[] {OUTPUT_SCORES_NAME};
		int[] sampleRateList = new int[] {SAMPLE_RATE};

		// Loop, grabbing recorded data and running the recognition model on it.
		while (shouldContinueRecognition) {
			// The recording thread places data in this round-robin buffer, so lock to
			// make sure there's no writing happening and then copy it to our own
			// local version.
			recordingBufferLock.lock();
			try {
				int maxLength = recordingBuffer.length;
				int firstCopyLength = maxLength - recordingOffset;
				int secondCopyLength = recordingOffset;
				System.arraycopy(recordingBuffer, recordingOffset, inputBuffer, 0, firstCopyLength);
				System.arraycopy(recordingBuffer, 0, inputBuffer, firstCopyLength, secondCopyLength);
			} finally {
				recordingBufferLock.unlock();
			}

			// We need to feed in float values between -1.0f and 1.0f, so divide the
			// signed 16-bit inputs.
			for (int i = 0; i < RECORDING_LENGTH; ++i) {
				floatInputBuffer[i] = inputBuffer[i] / 32767.0f;
			}

			// Run the model.
			inferenceInterface.feed(SAMPLE_RATE_NAME, sampleRateList);
			inferenceInterface.feed(INPUT_DATA_NAME, floatInputBuffer, RECORDING_LENGTH, 1);
			inferenceInterface.run(outputScoresNames);
			inferenceInterface.fetch(OUTPUT_SCORES_NAME, outputScores);

			// Use the smoother to figure out if we've had a real recognition event.
			long currentTime = System.currentTimeMillis();
			final RecognizeCommands.RecognitionResult result =
					recognizeCommands.processLatestResults(outputScores, currentTime);

			runOnUiThread(
					new Runnable() {
						@Override
						public void run() {
							// If we do have a new command, highlight the right list entry.
							if (!result.foundCommand.startsWith("_") && result.isNewCommand) {
								int labelIndex = -1;
								for (int i = 0; i < labels.size(); ++i) {
									if (labels.get(i).equals(result.foundCommand)) {
										labelIndex = i;
									}
								}
								/******************************************/
								if(labelIndex==2 || labelIndex==11){
									if (tts != null) {
										tts.stop();
										tts.shutdown();
									}
									stopRecording();
									stopRecognition();
									Intent intent = new Intent(DialogRateActivity.this,MenuActivity.class);
									startActivity(intent);
									finish();
								}
								else{
									//Toast.makeText(SplashScreensActivity.this,"okkkkkkkk2",Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
			try {
				// We don't need to run too frequently, so snooze for a bit.
				sleep(MINIMUM_TIME_BETWEEN_SAMPLES_MS);
			} catch (Exception e) {
				// Ignore
			}
		}
	}
	// act on result of TTS data check
	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				speakOut(toSpeak);
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	private void speakOut(String toSpeak){
		tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
	}



	public void onBackPressed()
	{
		Intent intent=new Intent(DialogRateActivity.this,MenuActivity.class);

		startActivity(intent);
		finish();
	}
}
