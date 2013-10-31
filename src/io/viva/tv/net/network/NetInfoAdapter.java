package io.viva.tv.net.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetInfoAdapter {
	private static final String TAG = "NetInfoAdapter";
	private static Map<String, String> netMap = new HashMap<String, String>();
	private static Map<Integer, String> phoneType = new HashMap<Integer, String>();
	private static Map<Integer, String> networkType = new HashMap<Integer, String>();
	private boolean netExists = false;
	private boolean wifiConnected = false;
	private boolean mobileConnected = false;
	private boolean ethConnected = false;
	private boolean isRoaming = false;
	private String n_a = "n/a";
	private String strUnknown = "Unknown";

	public NetInfoAdapter(Context paramContext) {
		phoneType.put(Integer.valueOf(0), "None");
		phoneType.put(Integer.valueOf(1), "GSM");
		phoneType.put(Integer.valueOf(2), "CDMA");
		networkType.put(Integer.valueOf(0), this.strUnknown);
		networkType.put(Integer.valueOf(1), "GPRS");
		networkType.put(Integer.valueOf(2), "EDGE");
		networkType.put(Integer.valueOf(3), "UMTS");
		networkType.put(Integer.valueOf(4), "CDMA");
		networkType.put(Integer.valueOf(5), "EVDO_0");
		networkType.put(Integer.valueOf(6), "EVDO_A");
		networkType.put(Integer.valueOf(7), "1xRTT");
		networkType.put(Integer.valueOf(8), "HSDPA");
		networkType.put(Integer.valueOf(9), "HSUPA");
		networkType.put(Integer.valueOf(10), "HSPA");
		networkType.put(Integer.valueOf(11), "IDEN");
		netMap.put("state", "");
		netMap.put("interface", "");
		netMap.put("type", "");
		netMap.put("netID", "");
		netMap.put("roaming", "");
		netMap.put("ip", "");
		netMap.put("bgdata", "");
		netMap.put("data_activity", this.n_a);
		netMap.put("cell_location", this.n_a);
		netMap.put("cell_type", this.n_a);
		netMap.put("Phone_type", this.n_a);
		this.netExists = false;
		this.wifiConnected = false;
		this.mobileConnected = false;
		this.isRoaming = false;
		ConnectivityManager localConnectivityManager = null;
		try {
			Log.v("NetInfoAdapter", "GET ConnectivityManager");
			localConnectivityManager = (ConnectivityManager) paramContext.getSystemService("connectivity");
		} catch (Exception localException1) {
			Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter6");
			Log.w("NetInfoAdapter", "Cannot get connectivity service! except: " + localException1.getMessage());
			localConnectivityManager = null;
		}
		NetworkInfo localNetworkInfo = null;
		if (null != localConnectivityManager)
			try {
				localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
			} catch (Exception localException2) {
				Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter7");
				Log.w("NetInfoAdapter", "Cannot get active network info! except: " + localException2.getMessage());
				localNetworkInfo = null;
			}
		Log.v("NetInfoAdapter", "check connection states");
		int i = 0;
		try {
			i = (localNetworkInfo != null) && (localNetworkInfo.isConnected()) ? 1 : 0;
		} catch (Exception localException3) {
			Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter8");
			i = 0;
		}
		if (i != 0) {
			Log.v("NetInfoAdapter", "Network is connected");
			this.netExists = true;
			netMap.put("state", "connected");
			WifiManager localWifiManager = null;
			NetworkInterface localNetworkInterface = getInternetInterface();
			try {
				localWifiManager = (WifiManager) paramContext.getSystemService("wifi");
			} catch (Exception localException4) {
				Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter9");
				Log.w("NetInfoAdapter", "Cannot get Wifi service! except: " + localException4.getMessage());
				localWifiManager = null;
			}
			if (null != localNetworkInterface)
				try {
					netMap.put("interface", localNetworkInterface.getName());
					netMap.put("ip", getIPAddress(localNetworkInterface));
				} catch (Exception localException5) {
					Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter10");
					Log.w("NetInfoAdapter", localException5.toString());
				}
			String str = "";
			try {
				str = localNetworkInfo.getTypeName();
				Log.v("NetInfoAdapter", "Connection type is " + str);
			} catch (Exception localException6) {
				Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter11");
				str = "";
				Log.w("NetInfoAdapter", localException6.toString());
			}
			Object localObject;
			if ((null != localWifiManager) && (localWifiManager.isWifiEnabled())) {
				Log.v("NetInfoAdapter", "Wifi connected");
				netMap.put("type", "net_type_wifi");
				localObject = null;
				this.wifiConnected = true;
				try {
					localObject = localWifiManager.getConnectionInfo();
				} catch (Exception localException10) {
					Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter12");
					localObject = null;
				}
				if (null != localObject)
					try {
						netMap.put("netID", ((WifiInfo) localObject).getSSID());
						netMap.put("speed", Integer.toString(((WifiInfo) localObject).getLinkSpeed()) + "Mbps");
					} catch (Exception localException11) {
						Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter13");
					}
			} else {
				if (str.equalsIgnoreCase("MOBILE")) {
					this.mobileConnected = true;
					Log.v("NetInfoAdapter", "Mobile connected");
					netMap.put("type", "net_type_mobile");
				} else if (str.equalsIgnoreCase("ETH")) {
					this.ethConnected = true;
					Log.v("NetInfoAdapter", "Ethernet connected");
					netMap.put("type", "net_type_ethernet");
				} else {
					Log.v("NetInfoAdapter", "Unknown/unsupported network type");
					netMap.put("type", str + " net_type_unsupported");
				}
				try {
					String netID = localNetworkInfo.getExtraInfo();
					netMap.put("netID", netID);
				} catch (Exception localException7) {
					Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter14");
				}
				try {
					netMap.put("bgdata", localConnectivityManager.getBackgroundDataSetting() ? "permitted" : "denied");
					Log.v("NetInfoAdapter", "bgdata: " + (String) netMap.get("bgdata"));
				} catch (Exception localException8) {
					Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter15");
				}
				try {
					this.isRoaming = localNetworkInfo.isRoaming();
				} catch (Exception localException9) {
					Log.v("NetInfoAdapter", "NetInfoAdapter.NetInfoAdapter16");
					this.isRoaming = false;
				}
				if (this.isRoaming)
					netMap.put("roaming", "roaming_yes");
				else
					netMap.put("roaming", "roaming_no");
			}
		} else {
			netMap.put("state", "not_connected");
			netMap.put("dns", "");
		}
	}

	public String getPhoneType(Integer paramInteger) {
		if (phoneType.containsKey(paramInteger))
			return (String) phoneType.get(paramInteger);
		return this.strUnknown;
	}

	public String getNetworkType(Integer paramInteger) {
		if (networkType.containsKey(paramInteger))
			return (String) networkType.get(paramInteger);
		return this.strUnknown;
	}

	public String getInfo(String paramString) {
		return exists(paramString) ? (String) netMap.get(paramString) : "";
	}

	public boolean exists(String paramString) {
		return netMap.containsKey(paramString);
	}

	public boolean isConnected() {
		return this.netExists;
	}

	public boolean isEthConnected() {
		return this.ethConnected;
	}

	public boolean isWifiConnected() {
		return this.wifiConnected;
	}

	public boolean isMobileConnected() {
		return this.mobileConnected;
	}

	private static String getIPAddress(NetworkInterface paramNetworkInterface) {
		String str = "";
		Enumeration localEnumeration = paramNetworkInterface.getInetAddresses();
		while (localEnumeration.hasMoreElements()) {
			InetAddress localInetAddress = (InetAddress) localEnumeration.nextElement();
			str = localInetAddress.getHostAddress();
		}
		return str;
	}

	private static NetworkInterface getInternetInterface() {
		try {
			Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();
			while (localEnumeration.hasMoreElements()) {
				NetworkInterface localNetworkInterface = (NetworkInterface) localEnumeration.nextElement();
				if (!localNetworkInterface.equals(NetworkInterface.getByName("lo")))
					return localNetworkInterface;
			}
		} catch (SocketException localSocketException) {
			Log.e("NetInfoAdapter", "getInternetInterface ERROR:" + localSocketException.toString());
		} catch (Exception localException) {
			Log.v("NetInfoAdapter", "NetInfoAdapter.getInternetInterface");
			Log.e("NetInfoAdapter", "getInternetInterface ERROR:" + localException.toString());
		}
		return null;
	}
}