package net.oschina.app.ui;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewArticleAdapter;
import net.oschina.app.adapter.ListViewTopicAdapter;
import net.oschina.app.adapter.ListViewSpecialAdapter;
import net.oschina.app.adapter.MyBaseAdapter;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.BaseList;
import net.oschina.app.bean.ArticleList;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.HeadImg;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.TopicList;
import net.oschina.app.bean.SpecialList;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;
import net.oschina.app.common.UIHelper;
import net.oschina.app.common.UpdateManager;
import net.oschina.app.widget.NewDataToast;
import net.oschina.app.widget.PullToRefreshListView;
import net.oschina.app.widget.ScrollLayout;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 应用程序首页
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Main extends BaseActivity {

	//public static final int QUICKACTION_LOGIN_OR_LOGOUT = 0;
	//public static final int QUICKACTION_USERINFO = 1;
	//public static final int QUICKACTION_SOFTWARE = 2;
	public static final int QUICKACTION_SEARCH = 0;
	public static final int QUICKACTION_SETTING = 1;
	public static final int QUICKACTION_EXIT = 2;
	
	private static final int LISTVIEW_NUM = 3;

	// -------------界面控件 --------------
	
	private ScrollLayout mScrollLayout;
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;
	
	private RadioButton fbArticle;
	private RadioButton fbTopic;
	private RadioButton fbSpecial;
	private ImageView fbSetting;
	
	private ImageView mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;
	private ImageButton mHead_search;
	//private ImageButton mHeadPub_topic;
	//private ImageButton mHeadPub_tweet;

	private PullToRefreshListView[] listView = new PullToRefreshListView[LISTVIEW_NUM];
	
	private int[] listViewR={
		R.id.frame_listview_article,
		R.id.frame_listview_topic,
		R.id.frame_listview_special
	};
	
	//ListView Header 置顶大图
	/*private View[] lvHeader = new View[TypeHelper.Article.CATALOG_COUNT];
	private int lvHeaderR[]={
		R.layout.listview_header_artical,
		R.layout.listview_header_artical,
		R.layout.listview_header_artical
	};
		
	private ImageView[] lvHead_imageview = new ImageView[TypeHelper.Article.CATALOG_COUNT];
	private int lvHeadImgR[]={
		R.id.listview_header_image,
		R.id.listview_header_image,
		R.id.listview_header_image
	};*/
	
	private View lvHeader;
	private int lvHeaderR = R.layout.listview_header_article;
	
	private ImageView lvHead_imageview;
	private int lvHeadImgR = R.id.listview_header_image;
	
	private TextView lvHead_textview;
	private int lvHeadTextR = R.id.listview_header_title;
	
	//ListView Footer 底部更新提示
	private View[] lvFooter = new View[LISTVIEW_NUM]; 
	private int[] lvFooterR ={
		R.layout.listview_footer,
		R.layout.listview_footer,
		R.layout.listview_footer
	};
	
	private TextView[] lvFoot_more = new TextView[LISTVIEW_NUM];
	private int[] lvFootMoreR ={
		R.id.listview_foot_more,
		R.id.listview_foot_more,
		R.id.listview_foot_more
	};
	
	private ProgressBar[] lvFoot_progress = new ProgressBar[LISTVIEW_NUM];
	private int[] lvFootProgressR ={
		R.id.listview_foot_progress,
		R.id.listview_foot_progress,
		R.id.listview_foot_progress
	};
	
	//初始化Adaper参数
	private int[] lvItemR ={
		R.layout.article_listitem,
		R.layout.topic_listitem,
		R.layout.special_listitem
	};
	
	//getTag()
	private int[] lvItemTitleR ={
		R.id.article_listitem_title,
		R.id.topic_listitem_title,
		R.id.special_listitem_title
	};
	
	private ArrayList<ArrayList<Button>> frameCatalogBtn;

	private int[][] frameCatalogBtnR ={
		{R.id.frame_btn_article_recommend,	R.id.frame_btn_article_lastest,	R.id.frame_btn_article_zatan,	R.id.frame_btn_article_all},
		{R.id.frame_btn_topic_sixiang,		R.id.frame_btn_topic_zhengjing,	R.id.frame_btn_topic_wenshi},
		{R.id.frame_btn_special_latest,		R.id.frame_btn_special_important}
	};

	//public static BadgeView bv_active;
	//public static BadgeView bv_message;
	//public static BadgeView bv_atme;
	//public static BadgeView bv_review;
	
	private QuickActionWidget mGrid;// 快捷栏控件
	
	
	//----------------全局数据---------------
	
	//数组下标索引值与类型值的对应关系
	private int[] index2Type = { 
		TypeHelper.ARTICLE,
		TypeHelper.TOPIC,
		TypeHelper.SPECIAL
		//UIHelper.LISTVIEW_DATATYPE_COMMENT,
		//UIHelper.LISTVIEW_DATATYPE_FAVORITE,
		//UIHelper.LISTVIEW_DATATYPE_SEARCH,
	};
	private HashMap<Integer, Integer> type2Index; 

	private int curCatalog[] = { 
		TypeHelper.Article.CATALOG_NEWS,
		TypeHelper.Topic.CATALOG_WENSHI,
		TypeHelper.Special.CATALOG_LATEST
		//TweetList.CATALOG_LASTEST
		//ActiveList.CATALOG_LASTEST
	};

	private MyBaseAdapter[] lvAdapter = new MyBaseAdapter[LISTVIEW_NUM];
	
	private ArrayList<ArrayList<BaseItem>> lvData;

	private int[] lvSumData = new int[LISTVIEW_NUM]; 
	
	private int[] lvPageSize = {20, 10, 5}; 
	
	private Handler[] lvHandler = new Handler[LISTVIEW_NUM];
	

	private boolean isClearNotice = false;
	private int curClearNoticeType = 0;
	
	//private TweetReceiver tweetReceiver;// 动弹发布接收器
	private AppContext appContext;// 全局Context
	private BitmapManager bmpManager;
	
	//自定义数据
	/*private String newsBigImg;
	private String recommendBigmap;
	private String zatanBigmap;
	private String newsImgLink;
	private String recommenddetail;
	private String zatandetail;*/
	
	//private BitmapManager bitmapmanager;
	//private int window_width;
	
	//---------------Activity生命周期---------------
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 注册广播接收器
//		tweetReceiver = new TweetReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("net.oschina.app.action.APP_TWEETPUB");
//		registerReceiver(tweetReceiver, filter);

		appContext = (AppContext) getApplication();
		bmpManager = new BitmapManager(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.widget_dimg_loading));
		
		// 网络连接判断
		if (!appContext.isNetworkConnected())
			UIHelper.ToastMessage(this, R.string.network_not_connected);
		// 初始化登录
		//appContext.initLoginInfo();
		
		this.initListViewData();
		this.initHeadView();
		this.initFootBar();
		this.initPageScroll();
		this.initFrameButton();
		//this.initBadgeView();
		this.initQuickActionGrid();
		this.initFrameListView();

		// 检查新版本
		if (appContext.isCheckUp()) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		}

		// 启动轮询通知信息
		//this.foreachUserNotice();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mViewCount == 0)
			mViewCount = 4;
		if (mCurSel == 0 && !fbArticle.isChecked()) {
			fbArticle.setChecked(true);
			fbTopic.setChecked(false);
			fbSpecial.setChecked(false);
			//fbTweet.setChecked(false);
			//fbactive.setChecked(false);
		}
		// 读取左右滑动配置
		mScrollLayout.setIsScroll(appContext.isScroll());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//unregisterReceiver(tweetReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (intent.getBooleanExtra("LOGIN", false)) {
			// 加载动弹、动态及留言(当前动弹的catalog大于0表示用户的uid)
			/*if (lvTweetData.isEmpty() && curTweetCatalog > 0 && mCurSel == 2) {
				this.loadLvTweetData(curTweetCatalog, 0, lvTweetHandler,UIHelper.LISTVIEW_ACTION_INIT);
			} else if (mCurSel == 3) {
				if (lvActiveData.isEmpty()) {
					this.loadLvActiveData(curActiveCatalog, 0, lvActiveHandler,UIHelper.LISTVIEW_ACTION_INIT);
				}
				if (lvMsgData.isEmpty()) {
					this.loadLvMsgData(0, lvMsgHandler,UIHelper.LISTVIEW_ACTION_INIT);
				}
			}*/
		} else if (intent.getBooleanExtra("NOTICE", false)) {
			// 查看最新信息
			mScrollLayout.scrollToScreen(3);
		}
	}

	//public class TweetReceiver extends BroadcastReceiver {}

	/**
	 * 初始化快捷栏
	 */
	private void initQuickActionGrid() {
		mGrid = new QuickActionGrid(this);
		/*mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_login,
				R.string.main_menu_login));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_myinfo,
				R.string.main_menu_myinfo));
		mGrid.addQuickAction(new MyQuickAction(this,
				R.drawable.ic_menu_software, R.string.main_menu_software));*/
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_search,
				R.string.main_menu_search));
		mGrid.addQuickAction(new MyQuickAction(this,
				R.drawable.ic_menu_setting, R.string.main_menu_setting));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_exit,
				R.string.main_menu_exit));

		mGrid.setOnQuickActionClickListener(mActionListener);
	}

	/**
	 * 快捷栏item点击事件
	 */
	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			switch (position) {
			/*case QUICKACTION_LOGIN_OR_LOGOUT:// 用户登录-注销
				UIHelper.loginOrLogout(Main.this);
				break;
			case QUICKACTION_USERINFO:// 我的资料
				UIHelper.showUserInfo(Main.this);
				break;
			case QUICKACTION_SOFTWARE:// 开源软件
				UIHelper.showSoftware(Main.this);
				break;*/
			case QUICKACTION_SEARCH:// 搜索
				UIHelper.showSearch(Main.this);
				break;
			case QUICKACTION_SETTING:// 设置
				UIHelper.showSetting(Main.this);
				break;
			case QUICKACTION_EXIT:// 退出
				UIHelper.Exit(Main.this);
				break;
			}
		}
	};
	

	/**
	 * 初始化所有ArrayList
	 */
	private void initListViewData() {
		
		frameCatalogBtn = new ArrayList<ArrayList<Button>>(){{
			add(new ArrayList<Button>());
			add(new ArrayList<Button>());
			add(new ArrayList<Button>());
		}};
		
		type2Index = new HashMap<Integer, Integer>(){{ 
			put(TypeHelper.ARTICLE, 0);
			put(TypeHelper.TOPIC, 1);
			put(TypeHelper.SPECIAL, 2);
		}};
		
		lvData = new ArrayList<ArrayList<BaseItem>>(){{
			/*for (int i = 0; i < LISTVIEW_NUM; i++) {
				add(new ArrayList<BaseItem>());
			}*/
			add(new ArrayList<BaseItem>());
			add(new ArrayList<BaseItem>());
 			add(new ArrayList<BaseItem>());
		}};
		
    }

	/**
	 * 初始化所有ListView
	 */
	private void initFrameListView() {
		// 初始化listview控件
		for (int i = 0; i < LISTVIEW_NUM; i++) {
			this.initListView(index2Type[i]);
		}
		
		//加载listview数据
		this.initFrameListViewData();
    }
    
    /**
     * 初始化所有ListView数据
     */
    private void initFrameListViewData()
    {
        //初始化Handler
    	for (int i = 0; i < LISTVIEW_NUM; i++) {
    		lvHandler[i] = this.getLvHandler(index2Type[i]);
		}
    	
    	int articleIndex = type2Index.get(TypeHelper.ARTICLE);
        //加载文章数据（因为文章是默认第一屏）
		if(lvData.get(articleIndex).isEmpty()) {
			loadLvDataThread(curCatalog[articleIndex], 0, 
					lvHandler[articleIndex], 
					UIHelper.LISTVIEW_ACTION_INIT, 
					TypeHelper.ARTICLE);
		}
	}

	/**
	 * 初始化列表界面控件+事件响应
	 */
	private void initListView(final int objType) {
		final int index = type2Index.get(objType);
		switch (objType) {
		case TypeHelper.ARTICLE:
			lvAdapter[index] = new ListViewArticleAdapter(this, lvData.get(index), lvItemR[index]);
			break;
		case TypeHelper.TOPIC:
			lvAdapter[index] = new ListViewTopicAdapter(this, lvData.get(index), lvItemR[index]);
			break;
		case TypeHelper.SPECIAL:
			lvAdapter[index] = new ListViewSpecialAdapter(this, lvData.get(index), lvItemR[index]);
			break;
		default:
			break;
		}
		
		//如果是文章列表，则在lv顶部添加大图layout
		if(objType == TypeHelper.ARTICLE){
				lvHeader = getLayoutInflater().inflate(lvHeaderR, null);
				lvHead_imageview = (ImageView) lvHeader.findViewById(lvHeadImgR);
				lvHead_textview = (TextView) lvHeader.findViewById(lvHeadTextR);
		}
		
		lvFooter[index] = getLayoutInflater()
				.inflate(lvFooterR[index], null);
		lvFoot_more[index] = (TextView) lvFooter[index]
				.findViewById(lvFootMoreR[index]);
		lvFoot_progress[index] = (ProgressBar) lvFooter[index]
				.findViewById(lvFootProgressR[index]);
		listView[index] = (PullToRefreshListView) findViewById(listViewR[index]);
		if(objType == TypeHelper.ARTICLE)
			listView[index].addHeaderView(lvHeader);
		listView[index].addFooterView(lvFooter[index]);// 添加底部视图 必须在setAdapter前
		//给View添加Adapter，两者联系的关键！系统绘制View时调用相应Adapter的getView
		listView[index].setAdapter(lvAdapter[index]);	
		//OnItemClick
		listView[index].setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lvFooter[index])
					return;

				BaseItem base = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					base = (BaseItem) view.getTag();
				} else { //否则从title中取出TAG---item对象
					TextView tv = (TextView) view.findViewById(lvItemTitleR[index]);
					base = (BaseItem) tv.getTag();
				}
				if (base == null)
					return;
				
				// 跳转到详情Activity
				UIHelper.showDetail(view.getContext(), base.getId(), objType);
			}
		});
		//OnScroll
		listView[index].setOnScrollListener(new AbsListView.OnScrollListener() {
			//onScrollStateChanged
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				listView[index].onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvData.get(index).isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvFooter[index]) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(listView[index].getTag());
				//如果滑到底而且还有更多数据可读（就是列表填满PAGE_SIZE个）
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					listView[index].setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvFoot_more[index].setText(R.string.load_ing);
					lvFoot_progress[index].setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvSumData[index] / lvPageSize[index];
					//按照pageIndex新数据
					loadLvDataThread(curCatalog[index], pageIndex, lvHandler[index],
							UIHelper.LISTVIEW_ACTION_SCROLL, objType);
				}
			}
			//onScroll
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				listView[index].onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		//OnRefresh
		listView[index].setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvDataThread(curCatalog[index], 0, lvHandler[index],
						UIHelper.LISTVIEW_ACTION_REFRESH, objType);
			}
		});
	}
    
	/**
	 * 初始化动弹列表
	 */
	//private void initTweetListView()
	
	/**
	 * 初始化动态列表
	 */
	//private void initActiveListView() 
		

	/**
	 * 初始化留言列表
	 */
	//private void initMsgListView()

	/**
	 * 初始化头部视图
	 */
	private void initHeadView() {
		mHeadLogo = (ImageView) findViewById(R.id.main_head_logo);
		mHeadTitle = (TextView) findViewById(R.id.main_head_title);
		mHeadProgress = (ProgressBar) findViewById(R.id.main_head_progress);
		mHead_search = (ImageButton) findViewById(R.id.main_head_search);

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
	private void initFootBar() {
		fbArticle = (RadioButton) findViewById(R.id.main_footbar_news);
		fbTopic = (RadioButton) findViewById(R.id.main_footbar_topic);
		fbSpecial = (RadioButton)findViewById(R.id.main_footbar_special);
		//fbTweet = (RadioButton) findViewById(R.id.main_footbar_tweet);
		//fbactive = (RadioButton) findViewById(R.id.main_footbar_active);

		fbSetting = (ImageView) findViewById(R.id.main_footbar_setting);
		fbSetting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 展示快捷栏&判断是否登录&是否加载文章图片
				//UIHelper.showSettingLoginOrLogout(Main.this, mGrid.getQuickAction(0));
				mGrid.show(v);
			}
		});
	}

	/**
	 * 初始化通知信息标签控件
	 */
	/*
	//private void initBadgeView()


	/**
	 * 初始化水平滚动翻页
	 */
	private void initPageScroll() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout_footer);
		mHeadTitles = getResources().getStringArray(R.array.head_titles);
		mViewCount = mScrollLayout.getChildCount();
		mButtons = new RadioButton[mViewCount];

		for (int i = 0; i < mViewCount; i++) {
			mButtons[i] = (RadioButton) linearLayout.getChildAt(i * 2);
			mButtons[i].setTag(i);
			mButtons[i].setChecked(false);
			mButtons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					int pos = (Integer) (v.getTag());
					// 点击当前项刷新
					if (mCurSel == pos) { //0-文章 1-话题 2-专题
						listView[pos].clickRefresh();
							/*case 4:// 动态+留言
							if (lvActive.getVisibility() == View.VISIBLE)
								lvActive.clickRefresh();
							else
								lvMsg.clickRefresh();
							break;*/
					}
					setCurPoint(pos);
					mScrollLayout.snapToScreen(pos);
				}
			});
		}

		// 设置第一显示屏
		mCurSel = 0;
		mButtons[mCurSel].setChecked(true);

		mScrollLayout
				.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
					public void OnViewChange(int viewIndex) { //0-文章 1-话题 2-专题
						// 切换列表视图-如果列表数据为空：加载数据
						//TODO:目前策略：非空就不刷新。可以换一种策略，加入时间因素
						if (lvData.get(viewIndex).isEmpty()) {
							loadLvDataThread(curCatalog[viewIndex], 0, 
									lvHandler[viewIndex], 
									UIHelper.LISTVIEW_ACTION_INIT, index2Type[viewIndex]); 
						}
						/*case 3:// 动态
							// 判断登录
						
							// 处理通知信息
						*/
						setCurPoint(viewIndex);
					}
				});
	}

	/**
	 * 设置底部栏当前焦点
	 * 
	 * @param index
	 */
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index)
			return;

		mButtons[mCurSel].setChecked(false);
		mButtons[index].setChecked(true);
		mHeadTitle.setText(mHeadTitles[index]);
		mCurSel = index;

		mHead_search.setVisibility(View.GONE);
		//mHeadPub_topic.setVisibility(View.GONE);
		//mHeadPub_tweet.setVisibility(View.GONE);
		// 头部logo、发帖、发动弹按钮显示
		if (index == 0) {
			mHeadLogo.setImageResource(R.drawable.frame_logo_news);
			mHead_search.setVisibility(View.VISIBLE);
		} else if (index == 1) {
			mHeadLogo.setImageResource(R.drawable.frame_logo_topic);
			mHead_search.setVisibility(View.VISIBLE);
		} else if (index == 2) {
			mHeadLogo.setImageResource(R.drawable.frame_logo_tweet);
			//mHeadPub_tweet.setVisibility(View.VISIBLE);
		} else if (index == 3) {
			mHeadLogo.setImageResource(R.drawable.frame_logo_active);
			//mHeadPub_tweet.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化各个主页的按钮(资讯、问答、动弹、动态、留言)
	 */
	private void initFrameButton() {
		//初始化按钮控件
    	for (int i = 0; i < frameCatalogBtnR.length; i++) {
			for (int j = 0; j < frameCatalogBtnR[i].length; j++) {
				frameCatalogBtn.get(i).add(j, (Button)findViewById(frameCatalogBtnR[i][j]));
			}
		}

    	//设置首选择项	默认数组首个按钮为默认按钮
    	for (int i = 0; i < frameCatalogBtn.size(); i++) {
    		frameCatalogBtn.get(i).get(0).setEnabled(false);
		}
    	
    	//绑定OnClickListener
    	for (int i = 0; i < frameCatalogBtn.size(); i++) {
			for (int j = 0; j < frameCatalogBtn.get(i).size(); j++) {
				frameCatalogBtn.get(i).get(j).setOnClickListener(frameBtnClick(i,j));
			}
		}

		// 动弹

		// 动态+留言

		// 特殊处理
	}
	
	private View.OnClickListener frameBtnClick(final int typeIndex,final int catalog){//catalog 此类别来自于屏幕上面点击哪个按钮
		return new View.OnClickListener() {
			public void onClick(View v) {
				for (int k = 0; k < frameCatalogBtn.get(typeIndex).size(); k++) {
					if(k == catalog)
						frameCatalogBtn.get(typeIndex).get(k).setEnabled(false);
					else 
						frameCatalogBtn.get(typeIndex).get(k).setEnabled(true);
				}
				if(index2Type[typeIndex] == TypeHelper.ARTICLE){
					if (catalog+1 == TypeHelper.Article.CATALOG_ALL) 
						lvHeader.setVisibility(View.GONE);	
					else
						lvHeader.setVisibility(View.VISIBLE);
				}
		    	curCatalog[typeIndex] = catalog+1; //从0开始到从1开始的转换
		    	loadLvDataThread(curCatalog[typeIndex], 0, lvHandler[typeIndex],
						UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG,index2Type[typeIndex]);
			}
		};
	}	


/*	private View.OnClickListener frameActiveBtnClick(final Button btn,
			final int catalog) {


	/**
	 * 获取listview的初始化Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @return
	 */
	private Handler getLvHandler(final int objType) {
		return new Handler() {
			public void handleMessage(Message msg) {
				int index = type2Index.get(objType);//Type转下标
				//int pageSize = ((BaseList)msg.obj).getMaxPageSize();
				if (msg.what >= 0) {
					// lv数据处理：将Msg传来的数据对比后本地化到lvData
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2,
							msg.arg1);
					
					// 更新lv界面层
					if (msg.what < lvPageSize[index]) {
						listView[index].setTag(UIHelper.LISTVIEW_DATA_FULL);
						//下面这句是Handler调用Adapter改变View显示的关键！
						lvAdapter[index].notifyDataSetChanged();
						lvFoot_more[index].setText(R.string.load_full);
					} else if (msg.what == lvPageSize[index]) {
						listView[index].setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvAdapter[index].notifyDataSetChanged();
						lvFoot_more[index].setText(R.string.load_more);

						// 特殊处理-热门动弹不能翻页
					}				
					// 更新文章lv的大图与标题
					if(objType == TypeHelper.ARTICLE 
							&& ((BaseList)msg.obj).getCatalog() != TypeHelper.Article.CATALOG_ALL){
						HeadImg himg = ((ArticleList)msg.obj).getHeadImg();
						if(himg != null){
							String imgURL = himg.getImg();
							String title = himg.getTitle();
							final int articleId = himg.getArticleId();
							
							boolean isLoadImage;
							if (AppContext.NETTYPE_WIFI == appContext.getNetworkType()) {
								isLoadImage = true;
							} else {
								isLoadImage = appContext.isLoadImage();
							}
							
							//加载标题
							lvHead_textview.setText(title);
							
							if(isLoadImage){
								//加载图片
								if(imgURL.endsWith("default.gif") || StringUtils.isEmpty(imgURL)){
									lvHead_imageview.setImageResource(R.drawable.widget_dimg);
								}else{
									bmpManager.loadBitmap(imgURL, lvHead_imageview);
								}
								
								
								//点击图片和标题跳转到详情
								lvHeader.setOnClickListener(new View.OnClickListener(){
									public void onClick(View v) {
										UIHelper.showArticleDetail(v.getContext(), articleId);
									}
								});
							}
						}
					}
					
					// 发送通知广播
					if (notice != null) {
						UIHelper.sendBroadCast(listView[index].getContext(), notice);
					}
					// 是否清除通知信息
					if (isClearNotice) {
						isClearNotice = false;// 重置
						curClearNoticeType = 0;
					}
				} else if (msg.what < 0) {
					if(msg.what == -1){
						// 有异常--显示加载出错 & 弹出错误消息
						listView[index].setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvFoot_more[index].setText(R.string.load_error);
						((AppException) msg.obj).makeToast(Main.this);
					}
					//TODO:以下是自定义异常,未作处理
					else if (msg.what == -2) { 
						listView[index].setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvFoot_more[index].setText(R.string.load_error);
					}
				}
				if (lvAdapter[index].getCount() == 0) {
					listView[index].setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvFoot_more[index].setText(R.string.load_empty);
				}
				lvFoot_progress[index].setVisibility(ProgressBar.GONE);
				mHeadProgress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					listView[index].onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					listView[index].setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					listView[index].onRefreshComplete();
					listView[index].setSelection(0);
				}
			}
		};
	}

    /**
     * listview数据处理Handler
     * 功能：将Msg传来的数据对比后本地化到lvData
     * @param what 数量
     * @param obj 数据
     * @param objtype 数据类型  1--Article 2--Topic 3--Special 4--ACTIVE 5--Message
     * @param actiontype 操作类型
     * @return notice 通知信息
     */
    private Notice handleLvData(int what,Object obj,int objtype,int actiontype){
    	Notice notice = null;
    	BaseList list = null;
    	int index = type2Index.get(objtype);//Type转下标
		switch (actiontype) {
			case UIHelper.LISTVIEW_ACTION_INIT:			//初始化
			case UIHelper.LISTVIEW_ACTION_REFRESH:		//刷新
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:	//切换栏目
				int newdata = 0;//新加载数据-只有刷新动作才会使用到
				list = (BaseList)obj;
				notice = list.getNotice();
				lvSumData[index] = what;
				if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
					if(lvData.size() > 0){
						//对于每一个旧数据中的项，如果在新数据中找不到相同id的，则newdata+1
						for(BaseItem item1 : list.getList()){
							boolean b = false;
							for(BaseItem item2 : lvData.get(index)){
								if(item1.getId() == item2.getId()){
									b = true;
									break;
								}
							}
							if(!b) newdata++;
						}
					}else{
						//若旧数据为空，则newdata为新数据个数
						newdata = what;
					}
				}
				lvData.get(index).clear();//先清除原有数据
				lvData.get(index).addAll(list.getList());
				
				if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
					//提示新加载数据
					if(newdata >0){
						NewDataToast.makeText(this, getString(R.string.new_data_toast_message, newdata), appContext.isAppSound()).show();
					}else{
						NewDataToast.makeText(this, getString(R.string.new_data_toast_none), false).show();
					}
				}
				break;
			case UIHelper.LISTVIEW_ACTION_SCROLL:	//列表下滚到底
				list = (BaseList)obj;
				notice = list.getNotice();
				lvSumData[index] += what;
				if(lvData.get(index).size() > 0){
					for(BaseItem art1 : list.getList()){
						boolean b = false;
						for(BaseItem art2 : lvData.get(index)){
							if(art1.getId() == art2.getId()){
								b = true;
								break;
							}
						}
						if(!b) lvData.get(index).add(art1);
					}
				}else{
					lvData.get(index).addAll(list.getList());
				}
				break;
		}
		return notice;
    }
	
	
    /**
     * 线程加载列表数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvDataThread(	final int catalog, 		final int pageIndex, 
									final Handler handler, 	final int action, 
									final int objType){
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {					
					BaseList list;
					switch (objType) {
					case TypeHelper.ARTICLE:
						list = new ArticleList();
						break;
					case TypeHelper.TOPIC:
						list = new TopicList();
						break;
					case TypeHelper.SPECIAL:
						list = new SpecialList();
						break;
					default:
						return;
					}
					//TODO:这里的HashMap细节可隐藏在具体类中，亦可switch(objType)
					Map<String, Object> httpGetPara = new HashMap<String, Object>(){{
						put("catalog", catalog);
						put("pageIndex", pageIndex);
						put("pageSize", lvPageSize[type2Index.get(objType)]);
					}};
					//请求网络或缓存数据
					list = appContext.getList(list, httpGetPara, isRefresh);
					
					//加载文章列表大图(全部栏目不需要)
					if(objType == TypeHelper.ARTICLE && catalog != TypeHelper.Article.CATALOG_ALL){
						HeadImg himg = new HeadImg();
						ApiClient.httpGet(himg, appContext, httpGetPara);
						((ArticleList)list).setHeadImg(himg);
					}
						
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = objType;
				
				//如果当前的Catalog和请求时发来的Catalog不符，则不发送数据
                if(curCatalog[type2Index.get(objType)] == catalog) 
                	handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 线程加载动弹数据（同通用接口）
	 * 
	 * @param catalog	-1 热门，0 最新，大于0 某用户的动弹(uid)
	 * @param pageIndex 当前页数
	 * @param handler	处理器
	 * @param action	动作标识
	 */

	/**
	 * 线程加载动态数据（同通用接口）
	 * 
	 * @param catalog
	 * @param pageIndex	当前页数
	 * @param handler
	 * @param action
	 */

	/**
	 * 线程加载留言数据
	 * @param 注意：无catalog参数
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 * @param action
	 */

	/**
	 * 轮询通知信息
	 */
	/*private void foreachUserNotice() {
		final int uid = appContext.getLoginUid();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					UIHelper.sendBroadCast(Main.this, (Notice) msg.obj);
				}
				foreachUserNotice();// 回调
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					sleep(60 * 1000);
					if (uid > 0) {
						Notice notice = appContext.getUserNotice(uid);
						msg.what = 1;
						msg.obj = notice;
					} else {
						msg.what = 0;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}*/

	/**
	 * 通知信息处理
	 * 
	 * @param type
	 *            1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 */
	/*private void ClearNotice(final int type) {
		final int uid = appContext.getLoginUid();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					Result res = (Result) msg.obj;
					if (res.OK() && res.getNotice() != null) {
						UIHelper.sendBroadCast(Main.this, res.getNotice());
					}
				} else {
					((AppException) msg.obj).makeToast(Main.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Result res = appContext.noticeClear(uid, type);
					msg.what = 1;
					msg.obj = res;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}*/

	/**
	 * 创建menu TODO 停用原生菜单
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.main_menu, menu);
		// return true;
	}

	/**
	 * 菜单被显示之前的事件
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		UIHelper.showMenuLoginOrLogout(this, menu);
		return true;
	}

	/**
	 * 处理menu的事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id) {
		case R.id.main_menu_user:
			UIHelper.loginOrLogout(this);
			break;
		case R.id.main_menu_about:
			UIHelper.showAbout(this);
			break;
		case R.id.main_menu_setting:
			UIHelper.showSetting(this);
			break;
		case R.id.main_menu_exit:
			UIHelper.Exit(this);
			break;
		}
		return true;
	}

	/**
	 * 监听返回--是否退出程序
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 是否退出应用
			UIHelper.Exit(this);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 展示快捷栏&判断是否登录
			UIHelper.showSettingLoginOrLogout(Main.this,
					mGrid.getQuickAction(0));
			mGrid.show(fbSetting, true);
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			// 展示搜索页
			UIHelper.showSearch(Main.this);
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}
	
	//load ImageView
	/*public void initChildView(Context context, ImageView imageView, String image_id) {
		//ArrayList<View> views = new ArrayList<View>();
		Bitmap bitmap = null;
//		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
		bitmapmanager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
		//for (int i = 0; i < image_id.length; i++) {
			//ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_XY);
			if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(image_id)){
		        imageView.setImageResource(R.drawable.widget_dface);
			}else{
				bitmapmanager.loadBitmap(image_id, imageView, null, window_width, 1);
			}
			//Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					//image_id[i]);
			//Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					//bmpManager);
			//Bitmap bitmap2 = getBitmap(imageView, window_width);
			//int frameheight = bitmap.getHeight();// 获取要显示的高度
			//imageView.setImageBitmap(bitmap);
			//flipper.addView(imageView, layoutParams);
			//views.add(imageView);
		//}
		//initPoint(image_id);
	}*/
}
