package io.viva.tv.net.download;

public class DownloadRequest extends OperationRequest {
	private String remoteUri;
	private String localUriCandidates;
	private String localUri;
	private long downloadedSize;
	private String name;
	private Boolean isContinue = Boolean.FALSE;

	public DownloadRequest(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, String paramString5, String paramString6,
			long paramLong2) {
		super(paramString5, paramString4, paramString6, System.currentTimeMillis(), paramLong2);
		this.name = paramString6;
		this.localUriCandidates = paramString2;
		this.remoteUri = paramString1;
		this.localUri = paramString3;
		this.downloadedSize = paramLong1;
	}

	public DownloadRequest(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, String paramString5, String paramString6,
			long paramLong2, long paramLong3) {
		super(paramString5, paramString4, paramString6, paramLong2, paramLong3);
		this.name = paramString6;
		this.remoteUri = paramString1;
		this.localUriCandidates = paramString2;
		this.localUri = paramString3;
		this.downloadedSize = paramLong1;
	}

	public Boolean isContinue() {
		return this.isContinue;
	}

	public void setIsContinue(Boolean paramBoolean) {
		this.isContinue = paramBoolean;
	}

	public long getDownloadedSize() {
		return this.downloadedSize;
	}

	public void setDownloadedSize(long paramLong) {
		this.downloadedSize = paramLong;
	}

	public DownloadRequest(String paramString) {
		super(paramString);
	}

	public String getRemoteUri() {
		return this.remoteUri;
	}

	public void setRemoteUri(String paramString) {
		this.remoteUri = paramString;
	}

	public String getLocalUri() {
		return this.localUri;
	}

	public void setLocalUri(String paramString) {
		this.localUri = paramString;
	}

	public String getLocalUriCandidates() {
		return this.localUriCandidates;
	}

	public void setLocalUriCandidates(String paramString) {
		this.localUriCandidates = paramString;
	}

	public String getName() {
		return this.name;
	}

	public int getProgress() {
		if (this.totalSize <= 0L)
			return 0;
		return (int) (this.downloadedSize * 100L / this.totalSize);
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.net.download.DownloadRequest JD-Core Version: 0.6.2
 */