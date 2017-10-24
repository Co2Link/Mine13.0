//package com.woc.mine;
//
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.util.DisplayMetrics;
//import android.view.View;
//import android.widget.Toast;
//import android.widget.Button;
//
///**
// * Created by zyw on 2016/7/29.
// */
//public class MainActivity extends Activity {
//    public  static  int W;
//    public  static  int H;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        W = dm.widthPixels;//宽度
//        H = dm.heightPixels ;//高度
//
//        setContentView(new MainView(this));
//        Button MyButton=(Button)findViewById(R.id.set);
//        MyButton.setOnClickListener(new MyClicListener());
//
//
//
//        new AlertDialog.Builder(this)
//                .setCancelable(false)
//                .setTitle("游戏规则")
//                .setMessage("把你认为不是雷的位置全部点开，只留着有雷的位置，每局游戏有10个雷。\n\n--卧槽工作室")
//                .setPositiveButton("我知道了",null)
//                .create()
//                .show();
//    }
//    class MyClicListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v)
//        {
//            View myview=findViewById(R.id.MyView);
//            int W=myview.getWidth();
//            int H=myview.getHeight();
//            String fuck=String.valueOf(W)+"-"+String.valueOf(H);
//            Toast myToast=Toast.makeText(MainActivity.this,fuck,Toast.LENGTH_LONG);
//            myToast.show();
//        }
//    }
//}
