package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Recommend;
import net.oschina.app.bean.RecommendList;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户博客Adapter类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewRecommendAdapter extends BaseAdapter {
	private Context 					context;//运行上下文
	private List<Recommend> 			listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	private int							recommendtype;
	static class ListItemView{				//自定义控件集合  
	        public TextView title;
	        public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView flag;
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewRecommendAdapter(Context context, int recommendtype, List<Recommend> data, int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.recommendtype = recommendtype;
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
			listItemView.title = (TextView)convertView.findViewById(R.id.recommend_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.recommend_listitem_author);
			//listItemView.count = (TextView)convertView.findViewById(R.id.zatan_listitem_commentCount);
			listItemView.date = (TextView)convertView.findViewById(R.id.recommend_listitem_date);
			listItemView.flag = (ImageView)convertView.findViewById(R.id.recommend_listitem_flag);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		Recommend recommend = listItems.get(position);
		
		listItemView.title.setText(recommend.getTitle());
		listItemView.title.setTag(recommend);//设置隐藏参数(实体类)
		listItemView.date.setText(StringUtils.friendly_time(recommend.getPubDate()));
		//listItemView.count.setText(zatan.getCommentCount()+"");
		if(StringUtils.isToday(recommend.getPubDate()))
			listItemView.flag.setVisibility(View.VISIBLE);
		else
			listItemView.flag.setVisibility(View.GONE);
		
		if(recommendtype == RecommendList.CATALOG_USER){
			listItemView.author.setVisibility(View.GONE);
		}else{
			listItemView.author.setText(recommend.getAuthor()+"   发表于");
		}
		
		return convertView;
	}
}