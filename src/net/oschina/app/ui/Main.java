package net.oschina.app.ui;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.AppManager;
import net.oschina.app.R;
//import net.oschina.app.adapter.ListViewActiveAdapter;
import net.oschina.app.adapter.ListViewZatanAdapter;
//import net.oschina.app.adapter.ListViewMessageAdapter;
import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.adapter.ListViewRecommendAdapter;
import net.oschina.app.adapter.ListViewHuatiAdapter;
import net.oschina.app.adapter.ListViewZhuantiAdapter;
//import net.oschina.app.bean.Active;
//import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Zatan;
import net.oschina.app.bean.ZatanList;
//import net.oschina.app.bean.MessageList;
//import net.oschina.app.bean.Messages;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Recommend;
import net.oschina.app.bean.RecommendList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Huati;
import net.oschina.app.bean.HuatiList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Zhuanti;
import net.oschina.app.bean.ZhuantiList;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.common.UpdateManager;
import net.oschina.app.widget.BadgeView;
import net.oschina.app.widget.NewDataToast;
import net.oschina.app.widget.PullToRefreshListView;
import net.oschina.app.widget.ScrollLayout;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class Main extends Activity {
	//public static final int QUICKACTION_LOGIN_OR_LOGOUT = 0;
	//public static final int QUICKACTION_USERINFO = 1;
	//public static final int QUICKACTION_SOFTWARE = 2;
	public static final int QUICKACTION_SEARCH = 0;
	public static final int QUICKACTION_SETTING = 1;
	public static final int QUICKACTION_EXIT = 2;
	

    private ScrollLayout mScrollLayout;
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;
	
	private RadioButton fbNews;
	private RadioButton fbHuati;
	private RadioButton fbZhuanti;
	private ImageView fbSetting;
	
	private ImageButton mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;
	private ImageButton mHead_search;
	//private ImageButton mHeadPub_huati;
	//private ImageButton mHeadPub_tweet;
	
	private int curNewsCatalog = NewsList.CATALOG_ALLN;
	private int curHuatiCatalog = HuatiList.CATALOG_HISTORY;
	private int curZhuantiCatalog = ZhuantiList.CATALOG_COMMON;
	//private int curActiveCatalog = ActiveList.CATALOG_LASTEST;
	
	private PullToRefreshListView lvRecommend;
	private PullToRefreshListView lvNews;
	private PullToRefreshListView lvZatan;
	private PullToRefreshListView lvAll;
	private PullToRefreshListView lvHuati;
	private PullToRefreshListView lvZhuanti;
	//private PullToRefreshListView lvActive;
	//private PullToRefreshListView lvMsg;
	
	private ListViewNewsAdapter lvNewsAdapter;
	private ListViewZatanAdapter lvZatanAdapter;
	private ListViewRecommendAdapter lvRecommendAdapter;
	private ListViewAllAdapter lvAllAdapter;
	private ListViewHuatiAdapter lvHuatiAdapter;
	private ListViewZhuantiAdapter lvZhuantiAdapter;
	//private ListViewActiveAdapter lvActiveAdapter;
	//private ListViewMessageAdapter lvMsgAdapter;
	
	private List<News> lvNewsData = new ArrayList<News>();
	private List<Zatan> lvZatanData = new ArrayList<Zatan>();
	private List<Recommend> lvRecommendData = new ArrayList<Recommend>();
	private List<All> lvAllData = new ArrayList<All>();
	private List<Huati> lvHuatiData = new ArrayList<Huati>();
	private List<Zhuanti> lvZhuantiData = new ArrayList<Zhuanti>();
	//private List<Active> lvActiveData = new ArrayList<Active>();
	//private List<Messages> lvMsgData = new ArrayList<Messages>();
	
	private Handler lvNewsHandler;
	private Handler lvZatanHandler;
	private Handler lvRecommendHandler;
	private Handler lvAllHandler;
	private Handler lvHuatiHandler;
	private Handler lvZhuantiHandler;
	//private Handler lvActiveHandler;
	//private Handler lvMsgHandler;
	
	private int lvNewsSumData;
	private int lvZatanSumData;
	private int lvRecommendSumData;
	private int lvAllSumData;
	private int lvHuatiSumData;
	private int lvZhuantiSumData;
	//private int lvActiveSumData;
	//private int lvMsgSumData;
	
	private Button framebtn_News_lastest;
	private Button framebtn_News_zatan;
	private Button framebtn_News_recommend;
	private Button framebtn_News_all;
	private Button framebtn_Huati_history;
	private Button framebtn_Huati_think;
	private Button framebtn_Huati_politics;
	//private Button framebtn_Question_job;
	//private Button framebtn_Question_site;
	private Button framebtn_Zhuanti_common;
	//private Button framebtn_Tweet_hot;
	private Button framebtn_Zhuanti_particular;
	//private Button framebtn_Active_lastest;
	//private Button framebtn_Active_atme;
	//private Button framebtn_Active_comment;
	//private Button framebtn_Active_myself;
	//private Button framebtn_Active_message;
	
	private View lvNews_footer;
	private View lvZatan_footer;
	private View lvRecommend_footer;
	private View lvAll_footer;
	private View lvHuati_footer;
	private View lvZhuanti_footer;
	//private View lvActive_footer;
	//private View lvMsg_footer;
	
	private TextView lvNews_foot_more;
	private TextView lvZatan_foot_more;
	private TextView lvRecommend_foot_more;
	private TextView lvAll_foot_more;
	private TextView lvHuati_foot_more;
	private TextView lvZhuanti_foot_more;
	//private TextView lvActive_foot_more;
	//private TextView lvMsg_foot_more;
	
	private ProgressBar lvNews_foot_progress;
	private ProgressBar lvZatan_foot_progress;
	private ProgressBar lvRecommend_foot_progress;
	private ProgressBar lvAll_foot_progress;
	private ProgressBar lvHuati_foot_progress;
	private ProgressBar lvZhuanti_foot_progress;
	//private ProgressBar lvActive_foot_progress;
	//private ProgressBar lvMsg_foot_progress;
	
	private QuickActionWidget mGrid;//快捷栏控件
	
	private AppContext appContext;//全局Context
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        appContext = (AppContext)getApplication();
        //网络连接判断
        if(!appContext.isNetworkConnected())
        	UIHelper.ToastMessage(this, R.string.network_not_connected);
		
		AppManager.getAppManager().addActivity(this);
		
		this.initHeadView();
		this.initFootBar();
		this.initPageScroll();
		this.initQuickActionGrid();
		this.initFrameListView();
		//this.initBadgeView();
		this.initFrameButton();
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	if(mViewCount == 0) mViewCount = 3;
    	if(mCurSel == 0 && !fbNews.isChecked()) {
    		fbNews.setChecked(true);
    		fbHuati.setChecked(false);
    		fbZhuanti.setChecked(false);
    	}
    	//读取左右滑动配置
    	mScrollLayout.setIsScroll(appContext.isScroll());
    }
	
	   /**
     * 初始化快捷栏
     */
    private void initQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        //mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_login, R.string.main_menu_login));
        //mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_myinfo, R.string.main_menu_myinfo));
        //mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_software, R.string.main_menu_software));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_search, R.string.main_menu_search));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_setting, R.string.main_menu_setting));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_exit, R.string.main_menu_exit));
        
        mGrid.setOnQuickActionClickListener(mActionListener);
    }
    
    /**
     * 快捷栏item点击事件
     */
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		switch (position) {
    		/*case QUICKACTION_LOGIN_OR_LOGOUT://用户登录-注销
    			UIHelper.loginOrLogout(Main.this);
    			break;
    		case QUICKACTION_USERINFO://我的资料
    			UIHelper.showUserInfo(Main.this);
    			break;
    		case QUICKACTION_SOFTWARE://开源软件
    			UIHelper.showSoftware(Main.this);
    			break;*/
    		case QUICKACTION_SEARCH://搜索
    			UIHelper.showSearch(Main.this);
    			break;
    		case QUICKACTION_SETTING://设置
    			UIHelper.showSetting(Main.this);
    			break;
    		case QUICKACTION_EXIT://退出
    			UIHelper.Exit(Main.this);
    			break;
    		}
        }
    };
    
    /**
     * 初始化所有ListView
     */
    private void initFrameListView()
    {
    	//初始化listview控件
		this.initNewsListView();
		this.initZatanListView();
		this.initRecommendListView();
		this.initAllListView();
		this.initHuatiListView();
		this.initZhuantiListView();
		//this.initActiveListView();
		//this.initMsgListView();
		//加载listview数据
		this.initFrameListViewData();
    }
    
    /**
     * 初始化所有ListView数据
     */
    private void initFrameListViewData()
    {
        //初始化Handler
        lvNewsHandler = this.getLvHandler(lvNews, lvNewsAdapter, lvNews_foot_more, lvNews_foot_progress, AppContext.PAGE_SIZE);
        lvZatanHandler = this.getLvHandler(lvZatan, lvZatanAdapter, lvZatan_foot_more, lvZatan_foot_progress, AppContext.PAGE_SIZE);
        lvRecommendHandler = this.getLvHandler(lvRecommend, lvRecommendAdapter, lvRecommend_foot_more, lvRecommend_foot_progress, AppContext.PAGE_SIZE);
        lvAllHandler = this.getLvHandler(lvAll, lvAlladapter, lvAll_foot_more, lvAll_foot_progress, AppContext.PAGE_SIZE);
        lvHuatiHandler = this.getLvHandler(lvHuati, lvHuatiAdapter, lvHuati_foot_more, lvHuati_foot_progress, AppContext.PAGE_SIZE);  
        lvZhuantiHandler = this.getLvHandler(lvZhuanti, lvZhuantiAdapter, lvZhuanti_foot_more, lvZhuanti_foot_progress, AppContext.PAGE_SIZE);  
        //lvActiveHandler = this.getLvHandler(lvActive, lvActiveAdapter, lvActive_foot_more, lvActive_foot_progress, AppContext.PAGE_SIZE); 
        //lvMsgHandler = this.getLvHandler(lvMsg, lvMsgAdapter, lvMsg_foot_more, lvMsg_foot_progress, AppContext.PAGE_SIZE);      	
    	
        //加载资讯数据
		if(lvNewsData.isEmpty()) {
			loadLvNewsData(curNewsCatalog, 0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}
    }
    
    /**
     * 初始化推荐列表
     */
	private void initRecommendListView()
    {
        lvRecommendAdapter = new ListViewRecommendAdapter(this, lvRecommendData, R.layout.recommend_listitem);        
        lvRecommend_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvRecommend_foot_more = (TextView)lvRecommend_footer.findViewById(R.id.listview_foot_more);
        lvRecommend_foot_progress = (ProgressBar)lvRecommend_footer.findViewById(R.id.listview_foot_progress);
        lvRecommend = (PullToRefreshListView)findViewById(R.id.frame_listview_recommend);
        lvRecommend.addFooterView(lvRecommend_footer);//添加底部视图  必须在setAdapter前
        lvRecommend.setAdapter(lvRecommendAdapter); 
        lvRecommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击头部、底部栏无效
        		if(position == 0 || view == lvRecommend_footer) return;
        		
        		Recommend recommend = null;        		
        		//判断是否是TextView
        		if(view instanceof TextView){
        			recommend = (Recommend)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.recommend_listitem_title);
        			recommend = (Recommend)tv.getTag();
        		}
        		if(recommend == null) return;
        		
        		//跳转到博客详情
        		UIHelper.showUrlRedirect(view.getContext(), recommend.getUrl());                                                      //此处需要详细看
        	}        	
		});
        lvRecommend.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvRecommend.onScrollStateChanged(view, scrollState);
				
				//数据为空--不用继续下面代码了
				if(lvRecommendData.isEmpty()) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvRecommend_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvRecommend.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvRecommend.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvRecommend_foot_more.setText(R.string.load_ing);
					lvRecommend_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvRecommendSumData/AppContext.PAGE_SIZE;
					loadLvRecommendData(curNewsCatalog, pageIndex, lvRecommendHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvRecommend.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvRecommend.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvRecommendData(curNewsCatalog, 0, lvRecommendHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });					
    }
    
    /**
     * 初始化新闻列表
     */
    private void initNewsListView()
    {
        lvNewsAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);        
        lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvNews_foot_more = (TextView)lvNews_footer.findViewById(R.id.listview_foot_more);
        lvNews_foot_progress = (ProgressBar)lvNews_footer.findViewById(R.id.listview_foot_progress);
        lvNews = (PullToRefreshListView)findViewById(R.id.frame_listview_news);
        lvNews.addFooterView(lvNews_footer);//添加底部视图  必须在setAdapter前
        lvNews.setAdapter(lvNewsAdapter); 
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击头部、底部栏无效
        		if(position == 0 || view == lvNews_footer) return;
        		
        		News news = null;        		
        		//判断是否是TextView
        		if(view instanceof TextView){
        			news = (News)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.news_listitem_title);
        			news = (News)tv.getTag();
        		}
        		if(news == null) return;
        		
        		//跳转到新闻详情
        		UIHelper.showNewsRedirect(view.getContext(), news);
        	}        	
		});
        lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvNews.onScrollStateChanged(view, scrollState);
				
				//数据为空--不用继续下面代码了
				if(lvNewsData.isEmpty()) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvNews_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvNews.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvNewsSumData/AppContext.PAGE_SIZE;
					loadLvNewsData(curNewsCatalog, pageIndex, lvNewsHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvNews.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvNewsData(curNewsCatalog, 0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });					
    }
   
    /**
     * 初始化杂谈列表
     */
	private void initZatanListView()
    {
        lvZatanAdapter = new ListViewZatanAdapter(this, ZatanList.CATALOG_LATEST, lvZatanData, R.layout.zatan_listitem);        
        lvZatan_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvZatan_foot_more = (TextView)lvZatan_footer.findViewById(R.id.listview_foot_more);
        lvZatan_foot_progress = (ProgressBar)lvZatan_footer.findViewById(R.id.listview_foot_progress);
        lvZatan = (PullToRefreshListView)findViewById(R.id.frame_listview_zatan);
        lvZatan.addFooterView(lvZatan_footer);//添加底部视图  必须在setAdapter前
        lvZatan.setAdapter(lvZatanAdapter); 
        lvZatan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击头部、底部栏无效
        		if(position == 0 || view == lvZatan_footer) return;
        		
        		Zatan zatan = null;        		
        		//判断是否是TextView
        		if(view instanceof TextView){
        			zatan = (Zatan)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.zatan_listitem_title);
        			zatan = (Zatan)tv.getTag();
        		}
        		if(zatan == null) return;
        		
        		//跳转到博客详情
        		UIHelper.showUrlRedirect(view.getContext(), zatan.getUrl());
        	}        	
		});
        lvZatan.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvZatan.onScrollStateChanged(view, scrollState);
				
				//数据为空--不用继续下面代码了
				if(lvZatanData.isEmpty()) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvZatan_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvZatan.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvZatan.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvZatan_foot_more.setText(R.string.load_ing);
					lvZatan_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvZatanSumData/AppContext.PAGE_SIZE;
					loadLvZatanData(curNewsCatalog, pageIndex, lvZatanHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvZatan.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvZatan.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvZatanData(curNewsCatalog, 0, lvZatanHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });					
    }
    
	/**
     * 初始化全部列表
     */
	private void initAllListView()
    {
        lvAllAdapter = new ListViewRecommendAdapter(this, lvAllData, R.layout.All_listitem);        
        lvAll_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvAll_foot_more = (TextView)lvAll_footer.findViewById(R.id.listview_foot_more);
        lvAll_foot_progress = (ProgressBar)lvAll_footer.findViewById(R.id.listview_foot_progress);
        lvAll = (PullToRefreshListView)findViewById(R.id.frame_listview_All);
        lvAll.addFooterView(lvAll_footer);//添加底部视图  必须在setAdapter前
        lvAll.setAdapter(lvAllAdapter); 
        lvAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击头部、底部栏无效
        		if(position == 0 || view == lvAll_footer) return;
        		
        		All all = null;        		
        		//判断是否是TextView
        		if(view instanceof TextView){
        			all = (All)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.All_listitem_title);
        			all = (All)tv.getTag();
        		}
        		if(all == null) return;
        		
        		//跳转到博客详情
        		UIHelper.showUrlRedirect(view.getContext(), all.getUrl());                                                      //此处需要详细看
        	}        	
		});
        lvAll.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvAll.onScrollStateChanged(view, scrollState);
				
				//数据为空--不用继续下面代码了
				if(lvAllData.isEmpty()) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvAll_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvAll.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvAll.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvAll_foot_more.setText(R.string.load_ing);
					lvAll_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvAllSumData/AppContext.PAGE_SIZE;
					loadLvRecommendData(curNewsCatalog, pageIndex, lvAllHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvAll.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvAll.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvRecommendData(curNewsCatalog, 0, lvAllHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });					
    }
	
    /**
     * 初始化话题列表
     */
    private void initHuatiListView()
    {    	
        lvHuatiAdapter = new ListViewHuatiAdapter(this, lvHuatiData, R.layout.huati_listitem);        
        lvHuati_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvHuati_foot_more = (TextView)lvHuati_footer.findViewById(R.id.listview_foot_more);
        lvHuati_foot_progress = (ProgressBar)lvHuati_footer.findViewById(R.id.listview_foot_progress);
        lvHuati = (PullToRefreshListView)findViewById(R.id.frame_listview_huati);
        lvHuati.addFooterView(lvHuati_footer);//添加底部视图  必须在setAdapter前
        lvHuati.setAdapter(lvHuatiAdapter); 
        lvHuati.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击头部、底部栏无效
        		if(position == 0 || view == lvHuati_footer) return;
        		
        		Huati huati = null;		
        		//判断是否是TextView
        		if(view instanceof TextView){
        			huati = (Huati)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.huati_listitem_title);
        			huati = (Huati)tv.getTag();
        		}
        		if(huati == null) return;
        		
        		//跳转到问答详情
        		//UIHelper.showHuatiDetail(view.getContext(), post.getId());
        	}        	
		});
        lvHuati.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvHuati.onScrollStateChanged(view, scrollState);
				
				//数据为空--不用继续下面代码了
				if(lvHuatiData.isEmpty()) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvHuati_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvHuati.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvHuati_foot_more.setText(R.string.load_ing);
					lvHuati_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvHuatiSumData/AppContext.PAGE_SIZE;
					loadLvHuatiData(curHuatiCatalog, pageIndex, lvHuatiHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvHuati.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvHuati.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvHuatiData(curHuatiCatalog, 0, lvHuatiHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });			
    }
	
    /**
     * 初始化专题列表
     */
    private void initZhuantiListView()
    {
    	 lvZhuantiAdapter = new ListViewZhuantiAdapter(this, lvZhuantiData, R.layout.zhuanti_listitem);        
         lvZhuanti_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
         lvZhuanti_foot_more = (TextView)lvHuati_footer.findViewById(R.id.listview_foot_more);
         lvZhuanti_foot_progress = (ProgressBar)lvHuati_footer.findViewById(R.id.listview_foot_progress);
         lvZhuanti = (PullToRefreshListView)findViewById(R.id.frame_listview_zhuanti);
         lvZhuanti.addFooterView(lvZhuanti_footer);//添加底部视图  必须在setAdapter前
         lvZhuanti.setAdapter(lvZhuantiAdapter); 
         lvZhuanti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         		//点击头部、底部栏无效
         		if(position == 0 || view == lvZhuanti_footer) return;
         		
         		Zhuanti zhuanti = null;		
         		//判断是否是TextView
         		if(view instanceof TextView){
         			zhuanti = (Zhuanti)view.getTag();
         		}else{
         			TextView tv = (TextView)view.findViewById(R.id.zhuanti_listitem_title);
         			zhuanti = (Zhuanti)tv.getTag();
         		}
         		if(zhuanti == null) return;
         		
         		//跳转到问答详情
         		//UIHelper.showHuatiDetail(view.getContext(), post.getId());
         	}        	
 		});
         lvZhuanti.setOnScrollListener(new AbsListView.OnScrollListener() {
 			public void onScrollStateChanged(AbsListView view, int scrollState) {
 				lvZhuanti.onScrollStateChanged(view, scrollState);
 				
 				//数据为空--不用继续下面代码了
 				if(lvZhuantiData.isEmpty()) return;
 				
 				//判断是否滚动到底部
 				boolean scrollEnd = false;
 				try {
 					if(view.getPositionForView(lvZhuanti_footer) == view.getLastVisiblePosition())
 						scrollEnd = true;
 				} catch (Exception e) {
 					scrollEnd = false;
 				}
 				
 				int lvDataState = StringUtils.toInt(lvZhuanti.getTag());
 				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
 				{
 					lvZhuanti_foot_more.setText(R.string.load_ing);
 					lvZhuanti_foot_progress.setVisibility(View.VISIBLE);
 					//当前pageIndex
 					int pageIndex = lvZhuantiSumData/AppContext.PAGE_SIZE;
 					loadLvZhuantiData(curZhuantiCatalog, pageIndex, lvZhuantiHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
 				}
 			}
 			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
 				lvZhuanti.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
 			}
 		});
         lvZhuanti.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
             public void onRefresh() {
             	loadLvZhuantiData(curZhuantiCatalog, 0, lvZhuantiHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
             }
         });
    }
    
    /**
     * 初始化头部视图
     */
	private void initHeadView()
	{
		mHeadLogo = (ImageButton)findViewById(R.id.main_head_logo);
    	mHeadTitle = (TextView)findViewById(R.id.main_head_title);
    	mHeadProgress = (ProgressBar)findViewById(R.id.main_head_progress);
    	mHead_search = (ImageButton)findViewById(R.id.main_head_search);
    	//mHeadPub_huati = (ImageButton)findViewById(R.id.main_head_pub_post);
    	//mHeadPub_tweet = (ImageButton)findViewById(R.id.main_head_pub_tweet);
    	
    	mHead_search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showSearch(v.getContext());
			}
		});
    	mHeadLogo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mScrollLayout.scrollTo(0, 0);
			}
		});
	}

    /**
     * 初始化底部栏
     */
    private void initFootBar()
    {
    	fbNews = (RadioButton)findViewById(R.id.main_footbar_news);
    	fbHuati = (RadioButton)findViewById(R.id.main_footbar_huati);
    	fbZhuanti = (RadioButton)findViewById(R.id.main_footbar_zhuanti);
    	
    	fbSetting = (ImageView)findViewById(R.id.main_footbar_setting);
    	fbSetting.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {    			
    			//展示快捷栏&判断是否登录&是否加载文章图片
    			//UIHelper.showSettingLoginOrLogout(Main.this, mGrid.getQuickAction(0));
    			mGrid.show(v);
    		}
    	});    	
    }
    
	/**
     * 初始化水平滚动翻页
     */
    private void initPageScroll()
    {
    	mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
    	
    	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout_footer);
    	mHeadTitles = getResources().getStringArray(R.array.head_titles);
    	mViewCount = mScrollLayout.getChildCount();
    	mButtons = new RadioButton[mViewCount];
    	
    	for(int i = 0; i < mViewCount; i++)
    	{
    		mButtons[i] = (RadioButton) linearLayout.getChildAt(i*2);
    		mButtons[i].setTag(i);
    		mButtons[i].setChecked(false);
    		mButtons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					int pos = (Integer)(v.getTag());
					//点击当前项刷新
	    			if(mCurSel == pos) {
		    			switch (pos) {
						case 0://资讯+杂谈
							if(lvRecommend.getVisibility() == View.VISIBLE)
								lvRecommend.clickRefresh();
							else if(lvNews.getVisibility() == View.VISIBLE)
								lvNews.clickRefresh();
							else if(lvZatan.getVisibility() == View.VISIBLE)
								lvZatan.clickRefresh();
							else
								lvAll.clickRefresh();
							break;	
						case 1://话题
							lvHuati.clickRefresh();
							break;
						case 2://专题
							lvZhuanti.clickRefresh();
							break;
						/*case 3://动态+留言
							if(lvActive.getVisibility() == View.VISIBLE)
								lvActive.clickRefresh();
							else
								lvMsg.clickRefresh();
							break;*/
						}
	    			}
					setCurPoint(pos);
					mScrollLayout.snapToScreen(pos);
				}
			});
    	}
    	
    	//设置第一显示屏
    	mCurSel = 0;
    	mButtons[mCurSel].setChecked(true);
    	
    	mScrollLayout.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
			public void OnViewChange(int viewIndex) {
				//切换列表视图-如果列表数据为空：加载数据
				switch (viewIndex) {
				case 0://资讯
					if(lvRecommendData.isEmpty()) {
						loadLvRecommendData(curNewsCatalog, 0, lvRecommendHandler, UIHelper.LISTVIEW_ACTION_INIT);    //由资讯改为推荐
					}
					break;	
				case 1://话题
					if(lvHuatiData.isEmpty()) {
						loadLvHuatiData(curHuatiCatalog, 0, lvHuatiHandler, UIHelper.LISTVIEW_ACTION_INIT);
					} 
					break;
				case 2://专题
					if(lvZhuantiData.isEmpty()) {
						loadLvZhuantiData(curZhuantiCatalog, 0, lvZhuantiHandler, UIHelper.LISTVIEW_ACTION_INIT);
					}
					break;
				/*case 3://动态
					if(lvActive.getVisibility() == View.VISIBLE) {
						if(lvActiveData.isEmpty()) {
							loadLvActiveData(curActiveCatalog, 0, lvActiveHandler, UIHelper.LISTVIEW_ACTION_INIT);
						}
					} else {
						if(lvMsgData.isEmpty()) {
							loadLvMsgData(0, lvMsgHandler, UIHelper.LISTVIEW_ACTION_INIT);
						}
					}
					break;*/
				}
				setCurPoint(viewIndex);
			}
		});
    }
	
	/**
     * 设置底部栏当前焦点
     * @param index
     */
    private void setCurPoint(int index)
    {
    	if (index < 0 || index > mViewCount - 1 || mCurSel == index)
    		return;
   	
    	mButtons[mCurSel].setChecked(false);
    	mButtons[index].setChecked(true);    	
    	mHeadTitle.setText(mHeadTitles[index]);    	
    	mCurSel = index;
    	
    	mHead_search.setVisibility(View.GONE);
    	//mHeadPub_huati.setVisibility(View.GONE);
    	//mHeadPub_tweet.setVisibility(View.GONE);
		//头部logo、发帖、发动弹按钮显示
    	if(index == 0){
    		mHeadLogo.setBackgroundResource(R.drawable.frame_logo_news_p);
    		mHead_search.setVisibility(View.VISIBLE);
    	}
    	else if(index == 1){
    		mHeadLogo.setBackgroundResource(R.drawable.frame_logo_news_p);
    		mHead_search.setVisibility(View.VISIBLE);
    	}
    	else if(index == 2){
    		mHeadLogo.setBackgroundResource(R.drawable.frame_logo_news_p);
    		mHead_search.setVisibility(View.VISIBLE);
    	}
    }
      
    /**
     * 初始化各个主页的按钮(资讯、问答、动弹、动态、留言)
     */
    private void initFrameButton()
    {
    	//初始化按钮控件
    	framebtn_News_recommend = (Button)findViewById(R.id.frame_btn_news_recommend);
    	framebtn_News_zatan = (Button)findViewById(R.id.frame_btn_news_zatan);
    	framebtn_News_lastest = (Button)findViewById(R.id.frame_btn_news_lastest);
    	framebtn_News_all = (Button)findViewById(R.id.frame_btn_news_all);
    	framebtn_Huati_history = (Button)findViewById(R.id.frame_btn_huati_history);
    	framebtn_Huati_think = (Button)findViewById(R.id.frame_btn_huati_think);
    	framebtn_Huati_politics = (Button)findViewById(R.id.frame_btn_huati_politics);
    	//framebtn_Question_job = (Button)findViewById(R.id.frame_btn_question_job);
    	//framebtn_Question_site = (Button)findViewById(R.id.frame_btn_question_site);
    	framebtn_Zhuanti_common = (Button)findViewById(R.id.frame_btn_zhuanti_common);
    	framebtn_Zhuanti_particular = (Button)findViewById(R.id.frame_btn_zhuanti_particular);
    	//framebtn_Tweet_my = (Button)findViewById(R.id.frame_btn_tweet_my);
    	//framebtn_Active_lastest = (Button)findViewById(R.id.frame_btn_active_lastest);
    	//framebtn_Active_atme = (Button)findViewById(R.id.frame_btn_active_atme);
    	//framebtn_Active_comment = (Button)findViewById(R.id.frame_btn_active_comment);
    	//framebtn_Active_myself = (Button)findViewById(R.id.frame_btn_active_myself);
    	//framebtn_Active_message = (Button)findViewById(R.id.frame_btn_active_message);
    	//设置首选择项
    	framebtn_News_recommend.setEnabled(false);
    	framebtn_Huati_history.setEnabled(false);
    	framebtn_Zhuanti_common.setEnabled(false);
    	//framebtn_Active_lastest.setEnabled(false);
    	//资讯+杂谈
    	framebtn_News_recommend.setOnClickListener(frameNewsBtnClick(framebtn_News_recommend,RecommendList.CATALOG_RECOMMEND));
    	framebtn_News_zatan.setOnClickListener(frameNewsBtnClick(framebtn_News_zatan,ZatanList.CATALOG_LATEST));
    	framebtn_News_lastest.setOnClickListener(frameNewsBtnClick(framebtn_News_lastest,NewsList.CATALOG_ALLN));
    	framebtn_News_all.setOnClickListener(frameNewsBtnClick(framebtn_News_all,AllList.CATALOG_ALL));
    	//话题
    	framebtn_Huati_history.setOnClickListener(frameHuatiBtnClick(framebtn_Huati_history,HuatiList.CATALOG_HISTORY));
    	framebtn_Huati_think.setOnClickListener(frameHuatiBtnClick(framebtn_Huati_think,HuatiList.CATALOG_THINK));
    	framebtn_Huati_politics.setOnClickListener(frameHuatiBtnClick(framebtn_Huati_politics,HuatiList.CATALOG_POLITICS));
    	//framebtn_Question_job.setOnClickListener(frameQuestionBtnClick(framebtn_Question_job,PostList.CATALOG_JOB));
    	//framebtn_Question_site.setOnClickListener(frameQuestionBtnClick(framebtn_Question_site,PostList.CATALOG_SITE));
    	//动弹
    	framebtn_Zhuanti_common.setOnClickListener(frameZhuantiBtnClick(framebtn_Zhuanti_common,ZhuantiList.CATALOG_COMMON));
    	framebtn_Zhuanti_particular.setOnClickListener(frameZhuantiBtnClick(framebtn_Zhuanti_particular,ZhuantiList.CATALOG_PARTICULAR));
    	//framebtn_Tweet_my.setOnClickListener(new View.OnClickListener() {
			//public void onClick(View v) {
				//判断登录
				//int uid = appContext.getLoginUid();
				//if(uid == 0){
					//UIHelper.showLoginDialog(Main.this);
					//return;
				//}		
				
	    		//framebtn_Tweet_lastest.setEnabled(true);
	    		//framebtn_Tweet_hot.setEnabled(true);
	    		//framebtn_Tweet_my.setEnabled(false);
				
				//curTweetCatalog = uid;
				//loadLvTweetData(curTweetCatalog, 0, lvTweetHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
			//}
		//});
    	//动态+留言
    	//framebtn_Active_lastest.setOnClickListener(frameActiveBtnClick(framebtn_Active_lastest,ActiveList.CATALOG_LASTEST));
    	//framebtn_Active_atme.setOnClickListener(frameActiveBtnClick(framebtn_Active_atme,ActiveList.CATALOG_ATME));
    	//framebtn_Active_comment.setOnClickListener(frameActiveBtnClick(framebtn_Active_comment,ActiveList.CATALOG_COMMENT));
    	//framebtn_Active_myself.setOnClickListener(frameActiveBtnClick(framebtn_Active_myself,ActiveList.CATALOG_MYSELF));
    	//framebtn_Active_message.setOnClickListener(frameActiveBtnClick(framebtn_Active_message,0));
    	//特殊处理
    	//framebtn_Active_atme.setText("@"+getString(R.string.frame_title_active_atme));
    }
    private View.OnClickListener frameNewsBtnClick(final Button btn,final int catalog){//catalog 此类别来自于上面点击哪个按钮
    	return new View.OnClickListener() {
			public void onClick(View v) {
				if(btn == framebtn_News_recommend){
		    		framebtn_News_recommend.setEnabled(false);
		    	}else{
		    		framebtn_News_recommend.setEnabled(true);
		    	}
		    	if(btn == framebtn_News_zatan){
		    		framebtn_News_zatan.setEnabled(false);
		    	}else{
		    		framebtn_News_zatan.setEnabled(true);
		    	}
		    	if(btn == framebtn_News_lastest){
		    		framebtn_News_lastest.setEnabled(false);
		    	}else{
		    		framebtn_News_lastest.setEnabled(true);
		    	}
		    	if(btn == framebtn_News_all){
		    		framebtn_News_all.setEnabled(false);
		    	}else{
		    		framebtn_News_all.setEnabled(true);
		    	}
		    	
		    	curNewsCatalog = catalog;                                                                 //在此切换类别
		    	
				//非新闻列表 原来是用来判断是不是点到资讯按钮了，要是没有点到资讯按钮，就可以根据catalog的类别去加载是推荐还是博客
		    	if(btn == framebtn_News_recommend)
		    	{
		    		lvRecommend.setVisibility(View.VISIBLE);
		    		lvNews.setVisibility(View.GONE);
		    		lvZatan.setVisibility(View.GONE);
		    		lvAll.setVisibility(View.GONE);
		    		
		    		lvRecommend_foot_more.setText(R.string.load_more);
					lvRecommend_foot_progress.setVisibility(View.GONE);
					
					loadLvRecommendData(curNewsCatalog, 0, lvRecommendHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
		    	}
		    	
		    	else if(btn == framebtn_News_lastest)
		    	{
		    		lvRecommend.setVisibility(View.GONE);
		    		lvNews.setVisibility(View.VISIBLE);
		    		lvZatan.setVisibility(View.GONE);
		    		lvAll.setVisibility(View.GONE);
		    		
		    		lvNews_foot_more.setText(R.string.load_more);
					lvNews_foot_progress.setVisibility(View.GONE);
					
					loadLvNewsData(curNewsCatalog, 0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
		    	}
		    	else if(btn == frambtn_News_Zatan)
		    	{
		    		lvRecommend.setVisibility(View.GONE);
		    		lvNews.setVisibility(View.GONE);
		    		lvZatan.setVisibility(View.VISIBLE);
		    		lvAll.setVisibility(View.GONE);
		    		
		    		if(lvZatanData.size() > 0){
		    			lvZatan_foot_more.setText(R.string.load_more);
		    			lvZatan_foot_progress.setVisibility(View.GONE);		    			
		    		}
		    		
	    			loadLvZatanData(curNewsCatalog, 0, lvZatanHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);//此常量是为了区别这次不是加载能更多和刷新
		    	}
		    	else
		    	{
		    		lvRecommend.setVisibility(View.GONE);
		    		lvNews.setVisibility(View.GONE);
		    		lvZatan.setVisibility(View.GONE);
		    		lvAll.setVisibility(View.VISIBLE);
		    		
		    		if(lvAllData.size() > 0){
		    			lvAll_foot_more.setText(R.string.load_more);
		    			lvAll_foot_progress.setVisibility(View.GONE);		    			
		    		}
		    		
	    			loadLvAllData(curNewsCatalog, 0, lvAllHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
		    	}
			}
		};
    }
    private View.OnClickListener frameHuatiBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == framebtn_Huati_history)
		    		framebtn_Huati_history.setEnabled(false);
		    	else
		    		framebtn_Huati_history.setEnabled(true);
		    	if(btn == framebtn_Huati_think)
		    		framebtn_Huati_think.setEnabled(false);
		    	else
		    		framebtn_Huati_think.setEnabled(true);
		    	if(btn == framebtn_Huati_politics)
		    		framebtn_Huati_politics.setEnabled(false);
		    	else
		    		framebtn_Huati_politics.setEnabled(true);
		    	
				lvHuati_foot_more.setText(R.string.load_more);
				lvHuati_foot_progress.setVisibility(View.GONE);
				
				curHuatiCatalog = catalog;
				loadLvHuatiData(curHuatiCatalog, 0, lvHuatiHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
			}
		};
    } 
    private View.OnClickListener frameZhuantiBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == framebtn_Zhuanti_common)
		    		framebtn_Zhuanti_common.setEnabled(false);
		    	else
		    		framebtn_Zhuanti_common.setEnabled(true);
		    	if(btn == framebtn_Zhuanti_particular)
		    		framebtn_Zhuanti_particular.setEnabled(false);
		    	else
		    		framebtn_Zhuanti_particular.setEnabled(true);
				lvZhuanti_foot_more.setText(R.string.load_more);
				lvZhuanti_foot_progress.setVisibility(View.GONE);
				
				curZhuantiCatalog = catalog;
				loadLvZhuantiData(curZhuantiCatalog, 0, lvZhuantiHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);		    	
			}
		};
    }
    
    /**
     * 获取listview的初始化Handler
     * @param lv
     * @param adapter
     * @return
     */
    private Handler getLvHandler(final PullToRefreshListView lv,final BaseAdapter adapter,final TextView more,final ProgressBar progress,final int pageSize){
    	return new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what >= 0){
					//listview数据处理
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
					
					if(msg.what < pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					}else if(msg.what == pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
						
						//特殊处理-热门动弹不能翻页
						//if(lv == lvTweet) {
							//TweetList tlist = (TweetList)msg.obj;
							//if(lvTweetData.size() == tlist.getTweetCount()){
								//lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
								//more.setText(R.string.load_full);
							//}
						//}
					}
					//发送通知广播
					if(notice != null){
						UIHelper.sendBroadCast(lv.getContext(), notice);
					}
					//是否清除通知信息
					//if(isClearNotice){
						//ClearNotice(curClearNoticeType);
						//isClearNotice = false;//重置
						//curClearNoticeType = 0;
					//}
				}
				else if(msg.what == -1){
					//有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(Main.this);
				}
				if(adapter.getCount()==0){
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				mHeadProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					lv.setSelection(0);
				}else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG){
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
    }
    
    /**
     * listview数据处理
     * @param what 数量
     * @param obj 数据
     * @param objtype 数据类型
     * @param actiontype 操作类型
     * @return notice 通知信息
     */
    private Notice handleLvData(int what,Object obj,int objtype,int actiontype){
    	Notice notice = null;
		switch (actiontype) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
				int newdata = 0;//新加载数据-只有刷新动作才会使用到
				switch (objtype) {
				    case UIHelper.LISTVIEW_DATATYPE_RECOMMEND:
					    RecommendList rlist = (RecommendList)obj;
					    notice = rlist.getNotice();
					    lvRecommendSumData = what;
					    if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
						    if(lvRecommendData.size() > 0){
							    for(Recommend recommend1 : rlist.getRecommendlist()){
								    boolean b = false;
								    for(Recommend recommend2 : lvRecommendData){
									    if(recommend1.getId() == recommend2.getId()){
										    b = true;
										    break;
									    }
								    }
								    if(!b) newdata++;
							    }
						    }else{
							    newdata = what;
						    }
					    }
					    lvRecommendData.clear();//先清除原有数据
					    lvRecommendData.addAll(rlist.getRecommendlist());
					    break;
					case UIHelper.LISTVIEW_DATATYPE_NEWS:
						NewsList nlist = (NewsList)obj;
						notice = nlist.getNotice();
						lvNewsSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvNewsData.size() > 0){
								for(News news1 : nlist.getNewslist()){
									boolean b = false;
									for(News news2 : lvNewsData){
										if(news1.getId() == news2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvNewsData.clear();//先清除原有数据
						lvNewsData.addAll(nlist.getNewslist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_ZATAN:
						ZatanList blist = (ZatanList)obj;
						notice = blist.getNotice();
						lvZatanSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvZatanData.size() > 0){
								for(Zatan zatan1 : blist.getZatanlist()){
									boolean b = false;
									for(Zatan zatan2 : lvZatanData){
										if(zatan1.getId() == zatan2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvZatanData.clear();//先清除原有数据
						lvZatanData.addAll(blist.getZatanlist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_ALL:
						AllList alist = (AllList)obj;
						notice = alist.getNotice();
						lvAllSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvAllData.size() > 0){
								for(All all1 : alist.getAlllist()){
									boolean b = false;
									for(All all2 : lvAllData){
										if(all1.getId() == all2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvAllData.clear();//先清除原有数据
						lvAllData.addAll(alist.getAlllist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_HUATI:
						HuatiList hlist = (HuatiList)obj;
						notice = hlist.getNotice();
						lvHuatiSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvHuatiData.size() > 0){
								for(Huati huati1 : hlist.getHuatilist()){
									boolean b = false;
									for(Huati huati2 : lvHuatiData){
										if(huati1.getId() == huati2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvHuatiData.clear();//先清除原有数据
						lvHuatiData.addAll(hlist.getHuatilist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_ZHUANTI:
						ZhuantiList zlist = (ZhuantiList)obj;
						notice = zlist.getNotice();
						lvZhuantiSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvZhuantiData.size() > 0){
								for(Zhuanti zhuanti1 : zlist.getZhuantilist()){
									boolean b = false;
									for(Zhuanti zhuanti2 : lvZhuantiData){
										if(zhuanti1.getId() == zhuanti2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvZhuantiData.clear();//先清除原有数据
						lvZhuantiData.addAll(zlist.getZhuantilist());
						break;
					/*case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
						ActiveList alist = (ActiveList)obj;
						notice = alist.getNotice();
						lvActiveSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvActiveData.size() > 0){
								for(Active active1 : alist.getActivelist()){
									boolean b = false;
									for(Active active2 : lvActiveData){
										if(active1.getId() == active2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvActiveData.clear();//先清除原有数据
						lvActiveData.addAll(alist.getActivelist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
						MessageList mlist = (MessageList)obj;
						notice = mlist.getNotice();
						lvMsgSumData = what;
						if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
							if(lvMsgData.size() > 0){
								for(Messages msg1 : mlist.getMessagelist()){
									boolean b = false;
									for(Messages msg2 : lvMsgData){
										if(msg1.getId() == msg2.getId()){
											b = true;
											break;
										}
									}
									if(!b) newdata++;
								}
							}else{
								newdata = what;
							}
						}
						lvMsgData.clear();//先清除原有数据
						lvMsgData.addAll(mlist.getMessagelist());
						break;*/
				}
				if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
					//提示新加载数据
					if(newdata >0){
						NewDataToast.makeText(this, getString(R.string.new_data_toast_message, newdata), appContext.isAppSound()).show();
					}else{
						NewDataToast.makeText(this, getString(R.string.new_data_toast_none), false).show();
					}
				}
				break;
			case UIHelper.LISTVIEW_ACTION_SCROLL:
				switch (objtype) {
				    case UIHelper.LISTVIEW_DATATYPE_RECOMMEND:
						RecommendList rlist = (RecommendList)obj;
						notice = rlist.getNotice();
						lvRecommendSumData += what;
						if(lvRecommendData.size() > 0){
							for(Recommend recommend1 : rlist.getRecommendlist()){
								boolean b = false;
								for(Recommend recommend2 : lvRecommendData){
									if(recommend1.getId() == recommend2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvRecommendData.add(recommend1);
							}
						}else{
							lvRecommendData.addAll(rlist.getRecommendlist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_NEWS:
						NewsList nlist = (NewsList)obj;
						notice = nlist.getNotice();
						lvNewsSumData += what;
						if(lvNewsData.size() > 0){
							for(News news1 : nlist.getNewslist()){
								boolean b = false;
								for(News news2 : lvNewsData){
									if(news1.getId() == news2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvNewsData.add(news1);
							}
						}else{
							lvNewsData.addAll(nlist.getNewslist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_ZATAN:
						ZatanList blist = (ZatanList)obj;
						notice = blist.getNotice();
						lvZatanSumData += what;
						if(lvZatanData.size() > 0){
							for(Zatan zatan1 : blist.getZatanlist()){
								boolean b = false;
								for(Zatan zatan2 : lvZatanData){
									if(zatan1.getId() == zatan2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvZatanData.add(zatan1);
							}
						}else{
							lvZatanData.addAll(blist.getZatanlist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_ALL:
						AllList alist = (AllList)obj;
						notice = alist.getNotice();
						lvAllSumData += what;
						if(lvAllData.size() > 0){
							for(All all1 : alist.getAlllist()){
								boolean b = false;
								for(All all2 : lvAllData){
									if(all1.getId() == all2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvAllData.add(all1);
							}
						}else{
							lvAllData.addAll(alist.getAlllist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_HUATI:
						HuatiList hlist = (HuatiList)obj;
						notice = hlist.getNotice();
						lvNewsSumData += what;
						if(lvNewsData.size() > 0){
							for(Huati huati1 : hlist.getHuatilist()){
								boolean b = false;
								for(Huati huati2 : lvHuatiData){
									if(huati1.getId() == huati2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvHuatiData.add(huati1);
							}
						}else{
							lvHuatiData.addAll(hlist.getHuatilist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_ZHUANTI:
						ZhuantiList zlist = (ZhuantiList)obj;
						notice = zlist.getNotice();
						lvZhuantiSumData += what;
						if(lvZhuantiData.size() > 0){
							for(Zhuanti zhuanti1 : zlist.getZhuantilist()){
								boolean b = false;
								for(Zhuanti zhuanti2 : lvZhuantiData){
									if(zhuanti1.getId() == zhuanti2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvZhuantiData.add(zhuanti1);
							}
						}else{
							lvZhuantiData.addAll(zlist.getZhuantilist());
						}
						break;
					/*case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
						ActiveList alist = (ActiveList)obj;
						notice = alist.getNotice();
						lvActiveSumData += what;
						if(lvActiveData.size() > 0){
							for(Active active1 : alist.getActivelist()){
								boolean b = false;
								for(Active active2 : lvActiveData){
									if(active1.getId() == active2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvActiveData.add(active1);
							}
						}else{
							lvActiveData.addAll(alist.getActivelist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
						MessageList mlist = (MessageList)obj;
						notice = mlist.getNotice();
						lvMsgSumData += what;
						if(lvMsgData.size() > 0){
							for(Messages msg1 : mlist.getMessagelist()){
								boolean b = false;
								for(Messages msg2 : lvMsgData){
									if(msg1.getId() == msg2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvMsgData.add(msg1);
							}
						}else{
							lvMsgData.addAll(mlist.getMessagelist());
						}
						break;*/
				}
				break;
		}
		return notice;
    }
    
    /**
     * 线程加载推荐数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvRecommendData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {					
					RecommendList list = appContext.getRecommendList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_RECOMMEND;
                if(curNewsCatalog == catalog)
                	handler.sendMessage(msg);
			}
		}.start();
	}
	
    /**
     * 线程加载新闻数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvNewsData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {					
					NewsList list = appContext.getNewsList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                if(curNewsCatalog == catalog)
                	handler.sendMessage(msg);
			}
		}.start();
	}
    
	/**
     * 线程加载杂谈数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvZatanData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				/*String type = "";
				switch (catalog) {
				case ZatanList.CATALOG_LATEST:
					type = ZatanList.TYPE_LATEST;
					break;
				case ZatanList.CATALOG_ALL:
					type = ZatanList.TYPE_ALL;                                        //此处要注意修改将原来的recommend改为all
					break;
				}*/
				try {
					ZatanList list = appContext.getZatanList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_ZATAN;
                if(curNewsCatalog == catalog)
                	handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
     * 线程加载新闻数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvAllData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {					
					AllList list = appContext.getAllList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_ALL;
                if(curNewsCatalog == catalog)
                	handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
     * 线程加载话题数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvHuatiData(final int catalog,final int pageIndex,final Handler handler,final int action){  
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					HuatiList list = appContext.getHuatiList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_HUATI;
				if(curHuatiCatalog == catalog)
					handler.sendMessage(msg);
			}
		}.start();
	}
	
	private void loadLvZhuantiData(final int catalog,final int pageIndex,final Handler handler,final int action){  
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					ZhuantiList list = appContext.getZhuantiList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_ZHUANTI;
				if(curZhuantiCatalog == catalog)
					handler.sendMessage(msg);
			}
		}.start();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
}
