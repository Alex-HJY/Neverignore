/*
 * @Title ThreadDAO.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-8 上午10:55:21
 * @version 1.0
 */
package com.Service;


import java.util.List;

/** 
 * 数据访问接口
 * @author Yann
 * @date 2015-8-8 上午10:55:21
 */
public interface ThreadDAO
{
	/** 
	 * 插入线程信息
	 * @param threadInfo
	 * @return void
	 * @author Yann
	 * @date 2015-8-8 上午10:56:18
	 */ 
	public void insertThread(Threadinfo threadInfo);
	/** 
	 * 删除线程信息
	 * @param url
	 * @param thread_id
	 * @return void
	 * @author Yann
	 * @date 2015-8-8 上午10:56:57
	 */ 
	public void deleteThread(String url, int thread_id);
	/** 
	 * 更新线程下载进度
	 * @param url
	 * @param thread_id
	 * @return void
	 * @author Yann
	 * @date 2015-8-8 上午10:57:37
	 */ 
	public void updateThread(String url, int thread_id, int finished);
	/** 
	 * 查询文件的线程信息
	 * @param url
	 * @return
	 * @return List<ThreadInfo>
	 * @author Yann
	 * @date 2015-8-8 上午10:58:48
	 */ 
	public List<Threadinfo> getThreads(String url);
	/** 
	 * 线程信息是否存在
	 * @param url
	 * @param thread_id
	 * @return
	 * @return boolean
	 * @author Yann
	 * @date 2015-8-8 上午10:59:41
	 */ 
	public boolean isExists(String url, int thread_id);

	public void updateThread_state(int thread_id, int pause);

	public void updateThread_length(int thread_id, int length);
	public int GetThread_state(int thread_id);
	public List<Threadinfo> getAll();
}
