 package io.viva.tv.app.widget;
 
 import io.viva.tv.app.widget.adapter.PokerItemAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
 
 class PokerListConnector extends BaseAdapter
 {
   ArrayList<PokerItemMetadata> mList;
   SparseArray<PokerFlow> mPokerFlowList;
   Context mContext;
   PokerGroupView mPokerGroupView;
   static final int TYPE_POKER_FLOW = 1;
   static final int DEFAULT_SPACING = -75;
   PokerGroupDataSetObserver observer;
   private int mPokerVerticalSpacing = -75;
 
   public PokerListConnector(Context context, PokerGroupView pokerGroupView) {
     this.mList = new ArrayList<PokerItemMetadata>();
     this.mPokerFlowList = new SparseArray<PokerFlow>();
     this.mContext = context;
     this.mPokerGroupView = pokerGroupView;
     this.observer = new PokerGroupDataSetObserver();
   }
 
   public int getCount()
   {
     return this.mList.size();
   }
 
   public Object getItem(int position)
   {
     return this.mList.get(position);
   }
 
   public long getItemId(int position)
   {
     return position;
   }
 
   public int getViewTypeCount() {
     return 2;
   }
 
   public int getItemViewType(int position)
   {
     return ((PokerItemMetadata)this.mList.get(position)).mPokerFlowAdapter != null ? 1 : 0;
   }
 
   public void setPokerSpacing(int spacing) {
     this.mPokerVerticalSpacing = spacing;
   }
 
   public void addPokerFlow(SpinnerAdapter adapter) {
     if (adapter != null) {
       PokerItemMetadata item = new PokerItemMetadata(adapter);
       item.mPosition = this.mList.size();
       this.mList.add(item);
       getPokerFlow(item.mPosition).setAdapter(adapter);
 
       adapter.registerDataSetObserver(this.observer);
     }
   }
 
   public void addPokerItemAdapter(PokerItemAdapter itemAdapter) {
     if (itemAdapter != null) {
       PokerItemMetadata item = new PokerItemMetadata(itemAdapter);
       item.mPosition = this.mList.size();
       this.mList.add(item);
 
       itemAdapter.registerDataSetObserver(this.observer);
     }
   }
 
   public void removeItem(int position) {
     PokerItemMetadata metadata = (PokerItemMetadata)this.mList.remove(position);
     if (metadata.mPokerFlowAdapter != null)
       this.mPokerFlowList.remove(position);
     else if (metadata.mPokerItemAdapter == null);
   }
 
   public void removeItem(Adapter adapter)
   {
   }
 
   public void insertItem(Adapter item, int index)
   {
   }
 
   public void clear()
   {
     this.mList.clear();
     this.mPokerFlowList.clear();
   }
 
   public boolean isPokerFlow(int position) {
     return ((PokerItemMetadata)this.mList.get(position)).mPokerFlowAdapter != null;
   }
 
   public PokerFlow getPokerFlow(int position) {
     PokerFlow pokerFlow = (PokerFlow)this.mPokerFlowList.get(position);
     if (pokerFlow == null) {
       pokerFlow = new PokerFlow(this.mContext);
       initPokerFlow(pokerFlow);
       this.mPokerFlowList.put(position, pokerFlow);
     }
     return pokerFlow;
   }
 
   private void initPokerFlow(PokerFlow pokerFlow) {
     pokerFlow.setLayoutParams(new AbsSpinner.LayoutParams(-2, -1));
 
     pokerFlow.setSpacing(this.mPokerVerticalSpacing);
   }
 
   public View getView(int position, View convertView, ViewGroup parent)
   {
     int viewType = getItemViewType(position);
 
     PokerItemMetadata item = (PokerItemMetadata)this.mList.get(position);
     View view = null;
     if (viewType == 1)
     {
       view = getPokerFlow(position);
     }
     else
     {
       view = item.mPokerItemAdapter.getView(position, convertView, parent);
     }
     return view;
   }
 
   static class PokerItemMetadata
   {
     SpinnerAdapter mPokerFlowAdapter;
     Adapter mPokerItemAdapter;
     int mPosition;
     int mKeyValue;
 
     public PokerItemMetadata(SpinnerAdapter adapter)
     {
       this.mPokerFlowAdapter = adapter;
     }
 
     public PokerItemMetadata(PokerItemAdapter itemAdapter) {
       this.mPokerItemAdapter = itemAdapter;
     }
   }
 
   protected class PokerGroupDataSetObserver extends DataSetObserver
   {
     protected PokerGroupDataSetObserver()
     {
     }
 
     public void onChanged()
     {
       PokerListConnector.this.notifyDataSetChanged();
 
       PokerListConnector.this.mPokerGroupView.clearReflectBitmap();
     }
 
     public void onInvalidated()
     {
       PokerListConnector.this.notifyDataSetInvalidated();
 
       PokerListConnector.this.mPokerGroupView.clearReflectBitmap();
     }
   }
 }

