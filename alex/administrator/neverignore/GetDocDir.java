package com.alex.administrator.neverignore;

/**
 * Created by Administrator on 2015/8/18.
 */
public class GetDocDir {
    public static String GetPdfDir(){
        return android.os.Environment.getExternalStorageDirectory()+"/neverignore/";
    }
}
