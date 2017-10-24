package com.woc.mine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.util.Date;
import java.util.Timer;

/**
 * Created by zyw on 2016/7/29.
 */
public class MainView extends SurfaceView implements SurfaceHolder.Callback{
    SQLiteDatabase db;
    private SurfaceHolder holder;
    private   Mine mine;
    private  boolean isFirst=true;//标记是否是本局第一次点击屏幕
    private  Context context;
    private  int mineNum=6;//产生的雷的个数
    private  final int ROW=10;//要生成的矩阵高
    private  final int COL=6;//要生成的矩阵宽
    private   int TILE_WIDTH=50;//块大小
    private  boolean isFalse=false;
    private int H;
    private int W;
    private long t1=0;
    private boolean flag_Longpress;
    private MainActivity MyActivity;
    private LongPressThread MyLongPressThread=new LongPressThread();


    public MainView(Context context,AttributeSet attrs) {
        super(context,attrs);
        this.context=context;
        MyActivity=(MainActivity)context;
        holder=getHolder();
        holder.addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){

    }
    public void surfaceCreated(SurfaceHolder holder){
        new renderThread().start();
    }
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        获取控件大小，并设置方块大小
        H=getHeight();
        W=getWidth();
        TILE_WIDTH=W/(COL)>H/(ROW)?H/(ROW):W/(COL);
        mine=new Mine((W-COL*TILE_WIDTH)/2,(H-ROW*TILE_WIDTH)/2,COL,ROW,mineNum,TILE_WIDTH);
        try {
            mine.init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //自定义
    public void setcustom(int COL1,int ROW1,int Num) {
        H=getHeight();
        W=getWidth();
        MainActivity.subrecLen=0;
        MainActivity.recLen=0;
        MainActivity.flag_time=false;
        mineNum=Num;
        TILE_WIDTH=W/10;
        TILE_WIDTH=W/(COL1)>H/(ROW1)?H/(ROW1):W/(COL1);
        mine=new Mine((W-COL1*TILE_WIDTH)/2,(H-ROW1*TILE_WIDTH)/2,COL1,ROW1,Num,TILE_WIDTH);
        mine.init();
        new renderThread().start();
        isFirst=true;
    }
    //自定义

    //数据库
    private void insertData(SQLiteDatabase db
            , String id, int time,String mode)  // ②
    {
        // 执行插入语句
        //采用ignore防止插入相同主键的数据
        db.execSQL("insert OR IGNORE into news_inf values(null, ? , ? , ?)"
                , new Object[] {id, time,mode });

    }
    //数据库


    //自定义View若要用xml的layout显示，必须实现  xx（）

    /**
     * 判断胜利
     */
    public void logic()
    {
        int count=0;


        for (int i=0;i<mine.mapRow;i++)
        {
            for (int j=0;j<mine.mapCol;j++)
            {
                if(!mine.tile[i][j].open)
                {
                    count++;
                }
            }
        }
        //逻辑判断是否胜利
        if(count==mineNum)
        {
            //打开或创建数据库

                db = SQLiteDatabase.openOrCreateDatabase(
                        MyActivity.getFilesDir().toString()
                                + "/my.db3", null);

            final LinearLayout MyView=(LinearLayout)MyActivity.getLayoutInflater().inflate(R.layout.score,null);

            TextView MyTextView_time=(TextView)MyView.findViewById(R.id.mytime);
            TextView MyTextView_mode=(TextView)MyView.findViewById(R.id.score_mode);
            MyTextView_time.setText(String.valueOf(MainActivity.recLen));

            final int buf_time=MainActivity.recLen;

            //非自定义模式时，记录成绩
            if(MainActivity.mode!=0) {
                switch (MainActivity.mode) {
                    case 1:
                        MyTextView_mode.setText("初级");
                        break;
                    case 2:
                        MyTextView_mode.setText("中级");
                        break;
                    case 3:
                        MyTextView_mode.setText("高级");
                        break;
                }
                new AlertDialog.Builder(context)
                        .setMessage("通关！！！")
                        .setView(MyView)
                        .setCancelable(false)
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText MyEditText=(EditText) MyView.findViewById(R.id.play_name);
                                String id=MyEditText.getText().toString();
                                if(id.isEmpty())id="不留名";
                                try
                                {
                                    insertData(db, id, buf_time,String.valueOf(MainActivity.mode));
                                }
                                catch (SQLiteException se)
                                {
                                    // 执行DDL创建数据表
                                    db.execSQL("create table news_inf(_id int,"
                                            + "news_id varchar(50),"
                                            + " news_time int,"
                                            + " news_modes  varchar(50),"
                                            +"constraint pk_t2 primary key (news_id,news_time))");

                                    // 执行insert语句插入数据
                                    insertData(db, id, buf_time,String.valueOf(MainActivity.mode));
                                }
                                MainActivity.first_record=true;
                                mine.init();
                                new renderThread().start();
                            }
                        })
                        .create()
                        .show();
            }
            else {
                new AlertDialog.Builder(context)
                        .setMessage("恭喜你，你找出了所有雷")
                        .setCancelable(false)
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mine.init();
                                new renderThread().start();
                            }
                        })
                        .create()
                        .show();
            }
            isFirst=true;
            MainActivity.flag_time=false;
            MainActivity.subrecLen=0;
            MainActivity.recLen=0;
        }
    }

    /**
     * 点击屏幕事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x=(int)event.getX();
        int y=(int)event.getY();
        if(x>=mine.x&&y>=mine.y&&x<=(mine.mapWidth+mine.x)&&y<=(mine.y+mine.mapHeight))
        {

            int idxX=(x-mine.x)/mine.tileWidth;
            int idxY=(y-mine.y)/mine.tileWidth;
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                flag_Longpress=false;
                if(MyLongPressThread!=null)
                {
                    MyLongPressThread.set(idxX,idxY);
                    postDelayed(MyLongPressThread,500);    //延时0.5s执行MyLongPressThread
                }

            }
            if(event.getAction()==MotionEvent.ACTION_UP)
            {
                if(MyLongPressThread!=null)
                {
                    removeCallbacks(MyLongPressThread);   //ACTION_UP时，去掉MyLongPressThread
                }
                if(!flag_Longpress)  //若已触发长按，则不执行open操作
                {
                    mine.open_recursion(new Mine.Point(idxX,idxY),isFirst);
                    //计时
                    if(isFirst)
                    {
                        MainActivity.flag_time=true;
                        MainActivity.subrecLen=0;
                    }

                    //计时
                    isFirst=false;
                    if(mine.tile[idxY][idxX].value==-1)
                    {
                        MainActivity.imageVie.setBackgroundResource(R.drawable.radio);
                        mine.isDrawAllMine=true;
                        new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage("你踩到雷了~~~")
                                .setView(MainActivity.imageVie)
                                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mine.init();
                                        isFalse=true;
                                        isFirst=true;
                                        MainActivity.recLen=0;

                                        new renderThread().start();
                                    }
                                })
                                .create()
                                .show();
                    }
                    logic();
                    new renderThread().start();
                }

            }
        }
        return true;
    }


    class LongPressThread extends Thread
    {
        int x,y;
        @Override
        public void run() {
            if(mine.tile[y][x].flag)
            {
                mine.tile[y][x].flag=false;
            }
            else
            {
                mine.tile[y][x].flag=true;
            }
            flag_Longpress=true;
            new renderThread().start();
        }
        public void set(int x,int y) {
            this.x=x;
            this.y=y;
        }
    }

    class renderThread extends Thread
    {
        @Override
        public void run() {
            synchronized (holder) {
                Canvas canvas=holder.lockCanvas();
                mine.draw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
