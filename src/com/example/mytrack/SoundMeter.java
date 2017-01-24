package com.example.mytrack;

import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
//classe per misurare il suono
public class SoundMeter {

	public static double REFERENCE = 0.00002;

	private MediaRecorder mRecorder = null;

	private static double mEMA = 0.0;
	static final private double EMA_FILTER = 0.6;

	public void start() {
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");
			try {
				mRecorder.prepare();
				mRecorder.start();
				mEMA = 0.0;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void stop() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public double soundDb() {
		double p = mRecorder.getMaxAmplitude() / 51805.5336;
		double db = 20 * Math.log10(p / 0.00002);
		if (db > 0)
			return db;
		return 0;
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return mRecorder.getMaxAmplitude();
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}

}
