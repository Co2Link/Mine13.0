//package com.woc.mine;
//
///**
// * Created by Administrator on 2017/5/30.
// */
//
//import android.graphics.Canvas;
//import android.view.SurfaceHolder;
//
//public class RenderThread extends Thread{
//    private SurfaceHolder surfaceHolder;
//    private MainView MyView;
//
//    public RenderThread(SurfaceHolder MyHold,MainView MyView1) {
//        this.surfaceHolder=MyHold;
//        this.MyView=MyView1;
//    }
//    public void run() {
//
//        Canvas canvas=null;
//        try {
//            canvas=this.surfaceHolder.lockCanvas();
//            synchronized (surfaceHolder) {
//                this.MyView.doDraw(canvas);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            if(canvas!=null) {
//                this.surfaceHolder.unlockCanvasAndPost(canvas);
//            }
//        }
//
//
//    }
//}
