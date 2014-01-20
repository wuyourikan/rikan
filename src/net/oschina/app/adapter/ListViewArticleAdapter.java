package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Article;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.common.StringUtils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 文章Adapter类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewArticleAdapter extends MyBaseAdapter {
	
	//AriticalListItem控件集合 ,用于setTag保存
	static class ListItemView extends MyBaseAdapter.ListItemView{
		    //public ImageView flag;
	}  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewArticleAdapter(Context context, List<BaseItem> data,int resource) {
		super(context,data,resource);
	}
	
	
	/**
	 * ListView Item设置
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//自定义视图
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.title = (TextView)convertView.findViewById(R.id.article_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.article_listitem_author);
			//listItemView.count= (TextView)convertView.findViewById(R.id.article_listitem_commentCount);
			listItemView.date = (TextView)convertView.findViewById(R.id.article_listitem_date);
			//listItemView.flag= (ImageView)convertView.findViewById(R.id.article_listitem_flag);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		Article article = (Article)listItems.get(position);
		
		listItemView.title.setText(article.getTitle());
		listItemView.title.setTag(article);//设置隐藏参数(实体类)
		listItemView.author.setText(article.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(article.getPubDate()));
		//listItemView.count.setText(article.getCommentCount()+"");

		/*if(StringUtils.isToday(article.getPubDate()))
			listItemView.flag.setVisibility(View.VISIBLE);
		else
			listItemView.flag.setVisibility(View.GONE);*/
		
		return convertView;
	}
}
