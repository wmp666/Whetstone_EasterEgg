package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.CTColor;
import com.wmp.PublicTools.UITools.CTFont;
import com.wmp.PublicTools.UITools.CTFontSizeStyle;
import com.wmp.PublicTools.UITools.GetIcon;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EasterEggUnit extends BasicEasterEggUnit {

    private final JDialog textFrame = new JDialog(new Frame(), "班主任");

    private Thread thread;
    @Override
    public String getID() {
        return "NJHacking";
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
        return "显示一段文字在在左上角\n可用参数:文字, 显示时间(min)";
    }

    @Override
    public void run(String[] args) {
        Log.trayIcon.displayMessage("班主任", "班主任已成功监管电脑,不要搞小动作", TrayIcon.MessageType.WARNING);

        {
            JDialog frame = new JDialog();
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException _) {
                    }
                    e.getWindow().setVisible(false);
                }
            });

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            JLabel label = new JLabel(GetIcon.getIcon(ClassFormPanel.class.getResource("/image/nj_dunk_2.png"), screenSize.width, screenSize.height, false));
            frame.add(label);


            frame.pack();
            frame.setLocation(0, 0);

            frame.setVisible(true);
        }




        textFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        textFrame.setUndecorated(true);
        textFrame.setAlwaysOnTop(true);
        textFrame.setBackground(new Color(0,0,0,0));
        textFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new Thread(()->{
                    try {
                        Thread.sleep(Integer.parseInt(args[1])*60*1000);
                    } catch (InterruptedException _) {
                    }
                    e.getWindow().setVisible(false);
                }).start();

            }
        });

        JLabel label = new JLabel(args[0]);
        label.setForeground(CTColor.mainColor);
        label.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.BIG));
        textFrame.add(label);

        ((JPanel)textFrame.getContentPane()).setOpaque(false);

        textFrame.pack();
        textFrame.setLocation((int) (100 * CTInfo.dpi), (int) (100 *CTInfo.dpi));

        textFrame.setVisible(true);
    }

    @Override
    public void clear() {
        textFrame.dispose();
    }
}
