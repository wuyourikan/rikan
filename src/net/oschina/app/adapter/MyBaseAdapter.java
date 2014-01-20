package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.bean.BaseItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class MyBaseAdapter extends BaseAdapter {
	protected 	Context 			context;		//运行上下文
	protected 	List<BaseItem> 		listItems;		//数据集合
	protected 	LayoutInflater 		listContainer;	//视图容器
	protected	int 				itemViewResource;//自定义项视图源 
	
	//列表项视图的控件集合，用于setTag保存
	static class ListItemView{			
	        public TextView title;  	//标题
		    public TextView author;		//作者
		    public TextView date;  		//日期
		    public TextView count;		//评论|阅览数
	}  
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public MyBaseAdapter(Context context, List<BaseItem> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}
	
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	//标识LinkView上的链接
	private boolean isLinkViewClick = false;

	public boolean isLinkViewClick() {
		return isLinkViewClick;
	}

	public void setLinkViewClick(boolean isLinkViewClick) {
		this.isLinkViewClick = isLinkViewClick;
	}

}
