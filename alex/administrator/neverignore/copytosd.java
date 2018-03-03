package com.alex.administrator.neverignore;

/**
 * Created by Administrator on 2015/8/18.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
/*将assets文件夹下的数据库写入SD卡中
 * @author Dave */
public class copytosd {
    private Context context;
    String filename,tofilename;
    String filePath =  GetDocDir.GetPdfDir();
    public copytosd(Context context,String filename,String tofilename){
        this.context = context;
        this.filename=filename;
        this.tofilename=tofilename;
        if(!isExist()){
            write();
        }
    }
    private void write(){
        InputStream inputStream;
        try {
            inputStream = context.getResources().getAssets().open(filename);
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + tofilename);
            byte[] buffer = new byte[512];
            int count = 0;
            while((count = inputStream.read(buffer)) > 0){
                fileOutputStream.write(buffer, 0 ,count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            System.out.println("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isExist(){
        File file = new File(filePath + tofilename);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }
}