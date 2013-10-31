 package io.viva.tv.app.widget.adapter;
 
 import android.content.Context;
 import android.content.res.Resources;
 import android.graphics.Bitmap;
 import android.graphics.drawable.BitmapDrawable;
 import android.graphics.drawable.Drawable;
 import android.util.Log;
 import java.util.ArrayList;
 import java.util.List;
 
 public class ResourceImageAdapter extends AbsCoverFlowAdapter
 {
   private static final String TAG = ResourceImageAdapter.class.getSimpleName();
   private static final int DEFAULT_LIST_SIZE = 20;
   private static final List<Integer> IMAGE_RESOURCE_IDS = new ArrayList(20);
 
   private static final int[] DEFAULT_RESOURCE_LIST = new int[0];
   private final Context context;
 
   public ResourceImageAdapter(Context context)
   {
     this.context = context;
     setResources(DEFAULT_RESOURCE_LIST);
   }
 
   public final synchronized void setResources(int[] resourceIds)
   {
     IMAGE_RESOURCE_IDS.clear();
     for (int resourceId : resourceIds) {
       IMAGE_RESOURCE_IDS.add(Integer.valueOf(resourceId));
     }
     notifyDataSetChanged();
   }
 
   public synchronized int getCount()
   {
     return IMAGE_RESOURCE_IDS.size();
   }
 
   protected Bitmap createBitmap(int position)
   {
     Log.v(TAG, "creating item " + position);
     Bitmap bitmap = null;
     Drawable d = this.context.getResources().getDrawable(((Integer)IMAGE_RESOURCE_IDS.get(position)).intValue());
 
     if ((d instanceof BitmapDrawable)) {
       bitmap = ((BitmapDrawable)d).getBitmap();
     }
 
     return bitmap;
   }
 }

