package com.wmp.whetstone;

import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class EasterEggUnit extends BasicEasterEggUnit {

    private File[] files;
    @Override
    public String getID() {
        return "RandomAccessFile";
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
        return "创建稀疏文件";
    }

    @Override
    public FuncHelpUnit[] funcHelps() {
        return new FuncHelpUnit[]{
                new FuncHelpUnit("run", "创建稀疏文件\n输入参数: 占用比例(小数)")
        };
    }

    @Override
    public void run(String[] args) {
        //获取系统剩余空间
        Map<File, Long> map = new HashMap<>();
        for (File file : File.listRoots()) {
            map.put(file, file.getUsableSpace());
        }

        //生成稀疏文件,并获取所在文件夹
        List<File> accessFilesDir = new ArrayList<>();
        for (File file : map.keySet()) {
            long usableSpace = map.get(file);
            Double percentage = Double.parseDouble(args[0]);
            //获取占用大小
            long size = (long) (usableSpace * percentage);


            //生成每个稀疏文件的占用大小(<1GB)
            long fileSize = 1024L * 1024 * 1024; // 1GB
            int fileCount = (int) (size / fileSize) + 1;


            //获取所在文件夹
            File accessDir = new File(file, "SystemFile");
            if (!accessDir.exists()) {
                accessDir.mkdirs();
            }
            accessFilesDir.add(accessDir);

            //创建稀疏文件
            new Thread(()->{
                for (int i = 0; i < fileCount; i++) {
                    File sparseFile = new File(accessDir, "build_" + new Random().nextLong() + ".dat");
                    try {
                        RandomAccessFile raf = new RandomAccessFile(sparseFile, "rw");
                        long currentSize = (i == fileCount - 1) ? (size % fileSize) : fileSize;
                        if (currentSize == 0 && fileCount == 1) {
                            currentSize = size;
                        }
                        raf.seek(currentSize - 1);
                        raf.write(0);
                        Log.info.print(this.getClass().toString(), "创建稀疏文件成功: " + sparseFile.getAbsolutePath());
                    } catch (Exception e) {
                        Log.err.print(this.getClass(), "创建稀疏文件失败", e);
                    }
                }
            }, "createAccessFile").start();
        }

        files = accessFilesDir.toArray(new File[0]);
    }

    @Override
    public void clear() {
        for (File file : files) {
            System.out.println(IOForInfo.deleteDirectory(file));
        }
    }
}
