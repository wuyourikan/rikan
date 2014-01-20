package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Base;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.LinkView;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用户评论Adapter类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class ListViewCommentAdapter extends MyBaseAdapter {
	
	private BitmapManager 				bmpManager;
	
	//列表item视图的控件集合，用于setTag保存
	static class ListItemView {
			public ImageView face;
	        public TextView name;  
		    public TextView date;  
		    public LinkView content;
		    public TextView client;
		    public LinearLayout relies;
		    public LinearLayout refers;
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommentAdapter(Context context, List<BaseItem> data,int resource) {
		super(context,data,resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
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
			listItemView.face = (ImageView)convertView.findViewById(R.id.comment_listitem_userface);
			listItemView.name = (TextView)convertView.findViewById(R.id.comment_listitem_username);
			listItemView.date = (TextView)convertView.findViewById(R.id.comment_listitem_date);
			listItemView.content = (LinkView)convertView.findViewById(R.id.comment_listitem_content);
			listItemView.client= (TextView)convertView.findViewById(R.id.comment_listitem_client);
			listItemView.relies = (LinearLayout)convertView.findViewById(R.id.comment_listitem_relies);
			listItemView.refers = (LinearLayout)convertView.findViewById(R.id.comment_listitem_refers);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		Comment comment = (Comment)listItems.get(position);
		String faceURL = comment.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setTag(comment);//设置隐藏参数(实体类)
		listItemView.face.setOnClickListener(faceClickListener);
		listItemView.name.setText(comment.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(comment.getPubDate()));
		listItemView.content.setLinkText(comment.getContent());
		listItemView.content.setTag(comment);//设置隐藏参数(实体类)
		
		switch(comment.getAppClient())
		{	
			default:
				listItemView.client.setText("");
				break;
			case Comment.CLIENT_MOBILE:
				listItemView.client.setText("来自:手机");
				break;
			case Comment.CLIENT_ANDROID:
				listItemView.client.setText("来自:Android");
				break;
			case Comment.CLIENT_IPHONE:
				listItemView.client.setText("来自:iPhone");
				break;
			case Comment.CLIENT_WINDOWS_PHONE:
				listItemView.client.setText("来自:Windows Phone");
				break;
		}
		if(StringUtils.isEmpty(listItemView.client.getText().toString()))
			listItemView.client.setVisibility(View.GONE);
		else
			listItemView.client.setVisibility(View.VISIBLE);
		
		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();//先清空
		if(comment.getReplies().size() > 0){
			//评论数目
			View view = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv = (TextView)view.findViewById(R.id.comment_reply_content);
			tv.setText(context.getString(R.string.comment_reply_title, comment.getReplies().size()));
			listItemView.relies.addView(view);
			//评论内容
			for(Reply reply : comment.getReplies()){
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView)view2.findViewById(R.id.comment_reply_content);
				tv2.setText(reply.rauthor+"("+StringUtils.friendly_time(reply.rpubDate)+")："+reply.rcontent);
				listItemView.relies.addView(view2);
			}
			listItemView.relies.setVisibility(View.VISIBLE);
		}
		
		listItemView.refers.setVisibility(View.GONE);
		listItemView.refers.removeAllViews();//先清空
		if(comment.getRefers().size() > 0){
			//引用内容
			for(Refer refer : comment.getRefers()){
				View view = listContainer.inflate(R.layout.comment_refer, null);
				TextView title = (TextView)view.findViewById(R.id.comment_refer_title);
				TextView body = (TextView)view.findViewById(R.id.comment_refer_body);
				title.setText(refer.refertitle);
				body.setText(refer.referbody);
				listItemView.refers.addView(view);
			}
			listItemView.refers.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment comment = (Comment)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), comment.getAuthorId(), comment.getAuthor());
		}
	};
}
