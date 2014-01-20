package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Base;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.FavoriteList.Favorite;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 用户收藏Adapter类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class ListViewFavoriteAdapter extends MyBaseAdapter {
 
	static class ListItemView{				//自定义控件集合  
        public TextView title;  
	}  

	public ListViewFavoriteAdapter(Context context, List<BaseItem> data,int resource) {
		super(context,data,resource);
	}
	
	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//自定义视图
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.title = (TextView)convertView.findViewById(R.id.favorite_listitem_title);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		Favorite favorite = (Favorite)listItems.get(position);
		
		listItemView.title.setText(favorite.title);
		listItemView.title.setTag(favorite);//设置隐藏参数(实体类)
		
		return convertView;
	}
}