package org.ming.center.database;

public enum MusicType
{
	RADIO(0), ONLINEMUSIC(1), LOCALMUSIC(2);
	private int id;

	MusicType(int id)
	{
		this.id = id;
	}

}