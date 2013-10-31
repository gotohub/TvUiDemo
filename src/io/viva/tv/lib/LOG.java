package io.viva.tv.lib;

import android.util.Log;

public class LOG
{
  public static void d(String paramString1, boolean paramBoolean, String paramString2)
  {
    if (paramBoolean)
      Log.d(paramString1, paramString2);
  }

  public static void i(String paramString1, boolean paramBoolean, String paramString2)
  {
    if (paramBoolean)
      Log.i(paramString1, paramString2);
  }
}