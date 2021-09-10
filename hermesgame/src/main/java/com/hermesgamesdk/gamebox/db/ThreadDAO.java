package com.hermesgamesdk.gamebox.db;

import com.hermesgamesdk.gamebox.entity.ThreadInfo;

import java.util.List;



/**
 * 數據庫操作的接口類
 *
 */
public interface ThreadDAO {
	// 插入綫程
	public void insertThread(ThreadInfo info);
	// 刪除綫程
	public void deleteThread(String url);
	// 更新綫程
	public void updateThread(String url, String savePath, int finished);

	public void updateThreadSuccess(String url,int finished,String savePath);
	// 查詢綫程
	public List<ThreadInfo> queryThreads(String url);

	public List<ThreadInfo> queryThreadEnd(String url);

	public List<ThreadInfo> queryThreadUnEnd();

	public List<ThreadInfo> getRuningDownTask();

	public void deleteThreadById(String id);

	// 判斷綫程是否存在
	public boolean isExists(String url);
}
