package io.viva.tv.app.widget.animation;

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

	private static void getSelectPosInfo(HSimpleTransInfo info, int index) {
		if ((index + 1 < 0) || (index + 1 >= mSelectPos.length)) {
			info.X = mSelectPos[4].X;
			info.Y = mSelectPos[4].Y;
			info.Scale = mSelectPos[4].Scale;
			info.Alpha = mSelectPos[4].Alpha;

			return;
		}

		info.X = mSelectPos[(index + 1)].X;
		info.Y = mSelectPos[(index + 1)].Y;
		info.Scale = mSelectPos[(index + 1)].Scale;
		info.Alpha = mSelectPos[(index + 1)].Alpha;
	}

	public static TransInfo getSelectPos(float originX, float originY, float destX, float destY, int nowSelectView, int targetImageWidth) {
		float radio = 1.0F * targetImageWidth / 400.0F;

		HSimpleTransInfo info = new HSimpleTransInfo();

		getSelectPosInfo(info, nowSelectView);

		return new TransInfo(info.X * radio - originX + destX, info.Y * radio - originY + destY, info.Scale, info.Scale, info.Alpha);
	}

	public static PosInfo getAnimSelectPos(float originX, float originY, float destX, float destY, int moveDirection, int nowSelectView, int targetImageWidth) {
		HSimpleTransInfo info = new HSimpleTransInfo();

		float startX = 0.0F;
		float startScale = 0.0F;
		float startAlpha = 1.0F;
		float startY = 0.0F;

		float endX = 0.0F;
		float endY = 0.0F;
		float endScale = 0.0F;
		float endAlpha = 1.0F;

		float radio = 1.0F * targetImageWidth / 400.0F;

		int index = nowSelectView;

		if (moveDirection == 1) {
			getSelectPosInfo(info, index);
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;

			getSelectPosInfo(info, index + 1);
			endX = info.X;
			endY = info.Y;
			endScale = info.Scale;
			endAlpha = info.Alpha;
		} else {
			getSelectPosInfo(info, index);
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;

			getSelectPosInfo(info, index - 1);
			endX = info.X;
			endY = info.Y;
			endScale = info.Scale;
			endAlpha = info.Alpha;
		}

		info = null;

		return SelectorAlphaChange(originX, originY, destX, destY, radio, startX, endX, startY, endY, startScale, endScale, startAlpha, endAlpha);
	}

	public static PosInfo getAnimStableSelectPos(float originX, float originY, float destX, float destY, int targetImageWidth) {
		HSimpleTransInfo info = new HSimpleTransInfo();

		float startX = 0.0F;
		float startY = 0.0F;
		float startScale = 0.0F;
		float startAlpha = 1.0F;

		float endX = 0.0F;
		float endY = 0.0F;
		float endScale = 0.0F;
		float endAlpha = 1.0F;
		float radio = 1.0F * targetImageWidth / 400.0F;

		getSelectPosInfo(info, 3);
		startX = info.X;
		startY = info.Y;
		startScale = info.Scale;
		startAlpha = info.Alpha;

		getSelectPosInfo(info, 3);
		endX = info.X;
		endY = info.Y;
		endScale = info.Scale;
		endAlpha = info.Alpha;

		info = null;

		return SelectorAlphaChange(originX, originY, destX, destY, radio, startX, endX, startY, endY, startScale, endScale, startAlpha, endAlpha);
	}

	private static PosInfo SelectorAlphaChange(float originX, float originY, float destX, float destY, float radio, float startX, float endX, float startY, float endY,
			float startScale, float endScale, float startAlpha, float endAlpha) {
		TransInfoKey startKey = new TransInfoKey(startX * radio - originX + destX, startY * radio - originY + destY, startScale, startScale, startAlpha, 0.0F);

		TransInfoKey endKey = new TransInfoKey(endX * radio - originX + destX, endY * radio - originY + destY, endScale, endScale, endAlpha, 1.0F);

		TransInfoKey interPolateStart = new TransInfoKey((startX + (endX - startX) * 0.4F) * radio - originX + destX, startY * radio - originY + destY, startScale + 0.4F
				* (endScale - startScale), startScale + 0.4F * (endScale - startScale), 0.3F, 0.4F);

		TransInfoKey interPolateEnd = new TransInfoKey((startX + 0.6F * (endX - startX)) * radio - originX + destX, endY * radio - originY + destY, startScale + 0.6F
				* (endScale - startScale), startScale + 0.6F * (endScale - startScale), 0.3F, 0.6F);

		return new PosInfo(new TransInfoKey[] { startKey, interPolateStart, interPolateEnd, endKey });
	}

	private static void getStablePosInfo(HSimpleTransInfo info, int index) {
		if ((index + 1 < 0) || (index + 1 >= mStableInfoA6.length)) {
			info.X = mStableInfoA6[4].X;
			info.Y = mStableInfoA6[4].Y;
			info.Scale = mStableInfoA6[4].Scale;
			info.Alpha = mStableInfoA6[4].Alpha;

			return;
		}

		info.X = mStableInfoA6[(index + 1)].X;
		info.Y = mStableInfoA6[(index + 1)].Y;
		info.Scale = mStableInfoA6[(index + 1)].Scale;
		info.Alpha = mStableInfoA6[(index + 1)].Alpha;
	}

	public static PosInfo getAnimStablePos(float originX, float originY, float destX, float destY, int moveDirection, int viewIndex, int targetImageWidth) {
		PosInfo posInfo = null;

		HSimpleTransInfo info = new HSimpleTransInfo();

		float startX = 0.0F;
		float startY = 0.0F;
		float startScale = 0.0F;
		float startAlpha = 1.0F;
		float endX = 0.0F;
		float endY = 0.0F;
		float endScale = 0.0F;
		float endAlpha = 1.0F;

		float radio = 1.0F * targetImageWidth / 400.0F;

		int index = viewIndex;

		if (moveDirection == 1) {
			getStablePosInfo(info, index);
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;

			getStablePosInfo(info, index - 1);
			endX = info.X;
			endY = info.Y;
			endScale = info.Scale;
			endAlpha = info.Alpha;
		} else {
			getStablePosInfo(info, index - 1);
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;

			getStablePosInfo(info, index);
			endX = info.X;
			endY = info.Y;
			endScale = info.Scale;
			endAlpha = info.Alpha;
		}

		info = null;

		posInfo = new PosInfo(startX * radio - originX + destX, endX * radio - originX + destX, startY * radio - originY + destY, endY * radio - originY + destY, startScale,
				endScale, startScale, endScale, startAlpha, endAlpha);

		return posInfo;
	}

	public static PosInfo getAnimUnstablePos(float originX, float originY, float destX, float destY, int moveDirection, int viewSelectIndex, int viewIndex, int targetImageWidth) {
		float startX = 0.0F;
		float startY = 0.0F;
		float startScale = 0.0F;
		float startAlpha = 1.0F;
		float endX = 0.0F;
		float endY = 0.0F;
		float endScale = 0.0F;
		float endAlpha = 1.0F;

		int animationIndex = 0;

		float radio = 1.0F * targetImageWidth / 400.0F;

		if (moveDirection == 1)
			animationIndex = viewSelectIndex;
		else {
			animationIndex = viewSelectIndex - 1;
		}

		HSimpleTransInfo startInfo = null;
		HSimpleTransInfo endInfo = null;

		HSimpleTransInfo[] infoArray = null;

		if (viewIndex < 0) {
			viewIndex = 0;
		}

		if (viewIndex >= mUnstableInfoA6.length) {
			viewIndex = mUnstableInfoA6.length - 1;
		}

		if (animationIndex + 1 < 0) {
			animationIndex = -1;
		}

		if (animationIndex + 2 >= mFramePos.length) {
			animationIndex = mFramePos.length - 3;
		}

		infoArray = mFramePos[(animationIndex + 1)];
		startInfo = infoArray[viewIndex];

		infoArray = mFramePos[(animationIndex + 2)];
		endInfo = infoArray[viewIndex];

		startX = startInfo.X;
		startY = startInfo.Y;
		startScale = startInfo.Scale;
		startAlpha = startInfo.Alpha;

		endX = endInfo.X;
		endY = endInfo.Y;
		endScale = endInfo.Scale;
		endAlpha = endInfo.Scale;

		PosInfo posInfo = null;

		if (moveDirection == 1) {
			posInfo = new PosInfo(startX * radio - originX + destX, endX * radio - originX + destX, startY * radio - originY + destY, endY * radio - originY + destY, startScale,
					endScale, startScale, endScale, startAlpha, endAlpha);
		} else if (moveDirection == 0) {
			posInfo = new PosInfo(endX * radio - originX + destX, startX * radio - originX + destX, endY * radio - originY + destY, startY * radio - originY + destY, endScale,
					startScale, endScale, startScale, endAlpha, startAlpha);
		}

		return posInfo;
	}

	public static TransInfo getInitialPosInfo(float originX, float originY, float destX, float destY, int viewSelectIndex, int viewIndex, int targetImageWidth) {
		boolean isStable = true;
		float radio = 1.0F * targetImageWidth / 400.0F;
		HSimpleTransInfo info = null;

		if ((viewSelectIndex + 1 < 0) || (viewSelectIndex + 1 >= mFramePos.length)) {
			return new TransInfo(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		}

		HSimpleTransInfo[] infoArray = mFramePos[(viewSelectIndex + 1)];

		if ((viewIndex < 0) || (viewIndex >= infoArray.length)) {
			return new TransInfo(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		}

		info = infoArray[viewIndex];

		return new TransInfo(info.X * radio - originX + destX, info.Y * radio - originY + destY, info.Scale, info.Scale, info.Alpha);
	}

	public static int whichCommonStatus(int arrayListNum, int selectPosition) {
		if (selectPosition < -1) {
			return ANI_STATUS_A2;
		}

		if (selectPosition > arrayListNum - 1) {
			return ANI_STATUS_A8;
		}
		if (selectPosition <= 3) {
			return selectPosition;
		}

		if (selectPosition <= -1) {
			return -1;
		}

		if (selectPosition >= arrayListNum - 2) {
			return 4 + selectPosition - (arrayListNum - 2);
		}

		return 3;
	}

	private static boolean checkCommonStatus(int arrayListNum, int selectPosition) {
		if (3 == whichCommonStatus(arrayListNum, selectPosition)) {
			return true;
		}

		return false;
	}

	public static int getVisableStartPosition(int arrayListNum, int selectedPosition) {
		int startStablePosition = 3;
		int lastStablePosition = arrayListNum - 2 - 1;

		if (selectedPosition <= startStablePosition)
			return 0;
		if (selectedPosition >= lastStablePosition) {
			return arrayListNum - 6;
		}
		return selectedPosition - 3;
	}

	public static int getVisableEndPosition(int arrayListNum, int selectedPosition) {
		return getVisableStartPosition(arrayListNum, selectedPosition) + 6;
	}

	private static int getViewIndex(int selectedPosition, int adapterViewIndex, int count) {
		int startStablePosition = 3;
		int lastStablePosition = count - 2;

		if (selectedPosition < 4)
			return adapterViewIndex;
		if (selectedPosition >= lastStablePosition) {
			return adapterViewIndex - (lastStablePosition - 1 - 3);
		}
		return adapterViewIndex - (selectedPosition - 3);
	}

	public static PosInfo getUniversalAnimatorPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth, int adapterViewIndex, int count,
			int selectedPosition, int newSelectedPosition) {
		if (count < 6) {
			return null;
		}

		int step = 0;
		int sign = 1;

		step = newSelectedPosition - selectedPosition;
		if (step < 0) {
			step = -step;
			sign = -1;
		}

		mBaseInfo.set(originX, originY, destX, destY, targetImageWidth);
		BasePositionInfo baseInfo = mBaseInfo;
		PosInfo posInfo = new PosInfo();

		float key = 0.0F;

		int curAniStatus = 0;

		int sel = 0;

		sel = selectedPosition;

		key = 0.0F;

		curAniStatus = whichCommonStatus(count, sel);

		int viewIndex = getViewIndex(selectedPosition, adapterViewIndex, count);

		TransInfoKey keyFrame = pointToStatus(key, curAniStatus, viewIndex, baseInfo);
		posInfo.addKeyFrame(keyFrame);

		for (int i = 0; i < step; i++) {
			sel += sign;

			key = 1.0F * (i + 1) / step;

			curAniStatus = whichCommonStatus(count, sel);

			viewIndex = getViewIndex(sel, adapterViewIndex, count);

			keyFrame = pointToStatus(key, curAniStatus, viewIndex, baseInfo);

			posInfo.addKeyFrame(keyFrame);
		}

		baseInfo = null;

		return posInfo;
	}

	private static TransInfoKey pointToStatus(float key, int aniStatus, int viewIndex, BasePositionInfo baseInfo) {
		float radio = baseInfo.radio;
		HSimpleTransInfo info = null;

		if (aniStatus != ANI_STATUS_A6) {
			HSimpleTransInfo[] infoArray = mFramePos[(aniStatus + 1)];

			int innerIndex = viewIndex;

			if (innerIndex < 0)
				info = mStableBoundInfo[0];
			else if (innerIndex >= infoArray.length) {
				info = mStableBoundInfo[1];
			} else {
				info = infoArray[innerIndex];
			}

			TransInfoKey keyFrame = new TransInfoKey(info.X * radio - baseInfo.originX + baseInfo.destX, info.Y * radio - baseInfo.originY + baseInfo.destY, info.Scale,
					info.Scale, info.Alpha, key);

			return keyFrame;
		}
		int innerIndex = viewIndex + 1;

		if (innerIndex < 0)
			info = mStableBoundInfo[0];
		else if (innerIndex >= mStableInfoA6.length)
			info = mStableBoundInfo[1];
		else {
			info = mStableInfoA6[innerIndex];
		}

		TransInfoKey keyFrame = new TransInfoKey(info.X * radio - baseInfo.originX + baseInfo.destX, info.Y * radio - baseInfo.originY + baseInfo.destY, info.Scale, info.Scale,
				info.Alpha, key);

		return keyFrame;
	}

	private static void getLeftBoundLineCenter(Point pt, float destX, float destY, int targetImageWidth) {
		float radio = 1.0F * targetImageWidth / 400.0F;
		HSimpleTransInfo info = mStableInfoA6[0];

		pt.x = ((int) (info.X * radio + destX));
		pt.y = ((int) (info.Y * radio + destY));
	}

	private static void getRightBoundLineCenter(Point pt, float destX, float destY, int targetImageWidth) {
		float radio = 1.0F * targetImageWidth / 400.0F;
		HSimpleTransInfo info = mStableInfoA6[(mStableInfoA6.length - 1)];

		pt.x = ((int) (info.X * radio + destX));
		pt.y = ((int) (info.Y * radio + destY));
	}

	private static TransInfoKey pointToSelectorStatus(float key, int aniStatus, BasePositionInfo baseInfo) {
		HSimpleTransInfo info = new HSimpleTransInfo();

		float radio = baseInfo.radio;

		getSelectPosInfo(info, aniStatus);

		return new TransInfoKey(info.X * radio - baseInfo.originX + baseInfo.destX, info.Y * radio - baseInfo.originY + baseInfo.destY, info.Scale, info.Scale, info.Alpha, key);
	}

	public static PosInfo getUniversalSelectorPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth, int count, int selectedPosition,
			int newSelectedPosition) {
		if (count < 6) {
			return null;
		}

		int step = 0;
		int sign = 1;

		step = newSelectedPosition - selectedPosition;
		if (step < 0) {
			step = -step;
			sign = -1;
		}

		BasePositionInfo baseInfo = new BasePositionInfo(originX, originY, destX, destY, targetImageWidth);
		PosInfo posInfo = new PosInfo();

		float key = 0.0F;

		int curAniStatus = 0;

		int sel = 0;

		sel = selectedPosition;

		curAniStatus = whichCommonStatus(count, sel);

		key = 0.0F;
		TransInfoKey keyFrameStart = pointToSelectorStatus(key, curAniStatus, baseInfo);

		if (step == 1) {
			sel += sign;

			curAniStatus = whichCommonStatus(count, sel);

			key = 1.0F;

			TransInfoKey keyFrame = pointToSelectorStatus(key, curAniStatus, baseInfo);

			SelectorAlphaChange(posInfo, keyFrameStart, keyFrame, baseInfo);
		} else {
			posInfo.addKeyFrame(keyFrameStart);

			for (int i = 0; i < step; i++) {
				sel += sign;

				key = 1.0F * (i + 1) / step;

				curAniStatus = whichCommonStatus(count, sel);
				TransInfoKey keyFrame = pointToSelectorStatus(key, curAniStatus, baseInfo);

				posInfo.addKeyFrame(keyFrame);
			}

		}

		baseInfo = null;

		return posInfo;
	}

	private static void SelectorAlphaChange(PosInfo posInfo, TransInfoKey startKey, TransInfoKey endKey, BasePositionInfo baseInfo) {
		TransInfo startTransInfo = startKey.mTransInfo;
		TransInfo endTransInfo = endKey.mTransInfo;
		float radio = baseInfo.radio;

		TransInfoKey interPolateStart = new TransInfoKey(startTransInfo.x + (endTransInfo.x - startTransInfo.x) * 0.4F, startTransInfo.y, startTransInfo.scaleX + 0.4F
				* (endTransInfo.scaleX - startTransInfo.scaleX), startTransInfo.scaleY + 0.4F * (endTransInfo.scaleY - startTransInfo.scaleY), 0.3F, 0.4F);

		TransInfoKey interPolateEnd = new TransInfoKey(startTransInfo.x + 0.6F * (endTransInfo.x - startTransInfo.x), endTransInfo.y, startTransInfo.scaleX + 0.6F
				* (endTransInfo.scaleX - startTransInfo.scaleX), startTransInfo.scaleY + 0.6F * (endTransInfo.scaleY - startTransInfo.scaleY), 0.3F, 0.6F);

		posInfo.addKeyFrame(startKey);
		posInfo.addKeyFrame(interPolateStart);
		posInfo.addKeyFrame(interPolateEnd);
		posInfo.addKeyFrame(endKey);
	}

	public static TransInfo getUniversalInitialPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth, int count, int selectedPosition,
			int adapterViewIndex) {
		if (count < 6) {
			return null;
		}

		BasePositionInfo baseInfo = new BasePositionInfo(originX, originY, destX, destY, targetImageWidth);

		float key = 0.0F;

		int curAniStatus = 0;

		int sel = 0;

		sel = selectedPosition;

		key = 0.0F;

		curAniStatus = whichCommonStatus(count, sel);

		int viewIndex = getViewIndex(selectedPosition, adapterViewIndex, count);

		TransInfoKey keyFrame = pointToStatus(key, curAniStatus, viewIndex, baseInfo);

		TransInfo trans = new TransInfo();

		trans.clone(keyFrame.mTransInfo);

		return trans;
	}

	public static TransInfo getUniversalInitialSelectorPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth, int count, int selectedPosition) {
		if (count < 6) {
			return null;
		}

		BasePositionInfo baseInfo = new BasePositionInfo(originX, originY, destX, destY, targetImageWidth);

		float key = 0.0F;

		int curAniStatus = 0;

		int sel = 0;

		sel = selectedPosition;

		curAniStatus = whichCommonStatus(count, sel);

		key = 0.0F;
		TransInfoKey keyFrame = pointToSelectorStatus(key, curAniStatus, baseInfo);

		TransInfo trans = new TransInfo();

		trans.clone(keyFrame.mTransInfo);

		return trans;
	}

	static class BasePositionInfo {
		float originX;
		float originY;
		float destX;
		float destY;
		int targetImageWidth;
		float radio;

		public BasePositionInfo(float originX, float originY, float destX, float destY, int targetImageWidth) {
			set(originX, originY, destX, destY, targetImageWidth);
		}

		public void set(float originX, float originY, float destX, float destY, int targetImageWidth) {
			this.originX = originX;
			this.originY = originY;
			this.destX = destX;
			this.destY = destY;
			this.targetImageWidth = targetImageWidth;
			this.radio = (1.0F * targetImageWidth / 400.0F);
		}
	}

	private static class HSimpleTransInfo {
		public float X;
		public float Y;
		public float Scale;
		public float Alpha;

		public HSimpleTransInfo(float x, float y, float scale, float alpha) {
			this.X = x;
			this.Y = y;
			this.Scale = scale;
			this.Alpha = alpha;
		}

		public HSimpleTransInfo() {
			this(0.0F, 0.0F, 0.0F, 0.0F);
		}
	}
}
