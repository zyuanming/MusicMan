package org.ming.center.player;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ming.center.ConfigSettingParameter;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.MobileMusicApplication;
import org.ming.center.database.MusicType;
import org.ming.center.database.Song;
import org.ming.center.system.SystemControllerImpl;
import org.ming.dispatcher.Dispatcher;
import org.ming.dispatcher.DispatcherEventEnum;
import org.ming.util.MyLogger;
import org.ming.util.NetUtil;
import org.ming.util.Util;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MusicPlayerWrapper
{
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
	private DataSourceHandler currentHandler;
	private Song currentSong;
	private long currentSongBytesPlayed;
	private int currentSongDuration;
	private int currentSongMilliSecPlayed;
	private Observer dataChecker = new Observer()
	{
		public void update(Observable paramAnonymousObservable,
				Object paramAnonymousObject)
		{
			DataSourceHandler localDataSourceHandler = (DataSourceHandler) paramAnonymousObject;
			if (localDataSourceHandler.isEnd())
			{
				Log.v("xxx cache1",
						"all:" + localDataSourceHandler.getPercent()
								+ "Downloaded:"
								+ localDataSourceHandler.getBytes());
				MusicPlayerWrapper.this.mDispatcher
						.sendMessage(MusicPlayerWrapper.this.mDispatcher
								.obtainMessage(1020));
				paramAnonymousObservable.deleteObserver(this);
			}
			if ((localDataSourceHandler.getPercent() <= MusicPlayerWrapper.this
					.getCurrentPosition()
					/ MusicPlayerWrapper.this.getDuration())
					&& (!MusicPlayerWrapper.this.checkedLocalSongType())
					&& ((MusicPlayerWrapper.this.mSong == null) || (!MusicPlayerWrapper.this.mSong.isDolby)))
			{
				MusicPlayerWrapper.this.interruptBy(4);
				MusicPlayerWrapper.this.currentSongBytesPlayed = localDataSourceHandler
						.getBytes();
				Log.v("xxx cache2",
						"all:" + localDataSourceHandler.getPercent()
								+ "Downloaded:"
								+ localDataSourceHandler.getBytes());
				MusicPlayerWrapper.this.doComplete(localDataSourceHandler);
				paramAnonymousObservable.deleteObserver(this);
			}
		}
	};
	private int interruptState = 0;
	private boolean isAbandonAudioFocus = false;
	private DataSourceHandler localHandler;
	private MobileMusicApplication mApp;
	private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener()
	{
		public void onAudioFocusChange(int paramAnonymousInt)
		{
			if (MusicPlayerWrapper.this.isAbandonAudioFocus)
			{
				if (paramAnonymousInt == -2)
					MusicPlayerWrapper.this.autoPause();
				else if (paramAnonymousInt == 1)
					MusicPlayerWrapper.this.resume();
				else if (paramAnonymousInt == -1)
					MusicPlayerWrapper.this.autoPause();
			}
		}
	};
	private AudioTrackPlayThread mAudioTrackPlay = null;
	private Dispatcher mDispatcher;
	final Handler mHandler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			if (paramAnonymousMessage.what == 256)
				MusicPlayerWrapper.this.mAudioTrackPlay = null;
		}
	};
	private Song mSong;
	private TelephonyManager mTelephonyManager;
	private MediaPlayer player;
	private Observer startChecker = new Observer()
	{
		public void update(Observable paramAnonymousObservable,
				Object paramAnonymousObject)
		{
			if (MusicPlayerWrapper.this.doUpdate(paramAnonymousObservable,
					paramAnonymousObject))
			{
				paramAnonymousObservable.deleteObserver(this);
				paramAnonymousObservable
						.addObserver(MusicPlayerWrapper.this.dataChecker);
			}
		}
	};
	private Handler startStopHandler = new Handler()
	{
		public void handleMessage(Message message)
		{
			logger.v("handleMessage() ---> Enter : " + message.what);
			switch (message.what)
			{
			default:
				super.handleMessage(message);
				break;
			case 0:
				mSong = ((Song) message.obj);
				Message localMessage = mDispatcher.obtainMessage(
						DispatcherEventEnum.PLAYER_EVENT_CACHE_STRAT_PLAYING,
						mSong);
				mDispatcher.sendMessage(localMessage);
				startInternal((Song) message.obj);
				break;
			case 1:
				stopInternal();
				break;
			case 2:
				removeMessages(2);
				if (GlobalSettingParameter.useraccount == null)
					mDispatcher.sendMessage(mDispatcher.obtainMessage(1013));
				else
					mDispatcher.sendMessage(mDispatcher.obtainMessage(1013));
				break;
			}
			logger.v("handleMessage() ---> Exit");
		}
	};
	private volatile State state = State.END;

	public MusicPlayerWrapper(MobileMusicApplication paramMobileMusicApplication)
	{
		logger.v("MusicPlayerWrapper() ---> Enter");
		this.mApp = paramMobileMusicApplication;
		this.mDispatcher = paramMobileMusicApplication.getEventDispatcher();
		this.mTelephonyManager = ((TelephonyManager) paramMobileMusicApplication
				.getSystemService("phone"));
		this.mTelephonyManager.listen(new PhoneStateListener()
		{
			public void onCallStateChanged(int paramAnonymousInt,
					String paramAnonymousString)
			{
				switch (paramAnonymousInt)
				{
				default:
					break;
				case 1:
				case 2:
					if (state != State.PAUSED)
						interruptBy(2);
					break;
				case 0:
					resumeInterruptBy(2);
					break;
				}
			}
		}, 32);
		logger.v("MusicPlayerWrapper() ---> Exit");
	}

	private void autoPause()
	{
		try
		{
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				if (this.mAudioTrackPlay != null)
				{
					interruptBy(1);
					this.mDispatcher.removeMessages(1017);
					this.mDispatcher.removeMessages(1013);
					this.mDispatcher.sendMessage(this.mDispatcher
							.obtainMessage(1011));
				}
				interruptBy(1);
				this.mDispatcher.removeMessages(1017);
				this.mDispatcher.removeMessages(1013);
				this.mDispatcher.sendMessage(this.mDispatcher
						.obtainMessage(1011));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean checkLoaclMusicIsdobly()
	{
		DataSourceHandler localDataSourceHandler = this.localHandler;
		boolean bool1 = false;
		if (localDataSourceHandler != null)
		{
			int i = this.mSong.mMusicType;
			int j = MusicType.LOCALMUSIC.ordinal();
			bool1 = false;
			if (i == j)
			{
				Song localSong = this.mSong;
				bool1 = false;
				if (localSong != null)
				{
					boolean bool2 = this.mSong.isDolby;
					bool1 = false;
					if (bool2)
						bool1 = true;
				}
			}
		}
		return bool1;
	}

	private boolean checkOnline30Sec(int paramInt)
	{
		boolean bool = true;
		if ((GlobalSettingParameter.useraccount == null)
				|| (!Util.isOnlineMusic(getCurrentSong()))
				|| (!getCurrentSong().isDolby)
				|| (Integer.toString(3)
						.equals(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER))
				|| (Integer.toString(2)
						.equals(GlobalSettingParameter.SERVER_INIT_PARAM_MEMBER))
				|| ("1".equals(GlobalSettingParameter.SERVER_INIT_PARAM_ONLINE_MUSIC_ORDER_STATUS)))
		{
			this.mDispatcher.removeMessages(1017);
			this.mDispatcher.removeMessages(1013);
			this.startStopHandler.removeMessages(2);
			this.startStopHandler.sendEmptyMessage(2);
			bool = false;
		}
		return bool;
	}

	private boolean checkedLocalSongType()
	{
		Song localSong1 = this.mSong;
		boolean flag = false;
		if (localSong1 != null)
		{
			int i = this.mSong.mMusicType;
			int j = MusicType.LOCALMUSIC.ordinal();
			if (i == j)
			{
				boolean flag1 = mSong.isDolby;
				if (flag1)
					flag = true;
			}
		}
		return flag;
	}

	private void doComplete(DataSourceHandler paramDataSourceHandler)
	{
		int i;
		try
		{
			logger.v("doComplete() ---> Enter");
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				logger.e("dolbymobile3 --> doComplete");
				this.mDispatcher.removeMessages(1013);
				this.state = State.COMPLETED;
				stopInternal();
				this.mDispatcher.sendMessage(this.mDispatcher
						.obtainMessage(1002));
				return;
			}
			this.mDispatcher.removeMessages(1017);
			this.mDispatcher.removeMessages(1013);
			this.state = State.COMPLETED;
			i = getCurrentPosition();
			if (!paramDataSourceHandler.isEnd())
			{
				paramDataSourceHandler.registerObserver(this, i);
			}
			if (paramDataSourceHandler.getBytes() > this.currentSongBytesPlayed)
			{
				this.currentSongMilliSecPlayed = i;
				doOpen(paramDataSourceHandler, this.currentSongMilliSecPlayed);
			} else if (paramDataSourceHandler.isError())
			{
				logger.v("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				doOnError(1, -102);
			} else
			{
				stopInternal();
				this.mDispatcher.sendMessage(this.mDispatcher
						.obtainMessage(1002));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		logger.v("doComplete() ---> Exit");
	}

	private void doOnError(int paramInt1, int paramInt2)
	{
		logger.v("doOnError ----> enter");
		try
		{
			this.state = State.ERROR;
			this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(1004,
					paramInt1, paramInt2, null));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.v("doOnError");
	}

	private void doOnPrepared(int paramInt)
	{
		try
		{
			this.state = State.PREPARED;
			this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(1003));
			doStart(paramInt);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void doOpen(final DataSourceHandler handler, final int msec)
	{
		String s;
		logger.v("doOpen() ---> Enter");
		s = handler.getAbsoluteFilePath();
		if (s != null)
		{
			if (player != null && player.isPlaying())
			{
				player.stop();
				player = null;
			} else
			{
				player = new MediaPlayer();
				state = State.IDLE;
				player.setWakeMode(mApp, 1);
				player.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener()
				{

					public boolean onError(MediaPlayer mediaplayer, int i, int j)
					{
						return true;
					}
				});
			}
			try
			{
				if (!s.startsWith("content://"))
				{
					if (currentSongDuration <= 0)
					{
						DataInputStream datainputstream = new DataInputStream(
								new FileInputStream(s));
						currentSongDuration = PlayerID3V2Parser.getInstance()
								.parseDuration(datainputstream);
						datainputstream.close();
					}
					if (currentSongDuration > 0)
					{
						if (Util.isOnlineMusic(currentSong))
						{
							player.setDataSource((new RandomAccessFile(s, "r"))
									.getFD());
						} else
						{
							player.setDataSource(s);
						}
					} else
					{
						long l = 1000L * (new File(s)).length();
						if (!"0".equals(GlobalSettingParameter.SERVER_INIT_PARAM_BITERATE))
						{
							if ("1".equals(GlobalSettingParameter.SERVER_INIT_PARAM_BITERATE))
							{
								currentSongDuration = (int) (l / 5120L);
								if (s.startsWith(mApp.getApplicationInfo().dataDir))
								{
									player.setDataSource((new RandomAccessFile(
											s, "r")).getFD());
								} else
								{
									player.setDataSource(s);
								}
							} else
							{
								if (Util.isOnlineMusic(currentSong))
								{
									player.setDataSource((new RandomAccessFile(
											s, "r")).getFD());
								} else
								{
									player.setDataSource(s);
								}
							}
						} else
						{
							currentSongDuration = (int) (l / 2048L);
						}
					}
				} else
				{
					player.setDataSource(mApp, Uri.parse(s));
				}

				currentSongBytesPlayed = currentHandler.getBytes();
				state = State.INITIALIZED;
				player.setAudioStreamType(3);
				player.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener()
				{
					public void onPrepared(MediaPlayer mediaplayer)
					{
						doOnPrepared(msec);
					}
				});
				player.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener()
				{
					public void onCompletion(MediaPlayer mediaplayer)
					{
						doComplete(handler);
					}

				});
				player.prepareAsync();
				state = State.PREPARING;
				logger.v("doOpen() ---> Exit");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			logger.v("******************************************************************************");
			// mDispatcher
			// .sendMessage(mDispatcher.obtainMessage(
			// DispatcherEventEnum.PLAYER_EVENT_ERROR_OCCURED, 0,
			// 0, null));
			/**
			 * 就是因为这个发送错误信息代码导致程序死机。。。
			 */
			if (player != null && player.isPlaying())
			{
				player.stop();
				player = null;
			} else
			{
				player = new MediaPlayer();
				state = State.IDLE;
				player.setWakeMode(mApp, 1);
				player.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener()
				{

					public boolean onError(MediaPlayer mediaplayer, int i, int j)
					{
						return true;
					}
				});
			}
			try
			{
				player.setDataSource(mApp, Uri.parse(currentSong.mUrl));
				currentSongBytesPlayed = currentHandler.getBytes();
				state = State.INITIALIZED;
				player.setAudioStreamType(3);
				player.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener()
				{
					public void onPrepared(MediaPlayer mediaplayer)
					{
						doOnPrepared(msec);
					}
				});
				player.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener()
				{
					public void onCompletion(MediaPlayer mediaplayer)
					{
						doComplete(handler);
					}

				});
				player.prepareAsync();
				state = State.PREPARING;
				logger.v("doOpen() ---> Exit");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void doStart(int paramInt)
	{
		try
		{
			logger.v("doStart() ---> Enter");
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				if (this.mAudioTrackPlay != null)
				{
					resumeInterrupt();
					return;
				}
			}
			if (this.player != null)
			{
				State[] arrayOfState = new State[5];
				arrayOfState[0] = State.PREPARED;
				arrayOfState[1] = State.STARTED;
				arrayOfState[2] = State.PAUSED;
				arrayOfState[3] = State.STOPPED;
				arrayOfState[4] = State.COMPLETED;
				if (inStates(arrayOfState))
				{
					if (paramInt > 0)
						seekTo(paramInt);
				}
			}
			resumeInterrupt();
			logger.v("doStart() ---> Exit");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean doUpdate(Observable paramObservable, Object paramObject)
	{
		boolean bool1 = true;
		boolean bool3;
		final DataSourceHandler localDataSourceHandler = (DataSourceHandler) paramObject;
		try
		{
			logger.v("doUpdate() ---> Enter");
			boolean bool2 = localDataSourceHandler.isEnd();
			long l = localDataSourceHandler.getBytes();
			this.localHandler = localDataSourceHandler;
			int i;
			if (this.mSong.isDolby)
			{
				i = 102400;
				if (!ConfigSettingParameter.LOCAL_PARAM_DEVICE_IS_MOTOROLA_MT870_CMCC)
					if ((bool2)
							&& (localDataSourceHandler.getPercent() >= 1.0F))
					{
						bool3 = bool1;
						if (bool3)
							if (!isInterruptedBy(4))
								resumeInterruptBy(4);
						return bool1;
					}
			} else
			{
				i = 81920;
			}
			bool3 = false;
			if (((bool2) && (l > this.currentSongBytesPlayed))
					|| (l - this.currentSongBytesPlayed >= i))
				bool3 = false;
			if ((checkLoaclMusicIsdobly())
					|| ((this.mSong != null) && (this.mSong.isDolby) && ((this.mSong.mMusicType == MusicType.ONLINEMUSIC
							.ordinal()) || (this.mSong.mMusicType == MusicType.RADIO
							.ordinal()))))
			{
				if (PlayerControllerImpl.getInstance(this.mApp)
						.getCurrentPlayingItem() != null)
				{
					PlayerControllerImpl.getInstance(this.mApp)
							.getCurrentPlayingItem().isDolby = true;
					this.mSong.isDolby = true;
				}
				this.state = State.IDLE;
				this.mAudioTrackPlay = AudioTrackPlayThread
						.startDolbyThread(this.currentHandler);
				this.mAudioTrackPlay
						.setOnPreparedListener(new AudioTrackPlayThread.OnPreparedListener()
						{
							public void onPrepared()
							{
								MusicPlayerWrapper.this.doOnPrepared(0);
							}
						});
				this.mAudioTrackPlay
						.setOnCompletionListener(new AudioTrackPlayThread.OnCompletionListener()
						{
							public void onCompletion()
							{
								MusicPlayerWrapper.this
										.doComplete(localDataSourceHandler);
							}
						});
				this.mAudioTrackPlay.start();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.v("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		doOpen(localDataSourceHandler, this.currentSongMilliSecPlayed);
		logger.v("doUpdate() ---> Exit");
		return bool1;
	}

	private Song getCurrentSong()
	{
		Song song = new Song();
		try
		{
			song = this.currentSong;

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return song;
	}

	private boolean inStates(State[] paramArrayOfState)
	{
		boolean bool = false;
		int i = paramArrayOfState.length;
		for (int j = 0; j < i; j++)
		{
			State localState = paramArrayOfState[j];
			if (this.state == localState)
				bool = true;
		}
		return bool;
	}

	private void interrupt()
	{
		try
		{
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				if (this.mAudioTrackPlay != null)
				{
					this.mAudioTrackPlay.pause();
					this.state = State.PAUSED;
					this.mDispatcher.removeMessages(1013);
				}
			} else
			{
				if (this.player != null)
				{
					State[] arrayOfState = new State[2];
					arrayOfState[0] = State.STARTED;
					arrayOfState[1] = State.PAUSED;
					if ((inStates(arrayOfState))
							&& (isInterruptedBy(INTERRUPT_ANY)))
					{
						this.player.pause();
						this.state = State.PAUSED;
						this.mDispatcher.removeMessages(1017);
						this.mDispatcher.removeMessages(1013);
						this.mDispatcher.sendMessage(this.mDispatcher
								.obtainMessage(1011));
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void interruptBy(int paramInt)
	{
		try
		{
			this.interruptState = (paramInt | this.interruptState);
			interrupt();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean isInterruptedBy(int paramInt)
	{
		boolean bool = false;
		try
		{
			int i = this.interruptState;
			if ((i & paramInt) != 0)
			{
				bool = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool;
	}

	private void openDuby()
	{
		try
		{
			this.state = State.IDLE;
			this.mAudioTrackPlay = AudioTrackPlayThread
					.startDolbyThread(this.currentHandler);
			this.mAudioTrackPlay
					.setOnPreparedListener(new AudioTrackPlayThread.OnPreparedListener()
					{
						public void onPrepared()
						{
							MusicPlayerWrapper.this.doOnPrepared(0);
						}
					});
			this.mAudioTrackPlay
					.setOnCompletionListener(new AudioTrackPlayThread.OnCompletionListener()
					{
						public void onCompletion()
						{
							MusicPlayerWrapper.this
									.doComplete(MusicPlayerWrapper.this.currentHandler);
						}
					});
			this.mAudioTrackPlay.start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void resumeInterrupt()
	{
		logger.v("resumeInterrupt() ---> Enter");
		AudioManager localAudioManager = (AudioManager) this.mApp
				.getApplicationContext().getSystemService("audio");
		try
		{
			if ((this.mSong != null)
					&& ((checkedLocalSongType()) || ((this.mSong != null) && (this.mSong.isDolby))))
			{
				if ((!isInterruptedBy(7)) && (this.mAudioTrackPlay != null))
				{
					// 请求音频焦点
					localAudioManager.requestAudioFocus(
							this.mAudioFocusListener, 3, 1);
					this.mApp.sendBroadcast(new Intent(
							"cmccwm.mobilemusic.action.PLAYER_START"));
					this.mAudioTrackPlay.dostart();
					this.state = State.STARTED;
					this.mDispatcher.sendMessage(this.mDispatcher
							.obtainMessage(1010));
					logger.v("resumeInterrupt() ---> Exit");
					return;
				}
			}
			if (this.player != null)
			{
				State[] arrayOfState = new State[5];
				arrayOfState[0] = State.PREPARED;
				arrayOfState[1] = State.STARTED;
				arrayOfState[2] = State.PAUSED;
				arrayOfState[3] = State.STOPPED;
				arrayOfState[4] = State.COMPLETED;
				if (inStates(arrayOfState))
				{
					if (!isInterruptedBy(INTERRUPT_ANY))
					{
						localAudioManager.requestAudioFocus(
								this.mAudioFocusListener, 3, 1);
						this.mApp.sendBroadcast(new Intent(
								"cmccwm.mobilemusic.action.PLAYER_START"));
						this.player.start();
						this.state = State.STARTED;
						this.mDispatcher.sendMessage(this.mDispatcher
								.obtainMessage(1010));
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		localAudioManager.requestAudioFocus(this.mAudioFocusListener, 3, 1);
	}

	private void resumeInterruptBy(int paramInt)
	{
		this.interruptState &= (paramInt ^ 0xFFFFFFFF);
		resumeInterrupt();
	}

	private void startInternal(Song song)
	{
		boolean flag;
		logger.v("startInternal() ---> Enter");
		flag = isInterruptedBy(2);
		if (!flag && song != null)
		{
			stopInternal();
			if (((song != null) && (this.currentSong == null))
					|| ((song == null) && (this.currentSong != null))
					|| ((song != null) && (this.currentSong != null) && (!song.mUrl
							.equals(this.currentSong.mUrl))))
			{
				logger.v("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
				currentHandler = null;
			}
			currentSong = song;
			currentSongDuration = currentSong.mDuration;
			currentSongBytesPlayed = 0L;
			currentSongMilliSecPlayed = 0;
			state = State.INITIALING;
			interruptState = 0;
			mDispatcher
					.sendMessage(mDispatcher
							.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_PREPARE_START));
			if ((Util.isOnlineMusic(this.currentSong))
					&& (!((NetUtil.netState != 3) || (SystemControllerImpl
							.getInstance(this.mApp).checkWapStatus())) && (NetUtil
							.isConnection())))
			{
				mApp.getEventDispatcher().sendMessage(
						mApp.getEventDispatcher().obtainMessage(
								DispatcherEventEnum.PLAYER_EVENT_WAP_CLOSED));
				doOnError(1, -102);
			}

			if (currentHandler != null)
			{
				localHandler = currentHandler;
				if (currentHandler.getObervable() != null)
					currentHandler.getObervable().deleteObservers();
				if (!currentHandler.isEnd())
				{
					currentHandler.getObervable().addObserver(startChecker);
				} else
				{
					currentHandler.getObervable().addObserver(dataChecker);
					if (checkLoaclMusicIsdobly())
					{
						if (PlayerControllerImpl.getInstance(mApp)
								.getCurrentPlayingItem() != null)
						{
							PlayerControllerImpl.getInstance(mApp)
									.getCurrentPlayingItem().isDolby = true;
							mSong.isDolby = true;
						}
						openDuby();
					} else
					{
						mSong.isDolby = false;
						logger.v("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
						doOpen(currentHandler, 0);
					}
				}
			} else
			{
				logger.v(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				currentHandler = DataSourceHandler
						.startHandle(song, this, mApp);
				logger.v("currentHandler = " + currentHandler);
				doOpen(currentHandler, 0);
			}
			logger.v("startInternal() ---> Exit");
		}
	}

	public void addPlayObserver(DataSourceHandler paramDataSourceHandler,
			int paramInt)
	{
		logger.v("addPlayObserver() ---> Exit");
		this.currentSongMilliSecPlayed = paramInt;
		if (this.currentSongMilliSecPlayed == 0)
			this.currentSongBytesPlayed = 0L;
		paramDataSourceHandler.getObervable().addObserver(this.startChecker);
		logger.v("addPlayObserver() ---> Exit");
	}

	public void close()
	{
		DataSourceHandler.close();
	}

	public int getCurrentPosition()
	{
		int i = 0;
		try
		{
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				if (this.mAudioTrackPlay != null)
				{
					State[] arrayOfState1 = new State[7];
					arrayOfState1[0] = State.IDLE;
					arrayOfState1[1] = State.INITIALIZED;
					arrayOfState1[2] = State.PREPARED;
					arrayOfState1[3] = State.STARTED;
					arrayOfState1[4] = State.PAUSED;
					arrayOfState1[5] = State.STOPPED;
					arrayOfState1[6] = State.COMPLETED;
					if (inStates(arrayOfState1))
						this.currentSongMilliSecPlayed = this.mAudioTrackPlay
								.getCurrentPosition();
				}
				logger.v("getCurrentPosition() ---> Exit");
			}

			if (this.player != null)
			{
				State[] arrayOfState2 = new State[7];
				arrayOfState2[0] = State.IDLE;
				arrayOfState2[1] = State.INITIALIZED;
				arrayOfState2[2] = State.PREPARED;
				arrayOfState2[3] = State.STARTED;
				arrayOfState2[4] = State.PAUSED;
				arrayOfState2[5] = State.STOPPED;
				arrayOfState2[6] = State.COMPLETED;
				if (inStates(arrayOfState2))
					this.currentSongMilliSecPlayed = this.player
							.getCurrentPosition();

				i = this.currentSongMilliSecPlayed;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}

	public int getDuration()
	{
		int i = 0;
		try
		{
			logger.v("getDuration() ---> Enter");
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				if (this.mAudioTrackPlay != null)
				{
					State[] arrayOfState1 = new State[8];
					arrayOfState1[0] = State.IDLE;
					arrayOfState1[1] = State.INITIALIZED;
					arrayOfState1[2] = State.PREPARED;
					arrayOfState1[3] = State.STARTED;
					arrayOfState1[4] = State.PAUSED;
					arrayOfState1[5] = State.STOPPED;
					arrayOfState1[6] = State.COMPLETED;
					arrayOfState1[7] = State.SEEKING;
					if (inStates(arrayOfState1))
						this.currentSongDuration = this.mAudioTrackPlay
								.getDuration();
				}
			}

			if ((this.currentSongDuration <= 0) && (this.player != null))
			{
				State[] arrayOfState2 = new State[6];
				arrayOfState2[0] = State.PREPARED;
				arrayOfState2[1] = State.STARTED;
				arrayOfState2[2] = State.PAUSED;
				arrayOfState2[3] = State.STOPPED;
				arrayOfState2[4] = State.COMPLETED;
				arrayOfState2[5] = State.SEEKING;
				if (inStates(arrayOfState2))
					this.currentSongDuration = this.player.getDuration();
			}
			if (this.currentSongDuration > 900000)
				this.currentSongDuration = this.player.getDuration();

			i = this.currentSongDuration;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.v("getDuration() ---> Exit");
		return i;
	}

	public float getPercent()
	{
		float f = 0;
		if (this.currentHandler != null)
			f = this.currentHandler.getPercent();
		return f;
	}

	/**
	 * 检查文件路径是否是在外部存储，如SD卡
	 * 
	 * @return
	 */
	public boolean isFileOnExternalStorage()
	{
		boolean bool = false;
		if ((this.currentHandler != null)
				&& (this.currentHandler.getAbsoluteFilePath() != null)
				&& (this.currentHandler.getAbsoluteFilePath()
						.startsWith(Environment.getExternalStorageDirectory()
								.getAbsolutePath())))
		{
			bool = true;
		}
		return bool;
	}

	public boolean isInitialized()
	{
		boolean bool1 = true;
		try
		{
			if (!isPlaying())
			{
				boolean bool2 = isInterruptedBy(INTERRUPT_ANY);
				if (!bool2)
				{
					bool1 = false;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool1;
	}

	public boolean isInteruptByCall()
	{
		logger.d("isInteruptByCall ----> enter");
		boolean bool = false;
		try
		{
			bool = isInterruptedBy(2);
			logger.d("return ----> " + bool);
			logger.d("isInteruptByCall ----> exit");
			return bool;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool;
	}

	public boolean isLooping()
	{
		boolean bool1 = true;
		try
		{
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
				if (this.mAudioTrackPlay != null)
				{
					boolean bool2 = this.mAudioTrackPlay.isLooping();
					if (!bool2)
						;
				}
			bool1 = false;
			if (this.player != null)
			{
				boolean bool3 = this.player.isLooping();
				if (bool3)
					bool1 = true;
			} else
			{
				bool1 = false;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool1;
	}

	public boolean isPaused()
	{
		boolean bool1 = true;
		try
		{
			Log.v("TAG", "state = " + this.state);
			if (isInterruptedBy(1))
			{
				State[] arrayOfState = new State[3];
				arrayOfState[0] = State.STOPPED;
				arrayOfState[1] = State.COMPLETED;
				arrayOfState[2] = State.END;
				boolean bool2 = inStates(arrayOfState);
				if (!bool2)
					return bool1;
			}
			bool1 = false;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool1;
	}

	public boolean isPlaying()
	{
		logger.v("isPlaying() ----> enter");
		boolean bool1 = true;
		try
		{
			if ((this.mSong != null)
					&& ((checkedLocalSongType()) || ((this.mSong != null) && (this.mSong.isDolby))))
			{
				logger.v("this.mSong != null");
				if (this.mAudioTrackPlay != null)
				{
					boolean bool3 = this.mAudioTrackPlay.isPlaying();
					if (!bool3)
					{
						bool1 = false;
					} else
					{
						return bool1;
					}
				}
			} else
			{
				boolean bool2;
				if (this.player != null)
				{
					State[] arrayOfState = new State[8];
					arrayOfState[0] = State.IDLE;
					arrayOfState[1] = State.INITIALIZED;
					arrayOfState[2] = State.PREPARED;
					arrayOfState[3] = State.STARTED;
					arrayOfState[4] = State.PAUSED;
					arrayOfState[5] = State.STOPPED;
					arrayOfState[6] = State.COMPLETED;
					arrayOfState[7] = State.SEEKING;
					if (inStates(arrayOfState))
					{
						bool2 = this.player.isPlaying();
						bool1 = bool2;
					}
				} else
				{
					bool1 = false;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.v("return ---> " + bool1);
		logger.v("isPlaying() ----> exit");
		return bool1;
	}

	public void pause()
	{
		logger.v("pause() ----> enter");
		try
		{
			if ((checkedLocalSongType())
					|| ((this.mSong != null) && (this.mSong.isDolby)))
			{
				if (this.mAudioTrackPlay != null)
				{
					interruptBy(INTERRUPT_PAUSE);
					this.mDispatcher
							.removeMessages(DispatcherEventEnum.PLAYER_EVENT_NO_LOGIN_LISTEN);
					this.mDispatcher
							.removeMessages(DispatcherEventEnum.PLAYER_EVENT_NO_RIGHTS_LISTEN_ONLINE_LISTEN);
					this.mDispatcher
							.sendMessage(this.mDispatcher
									.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE));
				}
			} else
			{
				((AudioManager) this.mApp.getApplicationContext()
						.getSystemService("audio"))
						.abandonAudioFocus(this.mAudioFocusListener);
				interruptBy(INTERRUPT_PAUSE);
				this.mDispatcher
						.removeMessages(DispatcherEventEnum.PLAYER_EVENT_NO_LOGIN_LISTEN);
				this.mDispatcher
						.removeMessages(DispatcherEventEnum.PLAYER_EVENT_NO_RIGHTS_LISTEN_ONLINE_LISTEN);
				this.mDispatcher
						.sendMessage(this.mDispatcher
								.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_PAUSE));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.v("pause() ----> exit");
	}

	public void resume()
	{
		try
		{
			resumeInterruptBy(INTERRUPT_PAUSE);
			return;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void resumeOrRestart()
	{
		try
		{
			State[] arrayOfState = new State[1];
			arrayOfState[0] = State.END;
			if (inStates(arrayOfState))
			{
				if ((checkedLocalSongType())
						|| ((this.mSong != null) && (this.mSong.isDolby)))
				{
					if (this.mAudioTrackPlay == null)
					{
						this.state = State.IDLE;
						this.mAudioTrackPlay = AudioTrackPlayThread
								.startDolbyThread(this.currentHandler);
						this.mAudioTrackPlay
								.setOnPreparedListener(new AudioTrackPlayThread.OnPreparedListener()
								{
									public void onPrepared()
									{
										doOnPrepared(0);
									}
								});
						this.mAudioTrackPlay
								.setOnCompletionListener(new AudioTrackPlayThread.OnCompletionListener()
								{
									public void onCompletion()
									{
										doComplete(MusicPlayerWrapper.this.currentHandler);
									}
								});
						this.mAudioTrackPlay.start();
					}
				} else
				{

					start(this.currentSong);
				}
			} else
			{
				if (isInterruptedBy(INTERRUPT_PAUSE))
					resume();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void retryDowmload()
	{
		// if (((!isPlaying()) && (!isPaused()))
		// || ((this.currentHandler != null) && (this.currentHandler
		// .isDownloadSuccess())))
		// if (this.mSong != null)
		// this.currentHandler = DataSourceHandler.startHandle(this.mSong,
		// this, this.mApp);
	}

	public void seekTo(int paramInt)
	{
		try
		{
			logger.v("seekTo() ---> Enter");
			final State localState = this.state;
			if ((this.mSong != null)
					&& ((checkedLocalSongType()) || ((this.mSong != null) && (this.mSong.isDolby))))
			{
				State[] arrayOfState2 = new State[5];
				arrayOfState2[0] = State.PREPARED;
				arrayOfState2[1] = State.STARTED;
				arrayOfState2[2] = State.PAUSED;
				arrayOfState2[3] = State.STOPPED;
				arrayOfState2[4] = State.COMPLETED;
				if (inStates(arrayOfState2))
				{
					this.state = State.SEEKING;
					this.mAudioTrackPlay
							.setOnSeekCompleteListener(new AudioTrackPlayThread.OnSeekCompleteListener()
							{
								public void onSeekComplete()
								{
									MusicPlayerWrapper.this.state = localState;
									MusicPlayerWrapper.this.resumeInterrupt();
								}
							});
					this.mAudioTrackPlay.seekto(paramInt);
					logger.e("dolbymobile3 --> Seek to=" + paramInt);
				}
				if (this.player != null)
				{
					State[] arrayOfState1 = new State[5];
					arrayOfState1[0] = State.PREPARED;
					arrayOfState1[1] = State.STARTED;
					arrayOfState1[2] = State.PAUSED;
					arrayOfState1[3] = State.STOPPED;
					arrayOfState1[4] = State.COMPLETED;
					if (inStates(arrayOfState1))
					{
						this.state = State.SEEKING;
						this.player
								.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener()
								{
									public void onSeekComplete(
											MediaPlayer paramAnonymousMediaPlayer)
									{
										MusicPlayerWrapper.this.state = localState;
										MusicPlayerWrapper.this
												.resumeInterrupt();
									}
								});
						this.player.seekTo(paramInt);
					}
				}
			}
			logger.v("seekTo() ---> Exit");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void start(Song paramSong)
	{
		logger.v("start() ---> Enter");
		startStopHandler.removeMessages(1);
		startStopHandler.removeMessages(0);
		Message localMessage = Message.obtain(startStopHandler, 0, paramSong);
		startStopHandler.sendMessage(localMessage);
		logger.v("start() ---> Exit");
	}

	public void stop()
	{
		logger.v("stop() ---> Enter");
		this.startStopHandler.removeMessages(1);
		this.startStopHandler.removeMessages(0);
		Message.obtain(this.startStopHandler, 1).sendToTarget();
		logger.v("stop() ---> Exit");
	}

	public void stopInternal()
	{
		try
		{
			logger.v("stopInternal() ---> Enter");
			if (this.mAudioTrackPlay != null)
			{
				this.mAudioTrackPlay.stopPlay();
				this.mAudioTrackPlay = null;
				this.state = State.STOPPED;
			}
			if ((this.currentHandler != null)
					&& (this.currentHandler.getObervable() != null))
				this.currentHandler.getObervable().deleteObservers();
			if (this.player != null)
			{
				State[] arrayOfState = new State[5];
				arrayOfState[0] = State.PREPARED;
				arrayOfState[1] = State.STARTED;
				arrayOfState[2] = State.PAUSED;
				arrayOfState[3] = State.STOPPED;
				arrayOfState[4] = State.COMPLETED;
				if (inStates(arrayOfState))
				{
					this.player.stop();
					this.player.release();
					this.player = null;
					this.state = State.STOPPED;
				}
			}
			this.currentSongBytesPlayed = 0L;
			this.currentSongMilliSecPlayed = 0;
			this.startStopHandler.removeMessages(2);
			this.mDispatcher.removeMessages(1017);
			this.mDispatcher.removeMessages(1013);
			this.mDispatcher
					.sendMessage(this.mDispatcher
							.obtainMessage(DispatcherEventEnum.PLAYER_EVENT_PLAYBACK_STOP));
			this.isAbandonAudioFocus = true;
			this.state = State.END;
			((AudioManager) this.mApp.getApplicationContext().getSystemService(
					"audio")).abandonAudioFocus(this.mAudioFocusListener);
			logger.v("stopInternal() ---> Exit");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public enum State
	{
		PREPARED("PREPARED", 4), STARTED("STARTED", 5), SEEKING("SEEKING", 6), PAUSED(
				"PAUSED", 7), STOPPED("STOPPED", 8), COMPLETED("COMPLETED", 9), ERROR(
				"ERROR", 10), END("END", 11), IDLE("IDLE", 0), INITIALIZED(
				"INITIALIZED", 1), INITIALING("INITIALING", 2), PREPARING(
				"PREPARING", 3);
		private String name;
		private int id;

		State(String name, int id)
		{
			this.name = name;
			this.id = id;
		}

	}
}