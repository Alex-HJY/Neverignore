/*
 * @Title FileInfo.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-7 下午10:13:28
 * @version 1.0
 */
package com.Service;


import java.io.Serializable;

/** 
 * 文件信息
 * @author Yann
 * @date 2015-8-7 下午10:13:28
 */
public class Threadinfo implements Serializable
{

	private int id;
	private String url;
	private int start;
	private int end;
	private int finished;
	private int length;
	private String fileName;
	int pause;

	public Threadinfo(int id, String url, int start, int end, int finished, int length, String name, int pause)
	{
		this.pause=pause;
		this.id = id;
		this.url = url;
		this.start = start;
		this.fileName=name;
		this.length=length;
		this.end = end;
		this.finished = finished;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public int getLength()
	{
		return length;
	}
	public void setLength(int length)
	{
		this.length = length;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String getName()
	{
		return fileName;
	}
	public void setName(String name)
	{
		this.fileName = name;
	}
	public int getStart()
	{
		return start;
	}
	public void setStart(int start)
	{
		this.start = start;
	}
	public int getEnd()
	{
		return end;
	}
	public void setEnd(int end)
	{
		this.end = end;
	}
	public int getPause()
	{
		return pause;
	}
	public void setPause(int pause)
	{
		this.pause = pause;
	}
	public int getFinished()
	{
		return finished;
	}
	public void setFinished(int finished)
	{
		this.finished = finished;
	}
	@Override
	public String toString()
	{
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start
				+ ", end=" + end + ", finished=" + finished + ", length=" +length+ ", name=" +
				fileName+",pause="+pause+""+"]";
	}


}
