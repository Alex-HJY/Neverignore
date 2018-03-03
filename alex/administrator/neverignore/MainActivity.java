package com.alex.administrator.neverignore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.Service.DownloadService;
import com.Service.ThreadDAO;
import com.Service.ThreadDAOImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity {
    public static double AppVersion=2.0, newVersion;
    public static MainActivity mMainActivity = null;
    public static Page_Mybook pageMybook =new Page_Mybook();
    public static  Page_Books pageBooks =new Page_Books();
    public static  Page_Edit pageEdit =new Page_Edit();
    public static Page_download pageDownload=new Page_download();
    public static ImageButton btn_edit;
    CheckBox btn_mybook;
    CheckBox btn_bookgroup;
    public static ThreadDAOImpl mDao=null;

    private boolean getServerVersion() {
        String urlStr = "http://www.neverignore.cn/version.txt";
        //long a = System.currentTimeMillis();
        try {
			/*
			 * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
			 * <uses-permission android:name="android.permission.INTERNET" />
			 */
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(60 * 1000);
            // 取得inputStream，并进行读取
            InputStream input = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                sb.append(line);

            }

            System.out.println(sb.toString());
            newVersion =Double.parseDouble(sb.toString());


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /*public void initialTopbar(){
        ImageButton btn_edit=(ImageButton)findViewById(R.id.edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {

            Boolean flag=true;
            @Override
            public void onClick(View v) {
                if (flag)
                {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                    begintransaction.replace(R.id.layout1, pageEdit);
                    begintransaction.commit();
                    flag=false;}
                else{
                    flag=true;
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                    begintransaction.replace(R.id.layout1, pageMybook);
                    begintransaction.commit();

                }

            }
        });
    }*/


    public void initialbottombar (){
        btn_edit=(ImageButton)findViewById(R.id.edit);
        btn_mybook=(CheckBox)findViewById(R.id.btn_mybook);
        btn_bookgroup=(CheckBox)findViewById(R.id.btn_bookgroup);
        btn_mybook.setChecked(true);
        btn_bookgroup.setChecked(false);
        final FragmentManager fragmentManager=getFragmentManager();
        final FragmentTransaction begintransaction=fragmentManager.beginTransaction();
        begintransaction.add(R.id.layout1, pageMybook);
        begintransaction.commit();

        btn_mybook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_mybook.setBackgroundResource(R.drawable.ui_mybook_pressed);
                    btn_bookgroup.setChecked(false);
                    btn_bookgroup.setBackgroundResource(R.drawable.ui_bookgroup_unpressed);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction begintransaction = fragmentManager.beginTransaction();
                    begintransaction.replace(R.id.layout1, pageMybook);
                    begintransaction.commit();
                }

            }
        });
        btn_bookgroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_mybook.setChecked(false);
                    btn_bookgroup.setBackgroundResource(R.drawable.ui_bookgroup_pressed);
                    btn_mybook.setBackgroundResource(R.drawable.ui_mybook_unpressed);
                    FragmentManager fragmentManager=getFragmentManager();
                    FragmentTransaction begintransaction=fragmentManager.beginTransaction();
                    begintransaction.replace(R.id.layout1, pageBooks);
                    begintransaction.commit();

                }
            }
        });
    }

    void  initialdownload() {


    }

    public Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(mMainActivity, "下载完毕", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mMainActivity=this;
        mDao=new ThreadDAOImpl(mMainActivity);



       initialbottombar();
        initialdownload();
        newVersion=2.0;
        if (getServerVersion()) {
            Log.i("NEWVersion", "" + newVersion);
            if ((newVersion - AppVersion) >= 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("警告");
                builder.setMessage("当前软件版本过低，请重新下载。" +
                        "官方网站:www.neverignore.cn");
                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                builder.show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back) {
            android.os.Process.killProcess(android.os.Process.myPid());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction(DownloadService.ACTION_STOPALL);
        startService(intent);

        super.onDestroy();
    }
}
