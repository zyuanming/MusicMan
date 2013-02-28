package org.ming.ui.view;

public abstract interface BaseViewInterface
{
	public abstract void addListner();

	public abstract void getDataFromURL();

	public abstract void removeListner();

	public abstract void setURL(String paramString);

	public abstract void getDataFromURL(int url);
}