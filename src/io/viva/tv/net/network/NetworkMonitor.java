package io.viva.tv.net.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetworkMonitor extends BroadcastReceiver {
	private static final String TAG = "NetworkMonitor";
	private final int ON_NETWORK_DISCONNECTED = 100;
	private Context mContext = null;
	private Runnable mRunnable = null;
	private Handler mHandler = null;
	private boolean ENABLE = true;
	private INetworkCtrListener iNetworkCtrListener;

	public NetworkMonitor(INetworkCtrListener paramINetworkCtrListener) {
		this.iNetworkCtrListener = paramINetworkCtrListener;
		Log.d(TAG, "NetworkMonitor.Constructor");
	}

	public void onReceive(Context paramContext, Intent paramIntent) {
		Log.d(TAG, "NetworkMonitor.onReceive");
		try {
			this.mContext = paramContext;
			NetInfoAdapter localNetInfoAdapter = new NetInfoAdapter(this.mContext);
			if (localNetInfoAdapter.isConnected()) {
				String str = "NetworkMonitor: connected " + localNetInfoAdapter.getInfo("type");
				if (localNetInfoAdapter.exists("netID"))
					str = str + " " + localNetInfoAdapter.getInfo("netID");
				if (localNetInfoAdapter.exists("speed"))
					str = str + " " + localNetInfoAdapter.getInfo("speed");
				Log.v(TAG, str);
				if (this.ENABLE) {
					String[] arrayOfString = new LableMap().getLableList();
					int i = 2;
				}
				localNetInfoAdapter = null;
			} else {
				Log.v(TAG, "NetworkMonitor: not connected");
				if (null == this.mHandler)
					this.mHandler = new Handler() {
						public void handleMessage(Message paramAnonymousMessage) {
							switch (paramAnonymousMessage.what) {
							case ON_NETWORK_DISCONNECTED:
								Log.i(TAG, "Connection re-build.");
								removeCallbacks(NetworkMonitor.this.mRunnable);
								NetworkMonitor.this.iNetworkCtrListener.setNetworkConnectedStatus(true);
								NetworkMonitor.this.mRunnable = null;
							}
						}
					};
				if (null == this.mRunnable)
					this.mRunnable = new Runnable() {
						public void run() {
							NetworkMonitor.this.mHandler.obtainMessage(ON_NETWORK_DISCONNECTED).sendToTarget();
						}
					};
				this.mHandler.postDelayed(this.mRunnable, 1000L);
			}
		} catch (Exception localException) {
			Log.v(TAG, "NetworkMonitor.onReceive");
			Log.e(TAG, "NetworkMonitor.onReceive, failed: " + localException.getMessage());
		}
	}

	public static abstract interface INetworkCtrListener {
		public abstract void setNetworkConnectedStatus(boolean paramBoolean);
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.net.network.NetworkMonitor JD-Core Version: 0.6.2
 */