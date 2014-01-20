package net.oschina.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewCommentAdapter;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.BaseList;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Topic;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.BadgeView;
import net.oschina.app.widget.PullToRefreshListView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 问答详情
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2013-12-27
 */
public class TopicDetail extends BaseActivity {

	private FrameLayout mHeader;
	private LinearLayout mFooter;
	private ImageView mBack;
	private ImageView mFavorite;
	private ImageView mRefresh;
	private TextView mHeadTitle;
	private ProgressBar mProgressbar;
	private ScrollView mScrollView;
    private ViewSwitcher mViewSwitcher;
    
	//private BadgeView bv_comment;
	private ImageView mDetail;
	//private ImageView mCommentList;
	private ImageView mShare;
	
	private TextView mTitle;
	private TextView mAuthor;
	private TextView mPubDate;
	//private TextView mCommentCount;
	
	private WebView mWebView;
    private Handler mHandler;
    private Topic topicDetail;
    private int topicId;
    
	private final static int VIEWSWITCH_TYPE_DETAIL = 0x001;
	private final static int VIEWSWITCH_TYPE_COMMENTS = 0x002;
	
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int DATA_LOAD_FAIL = 0x003;
	
	private PullToRefreshListView mLvComment;
	private ListViewCommentAdapter lvCommentAdapter;
	private List<BaseItem> lvCommentData = new ArrayList<BaseItem>();
	private View lvComment_footer;
	private TextView lvComment_foot_more;
	private ProgressBar lvComment_foot_progress;
    private Handler mCommentHandler;
    private int lvSumData;
    
    private int curId;
	private int curCatalog;	
	private int curLvDataState;
	private int curLvPosition;//当前listview选中的item位置
	
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;	
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;
	
	private int _catalog;
	private int _id;
	private int _uid;
	private String _content;
	private int _isTopicToMyZone;
	
	private GestureDetector gd;
	private boolean isFullScreen;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_detail);
        
        this.initView();        
        this.initData();
        
    	//加载评论视图&数据
    	//this.initCommentView();
    	//this.initCommentData();
    	
    	//注册双击全屏事件
    	this.regOnDoubleEvent();
    }
    
    //初始化视图控件
    private void initView()
    {
		topicId = getIntent().getIntExtra("topic_id", 0);
		
    	if(topicId > 0) tempCommentKey = AppConfig.TEMP_COMMENT + "_" + CommentList.CATALOG_TOPIC + "_" + topicId;
    	
    	mHeader = (FrameLayout)findViewById(R.id.topic_detail_header);
    	mFooter = (LinearLayout)findViewById(R.id.topic_detail_footer);
    	mBack = (ImageView)findViewById(R.id.topic_detail_back);
    	mRefresh = (ImageView)findViewById(R.id.topic_detail_refresh);
    	mHeadTitle = (TextView)findViewById(R.id.topic_detail_head_title);
    	mProgressbar = (ProgressBar)findViewById(R.id.topic_detail_head_progress);
    	mViewSwitcher = (ViewSwitcher)findViewById(R.id.topic_detail_viewswitcher);
    	mScrollView = (ScrollView)findViewById(R.id.topic_detail_scrollview);
    	
    	mDetail = (ImageView)findViewById(R.id.topic_detail_footbar_detail);
    	//mCommentList = (ImageView)findViewById(R.id.topic_detail_footbar_commentlist);
    	mShare = (ImageView)findViewById(R.id.topic_detail_footbar_share);
    	mFavorite = (ImageView)findViewById(R.id.topic_detail_footbar_favorite);
    	
    	mTitle = (TextView)findViewById(R.id.topic_detail_title);
    	mAuthor = (TextView)findViewById(R.id.topic_detail_author);
    	mPubDate = (TextView)findViewById(R.id.topic_detail_date);
    	//mCommentCount = (TextView)findViewById(R.id.topic_detail_commentcount);
    	
    	mWebView = (WebView)findViewById(R.id.topic_detail_webview);
    	mWebView.getSettings().setSupportZoom(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
    	mWebView.getSettings().setDefaultFontSize(15);    	
    	UIHelper.addWebImageShow(this, mWebView);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mFavorite.setOnClickListener(favoriteClickListener);
    	mRefresh.setOnClickListener(refreshClickListener);
    	mShare.setOnClickListener(shareClickListener);
    	mDetail.setOnClickListener(detailClickListener);
    	//mCommentList.setOnClickListener(commentlistClickListener);
    	mAuthor.setOnClickListener(authorClickListener);
    	
/*    	bv_comment = new BadgeView(this, mCommentList);
    	bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
    	bv_comment.setIncludeFontPadding(false);
    	bv_comment.setGravity(Gravity.CENTER);
    	bv_comment.setTextSize(8f);
    	bv_comment.setTextColor(Color.WHITE);*/
    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mFootViewSwitcher = (ViewSwitcher)findViewById(R.id.topic_detail_foot_viewswitcher);
    	mFootPubcomment = (Button)findViewById(R.id.topic_detail_foot_pubcomment);
    	mFootPubcomment.setOnClickListener(commentpubClickListener);
    	mFootEditebox = (ImageView)findViewById(R.id.topic_detail_footbar_editebox);
    	mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
    	mFootEditer = (EditText)findViewById(R.id.topic_detail_foot_editer);
    	mFootEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){  
					imm.showSoftInput(v, 0);  
		        }  
		        else{  
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		        }  
			}
		}); 
    	mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if(mFootViewSwitcher.getDisplayedChild()==1){
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
    	//编辑器添加文本监听
    	mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this, tempCommentKey));
    	
    	//显示临时编辑内容
    	UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
    }
    
    //初始化控件数据
	private void initData()
	{		
		mHandler = new Handler()
		{
			public void handleMessage(Message msg) {

				headButtonSwitch(DATA_LOAD_COMPLETE);
				
				if(msg.what == 1)
				{					
					mTitle.setText(topicDetail.getTitle());
					mAuthor.setText(topicDetail.getAuthor());
					//mPubDate.setText(StringUtils.friendly_time(topicDetail.getPubDate()));
					mPubDate.setText(topicDetail.getPubDate());
					//mCommentCount.setText(topicDetail.getAnswerCount()+"回/"+topicDetail.getViewCount()+"阅");
					
					//是否收藏
					if(topicDetail.getFavorite() == 1)
						mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					else
						mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					
					//显示评论数
/*					if(topicDetail.getAnswerCount() > 0){
						bv_comment.setText(topicDetail.getAnswerCount()+"");
						bv_comment.show();
					}else{
						bv_comment.setText("");
						bv_comment.hide();
					}
					
					//显示标签
					String tags = getTopicTags(topicDetail.getTags());	*/				
					
					String body = UIHelper.WEB_STYLE + topicDetail.getBody() + "<div style=\"margin-bottom: 80px\" />";
					
					//去掉<img>前面的<a>
					body = body.replaceAll("(<a.*>)(<img.*/>)","$2");

					//读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
					boolean isLoadImage;
					AppContext ac = (AppContext)getApplication();
					if(AppContext.NETTYPE_WIFI == ac.getNetworkType()){
						isLoadImage = true;
					}else{
						isLoadImage = ac.isLoadImage();
					}
					if(isLoadImage){
						//去掉所有<img>标签中的width和height属性
						body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+","$1");
						body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+","$1");
						// 添加点击图片放大支持
						body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
								"$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");
					}else{
						body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>","");
					}
					
					mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8",null);
					mWebView.setWebViewClient(UIHelper.getWebViewClient());	
					mWebView.clearFocus();
					
					//发送通知广播
					if(msg.obj != null){
						UIHelper.sendBroadCast(TopicDetail.this, (Notice)msg.obj);
					}
				}
				else if(msg.what == 0)
				{
					headButtonSwitch(DATA_LOAD_FAIL);
					
					UIHelper.ToastMessage(TopicDetail.this, R.string.msg_load_is_null);
				}
				else if(msg.what == -1 && msg.obj != null)
				{
					headButtonSwitch(DATA_LOAD_FAIL);
					
					((AppException)msg.obj).makeToast(TopicDetail.this);
				}
			}
		};
		
		initDataThread(topicId, false);
	}
	
    private void initDataThread(final int topic_id, final boolean isRefresh)
    {
    	headButtonSwitch(DATA_LOAD_ING);
		
		new Thread(){
			public void run() {
                Message msg = new Message();
				try {
					topicDetail = new Topic();
					Map<String, Object> httpGetPara = new HashMap<String, Object>(){{
						put("id", topic_id);
					}};
					topicDetail = (Topic)((AppContext) getApplication()).getItem(topicDetail, httpGetPara, isRefresh);
					msg.what = (topicDetail.getId() > 0) ? 1 : 0;
					msg.obj = (topicDetail.getId() != 0) ? topicDetail.getNotice() : null;// 通知信息
	            } catch (AppException e) {
	                e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
                mHandler.sendMessage(msg);
			}
		}.start();
    }
	
    //将taglist转化为HTML标签
    /*private String getTopicTags(List<String> taglist) {
    	if(taglist == null)
    		return "";
    	String tags = "";
    	for(String tag : taglist) {
    		tags += String.format("<a class='tag' href='http://www.oschina.net/topic/tag/%s' >&nbsp;%s&nbsp;</a>&nbsp;&nbsp;", URLEncoder.encode(tag), tag);
    	}
    	return String.format("<div style='margin-top:10px;'>%s</div>", tags);
    }*/
    
    
    /**
     * 底部栏切换
     * @param type
     */
    private void viewSwitch(int type) {
    	switch (type) {
		case VIEWSWITCH_TYPE_DETAIL:
			mDetail.setEnabled(false);
			//mCommentList.setEnabled(true);
			mHeadTitle.setText(R.string.topic_detail_head_title);
			mViewSwitcher.setDisplayedChild(0);			
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			mDetail.setEnabled(true);
			//mCommentList.setEnabled(false);
			mHeadTitle.setText(R.string.comment_list_head_title);
			mViewSwitcher.setDisplayedChild(1);
			break;
    	}
    }
    
    /**
     * 头部按钮展示
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
		case DATA_LOAD_ING:
			mScrollView.setVisibility(View.GONE);
			mProgressbar.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mScrollView.setVisibility(View.VISIBLE);
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_FAIL:
			mScrollView.setVisibility(View.GONE);
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		}
    }
    
    //点击右上角的“刷新”按钮
    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			initDataThread(topicId, true);
			//loadLvCommentData(curId,curCatalog,0,mCommentHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
	
    //点击“作者”，跳转到该作者的用户中心
	private View.OnClickListener authorClickListener = new View.OnClickListener() {
		public void onClick(View v) {				
			//UIHelper.showUserCenter(v.getContext(), topicDetail.getAuthorId(), topicDetail.getAuthor());
		}
	};
	
	//点击下方“分享”按钮
	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(topicDetail == null){
				UIHelper.ToastMessage(v.getContext(), R.string.msg_read_detail_fail);
				return;
			}
			//分享到
			UIHelper.showShareDialog(TopicDetail.this, topicDetail.getTitle(), topicDetail.getUrl());
		}
	};
	
	//点击下方“详细”按钮（默认选中），控件切换
	private View.OnClickListener detailClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(topicId == 0){
				return;
			}
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};
	
	//点击下方“评论”按钮，控件切换
	private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(topicId == 0){
				return;
			}
			//切换显示评论
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};
	
	//点击下方“收藏”按钮，检查登录，异步发送请求与服务器同步
	private View.OnClickListener favoriteClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(topicId == 0 || topicDetail == null){
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(TopicDetail.this);
				return;
			}
			final int uid = ac.getLoginUid();
						
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						if(res.OK()){
							if(topicDetail.getFavorite() == 1){
								topicDetail.setFavorite(0);
								mFavorite.setImageResource(R.drawable.widget_bar_favorite);
							}else{
								topicDetail.setFavorite(1);
								mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
							}
							//重新保存缓存
							//ac.saveObject(topicDetail, topicDetail.getCacheKey());
						}
						UIHelper.ToastMessage(TopicDetail.this, res.getErrorMessage());
					}else{
						((AppException)msg.obj).makeToast(TopicDetail.this);
					}
				}        			
    		};
    		new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = null;
//					try {
//						if(topicDetail.getFavorite() == 1){
//							//TODO:收藏接口
//							//res = ac.delFavorite(uid, topicId, FavoriteList.TYPE_TOPIC);
//						}else{
//							//res = ac.addFavorite(uid, topicId, FavoriteList.TYPE_TOPIC);
//						}
//						msg.what = 1;
//						msg.obj = res;
//		            } catch (AppException e) {
//		            	e.printStackTrace();
//		            	msg.what = -1;
//		            	msg.obj = e;
//		            }
	                handler.sendMessage(msg);
				}        			
    		}.start();	
		}
	};
	
	//初始化“评论”视图控件
    private void initCommentView()
    {    	
    	lvComment_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvComment_foot_more = (TextView)lvComment_footer.findViewById(R.id.listview_foot_more);
        lvComment_foot_progress = (ProgressBar)lvComment_footer.findViewById(R.id.listview_foot_progress);

    	lvCommentAdapter = new ListViewCommentAdapter(this, lvCommentData, R.layout.comment_listitem); 
    	mLvComment = (PullToRefreshListView)findViewById(R.id.comment_list_listview);
    	
        mLvComment.addFooterView(lvComment_footer);//添加底部视图  必须在setAdapter前
        mLvComment.setAdapter(lvCommentAdapter); 
        mLvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击头部、底部栏无效
        		if(position == 0 || view == lvComment_footer) return;
        		
        		Comment com = null;
        		//判断是否是TextView
        		if(view instanceof TextView){
        			com = (Comment)view.getTag();
        		}else{
            		ImageView img = (ImageView)view.findViewById(R.id.comment_listitem_userface);
            		com = (Comment)img.getTag();
        		} 
        		if(com == null) return;

        		//跳转--回复评论界面
        		UIHelper.showCommentReply(TopicDetail.this,curId, curCatalog, com.getId(), com.getAuthorId(), com.getAuthor(), com.getContent());
        	}
		});
        mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);
				
				//数据为空--不用继续下面代码了
				if(lvCommentData.size() == 0) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvComment_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				if(scrollEnd && curLvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvComment_foot_more.setText(R.string.load_ing);
					lvComment_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvSumData/20;
					loadLvCommentData(curId, curCatalog, pageIndex, mCommentHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				mLvComment.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        mLvComment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == lvComment_footer) return false;				
				
        		Comment _com = null;
        		//判断是否是TextView
        		if(view instanceof TextView){
        			_com = (Comment)view.getTag();
        		}else{
            		ImageView img = (ImageView)view.findViewById(R.id.comment_listitem_userface);
            		_com = (Comment)img.getTag();
        		}
        		if(_com == null) return false;
        		
        		final Comment com = _com;
        		
        		curLvPosition = lvCommentData.indexOf(com);
        		
        		final AppContext ac = (AppContext)getApplication();
				//操作--回复 & 删除
        		int uid = ac.getLoginUid();
        		//判断该评论是否是当前登录用户发表的：true--有删除操作  false--没有删除操作
        		if(uid == com.getAuthorId())
        		{
	        		final Handler handler = new Handler(){
						public void handleMessage(Message msg) {
							if(msg.what == 1){
								Result res = (Result)msg.obj;
								if(res.OK()){
									lvSumData--;
									/*bv_comment.setText(lvSumData+"");
						    		bv_comment.show();*/
									lvCommentData.remove(com);
									lvCommentAdapter.notifyDataSetChanged();
								}
								UIHelper.ToastMessage(TopicDetail.this, res.getErrorMessage());
							}else{
								((AppException)msg.obj).makeToast(TopicDetail.this);
							}
						}        			
	        		};
	        		final Thread thread = new Thread(){
						public void run() {
							Message msg = new Message();
							/* TODO:删除评论
							 * try {
								Result res = ac.delComment(curId, curCatalog, com.getId(), com.getAuthorId());
								msg.what = 1;
								msg.obj = res;
				            } catch (AppException e) {
				            	e.printStackTrace();
				            	msg.what = -1;
				            	msg.obj = e;
				            }*/
			                handler.sendMessage(msg);
						}        			
	        		};
	        		UIHelper.showCommentOptionDialog(TopicDetail.this, curId, curCatalog, com, thread);
        		}
        		else
        		{
        			UIHelper.showCommentOptionDialog(TopicDetail.this, curId, curCatalog, com, null);
        		}
				return true;
			}        	
		});
        mLvComment.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvCommentData(curId, curCatalog, 0, mCommentHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }
    
    //初始化评论数据
	private void initCommentData()
	{
		curId = topicId;
		curCatalog = CommentList.CATALOG_TOPIC;
		
    	mCommentHandler = new Handler()
		{
			public void handleMessage(Message msg) 
			{				
				if(msg.what >= 0){						
					CommentList list = (CommentList)msg.obj;
					Notice notice = list.getNotice();
					//处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvCommentData.clear();//先清除原有数据
						lvCommentData.addAll(list.getList());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if(lvCommentData.size() > 0){
							for(BaseItem com1 : list.getList()){
								boolean b = false;
								for(BaseItem com2 : lvCommentData){
									if(com1.getId() == com2.getId() 
											&& ((Comment)com1).getAuthorId() == ((Comment)com2).getAuthorId()){
										b = true;
										break;
									}
								}
								if(!b) lvCommentData.add(com1);
							}
						}else{
							lvCommentData.addAll(list.getList());
						}
						break;
					}	
					
					//评论数更新
					/*if(topicDetail != null && lvCommentData.size() > topicDetail.getAnswerCount()){
						topicDetail.setAnswerCount(lvCommentData.size());
						bv_comment.setText(lvCommentData.size()+"");
						bv_comment.show();
					}*/
					
					if(msg.what < 20){
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_full);
					}else if(msg.what == 20){					
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_more);
					}
					//发送通知广播
					if(notice != null){
						UIHelper.sendBroadCast(TopicDetail.this, notice);
					}
				}
				else if(msg.what == -1){
					//有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvComment_foot_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(TopicDetail.this);
				}
				if(lvCommentData.size()==0){
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvComment_foot_more.setText(R.string.load_empty);
				}
				lvComment_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mLvComment.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					mLvComment.setSelection(0);
				}
			}
		};
		this.loadLvCommentData(curId,curCatalog,0,mCommentHandler,UIHelper.LISTVIEW_ACTION_INIT);
    }
    /**
     * 线程加载评论数据
     * @param id 当前文章id
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvCommentData(final int id,final int catalog,final int pageIndex,final Handler handler,final int action){  
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					BaseList commentlist = new CommentList();
					Map<String, Object> httpGetPara = new HashMap<String, Object>(){{
						put("catalog", catalog);
						put("pageIndex", pageIndex);
						put("id", id);
					}};
					((AppContext)getApplication()).getList(commentlist, httpGetPara, isRefresh);				
					msg.what = commentlist.getPageSize();
					msg.obj = commentlist;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
	} 
	
	//只有回复评论，切换Activity后需要返回数据
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 	
		if (resultCode != RESULT_OK) return;   
    	if (data == null) return;
    	
    	viewSwitch(VIEWSWITCH_TYPE_COMMENTS);//跳到评论列表
    	
        if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) 
        { 
        	Comment comm = (Comment)data.getSerializableExtra("COMMENT_SERIALIZABLE");
        	lvCommentData.add(0,comm);
        	lvCommentAdapter.notifyDataSetChanged();
        	mLvComment.setSelection(0);
    		//显示评论数
            /*int count = topicDetail.getAnswerCount() + 1;
            topicDetail.setAnswerCount(count);
    		bv_comment.setText(count+"");
    		bv_comment.show();*/
        }
        else if (requestCode == UIHelper.REQUEST_CODE_FOR_REPLY)
        {
        	Comment comm = (Comment)data.getSerializableExtra("COMMENT_SERIALIZABLE");
        	lvCommentData.set(curLvPosition, comm);
        	lvCommentAdapter.notifyDataSetChanged();
        }
	}
	
	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			_id = curId;
			
			if(curId == 0){
				return;
			}
			
			_catalog = curCatalog;
			
			_content = mFootEditer.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "请输入回帖内容");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(TopicDetail.this);
				return;
			}
			
//			if(mZone.isChecked())
//				_isTopicToMyZone = 1;
				
			_uid = ac.getLoginUid();
			
			mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",true,true); 	
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(TopicDetail.this, res.getErrorMessage());
						if(res.OK()){
							//发送通知广播
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(TopicDetail.this, res.getNotice());
							}
							//恢复初始底部栏
							mFootViewSwitcher.setDisplayedChild(0);
							mFootEditer.clearFocus();
							mFootEditer.setText("");
							mFootEditer.setVisibility(View.GONE);
							//跳到评论列表
					    	viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
					    	//更新评论列表
					    	lvCommentData.add(0,res.getComment());
					    	lvCommentAdapter.notifyDataSetChanged();
					    	mLvComment.setSelection(0);        	
							/*//显示评论数
					        int count = topicDetail.getAnswerCount() + 1;
					        topicDetail.setAnswerCount(count);
							bv_comment.setText(count+"");
							bv_comment.show();*/
							//清除之前保存的编辑内容
							ac.removeProperty(tempCommentKey);
						}
					}
					else {
						((AppException)msg.obj).makeToast(TopicDetail.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					/* TODO：删除评论
					 * try {
						//发表评论
						res = ac.pubComment(_catalog, _id, _uid, _content, _isTopicToMyZone);
						msg.what = 1;
						msg.obj = res;
		            } catch (AppException e) {
		            	e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
		            }*/
					handler.sendMessage(msg);
				}
			}.start();
		}
	};
	
	/**
	 * 注册双击全屏事件
	 */
	private void regOnDoubleEvent(){
		gd = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				isFullScreen = !isFullScreen;
				if (!isFullScreen) {   
                    WindowManager.LayoutParams params = getWindow().getAttributes();   
                    params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);   
                    getWindow().setAttributes(params);   
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);  
                    mHeader.setVisibility(View.VISIBLE);
                    mFooter.setVisibility(View.VISIBLE);
                } else {    
                    WindowManager.LayoutParams params = getWindow().getAttributes();   
                    params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;   
                    getWindow().setAttributes(params);   
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);   
                    mHeader.setVisibility(View.GONE);
                    mFooter.setVisibility(View.GONE);
                }
				return true;
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (isAllowFullScreen()) {
			gd.onTouchEvent(event);
		}
		return super.dispatchTouchEvent(event);
	}
}
