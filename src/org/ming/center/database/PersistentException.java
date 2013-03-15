package org.ming.center.database;

public class PersistentException extends Exception
{
	public static final int DUPLICATE_DOWNLOAD_ITEM = 200;
	public static final int DUPLICATE_PLAYLIST = 100;
	private static final long serialVersionUID = 2379603831904881138L;
	private int causeCode;
	private Exception exception;

	public PersistentException(int paramInt)
	{
		this(paramInt, null);
	}

	public PersistentException(int paramInt, Exception paramException)
	{
		this.causeCode = paramInt;
		this.exception = paramException;
	}

	public int getCauseCode()
	{
		return this.causeCode;
	}

	public Exception getException()
	{
		return this.exception;
	}

	public void setCauseCode(int paramInt)
	{
		this.causeCode = paramInt;
	}

	public void setException(Exception paramException)
	{
		this.exception = paramException;
	}
}