package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Huati;
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
 * 话题Adapter类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2012-12-28
 */
public class ListViewHuatiAdapter extends BaseAdapter {
	private Context 					context;//运行上下文
	private List<Huati> 				listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	private BitmapManager 				bmpManager;
	
	static class ListItemView{				//HuatiListItem控件集合 ,用于setTag保存
			public ImageView img;
	        public TextView title;  
	        public TextView intro;  
		    //public TextView author;
		    public TextView date;  
		    //public TextView count;
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewHuatiAdapter(Context context, List<Huati> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dimg_loading));
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
			listItemView.img = (ImageView)convertView.findViewById(R.id.huati_listitem_img);
			listItemView.title = (TextView)convertView.findViewById(R.id.huati_listitem_title);
			listItemView.intro = (TextView)convertView.findViewById(R.id.huati_listitem_intro);
/*			listItemView.author = (TextView)convertView.findViewById(R.id.huati_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.huati_listitem_count);*/
			listItemView.date= (TextView)convertView.findViewById(R.id.huati_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
      
		//设置文字和图片
		Huati huati = listItems.get(position);
		String imgURL = huati.getImg();
		if(imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)){
			listItemView.img.setImageResource(R.drawable.widget_dimg);
		}else{
			bmpManager.loadBitmap(imgURL, listItemView.img);
		}
		listItemView.img.setOnClickListener(imgClickListener);
		listItemView.img.setTag(huati);
		
		listItemView.title.setText(huati.getTitle());
		listItemView.title.setTag(huati);//设置隐藏参数(实体类)
		listItemView.intro.setText(huati.getIntro());
		//listItemView.author.setText(huati.getAuthor());
		//listItemView.date.setText(StringUtils.friendly_time(huati.getPubDate()));
		listItemView.date.setText(huati.getPubDate());
		//listItemView.count.setText(huati.getAnswerCount()+"回|"+huati.getViewCount()+"阅");
		
		return convertView;
	}
	//CHANGE:点击图片进入用户中心 --> 点击图片放大
	private View.OnClickListener imgClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Huati huati = (Huati)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), huati.getAuthorId(), huati.getAuthor());
			UIHelper.showImageZoomDialog(v.getContext(), huati.getImg());
		}
	};
}