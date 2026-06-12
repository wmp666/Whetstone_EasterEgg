package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.GetIcon;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;
import com.wmp.whetstone.tools.ResourceLocalizer;
import com.wmp.whetstone.tools.windowsAPI.WinAPIEntireFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "chuizis";
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
        return "锤子!\n可用参数:error";
    }

    @Override
    public void run(String[] args) {
        try {
            {
                JDialog frame = new JDialog();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setUndecorated(true);
                frame.setAlwaysOnTop(true);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException _) {
                        }
                        e.getWindow().setVisible(false);
                    }
                });

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                JLabel label = new JLabel(GetIcon.getIcon(ClassFormPanel.class.getResource("/image/cxx.jpg"), screenSize.width, screenSize.height, false));
                frame.add(label);

                frame.pack();
                frame.setLocation(0, 0);

                frame.setVisible(true);
            }

            WinAPIEntireFunction.invertScreenWithJNA();

            for (int i = 0; i < 5; i++) {
                WinAPIEntireFunction.pressKey(WinAPIEntireFunction.VK_LWIN);

                Thread.sleep(500);

            }


            ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "chuizis.exe");
            ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "xxx.mp3");


            //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"});

            WinAPIEntireFunction.clearInvertScreen();
        } catch (Exception e) {
            Log.err.print(EasterEggUnit.class, "运行锤子时发生错误", e);
        }
    }

    @Override
    public void clear() {

    }
}
