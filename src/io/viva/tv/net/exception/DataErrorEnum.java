package io.viva.tv.net.exception;

public enum DataErrorEnum {
	UNKNOWN_ERROR(1001, "未知错误"), CREATE_CONNECTION_FAILED(2001, "创建连接失败"), URL_ENCODE_FAILED(2002, "URL编码失败"), GET_DATA_FAILED(2003, "获取数据失败"), POST_DATA_FAILED(2004, "发送数据失败"), POST_DATA_CANCELED(
			2005, "用户取消发送数据"), RESPONSE_PARSE_FAILED(2006, "解析返回值失败"), DOWNLOAD_NET_FAILED(3001, "下载时网络连接失败"), DOWNLOAD_STORAGE_FAILED(3002, "下载时存储失败"), DOWNLOAD_FILE_FAILED(3003,
			"下载时文件读写失败"), DOWNLOAD_FILE_NOT_EXISTS(3004, "下载文件不存在"), DOWNLOAD_LACK_OF_SPACE(3005, "存储空间不足");

	private int code;
	private String message;

	private DataErrorEnum(int paramInt, String paramString) {
		this.code = paramInt;
		this.message = paramString;
	}

	public static DataErrorEnum getByCode(int paramInt) {
		if (paramInt < 0)
			return UNKNOWN_ERROR;
		DataErrorEnum[] arrayOfDataErrorEnum1 = values();
		for (DataErrorEnum localDataErrorEnum : arrayOfDataErrorEnum1)
			if (localDataErrorEnum.getCode() == paramInt)
				return localDataErrorEnum;
		return UNKNOWN_ERROR;
	}

	public int getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}
}