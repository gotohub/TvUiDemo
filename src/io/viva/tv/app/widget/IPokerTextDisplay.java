package io.viva.tv.app.widget;

import java.util.ArrayList;

public abstract interface IPokerTextDisplay {
	public abstract void setTextArrayOnPokerGroup(ArrayList<String> paramArrayList);

	public abstract void setTextArrayOnPokerFlow(int paramInt, ArrayList<String> paramArrayList);

	public abstract void disableTextDisplayOnPokerGroup(boolean paramBoolean);
}
