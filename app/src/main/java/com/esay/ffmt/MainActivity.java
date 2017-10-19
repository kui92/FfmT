package com.esay.ffmt;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.esay.ffmtool.FfmpegTool;

import java.io.File;

import static android.R.attr.path;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    private FfmpegTool ffmpegTool=FfmpegTool.getInstance(MainActivity.this);
    private String clipResutl="";

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ffmpegTool.setImageDecodeing(new FfmpegTool.ImageDecodeing() {
            @Override
            public void sucessOne(String path, int i) {
                Log.i("decodToImageCall","path:"+path+"___index:"+i);
            }
        });
    }

    /**
     * 视频裁剪
     * @param view
     */
    public void click1(View view){
       new Thread(){
           @Override
           public void run() {
               String basePath = Environment.getExternalStorageDirectory().getPath();
               String dir=basePath+File.separator+"test"+File.separator;
               String videoPath=dir+"c.mp4";
               String out=dir+"out.mp4";
               //参数说明 视频源  输出结果地址 开始时间单位s  视频时长单位s  标志位  回调
               ffmpegTool.clipVideo(videoPath, out, 10, 10, 1, new FfmpegTool.VideoResult() {
                   @Override
                   public void clipResult(int code, String src, String dst, boolean sucess, int tag) {
                       Log.i("MainActivity","code:"+code);
                       Log.i("MainActivity","src:"+src);
                       Log.i("MainActivity","dst:"+dst);
                       Log.i("MainActivity","sucess:"+sucess);
                       Log.i("MainActivity","tag:"+tag);
                       clipResutl=dst;
                   }
               });

           }
       }.start();
    }

    /**
     * 解码成图片
     * @param view
     */
    public void click2(View view){
        new  Thread(){
            @Override
            public void run() {
                String path= Environment.getExternalStorageDirectory().getPath()+ File.separator+"test"+File.separator;
                String video=path+"c.mp4";
                FfmpegTool.getInstance(MainActivity.this).videoToImage(video.replaceAll(File.separator, "/"), path.replaceAll(File.separator, "/"), 0, 60, new FfmpegTool.VideoResult() {
                    @Override
                    public void clipResult(int code, String src, String dst, boolean sucess, int tag) {
                        Log.i("MainActivity","code:"+code);
                        Log.i("MainActivity","src:"+src);
                        Log.i("MainActivity","dst:"+dst);
                        Log.i("MainActivity","sucess:"+sucess);
                        Log.i("MainActivity","tag:"+tag);
                    }
                },0);
            }
        }.start();

    }

    /**
     * 视频压缩
     */
    public void click3(View view){
        new Thread(){
            @Override
            public void run() {
                String path= Environment.getExternalStorageDirectory().getPath()+ File.separator+"test"+File.separator;
                //参数说明  视频源  压缩结果 标志位 回调
                FfmpegTool.getInstance(MainActivity.this).compressVideo("/storage/emulated/0/test/out.mp4", path, 2, new FfmpegTool.VideoResult() {
                    @Override
                    public void clipResult(int code, String src, String dst, boolean sucess, int tag) {
                        Log.i("MainActivity","code:"+code);
                        Log.i("MainActivity","src:"+src);
                        Log.i("MainActivity","dst:"+dst);
                        Log.i("MainActivity","sucess:"+sucess);
                        Log.i("MainActivity","tag:"+tag);
                    }
                });
            }
        }.start();
    }


    public void click4(View view){
        new Thread(){
            @Override
            public void run() {
                String path= Environment.getExternalStorageDirectory().getPath()+ File.separator+"test"+File.separator;
                String video=path+"c.mp4";
                ffmpegTool.decodToImageWithCall(video.replaceAll(File.separator,"/")
                        ,Environment.getExternalStorageDirectory().getPath()
                                + File.separator+"test2"+File.separator,0,10);
            }
        }.start();
    }

}
