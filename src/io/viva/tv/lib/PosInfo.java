package io.viva.tv.lib;

import java.util.ArrayList;

public class PosInfo {
	public static final int MOVE_INVALID = -1;
	public static final int MOVE_LEFT = 0;
	public static final int MOVE_RIGHT = 1;
	ArrayList<TransInfoKey> mList = new ArrayList();

	public PosInfo() {
	}

	public PosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
		this.mList.add(new TransInfoKey(paramFloat1, paramFloat2, paramFloat3, paramFloat2, paramFloat5, 0.0F));
	}

	public void addKeyFrame(TransInfoKey paramTransInfoKey) {
		if (this.mList == null)
			this.mList = new ArrayList();
		this.mList.add(paramTransInfoKey);
	}

	public PosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8,
			float paramFloat9, float paramFloat10) {
		this(new TransInfoKey[] { new TransInfoKey(paramFloat1, paramFloat3, paramFloat5, paramFloat7, paramFloat9, 0.0F),
				new TransInfoKey(paramFloat2, paramFloat4, paramFloat6, paramFloat8, paramFloat10, 1.0F) });
	}

	public PosInfo(TransInfoKey[] paramArrayOfTransInfoKey) {
		int i = paramArrayOfTransInfoKey.length;
		TransInfoKey[] arrayOfTransInfoKey = (TransInfoKey[]) paramArrayOfTransInfoKey.clone();
		for (int j = 0; j < i; j++)
			this.mList.add(arrayOfTransInfoKey[j]);
	}

	public TransInfo evalute(float paramFloat) {
		if (paramFloat <= 0.0F) {
			TransInfoKey localTransInfoKey1 = (TransInfoKey) this.mList.get(0);
			if (localTransInfoKey1 != null)
				return localTransInfoKey1.mTransInfo;
			return null;
		}
		int i;
		Object localObject;
		if (paramFloat >= 1.0F) {
			i = this.mList.size();
			if (i >= 1) {
				localObject = (TransInfoKey) this.mList.get(i - 1);
				if (localObject != null)
					return ((TransInfoKey) localObject).mTransInfo;
				return null;
			}
		} else {
			i = this.mList.size();
			if (i == 2)
				return evalute(paramFloat, (TransInfoKey) this.mList.get(0), (TransInfoKey) this.mList.get(1));
			float f1 = paramFloat;
			localObject = (TransInfoKey) this.mList.get(0);
			float f2 = ((TransInfoKey) localObject).mKey;
			for (int j = 1; j < i; j++) {
				TransInfoKey localTransInfoKey2 = (TransInfoKey) this.mList.get(j);
				float f3 = localTransInfoKey2.mKey;
				if (f1 < f3)
					return evalute((f1 - f2) / (f3 - f2), (TransInfoKey) localObject, localTransInfoKey2);
				localObject = localTransInfoKey2;
				f2 = f3;
			}
		}
		return null;
	}

	private TransInfo evalute(float paramFloat, TransInfoKey paramTransInfoKey1, TransInfoKey paramTransInfoKey2) {
		TransInfo localTransInfo1 = paramTransInfoKey1.mTransInfo;
		TransInfo localTransInfo2 = paramTransInfoKey2.mTransInfo;
		float f1 = localTransInfo1.x + paramFloat * (localTransInfo2.x - localTransInfo1.x);
		float f2 = localTransInfo1.y + paramFloat * (localTransInfo2.y - localTransInfo1.y);
		float f3 = localTransInfo1.scaleX + paramFloat * (localTransInfo2.scaleX - localTransInfo1.scaleX);
		float f4 = localTransInfo1.scaleY + paramFloat * (localTransInfo2.scaleY - localTransInfo1.scaleY);
		float f5 = localTransInfo1.alpha + paramFloat * (localTransInfo2.alpha - localTransInfo1.alpha);
		return new TransInfo(f1, f2, f3, f4, f5);
	}

	public TransInfo getStartTrans() {
		if (this.mList != null) {
			TransInfoKey localTransInfoKey = (TransInfoKey) this.mList.get(0);
			if (localTransInfoKey != null)
				return localTransInfoKey.mTransInfo;
		}
		return null;
	}

	public TransInfo getEndTrans() {
		if (this.mList != null) {
			int i = this.mList.size();
			TransInfoKey localTransInfoKey = (TransInfoKey) this.mList.get(i - 1);
			if (localTransInfoKey != null)
				return localTransInfoKey.mTransInfo;
		}
		return null;
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.lib.PosInfo JD-Core Version: 0.6.2
 */