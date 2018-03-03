package com.alex.administrator.neverignore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Service.DownloadService;
import com.Service.Threadinfo;

import java.util.List;

/**
 * Created by Administrator on 2015/8/24.
 */
public class Adapter_downlist extends BaseAdapter {

    private Context context;
    List<Threadinfo> downloadlist;
    Threadinfo mthreadinfo;

    public Adapter_downlist(Context context, List<Threadinfo> downloadlist){
        this.context=context;
        this.downloadlist=downloadlist;

    }

    @Override
    public int getCount() {
        return downloadlist.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Viewholder viewholder;
        if (view==null){
            view=LayoutInflater.from(context).inflate(R.layout.adapter_downloadlist,null);
            viewholder=new Viewholder();
            viewholder.name=(TextView)view.findViewById(R.id.downloadlist_name);
            viewholder.percent=(TextView)view.findViewById(R.id.downloadlist_percent);
            viewholder.begin=(ImageButton)view.findViewById(R.id.downloadlist_begin);
            viewholder.pause=(ImageButton)view.findViewById(R.id.downloadlist_pause);
            viewholder.progressBar=(ProgressBar)view.findViewById(R.id.downloadlist_progerssbar);
            viewholder.delete=(ImageButton)view.findViewById(R.id.downloadlist_delete);
            view.setTag(viewholder);
            viewholder.flag=1;
            viewholder.rposition=position;


        }
        else
        {
            viewholder=(Viewholder)view.getTag();
    }
        mthreadinfo=downloadlist.get(position);
        if(mthreadinfo.getPause()==1) {
            viewholder.flag=1;
            viewholder.begin.setBackgroundResource(R.drawable.ui_begin);
        }else
        {
            viewholder.flag=0;
            viewholder.begin.setBackgroundResource(R.drawable.ui_pause);
        }
        viewholder.name.setText(mthreadinfo.getName());
        viewholder.progressBar.setMax(100);
        if (mthreadinfo.getEnd()!= 0)
            viewholder.progressBar.setProgress(mthreadinfo.getFinished() * 100 / mthreadinfo.getEnd());
       else
            viewholder.progressBar.setProgress(0);
       if (mthreadinfo.getEnd()!= 0)
            viewholder.percent.setText(mthreadinfo.getFinished() * 100 / mthreadinfo.getEnd()+"%");
        else
           viewholder.percent.setText(0+"%");

        viewholder.begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewholder.flag==1) {
                    viewholder.flag=0;
                    viewholder.begin.setBackgroundResource(R.drawable.ui_pause);
                    mthreadinfo=downloadlist.get(viewholder.rposition);
                    mthreadinfo.setPause(1);
                    Log.i("BEGIN", ""+mthreadinfo.getName());
                    Intent intent = new Intent(MainActivity.mMainActivity, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("threadinfo", mthreadinfo);
                    MainActivity.mMainActivity.startService(intent);
                }
                else
                {
                    viewholder.flag=1;
                    mthreadinfo=downloadlist.get(viewholder.rposition);
                    Log.i("PAUSE", ""+mthreadinfo.getName());

                    viewholder.begin.setBackgroundResource(R.drawable.ui_begin);
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_STOP);
                    intent.putExtra("threadinfo", mthreadinfo);
                    context.startService(intent);
                }
              /*  mthreadinfo.setPause(1);
                Intent intent = new Intent(MainActivity.mMainActivity, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("threadinfo", mthreadinfo);
                MainActivity.mMainActivity.startService(intent);
*/
            }
        });
        viewholder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("threadinfo", mthreadinfo);
                context.startService(intent);

            }
        });
        viewholder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mthreadinfo=downloadlist.get(viewholder.rposition);
                Intent intent = new Intent(context, DownloadService.class);
                intent.setAction(DownloadService.ACTION_DELETE);
                Log.i("DELETE", "" + mthreadinfo.getName());
                intent.putExtra("threadinfo", mthreadinfo);
                context.startService(intent);
                Intent intent2 = new Intent();
                intent2.setAction("Datachange");
                context.sendBroadcast(intent2);

            }
        });
        return view;
    }
    static class Viewholder{
        TextView name,percent;
        ProgressBar progressBar;
        ImageButton begin,pause,delete;
        int flag,rposition;
    }
}
