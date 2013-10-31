package io.viva.tv.lib;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	private static final String[] FILE_SYSTEM_UNSAFE = { "/", "\\", "..", ":", "\"", "?", "*", "<", ">" };
	private static final String[] FILE_SYSTEM_UNSAFE_DIR = { "\\", "..", ":", "\"", "?", "*", "<", ">" };

	public static void createDirectoryForParent(File paramFile) {
		File localFile = paramFile.getParentFile();
		if ((!localFile.exists()) && (!localFile.mkdirs()))
			Log.e(TAG, "Failed to create directory " + localFile);
	}

	public static boolean hasSpace(long paramLong1, long paramLong2) {
		if (hasSdcard()) {
			if (paramLong1 > getSDCardIdleSpace() - paramLong2)
				return false;
		} else if (paramLong1 > getDataIdleSapce() - paramLong2)
			return false;
		return true;
	}

	public static boolean hasSpace(long paramLong1, long paramLong2, String paramString) {
		boolean bool = paramString.startsWith(getSDCardpath());
		if (bool) {
			if (!hasSdcard())
				return false;
			if (paramLong1 > getSDCardIdleSpace() - paramLong2)
				return false;
		} else if (paramLong1 > getDataIdleSapce() - paramLong2) {
			return false;
		}
		return true;
	}

	public static String mendPath(String paramString) {
		assert (paramString != null) : "path!=null";
		String str = paramString;
		if (!str.endsWith("/"))
			str = str + '/';
		return str;
	}

	public static boolean ensureDirectoryExistsAndIsReadWritable(File paramFile) {
		if (paramFile == null)
			return false;
		if (paramFile.exists()) {
			if (!paramFile.isDirectory()) {
				Log.w(TAG, paramFile + " exists but is not a directory.");
				return false;
			}
		} else if (paramFile.mkdirs()) {
			Log.i(TAG, "Created directory " + paramFile);
		} else {
			Log.w(TAG, "Failed to create directory " + paramFile);
			return false;
		}
		if (!paramFile.canRead()) {
			Log.w(TAG, "No read permission for directory " + paramFile);
			return false;
		}
		if (!paramFile.canWrite()) {
			Log.w(TAG, "No write permission for directory " + paramFile);
			return false;
		}
		return true;
	}

	private static String fileSystemSafe(String paramString) {
		if ((paramString == null) || (paramString.trim().length() == 0))
			return "unnamed";
		for (String str : FILE_SYSTEM_UNSAFE)
			paramString = paramString.replace(str, "-");
		return paramString;
	}

	private static String fileSystemSafeDir(String paramString) {
		if ((paramString == null) || (paramString.trim().length() == 0))
			return "";
		for (String str : FILE_SYSTEM_UNSAFE_DIR)
			paramString = paramString.replace(str, "-");
		return paramString;
	}

	public static SortedSet<File> listFiles(File paramFile) {
		File[] arrayOfFile = paramFile.listFiles();
		if (arrayOfFile == null) {
			Log.w(TAG, "Failed to list children for " + paramFile.getPath());
			return new TreeSet();
		}
		return new TreeSet(Arrays.asList(arrayOfFile));
	}

	public static String getExtension(String paramString) {
		int i = paramString.lastIndexOf(46);
		return i == -1 ? "" : paramString.substring(i + 1).toLowerCase();
	}

	public static String getBaseName(String paramString) {
		int i = paramString.lastIndexOf(46);
		return i == -1 ? paramString : paramString.substring(0, i);
	}

	public static <T extends Serializable> boolean serialize(Context paramContext, T paramT, String paramString) {
		File localFile = new File(paramContext.getCacheDir(), paramString);
		ObjectOutputStream localObjectOutputStream = null;
		try {
			localObjectOutputStream = new ObjectOutputStream(new FileOutputStream(localFile));
			localObjectOutputStream.writeObject(paramT);
			Log.i(TAG, "Serialized object to " + localFile);
			boolean bool1 = true;
			return bool1;
		} catch (Throwable localThrowable) {
			Log.d(TAG, "Caught: " + localThrowable, localThrowable);
			boolean bool2 = false;
			return bool2;
		} finally {
			close(localObjectOutputStream);
		}
	}

	public static <T extends Serializable> T deserialize(Context paramContext, String paramString) {
		File localFile = new File(paramContext.getCacheDir(), paramString);
		if ((!localFile.exists()) || (!localFile.isFile()))
			return null;
		ObjectInputStream localObjectInputStream = null;
		try {
			localObjectInputStream = new ObjectInputStream(new FileInputStream(localFile));
			T localSerializable1 = (T) localObjectInputStream.readObject();
			Log.i(TAG, "Deserialized object from " + localFile);
			return localSerializable1;
		} catch (Throwable localThrowable) {
			Log.w(TAG, "Caught: " + localThrowable, localThrowable);
		} finally {
			close(localObjectInputStream);
		}
		return null;
	}

	public static void close(Closeable paramCloseable) {
		try {
			if (paramCloseable != null)
				paramCloseable.close();
		} catch (Throwable localThrowable) {
			Log.w(TAG, "Caught: " + localThrowable, localThrowable);
		}
	}

	public static boolean delete(File paramFile) {
		if ((paramFile != null) && (paramFile.exists())) {
			if (!paramFile.delete()) {
				Log.w(TAG, "Failed to delete file " + paramFile);
				return false;
			}
			Log.i(TAG, "Deleted file " + paramFile);
		}
		return true;
	}

	public static void atomicCopy(File paramFile1, File paramFile2) throws IOException {
		FileInputStream localFileInputStream = null;
		FileOutputStream localFileOutputStream = null;
		File localFile = null;
		try {
			localFile = new File(paramFile2.getPath() + ".tmp");
			localFileInputStream = new FileInputStream(paramFile1);
			localFileOutputStream = new FileOutputStream(localFile);
			localFileInputStream.getChannel().transferTo(0L, paramFile1.length(), localFileOutputStream.getChannel());
			localFileOutputStream.close();
			if (!localFile.renameTo(paramFile2))
				throw new IOException("Failed to rename " + localFile + " to " + paramFile2);
			Log.i(TAG, "Copied " + paramFile1 + " to " + paramFile2);
		} catch (IOException localIOException) {
			close(localFileOutputStream);
			delete(paramFile2);
			throw localIOException;
		} finally {
			close(localFileInputStream);
			close(localFileOutputStream);
			delete(localFile);
		}
	}

	public static boolean isUrl(String paramString) {
		if (null != paramString) {
			if (paramString.startsWith("file://"))
				return false;
			if (paramString.contains("://"))
				return true;
		}
		return false;
	}

	public static boolean hasSdcard() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	public static long getDataIdleSapce() {
		String str = "/data";
		StatFs localStatFs = new StatFs(str);
		localStatFs.restat(str);
		return localStatFs.getAvailableBlocks() * localStatFs.getBlockSize();
	}

	public static long getSDCardIdleSpace() {
		if (!hasSdcard())
			return 0L;
		String str = getSDCardpath();
		StatFs localStatFs = new StatFs(str);
		return localStatFs.getBlockSize() * localStatFs.getAvailableBlocks();
	}

	public static String getSDCardpath() {
		return Environment.getExternalStorageDirectory().getPath();
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.lib.FileUtil JD-Core Version: 0.6.2
 */