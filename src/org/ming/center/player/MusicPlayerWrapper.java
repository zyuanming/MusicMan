package org.ming.center.player;

import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;

import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MusicPlayerWrapper {

	private int interruptState = 0;
	private static final int INTERRUPT_ANY = 7;
	private static final int INTERRUPT_CALL = 2;
	private static final int INTERRUPT_DATA = 4;
	private static final int INTERRUPT_NONE = 0;
	private static final int INTERRUPT_PAUSE = 1;
	private static final int MAX_SONG_DURATION = 900000;
	private static final MyLogger logger = MyLogger
			.getLogger("MusicPlayerWrapper");
	private final int CHECK_30S = 2;
	final int EVENT_PLAY_OVER = 256;
	private final int NETWORK_PER_PLAY_LEN = 81920;
	private final int NETWORK_PER_PLAY_LEN_DUBY = 102400;
	private final int START = 0;
	private final int STOP = 1;
	// private DataSourceHandler currentHandler;
	private Song currentSong;
	private long currentSongBytesPlayed;
	private int currentSongDuration;
	private int currentSongMilliSecPlayed;
	private boolean isAbandonAudioFocus = false;
	// private DataSourceHandler localHandler;
	private MobileMusicApplication mApp;
	private Dispatcher mDispatcher;
	private Song mSong;
	private TelephonyManager mTelephonyManager;
	private MediaPlayer player;
	private volatile State state = State.END;

	public MusicPlayerWrapper(MobileMusicApplication paramMobileMusicApplication) {
		logger.v("MusicPlayerWrapper() ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mDispatcher = paramMobileMusicApplication.getEventDispatcher();
		this.mTelephonyManager = ((TelephonyManager) paramMobileMusicApplication
				.getSystemService("phone"));
		this.mTelephonyManager.listen(new PhoneStateListener() {
			public void onCallStateChanged(int paramAnonymousInt,
					String paramAnonymousString) {
				switch (paramAnonymousInt) {
				default:
				case 1:
				case 2:
				case 0:
				}
				if (state != MusicPlayerWrapper.State.PAUSED) {
					interruptBy(2);
					MusicPlayerWrapper.this.resumeInterruptBy(2);
				}
			}
		}, 32);
		logger.v("MusicPlayerWrapper() ---> Exit");
	}

	private void resumeInterruptBy(int paramInt) {
		this.interruptState &= (paramInt ^ 0xFFFFFFFF);
		resumeInterrupt();
	}

	private void resumeInterrupt() {
		// AudioManager localAudioManager;
		// logger.v("resumeInterrupt() ---> Enter");
		// localAudioManager = (AudioManager) this.mApp.getApplicationContext()
		// .getSystemService("audio");
		// if ((this.mSong != null)
		// && ((checkedLocalSongType()) || ((this.mSong != null) &&
		// (this.mSong.isDolby)))) {
		// if ((!isInterruptedBy(7)) && (this.mAudioTrackPlay != null)) {
		// localAudioManager.requestAudioFocus(this.mAudioFocusListener,
		// 3, 1);
		// this.mApp.sendBroadcast(new Intent(
		// "cmccwm.mobilemusic.action.PLAYER_START"));
		// this.mAudioTrackPlay.dostart();
		// this.state = State.STARTED;
		// this.mDispatcher.sendMessage(this.mDispatcher
		// .obtainMessage(1010));
		// }
		// logger.v("resumeInterrupt() ---> Exit");
		// return;
		// }
		// if (this.player != null) {
		// State[] arrayOfState = new State[5];
		// arrayOfState[0] = State.PREPARED;
		// arrayOfState[1] = State.STARTED;
		// arrayOfState[2] = State.PAUSED;
		// arrayOfState[3] = State.STOPPED;
		// arrayOfState[4] = State.COMPLETED;
		// if (inStates(arrayOfState)) {
		// if (isInterruptedBy(7))
		// continue;
		// localAudioManager.requestAudioFocus(this.mAudioFocusListener,
		// 3, 1);
		// this.mApp.sendBroadcast(new Intent(
		// "cmccwm.mobilemusic.action.PLAYER_START"));
		// this.player.start();
		// this.state = State.STARTED;
		// this.mDispatcher.sendMessage(this.mDispatcher
		// .obtainMessage(1010));
		// continue;
		// }
		// }
		// localAudioManager.requestAudioFocus(this.mAudioFocusListener, 3, 1);
	}

	public enum State {
		IDLE(0), INITIALING(1), INITIALIZED(2), PREPARING(3), PREPARED(4), STARTED(
				5), SEEKING(6), PAUSED(7), STOPPED(8), COMPLETED(9), ERROR(10), END(
				11);
		int id;

		State(int id) {
			this.id = id;
		}
	}

	private void interruptBy(int paramInt) {
		try {
			this.interruptState = (paramInt | this.interruptState);
			interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void interrupt() {
		if ((checkedLocalSongType())
				|| ((this.mSong != null) && (this.mSong.isDolby)))
			// if (this.mAudioTrackPlay != null) {
			// this.mAudioTrackPlay.pause();
			// this.state = State.PAUSED;
			// this.mDispatcher.removeMessages(1013);
			// }
			if (this.player != null) {
				State[] arrayOfState = new State[2];
				arrayOfState[0] = State.STARTED;
				arrayOfState[1] = State.PAUSED;
				if ((inStates(arrayOfState)) && (isInterruptedBy(7))) {
					this.player.pause();
					this.state = State.PAUSED;
					this.mDispatcher.removeMessages(1017);
					this.mDispatcher.removeMessages(1013);
					this.mDispatcher.sendMessage(this.mDispatcher
							.obtainMessage(1011));
				}
			}
	}

	private boolean inStates(State[] paramArrayOfState) {
		int i = paramArrayOfState.length;
		boolean bool = false;
		for (int j = 0; j < i; j++) {
			State localState = paramArrayOfState[j];
			if (this.state != localState)
				break;
			bool = true;
		}
		return bool;
	}

	private boolean checkedLocalSongType() {
		Song localSong1 = this.mSong;
		boolean bool1 = false;
		if (localSong1 == null)
			return bool1;
		int i = this.mSong.mMusicType;
		int j = MusicType.LOCALMUSIC;
		bool1 = false;
		if (i == j) {
			Song localSong2 = this.mSong;
			bool1 = false;
			if (localSong2 != null) {
				boolean bool2 = this.mSong.isDolby;
				bool1 = false;
				if (bool2)
					bool1 = true;
			}
		}
		return bool1;
	}

	public boolean isInteruptByCall() {
		try {
			boolean bool = isInterruptedBy(2);
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isInterruptedBy(int paramInt) {
		try {
			boolean bool = false;
			int i = this.interruptState;
			if ((i & paramInt) != 0) {
				bool = true;
				return bool;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
