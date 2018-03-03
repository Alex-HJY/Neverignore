package com.alex.administrator.neverignore;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;

import java.io.File;

/**
 * Created by Administrator on 2015/8/18.
 */
public class Openpdf {
    public Openpdf(String filename,Activity activity){
        String path=GetDocDir.GetPdfDir()+filename+".pdf";
        File file=new File(path);
        if (file.exists()){
            Uri uri=Uri.parse(path);
            Intent intent=new Intent(activity,MuPDFActivity.class);
            intent.setData(uri);
            intent.setAction(Intent.ACTION_VIEW);
            activity.startActivity(intent);
        }else
            Toast.makeText(activity,path+"文件不存在,请到题库中下载",Toast.LENGTH_LONG).show();

    }
    public static Boolean fileExist(String filename){
        String path=GetDocDir.GetPdfDir()+filename+".pdf";
        File file=new File(path);
        return file.exists();
    }
}
