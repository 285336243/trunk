package com.shengzhish.xyj.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 存储工具类
 * 
 * @author YZQ
 * 
 */
public class StorageUtil {

	 /** 获取内存存储可用空间
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

	public static File makeCacheDir(String saveDir) {
		if(isExternalStorageAvailable()) {
			File savedir = new File(Environment.getExternalStorageDirectory(),"/ShengDa/"+saveDir);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
			return savedir;
		}
		return null;
	}

    public static File getFilePath(String path) {
        if(isExternalStorageAvailable()) {
            File file =  new File(Environment.getExternalStorageDirectory(),"/ShengDa/"+path);
            if(file.exists()) {
                return file;
            }else {
                return null;
            }
        }
        return null;
    }

    public static File makeFile(String fileName) {
        if(isExternalStorageAvailable()) {
            File file = new File(Environment.getExternalStorageDirectory(),"/ShengDa/"+fileName);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                    return file;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getSDCardPath() {
        if(isExternalStorageAvailable()) {
            return Environment.getExternalStorageDirectory()+"/ShengDa/";
        }
        return "";
    }
	
	/**
	 * 根据文件绝对路径获取文件名
	 * @param filePath
	 * @return
	 */
	public static String getFileName( String filePath )
	{
		if("".equals(filePath) ){
			return "";
		}
		return filePath.substring( filePath.lastIndexOf( File.separator )+1 );
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
            for (int i=0; i<fileName.length; i++) {
                File f = new File(filePath+"/"+fileName[i]);
                clearFile(f);
            }
        }
    }

    public static void clearFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    clearFile(f);
                }
            }
            file.delete();
        }
    }
}
