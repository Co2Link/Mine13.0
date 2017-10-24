package com.woc.mine;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CursorAdapter;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import static android.content.res.Resources.getSystem;

/**
 * Created by zyw on 2016/7/29.
 */
public class MainActivity extends Activity {
    private TextView MyView;
    private MainView MyMainView;
    Intent intent=null;
    public boolean ismute=false;
    public static ImageView imageVie;

    public static Bitmap bmp;

    //计时
    public static int recLen=0;
    public static int subrecLen=0;
    public static boolean flag_time=false;
    //计时

    public static int mode=1;
    private SQLiteDatabase db;
    public static boolean first_record=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageVie=new ImageView(this);

        bmp=BitmapFactory.decodeResource(getResources(),R.drawable.huaji);

        setContentView(R.layout.layout);
        MyView=(TextView) findViewById(R.id.Mytime);
        MyMainView=(MainView)findViewById(R.id.MyView) ;
        handler.postDelayed(runnable,0);



//        BGM
        intent=new Intent(this,BGM.class);
        bindService(intent,conn, Service.BIND_AUTO_CREATE);


        final Button bn_BGM=(Button) findViewById(R.id.mute);
        bn_BGM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ismute) {
                    binder.unmute();
                    ismute=false;
                    bn_BGM.setText("mute");
                }
                else {
                    binder.mute();
                    ismute=true;
                    bn_BGM.setText("unmute");
                }
            }
        });
        //数据库
        //打开或创建数据库
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()
                        + "/my.db3", null);
        //数据库

    }
    @Override
    protected  void onStop() {
        //必须两句一起才能stopservice
        stopService(intent);
        unbindService(conn);
        super.onStop();
    }
    @Override
    protected  void onRestart() {

        bindService(intent,conn,Service.BIND_AUTO_CREATE);
        super.onRestart();
    }

    BGM.MyBinder binder;

    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder=(BGM.MyBinder)iBinder;
            startService(intent);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            stopService(intent);
        }
    };

    //计时
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(recLen==0)MyView.setText(""+recLen);
            if(flag_time&&subrecLen%10==0){
                MyView.setText(""+recLen);

                recLen++;
            }
                subrecLen++;
                handler.postDelayed(this,100);
        }
    };
    //查看数据库

    private void inflateList(Cursor cursor, ListView listView)
    {
        // 填充SimpleCursorAdapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                MainActivity.this,
                R.layout.line, cursor,
                new String[] { "news_id", "news_time"}
                , new int[] {R.id.my_id, R.id.my_time },
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);  // ③
        // 显示数据
        listView.setAdapter(adapter);
    }
    public void query(View source)
    {
        //判断是否已有记录，表是否已创建
        if(!first_record) {
            Toast.makeText(this,"尚未有记录",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            LinearLayout MyView=(LinearLayout)getLayoutInflater().inflate(R.layout.query,null);
            final ListView listView=(ListView)MyView.findViewById(R.id.MyListView);

            final RadioGroup MyRdG=(RadioGroup)MyView.findViewById(R.id.q_MyRadioGroup);

                MyRdG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        Cursor cursor=null;
                        int button_id=MyRdG.getCheckedRadioButtonId();
                        switch (button_id) {
                            case R.id.q_primary:
                                try {
                                    cursor=db.rawQuery("select * from news_inf where news_modes==1 ORDER BY news_time",null);
                                    inflateList(cursor,listView);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.q_standard:
                                cursor=db.rawQuery("select * from news_inf where news_modes==2 ORDER BY news_time",null);
                                inflateList(cursor,listView);
                                break;
                            case R.id.q_advanced:
                                cursor=db.rawQuery("select * from news_inf where news_modes==3 ORDER BY news_time",null);
                                inflateList(cursor,listView);
                                break;
                        }
                    }
                });
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setMessage("记录")
                    .setView(MyView)
                    .create()
                    .show();
        }
    }

    //自定义
    public void setcustom(View source)
    {
        @SuppressWarnings("ResourceType")
        LinearLayout MyView=(LinearLayout)getLayoutInflater().inflate(R.layout.setcustom,null);
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                // 设置对话框标题
                .setTitle("自定义")
                // 设置图标
                .setView(MyView);

        setPositiveButton(builder,MyView)
                .create()
                .show();
    }
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder,final LinearLayout MyView)
    {
        // 调用setPositiveButton方法添加“确定”按钮
        return builder.setPositiveButton("确定", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                RadioGroup rg=(RadioGroup)MyView.findViewById(R.id.MyRadioGroup);
                int checked_id=rg.getCheckedRadioButtonId();

                MainView MyMainView=(MainView) findViewById(R.id.MyView);
                switch (checked_id) {
                    case R.id.primary:
                        MyMainView.setcustom(6,10,6);
                        mode=1;
                        break;
                    case R.id.standard:
                        MyMainView.setcustom(8,12,12);
                        mode=2;
                        break;
                    case R.id.advanced:
                        MyMainView.setcustom(10,16,24);
                        mode=3;
                        break;
                    case R.id.my_custom:
                        mode=0;
                        EditText T1=(EditText) MyView.findViewById(R.id.setH);
                        EditText T2=(EditText) MyView.findViewById(R.id.setW);
                        EditText T3=(EditText) MyView.findViewById(R.id.setM);
                        int H=0,W=0,M=0;
                        if(!TextUtils.isEmpty(T1.getText())&&!TextUtils.isEmpty(T2.getText())&&!TextUtils.isEmpty(T3.getText())) {
                            H=Integer.parseInt(T1.getText().toString());
                            W=Integer.parseInt(T2.getText().toString());
                            M=Integer.parseInt(T3.getText().toString());
                            if(H>25||W>16) {
                                Toast.makeText(getApplicationContext(),"Height最大值为25,Width最大值为16",Toast.LENGTH_LONG).show();
                            }
                            else if(M>(H-1)*(W-1)){
                                Toast.makeText(getApplicationContext(),"Num最大值为"+(H-1)*(W-1)+"-->(H-1)*(W-1)",Toast.LENGTH_LONG).show();
                            }
                            else {
                                MyMainView.setcustom(W,H,M);
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"请输入自定义值",Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }
}
