 package io.viva.tv.app.widget.adapter;
 
 import android.graphics.Bitmap;
 import android.view.View;
 import android.view.ViewGroup;
 
 public abstract class ReflectedBitmapAdapter extends PokerItemAdapter
 {
   @Deprecated
   public View getView(int position, View convertView, ViewGroup parent)
   {
     return null;
   }
 
   public abstract Bitmap getReflectedBitmapHorizontal(int paramInt);
 
   public Bitmap getReflectedBitmapVertical(int position, int columnIndex)
   {
     return null;
   }
 }

