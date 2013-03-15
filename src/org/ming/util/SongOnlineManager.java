package org.ming.util;

import java.util.ArrayList;

import org.ming.ui.activity.online.SongMusicInfo;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.cm.omp.fuction.InitCmOmpInterface;
import com.cm.omp.fuction.MusicQueryInterface;
import com.cm.omp.fuction.OnlineListenerMusicInterface;
import com.cm.omp.fuction.data.ChartInfo;
import com.cm.omp.fuction.data.CodeMessageObject;
import com.cm.omp.fuction.data.MusicInfo;

/**
 * 
 * @author jack.long
 * 
 *         彩铃专区接口的管理类
 */
public class SongOnlineManager
{
	public static final int BANG_TYPE = 1;// 榜单歌曲信息
	public static final int ALBUM_TYPE = 2;// 专辑歌曲信息
	public static final int TAG_TYPE = 3;// 标签歌曲信息
	public static final int RING_SONG_WEEK_BANG = 5;// 周热销榜
	public static final int RING_SONG_MOUTH_BANG = 6;// 月热销榜
	public static final int RING_SONG_TOTAL_BNAG = 7;// 总热销榜

	public static final int SONG_PAGE_SIZE = 30;
	// 获取信息成功
	public static final String GET_MSG_SUCC = "000000";

	private static SongOnlineManager songOnlineManager;
	private ArrayList<ChartInfo> chartInfos = null;
	private static final MyLogger logger = MyLogger
			.getLogger("SongOnlineManager");

	private SongOnlineManager()
	{

	}

	public static SongOnlineManager getInstance()
	{
		if (songOnlineManager == null)
		{
			songOnlineManager = new SongOnlineManager();
		}
		return songOnlineManager;
	}

	// 判断SIM卡是否是移动的
	public boolean isCanUseSim(Activity context)
	{
		logger.v("isCanUseSim() ----> Enter");
		try
		{
			TelephonyManager mgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			if (TelephonyManager.SIM_STATE_READY == mgr.getSimState())
			{
				String operator = mgr.getSimOperator();
				if (operator != null)
				{
					if (operator.equals("46000") || operator.equals("46002")
							|| operator.equals("46007"))
					{
						logger.v("Can use Sim Card");
						logger.v("isCanUseSim() ----> Exit");
						return true;
					}
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取榜单信息
	 * 
	 * @param mContext
	 * @param pageNumber
	 * @param numberPerPage
	 *            每页条数，[0-30]
	 * @return Object [] 1.结果代码：(ArrayList<RingSongMusicInfo>)objs[0]榜单的歌曲信息
	 *         2.结果代码：(ArrayList<ChartInfo>)objs[1]榜单信息
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ChartInfo> getChartInfo(Context mContext, int pageNumber,
			int numberPerPage)
	{
		logger.v("getChartInfo() ----> Enter");
		if (!NetUtil.isConnection())
		{
			logger.v("No Network Connection");
			Toast.makeText(mContext, "没有网络连接", Toast.LENGTH_SHORT).show();
		} else
		{
			if (chartInfos == null)
			{
				CodeMessageObject obj = MusicQueryInterface.getChartInfo(
						mContext, "1", SONG_PAGE_SIZE + "");
				if (obj.getCode().equals(GET_MSG_SUCC))
				{
					chartInfos = (ArrayList<ChartInfo>) obj.getObject();
				} else
				{
					logger.v("getMessage not success");
					logger.v("getChartInfo() ----> Exit");
					return null;
				}
			}
		}
		logger.v("getChartInfo() ----> Exit");
		return chartInfos;
	}

	/**
	 * 获取榜单歌曲信息
	 * 
	 * @param mContext
	 * @param chartCode
	 *            根据排行榜主类获取排行榜音乐信息
	 * @param pageNumber
	 * @param numberPerPage
	 * @return 1.结果代码：“000000”，获取数据成功
	 *         getObject()获得Object对象后强制转换为ArrayList<RingSongMusicInfo >后，可获取相关参数
	 *         2.结果代码：非“000000”，则获取数据失败，getMessage()获得对应结果说明。
	 * 
	 */
	public ArrayList<SongMusicInfo> getMusicInfoByChart(Context mContext,
			ChartInfo chartInfo, int pageNumber, int numberPerPage)
	{
		logger.v("getMusicInfoByChart() ----> Enter");
		ArrayList<SongMusicInfo> infos = null;
		if (!NetUtil.isConnection())
		{
			Toast.makeText(mContext, "没有网络连接", Toast.LENGTH_SHORT).show();
		} else
		{
			CodeMessageObject obj = MusicQueryInterface.getMusicInfoByChart(
					mContext, chartInfo.getChartCode(), pageNumber + "",
					numberPerPage + "");
			if (obj.getCode().equals(GET_MSG_SUCC))
			{
				logger.v("getMessage success");
				@SuppressWarnings("unchecked")
				ArrayList<MusicInfo> mcInfos = (ArrayList<MusicInfo>) obj
						.getObject();
				infos = new ArrayList<SongMusicInfo>();
				// 给每首歌曲添加试听地址
				for (int i = 0; i < mcInfos.size(); i++)
				{
					MusicInfo tempInfo = mcInfos.get(i);
					SongMusicInfo musicInfo = new SongMusicInfo(
							tempInfo.getMusicId(), tempInfo.getCount(),
							tempInfo.getCrbtValidity(), tempInfo.getPrice(),
							tempInfo.getSongName(), tempInfo.getSingerId(),
							tempInfo.getSingerName(), null, false);
					musicInfo.setChartName(chartInfo.getChartName());
					infos.add(musicInfo);
				}
			}
		}
		logger.v("getMusicInfoByChart() ----> Exit");
		return infos;
	}

	/**
	 * 获取榜单歌曲信息
	 * 
	 * @param mContext
	 * @param chartCode
	 *            根据排行榜代码获取排行榜音乐信息
	 * @param pageNumber
	 * @param numberPerPage
	 * @return 1.结果代码：“000000”，获取数据成功
	 *         getObject()获得Object对象后强制转换为ArrayList<RingSongMusicInfo >后，可获取相关参数
	 *         2.结果代码：非“000000”，则获取数据失败，getMessage()获得对应结果说明。
	 * 
	 */
	public ArrayList<SongMusicInfo> getMusicInfoByCode(Context mContext,
			String code, int pageNumber, int numberPerPage, String name)
	{
		logger.v("getMusicInfoByCode() ----> Enter");
		ArrayList<SongMusicInfo> infos = null;
		if (!NetUtil.isConnection())
		{
			Toast.makeText(mContext, "没有网络连接", Toast.LENGTH_SHORT).show();
		} else
		{
			CodeMessageObject obj = MusicQueryInterface.getMusicInfoByChart(
					mContext, code, pageNumber + "", numberPerPage + "");
			if (obj.getCode().equals(GET_MSG_SUCC))
			{
				logger.v("getMessage success");
				@SuppressWarnings("unchecked")
				ArrayList<MusicInfo> mcInfos = (ArrayList<MusicInfo>) obj
						.getObject();
				infos = new ArrayList<SongMusicInfo>();
				// 还没有添加歌曲的试听地址
				for (int i = 0; i < mcInfos.size(); i++)
				{
					MusicInfo tempInfo = mcInfos.get(i);
					Log.d("song***", "MusicId" + tempInfo.getMusicId());
					SongMusicInfo musicInfo = new SongMusicInfo(
							tempInfo.getMusicId(), tempInfo.getCount(),
							tempInfo.getCrbtValidity(), tempInfo.getPrice(),
							tempInfo.getSongName(), tempInfo.getSingerId(),
							tempInfo.getSingerName(), null, false);
					musicInfo.setChartName(name);
					infos.add(musicInfo);
				}
			}
		}
		logger.v("getMusicInfoByCode() ----> Exit");
		return infos;
	}

	/**
	 * 初始化 获取必要验证参数，只有当初始化成功后才能调用其他能力
	 * 
	 * @param mContext
	 * @return true：初始化成功 false：初始化失败
	 */
	public boolean initCmOmp(Context mContext) throws Exception
	{
		if (!NetUtil.isConnection())
		{
			throw new Exception();
		}

		return InitCmOmpInterface.initCmOmp(mContext);
	}

	/**
	 * 获取在线听歌地址 获取歌曲在线听地址
	 * 
	 * @param mContext
	 * @param musicId
	 *            歌曲ID
	 * @return 1.结果代码：“000000”，获取数据成功
	 *         getObject()获得Object对象后强制转换为String后，可获取在线听歌地址url；
	 *         2.结果代码：非“000000”，则获取数据失败，getMessage()获得对应结果说明。
	 */
	public CodeMessageObject getOnLineListenerSongUrl(Context mContext,
			String musicId) throws Exception
	{
		if (!NetUtil.isConnection())
		{
			throw new Exception();
		}
		return OnlineListenerMusicInterface.getOnLineListenerSongUrl(mContext,
				musicId);
	}
}
