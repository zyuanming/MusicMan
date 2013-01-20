package org.ming.logic;

import java.util.Map;

public class Task
{
	private int taskID; // 任务编号
	private Map taskParam; // 任务参数
	
	public static final int TASK_USER_LOGIN = 1;   //用户登录

	public Task(int id, Map param)
	{
		this.taskID = id;
		this.taskParam = param;
	}

	public int getTaskID()
	{
		return taskID;
	}

	public void setTaskID(int taskID)
	{
		this.taskID = taskID;
	}

	public Map getTaskParam()
	{
		return taskParam;
	}

	public void setTaskParam(Map taskParam)
	{
		this.taskParam = taskParam;
	}
}
