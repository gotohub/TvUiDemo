 package io.viva.tv.app.widget.adapter;
 
 import android.database.DataSetObservable;
 import android.database.DataSetObserver;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.Adapter;
 
 public abstract class PokerItemAdapter
   implements Adapter
 {
   private final DataSetObservable mDataSetObservable = new DataSetObservable();
 
   public void registerDataSetObserver(DataSetObserver observer) {
     this.mDataSetObservable.registerObserver(observer);
   }
 
   public void unregisterDataSetObserver(DataSetObserver observer) {
     this.mDataSetObservable.unregisterObserver(observer);
   }
 
   public void notifyDataSetChanged()
   {
     this.mDataSetObservable.notifyChanged();
   }
 
   public void notifyDataSetInvalidated()
   {
     this.mDataSetObservable.notifyInvalidated();
   }
 
   public int getCount() {
     return 1;
   }
 
   public abstract View getView(int paramInt, View paramView, ViewGroup paramViewGroup);
 
   public Object getItem(int position)
   {
     return null;
   }
 
   public long getItemId(int position)
   {
     return 1L;
   }
 
   public boolean hasStableIds()
   {
     return true;
   }
 
   public int getItemViewType(int position)
   {
     return 1;
   }
 
   public int getViewTypeCount()
   {
     return 1;
   }
 
   public boolean isEmpty()
   {
     return false;
   }
 }

