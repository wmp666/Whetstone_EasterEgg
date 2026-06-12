package com.wmp.whetstone;

import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EasterEggUnit extends BasicEasterEggUnit {

    private Thread thread;
    @Override
    public String getID() {
        return "screenBlocking";
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
        return "再过一段时间后遮挡屏幕\n可用参数: 启动前最长等待时间(min) 显示时间(s) 显示次数";
    }

    @Override
    public void run(String[] args) {
        try {
            for(int i = 0;i < Integer.parseInt(args[2]);i++){
                blocking(args);
            }
        } catch (Exception e) {
            Log.err.print(EasterEggUnit.class, "遮挡屏幕时发生错误", e);
        }
    }

    private static void blocking(String[] args) throws InterruptedException, AWTException {
        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();


        int waitTime = (new Random().nextInt(Integer.parseInt(args[0])) + 1);
        Log.info.print(ClassFormPanel.class.toString(), String.format("预启动：屏幕遮挡|参数：启动前等待时间:%smin;显示时间:%ss;大小：%s", waitTime, args[1], d));

        Thread.sleep((long) waitTime * 60 * 1000);

        //创建一个robot对象
        Robot robot = new Robot();

        //创建该分辨率的矩形对象
        Rectangle screenRect = new Rectangle(d);
        //根据这个矩形截图
        BufferedImage bufferedImage = robot.createScreenCapture(screenRect);

        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);

        final boolean[] b = {false};
        Object temp = 0;
        Runnable r = () -> {
            synchronized (temp){
                if (b[0]) return;
                b[0] = true;
                try {
                    Thread.sleep(Integer.parseInt(args[1]) * 1000L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                dialog.setVisible(false);
            }
        };

        JLabel image = new JLabel(new ImageIcon(bufferedImage));
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                r.run();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                r.run();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                r.run();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                r.run();
            }
        });
        dialog.add(image);

        dialog.pack();

        dialog.setVisible(true);
        Log.info.print(ClassFormPanel.class.toString(), "屏幕遮挡完毕！");
    }

    @Override
    public void clear() {

    }
}
