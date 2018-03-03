package com.alex.administrator.neverignore;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Service.Threadinfo;
import com.Service.DownloadService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/8/23.
 */


public class Page_download extends Fragment {
    static class Viewholder{
        TextView name,percent;
        ProgressBar progressBar;
        ImageButton begin,pause;
    }
    BroadcastReceiver mReceiver,mReceiver2;
    View view;
    ListView downloadlistview;
    TextView percent;
    List<Threadinfo> downloadlist;
    Adapter_downlist downloadlistadapter;
    int finised ;
    int position;
    ProgressBar bar;

    public void initialTopbar(){
        ImageButton btn_edit=(ImageButton)getActivity().findViewById(R.id.edit);
        btn_edit.setImageResource(R.drawable.ui_back);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                begintransaction.replace(R.id.layout1, MainActivity.pageBooks);
                begintransaction.commit();

            }
        });
    }


    void getdata(){
        downloadlist.clear();
        SQLiteDatabase db=getActivity().openOrCreateDatabase("downloadDB", getActivity().MODE_PRIVATE, null);
        db.execSQL("create table if not exists thread_info (_id integer primary key autoincrement," +
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
                downloadlist.add(threadinfo);
                Log.i("getdata", threadinfo.toString());
            }
        } else {

        }
            c.close();
        db.close();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view=inflater.inflate(R.layout.page_download,container,false);

        initialTopbar();


        downloadlistview=(ListView)view.findViewById(R.id.downloadlistview);
        downloadlist=new ArrayList<Threadinfo>();
        getdata();
        downloadlistadapter=new Adapter_downlist(getActivity(),downloadlist);
        downloadlistview.setAdapter(downloadlistadapter);


         mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                    finised = intent.getIntExtra("finished", 0);
                    position = intent.getIntExtra("position", -1);
                    View barr=downloadlistview.getChildAt(position);
                    getdata();
                    downloadlistadapter.notifyDataSetChanged();
                    if (barr!=null){

                        bar = (ProgressBar) downloadlistview.getChildAt(position).findViewById(R.id.downloadlist_progerssbar);
                        bar.setProgress(finised);
                        percent=(TextView) downloadlistview.getChildAt(position).findViewById(R.id.downloadlist_percent);
                        percent.setText(finised + "%");

                 }
                   // downloadlistview.setAdapter(downloadlistadapter);
                }
            }
        };

        mReceiver2 = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
            if("Datachange".equals(intent.getAction()))
                {
                    Log.i("Page", "getchange1");
                    getdata();
                    downloadlistadapter.notifyDataSetChanged();
                    downloadlistview.setAdapter(downloadlistadapter);
                }
            }
        };



        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        getActivity().registerReceiver(mReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("Datachange");
        getActivity().registerReceiver(mReceiver2, filter);





        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(mReceiver2);
        super.onDestroy();
    }
}
