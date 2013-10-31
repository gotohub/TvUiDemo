package io.viva.tv.app.widget.animation;

public class VerticalPosInfo {
	private static final int L_OUTTER = 600;
	private static final int L_INNER = 300;
	public static final int DEFAULT_IMAGE_WIDTH = 400;
	public static final int DEFAULT_IMAGE_HEIGHT = 600;
	private static final int Y_CENTER = 0;
	private static final float DELAY_ENTRANCE_VIEW_TIME = 0.2F;
	private static final float RADIO_OUTTER = 0.65F;
	private static final float RADIO_INNER = 0.85F;
	private static final float RADIO_CENTER = 1.0F;

	public static PosInfo getAnimZoomInPos(float originX, float originY, float destX, float destY, int viewIndex, int targetImageWidth, boolean isZoomIn, int count, int selectIndex) {
		float radio = 1.0F * targetImageWidth / 400.0F;

		if (count == 3) {
			return getAnimZoomInPos(originX, originY, destX, destY, viewIndex, radio, isZoomIn, 0);
		}
		if ((selectIndex == 0) && (count == 2)) {
			return getAnimZoomInPos(originX, originY, destX, destY, viewIndex, radio, isZoomIn, 1);
		}
		return getAnimZoomInPos(originX, originY, destX, destY, viewIndex, radio, isZoomIn, -1);
	}

	private static PosInfo getAnimZoomInPos(float originX, float originY, float destX, float destY, int viewIndex, float radio, boolean isZoomIn, int type) {
		VSimpleTransInfo info = new VSimpleTransInfo();

		PosInfo posInfo = null;

		float startX = 0.0F;
		float startY = 0.0F;
		float startScale = 0.0F;
		float startAlpha = 1.0F;

		float endX = 0.0F;
		float endY = 0.0F;
		float endScale = 0.0F;
		float endAlpha = 1.0F;

		if (type == 0) {
			if (isZoomIn) {
				getDefaultPosInfo(info, viewIndex);
				startX = info.X;
				startY = info.Y;
				startScale = info.Scale;
				startAlpha = info.Alpha;

				getDefaultPosInfo(info, 1);
				endX = info.X;
				endY = info.Y;
				endScale = 0.9F;
				endAlpha = info.Alpha;
			} else {
				getDefaultPosInfo(info, 1);
				startX = info.X;
				startY = info.Y;
				startScale = 0.9F;
				startAlpha = info.Alpha;

				getDefaultPosInfo(info, viewIndex);
				endX = info.X;
				endY = info.Y;
				endScale = info.Scale;
				endAlpha = info.Alpha;
			}
		} else if (type == -1) {
			if (isZoomIn) {
				getDefaultPosInfo(info, viewIndex);
				startX = info.X;
				startY = info.Y;
				startScale = info.Scale;
				startAlpha = info.Alpha;

				getDefaultPosInfo(info, 1);
				endX = info.X;
				endY = info.Y;
				endScale = 0.9F;
				endAlpha = info.Alpha;
			} else {
				getDefaultPosInfo(info, 1);
				startX = info.X;
				startY = info.Y;
				startScale = 0.9F;
				startAlpha = info.Alpha;

				getDefaultPosInfo(info, viewIndex);
				endX = info.X;
				endY = info.Y;
				endScale = info.Scale;
				endAlpha = info.Alpha;
			}
		} else if (type == 1) {
			if (isZoomIn) {
				getDefaultPosInfo(info, viewIndex + 1);
				startX = info.X;
				startY = info.Y;
				startScale = info.Scale;
				startAlpha = info.Alpha;

				getDefaultPosInfo(info, 1);
				endX = info.X;
				endY = info.Y;
				endScale = 0.9F;
				endAlpha = info.Alpha;
			} else {
				getDefaultPosInfo(info, 1);
				startX = info.X;
				startY = info.Y;
				startScale = 0.9F;
				startAlpha = info.Alpha;

				getDefaultPosInfo(info, viewIndex + 1);
				endX = info.X;
				endY = info.Y;
				endScale = info.Scale;
				endAlpha = info.Alpha;
			}

		}

		posInfo = new PosInfo(startX * radio - originX + destX, endX * radio - originX + destX, startY * radio - originY + destY, endY * radio - originY + destY, startScale,
				endScale, startScale, endScale, startAlpha, endAlpha);

		return posInfo;
	}

	private static void getDefaultPosInfo(VSimpleTransInfo info, int index) {
		float X = 0.0F;
		float Y = 0.0F;
		float Scale = 0.0F;
		float Alpha = 0.0F;

		switch (index) {
		case -1:
			X = 0.0F;
			Y = -600.0F;
			Scale = 0.65F;
			Alpha = 1.0F;
			break;
		case 0:
			X = 0.0F;
			Y = -300.0F;
			Scale = 0.85F;
			Alpha = 1.0F;
			break;
		case 1:
			X = 0.0F;
			Y = 0.0F;
			Scale = 1.0F;
			Alpha = 1.0F;
			break;
		case 2:
			X = 0.0F;
			Y = 300.0F;
			Scale = 0.85F;
			Alpha = 1.0F;
			break;
		case 3:
			X = 0.0F;
			Y = 600.0F;
			Scale = 0.65F;
			Alpha = 1.0F;
			break;
		}

		info.X = X;
		info.Y = Y;
		info.Scale = Scale;
		info.Alpha = Alpha;
	}

	public static PosInfo getAnimStablePos(float originX, float originY, float destX, float destY, int moveDirection, int viewIndex, int targetImageWidth) {
		PosInfo posInfo = null;

		VSimpleTransInfo info = new VSimpleTransInfo();

		float radio = 1.0F * targetImageWidth / 400.0F;

		float startX = 0.0F;
		float startY = 0.0F;
		float startScale = 0.0F;
		float startAlpha = 1.0F;

		float endX = 0.0F;
		float endY = 0.0F;
		float endScale = 0.0F;
		float endAlpha = 1.0F;

		int index = viewIndex;

		if (moveDirection == 1) {
			getDefaultPosInfo(info, index);
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;

			getDefaultPosInfo(info, index - 1);
			endX = info.X;
			endY = info.Y;
			endScale = info.Scale;
			endAlpha = info.Alpha;
		} else {
			getDefaultPosInfo(info, index - 1);
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;

			getDefaultPosInfo(info, index);
			endX = info.X;
			endY = info.Y;
			endScale = info.Scale;
			endAlpha = info.Alpha;
		}

		if ((moveDirection == 1) && (viewIndex == 2)) {
			return DelayEntrance(originX, originY, destX, destY, radio, startX, endX, startY, endY, startScale, endScale, startAlpha, endAlpha);
		}

		if ((moveDirection == 0) && (viewIndex == 1)) {
			return DelayEntrance(originX, originY, destX, destY, radio, startX, endX, startY, endY, startScale, endScale, startAlpha, endAlpha);
		}

		posInfo = new PosInfo(startX * radio - originX + destX, endX * radio - originX + destX, startY * radio - originY + destY, endY * radio - originY + destY, startScale,
				endScale, startScale, endScale, startAlpha, endAlpha);

		return posInfo;
	}

	private static PosInfo DelayEntrance(float originX, float originY, float destX, float destY, float radio, float startX, float endX, float startY, float endY, float startScale,
			float endScale, float startAlpha, float endAlpha) {
		TransInfoKey startKey = new TransInfoKey(startX * radio - originX + destX, startY * radio - originY + destY, startScale, startScale, startAlpha, 0.0F);

		TransInfoKey endKey = new TransInfoKey(endX * radio - originX + destX, endY * radio - originY + destY, endScale, endScale, endAlpha, 1.0F);

		TransInfoKey interPolate = new TransInfoKey(startX * radio - originX + destX, startY * radio - originY + destY, startScale, startScale, startAlpha, 0.2F);

		return new PosInfo(new TransInfoKey[] { startKey, interPolate, endKey });
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

		float radio = 1.0F * targetImageWidth / 400.0F;

		int animationIndex = 0;

		if (moveDirection == 1)
			animationIndex = viewSelectIndex;
		else {
			animationIndex = viewSelectIndex - 1;
		}

		int type = 0;
		if (animationIndex == 0)
			switch (viewIndex) {
			case 0:
				startX = 0.0F;
				startY = 0.0F;
				startScale = 1.0F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = -300.0F;
				endScale = 0.85F;
				endAlpha = 1.0F;
				break;
			case 1:
				startX = 0.0F;
				startY = 300.0F;
				startScale = 0.85F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = 0.0F;
				endScale = 1.0F;
				endAlpha = 1.0F;

				break;
			case 2:
				startX = 0.0F;
				startY = 600.0F;
				startScale = 0.65F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = 300.0F;
				endScale = 0.85F;
				endAlpha = 1.0F;
				break;
			default:
				break;
			}
		else if (animationIndex == 1) {
			switch (viewIndex) {
			case 0:
				startX = 0.0F;
				startY = -300.0F;
				startScale = 0.85F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = -600.0F;
				endScale = 0.65F;
				endAlpha = 1.0F;
				break;
			case 1:
				startX = 0.0F;
				startY = 0.0F;
				startScale = 1.0F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = -300.0F;
				endScale = 0.85F;
				endAlpha = 1.0F;
				break;
			case 2:
				startX = 0.0F;
				startY = 300.0F;
				startScale = 0.85F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = 0.0F;
				endScale = 1.0F;
				endAlpha = 1.0F;
				break;
			default:
				break;
			}
		} else if (animationIndex == 2) {
			switch (viewIndex) {
			case 0:
				startX = 0.0F;
				startY = 0.0F;
				startScale = 1.0F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = -300.0F;
				endScale = 0.85F;
				endAlpha = 1.0F;
				break;
			case 1:
				startX = 0.0F;
				startY = 300.0F;
				startScale = 0.85F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = 0.0F;
				endScale = 1.0F;
				endAlpha = 1.0F;
				break;
			case 2:
				startX = 0.0F;
				startY = 600.0F;
				startScale = 0.65F;
				startAlpha = 1.0F;

				endX = 0.0F;
				endY = 300.0F;
				endScale = 0.85F;
				endAlpha = 1.0F;
				break;
			}

		}

		if (moveDirection == 1) {
			if ((viewIndex == 2) && (viewSelectIndex == 1)) {
				return DelayEntrance(originX, originY, destX, destY, radio, startX, endX, startY, endY, startScale, endScale, startAlpha, endAlpha);
			}

			if ((viewIndex == 1) && (viewSelectIndex == 0)) {
				return DelayEntrance(originX, originY, destX, destY, radio, startX, endX, startY, endY, startScale, endScale, startAlpha, endAlpha);
			}

		}

		if (moveDirection == 0) {
			if ((viewIndex == 0) && (viewSelectIndex == 1)) {
				return DelayEntrance(originX, originY, destX, destY, radio, endX, startX, endY, startY, endScale, startScale, endAlpha, startAlpha);
			}

			if ((viewIndex == 1) && (viewSelectIndex == 2)) {
				return DelayEntrance(originX, originY, destX, destY, radio, endX, startX, endY, startY, endScale, startScale, endAlpha, startAlpha);
			}

		}

		TransInfoKey startKey = new TransInfoKey(startX * radio - originX + destX, startY * radio - originY + destY, startScale, startScale, startAlpha, 0.0F);

		TransInfoKey endKey = new TransInfoKey(endX * radio - originX + destX, endY * radio - originY + destY, endScale, endScale, endAlpha, 1.0F);

		if (moveDirection == 1) {
			return new PosInfo(new TransInfoKey[] { startKey, endKey });
		}

		return new PosInfo(new TransInfoKey[] { endKey, startKey });
	}

	public static TransInfo getInitialPosInfo(float originX, float originY, float destX, float destY, int viewIndex, int targetImageWidth, int count, int selectIndex) {
		VSimpleTransInfo info = new VSimpleTransInfo();

		float X = 0.0F;
		float Y = 0.0F;
		float Scale = 0.0F;
		float Alpha = 1.0F;

		float radio = 1.0F * targetImageWidth / 400.0F;

		if (count == 2) {
			if (selectIndex == 0)
				getDefaultPosInfo(info, viewIndex + 1);
			else
				getDefaultPosInfo(info, viewIndex);
		} else if (count == 3)
			getDefaultPosInfo(info, viewIndex);
		else {
			getDefaultPosInfo(info, 1);
		}

		X = info.X;
		Y = info.Y;
		Scale = info.Scale;
		Alpha = info.Alpha;

		return new TransInfo(X * radio - originX + destX, Y * radio - originY + destY, Scale, Scale, Alpha);
	}

	static class VSimpleTransInfo {
		public float Y;
		public float X;
		public float Scale;
		public float Alpha;
	}
}
