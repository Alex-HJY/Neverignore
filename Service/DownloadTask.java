package com.Service;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.alex.administrator.neverignore.MainActivity;

/**
 * Created by Administrator on 2015/8/25.
 */
public class DownloadTask {
    private int mFinised = 0;
    String code;
    String name;
    Threadinfo threadinfo;
    private ThreadDAO mDao;
    Context context;
    int position;
    public DownloadTask(String code,Threadinfo threadinfo,Context context,int Position){
        this.code=code;
        this.threadinfo = threadinfo;
        this.position=Position;
        this.context=context;
        mDao = new ThreadDAOImpl(context);

        switch (code){
            case "ACTION_START":
                StartTask(threadinfo);
                break;

        }
    }

    void StartTask(Threadinfo threadinfo){
        List<Threadinfo> threads = mDao.getThreads(threadinfo.getUrl());
        Log.i("thread", " StartTask…");
        if (0 != threads.size()){
            Log.i("thread", " size="+threads.size()+"");
            Threadinfo threadInfo1 = threads.get(0);
            Log.i("thread", threadInfo1.toString());
            if (threadInfo1.getPause()==1&& threadInfo1.getLength()!=0){
                threadInfo1.setPause(0);
                mDao.updateThread_state(threadInfo1.getId(), 0);
//                Toast.makeText(MainActivity.mMainActivity,"download thread begin",Toast.LENGTH_SHORT).show();
                new DownloadThread(threadInfo1).start();
            }

        }

    }




    private class DownloadThread extends Thread
    {
        private Threadinfo mThreadInfo = null;

        /**
         *@param mInfo
         */
        public DownloadThread(Threadinfo mInfo)
        {
            this.mThreadInfo = mInfo;
        }

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            Log.i("thread", "xiazai线程初始化中……");
            // 向数据库插入线程信息
            if (!mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId()))
            {
                mDao.insertThread(mThreadInfo);
            }

            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;

            try
            {
                URL url = new URL(mThreadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                // 设置下载位置
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                connection.setRequestProperty("Range",
                        "bytes=" + start + "-" + mThreadInfo.getEnd());
                // 设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH,
                        mThreadInfo.getName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                Intent intent = new Intent();
                intent.setAction(DownloadService.ACTION_UPDATE);
                Intent intent2 = new Intent();
                intent2.setAction("Datachange");
                mFinised += mThreadInfo.getFinished();
                name=mThreadInfo.getName();
                // 开始下载
                Log.i("ResponseCode",connection.getResponseCode()+"");
                if (connection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT)
                {
                    // 读取数据
                    inputStream = connection.getInputStream();
                    byte buf[] = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buf)) != -1)
                    {
                        // 写入文件
                        raf.write(buf, 0, len);
                        // 把下载进度发送广播给Activity
                        mFinised += len;
                        if (System.currentTimeMillis() - time > 500)
                        {
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinised);
                            time = System.currentTimeMillis();
                            Log.i("finished", mFinised * 100 / mThreadInfo.getEnd()+"");
                            intent.putExtra("finished", mFinised * 100 / mThreadInfo.getEnd());
                            intent.putExtra("position",position);
                            context.sendBroadcast(intent);
                        }

                        // 在下载暂停时，保存下载进度
                        if ( mDao.GetThread_state(mThreadInfo.getId())==1)
                        {
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinised);
                            DownloadService.getdata();
                            context.sendBroadcast(intent2);
                            return;
                        }
                    }
                    // 删除线程信息
                    mDao.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
                    Log.i("DownloadTask", "下载完毕");
                    DownloadService.getdata();

                    context.sendBroadcast(intent2);
                    MainActivity.mMainActivity.handler.sendEmptyMessage(0);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                    if (raf != null)
                    {
                        raf.close();
                    }
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
    }
}
