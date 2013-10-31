package io.viva.tv.net.download;

import io.viva.tv.net.exception.DataErrorEnum;

public abstract interface IDownloadControl
{
  public abstract void onStart(Object paramObject, DownloadRequest paramDownloadRequest);

  public abstract void onProgress(Object paramObject, DownloadRequest paramDownloadRequest, int paramInt1, int paramInt2);

  public abstract void onFinished(Object paramObject, DownloadRequest paramDownloadRequest);

  public abstract void onCancel(Object paramObject, DownloadRequest paramDownloadRequest);

  public abstract void onError(Object paramObject, DownloadRequest paramDownloadRequest, DataErrorEnum paramDataErrorEnum);

  public abstract void onNetworkDisconnect();
}