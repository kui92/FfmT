package com.esay.ffmtool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static android.R.attr.tag;

/**
 * Created by ZBK on 2017/9/28.
 * Describe:
 */

public class FfmpegTool {
    /**
     * 裁剪视频的回调接口
     */
    public interface VideoResult{
        /**
         * 裁剪结果回调
         * @param code 返回码
         * @param src 视频源
         * @param dst 裁剪结果保存地址
         * @param sucess 裁剪是否成功 true 成功
         */
        public void clipResult(int code,String src,String dst,boolean sucess,int tag);
    }
    private static FfmpegTool instance=new FfmpegTool();

    private final int CLIP=0;
    private final int DECOD_IMAGE=1;
    private final int COMPRESS=2;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Result result= (Result) msg.obj;
            if (msg.what==CLIP){
                if (msg.arg1==0){
                    if (result.call!=null)result.call.clipResult(result.retCode,result.src,result.dst,true,result.tag);
                }else {
                    if (result.call!=null)result.call.clipResult(result.retCode,result.src,result.dst,false,result.tag);
                }
            }else if (msg.what==DECOD_IMAGE){
                if (msg.arg1==0){
                    if (result.call!=null)result.call.clipResult(result.retCode,result.src,result.dst,true,result.tag);
                }else {
                    if (result.call!=null)result.call.clipResult(result.retCode,result.src,result.dst,false,result.tag);
                }
            }else if (msg.what==COMPRESS){
                if (msg.arg1==0){
                    if (result.call!=null)result.call.clipResult(result.retCode,result.src,result.dst,true,result.tag);
                }else {
                    if (result.call!=null)result.call.clipResult(result.retCode,result.src,result.dst,false,result.tag);
                }
            }
        }
    };
    private  FfmpegTool(){

    }



    public static FfmpegTool getInstance(){
        if (instance==null){
            synchronized(FfmpegTool.class){
                if (instance == null)instance = new FfmpegTool();
            }
        }
        return instance;
    }

     class Result{
        public String src;
        public String dst;
        public int retCode;
        public int tag;
        public VideoResult call;
    }

    static {
        System.loadLibrary("avutil");
        System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("swresample");
        System.loadLibrary("avfilter");
        System.loadLibrary("jxffmpegrun");
    }
    public static native int cmdRun(String[] cmd);

    public static native int decodToImage(String srcPath,String savePath,int startTime,int count);



    public int videoToImage(String src,String dir,int startTime,int count,VideoResult call){
        int result=-1;
        result=decodToImage(src,dir,startTime,count);
        if (call!=null){
            Result result1=new Result();
            result1.src=src;
            result1.dst=dir;
            result1.retCode=result;
            result1.call=call;
            result1.tag=tag;
            Message message=new Message();
            message.what=DECOD_IMAGE;
            message.arg1=result;
            message.obj=result1;
            handler.sendMessage(message);
        }
        return result;
    }


    //ffmpeg -y -ss 10 -t 15 -i /storage/emulated/0/test/c.mp4 -vcodec copy -acodec copy -strict -2 /storage/emulated/0/test/out.mp4

    /**
     * 裁剪视频
     * @param src 视频源地址
     * @param dst 裁剪结果
     * @param startTime 开始裁剪时间
     * @param duration 裁剪时长
     * @return
     */
    public  int clipVideo(String src,String dst,int startTime,int duration,int tag,VideoResult call){
        String cmd=String.format("ffmpeg -y -ss "+startTime+" -t "+duration+
        " -i "+src+" -vcodec copy -acodec copy -strict -2 "+dst);
        String regulation="[ \\t]+";
        Log.i("clipVideo","cmd:"+cmd);
        final String[] split = cmd.split(regulation);
        int result=-1;
        result=cmdRun(split);
        if (call!=null){
            Result result1=new Result();
            result1.src=src;
            result1.dst=dst;
            result1.retCode=result;
            result1.call=call;
            result1.tag=tag;
            Message message=new Message();
            message.what=COMPRESS;
            message.arg1=result;
            message.obj=result1;
            handler.sendMessage(message);
        }
        return result;
    }

//ffmpeg -y -i /storage/emulated/0/esay/temp/temp1506592501.mp4 -b:v 1500k -bufsize 1500k -maxrate 2000k -g 26 /storage/emulated/0/esay/temp/temp1506592521.mp4

    /**
     * 压缩视频
     * @param src 视频源地址
     * @param dst 结果目录
     * @param tag 标志位
     * @param call 回调
     * @return
     */
    public int compressVideo(String src,String dst,int tag,VideoResult call){
        dst=dst+"temp"+System.currentTimeMillis()/1000+".mp4";
        String cmd=String.format("ffmpeg -threads 32 -y -i "+src
                +" -b:v 1500k -bufsize 3000k -maxrate 2000k -preset superfast "+dst);
        //cmd="ffmpeg -threads 64 -i /storage/emulated/0/test/out.mp4 -c:v libx264  -x264opts bitrate=2000:vbv-maxrate=2500  -crf 20 -preset superfast  -vbr 4   /storage/emulated/0/test/tes.mp4";
        String regulation="[ \\t]+";
        Log.i("compressVideo","cmd:"+cmd);
        final String[] split = cmd.split(regulation);
        int result=-1;
        result=cmdRun(split);
        if (call!=null){
            Result result1=new Result();
            result1.src=src;
            result1.dst=dst;
            result1.retCode=result;
            result1.call=call;
            result1.tag=tag;
            Message message=new Message();
            message.what=COMPRESS;
            message.arg1=result;
            message.obj=result1;
            handler.sendMessage(message);
        }
        Log.i("compressVideo","result:"+result);
        return result;
    }


}
