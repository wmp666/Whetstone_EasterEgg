package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;
import com.wmp.whetstone.tools.ResourceLocalizer;

import java.io.File;
import java.util.Random;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "videoPlayer";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "2.1.0";
    }

    @Override
    public String help() {
        return "用于播放随机的一个视频\n输入参数: 无";
    }

    @Override
    public void run(String[] args) {
        String[] choices = {"sjz", "ys", "zmd" ,"cs", "bt"};

        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "VideoPlayer.exe");
        String choice = choices[new Random().nextInt(choices.length)];
        Log.info.print(getClass().toString(), "选择的视频；" + choice);
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/video/" + choice + "/", "video.mp4");

        new Thread(()->{

            try {
                Thread.sleep(3*60*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                Process process = Runtime.getRuntime().exec(new String[]{CTInfo.TEMP_PATH + "\\Whetstone\\VideoPlayer.exe"}, null, new File(CTInfo.TEMP_PATH, "\\Whetstone\\"));
                int status = process.waitFor();

                Log.info.print(ClassFormPanel.class.toString(), "VideoPlayer.exe关闭：" + status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "三角洲启动！").start();
    }

    @Override
    public void clear() {

    }
}
