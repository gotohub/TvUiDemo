package io.viva.tv.net.download;

import java.text.DecimalFormat;

public class OperationRequest {
	protected String saveName;
	protected String name;
	protected String source;
	protected long requestTime;
	protected long overTime;
	protected long totalSize;
	protected Object info;

	public OperationRequest() {
	}

	public OperationRequest(String paramString) {
		this.saveName = paramString;
	}

	public OperationRequest(String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2) {
		this.source = paramString2;
		this.name = paramString3;
		this.saveName = paramString1;
		this.requestTime = paramLong1;
		this.overTime = paramLong2;
	}

	public Object getInfo() {
		return this.info;
	}

	public void setInfo(Object paramObject) {
		this.info = paramObject;
	}

	public long getOverTime() {
		return this.overTime;
	}

	public void setOverTime(long paramLong) {
		this.overTime = paramLong;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public String getSaveName() {
		return this.saveName;
	}

	public void setSaveName(String paramString) {
		this.saveName = paramString;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String paramString) {
		this.source = paramString;
	}

	public long getRequestTime() {
		return this.requestTime;
	}

	public void setRequestTime(long paramLong) {
		this.requestTime = paramLong;
	}

	public long getTotalSize() {
		return this.totalSize;
	}

	public void setTotalSize(long paramLong) {
		this.totalSize = paramLong;
	}

	public String getSizeShow() {
		float f = (float) this.totalSize / 1048576.0F;
		DecimalFormat localDecimalFormat = new DecimalFormat("0.##");
		return String.valueOf(localDecimalFormat.format(f)) + "M";
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.net.download.OperationRequest JD-Core Version: 0.6.2
 */