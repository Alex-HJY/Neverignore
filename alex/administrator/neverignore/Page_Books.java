package com.alex.administrator.neverignore;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.Service.DownloadService;
import com.Service.ThreadDAO;
import com.Service.ThreadDAOImpl;
import com.Service.Threadinfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2015/8/11.
 */
public class Page_Books extends Fragment {
    View view;
    Context context=getActivity();

    Spinner spinner1,spinner2,spinner3;
    List<String> list1,list2,list3;
    ArrayAdapter adapter1,adapter2,adapter3;
    ImageView bookpic;
    Button putinto;
    Button read;
    ThreadDAOImpl mDao=null;

    String[] name1={"高数上","高数下","电路","电磁场导论","电机学","电力系统分析","复变函数","概率论",
            "积分变换","模拟电子技术","线性代数","大物上","大物下","工图答案","大物下试题卷","高数下试题卷","工图试题卷","线代试题卷","大物ppt","电路ppt"};
    String[] url={"http://www.neverignore.cn/answer-pdf/gaoshushang.pdf",
                  "http://www.neverignore.cn/answer-pdf/gaoshuxia.pdf",
                  "http://www.neverignore.cn/answer-pdf/dianlu.pdf",
            "http://www.neverignore.cn/answer-pdf/diancichangdaolun.pdf",
            "http://www.neverignore.cn/answer-pdf/dianjixue.pdf",
            "http://www.neverignore.cn/answer-pdf/dianlixitongfenxi.pdf",
            "http://www.neverignore.cn/answer-pdf/fubianhanshu.pdf",
            "http://www.neverignore.cn/answer-pdf/gailvlun.pdf",
            "http://www.neverignore.cn/answer-pdf/jifenbianhuan.pdf",
            "http://www.neverignore.cn/answer-pdf/monidianzijishu.pdf",
            "http://www.neverignore.cn/answer-pdf/xianxingdaishu.pdf",
            "http://www.neverignore.cn/answer-pdf/dawushang.pdf",
            "http://www.neverignore.cn/answer-pdf/dawuxia.pdf",
            "http://www.neverignore.cn/answer-pdf/gongtudaan.pdf",
            "http://www.neverignore.cn/answer-pdf/daxuewulixiashiti.pdf",
            "http://www.neverignore.cn/answer-pdf/gaoshuxiashiti.pdf",
            "http://www.neverignore.cn/answer-pdf/gongchengzhitushiti.pdf",
            "http://www.neverignore.cn/answer-pdf/xiandaishiti.pdf",
            "http://www.neverignore.cn/answer-pdf/ppt-dawu.pdf",
            "http://www.neverignore.cn/answer-pdf/ppt-dianlu.pdf"};
    int[] pictrue={R.drawable.gaoshushang,R.drawable.gaoshuxia,R.drawable.dianlu,
            R.drawable.diancichangdaolun,R.drawable.dianjixue,R.drawable.dianlixitongfenxi,R.drawable.fubianhanshu,
            R.drawable.gailvlun,R.drawable.jifenbianhuan,R.drawable.monidianzijishu,R.drawable.xianxingdaishu,
            R.drawable.dawushang,R.drawable.dawuxia,R.drawable.meiyoutu,R.drawable.meiyoutu,R.drawable.meiyoutu,R.drawable.meiyoutu,R.drawable.meiyoutu,R.drawable.meiyoutu,R.drawable.meiyoutu
          };
    public int Position=0;

    public List<String> putdata(List<String> list,String[] str){
        int i;
        list.clear();
        for (i=0;i<str.length;i++)
            list.add(str[i]);
        return list;
    };
    public void initialspinner(){
        bookpic=(ImageView)view.findViewById(R.id.bookpic);
        bookpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                read.performClick();
            }
        });
        list1=new ArrayList<String>();        list2=new ArrayList<String>();        list3=new ArrayList<String>();
        spinner1=(Spinner)view.findViewById(R.id.spinner1);
        spinner2=(Spinner)view.findViewById(R.id.spinner2);
        spinner3=(Spinner)view.findViewById(R.id.spinner3);
        list1=putdata(list1,name1);
        adapter1=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookpic.setImageResource(pictrue[position]);
                Position = position;
                if (mDao!=null) {
                    if (Openpdf.fileExist(name1[Position]) && (!mDao.isExists(url[Position], Position))) {
                        read.setText("开始阅读");

                    } else
                    {
                        read.setText("下载到本地");

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void initialbutton(){

        putinto=(Button)view.findViewById(R.id.putinto);
        read=(Button)view.findViewById(R.id.read);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDao!=null) {
                    if (read.getText()=="开始阅读") {
                                new Openpdf(name1[Position], getActivity());
                    }
                 else
                {

                                if (mDao.isExists(url[Position], Position)) {
                                    Threadinfo threadinfo=mDao.getThreads(url[Position]).get(0);
                                    Intent intent = new Intent(MainActivity.mMainActivity, DownloadService.class);
                                    intent.setAction(DownloadService.ACTION_START);
                                    intent.putExtra("threadinfo", threadinfo);
                                    MainActivity.mMainActivity.startService(intent);
                                    Toast.makeText(getActivity(), "已添加到下载列表，请到列表中查看", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Threadinfo threadinfo = new Threadinfo(Position,
                                            url[Position], 0, 0, 0, 0, name1[Position]+".pdf", 1);
                                    mDao.insertThread(threadinfo);
                                    Intent intent = new Intent(MainActivity.mMainActivity, DownloadService.class);
                                    intent.setAction(DownloadService.ACTION_START);
                                    intent.putExtra("threadinfo", threadinfo);
                                    MainActivity.mMainActivity.startService(intent);
                                    Toast.makeText(getActivity(), "已添加到下载列表，请到列表中查看", Toast.LENGTH_LONG).show();
                                }
                }
                }
            }
        });


        putinto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = getActivity().openOrCreateDatabase("bookDB", getActivity().MODE_PRIVATE, null);
                db.execSQL("create table if not exists mybookdb" +
                        " (_id integer primary key autoincrement,name text not null , picid integer not null , position integer)");
                Cursor c = db.rawQuery("select * from mybookdb", null);
                int position, picid, id;
                String name;
                boolean flag = true;
                while (c.moveToNext()) {
                    picid = c.getInt(c.getColumnIndex("picid"));
                    if (picid == pictrue[Position])
                        flag = false;
                }
                c.close();
                if (flag) {
                    db.execSQL("insert into mybookdb(name,picid,position) value" +
                            "s('" + name1[Position] + "'," + pictrue[Position] + "," + Position + ")");
                    Toast.makeText(getActivity(), name1[Position] + "加入收藏", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), name1[Position] + "已存在", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initialTopbar(){
        ImageButton btn_edit=(ImageButton)getActivity().findViewById(R.id.edit);
        btn_edit.setImageResource(R.drawable.ui_download);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                begintransaction.replace(R.id.layout1, MainActivity.pageDownload);
                begintransaction.commit();

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.page_books,container,false);
        mDao=MainActivity.mDao;

        initialTopbar();
        initialspinner();
        initialbutton();
        return view;
    }
}
