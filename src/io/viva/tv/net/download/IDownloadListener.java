package io.viva.tv.net.download;

import io.viva.tv.net.exception.DataErrorEnum;

public abstract interface IDownloadListener {
	public abstract void downloadWait(Object paramObject, OperationRequest paramOperationRequest);

	public abstract void downloadStart(Object paramObject, OperationRequest paramOperationRequest);

	public abstract void downloadFinish(Object paramObject, OperationRequest paramOperationRequest);

	public abstract void downloadCancel(Object paramObject, OperationRequest paramOperationRequest);

	public abstract void downloadProgress(Object paramObject, OperationRequest paramOperationRequest, int paramInt1, int paramInt2);

	public abstract void downloadError(Object paramObject, OperationRequest paramOperationRequest, DataErrorEnum paramDataErrorEnum);
}