package com.shudong.lib_base.utils

import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.widget.ImageView
import com.shudong.lib_base.ext.i
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException

class ChatController {
    private var mPosition = -1 // 和mSetData一起组成判断播放哪条录音的依据
    private val mp = MediaPlayer()
    private var mFIS: FileInputStream? = null
    private var mFD: FileDescriptor? = null
    private val mIsEarPhoneOn = false
    private var mVoiceAnimation: AnimationDrawable? = null
    fun setVoice(path: String?, isMe: Boolean?, ivVoice: ImageView, position: Int) {


        // 如果之前存在播放动画，无论这次点击触发的是暂停还是播放，停止上次播放的动画
        if (mVoiceAnimation != null) {
            "我要停止动画了------".i("zy1996")
            ivVoice.setBackgroundResource(com.shudong.lib_res.R.drawable.icon_voice_msg)
            mVoiceAnimation!!.stop()
        }
        if (mp.isPlaying && mPosition == position) {

            /* if(message.getDirect()== MessageDirect.receive){
                holder.setImageResource(R.id.iv_voice,R.drawable.voice_reciever_3);
            }else {
                holder.setImageResource(R.id.iv_voice,R.drawable.voice_sender_3);
            }*/
            pauseVoice()
        } else {


            //if (lastHolder != null) {
            /*if(lastMessage.getDirect()==MessageDirect.receive){
                    lastHolder.setImageResource(R.id.iv_voice, R.drawable.voice_reciever_3);
                }else {
                    lastHolder.setImageResource(R.id.iv_voice, R.drawable.voice_sender_3);
                }*/

            // }

            /*if(message.getDirect()== MessageDirect.receive){
                holder.setImageResource(R.id.iv_voice, R.drawable.voice_recieve);
            }else {
                holder.setImageResource(R.id.iv_voice, R.drawable.voice_send);
            }*/
            //ivVoice.setImageResource(R.drawable.iconv);

            ivVoice.setBackgroundResource(com.shudong.lib_res.R.drawable.animation_voice_play)
            mVoiceAnimation = ivVoice.background as AnimationDrawable
            mVoiceAnimation?.start()
            //mVoiceAnimation = ivVoice.drawable as AnimationDrawable
           /* if (mVoiceAnimation != null) {
                mVoiceAnimation!!.start()
            }*/
            playVoice(position, path,ivVoice)
        }
    }

    private fun pauseVoice() {
        mp.pause()
    }

    // public void playVoice(final int position, VoiceContent voiceContent, ViewHolder holder,Message message) {
    fun playVoice(position: Int, path: String?,ivVoice: ImageView) {

        // lastHolder = holder;
        //lastMessage = message;
        // 记录播放录音的位置
        mPosition = position
        try {
            mp.reset()
            mFIS = FileInputStream(path)
            mFD = mFIS!!.fd
            mp.setDataSource(mFD)
            if (mIsEarPhoneOn) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            mp.prepare()
            mp.setOnPreparedListener { mp ->
                mVoiceAnimation!!.start()
                mp.start()
            }
            mp.setOnCompletionListener { mp ->
                ivVoice.setBackgroundResource(com.shudong.lib_res.R.drawable.icon_voice_msg)
                mVoiceAnimation!!.stop()

                /* if(message.getDirect()== MessageDirect.receive){
                                holder.setImageResource(R.id.iv_voice,R.drawable.voice_reciever_3);
                            }else {
                                holder.setImageResource(R.id.iv_voice,R.drawable.voice_sender_3);
                            }*/mp.reset()
            }
        } catch (e: Exception) {
            // ToastUtil.getInstance().toast("文件没有发现");
        } finally {
            try {
                if (mFIS != null) {
                    mFIS!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    /*public  List<Double> setPictureScale(String extra, Message message, String path, final ImageView imageView) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);


        //计算图片缩放比例
        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        //XLog.d("当前图片的获取到的宽高度是======"+imageWidth+"==="+imageHeight);
        return setDensity(extra, message, imageWidth, imageHeight, imageView);
    }

    private List<Double> setDensity(String extra, Message message, double imageWidth, double imageHeight, ImageView imageView) {
        if (extra != null) {
            imageWidth = 200;
            imageHeight = 200;
        } else {
            if (imageWidth > 350) {
                imageWidth = 300;
                imageHeight = 50;
            } else if (imageHeight > 450) {
                imageWidth = 300;
                imageHeight = 450;
            } else if ((imageWidth < 50 && imageWidth > 20) || (imageHeight < 50 && imageHeight > 20)) {
                imageWidth = 200;
                imageHeight = 300;
            } else if (imageWidth < 20 || imageHeight < 20) {
                imageWidth = 100;
                imageHeight = 150;
            } else {
                imageWidth = 300;
                imageHeight = 450;
            }
        }

        //XLog.d("最终图片的获取到的宽高度是======"+imageWidth+"==="+imageHeight);


      */
    /*  ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);*/
    /*
        List<Double> list = new ArrayList<>();
        list.add(0,imageWidth);
        list.add(1,imageHeight);

        return list;
    }
*/
    companion object {
        //private ViewHolder lastHolder;
        //private Message lastMessage;
        private var mInstance: ChatController? = null
        val instance: ChatController?
            get() {
                if (mInstance == null) {
                    synchronized(ChatController::class.java) {
                        if (mInstance == null) mInstance = ChatController()
                    }
                }
                return mInstance
            }
    }
}