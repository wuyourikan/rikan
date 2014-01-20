package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Base;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.SearchList.SearchResult;
import net.oschina.app.common.StringUtils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 搜索Adapter类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class ListViewSearchAdapter extends MyBaseAdapter {
	
	//列表item视图的控件集合，用于setTag保存
	static class ListItemView extends MyBaseAdapter.ListItemView{
	    public LinearLayout layout;
	}  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewSearchAdapter(Context context, List<BaseItem> data,int resource) {
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
			listItemView.title = (TextView)convertView.findViewById(R.id.search_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.search_listitem_author);
			listItemView.date = (TextView)convertView.findViewById(R.id.search_listitem_date);
			listItemView.layout = (LinearLayout)convertView.findViewById(R.id.search_listitem_ll);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		SearchResult res = (SearchResult)listItems.get(position);
		
		listItemView.title.setText(res.getTitle());
		listItemView.title.setTag(res);//设置隐藏参数(实体类)
		if(StringUtils.isEmpty(res.getAuthor())) {
			listItemView.layout.setVisibility(LinearLayout.GONE);
		}else{
			listItemView.layout.setVisibility(LinearLayout.VERTICAL);
			listItemView.author.setText(res.getAuthor());
			listItemView.date.setText(StringUtils.friendly_time(res.getPubDate()));
		}
		
		return convertView;
	}
}