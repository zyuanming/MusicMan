package org.ming.center.player;

import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.util.Log;

public class PlayerAudioTrack
{
	AudioTrack mAudioTrack;
	int mChannel;
	int mFrequency;
	int mSampBit;

	public PlayerAudioTrack(int paramInt1, int paramInt2, int paramInt3)
	{
		this.mFrequency = paramInt1;
		this.mChannel = paramInt2;
		this.mSampBit = paramInt3;
	}

	public int getCurrentPosition()
	{
		if (this.mAudioTrack == null)
			;
		for (int i = 0;; i = 1000 * (this.mAudioTrack.getPlaybackHeadPosition() / this.mFrequency))
			return i;
	}

	public int getPrimePlaySize()
	{
		return 2 * AudioTrack.getMinBufferSize(this.mFrequency, this.mChannel,
				this.mSampBit);
	}

	public void init()
	{
		if (this.mAudioTrack != null)
			release();
		int i = AudioTrack.getMinBufferSize(this.mFrequency, this.mChannel,
				this.mSampBit);
		this.mAudioTrack = new AudioTrack(3, this.mFrequency, this.mChannel,
				this.mSampBit, i, 1);
		this.mAudioTrack
				.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener()
				{
					public void onMarkerReached(
							AudioTrack paramAnonymousAudioTrack)
					{}

					public void onPeriodicNotification(
							AudioTrack paramAnonymousAudioTrack)
					{}
				});
	}

	public void pause()
	{
		if (this.mAudioTrack != null)
			this.mAudioTrack.pause();
	}

	public void play()
	{
		if (this.mAudioTrack != null)
			this.mAudioTrack.play();
	}

	public void playAudioTrack(byte[] paramArrayOfByte, int paramInt1,
			int paramInt2)
	{
		if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0))
			try
			{
				this.mAudioTrack.write(paramArrayOfByte, paramInt1, paramInt2);
			} catch (Exception localException)
			{
				Log.i("PlayerAudioTrack", "catch exception...");
			}
	}

	public void release()
	{
		if (this.mAudioTrack != null)
		{
			this.mAudioTrack.stop();
			this.mAudioTrack.release();
		}
	}

	public void setCurrentPosition(int paramInt)
	{
		this.mAudioTrack.setPlaybackHeadPosition(paramInt);
	}

	public void stop()
	{
		if (this.mAudioTrack != null)
			this.mAudioTrack.stop();
	}
}