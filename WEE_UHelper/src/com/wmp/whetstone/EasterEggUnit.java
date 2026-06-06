package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.easter_egg_control.BasicEasterEggUnit;
import com.wmp.whetstone.tools.ResourceLocalizer;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "UHelper";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "2.0.0";
    }

    @Override
    public String help() {
        return "用于拦截所有U盘的插入\n可以设置运行参数：[个数]";
    }

    @Override
    public void run(String[] args) {
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "Uhelper.exe");


        //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
        thread = new Thread(() -> {

            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{CTInfo.TEMP_PATH + "\\Whetstone\\Uhelper.exe"});
                    int status = process.waitFor();

                    Log.info.print(ClassFormPanel.class.toString(), "UHelper.exe关闭：" + status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, "U盘助手");
        thread.start();
    }

    @Override
    public void clear() {
        thread.interrupt();
        //关闭现有的U盘助手
        WinAPIEntireFunction.killProcess("UHelper.exe", 100);

    }
}
