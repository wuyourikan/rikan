package net.oschina.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewSearchAdapter;
import net.oschina.app.bean.BaseItem;
import net.oschina.app.bean.SearchList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.SearchList.SearchResult;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;
import net.oschina.app.common.UIHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 搜索
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Search extends BaseActivity{
	private Button mSearchBtn;
	private EditText mSearchEditer;
	private ProgressBar mProgressbar;
	
	private Button search_type_article;
	private Button search_type_topic;
	private Button search_type_special;
	
	private ListView mlvSearch;
	private ListViewSearchAdapter lvSearchAdapter;
	private List<BaseItem> lvSearchData = new ArrayList<BaseItem>();
	private View lvSearch_footer;
	private TextView lvSearch_foot_more;
	private ProgressBar lvSearch_foot_progress; 
    private Handler mSearchHandler;
    private int lvSumData;
	
	private int curSearchType = TypeHelper.ARTICLE;
	private int curLvDataState;
	private String curSearchContent = "";
    
	private InputMethodManager imm;
	
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        this.initView();
        
        this.initData();
	}
	
    /**
     * 头部按钮展示
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
    	case DATA_LOAD_ING:
    		mSearchBtn.setClickable(false);
			mProgressbar.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_COMPLETE:
			mSearchBtn.setClickable(true);
			mProgressbar.setVisibility(View.GONE);
			break;
		}
    }
	
	//初始化视图控件
    private void initView()
    {
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mSearchBtn = (Button)findViewById(R.id.search_btn);
    	mSearchEditer = (EditText)findViewById(R.id.search_editer);
    	mProgressbar = (ProgressBar)findViewById(R.id.search_progress);
    	
    	mSearchBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mSearchEditer.clearFocus();
				curSearchContent = mSearchEditer.getText().toString();
				loadLvSearchData(curSearchType, 0, mSearchHandler, UIHelper.LISTVIEW_ACTION_INIT);
			}
		});
    	mSearchEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){  
					imm.showSoftInput(v, 0);  
		        }  
		        else{  
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		        }  
			}
		}); 
    	mSearchEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH) {
					if(v.getTag() == null) {
						v.setTag(1);
						mSearchEditer.clearFocus();
						curSearchContent = mSearchEditer.getText().toString();
						loadLvSearchData(curSearchType, 0, mSearchHandler, UIHelper.LISTVIEW_ACTION_INIT);						
					}else{
						v.setTag(null);
					}
					return true;
				}
				return false;
			}
		});
    	
    	search_type_article = (Button)findViewById(R.id.search_type_article);
    	search_type_topic = (Button)findViewById(R.id.search_type_topic);
    	search_type_special = (Button)findViewById(R.id.search_type_special);
    	
    	search_type_article.setOnClickListener(this.searchBtnClick(search_type_article,TypeHelper.ARTICLE));
    	search_type_topic.setOnClickListener(this.searchBtnClick(search_type_topic,TypeHelper.TOPIC));
    	search_type_special.setOnClickListener(this.searchBtnClick(search_type_special,TypeHelper.SPECIAL));
    
    	search_type_article.setEnabled(false);
    	
    	lvSearch_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvSearch_foot_more = (TextView)lvSearch_footer.findViewById(R.id.listview_foot_more);
    	lvSearch_foot_progress = (ProgressBar)lvSearch_footer.findViewById(R.id.listview_foot_progress);

    	lvSearchAdapter = new ListViewSearchAdapter(this, lvSearchData, R.layout.search_listitem); 
    	mlvSearch = (ListView)findViewById(R.id.search_listview);
    	mlvSearch.setVisibility(ListView.GONE);
    	mlvSearch.addFooterView(lvSearch_footer);//添加底部视图  必须在setAdapter前
    	mlvSearch.setAdapter(lvSearchAdapter); 
    	mlvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击底部栏无效
        		if(view == lvSearch_footer) return;
        		
        		SearchResult res = null;
        		//判断是否是TextView
        		if(view instanceof TextView){
        			res = (SearchResult)view.getTag();
        		}else{
        			TextView title = (TextView)view.findViewById(R.id.search_listitem_title);
        			res = (SearchResult)title.getTag();
        		} 
        		if(res == null) return;
        		
        		//跳转
        		UIHelper.showUrlRedirect(view.getContext(), res.getUrl());
        	}
		});
    	mlvSearch.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {				
				//数据为空--不用继续下面代码了
				if(lvSearchData.size() == 0) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvSearch_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				if(scrollEnd && curLvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					mlvSearch.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvSearch_foot_more.setText(R.string.load_ing);
					lvSearch_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvSumData/20;
					loadLvSearchData(curSearchType, pageIndex, mSearchHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			}
		});
    }
    
    //初始化控件数据
  	private void initData()
  	{			
		mSearchHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);

				if(msg.what >= 0){						
					SearchList list = (SearchList)msg.obj;
					Notice notice = list.getNotice();
					//处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						lvSumData = msg.what;
						lvSearchData.clear();//先清除原有数据
						lvSearchData.addAll(list.getList());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if(lvSearchData.size() > 0){
							for(BaseItem res1 : list.getList()){
								boolean b = false;
								for(BaseItem res2 : lvSearchData){
									if(res1.getId() == res2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvSearchData.add(res1);
							}
						}else{
							lvSearchData.addAll(list.getList());
						}
						break;
					}	
					
					if(msg.what < 20){
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvSearchAdapter.notifyDataSetChanged();
						lvSearch_foot_more.setText(R.string.load_full);
					}else if(msg.what == 20){					
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvSearchAdapter.notifyDataSetChanged();
						lvSearch_foot_more.setText(R.string.load_more);
					}
					//发送通知广播
					if(notice != null){
						UIHelper.sendBroadCast(Search.this, notice);
					}
				}
				else if(msg.what == -1){
					//有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvSearch_foot_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(Search.this);
				}
				if(lvSearchData.size()==0){
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvSearch_foot_more.setText(R.string.load_empty);
				}
				lvSearch_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
					mlvSearch.setSelection(0);//返回头部
				}
			}
		};
  	}
  	
    /**
     * 线程加载收藏数据
     * @param type 0:全部收藏 1:文章 2:话题 3:专题
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvSearchData(final int type,final int pageIndex,final Handler handler,final int action){  
		if(StringUtils.isEmpty(curSearchContent)){
			UIHelper.ToastMessage(Search.this, "请输入搜索内容");
			return;
		}
		
		headButtonSwitch(DATA_LOAD_ING);
		mlvSearch.setVisibility(ListView.VISIBLE);
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					final SearchList searchList = new SearchList(); 
					
					Map<String, Object> httpPostPara = new HashMap<String, Object>(){{
						put("type", type);
						put("content", curSearchContent);
						put("pageIndex", pageIndex);
						put("pageSize", searchList.getMaxPageSize());
					}};
					
					((AppContext)getApplication()).getList(searchList, httpPostPara, true, null, null);
					msg.what = searchList.getPageSize();
					msg.obj = searchList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
				if(curSearchType == type)
					handler.sendMessage(msg);
			}
		}.start();
	} 
	
	private View.OnClickListener searchBtnClick(final Button btn,final int type){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == search_type_article)
		    		search_type_article.setEnabled(false);
		    	else
		    		search_type_article.setEnabled(true);
		    	if(btn == search_type_topic)
		    		search_type_topic.setEnabled(false);
		    	else
		    		search_type_topic.setEnabled(true);	
		    	if(btn == search_type_special)
		    		search_type_special.setEnabled(false);
		    	else
		    		search_type_special.setEnabled(true);

				//开始搜索
				mSearchEditer.clearFocus();
				curSearchContent = mSearchEditer.getText().toString();
				curSearchType = type;
				loadLvSearchData(type, 0, mSearchHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);		    	
			}
		};
    }
}
