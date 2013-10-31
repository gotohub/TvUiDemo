package io.viva.tv.net.download;

import io.viva.tv.net.exception.DataErrorEnum;
import io.viva.tv.net.network.NetworkManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

public class DownloadManager implements IDownloadControl {
	LinkedList<Object> queue = new LinkedList();
	Map<Object, DownloadRequest> mapData = new HashMap();
	List<DownloadRequest> downloadWaitList = new LinkedList();
	Map<Object, DownloadTask> downloading = new HashMap();
	List<DownloadRequest> downloadingList = new LinkedList();
	Set<Object> cancelingSet = new HashSet();
	int taskNum = 1;
	long updateTime = 1500L;
	IDownloadListener listener;
	private Map<Object, Integer> retryCount = new HashMap();
	private static final int RETRY_NUM = 5;

	public DownloadManager(int paramInt, IDownloadListener paramIDownloadListener) {
		this.listener = paramIDownloadListener;
		this.taskNum = paramInt;
	}

	public void setTaskNum(int paramInt) {
		this.taskNum = paramInt;
	}

	public void start(Object paramObject, DownloadRequest paramDownloadRequest) {
		DownloadTask localDownloadTask = null;
		synchronized (this) {
			if (this.downloading.size() < this.taskNum) {
				localDownloadTask = new DownloadTask(paramObject, paramDownloadRequest, this.updateTime, this);
				this.downloading.put(paramObject, localDownloadTask);
				this.downloadingList.add(paramDownloadRequest);
			} else {
				this.queue.add(paramObject);
				this.mapData.put(paramObject, paramDownloadRequest);
				this.downloadWaitList.add(paramDownloadRequest);
			}
		}
		if (localDownloadTask != null)
			localDownloadTask.start();
		else if (this.listener != null)
			this.listener.downloadWait(paramObject, paramDownloadRequest);
	}

	public void stop(Object paramObject) {
		synchronized (this) {
			Object localObject1;
			if (this.downloading.containsKey(paramObject)) {
				localObject1 = (DownloadTask) this.downloading.get(paramObject);
				((DownloadTask) localObject1).cancel();
				this.downloading.remove(paramObject);
				this.downloadingList.remove(((DownloadTask) localObject1).getRequest());
			} else if (this.queue.contains(paramObject)) {
				localObject1 = (DownloadRequest) this.mapData.get(paramObject);
				this.mapData.remove(paramObject);
				this.downloadWaitList.remove(localObject1);
				this.queue.remove(paramObject);
			}
		}
	}

	public void cancel(Object paramObject) {
		Boolean localBoolean = Boolean.valueOf(isDwonloading(paramObject));
		Object localObject;
		if (localBoolean.booleanValue()) {
			localObject = (DownloadTask) this.downloading.get(paramObject);
			if (localObject != null) {
				if (((DownloadTask) localObject).isAlive()) {
					this.cancelingSet.add(paramObject);
					((DownloadTask) localObject).cancel();
				} else {
					this.listener.downloadCancel(paramObject, new OperationRequest(String.valueOf(paramObject)));
				}
			} else
				this.listener.downloadCancel(paramObject, new OperationRequest(String.valueOf(paramObject)));
			nextDownload(paramObject);
		} else {
			if (this.queue.contains(paramObject)) {
				localObject = (DownloadRequest) this.mapData.get(paramObject);
				this.mapData.remove(paramObject);
				this.downloadWaitList.remove(localObject);
				this.queue.remove(paramObject);
			}
			this.listener.downloadCancel(paramObject, new OperationRequest(String.valueOf(paramObject)));
		}
	}

	public boolean isWaiting(Object paramObject) {
		synchronized (this) {
			return this.queue.contains(paramObject);
		}
	}

	public int getProgress(Object paramObject) {
		if (this.downloading.containsKey(paramObject))
			return ((DownloadTask) this.downloading.get(paramObject)).getProgress();
		return 0;
	}

	public boolean isDwonloading(Object paramObject) {
		synchronized (this) {
			return this.downloading.containsKey(paramObject);
		}
	}

	public boolean isDwonloadingActive(Object paramObject) {
		DownloadTask localDownloadTask = (DownloadTask) this.downloading.get(paramObject);
		return (localDownloadTask != null) && (localDownloadTask.isAlive()) && (!localDownloadTask.isStoped());
	}

	public void nextDownload(Object paramObject) {
		synchronized (this) {
			if (this.downloading.containsKey(paramObject)) {
				DownloadTask localObject1 = (DownloadTask) this.downloading.get(paramObject);
				this.downloading.remove(paramObject);
				this.downloadingList.remove(((DownloadTask) localObject1).getRequest());
			}
			if (this.downloading.size() >= this.taskNum)
				return;
			if (this.queue.size() > 0) {
				paramObject = this.queue.poll();
				DownloadRequest localObject1 = (DownloadRequest) this.mapData.get(paramObject);
				this.mapData.remove(paramObject);
				this.downloadWaitList.remove(localObject1);
				DownloadTask localDownloadTask = new DownloadTask(paramObject, (DownloadRequest) localObject1, this.updateTime, this);
				this.downloading.put(paramObject, localDownloadTask);
				this.downloadingList.add(localObject1);
				localDownloadTask.start();
			}
		}
	}

	@Override
	public void onCancel(Object paramObject, DownloadRequest paramDownloadRequest) {
		if ((this.listener != null) && (this.cancelingSet.contains(paramObject))) {
			this.listener.downloadCancel(paramObject, new OperationRequest(String.valueOf(paramObject)));
			this.cancelingSet.remove(paramObject);
		}
	}

	public List<DownloadRequest> getDownloadingList() {
		return new CopyOnWriteArrayList(this.downloadingList);
	}

	public List<DownloadRequest> getDownloadWaitList() {
		return new CopyOnWriteArrayList(this.downloadWaitList);
	}

	@Override
	public void onStart(Object paramObject, DownloadRequest paramDownloadRequest) {
		this.listener.downloadStart(paramObject, paramDownloadRequest);
	}

	@Override
	public void onProgress(Object paramObject, DownloadRequest paramDownloadRequest, int paramInt1, int paramInt2) {
		this.listener.downloadProgress(paramObject, paramDownloadRequest, paramInt1, paramInt2);
	}

	@Override
	public void onFinished(Object paramObject, DownloadRequest paramDownloadRequest) {
		this.listener.downloadFinish(paramObject, paramDownloadRequest);
	}

	@Override
	public void onError(Object paramObject, DownloadRequest paramDownloadRequest, DataErrorEnum paramDataErrorEnum) {
		Log.d("DownloadTask", " onError  error= " + paramDataErrorEnum);
		if (paramDataErrorEnum == DataErrorEnum.DOWNLOAD_STORAGE_FAILED) {
			DownloadTask localDownloadTask = (DownloadTask) this.downloading.get(paramObject);
			Log.d("DownloadTask",
					" onError  error= " + paramDataErrorEnum + " , task.isAlive() = " + localDownloadTask.isAlive() + "task.isPaused() = " + localDownloadTask.isStoped());
			int i = (localDownloadTask != null) && (localDownloadTask.isAlive()) && (!localDownloadTask.isStoped()) ? 1 : 0;
			if (i != 0)
				return;
		} else {
			if (paramDataErrorEnum == DataErrorEnum.DOWNLOAD_NET_FAILED) {
				if (!NetworkManager.instance().isNetworkConnected())
					return;
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException localInterruptedException1) {
					localInterruptedException1.printStackTrace();
				}
				continueDownload(true);
				return;
			}
			if (paramDataErrorEnum == DataErrorEnum.DOWNLOAD_FILE_NOT_EXISTS) {
				if (!NetworkManager.instance().isNetworkConnected())
					return;
				if ((!this.retryCount.containsKey(paramObject)) || (((Integer) this.retryCount.get(paramObject)).intValue() < 5)) {
					try {
						Thread.sleep(10000L);
					} catch (InterruptedException localInterruptedException2) {
						localInterruptedException2.printStackTrace();
					}
					continueDownload(true);
					Integer localInteger = Integer.valueOf(this.retryCount.get(paramObject) == null ? 0 : ((Integer) this.retryCount.get(paramObject)).intValue());
					this.retryCount.put(paramObject, Integer.valueOf(localInteger.intValue() + 1));
					return;
				}
				this.retryCount.remove(paramObject);
				nextDownload(paramObject);
			} else if (paramDataErrorEnum != DataErrorEnum.DOWNLOAD_LACK_OF_SPACE) {
				nextDownload(paramObject);
			}
		}
		this.listener.downloadError(paramObject, paramDownloadRequest, paramDataErrorEnum);
	}

	public void continueDownload(boolean paramBoolean) {
		synchronized (this) {
			if (this.downloadingList.size() > 0) {
				Iterator localIterator = this.downloadingList.iterator();
				while (localIterator.hasNext()) {
					DownloadRequest localDownloadRequest = (DownloadRequest) localIterator.next();
					DownloadTask localDownloadTask1 = (DownloadTask) this.downloading.get(localDownloadRequest.getSaveName());
					Log.d("DownloadTask", " continueDownload forceRestart = " + paramBoolean + ",taskBefore.isAlive = " + localDownloadTask1.isAlive());
					DownloadTask localDownloadTask2;
					if (paramBoolean) {
						localDownloadTask1.cancel();
						localDownloadTask2 = new DownloadTask(localDownloadRequest.getSaveName(), localDownloadRequest, this.updateTime, this);
						this.downloading.put(localDownloadRequest.getSaveName(), localDownloadTask2);
						localDownloadTask2.start();
					} else if ((localDownloadTask1 == null) || (!localDownloadTask1.isAlive())) {
						localDownloadTask2 = new DownloadTask(localDownloadRequest.getSaveName(), localDownloadRequest, this.updateTime, this);
						this.downloading.put(localDownloadRequest.getSaveName(), localDownloadTask2);
						localDownloadTask2.start();
					}
				}
				return;
			}
		}
	}

	public void onNetworkDisconnect() {
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.net.download.DownloadManager JD-Core Version: 0.6.2
 */