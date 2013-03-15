package org.ming.ui.activity.online;

/**
 * @creator jack.long
 * @create-time 2012-12-28 下午2:07:08
 * @revision $Id 类说明 铃音数据对象
 */

public class SongMusicInfo
{
	private String musicId = null;// 歌曲id
	private String count = null;// 点击量
	private String crbtValidity = null;// 彩铃有效期(2010-2-26)
	private String ringSongPrice = null;// 彩铃价格
	private String songName = null;// 歌曲名称
	private String singerId = null;// 歌手id
	private String singerName = null;// 歌手名称
	private String songAuditionUrl = null;// 歌曲/铃音试听地址
	private boolean isBuyRingSong = false;
	private String albumName = null;// 专辑名
	private String chartName = null;// 榜单名称

	public SongMusicInfo(String musicId, String count, String crbtValidity,
			String ringSongPrice, String songName, String singerId,
			String singerName)
	{
		this.musicId = musicId;
		this.count = count;
		this.crbtValidity = crbtValidity;
		this.ringSongPrice = ringSongPrice;
		this.songName = songName;
		this.singerId = singerId;
		this.singerName = singerName;
	}

	public SongMusicInfo(String musicId, String count, String crbtValidity,
			String ringSongPrice, String songName, String singerId,
			String singerName, String songAuditionUrl)
	{
		this.musicId = musicId;
		this.count = count;
		this.crbtValidity = crbtValidity;
		this.ringSongPrice = ringSongPrice;
		this.songName = songName;
		this.singerId = singerId;
		this.singerName = singerName;
		this.songAuditionUrl = songAuditionUrl;
	}

	public SongMusicInfo(String musicId, String count, String crbtValidity,
			String ringSongPrice, String songName, String singerId,
			String singerName, String songAuditionUrl, boolean isBuyRingSong)
	{
		this.musicId = musicId;
		this.count = count;
		this.crbtValidity = crbtValidity;
		this.ringSongPrice = ringSongPrice;
		this.songName = songName;
		this.singerId = singerId;
		this.singerName = singerName;
		this.songAuditionUrl = songAuditionUrl;
		this.isBuyRingSong = isBuyRingSong;
	}

	public SongMusicInfo()
	{}

	public String getAlbumName()
	{
		return albumName;
	}

	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}

	public String getMusicId()
	{
		return musicId;
	}

	public void setMusicId(String musicId)
	{
		this.musicId = musicId;
	}

	public String getCount()
	{
		return count;
	}

	public void setCount(String count)
	{
		this.count = count;
	}

	public String getCrbtValidity()
	{
		return crbtValidity;
	}

	public void setCrbtValidity(String crbtValidity)
	{
		this.crbtValidity = crbtValidity;
	}

	public String getRingSongPrice()
	{
		return ringSongPrice;
	}

	public void setRingSongPrice(String ringSongPrice)
	{
		this.ringSongPrice = ringSongPrice;
	}

	public String getSongName()
	{
		return songName;
	}

	public void setSongName(String songName)
	{
		this.songName = songName;
	}

	public String getSingerId()
	{
		return singerId;
	}

	public void setSingerId(String singerId)
	{
		this.singerId = singerId;
	}

	public String getSingerName()
	{
		return singerName;
	}

	public void setSingerName(String singerName)
	{
		this.singerName = singerName;
	}

	public String getSongAuditionUrl()
	{
		return songAuditionUrl;
	}

	public void setSongAuditionUrl(String songAuditionUrl)
	{
		this.songAuditionUrl = songAuditionUrl;
	}

	public boolean isBuyRingSong()
	{
		return isBuyRingSong;
	}

	public void setBuyRingSong(boolean isBuyRingSong)
	{
		this.isBuyRingSong = isBuyRingSong;
	}

	public String getChartName()
	{
		return chartName;
	}

	public void setChartName(String chartName)
	{
		this.chartName = chartName;
	}

}
