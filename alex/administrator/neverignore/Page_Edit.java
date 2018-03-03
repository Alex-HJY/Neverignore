package com.alex.administrator.neverignore;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/13.
 */

public class Page_Edit extends Fragment{
    String[] name1={"高数上","高数下","电路"};

    int[] pictrue={R.drawable.gaoshushang,R.drawable.gaoshuxia,R.drawable.dianlu};

    public static View view;
    public static List<Map<String,Object>> mybooklistdata;
    public static GridView gridview_mybook;
    public static SimpleAdapter mybooklistadapter;
    public static Button btn_delete;
    public static List<String> Picked;

    public void initialTopbar(){
        ImageButton btn_edit=(ImageButton)getActivity().findViewById(R.id.edit);
        btn_edit.setImageResource(R.drawable.ui_back);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                begintransaction.replace(R.id.layout1, MainActivity.pageMybook);
                begintransaction.commit();

            }
        });
    }

    public void initialmybooklist(){
        //我的 初始化
        Picked=new ArrayList<String>();
        gridview_mybook=(GridView)view.findViewById(R.id.bookList_delete);
        mybooklistdata=new ArrayList<Map<String, Object>>();
        mybooklistadapter_getdata();

        mybooklistadapter=new SimpleAdapter(getActivity(),mybooklistdata,
                R.layout.adapter_mybooklist_edit,new String[]{"image","text"},
                new int[]{R.id.mybookgridchebox_delete,R.id.mybookgridtext_delete});
        mybooklistadapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView )
                {
                    view.setBackgroundResource((int) data);
                    return true;

                }
                return false;
            }
        });
        gridview_mybook.setAdapter(mybooklistadapter);

    }
    public void listedit(){


       gridview_mybook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent,  View view, final int position, long id) {
             //  Toast.makeText(getActivity(), Picked.size() + "", Toast.LENGTH_SHORT).show();
               ImageView v=(ImageView)view.findViewById(R.id.mybookgridchebox_delete);
               if (Picked.contains((String)mybooklistdata.get(position).get("text"))){
                   Picked.remove(Picked.indexOf((String)mybooklistdata.get(position).get("text")));
                   v.setImageResource(R.drawable.ui_unpicked);

               }else{
                   Picked.add((String)mybooklistdata.get(position).get("text"));
                   v.setImageResource(R.drawable.ui_picked);

               }




           }
       });

        btn_delete=(Button)view.findViewById(R.id.delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Picked.size()==0){
                    Toast.makeText(getActivity(),"没有选择！",Toast.LENGTH_SHORT).show();
                }else{
                    int i,k;
                    for (i=1;i<=Picked.size();i++){
                        String ii=Picked.get(i - 1);
                        SQLiteDatabase db=getActivity().openOrCreateDatabase("bookDB", getActivity().MODE_PRIVATE, null);
                        db.execSQL("create table if not exists mybookdb" +
                                " (_id integer primary key autoincrement,name text not null , picid integer not null , position integer)");
                        k=db.delete("mybookdb", "name=?", new String[]{ii});
                       // Toast.makeText(getActivity(),ii,Toast.LENGTH_SHORT).show();

                    }
                    mybooklistadapter_getdata();
                    mybooklistadapter.notifyDataSetChanged();
                    gridview_mybook.setAdapter(mybooklistadapter);
                }

            }
        });

    }
    public void mybooklistadapter_getdata(){
        mybooklistdata.clear();
        SQLiteDatabase db=getActivity().openOrCreateDatabase("bookDB", getActivity().MODE_PRIVATE, null);
        db.execSQL("create table if not exists mybookdb" +
                " (_id integer primary key autoincrement,name text not null , picid integer not null , position integer)");
        Cursor c=db.rawQuery("select * from mybookdb", null);
        if (c!=null) {
            int position,picid,id;
            String name;
            while (c.moveToNext()) {
                id=c.getInt(c.getColumnIndex("_id"));
             //   Toast.makeText(getActivity(),id+"...",Toast.LENGTH_SHORT).show();
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
        view=inflater.inflate(R.layout.page_edit,container,false);
        initialmybooklist();
        initialTopbar();
        listedit();
        return view;
    }
}