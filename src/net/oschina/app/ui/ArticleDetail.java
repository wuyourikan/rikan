package net.oschina.app.ui;

import java.util.HashMap;
import java.util.Map;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewCommentAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Article;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.BadgeView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
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
 * 文章详情
 * 
 * @author wyf (http://my.oschina.net/zgtjwytfc)
 * @version 1.0
 * @created 2014-1-6
 */
public class ArticleDetail extends BaseActivity {

	private ScrollView mScrollView;
	private ViewSwitcher mViewSwitcher;
	
	//-----------控件组-----------
	//---头部控件组（已删）---
	//private FrameLayout mHeader;
	//private ImageView mHome;
	//private ImageView mRefresh;
	//private TextView mHeadTitle;
	//private ProgressBar mProgressbar;
	
	//---底部控件组---
	private LinearLayout mFooter;
	private ImageView mBack;
	//private ImageView mDetail;
	private ImageView mFavorite;
	//private BadgeView bv_comment;
	//private ImageView mCommentList;
	private ImageView mShare;
	//private ViewSwitcher mFootViewSwitcher;
	//private ImageView mFootEditebox;
	//private EditText mFootEditer;
	//private Button mFootPubcomment;
	
	//---页面头---
	private TextView mTitle;
	private TextView mAuthor;
	private TextView mPubDate;
	private TextView mSource;
	//private TextView mCommentCount;

	private WebView mWebView;
	
	//-----------全局变量-----------
	private Handler mHandler;
	private Article articleDetail;

	private int articleId;

	private final static int VIEWSWITCH_TYPE_DETAIL = 0x001;
	//private final static int VIEWSWITCH_TYPE_COMMENTS = 0x002;

	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int DATA_LOAD_FAIL = 0x003;

	//评论模块
	//private PullToRefreshListView mLvComment;
	//private ListViewCommentAdapter lvCommentAdapter;
	//private List<Comment> lvCommentData = new ArrayList<Comment>();
	//private View lvComment_footer;
	//private TextView lvComment_foot_more;
	//private ProgressBar lvComment_foot_progress;
	//private Handler mCommentHandler;
	//private int lvSumData;

	/*private int curId;
	private int curCatalog;
	private int curLvDataState;
	private int curLvPosition;// 当前listview选中的item位置
	
	
	//private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;

	private int _catalog;
	private int _id;
	private int _uid;
	private String _content;
	private int _isPostToMyZone;
	 */
	private GestureDetector gd;
	private boolean isFullScreen;

	//-----------Activity生命周期-----------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_detail);

		this.initArticleView();
		this.initArticleData();

		// 加载评论视图&数据
		//this.initCommentView();
		//this.initCommentData();

		// 注册双击全屏事件
		this.regOnDoubleEvent();
	}

	// 初始化视图控件
	@SuppressLint("SetJavaScriptEnabled")
	private void initArticleView() {
		articleId = getIntent().getIntExtra("article_id", 0);

		//if (articleId > 0)
			//tempCommentKey = AppConfig.TEMP_COMMENT + "_" + CommentList.CATALOG_ARTICLE + "_" + articleId;
		
		//头部控件组（已删）
		//mHeader = (FrameLayout) findViewById(R.id.article_detail_header);
		//mRefresh = (ImageView) findViewById(R.id.article_detail_refresh);
		//mHeadTitle = (TextView) findViewById(R.id.article_detail_head_title);
		//mProgressbar = (ProgressBar) findViewById(R.id.article_detail_head_progress);
		//mHome = (ImageView) findViewById(R.id.article_detail_home);
		
		//底部控件组
		mFooter = (LinearLayout) findViewById(R.id.article_detail_footer);
		mBack 	= (ImageView) findViewById(R.id.article_datail_footbar_back);
		//mDetail = (ImageView) findViewById(R.id.article_detail_footbar_detail);
		//mCommentList = (ImageView) findViewById(R.id.article_detail_footbar_commentlist);
		mShare 	= (ImageView) findViewById(R.id.article_detail_footbar_share);
		mFavorite = (ImageView) findViewById(R.id.article_detail_footbar_favorite);
		
		//mDetail.setEnabled(false);
		
		//中间控件组
		mViewSwitcher = (ViewSwitcher) findViewById(R.id.article_detail_viewswitcher);
		mScrollView = (ScrollView) findViewById(R.id.article_detail_scrollview);
		mTitle 		= (TextView) findViewById(R.id.article_detail_title);
		mAuthor 	= (TextView) findViewById(R.id.article_detail_author);
		mPubDate 	= (TextView) findViewById(R.id.article_detail_date);
		mSource		= (TextView) findViewById(R.id.article_detail_source);
		//mCommentCount = (TextView) findViewById(R.id.article_detail_commentcount);
		
		//WebView设置
		mWebView = (WebView) findViewById(R.id.article_detail_webview);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDefaultFontSize(15);
        UIHelper.addWebImageShow(this, mWebView);
		
        //设置Button响应事件
		mBack.setOnClickListener(homeClickListener);
		mFavorite.setOnClickListener(favoriteClickListener);
		//mRefresh.setOnClickListener(refreshClickListener);
		//mAuthor.setOnClickListener(authorClickListener);
		mShare.setOnClickListener(shareClickListener);
		//mDetail.setOnClickListener(detailClickListener);
		//mCommentList.setOnClickListener(commentlistClickListener);

		//评论数气泡
		//bv_comment = new BadgeView(this, mCommentList);
		//bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
		//bv_comment.setIncludeFontPadding(false);
		//bv_comment.setGravity(Gravity.CENTER);
		//bv_comment.setTextSize(8f);
		//bv_comment.setTextColor(Color.WHITE);

		//底部栏点击评论切换View
		//imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		/*mFootViewSwitcher = (ViewSwitcher) findViewById(R.id.article_detail_foot_viewswitcher);
	  	mFootPubcomment = (Button) findViewById(R.id.article_detail_foot_pubcomment);
		mFootPubcomment.setOnClickListener(commentpubClickListener);
		mFootEditebox = (ImageView) findViewById(R.id.article_detail_footbar_editebox);
		mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
		mFootEditer = (EditText) findViewById(R.id.article_detail_foot_editer);
		mFootEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					imm.showSoftInput(v, 0);
				} else {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});
		mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mFootViewSwitcher.getDisplayedChild() == 1) {
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
		// 编辑器添加文本监听
		mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this, tempCommentKey));

		// 显示临时编辑内容
		UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
		*/
	}

	// 初始化控件数据
	private void initArticleData() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					headButtonSwitch(DATA_LOAD_COMPLETE);

					mTitle.setText(articleDetail.getTitle());
					mAuthor.setText(articleDetail.getAuthor());
					mPubDate.setText(articleDetail.getPubDate());
					mSource.setText(articleDetail.getSource());
					//mCommentCount.setText(String.valueOf(articleDetail.getCommentCount()));

					// 是否收藏
					if (articleDetail.getFavorite() == 1)
						mFavorite
								.setImageResource(R.drawable.widget_bar_favorite2);
					else
						mFavorite
								.setImageResource(R.drawable.widget_bar_favorite);

					// 显示评论数
					/*if (articleDetail.getCommentCount() > 0) {
						bv_comment.setText(articleDetail.getCommentCount() + "");
						bv_comment.show();
					} else {
						bv_comment.setText("");
						bv_comment.hide();
					}*/

					String body = UIHelper.WEB_STYLE + articleDetail.getBody();
					// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
					boolean isLoadImage;
					AppContext ac = (AppContext) getApplication();
					if (AppContext.NETTYPE_WIFI == ac.getNetworkType()) {
						isLoadImage = true;
					} else {
						isLoadImage = ac.isLoadImage();
					}
					if (isLoadImage) {
						// 过滤掉 img标签的width,height属性
						body = body.replaceAll(
								"(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
						body = body.replaceAll(
								"(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

						// 添加点击图片放大支持
						body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
								"$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");

					} else {
						// 过滤掉 img标签
						body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
					}

					// 相关新闻
					/*if (articleDetail.getRelatives().size() > 0) {
						String strRelative = "";
						for (Relative relative : articleDetail.getRelatives()) {
							strRelative += String
									.format("<a href='%s' style='text-decoration:none'>%s</a><p/>",
											relative.url, relative.title);
						}
						body += String.format(
								"<p/><hr/><b>相关资讯</b><div><p/>%s</div>",
								strRelative);
					}*/

					body += "<div style='margin-bottom: 80px'/>";

					mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
					mWebView.setWebViewClient(UIHelper.getWebViewClient());

					// 发送通知广播
					if (msg.obj != null) {
						UIHelper.sendBroadCast(ArticleDetail.this,
								(Notice) msg.obj);
					}
				} else if (msg.what == 0) {
					headButtonSwitch(DATA_LOAD_FAIL);

					UIHelper.ToastMessage(ArticleDetail.this,
							R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					headButtonSwitch(DATA_LOAD_FAIL);

					((AppException) msg.obj).makeToast(ArticleDetail.this);
				}
			}
		};

		initItemDataThread(articleId, false);
	}

	private void initItemDataThread(final int article_id, final boolean isRefresh) {
		headButtonSwitch(DATA_LOAD_ING);

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					articleDetail = new Article();
					Map<String, Object> httpGetPara = new HashMap<String, Object>(){{
						put("id", article_id);
					}};
					articleDetail = (Article)((AppContext) getApplication()).getItem(articleDetail, httpGetPara, isRefresh);
					msg.what = (articleDetail.getId() > 0) ? 1 : 0;
					msg.obj = (articleDetail.getId() != 0) ? articleDetail.getNotice() : null;// 通知信息
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 底部栏切换
	 * 
	 * @param type
	 */
	/*private void viewSwitch(int type) {
		switch (type) {
		case VIEWSWITCH_TYPE_DETAIL:
			//mDetail.setEnabled(false);
			//mCommentList.setEnabled(true);
			//mHeadTitle.setText(R.string.article_detail_head_title);
			mViewSwitcher.setDisplayedChild(0);
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			mDetail.setEnabled(true);
			mCommentList.setEnabled(false);
			mHeadTitle.setText(R.string.comment_list_head_title);
			mViewSwitcher.setDisplayedChild(1);
			break;)
		}
	}*/

	/**
	 * 头部按钮展示
	 * 
	 * @param type
	 */
	private void headButtonSwitch(int type) {
		switch (type) {
		case DATA_LOAD_ING:
			mScrollView.setVisibility(View.GONE);
			//mProgressbar.setVisibility(View.VISIBLE);
			//mRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mScrollView.setVisibility(View.VISIBLE);
			//mProgressbar.setVisibility(View.GONE);
			//mRefresh.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_FAIL:
			mScrollView.setVisibility(View.GONE);
			//mProgressbar.setVisibility(View.GONE);
			//mRefresh.setVisibility(View.VISIBLE);
			break;
		}
	}

	private View.OnClickListener homeClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showHome(ArticleDetail.this);
		}
	};

	/*private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			initData(articleId, true);
			loadLvCommentData(curId, curCatalog, 0, mCommentHandler,
					UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};

	private View.OnClickListener authorClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showUserCenter(v.getContext(), articleDetail.getAuthorId(),
					articleDetail.getAuthor());
		}
	};*/

	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (articleDetail == null) {
				UIHelper.ToastMessage(v.getContext(),
						R.string.msg_read_detail_fail);
				return;
			}
			// 分享到
			UIHelper.showShareDialog(ArticleDetail.this, articleDetail.getTitle(),
					articleDetail.getUrl());
		}
	};

	/*private View.OnClickListener detailClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (articleId == 0) {
				return;
			}
			// 切换到详情
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};*/

	/*private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (articleId == 0) {
				return;
			}
			// 切换到评论
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};*/

	private View.OnClickListener favoriteClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (articleId == 0 || articleDetail == null) {
				return;
			}

			final AppContext ac = (AppContext) getApplication();
			/*if (!ac.isLogin()) {
				UIHelper.showLoginDialog(ArticleDetail.this);
				return;
			}
			final int uid = ac.getLoginUid();
			*/
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						Result res = (Result) msg.obj;
						if (res.OK()) {
							if (articleDetail.getFavorite() == 1) {
								articleDetail.setFavorite(0);
								mFavorite
										.setImageResource(R.drawable.widget_bar_favorite);
							} else {
								articleDetail.setFavorite(1);
								mFavorite
										.setImageResource(R.drawable.widget_bar_favorite2);
							}
							// 重新保存缓存
							//ac.saveObject(articleDetail, articleDetail.getCacheKey());
						}
						UIHelper.ToastMessage(ArticleDetail.this,
								res.getErrorMessage());
					} else {
						((AppException) msg.obj).makeToast(ArticleDetail.this);
					}
				}
			};
			new Thread() {
				public void run() {
					/*Message msg = new Message();
					Result res = null;
					try {
						if (articleDetail.getFavorite() == 1) {
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
					handler.sendMessage(msg);*/
				}
			}.start();
		}
	};

	// 初始化评论视图控件
	/*private void initCommentView() {
		lvComment_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvComment_foot_more = (TextView) lvComment_footer
				.findViewById(R.id.listview_foot_more);
		lvComment_foot_progress = (ProgressBar) lvComment_footer
				.findViewById(R.id.listview_foot_progress);

		lvCommentAdapter = new ListViewCommentAdapter(this, lvCommentData,
				R.layout.comment_listitem);
		mLvComment = (PullToRefreshListView) findViewById(R.id.comment_list_listview);

		mLvComment.addFooterView(lvComment_footer);// 添加底部视图 必须在setAdapter前
		mLvComment.setAdapter(lvCommentAdapter);
		mLvComment
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0 || view == lvComment_footer)
							return;

						Comment com = null;
						// 判断是否是TextView
						if (view instanceof TextView) {
							com = (Comment) view.getTag();
						} else {
							ImageView img = (ImageView) view
									.findViewById(R.id.comment_listitem_userface);
							com = (Comment) img.getTag();
						}
						if (com == null)
							return;

						// 跳转--回复评论界面
						UIHelper.showCommentReply(ArticleDetail.this, curId,
								curCatalog, com.getId(), com.getAuthorId(),
								com.getAuthor(), com.getContent());
					}
				});
		mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvCommentData.size() == 0)
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvComment_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvComment_foot_more.setText(R.string.load_ing);
					lvComment_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvSumData / 20;
					loadLvCommentData(curId, curCatalog, pageIndex,
							mCommentHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mLvComment.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		mLvComment
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0 || view == lvComment_footer)
							return false;

						Comment _com = null;
						// 判断是否是TextView
						if (view instanceof TextView) {
							_com = (Comment) view.getTag();
						} else {
							ImageView img = (ImageView) view
									.findViewById(R.id.comment_listitem_userface);
							_com = (Comment) img.getTag();
						}
						if (_com == null)
							return false;

						final Comment com = _com;

						curLvPosition = lvCommentData.indexOf(com);

						final AppContext ac = (AppContext) getApplication();
						// 操作--回复 & 删除
						int uid = ac.getLoginUid();
						// 判断该评论是否是当前登录用户发表的：true--有删除操作 false--没有删除操作
						if (uid == com.getAuthorId()) {
							final Handler handler = new Handler() {
								public void handleMessage(Message msg) {
									if (msg.what == 1) {
										Result res = (Result) msg.obj;
										if (res.OK()) {
											lvSumData--;
											bv_comment.setText(lvSumData + "");
											bv_comment.show();
											lvCommentData.remove(com);
											lvCommentAdapter
													.notifyDataSetChanged();
										}
										UIHelper.ToastMessage(ArticleDetail.this,
												res.getErrorMessage());
									} else {
										((AppException) msg.obj)
												.makeToast(ArticleDetail.this);
									}
								}
							};
							final Thread thread = new Thread() {
								public void run() {
									Message msg = new Message();
									try {
										Result res = ac.delComment(curId,
												curCatalog, com.getId(),
												com.getAuthorId());
										msg.what = 1;
										msg.obj = res;
									} catch (AppException e) {
										e.printStackTrace();
										msg.what = -1;
										msg.obj = e;
									}
									handler.sendMessage(msg);
								}
							};
							UIHelper.showCommentOptionDialog(ArticleDetail.this,
									curId, curCatalog, com, thread);
						} else {
							UIHelper.showCommentOptionDialog(ArticleDetail.this,
									curId, curCatalog, com, null);
						}
						return true;
					}
				});
		mLvComment
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvCommentData(curId, curCatalog, 0,
								mCommentHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH);
					}
				});
	}*/

	// 初始化评论数据
	/*private void initCommentData() {
		curId = articleId;
		curCatalog = CommentList.CATALOG_ARTICLE;

		mCommentHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					CommentList list = (CommentList) msg.obj;
					Notice notice = list.getNotice();
					// 处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvCommentData.clear();// 先清除原有数据
						lvCommentData.addAll(list.getCommentlist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if (lvCommentData.size() > 0) {
							for (Comment com1 : list.getCommentlist()) {
								boolean b = false;
								for (Comment com2 : lvCommentData) {
									if (com1.getId() == com2.getId()
											&& com1.getAuthorId() == com2
													.getAuthorId()) {
										b = true;
										break;
									}
								}
								if (!b)
									lvCommentData.add(com1);
							}
						} else {
							lvCommentData.addAll(list.getCommentlist());
						}
						break;
					}

					// 评论数更新
					if (articleDetail != null
							&& lvCommentData.size() > articleDetail
									.getCommentCount()) {
						articleDetail.setCommentCount(lvCommentData.size());
						bv_comment.setText(lvCommentData.size() + "");
						bv_comment.show();
					}

					if (msg.what < 20) {
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_full);
					} else if (msg.what == 20) {
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_more);
					}
					// 发送通知广播
					if (notice != null) {
						UIHelper.sendBroadCast(ArticleDetail.this, notice);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvComment_foot_more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(ArticleDetail.this);
				}
				if (lvCommentData.size() == 0) {
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvComment_foot_more.setText(R.string.load_empty);
				}
				lvComment_foot_progress.setVisibility(View.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					mLvComment
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
					mLvComment.setSelection(0);
				}
			}
		};
		this.loadLvCommentData(curId, curCatalog, 0, mCommentHandler,
				UIHelper.LISTVIEW_ACTION_INIT);
	}*/

	/**
	 * 线程加载评论数据
	 * 
	 * @param id
	 *            当前文章id
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	/*private void loadLvCommentData(final int id, final int catalog,
			final int pageIndex, final Handler handler, final int action) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					CommentList commentlist = ((AppContext) getApplication())
							.getCommentList(catalog, id, pageIndex, isRefresh);
					msg.what = commentlist.getPageSize();
					msg.obj = commentlist;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;// 告知handler当前action
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;

		viewSwitch(VIEWSWITCH_TYPE_COMMENTS);// 跳到评论列表

		if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) {
			Comment comm = (Comment) data
					.getSerializableExtra("COMMENT_SERIALIZABLE");
			lvCommentData.add(0, comm);
			lvCommentAdapter.notifyDataSetChanged();
			mLvComment.setSelection(0);
			// 显示评论数
			int count = articleDetail.getCommentCount() + 1;
			articleDetail.setCommentCount(count);
			bv_comment.setText(count + "");
			bv_comment.show();
		} else if (requestCode == UIHelper.REQUEST_CODE_FOR_REPLY) {
			Comment comm = (Comment) data
					.getSerializableExtra("COMMENT_SERIALIZABLE");
			lvCommentData.set(curLvPosition, comm);
			lvCommentAdapter.notifyDataSetChanged();
		}
	}

	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			_id = curId;

			if (curId == 0) {
				return;
			}

			_catalog = curCatalog;

			_content = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}

			final AppContext ac = (AppContext) getApplication();
			if (!ac.isLogin()) {
				UIHelper.showLoginDialog(ArticleDetail.this);
				return;
			}

			// if(mZone.isChecked())
			// _isPostToMyZone = 1;

			_uid = ac.getLoginUid();

			mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",
					true, true);

			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {

					if (mProgress != null)
						mProgress.dismiss();

					if (msg.what == 1) {
						Result res = (Result) msg.obj;
						UIHelper.ToastMessage(ArticleDetail.this,
								res.getErrorMessage());
						if (res.OK()) {
							// 发送通知广播
							if (res.getNotice() != null) {
								UIHelper.sendBroadCast(ArticleDetail.this,
										res.getNotice());
							}
							// 恢复初始底部栏
							mFootViewSwitcher.setDisplayedChild(0);
							mFootEditer.clearFocus();
							mFootEditer.setText("");
							mFootEditer.setVisibility(View.GONE);
							// 跳到评论列表
							viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
							// 更新评论列表
							lvCommentData.add(0, res.getComment());
							lvCommentAdapter.notifyDataSetChanged();
							mLvComment.setSelection(0);
							// 显示评论数
							int count = articleDetail.getCommentCount() + 1;
							articleDetail.setCommentCount(count);
							bv_comment.setText(count + "");
							bv_comment.show();
							// 清除之前保存的编辑内容
							ac.removeProperty(tempCommentKey);
						}
					} else {
						((AppException) msg.obj).makeToast(ArticleDetail.this);
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						// 发表评论
						res = ac.pubComment(_catalog, _id, _uid, _content,
								_isPostToMyZone);
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

	/**
	 * 注册双击全屏事件
	 */
	private void regOnDoubleEvent() {
		gd = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						isFullScreen = !isFullScreen;
						if (!isFullScreen) {
							WindowManager.LayoutParams params = getWindow()
									.getAttributes();
							params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
							getWindow().setAttributes(params);
							getWindow()
									.clearFlags(
											WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
							//mHeader.setVisibility(View.VISIBLE);
							mFooter.setVisibility(View.VISIBLE);
						} else {
							WindowManager.LayoutParams params = getWindow()
									.getAttributes();
							params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							getWindow().setAttributes(params);
							getWindow()
									.addFlags(
											WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
							//mHeader.setVisibility(View.GONE);
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
