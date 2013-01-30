package org.ming.center.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.ming.center.MobileMusicApplication;
import org.ming.dispatcher.Dispatcher;
import org.ming.util.MyLogger;

public class AudioTrackPlayThread extends Thread
{
	private static ArrayList<AudioTrackPlayThread> handlers = new ArrayList();
	private static ReentrantLock handlersLock;
	private static final MyLogger logger = MyLogger
			.getLogger("AudioTrackPlayThread");
	private static ArrayList<ReadFileThread> readthreadlist = new ArrayList();
	final int EVENT_PLAY_OVER = 256;
	List<byte[]> backdatalist = new ArrayList();
	List<byte[]> datalist = new ArrayList();
	boolean isLooping = false;
	volatile boolean isPlaying = false;
	private final DataSourceHandler mDataSourceHandler;
	private Dispatcher mDispatcher;
	// private DolbyUtils mDolbyUtils = null;
	private int mDuration = 0;
	private OnCompletionListener mOnCompletionListener;
	private OnPreparedListener mOnPreparedListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;
	private boolean mParseComplete = false;
	private PlayerAudioTrack mPlayerAudioTrack;
	private int mSamplerate;
	private int mSeekSampleNumber = -1;
	int playSize;
	private int seektime = 0;
	protected volatile boolean stopped = false;

	static
	{
		handlersLock = new ReentrantLock();
	}

	public AudioTrackPlayThread(DataSourceHandler paramDataSourceHandler)
	{
		this.mDataSourceHandler = paramDataSourceHandler;
		this.mDispatcher = MobileMusicApplication.getInstance()
				.getEventDispatcher();
	}

	public static AudioTrackPlayThread startDolbyThread(
			DataSourceHandler datasourcehandler)
	{
		// int i;
		// handlersLock.lock();
		// i = 0;
		// _L7:
		// if(i < handlers.size()) goto _L2; else goto _L1
		// _L1:
		// int j;
		// handlers.clear();
		// j = 0;
		// _L3:
		// if(j < readthreadlist.size())
		// break MISSING_BLOCK_LABEL_95;
		// readthreadlist.clear();
		// AudioTrackPlayThread audiotrackplaythread;
		// try
		// {
		// Thread.sleep(800L);
		// }
		// catch(InterruptedException interruptedexception)
		// {
		// interruptedexception.printStackTrace();
		// }
		// audiotrackplaythread = new AudioTrackPlayThread(datasourcehandler);
		// handlers.add(audiotrackplaythread);
		// handlersLock.unlock();
		// return audiotrackplaythread;
		// _L2:
		// ((AudioTrackPlayThread)handlers.get(i)).stopThread();
		// i++;
		// continue; /* Loop/switch isn't completed */
		// ((ReadFileThread)readthreadlist.get(j)).stopThread();
		// j++;
		// goto _L3
		// Exception exception;
		// exception;
		// _L5:
		// handlersLock.unlock();
		// throw exception;
		// exception;
		// if(true) goto _L5; else goto _L4
		// _L4:
		// if(true) goto _L7; else goto _L6
		// _L6:
		return null;
	}

	private void stopThread()
	{
		if (this.stopped)
			if (isAlive())
			{
				this.stopped = true;
				interrupt();
			}
	}

	public void dostart()
	{
		if (this.mPlayerAudioTrack != null)
		{
			this.isPlaying = true;
			this.mPlayerAudioTrack.play();
		}
	}

	public int getCurrentPosition()
	{
		PlayerAudioTrack localPlayerAudioTrack = this.mPlayerAudioTrack;
		int i = 0;
		if (localPlayerAudioTrack != null)
			i = this.mPlayerAudioTrack.getCurrentPosition() + this.seektime;
		return i;
	}

	public int getDuration()
	{
		return this.mDuration;
	}

	public boolean isLooping()
	{
		return this.isLooping;
	}

	public boolean isPlaying()
	{
		return this.isPlaying;
	}

	public void pause()
	{
		if (this.mPlayerAudioTrack != null)
		{
			this.isPlaying = false;
			this.mPlayerAudioTrack.pause();
		}
	}

	public void run()
	{
		// logger.i("dolbymobile3 --> AudioTrackPlayThread run..");
		// Process.setThreadPriority(-19);
		// this.mDolbyUtils = new DolbyUtils();
		// if ((this.mDolbyUtils.startParse(this.mDataSourceHandler
		// .getAbsoluteFilePath()) != 0)
		// || (!"China Mobile".equalsIgnoreCase(this.mDolbyUtils
		// .getDistributor()))) {
		// logger.i("dolbymobile3 --> Parse dolby header ERROR");
		// this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(1019,
		// 1, -100, null));
		// this.mDolbyUtils = null;
		// }
		// this.datalist.clear();
		// this.seektime = 0;
		// this.mSamplerate = this.mDolbyUtils.DM3getSamplerate();
		// this.mDuration = this.mDolbyUtils.DM3getDuration();
		// logger.i("dolbymobile3 --> getDuration=" + this.mDuration);
		// try {
		// this.mPlayerAudioTrack = new PlayerAudioTrack(this.mSamplerate, 3,
		// 2);
		// this.mPlayerAudioTrack.init();
		// this.mPlayerAudioTrack.play();
		// this.playSize = this.mPlayerAudioTrack.getPrimePlaySize();
		// this.isPlaying = true;
		// this.mDispatcher.sendMessage(this.mDispatcher.obtainMessage(1009));
		// this.mParseComplete = false;
		// localReadFileThread = new ReadFileThread();
		// localReadFileThread.start();
		// readthreadlist.add(localReadFileThread);
		// this.mOnPreparedListener.onPrepared();
		// for (int i = 0;; i++)
		// try {
		// while (true) {
		// Thread.sleep(0L);
		// if (((this.datalist.size() != 0) && (this.isPlaying))
		// || (this.stopped) || (this.mParseComplete))
		// break;
		// Thread.sleep(200L);
		// }
		// } catch (Exception localException2) {
		// if (this.datalist.size() > 0) {
		// byte[] arrayOfByte1 = (byte[]) this.datalist.remove(0);
		// this.backdatalist.add(arrayOfByte1);
		// }
		// logger.e("dolbymobile3 --> ppplayer error" + i);
		// }
		// } catch (Exception localException1) {
		// ReadFileThread localReadFileThread;
		// localException1.printStackTrace();
		// if (this.mParseComplete) {
		// int j = this.datalist.size();
		// if (j != 0)
		// ;
		// }
		// this.isPlaying = false;
		// if (this.datalist.size() > 0)
		// this.datalist.clear();
		// if ((this.mOnCompletionListener != null) && (this.mParseComplete)
		// && (this.datalist.size() == 0) && (!this.stopped))
		// this.mOnCompletionListener.onCompletion();
		// localReadFileThread.interrupt();
		// this.mPlayerAudioTrack.release();
		// this.backdatalist = null;
		// this.datalist = null;
		// logger.i("dolbymobile3 --> dumi player thread exit");
		// break;
		// if (!this.stopped)
		// break label529;
		// logger.e("dolbymobile3 --> duby thread exit");
		// }
		// this.mPlayerAudioTrack.playAudioTrack((byte[]) this.datalist.get(0),
		// 0,
		// ((byte[]) this.datalist.get(0)).length);
		// byte[] arrayOfByte2 = (byte[]) this.datalist.remove(0);
		// this.backdatalist.add(arrayOfByte2);
	}

	public void seekto(int paramInt)
	{
		// this.mSeekSampleNumber = this.mDolbyUtils
		// .DM3getSampleNumberByTime(paramInt);
		this.mPlayerAudioTrack.stop();
		this.datalist.clear();
		this.seektime = (paramInt - this.mPlayerAudioTrack.getCurrentPosition());
		if (this.mOnSeekCompleteListener != null)
			this.mOnSeekCompleteListener.onSeekComplete();
	}

	public void setOnCompletionListener(
			OnCompletionListener paramOnCompletionListener)
	{
		this.mOnCompletionListener = paramOnCompletionListener;
	}

	public void setOnPreparedListener(OnPreparedListener paramOnPreparedListener)
	{
		this.mOnPreparedListener = paramOnPreparedListener;
	}

	public void setOnSeekCompleteListener(
			OnSeekCompleteListener paramOnSeekCompleteListener)
	{
		this.mOnSeekCompleteListener = paramOnSeekCompleteListener;
	}

	public void stopPlay()
	{
		logger.e("dolbymobile3 --> stop");
		System.gc();
		this.seektime = 0;
		this.mDuration = 0;
		this.isPlaying = false;
		this.stopped = true;
	}

	public static abstract interface OnCompletionListener
	{
		public abstract void onCompletion();
	}

	public static abstract interface OnPreparedListener
	{
		public abstract void onPrepared();
	}

	public static abstract interface OnSeekCompleteListener
	{
		public abstract void onSeekComplete();
	}

	private class ReadFileThread extends Thread
	{
		protected volatile boolean readFilestopped = false;

		private ReadFileThread()
		{}

		private void stopThread()
		{
			if (this.readFilestopped)
				if (isAlive())
				{
					this.readFilestopped = true;
					interrupt();
				}
		}
	}
}