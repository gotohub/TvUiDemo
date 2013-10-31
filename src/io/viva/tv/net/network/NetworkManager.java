package io.viva.tv.net.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.HashSet;
import java.util.Iterator;

public class NetworkManager
  implements NetworkMonitor.INetworkCtrListener
{
  private static NetworkManager networkManager = null;
  private Context applicationContext;
  private boolean isConnected = true;
  private NetworkMonitor mNetworkMonitor;
  private HashSet<INetworkListener> listenerSet = new HashSet();
  public static final int UNCONNECTED = -9999;

  public static NetworkManager instance()
  {
    if (null == networkManager)
      networkManager = new NetworkManager();
    return networkManager;
  }

  public void init(Context paramContext)
  {
    this.applicationContext = paramContext;
    this.mNetworkMonitor = new NetworkMonitor(this);
    this.applicationContext.registerReceiver(this.mNetworkMonitor, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
  }

  public void registerStateChangedListener(INetworkListener paramINetworkListener)
  {
    if (this.listenerSet.add(paramINetworkListener))
      paramINetworkListener.onNetworkChanged(this.isConnected);
  }

  public void unregisterStateChangedListener(INetworkListener paramINetworkListener)
  {
    this.listenerSet.remove(paramINetworkListener);
  }

  public void setNetworkConnectedStatus(boolean paramBoolean)
  {
    boolean bool = this.isConnected;
    this.isConnected = paramBoolean;
    if (this.isConnected != bool)
    {
      Iterator localIterator = this.listenerSet.iterator();
      while (localIterator.hasNext())
      {
        INetworkListener localINetworkListener = (INetworkListener)localIterator.next();
        localINetworkListener.onNetworkChanged(paramBoolean);
      }
    }
  }

  public int getNetworkType()
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)this.applicationContext.getSystemService("connectivity");
    NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
    if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()) && (localNetworkInfo.isAvailable()))
      return localNetworkInfo.getType();
    return -9999;
  }

  public boolean isNetworkAvailable()
  {
    return -9999 != getNetworkType();
  }

  public boolean isNetworkConnected()
  {
    return (this.isConnected) && (isNetworkAvailable());
  }

  public Context getApplicationContext()
  {
    return this.applicationContext;
  }

  public static abstract interface INetworkListener
  {
    public abstract void onNetworkChanged(boolean paramBoolean);
  }
}

/* Location:           C:\Users\Administrator\Desktop\AliTvAppSdk.jar
 * Qualified Name:     com.yunos.tv.net.network.NetworkManager
 * JD-Core Version:    0.6.2
 */