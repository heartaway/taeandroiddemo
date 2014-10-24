package com.taobao.tae.buyingdemo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.taobao.tae.buyingdemo.model.ItemDataObject;

/**
 * 数据与view的适配器，主要用于list中的数据有不同的类型及不同的view展现。
 * 该类将通过数据的类型来动态获取对应的view的id号，完成绑定动作。
 */
public abstract class DynamicBaseAdapter extends ListBaseAdapter{

	ArrayList<View> RecycledViews = new ArrayList<View>();
	private SparseIntArray typeMap = new SparseIntArray();
	
	private int viewTypeCount;
	private int maxType = -1;
	//private final int VIEW_ID_TAG = 50236;
	/**
	 * 构造方法

	 * 	@param	context:		Context上下文，可采用Application context
	 * 	@param	viewTypeCount:	数据对应展现view的类型数，例如有三种view则传入int 3
	 */
	public DynamicBaseAdapter(Context context,int viewTypeCount) {
		super(context, 0);
		// TODO Auto-generated constructor stub
		this.viewTypeCount = viewTypeCount;
	}

	/**
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
        if (mData.size() <= position)
            return IGNORE_ITEM_VIEW_TYPE;
		int id = mapData2Id((ItemDataObject) mData.get(position));
		if(typeMap.get(id,-1) != -1)
			return typeMap.get(id);
		else{
			maxType++;
			typeMap.put(id, maxType);
			return maxType;
		}
	}

	/**
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return viewTypeCount;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		/*
		 * 根据layout id inflate出来的view有两个tag
		 * 默认tag为view对应的viewholder，
		 * VIEW_ID_TAG tag为对应的layout id
		 */

		ItemDataObject data = (ItemDataObject) mData.get(position);
		int resourceId = mapData2Id(data);
		
		int size = holders.size();
		//已经和data绑定
	    if(convertView != null){
	    	ViewHolder tmpHolder = (ViewHolder) convertView.getTag();
	    	
	    	if(tmpHolder != null && !holders.contains(tmpHolder)){
	    		holders.add(tmpHolder);
	    	}
	    	if(tmpHolder != null && tmpHolder.bindedDo == data){
	    		if(!data.isChanged())
	    			return convertView;
	    	}else{
	    		if(position != 0){
					for(int i = 0;i<size;i++){
						ViewHolder holder = holders.get(i);
						//数据已和已有的view的绑定
						if(holder.bindedDo == data
								&& convertView != holder.contentView && holder.contentView != null){
							//交换view
							View childView = ((ViewGroup)convertView).getChildAt(0);
							View childView1 = ((ViewGroup)holder.contentView).getChildAt(0);
							
							((ViewGroup)convertView).removeViewAt(0);
							((ViewGroup)holder.contentView).removeViewAt(0);
							
							((ViewGroup)convertView).addView(childView1);
							((ViewGroup)holder.contentView).addView(childView);
							
							//交换contentview
							tmpHolder = (ViewHolder) convertView.getTag();
							tmpHolder.contentView = holder.contentView;
							holder.contentView = convertView;
							
							
							//交换viewholder
							tmpHolder.contentView.setTag(tmpHolder);
							convertView.setTag(holder);
			
							convertView.requestLayout();
			
							if(!data.isChanged())
								return convertView;
						}
					}
	    		}
	    	}
	    }
		//通过数据获取到需要绑定的layout id
		
		Integer viewId = null;
		if(convertView != null && convertView instanceof ViewGroup && ((ViewGroup)convertView).getChildCount() > 0){
			viewId = (Integer) ((ViewGroup)convertView).getChildAt(0).getTag(((ViewGroup)convertView).getChildAt(0).getId());
		}
		if(viewId == null || viewId.intValue() != resourceId){//view 和数据部匹配
			//传入的convertView和当前数据不是一个类型
			View reusedView = findReusedView(resourceId);
			//未找到可重用view
			if (reusedView == null) {
				// reusedView = mInflater.inflate(resourceId, null, false);
				reusedView = inflateByResourceId(resourceId);

				if (reusedView == null) {
					if(convertView == null){
						convertView = new FrameLayout(mInflater.getContext());
					}
					return convertView;
				}
				reusedView.setId(resourceId);
				reusedView.setTag(reusedView.getId(),
						Integer.valueOf(resourceId));
				ViewHolder viewHolder = view2Holder(reusedView);
				reusedView.setTag(viewHolder);
				holders.add(viewHolder);

			}
			
			if(convertView == null){
				//构建新的convertView
				FrameLayout frame = new FrameLayout(mInflater.getContext());
				frame.addView(reusedView);
				convertView = frame;
				ViewHolder viewHolder = (ViewHolder) reusedView.getTag();
				viewHolder.contentView = convertView;
				viewHolder.bindedDo = null;
				convertView.setTag(viewHolder);
			}else{
				View view = ((ViewGroup)convertView).getChildAt(0);
				((ViewGroup)convertView).removeView(view);
				((ViewGroup)convertView).addView(reusedView);
				ViewHolder viewHolder = (ViewHolder) reusedView.getTag();
				viewHolder.contentView = convertView;
				viewHolder.bindedDo = null;
				convertView.setTag(viewHolder);
				
				viewHolder = (ViewHolder) view.getTag();
				viewHolder.bindedDo = null;
				viewHolder.contentView = null;
				RecycledViews.add(view);
			}
		}
		//绑定数据
		ViewHolder viewHolder = convertView != null ? (ViewHolder) convertView.getTag() : new ViewHolder() {};
		if(viewHolder.bindedDo != data || data.isChanged()){
	    	viewHolder.bindedDo = data;
	    	bindView(viewHolder, data);
	    }
	
	    return convertView;
	}
	
	private View findReusedView(int id){
		//回收的view里查找可重用view
		int size = RecycledViews.size();
		View reusedView = null;
		for(int i=0;i<size;i++){
			reusedView = RecycledViews.get(i);
			Integer tagId = (Integer) reusedView.getTag(reusedView.getId());
			if(tagId.intValue() == id){
				//找到重用view
				RecycledViews.remove(reusedView);
				break;
			}	
			reusedView = null;
		}
		return reusedView;
	}

	@Override
	public void destroy() {
		RecycledViews.clear();
		super.destroy();
	}
	
	/**
	 * 获取数据对应view的layout资源id号
	 * @param		dataObjec:	item数据
	 * @return		对应需绑定view的layout资源id
	 */
	protected abstract int mapData2Id(ItemDataObject dataObjec);
	/**
	 * 通过资源id号获取view
	 * @param		resourceId
	 * @return		对应需绑定view
	 */
	protected View inflateByResourceId(int resourceId){
		return  mInflater.inflate(resourceId, null, false);
	}
}
