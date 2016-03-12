package com.jiujie8.choice.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 存储工具类
 *
 * @author YZQ
 */
public class StorageUtil {

    /**
     * 获取内存存储可用空间
     *
     * @return long
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory(); // 获取数据目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static String getSDCardPath(Context context) {
        if (isExternalStorageAvailable()) {
            return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        }
        return "";
    }

    public static File createImageFile(Context context) {
        if (!TextUtils.isEmpty(getSDCardPath(context))) {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = "jj_" + timeStamp + ".jpg";
            return new File(getSDCardPath(context), fileName);
        }
        return null;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if ("".equals(filePath)) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取内存存储总空间
     *
     * @return long
     */
    @SuppressWarnings("deprecation")
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 判断外部存储是否可用
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState());
    }

    /**
     * 获取外部可用空间大小
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableExternalMemorySize() {
        if (isExternalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * 获取外部总共空间大小
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getTotalExternalMemorySize() {
        if (isExternalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static void deleteAllFiles(String filePath, String[] fileName) {
        File file = new File(filePath);
        if (file.exists()) {
            for (int i = 0; i < fileName.length; i++) {
                File f = new File(filePath + "/" + fileName[i]);
                clearFile(f);
            }
        }
    }

    public static void clearFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    clearFile(f);
                }
            }
            file.delete();
        }
    }

    public static File makeCacheDir(String saveDir) {
        if (isExternalStorageAvailable()) {
            File savedir = new File(Environment.getExternalStorageDirectory(), "/JiuJie/" + saveDir);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
            return savedir;
        }
        return null;
    }
}
