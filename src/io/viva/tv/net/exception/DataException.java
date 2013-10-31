package io.viva.tv.net.exception;

public class DataException extends Exception {
	private static final long serialVersionUID = 5093049925663572599L;
	private DataErrorEnum dataErrorCode;

	public DataException() {
		this.dataErrorCode = DataErrorEnum.UNKNOWN_ERROR;
	}

	public DataException(String paramString) {
		super(paramString);
		this.dataErrorCode = DataErrorEnum.UNKNOWN_ERROR;
	}

	public DataException(Throwable paramThrowable) {
		super(paramThrowable);
		this.dataErrorCode = DataErrorEnum.UNKNOWN_ERROR;
	}

	public DataException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
		this.dataErrorCode = DataErrorEnum.UNKNOWN_ERROR;
	}

	public DataException(DataErrorEnum paramDataErrorEnum) {
		this.dataErrorCode = paramDataErrorEnum;
	}

	public DataException(String paramString, DataErrorEnum paramDataErrorEnum) {
		super(paramString);
		this.dataErrorCode = paramDataErrorEnum;
	}

	public DataException(Throwable paramThrowable, DataErrorEnum paramDataErrorEnum) {
		super(paramThrowable);
		this.dataErrorCode = paramDataErrorEnum;
	}

	public DataException(String paramString, Throwable paramThrowable, DataErrorEnum paramDataErrorEnum) {
		super(paramString, paramThrowable);
		this.dataErrorCode = paramDataErrorEnum;
	}

	public DataErrorEnum getNetErrorCode() {
		return this.dataErrorCode;
	}
}