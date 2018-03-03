package com.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.alex.administrator.neverignore.GetDocDir;
import com.alex.administrator.neverignore.MainActivity;
import com.alex.administrator.neverignore.Page_download;


import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */
public class DownloadService extends Service {
    int mmlength;
    public static final String DOWNLOAD_PATH = GetDocDir.GetPdfDir();
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_STOPALL = "ACTION_STOPALL";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final int MSG_INIT = 0;
    public static final int MSG_STOP= 1;
    public static List<Threadinfo> TasksLIST;
    public static ThreadDAO mDAO=null;
    private DownloadTask mTask = null;
    private String TAG = "DownloadService";
    static DBHelper mHelper;
    private String ttt;



    public static void getdata(){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        TasksLIST = new ArrayList<Threadinfo>();
        TasksLIST.clear();
        db.execSQL("create table if not exists thread_info(_id integer primary key autoincrement," +
                "thread_id integer, url text, start integer, end integer, finished integer, length integer, name text, pause integer)");
        Cursor c=db.rawQuery("select * from thread_info", null);
        if (c!=null) {
            while (c.moveToNext()) {
                Threadinfo threadinfo =new Threadinfo(c.getInt(c.getColumnIndex("thread_id")),
                        c.getString(c.getColumnIndex("url")),
                        c.getInt(c.getColumnIndex("start")),
                        c.getInt(c.getColumnIndex("end")),
                        c.getInt(c.getColumnIndex("finished")),
                        c.getInt(c.getColumnIndex("length")),
                        c.getString(c.getColumnIndex("name")),
                        c.getInt(c.getColumnIndex("pause")));
                TasksLIST.add(threadinfo);
            }
            c.close();
        }
        db.close();

    }



    private class InitThread extends Thread
    {
        public int mlength;
        private Threadinfo mThreadinfo = null;
        public InitThread(Threadinfo mThreadinfo)
        {
            this.mThreadinfo = mThreadinfo;
        }
        @Override
        public void run()
        {

            Log.i(TAG, "线程初始化中……");
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;

            try
            {
                // 连接网络文件
                URL url = new URL(mThreadinfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                int length = -1;

                if (connection.getResponseCode() == HttpStatus.SC_OK)
                {
                    // 获得文件的长度
                    length = connection.getContentLength();
                    mmlength=length;

                }
                if (length <= 0)
                {
                    return;
                }

                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists())
                {
                    dir.mkdir();
                }

                // 在本地创建文件
                File file = new File(dir, mThreadinfo.getName());
                raf = new RandomAccessFile(file, "rwd");
                // 设置文件长度
                raf.setLength(length);
                mThreadinfo.setLength(length);
                mDAO.updateThread_length(mThreadinfo.getId(),length);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
                if (raf != null)
                {
                    try
                    {
                        raf.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDAO=MainActivity.mDao;
        mHelper=new DBHelper(this);
        SQLiteDatabase db=mHelper.getWritableDatabase();
//        Toast.makeText(MainActivity.mMainActivity,"获得参数",Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.mMainActivity,intent.getAction(),Toast.LENGTH_SHORT).show();
        // 获得Activity传过来的参数
        ttt="sad";
        if (!intent.getAction().isEmpty()){
        ttt=intent.getAction();}
        if (ttt!="sad")
        if (ACTION_START.equals(intent.getAction()))
        {
            getdata();
            Threadinfo threadinfo = (Threadinfo) intent.getSerializableExtra("threadinfo");
//            Toast.makeText(MainActivity.mMainActivity,"获得线程",Toast.LENGTH_SHORT).show();
            int i,position=-1;
            for (i=0;i<TasksLIST.size();i++){
                if (TasksLIST.get(i).getId()==(threadinfo.getId())){
                    position=i;
                }
            }
            Log.i(TAG, "Start:" + threadinfo.toString());
            if (position>=0){
                mmlength=-1;
              //  Toast.makeText(MainActivity.mMainActivity,"//初始化线程",Toast.LENGTH_SHORT).show();
                new InitThread(threadinfo).start();
                getdata();
             //   Toast.makeText(MainActivity.mMainActivity,mmlength+"   "+TasksLIST.get(position).getLength()+"",Toast.LENGTH_SHORT).show();
                if (TasksLIST.get(position).getLength()==0)
                    try {
                       Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                //  Toast.makeText(MainActivity.mMainActivity,"初始化线程完毕",Toast.LENGTH_SHORT).show();
              new DownloadTask(ACTION_START, TasksLIST.get(position),this,position);
            }


        }
        else if (ACTION_STOP.equals(intent.getAction()))
        {
            Threadinfo threadinfo = (Threadinfo) intent.getSerializableExtra("threadinfo");
            mDAO.updateThread_state(threadinfo.getId(),1);
            getdata();
            Intent intent2 = new Intent();
            intent2.setAction("Datachange");
            this.sendBroadcast(intent2);
            Log.i("Service","send Datachange");

        }
        else if (ACTION_STOPALL.equals(intent.getAction()))
        {
            List<Threadinfo> all=mDAO.getAll();
            int i;
            for (i=0;i<all.size();i++){
                mDAO.updateThread_state(all.get(i).getId(),1);
            }

            Threadinfo threadinfo = (Threadinfo) intent.getSerializableExtra("threadinfo");
            Log.i(TAG , "Stopall");

        }
        else if (ACTION_DELETE.equals(intent.getAction()))
        {
            Threadinfo threadinfo = (Threadinfo) intent.getSerializableExtra("threadinfo");
            mDAO.deleteThread(threadinfo.getUrl(),threadinfo.getId());
            getdata();
            File file = new File(DownloadService.DOWNLOAD_PATH,threadinfo.getName());
            file.delete();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
