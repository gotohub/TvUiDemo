package io.viva.tv.lib;

import android.graphics.Point;

public class HorizontalPosInfo {
	public static final int DEFAULT_IMAGE_WIDTH = 400;
	public static final int DEFAULT_IMAGE_HEIGHT = 600;
	public static final float SELECTOR_ALPHA = 0.3F;
	public static final float SELECTOR_TIME_TO_DARK = 0.4F;
	public static final float SELECTOR_TIME_FROM_DARK = 0.6F;
	public static final int CANVAS_WIDTH = 1620;
	public static final int CANVAS_HEIGHT = 1080;
	public static final float X_CENTER = 810.0F;
	public static final float Y_CENTER = 540.0F;
	private static BasePositionInfo mBaseInfo = new BasePositionInfo(0.0F, 0.0F, 0.0F, 0.0F, 0);
	public static final int STABLE_PRE_NUM = 3;
	public static final int STABLE_NEXT_NUM = 2;
	public static final int STABLE_INDEX = 3;
	public static final int ANI_TOTAL_NUM = 6;
	public static int ANI_SELECT_NEGATIVE_POSITION = -1;
	private static int ANI_STATUS_A2 = -1;
	private static int ANI_STATUS_A3 = 0;
	private static int ANI_STATUS_A4 = 1;
	private static int ANI_STATUS_A5 = 2;
	private static int ANI_STATUS_A6 = 3;
	private static int ANI_STATUS_A7 = 4;
	private static int ANI_STATUS_A8 = 5;
	private static final HSimpleTransInfo[] mUnstableInfoA2 = { new HSimpleTransInfo(-586.79999F, 0.0F, 0.9F, 1.0F), new HSimpleTransInfo(-240.30005F, 0.0F, 0.85F, 1.0F),
			new HSimpleTransInfo(86.399963F, 0.0F, 0.8F, 1.0F), new HSimpleTransInfo(393.30005F, 0.0F, 0.75F, 1.0F), new HSimpleTransInfo(484.80005F, 0.0F, 0.7F, 1.0F),
			new HSimpleTransInfo(576.30005F, 0.0F, 0.65F, 1.0F) };
	private static final HSimpleTransInfo[] mUnstableInfoA3 = { new HSimpleTransInfo(-477.0F, 0.0F, 1.0F, 1.0F), new HSimpleTransInfo(-50.400024F, 0.0F, 0.7F, 1.0F),
			new HSimpleTransInfo(216.8999F, 0.0F, 0.65F, 1.0F), new HSimpleTransInfo(464.3999F, 0.0F, 0.6F, 1.0F), new HSimpleTransInfo(535.05005F, 0.0F, 0.55F, 1.0F),
			new HSimpleTransInfo(606.0F, 0.0F, 0.5F, 1.0F) };
	private static final HSimpleTransInfo[] mUnstableInfoA4 = { new HSimpleTransInfo(-626.40002F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(-199.80005F, 0.0F, 1.0F, 1.0F),
			new HSimpleTransInfo(226.80005F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(494.1001F, 0.0F, 0.65F, 1.0F), new HSimpleTransInfo(545.1001F, 0.0F, 0.6F, 1.0F),
			new HSimpleTransInfo(596.1001F, 0.0F, 0.55F, 1.0F) };
	private static final HSimpleTransInfo[] mUnstableInfoA5 = { new HSimpleTransInfo(-636.29999F, 0.0F, 0.65F, 1.0F), new HSimpleTransInfo(-369.0F, 0.0F, 0.7F, 1.0F),
			new HSimpleTransInfo(57.600037F, 0.0F, 1.0F, 1.0F), new HSimpleTransInfo(484.19995F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(535.19995F, 0.0F, 0.65F, 1.0F),
			new HSimpleTransInfo(586.19995F, 0.0F, 0.6F, 1.0F) };
	private static final HSimpleTransInfo[] mStableBoundInfo = { new HSimpleTransInfo(-663.45001F, 0.0F, 0.55F, 1.0F), new HSimpleTransInfo(593.69995F, 0.0F, 0.55F, 1.0F) };
	private static final HSimpleTransInfo[] mStableInfoA6 = { new HSimpleTransInfo(-655.95001F, 0.0F, 0.55F, 1.0F), new HSimpleTransInfo(-646.20001F, 0.0F, 0.6F, 1.0F),
			new HSimpleTransInfo(-595.20001F, 0.0F, 0.65F, 1.0F), new HSimpleTransInfo(-327.90002F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(98.699951F, 0.0F, 1.0F, 1.0F),
			new HSimpleTransInfo(525.30005F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(576.30005F, 0.0F, 0.6F, 1.0F), new HSimpleTransInfo(586.19995F, 0.0F, 0.55F, 1.0F) };
	private static final HSimpleTransInfo[] mUnstableInfoA6 = { new HSimpleTransInfo(-646.20001F, 0.0F, 0.6F, 1.0F), new HSimpleTransInfo(-595.20001F, 0.0F, 0.65F, 1.0F),
			new HSimpleTransInfo(-327.90002F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(98.699951F, 0.0F, 1.0F, 1.0F), new HSimpleTransInfo(525.30005F, 0.0F, 0.7F, 1.0F),
			new HSimpleTransInfo(576.30005F, 0.0F, 0.6F, 1.0F) };
	private static final HSimpleTransInfo[] mUnstableInfoA7 = { new HSimpleTransInfo(-656.09998F, 0.0F, 0.55F, 1.0F), new HSimpleTransInfo(-605.09998F, 0.0F, 0.6F, 1.0F),
			new HSimpleTransInfo(-554.09998F, 0.0F, 0.65F, 1.0F), new HSimpleTransInfo(-286.80005F, 0.0F, 0.7F, 1.0F), new HSimpleTransInfo(139.80005F, 0.0F, 1.0F, 1.0F),
			new HSimpleTransInfo(566.3999F, 0.0F, 0.7F, 1.0F) };
	private static final HSimpleTransInfo[] mUnstableInfoA8 = { new HSimpleTransInfo(-666.0F, 0.0F, 0.5F, 1.0F), new HSimpleTransInfo(-595.20001F, 0.0F, 0.55F, 1.0F),
			new HSimpleTransInfo(-524.40002F, 0.0F, 0.6F, 1.0F), new HSimpleTransInfo(-276.90002F, 0.0F, 0.65F, 1.0F), new HSimpleTransInfo(-9.600037F, 0.0F, 0.75F, 1.0F),
			new HSimpleTransInfo(417.0F, 0.0F, 1.0F, 1.0F) };
	private static final HSimpleTransInfo[][] mFramePos = { mUnstableInfoA2, mUnstableInfoA3, mUnstableInfoA4, mUnstableInfoA5, mUnstableInfoA6, mUnstableInfoA7, mUnstableInfoA8 };
	private static final HSimpleTransInfo[] mSelectPos = { new HSimpleTransInfo(-586.79999F, 0.0F, 0.9F, 0.0F), new HSimpleTransInfo(-477.0F, 0.0F, 1.0F, 1.0F),
			new HSimpleTransInfo(-199.80005F, 0.0F, 1.0F, 1.0F), new HSimpleTransInfo(57.600037F, 0.0F, 1.0F, 1.0F), new HSimpleTransInfo(98.699951F, 0.0F, 1.0F, 1.0F),
			new HSimpleTransInfo(139.80005F, 0.0F, 1.0F, 1.0F), new HSimpleTransInfo(417.0F, 0.0F, 1.0F, 1.0F) };

	public static int getCenterX() {
		return 810;
	}

	public static int getCenterY() {
		return 540;
	}

	private static void getSelectPosInfo(HSimpleTransInfo paramHSimpleTransInfo, int paramInt) {
		if ((paramInt + 1 < 0) || (paramInt + 1 >= mSelectPos.length)) {
			paramHSimpleTransInfo.X = mSelectPos[4].X;
			paramHSimpleTransInfo.Y = mSelectPos[4].Y;
			paramHSimpleTransInfo.Scale = mSelectPos[4].Scale;
			paramHSimpleTransInfo.Alpha = mSelectPos[4].Alpha;
			return;
		}
		paramHSimpleTransInfo.X = mSelectPos[(paramInt + 1)].X;
		paramHSimpleTransInfo.Y = mSelectPos[(paramInt + 1)].Y;
		paramHSimpleTransInfo.Scale = mSelectPos[(paramInt + 1)].Scale;
		paramHSimpleTransInfo.Alpha = mSelectPos[(paramInt + 1)].Alpha;
	}

	public static TransInfo getSelectPos(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2) {
		float f = 1.0F * paramInt2 / 400.0F;
		HSimpleTransInfo localHSimpleTransInfo = new HSimpleTransInfo();
		getSelectPosInfo(localHSimpleTransInfo, paramInt1);
		return new TransInfo(localHSimpleTransInfo.X * f - paramFloat1 + paramFloat3, localHSimpleTransInfo.Y * f - paramFloat2 + paramFloat4, localHSimpleTransInfo.Scale,
				localHSimpleTransInfo.Scale, localHSimpleTransInfo.Alpha);
	}

	public static PosInfo getAnimSelectPos(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3) {
		HSimpleTransInfo localHSimpleTransInfo = new HSimpleTransInfo();
		float f1 = 0.0F;
		float f2 = 0.0F;
		float f3 = 1.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 1.0F;
		float f9 = 1.0F * paramInt3 / 400.0F;
		int i = paramInt2;
		if (paramInt1 == 1) {
			getSelectPosInfo(localHSimpleTransInfo, i);
			f1 = localHSimpleTransInfo.X;
			f4 = localHSimpleTransInfo.Y;
			f2 = localHSimpleTransInfo.Scale;
			f3 = localHSimpleTransInfo.Alpha;
			getSelectPosInfo(localHSimpleTransInfo, i + 1);
			f5 = localHSimpleTransInfo.X;
			f6 = localHSimpleTransInfo.Y;
			f7 = localHSimpleTransInfo.Scale;
			f8 = localHSimpleTransInfo.Alpha;
		} else {
			getSelectPosInfo(localHSimpleTransInfo, i);
			f1 = localHSimpleTransInfo.X;
			f4 = localHSimpleTransInfo.Y;
			f2 = localHSimpleTransInfo.Scale;
			f3 = localHSimpleTransInfo.Alpha;
			getSelectPosInfo(localHSimpleTransInfo, i - 1);
			f5 = localHSimpleTransInfo.X;
			f6 = localHSimpleTransInfo.Y;
			f7 = localHSimpleTransInfo.Scale;
			f8 = localHSimpleTransInfo.Alpha;
		}
		localHSimpleTransInfo = null;
		return SelectorAlphaChange(paramFloat1, paramFloat2, paramFloat3, paramFloat4, f9, f1, f5, f4, f6, f2, f7, f3, f8);
	}

	public static PosInfo getAnimStableSelectPos(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
		HSimpleTransInfo localHSimpleTransInfo = new HSimpleTransInfo();
		float f1 = 0.0F;
		float f2 = 0.0F;
		float f3 = 0.0F;
		float f4 = 1.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 1.0F;
		float f9 = 1.0F * paramInt / 400.0F;
		getSelectPosInfo(localHSimpleTransInfo, 3);
		f1 = localHSimpleTransInfo.X;
		f2 = localHSimpleTransInfo.Y;
		f3 = localHSimpleTransInfo.Scale;
		f4 = localHSimpleTransInfo.Alpha;
		getSelectPosInfo(localHSimpleTransInfo, 3);
		f5 = localHSimpleTransInfo.X;
		f6 = localHSimpleTransInfo.Y;
		f7 = localHSimpleTransInfo.Scale;
		f8 = localHSimpleTransInfo.Alpha;
		localHSimpleTransInfo = null;
		return SelectorAlphaChange(paramFloat1, paramFloat2, paramFloat3, paramFloat4, f9, f1, f5, f2, f6, f3, f7, f4, f8);
	}

	private static PosInfo SelectorAlphaChange(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7,
			float paramFloat8, float paramFloat9, float paramFloat10, float paramFloat11, float paramFloat12, float paramFloat13) {
		TransInfoKey localTransInfoKey1 = new TransInfoKey(paramFloat6 * paramFloat5 - paramFloat1 + paramFloat3, paramFloat8 * paramFloat5 - paramFloat2 + paramFloat4,
				paramFloat10, paramFloat10, paramFloat12, 0.0F);
		TransInfoKey localTransInfoKey2 = new TransInfoKey(paramFloat7 * paramFloat5 - paramFloat1 + paramFloat3, paramFloat9 * paramFloat5 - paramFloat2 + paramFloat4,
				paramFloat11, paramFloat11, paramFloat13, 1.0F);
		TransInfoKey localTransInfoKey3 = new TransInfoKey((paramFloat6 + (paramFloat7 - paramFloat6) * 0.4F) * paramFloat5 - paramFloat1 + paramFloat3, paramFloat8 * paramFloat5
				- paramFloat2 + paramFloat4, paramFloat10 + 0.4F * (paramFloat11 - paramFloat10), paramFloat10 + 0.4F * (paramFloat11 - paramFloat10), 0.3F, 0.4F);
		TransInfoKey localTransInfoKey4 = new TransInfoKey((paramFloat6 + 0.6F * (paramFloat7 - paramFloat6)) * paramFloat5 - paramFloat1 + paramFloat3, paramFloat9 * paramFloat5
				- paramFloat2 + paramFloat4, paramFloat10 + 0.6F * (paramFloat11 - paramFloat10), paramFloat10 + 0.6F * (paramFloat11 - paramFloat10), 0.3F, 0.6F);
		return new PosInfo(new TransInfoKey[] { localTransInfoKey1, localTransInfoKey3, localTransInfoKey4, localTransInfoKey2 });
	}

	private static void getStablePosInfo(HSimpleTransInfo paramHSimpleTransInfo, int paramInt) {
		if ((paramInt + 1 < 0) || (paramInt + 1 >= mStableInfoA6.length)) {
			paramHSimpleTransInfo.X = mStableInfoA6[4].X;
			paramHSimpleTransInfo.Y = mStableInfoA6[4].Y;
			paramHSimpleTransInfo.Scale = mStableInfoA6[4].Scale;
			paramHSimpleTransInfo.Alpha = mStableInfoA6[4].Alpha;
			return;
		}
		paramHSimpleTransInfo.X = mStableInfoA6[(paramInt + 1)].X;
		paramHSimpleTransInfo.Y = mStableInfoA6[(paramInt + 1)].Y;
		paramHSimpleTransInfo.Scale = mStableInfoA6[(paramInt + 1)].Scale;
		paramHSimpleTransInfo.Alpha = mStableInfoA6[(paramInt + 1)].Alpha;
	}

	public static PosInfo getAnimStablePos(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3) {
		PosInfo localPosInfo = null;
		HSimpleTransInfo localHSimpleTransInfo = new HSimpleTransInfo();
		float f1 = 0.0F;
		float f2 = 0.0F;
		float f3 = 0.0F;
		float f4 = 1.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 1.0F;
		float f9 = 1.0F * paramInt3 / 400.0F;
		int i = paramInt2;
		if (paramInt1 == 1) {
			getStablePosInfo(localHSimpleTransInfo, i);
			f1 = localHSimpleTransInfo.X;
			f2 = localHSimpleTransInfo.Y;
			f3 = localHSimpleTransInfo.Scale;
			f4 = localHSimpleTransInfo.Alpha;
			getStablePosInfo(localHSimpleTransInfo, i - 1);
			f5 = localHSimpleTransInfo.X;
			f6 = localHSimpleTransInfo.Y;
			f7 = localHSimpleTransInfo.Scale;
			f8 = localHSimpleTransInfo.Alpha;
		} else {
			getStablePosInfo(localHSimpleTransInfo, i - 1);
			f1 = localHSimpleTransInfo.X;
			f2 = localHSimpleTransInfo.Y;
			f3 = localHSimpleTransInfo.Scale;
			f4 = localHSimpleTransInfo.Alpha;
			getStablePosInfo(localHSimpleTransInfo, i);
			f5 = localHSimpleTransInfo.X;
			f6 = localHSimpleTransInfo.Y;
			f7 = localHSimpleTransInfo.Scale;
			f8 = localHSimpleTransInfo.Alpha;
		}
		localHSimpleTransInfo = null;
		localPosInfo = new PosInfo(f1 * f9 - paramFloat1 + paramFloat3, f5 * f9 - paramFloat1 + paramFloat3, f2 * f9 - paramFloat2 + paramFloat4, f6 * f9 - paramFloat2
				+ paramFloat4, f3, f7, f3, f7, f4, f8);
		return localPosInfo;
	}

	public static PosInfo getAnimUnstablePos(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		float f1 = 0.0F;
		float f2 = 0.0F;
		float f3 = 0.0F;
		float f4 = 1.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 1.0F;
		int i = 0;
		float f9 = 1.0F * paramInt4 / 400.0F;
		if (paramInt1 == 1)
			i = paramInt2;
		else
			i = paramInt2 - 1;
		HSimpleTransInfo localObject1 = null;
		HSimpleTransInfo localObject2 = null;
		HSimpleTransInfo[] arrayOfHSimpleTransInfo = null;
		if (paramInt3 < 0)
			paramInt3 = 0;
		if (paramInt3 >= mUnstableInfoA6.length)
			paramInt3 = mUnstableInfoA6.length - 1;
		if (i + 1 < 0)
			i = -1;
		if (i + 2 >= mFramePos.length)
			i = mFramePos.length - 3;
		arrayOfHSimpleTransInfo = mFramePos[(i + 1)];
		localObject1 = arrayOfHSimpleTransInfo[paramInt3];
		arrayOfHSimpleTransInfo = mFramePos[(i + 2)];
		localObject2 = arrayOfHSimpleTransInfo[paramInt3];
		f1 = localObject1.X;
		f2 = localObject1.Y;
		f3 = localObject1.Scale;
		f4 = localObject1.Alpha;
		f5 = localObject2.X;
		f6 = localObject2.Y;
		f7 = localObject2.Scale;
		f8 = localObject2.Scale;
		PosInfo localPosInfo = null;
		if (paramInt1 == 1)
			localPosInfo = new PosInfo(f1 * f9 - paramFloat1 + paramFloat3, f5 * f9 - paramFloat1 + paramFloat3, f2 * f9 - paramFloat2 + paramFloat4, f6 * f9 - paramFloat2
					+ paramFloat4, f3, f7, f3, f7, f4, f8);
		else if (paramInt1 == 0)
			localPosInfo = new PosInfo(f5 * f9 - paramFloat1 + paramFloat3, f1 * f9 - paramFloat1 + paramFloat3, f6 * f9 - paramFloat2 + paramFloat4, f2 * f9 - paramFloat2
					+ paramFloat4, f7, f3, f7, f3, f8, f4);
		return localPosInfo;
	}

	public static TransInfo getInitialPosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3) {
		int i = 1;
		float f = 1.0F * paramInt3 / 400.0F;
		HSimpleTransInfo localObject = null;
		if ((paramInt1 + 1 < 0) || (paramInt1 + 1 >= mFramePos.length))
			return new TransInfo(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		HSimpleTransInfo[] arrayOfHSimpleTransInfo = mFramePos[(paramInt1 + 1)];
		if ((paramInt2 < 0) || (paramInt2 >= arrayOfHSimpleTransInfo.length))
			return new TransInfo(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		localObject = arrayOfHSimpleTransInfo[paramInt2];
		return new TransInfo(localObject.X * f - paramFloat1 + paramFloat3, localObject.Y * f - paramFloat2 + paramFloat4, localObject.Scale, localObject.Scale, localObject.Alpha);
	}

	public static int whichCommonStatus(int paramInt1, int paramInt2) {
		if (paramInt2 < -1)
			return ANI_STATUS_A2;
		if (paramInt2 > paramInt1 - 1)
			return ANI_STATUS_A8;
		if (paramInt2 <= 3)
			return paramInt2;
		if (paramInt2 <= -1)
			return -1;
		if (paramInt2 >= paramInt1 - 2)
			return 4 + paramInt2 - (paramInt1 - 2);
		return 3;
	}

	private static boolean checkCommonStatus(int paramInt1, int paramInt2) {
		return 3 == whichCommonStatus(paramInt1, paramInt2);
	}

	public static int getVisableStartPosition(int paramInt1, int paramInt2) {
		int i = 3;
		int j = paramInt1 - 2 - 1;
		if (paramInt2 <= i)
			return 0;
		if (paramInt2 >= j)
			return paramInt1 - 6;
		return paramInt2 - 3;
	}

	public static int getVisableEndPosition(int paramInt1, int paramInt2) {
		return getVisableStartPosition(paramInt1, paramInt2) + 6;
	}

	private static int getViewIndex(int paramInt1, int paramInt2, int paramInt3) {
		int i = 3;
		int j = paramInt3 - 2;
		if (paramInt1 < 4)
			return paramInt2;
		if (paramInt1 >= j)
			return paramInt2 - (j - 1 - 3);
		return paramInt2 - (paramInt1 - 3);
	}

	public static PosInfo getUniversalAnimatorPosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3,
			int paramInt4, int paramInt5) {
		if (paramInt3 < 6)
			return null;
		int i = 0;
		int j = 1;
		i = paramInt5 - paramInt4;
		if (i < 0) {
			i = -i;
			j = -1;
		}
		mBaseInfo.set(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt1);
		BasePositionInfo localBasePositionInfo = mBaseInfo;
		PosInfo localPosInfo = new PosInfo();
		float f = 0.0F;
		int k = 0;
		int n = 0;
		n = paramInt4;
		f = 0.0F;
		k = whichCommonStatus(paramInt3, n);
		int m = getViewIndex(paramInt4, paramInt2, paramInt3);
		TransInfoKey localTransInfoKey = pointToStatus(f, k, m, localBasePositionInfo);
		localPosInfo.addKeyFrame(localTransInfoKey);
		for (int i1 = 0; i1 < i; i1++) {
			n += j;
			f = 1.0F * (i1 + 1) / i;
			k = whichCommonStatus(paramInt3, n);
			m = getViewIndex(n, paramInt2, paramInt3);
			localTransInfoKey = pointToStatus(f, k, m, localBasePositionInfo);
			localPosInfo.addKeyFrame(localTransInfoKey);
		}
		localBasePositionInfo = null;
		return localPosInfo;
	}

	private static TransInfoKey pointToStatus(float paramFloat, int paramInt1, int paramInt2, BasePositionInfo paramBasePositionInfo) {
		float f = paramBasePositionInfo.radio;
		HSimpleTransInfo localHSimpleTransInfo = null;
		if (paramInt1 != ANI_STATUS_A6) {
			HSimpleTransInfo[] arrayOfHSimpleTransInfo = mFramePos[(paramInt1 + 1)];
			int j = paramInt2;
			if (j < 0)
				localHSimpleTransInfo = mStableBoundInfo[0];
			else if (j >= arrayOfHSimpleTransInfo.length)
				localHSimpleTransInfo = mStableBoundInfo[1];
			else
				localHSimpleTransInfo = arrayOfHSimpleTransInfo[j];
			TransInfoKey localTransInfoKey = new TransInfoKey(localHSimpleTransInfo.X * f - paramBasePositionInfo.originX + paramBasePositionInfo.destX, localHSimpleTransInfo.Y
					* f - paramBasePositionInfo.originY + paramBasePositionInfo.destY, localHSimpleTransInfo.Scale, localHSimpleTransInfo.Scale, localHSimpleTransInfo.Alpha,
					paramFloat);
			return localTransInfoKey;
		}
		int i = paramInt2 + 1;
		if (i < 0)
			localHSimpleTransInfo = mStableBoundInfo[0];
		else if (i >= mStableInfoA6.length)
			localHSimpleTransInfo = mStableBoundInfo[1];
		else
			localHSimpleTransInfo = mStableInfoA6[i];
		TransInfoKey localTransInfoKey = new TransInfoKey(localHSimpleTransInfo.X * f - paramBasePositionInfo.originX + paramBasePositionInfo.destX, localHSimpleTransInfo.Y * f
				- paramBasePositionInfo.originY + paramBasePositionInfo.destY, localHSimpleTransInfo.Scale, localHSimpleTransInfo.Scale, localHSimpleTransInfo.Alpha, paramFloat);
		return localTransInfoKey;
	}

	private static void getLeftBoundLineCenter(Point paramPoint, float paramFloat1, float paramFloat2, int paramInt) {
		float f = 1.0F * paramInt / 400.0F;
		HSimpleTransInfo localHSimpleTransInfo = mStableInfoA6[0];
		paramPoint.x = ((int) (localHSimpleTransInfo.X * f + paramFloat1));
		paramPoint.y = ((int) (localHSimpleTransInfo.Y * f + paramFloat2));
	}

	private static void getRightBoundLineCenter(Point paramPoint, float paramFloat1, float paramFloat2, int paramInt) {
		float f = 1.0F * paramInt / 400.0F;
		HSimpleTransInfo localHSimpleTransInfo = mStableInfoA6[(mStableInfoA6.length - 1)];
		paramPoint.x = ((int) (localHSimpleTransInfo.X * f + paramFloat1));
		paramPoint.y = ((int) (localHSimpleTransInfo.Y * f + paramFloat2));
	}

	private static TransInfoKey pointToSelectorStatus(float paramFloat, int paramInt, BasePositionInfo paramBasePositionInfo) {
		HSimpleTransInfo localHSimpleTransInfo = new HSimpleTransInfo();
		float f = paramBasePositionInfo.radio;
		getSelectPosInfo(localHSimpleTransInfo, paramInt);
		return new TransInfoKey(localHSimpleTransInfo.X * f - paramBasePositionInfo.originX + paramBasePositionInfo.destX, localHSimpleTransInfo.Y * f
				- paramBasePositionInfo.originY + paramBasePositionInfo.destY, localHSimpleTransInfo.Scale, localHSimpleTransInfo.Scale, localHSimpleTransInfo.Alpha, paramFloat);
	}

	public static PosInfo getUniversalSelectorPosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		if (paramInt2 < 6)
			return null;
		int i = 0;
		int j = 1;
		i = paramInt4 - paramInt3;
		if (i < 0) {
			i = -i;
			j = -1;
		}
		BasePositionInfo localBasePositionInfo = new BasePositionInfo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt1);
		PosInfo localPosInfo = new PosInfo();
		float f = 0.0F;
		int k = 0;
		int m = 0;
		m = paramInt3;
		k = whichCommonStatus(paramInt2, m);
		f = 0.0F;
		TransInfoKey localTransInfoKey2 = pointToSelectorStatus(f, k, localBasePositionInfo);
		TransInfoKey localTransInfoKey1;
		if (i == 1) {
			m += j;
			k = whichCommonStatus(paramInt2, m);
			f = 1.0F;
			localTransInfoKey1 = pointToSelectorStatus(f, k, localBasePositionInfo);
			SelectorAlphaChange(localPosInfo, localTransInfoKey2, localTransInfoKey1, localBasePositionInfo);
		} else {
			localPosInfo.addKeyFrame(localTransInfoKey2);
			for (int n = 0; n < i; n++) {
				m += j;
				f = 1.0F * (n + 1) / i;
				k = whichCommonStatus(paramInt2, m);
				localTransInfoKey1 = pointToSelectorStatus(f, k, localBasePositionInfo);
				localPosInfo.addKeyFrame(localTransInfoKey1);
			}
		}
		localBasePositionInfo = null;
		return localPosInfo;
	}

	private static void SelectorAlphaChange(PosInfo paramPosInfo, TransInfoKey paramTransInfoKey1, TransInfoKey paramTransInfoKey2, BasePositionInfo paramBasePositionInfo) {
		TransInfo localTransInfo1 = paramTransInfoKey1.mTransInfo;
		TransInfo localTransInfo2 = paramTransInfoKey2.mTransInfo;
		float f = paramBasePositionInfo.radio;
		TransInfoKey localTransInfoKey1 = new TransInfoKey(localTransInfo1.x + (localTransInfo2.x - localTransInfo1.x) * 0.4F, localTransInfo1.y, localTransInfo1.scaleX + 0.4F
				* (localTransInfo2.scaleX - localTransInfo1.scaleX), localTransInfo1.scaleY + 0.4F * (localTransInfo2.scaleY - localTransInfo1.scaleY), 0.3F, 0.4F);
		TransInfoKey localTransInfoKey2 = new TransInfoKey(localTransInfo1.x + 0.6F * (localTransInfo2.x - localTransInfo1.x), localTransInfo2.y, localTransInfo1.scaleX + 0.6F
				* (localTransInfo2.scaleX - localTransInfo1.scaleX), localTransInfo1.scaleY + 0.6F * (localTransInfo2.scaleY - localTransInfo1.scaleY), 0.3F, 0.6F);
		paramPosInfo.addKeyFrame(paramTransInfoKey1);
		paramPosInfo.addKeyFrame(localTransInfoKey1);
		paramPosInfo.addKeyFrame(localTransInfoKey2);
		paramPosInfo.addKeyFrame(paramTransInfoKey2);
	}

	public static TransInfo getUniversalInitialPosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		if (paramInt2 < 6)
			return null;
		BasePositionInfo localBasePositionInfo = new BasePositionInfo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt1);
		float f = 0.0F;
		int i = 0;
		int k = 0;
		k = paramInt3;
		f = 0.0F;
		i = whichCommonStatus(paramInt2, k);
		int j = getViewIndex(paramInt3, paramInt4, paramInt2);
		TransInfoKey localTransInfoKey = pointToStatus(f, i, j, localBasePositionInfo);
		TransInfo localTransInfo = new TransInfo();
		localTransInfo.clone(localTransInfoKey.mTransInfo);
		return localTransInfo;
	}

	public static TransInfo getUniversalInitialSelectorPosInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2,
			int paramInt3) {
		if (paramInt2 < 6)
			return null;
		BasePositionInfo localBasePositionInfo = new BasePositionInfo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt1);
		float f = 0.0F;
		int i = 0;
		int j = 0;
		j = paramInt3;
		i = whichCommonStatus(paramInt2, j);
		f = 0.0F;
		TransInfoKey localTransInfoKey = pointToSelectorStatus(f, i, localBasePositionInfo);
		TransInfo localTransInfo = new TransInfo();
		localTransInfo.clone(localTransInfoKey.mTransInfo);
		return localTransInfo;
	}

	static class BasePositionInfo {
		float originX;
		float originY;
		float destX;
		float destY;
		int targetImageWidth;
		float radio;

		public BasePositionInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
			set(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt);
		}

		public void set(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
			this.originX = paramFloat1;
			this.originY = paramFloat2;
			this.destX = paramFloat3;
			this.destY = paramFloat4;
			this.targetImageWidth = paramInt;
			this.radio = (1.0F * paramInt / 400.0F);
		}
	}

	private static class HSimpleTransInfo {
		public float X;
		public float Y;
		public float Scale;
		public float Alpha;

		public HSimpleTransInfo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
			this.X = paramFloat1;
			this.Y = paramFloat2;
			this.Scale = paramFloat3;
			this.Alpha = paramFloat4;
		}

		public HSimpleTransInfo() {
			this(0.0F, 0.0F, 0.0F, 0.0F);
		}
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.lib.HorizontalPosInfo JD-Core Version: 0.6.2
 */