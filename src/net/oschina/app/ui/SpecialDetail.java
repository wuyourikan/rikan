package net.oschina.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewArticleAdapter;
import net.oschina.app.bean.Article;
import net.oschina.app.bean.ArticleList;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.BaseList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Special;
import net.oschina.app.bean.Topic;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.NewDataToast;
import net.oschina.app.widget.PullToRefreshListView;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SpecialDetail extends BaseActivity{
	
	// -------------界面控件 --------------
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int DATA_LOAD_FAIL = 0x003;
	
	//顶部控件
	private FrameLayout mHeader;
	private ImageView mBack;
	private ImageView mRefresh;
	private ProgressBar mProgressbar;
	//private ImageView mFavorite;
	//private ImageView mShare;
		
	//列表头
	private View saLvHeader;
	private int saLvHeaderR = R.layout.listview_header_special;
	
	private TextView saLvHead_title;
	private int saLvHeadTitleR = R.id.listview_header_title;
	
	private ImageView saLvHead_img;
	private int saLvHeadImgR = R.id.listview_header_image;
	
	private TextView saLvHead_intro;
	private int saLvHeadIntroR = R.id.listview_header_intro;
	
	//列表控件(专题)
	private PullToRefreshListView saListView;
	private int saListViewR = R.id.special_article_listview;

	private View saLvFooter;
	private int saLvFooterR = R.layout.listview_footer;
	
	private TextView saLvFoot_more;
	private int saLvFootMoreR = R.id.listview_foot_more;

	private ProgressBar saLvFoot_progress;
	private int saLvFootProgressR = R.id.listview_foot_progress;

	//初始化Adaper参数
	private int saLvItemR = R.layout.article_listitem;
			
	//getTag()
	private int saLvItemTitleR = R.id.article_listitem_title;

	//--------------全局数据---------------
	private AppContext appContext;
	private BitmapManager bmpManager;
	//专题数据
	private ListViewArticleAdapter saLvAdapter ;
	private Handler saLvHandler;
	private Handler saItemHandler;
	private List<BaseItem> saLvData = new ArrayList<BaseItem>();
	private int saLvSumData = 0;
	
	private int specialId = 0;
	private Special specialDetail;
	
	private int pageSize = AppContext.PAGE_SIZE;
	
	private boolean isClearNotice = false;
	private int curClearNoticeType = 0;
	
	//--------------Activity生命周期---------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.special_detail);
		appContext = (AppContext) getApplication();
		bmpManager = new BitmapManager(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.widget_dimg_loading));
		
		specialId = getIntent().getIntExtra("special_id", 0);
		
		this.initTopView();
		this.initFrameListView();
		//this.initFootBar();	
	}
	
	private void initTopView(){
		
		//顶部控件组
		mHeader = (FrameLayout)findViewById(R.id.special_detail_header);
		mBack 	= (ImageView) findViewById(R.id.special_detail_back);
		mRefresh = (ImageView)findViewById(R.id.special_detail_refresh);
		mProgressbar = (ProgressBar)findViewById(R.id.special_detail_head_progress);
		//mCommentList = (ImageView) findViewById(R.id.article_detail_footbar_commentlist);
		//mShare 	= (ImageView) findViewById(R.id.article_detail_footbar_share);
		//mFavorite = (ImageView) findViewById(R.id.article_detail_footbar_favorite);

		 //设置Button响应事件
		mBack.setOnClickListener(homeClickListener);
		//mFavorite.setOnClickListener(favoriteClickListener);
		mRefresh.setOnClickListener(refreshClickListener);
		//mAuthor.setOnClickListener(authorClickListener);
		//mShare.setOnClickListener(shareClickListener);

	}
	/**
     * 头部按钮展示
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
		case DATA_LOAD_ING:
			mProgressbar.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_FAIL:
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		}
    }
	//点击返回
	private View.OnClickListener homeClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showHome(SpecialDetail.this);
		}
	};
	//点击分享
	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (specialDetail == null) {
				UIHelper.ToastMessage(v.getContext(),
						R.string.msg_read_detail_fail);
				return;
			}
			// 分享到
			UIHelper.showShareDialog(SpecialDetail.this, specialDetail.getTitle(),
					specialDetail.getUrl());
		}
	};
	//点击收藏
	/*private View.OnClickListener favoriteClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (specialId == 0 || specialDetail == null) {
				return;
			}

			if (!ac.isLogin()) {
				UIHelper.showLoginDialog(SpecialDetail.this);
				return;
			}
			final int uid = ac.getLoginUid();
			
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						Result res = (Result) msg.obj;
						if (res.OK()) {
							if (specialDetail.getFavorite() == 1) {
								specialDetail.setFavorite(0);
								mFavorite
										.setImageResource(R.drawable.widget_bar_favorite);
							} else {
								specialDetail.setFavorite(1);
								mFavorite
										.setImageResource(R.drawable.widget_bar_favorite2);
							}
							// 重新保存缓存
							//appContext.saveObject(specialDetail, specialDetail.getCacheKey());
						}
						UIHelper.ToastMessage(SpecialDetail.this,
								res.getErrorMessage());
					} else {
						((AppException) msg.obj).makeToast(SpecialDetail.this);
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					Result res = null;
					try {
						if (specialDetail.getFavorite() == 1) {
							res = ac.delFavorite(uid, articleId,
									FavoriteList.TYPE_ARTICLE);
						} else {
							res = ac.addFavorite(uid, articleId,
									FavoriteList.TYPE_ARTICLE);
						}
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
		}
	};*/
	//点击刷新
	private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			//加载Special数据
			initSpecialData();
			//加载SpecialArticle列表数据
			initSaListViewData();
			//loadLvCommentData(curId, curCatalog, 0, mCommentHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
	//点击作者
	/*private View.OnClickListener authorClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showUserCenter(v.getContext(), specialDetail.getAuthorId(),
					specialDetail.getAuthor());
		}
	};*/
	
	//点击切换评论
	/*private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (articleId == 0) {
				return;
			}
			// 切换到评论
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};*/
	
	
	
	/**
	 * 初始化所有ListView
	 */
	private void initFrameListView() {
		// 初始化listview控件
		initSaListView(TypeHelper.ARTICLE);
		//加载Special数据
		initSpecialData();
		//加载SpecialArticle列表数据
		initSaListViewData();
    }
	
	/**
	 * 初始化列表界面控件+事件响应
	 */
	private void initSaListView(final int objType) {
		
		saLvHeader = getLayoutInflater().inflate(saLvHeaderR, null);
		saLvHead_title = (TextView) saLvHeader.findViewById(saLvHeadTitleR);
		saLvHead_img = (ImageView) saLvHeader.findViewById(saLvHeadImgR);
		saLvHead_intro = (TextView) saLvHeader.findViewById(saLvHeadIntroR);
		saLvHeader.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		saLvAdapter = new ListViewArticleAdapter(this, saLvData, saLvItemR);
		saLvFooter = getLayoutInflater().inflate(saLvFooterR,null);
		saLvFoot_more = (TextView) saLvFooter.findViewById(saLvFootMoreR);
		saLvFoot_progress = (ProgressBar) saLvFooter.findViewById(saLvFootProgressR);
		saListView = (PullToRefreshListView) findViewById(saListViewR);
		saListView.addHeaderView(saLvHeader);// 头部视图
		saListView.addFooterView(saLvFooter);// 添加底部视图 必须在setAdapter前
		//给View添加Adapter，两者联系的关键！系统绘制View时调用相应Adapter的getView
		saListView.setAdapter(saLvAdapter);	
		//OnItemClick
		saListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == saLvFooter)
					return;

				BaseItem base = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					base = (BaseItem) view.getTag();
				} else { //否则从title中取出TAG---item对象
					TextView tv = (TextView) view.findViewById(saLvItemTitleR);
					base = (BaseItem) tv.getTag();
				}
				if (base == null)
					return;
				
				// 跳转到详情Activity
				UIHelper.showDetail(view.getContext(), base.getId(), objType);
			}
		});
		//OnScroll
		saListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			//onScrollStateChanged
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				saListView.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (saLvData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(saLvFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(saListView.getTag());
				//如果滑到底而且还有更多数据可读（就是列表填满PAGE_SIZE个）
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					saListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					saLvFoot_more.setText(R.string.load_ing);
					saLvFoot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = saLvSumData / AppContext.PAGE_SIZE;
					//按照pageIndex新数据
					loadSaLvData(pageIndex, saLvHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			//onScroll
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				saListView.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		//OnRefresh
		saListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadSaLvData(0, saLvHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}
	
	/**
	 * 
     * 初始化所有ListView数据
     */
    private void initSaListViewData()
    {
    	//初始化Handler
    	saLvHandler = new Handler() {
			public void handleMessage(Message msg) {
				headButtonSwitch(DATA_LOAD_COMPLETE);
				
				if (msg.what >= 0) {
					// listview数据处理
					Notice notice = handleSaLvData(msg.what, msg.obj, msg.arg2,
							msg.arg1);
					if (msg.what < pageSize) {
						saListView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						//下面这句是Handler调用Adapter改变View显示的关键！
						saLvAdapter.notifyDataSetChanged();
						saLvFoot_more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						saListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						saLvAdapter.notifyDataSetChanged();
						saLvFoot_more.setText(R.string.load_more);
					}
					// 发送通知广播
					if (notice != null) {
						UIHelper.sendBroadCast(saListView.getContext(), notice);
					}
					// 是否清除通知信息
					if (isClearNotice) {
						isClearNotice = false;// 重置
						curClearNoticeType = 0;
					}
				} else if (msg.what < 0) {
					if(msg.what == -1){
						// 有异常--显示加载出错 & 弹出错误消息
						saListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						saLvFoot_more.setText(R.string.load_error);
						((AppException) msg.obj).makeToast(SpecialDetail.this);
					}
					//TODO:以下是自定义异常,未作处理
					else if (msg.what == -2) { 
						saListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						saLvFoot_more.setText(R.string.load_error);
					}
				}
				if (saLvAdapter.getCount() == 0) {
					saListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					saLvFoot_more.setText(R.string.load_empty);
				}
				saLvFoot_progress.setVisibility(ProgressBar.GONE);
				//mHeadProgress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					saListView.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					saListView.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					saListView.onRefreshComplete();
					saListView.setSelection(0);
				}
			}
		};
    	
        //加载文章数据（因为文章是默认第一屏）
		if(saLvData.isEmpty()) {
			loadSaLvData(0, saLvHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}
	}
    
	/**
     * listview数据处理，将Msg传来的数据对比后本地化到lvData
     * @param what 数量
     * @param obj 数据
     * @param objtype 数据类型  1--Article 2--Huati 3--Special 4--ACTIVE 5--Message
     * @param actiontype 操作类型
     * @return notice 通知信息
     */
    private Notice handleSaLvData(int what,Object obj,int objtype,int actiontype){
    	Notice notice = null;
    	BaseList list = null;
		switch (actiontype) {
			case UIHelper.LISTVIEW_ACTION_INIT:			//初始化
			case UIHelper.LISTVIEW_ACTION_REFRESH:		//刷新
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:	//切换栏目
				int newdata = 0;//新加载数据-只有刷新动作才会使用到
				list = (BaseList)obj;
				notice = list.getNotice();
				saLvSumData = what;
				if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
					if(saLvData.size() > 0){
						//对于每一个旧数据中的项，如果在新数据中找不到相同id的，则newdata+1
						for(BaseItem item1 : list.getList()){
							boolean b = false;
							for(BaseItem item2 : saLvData){
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
				saLvData.clear();//先清除原有数据
				saLvData.addAll(list.getList());
				
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
				saLvSumData += what;
				if(saLvData.size() > 0){
					for(BaseItem art1 : list.getList()){
						boolean b = false;
						for(BaseItem art2 : saLvData){
							if(art1.getId() == art2.getId()){
								b = true;
								break;
							}
						}
						if(!b) saLvData.add(art1);
					}
				}else{
					saLvData.addAll(list.getList());
				}
				break;
		}
		return notice;
    }
   
    
    /**
     * 线程加载SpecialArticleList列表数据
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadSaLvData(final int pageIndex, final Handler handler, 	final int action){ 
		//mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		headButtonSwitch(DATA_LOAD_ING);
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {					
					BaseList list = new ArticleList();
					
					//TODO:这里的HashMap细节可隐藏在具体类中，亦可switch(objType)
					Map<String, Object> httpGetPara = new HashMap<String, Object>(){{
						put("catalog", TypeHelper.Article.CATALOG_SPECIAL);
						put("id", specialId);
						put("pageIndex", pageIndex);
						put("pageSize", AppContext.PAGE_SIZE);
					}};
					
					list = appContext.getList(list, httpGetPara, isRefresh);
					
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				//msg.arg2 = objType;
                
               	handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 
     * 初始化SpecialDetail数据
     */
	private void initSpecialData() {
		saItemHandler = new Handler() {
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);
				
				if (msg.what == 1) {
					saLvHead_title.setText(specialDetail.getTitle());
					saLvHead_intro.setText(specialDetail.getIntro());
					//mCommentCount.setText(String.valueOf(articleDetail.getCommentCount()));

					// 是否收藏
					/*if (specialDetail.getFavorite() == 1)
						mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					else
						mFavorite.setImageResource(R.drawable.widget_bar_favorite);*/

					// 显示评论数
					/*if (articleDetail.getCommentCount() > 0) {
						bv_comment.setText(articleDetail.getCommentCount() + "");
						bv_comment.show();
					} else {
						bv_comment.setText("");
						bv_comment.hide();
					}*/

					// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
					boolean isLoadImage;

					if (AppContext.NETTYPE_WIFI == appContext.getNetworkType()) {
						isLoadImage = true;
					} else {
						isLoadImage = appContext.isLoadImage();
					}
					if (isLoadImage) {
						//加载图片
						final String imgURL = specialDetail.getImg();
						if(imgURL.endsWith("default.gif") || StringUtils.isEmpty(imgURL)){
							saLvHead_img.setImageResource(R.drawable.widget_dimg);
						}else{
							bmpManager.loadBitmap(imgURL, saLvHead_img);
						}

						// 添加点击图片放大支持
						saLvHead_img.setOnClickListener(new View.OnClickListener(){
							public void onClick(View v) {
								UIHelper.showImageZoomDialog(v.getContext(), imgURL);
							}
						});
					} else {
						// 过滤掉img标签
					}

					// 发送通知广播
					if (msg.obj != null) {
						UIHelper.sendBroadCast(SpecialDetail.this,
								(Notice) msg.obj);
					}
				} else if (msg.what == 0) {
					headButtonSwitch(DATA_LOAD_FAIL);
					UIHelper.ToastMessage(SpecialDetail.this,
							R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					headButtonSwitch(DATA_LOAD_FAIL);
					((AppException) msg.obj).makeToast(SpecialDetail.this);
				}
			}
		};

		loadSpecialData(specialId, saItemHandler, false);
	}
	
	
    /**
     * 线程加载SpecialDetail详情数据
     * @param special_id 专题id
     * @param handler 处理器
     * @param isRefresh 是否刷新
     */
    private void loadSpecialData(final int special_id, final Handler handler, final boolean isRefresh) {
    	headButtonSwitch(DATA_LOAD_ING);
    	
    	new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					specialDetail = new Special();
					Map<String, Object> httpGetPara = new HashMap<String, Object>(){{
						put("id", special_id);
					}};
					specialDetail = (Special)appContext.getItem(specialDetail, httpGetPara, isRefresh);
					msg.what = (specialDetail.getId() > 0) ? 1 : 0;
					msg.obj = (specialDetail.getId() != 0) ? specialDetail.getNotice() : null;// 通知信息
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
}
