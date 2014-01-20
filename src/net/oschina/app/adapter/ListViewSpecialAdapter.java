package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.Special;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 专题列表Adapter类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class ListViewSpecialAdapter extends MyBaseAdapter {

	private BitmapManager 				bmpManager;
	static class ListItemView extends MyBaseAdapter.ListItemView{				//自定义控件集合  
		public ImageView img;	//图片
        public TextView intro;  //导语
	}  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewSpecialAdapter(Context context, List<BaseItem> data,int resource) {
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
			listItemView.img = (ImageView)convertView.findViewById(R.id.special_listitem_img);
			listItemView.title = (TextView)convertView.findViewById(R.id.special_listitem_title);
			listItemView.intro = (TextView)convertView.findViewById(R.id.special_listitem_intro);
			//listItemView.author = (TextView)convertView.findViewById(R.id.special_listitem_author);
			//listItemView.count= (TextView)convertView.findViewById(R.id.special_listitem_count);
			//listItemView.date= (TextView)convertView.findViewById(R.id.special_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
      
		//设置文字和图片
		Special special = (Special)listItems.get(position);
		String imgURL = special.getImg();
		//如果图片资源名为default.gif或者imgURL为空，加载默认图片
		if(imgURL.endsWith("default.gif") || StringUtils.isEmpty(imgURL)){
			listItemView.img.setImageResource(R.drawable.widget_dimg);
		}else{
			bmpManager.loadBitmap(imgURL, listItemView.img);
		}
		listItemView.img.setOnClickListener(imgClickListener);
		listItemView.img.setTag(special);
		
		listItemView.title.setText(special.getTitle());
		listItemView.title.setTag(special);//设置隐藏参数(实体类)
		listItemView.intro.setText(special.getIntro());
		//listItemView.author.setText(special.getAuthor());
		//listItemView.date.setText(StringUtils.friendly_time(special.getPubDate()));
		//listItemView.count.setText(special.getAnswerCount()+"回|"+special.getViewCount()+"阅");
		
		return convertView;
	}
	
	private View.OnClickListener imgClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Special special = (Special)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), special.getAuthorId(), special.getAuthor());
			UIHelper.showImageZoomDialog(v.getContext(), special.getImg());
		}
	};
}