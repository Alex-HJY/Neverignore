package com.alex.administrator.neverignore;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/11.
 */
public class Page_Mybook extends Fragment {
    public View view;
    Activity activity=getActivity();

    public static List<Map<String,Object>> mybooklistdata;
    public static GridView gridview_mybook;
    public static SimpleAdapter mybooklistadapter;
    ImageButton btn_edit;


    public void initialmybooklist(){
        //我的 初始化
        gridview_mybook=(GridView)view.findViewById(R.id.bookList);
        mybooklistdata=new ArrayList<Map<String, Object>>();
        mybooklistadapter_getdata();
        mybooklistadapter=new SimpleAdapter(getActivity(),mybooklistdata,
                R.layout.adapter_mybooklist,new String[]{"image","text"},
                new int[]{R.id.mybookgridimage,R.id.mybookgridtext});
        gridview_mybook.setAdapter(mybooklistadapter);
        gridview_mybook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,Object> map = (HashMap<String,Object>) mybooklistadapter.getItem(position);
                new Openpdf((String)map.get("text"), getActivity());
            }
        });


    }

    public void initialTopbar(){
        btn_edit=(ImageButton)getActivity().findViewById(R.id.edit);
        btn_edit.setImageResource(R.drawable.ui_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                begintransaction.replace(R.id.layout1, MainActivity.pageEdit);
                begintransaction.commit();

            }
        });
    }

    public void mybooklistadapter_getdata(){
        SQLiteDatabase db=getActivity().openOrCreateDatabase("bookDB", getActivity().MODE_PRIVATE, null);
        db.execSQL("create table if not exists mybookdb" +
                " (_id integer primary key autoincrement,name text not null , picid integer not null , position integer)");
        Cursor c=db.rawQuery("select * from mybookdb", null);
        if (c!=null) {
            int position,picid,id;
            String name;
            while (c.moveToNext()) {
                id=c.getInt(c.getColumnIndex("_id"));
                position=c.getInt(c.getColumnIndex("position"));
                picid=c.getInt(c.getColumnIndex("picid"));
                name=c.getString(c.getColumnIndex("name"));
                Map<String,Object> map=new HashMap<>();
                map.put("image",picid);
                map.put("text",name);
                mybooklistdata.add(map);
            }
            c.close();
        }

        }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.page_mybook,container,false);
        initialTopbar();
        initialmybooklist();

        return view;
    }
}
