package io.viva.tv.net.download;

import io.viva.tv.lib.FileUtil;
import io.viva.tv.net.exception.DataErrorEnum;
import io.viva.tv.net.exception.DataException;
import io.viva.tv.net.http.HttpConnectionBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

import android.util.Log;

public class DownloadTask extends Thread {
	private static final String TAG = "DownloadTask";
	public static final long RESERVED_SPACE = 31457280L;
	private DownloadRequest request;
	private Object key;
	private volatile boolean canceled = false;
	private volatile boolean stoped = false;
	private volatile boolean paused = false;
	private long updateTime = 1000L;
	private IDownloadControl iDownloadControl;

	public DownloadTask(Object paramObject, DownloadRequest paramDownloadRequest, long paramLong, IDownloadControl paramIDownloadControl) {
		this.request = paramDownloadRequest;
		this.key = paramObject;
		this.updateTime = paramLong;
		this.iDownloadControl = paramIDownloadControl;
	}

	public DownloadTask(Object paramObject, DownloadRequest paramDownloadRequest, IDownloadControl paramIDownloadControl) {
		this.request = paramDownloadRequest;
		this.key = paramObject;
		this.iDownloadControl = paramIDownloadControl;
	}

	public void run() {
		if (this.request == null)
			return;
		long l1 = 0L;
		int i = 0;
		String str1 = this.request.getLocalUriCandidates();
		long l2 = this.request.getDownloadedSize();
		this.request.setLocalUri(str1);
		File localFile1 = new File(str1);
		String str2 = localFile1.getParent();
		String str3 = localFile1.getName();
		File localFile2 = new File(str2, str3 + ".tmp");
		if (localFile2.exists()) {
			l2 = localFile2.length();
			if (l2 % 4096L != 0L)
				l2 -= l2 % 4096L;
			if (l2 != this.request.getDownloadedSize()) {
				this.request.setDownloadedSize(l2);
				Log.d("DownloadTask", "DownloadTask: run: DownloadedSize = " + this.request.getDownloadedSize() + ", actural size = " + l2 + ", but continue to download");
			}
		} else {
			File localObject1 = new File(str2);
			if (!((File) localObject1).exists())
				((File) localObject1).mkdirs();
		}
		HttpURLConnection localObject1 = null;
		try {
			localObject1 = new HttpConnectionBuilder(this.request.getRemoteUri(), "GET").setReadTimeout(20000).setProperty("User-Agent", "NetFox").build();
			if (l2 > 0L) {
				String str4 = "bytes=" + l2 + "-";
				((HttpURLConnection) localObject1).setRequestProperty("RANGE", str4);
			}
			long l3 = ((HttpURLConnection) localObject1).getContentLength();
			if (l3 <= 0L) {
				Log.e("DownloadTask", "remote file not exists.");
				this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_FILE_NOT_EXISTS);
				((HttpURLConnection) localObject1).disconnect();
				return;
			}
			try {
				if (!FileUtil.hasSpace(l3, 31457280L, str2)) {
					this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_LACK_OF_SPACE);
					((HttpURLConnection) localObject1).disconnect();
					return;
				}
			} catch (Exception localException) {
				Log.e("DownloadTask", "storage error 0.");
				if (this.iDownloadControl != null)
					this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_STORAGE_FAILED);
				((HttpURLConnection) localObject1).disconnect();
				return;
			}
			if (l2 > 0L)
				this.request.setTotalSize(l2 + ((HttpURLConnection) localObject1).getContentLength());
			else
				this.request.setTotalSize(((HttpURLConnection) localObject1).getContentLength());
		} catch (DataException localDataException1) {
			Log.e("DownloadTask", "net error.", localDataException1);
			if (this.iDownloadControl != null)
				this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_NET_FAILED);
			return;
		}
		if (this.iDownloadControl != null)
			this.iDownloadControl.onStart(this.key, this.request);
		InputStream localInputStream = null;
		RandomAccessFile localRandomAccessFile = null;
		try {
			byte[] arrayOfByte = new byte[4096];
			int j = 0;
			if (!localFile2.exists())
				localFile2.createNewFile();
			localInputStream = ((HttpURLConnection) localObject1).getInputStream();
			localRandomAccessFile = new RandomAccessFile(localFile2, "rwd");
			localRandomAccessFile.seek(l2);
			Log.d("DownloadTask", " pKname = " + this.request.getSaveName() + " Thread.id = " + getId());
			while ((!this.canceled) && ((j = localInputStream.read(arrayOfByte)) > 0)) {
				write(localRandomAccessFile, arrayOfByte, j);
				this.request.setDownloadedSize(this.request.getDownloadedSize() + j);
				if (System.currentTimeMillis() - l1 > this.updateTime) {
					int k = (int) (this.request.getDownloadedSize() - i) / 1024;
					int m = (int) (this.request.getDownloadedSize() * 100L / this.request.getTotalSize());
					int n = (int) (k / ((System.currentTimeMillis() - l1) / 1000L));
					if (this.iDownloadControl != null)
						this.iDownloadControl.onProgress(this.key, this.request, m, n);
					l1 = System.currentTimeMillis();
					i = (int) this.request.getDownloadedSize();
				}
			}
		} catch (IOException localIOException2) {
			Log.e("DownloadTask", "IOException= " + getId() + "   ********");
			this.stoped = Boolean.TRUE.booleanValue();
			if (this.iDownloadControl != null)
				this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_NET_FAILED);
			return;
		} catch (DataException localDataException2) {
			Log.e("DownloadTask", "storage error.  Thread.id = " + getId() + "   ********");
			this.stoped = Boolean.TRUE.booleanValue();
			if (this.iDownloadControl != null)
				this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_STORAGE_FAILED);
			return;
		} finally {
			try {
				if (localInputStream != null)
					localInputStream.close();
				if (localRandomAccessFile != null)
					localRandomAccessFile.close();
				((HttpURLConnection) localObject1).disconnect();
			} catch (IOException localIOException5) {
				Log.e("DownloadTask", "storage error 2.");
				return;
			}
		}
		if (this.canceled) {
			this.iDownloadControl.onCancel(this.key, this.request);
		} else if (this.request.getTotalSize() == this.request.getDownloadedSize()) {
			File localFile3 = new File(str2, str3);
			if (localFile3.exists())
				localFile3.delete();
			localFile2.renameTo(localFile3);
			if (this.iDownloadControl != null)
				this.iDownloadControl.onFinished(this.key, this.request);
		}
	}

	public DownloadRequest getRequest() {
		return this.request;
	}

	public void cancel() {
		this.canceled = true;
	}

	@Deprecated
	public boolean isPaused() {
		return this.paused;
	}

	public boolean isStoped() {
		return this.stoped;
	}

	@Deprecated
	public void pause() {
		this.paused = Boolean.TRUE.booleanValue();
	}

	public int getProgress() {
		if (this.request.getTotalSize() <= 0L)
			return 0;
		return (int) (this.request.getDownloadedSize() * 100L / this.request.getTotalSize());
	}

	private void write(RandomAccessFile paramRandomAccessFile, byte[] paramArrayOfByte, int paramInt) throws DataException {
		try {
			paramRandomAccessFile.write(paramArrayOfByte, 0, paramInt);
		} catch (IOException localIOException) {
			throw new DataException(DataErrorEnum.DOWNLOAD_STORAGE_FAILED);
		}
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.net.download.DownloadTask JD-Core Version: 0.6.2
 */