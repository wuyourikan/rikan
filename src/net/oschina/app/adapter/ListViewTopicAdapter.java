package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Topic;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 话题Adapter类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2012-12-28
 */
public class ListViewTopicAdapter extends MyBaseAdapter {

	private	BitmapManager	bmpManager;
	
	//HuatiListItem控件集合 ,用于setTag保存
	static class ListItemView extends MyBaseAdapter.ListItemView{		
		public ImageView img;	//图片
		public TextView intro;  //导语
	}  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewTopicAdapter(Context context, List<BaseItem> data,int resource) {
		super(context,data,resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dimg_loading));
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
			listItemView.img = (ImageView)convertView.findViewById(R.id.topic_listitem_img);
			listItemView.title = (TextView)convertView.findViewById(R.id.topic_listitem_title);
			listItemView.intro = (TextView)convertView.findViewById(R.id.topic_listitem_intro);
/*			listItemView.author = (TextView)convertView.findViewById(R.id.topic_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.topic_listitem_count);*/
			listItemView.date= (TextView)convertView.findViewById(R.id.topic_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
      
		//设置文字和图片
		//IMPORTANT：这里是将List<MyBase>向下还原类型的唯一地方。之前的差异初始化在parse方法中，以后统一接口为List<MyBase>
		Topic topic = (Topic)listItems.get(position);
		String imgURL = topic.getImg();
		if(imgURL.endsWith("default.gif") || StringUtils.isEmpty(imgURL)){
			listItemView.img.setImageResource(R.drawable.widget_dimg);
		}else{
			bmpManager.loadBitmap(imgURL, listItemView.img);
		}
		listItemView.img.setOnClickListener(imgClickListener);
		listItemView.img.setTag(topic);
		
		listItemView.title.setText(topic.getTitle());
		listItemView.title.setTag(topic);//设置隐藏参数(实体类)
		listItemView.intro.setText(topic.getIntro());
		//listItemView.author.setText(topic.getAuthor());
		//listItemView.date.setText(StringUtils.friendly_time(topic.getPubDate()));
		listItemView.date.setText(topic.getPubDate());
		//listItemView.count.setText(topic.getAnswerCount()+"回|"+topic.getViewCount()+"阅");
		
		return convertView;
	}
	
	//CHANGE:点击图片进入用户中心 --> 点击图片放大
	private View.OnClickListener imgClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Topic topic = (Topic)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), topic.getAuthorId(), topic.getAuthor());
			UIHelper.showImageZoomDialog(v.getContext(), topic.getImg());

		}
	};
}
