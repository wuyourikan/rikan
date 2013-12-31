package net.oschina.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Zatan;
import net.oschina.app.bean.BlogCommentList;
import net.oschina.app.bean.ZatanList;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.FriendList;
import net.oschina.app.bean.Huati;
import net.oschina.app.bean.HuatiList;
import net.oschina.app.bean.MyInformation;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Recommend;
import net.oschina.app.bean.RecommendList;
import net.oschina.app.bean.All;
import net.oschina.app.bean.AllList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.SearchList;
import net.oschina.app.bean.User;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.bean.Zhuanti;
import net.oschina.app.bean.ZhuantiList;
import net.oschina.app.common.CyptoUtils;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.ImageUtils;
import net.oschina.app.common.MethodsCompat;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.webkit.CacheManager;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	private boolean login = false;	//登录状态
	private int loginUid = 0;	//登录用户的id
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private Handler unLoginHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				//UIHelper.showLoginDialog(AppContext.this);
			}
		}		
	};

	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 应用程序是否发出提示音
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 用户是否登录
	 * @return
	 */
	/*public boolean isLogin() {
		return login;
	}*/
	
	/**
	 * 获取登录用户id
	 * @return
	 */
	/*public int getLoginUid() {
		return this.loginUid;
	}*/
	
	/**
	 * 用户注销
	 */
	/*public void Logout() {
		ApiClient.cleanCookie();
		this.cleanCookie();
		this.login = false;
		this.loginUid = 0;
	}*、
	
	/**
	 * 未登录或修改密码后的处理
	 */
	/*public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}*/
	
	/**
	 * 初始化用户登录信息
	 */
	/*public void initLoginInfo() {
		User loginUser = getLoginInfo();
		if(loginUser!=null && loginUser.getUid()>0 && loginUser.isRememberMe()){
			this.loginUid = loginUser.getUid();
			this.login = true;
		}else{
			this.Logout();
		}
	}*/
	
	/**
	 * 用户登录验证
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	/*public User loginVerify(String account, String pwd) throws AppException {
		return ApiClient.login(this, account, pwd);
	}*/
	
	/**
	 * 我的个人资料
	 * @param isRefresh 是否主动刷新
	 * @return
	 * @throws AppException
	 */
	public MyInformation getMyInformation(boolean isRefresh) throws AppException {
		MyInformation myinfo = null;
		String key = "myinfo_"+loginUid;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				myinfo = ApiClient.myInformation(this, loginUid);
				if(myinfo != null && myinfo.getName().length() > 0){
					Notice notice = myinfo.getNotice();
					myinfo.setNotice(null);
					saveObject(myinfo, key);
					myinfo.setNotice(notice);
				}
			}catch(AppException e){
				myinfo = (MyInformation)readObject(key);
				if(myinfo == null)
					throw e;
			}
		} else {
			myinfo = (MyInformation)readObject(key);
			if(myinfo == null)
				myinfo = new MyInformation();
		}
		return myinfo;
	}	
	
	/**
	 * 获取用户信息个人专页（包含该用户的动态信息以及个人信息）
	 * @param uid 自己的uid
	 * @param hisuid 被查看用户的uid
	 * @param hisname 被查看用户的用户名
	 * @param pageIndex 页面索引
	 * @return
	 * @throws AppException
	 */
	public UserInformation getInformation(int uid, int hisuid, String hisname, int pageIndex, boolean isRefresh) throws AppException {
		String _hisname = ""; 
		if(!StringUtils.isEmpty(hisname)){
			_hisname = hisname;
		}
		UserInformation userinfo = null;
		String key = "userinfo_"+uid+"_"+hisuid+"_"+(URLEncoder.encode(hisname))+"_"+pageIndex+"_"+PAGE_SIZE; 
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {			
			try{
				userinfo = ApiClient.information(this, uid, hisuid, _hisname, pageIndex, PAGE_SIZE);
				if(userinfo != null && pageIndex == 0){
					Notice notice = userinfo.getNotice();
					userinfo.setNotice(null);
					saveObject(userinfo, key);
					userinfo.setNotice(notice);
				}
			}catch(AppException e){
				userinfo = (UserInformation)readObject(key);
				if(userinfo == null)
					throw e;
			}
		} else {
			userinfo = (UserInformation)readObject(key);
			if(userinfo == null)
				userinfo = new UserInformation();
		}
		return userinfo;
	}
	
	/**
	 * 更新用户之间关系（加关注、取消关注）
	 * @param uid 自己的uid
	 * @param hisuid 对方用户的uid
	 * @param newrelation 0:取消对他的关注 1:关注他
	 * @return
	 * @throws AppException
	 */
	public Result updateRelation(int uid, int hisuid, int newrelation) throws AppException {
		return ApiClient.updateRelation(this, uid, hisuid, newrelation);
	}
	
	/**
	 * 更新用户头像
	 * @param portrait 新上传的头像
	 * @return
	 * @throws AppException
	 */
	public Result updatePortrait(File portrait) throws AppException {
		return ApiClient.updatePortrait(this, loginUid, portrait);
	}
	
	/**
	 * 清空通知消息
	 * @param uid
	 * @param type 1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 * @return
	 * @throws AppException
	 */
	public Result noticeClear(int uid, int type) throws AppException {
		return ApiClient.noticeClear(this, uid, type);
	}
	
	/**
	 * 获取用户通知信息
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public Notice getUserNotice(int uid) throws AppException {
		return ApiClient.getUserNotice(this, uid);
	}
	
	/**
	 * 用户收藏列表
	 * @param type 0:全部收藏 1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @param pageIndex 页面索引 0表示第一页
	 * @return
	 * @throws AppException
	 */
	public FavoriteList getFavoriteList(int type, int pageIndex, boolean isRefresh) throws AppException {
		FavoriteList list = null;
		String key = "favoritelist_"+loginUid+"_"+type+"_"+pageIndex+"_"+PAGE_SIZE; 
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getFavoriteList(this, loginUid, type, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (FavoriteList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (FavoriteList)readObject(key);
			if(list == null)
				list = new FavoriteList();
		}
		return list;
	}
	
	/**
	 * 用户粉丝、关注人列表
	 * @param relation 0:显示自己的粉丝 1:显示自己的关注者
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public FriendList getFriendList(int relation, int pageIndex, boolean isRefresh) throws AppException {
		FriendList list = null;
		String key = "friendlist_"+loginUid+"_"+relation+"_"+pageIndex+"_"+PAGE_SIZE; 
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getFriendList(this, loginUid, relation, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (FriendList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (FriendList)readObject(key);
			if(list == null)
				list = new FriendList();
		}
		return list;
	}
	
	/**
	 * 推荐列表
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ApiException
	 */
	public RecommendList getRecommendList(int catalog, int pageIndex, boolean isRefresh) throws AppException {                                  //此处注意
		RecommendList list = null;
		String key = "recommendlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getRecommendList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (RecommendList)readObject(key);
				if(list == null)
					throw e;
			}		
		} else {
			list = (RecommendList)readObject(key);
			if(list == null)
				list = new RecommendList();
		}
		return list;
	}
	
	/**
	 * 推荐详情
	 * @param news_id
	 * @return
	 * @throws ApiException
	 */
	public Recommend getRecommend(int recommend_id, boolean isRefresh) throws AppException {		
		Recommend recommend = null;
		String key = "recommend_"+recommend_id;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				recommend = ApiClient.getRecommendDetail(this, recommend_id);
				if(recommend != null){
					Notice notice = recommend.getNotice();
					recommend.setNotice(null);
					saveObject(recommend, key);
					recommend.setNotice(notice);
				}
			}catch(AppException e){
				recommend = (Recommend)readObject(key);
				if(recommend == null)
					throw e;
			}
		} else {
			recommend = (Recommend)readObject(key);
			if(recommend == null)
				recommend = new Recommend();
		}
		return recommend;		
	}
	
	/**
	 * 新闻列表
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ApiException
	 */
	public NewsList getNewsList(int catalog, int pageIndex, boolean isRefresh) throws AppException {                                  //此处注意
		NewsList list = null;
		String key = "newslist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getNewsList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (NewsList)readObject(key);
				if(list == null)
					throw e;
			}		
		} else {
			list = (NewsList)readObject(key);
			if(list == null)
				list = new NewsList();
		}
		return list;
	}
	
	/**
	 * 新闻详情
	 * @param news_id
	 * @return
	 * @throws ApiException
	 */
	public News getNews(int news_id, boolean isRefresh) throws AppException {		
		News news = null;
		String key = "news_"+news_id;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				news = ApiClient.getNewsDetail(this, news_id);
				if(news != null){
					Notice notice = news.getNotice();
					news.setNotice(null);
					saveObject(news, key);
					news.setNotice(notice);
				}
			}catch(AppException e){
				news = (News)readObject(key);
				if(news == null)
					throw e;
			}
		} else {
			news = (News)readObject(key);
			if(news == null)
				news = new News();
		}
		return news;		
	}
	
	/**
	 * 用户博客列表
	 * @param authoruid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public ZatanList getUserBlogList(int authoruid, String authorname, int pageIndex, boolean isRefresh) throws AppException {
		ZatanList list = null;
		String key = "userbloglist_"+authoruid+"_"+(URLEncoder.encode(authorname))+"_"+loginUid+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getUserBlogList(this, authoruid, authorname, loginUid, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (ZatanList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (ZatanList)readObject(key);
			if(list == null)
				list = new ZatanList();
		}
		return list;
	}
	
	/**
	 * 杂谈列表
	 * @param type 全部：all 最新：latest
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public ZatanList getZatanList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		ZatanList list = null;
		String key = "zatanlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getZatanList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (ZatanList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (ZatanList)readObject(key);
			if(list == null)
				list = new ZatanList();
		}
		return list;
	}
	
	/**
	 * 杂谈详情
	 * @param blog_id
	 * @return
	 * @throws AppException
	 */
	public Zatan getZatan(int zatan_id, boolean isRefresh) throws AppException {
		Zatan zatan = null;
		String key = "zatan_"+zatan_id;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				zatan = ApiClient.getZatanDetail(this, zatan_id);
				if(zatan != null){
					Notice notice = zatan.getNotice();
					zatan.setNotice(null);
					saveObject(zatan, key);
					zatan.setNotice(notice);
				}
			}catch(AppException e){
				zatan = (Zatan)readObject(key);
				if(zatan == null)
					throw e;
			}
		} else {
			zatan = (Zatan)readObject(key);
			if(zatan == null)
				zatan = new Zatan();
		}
		return zatan;
	}
	
	/**
	 * 全部列表
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ApiException
	 */
	public AllList getAllList(int catalog, int pageIndex, boolean isRefresh) throws AppException {                                  //此处注意
		AllList list = null;
		String key = "alllist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getAllList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (AllList)readObject(key);
				if(list == null)
					throw e;
			}		
		} else {
			list = (AllList)readObject(key);
			if(list == null)
				list = new AllList();
		}
		return list;
	}
	
	/**
	 * 全部详情
	 * @param news_id
	 * @return
	 * @throws ApiException
	 */
	public All getAll(int all_id, boolean isRefresh) throws AppException {		
		All all = null;
		String key = "all_"+all_id;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				all = ApiClient.getAllDetail(this, all_id);
				if(all != null){
					Notice notice = all.getNotice();
					all.setNotice(null);
					saveObject(all, key);
					all.setNotice(notice);
				}
			}catch(AppException e){
				all = (All)readObject(key);
				if(all == null)
					throw e;
			}
		} else {
			all = (All)readObject(key);
			if(all == null)
				all = new All();
		}
		return all;		
	}
	
	/**
	 * 话题列表
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public HuatiList getHuatiList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		HuatiList list = null;
		String key = "postlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {		
			try{
				list = ApiClient.getHuatiList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (HuatiList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (HuatiList)readObject(key);
			if(list == null)
				list = new HuatiList();
		}
		return list;
	}
	
	/**
	 * Tag相关话题列表
	 * @param tag
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public HuatiList getHuatiListByTag(String tag, int pageIndex, boolean isRefresh) throws AppException {
		HuatiList list = null;
		String key = "postlist_"+(URLEncoder.encode(tag))+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {		
			try{
				list = ApiClient.getHuatiListByTag(this, tag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (HuatiList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (HuatiList)readObject(key);
			if(list == null)
				list = new HuatiList();
		}
		return list;
	}
	
	/**
	 * 读取话题详情
	 * @param post_id
	 * @return
	 * @throws ApiException
	 */
	public Huati getHuati(int post_id, boolean isRefresh) throws AppException {		
		Huati huati = null;
		String key = "post_"+post_id;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {	
			try{
				huati = ApiClient.getHuatiDetail(this, post_id);
				if(huati != null){
					Notice notice = huati.getNotice();
					huati.setNotice(null);
					saveObject(huati, key);
					huati.setNotice(notice);
				}
			}catch(AppException e){
				huati = (Huati)readObject(key);
				if(huati == null)
					throw e;
			}
		} else {
			huati = (Huati)readObject(key);
			if(huati == null)
				huati = new Huati();
		}
		return huati;		
	}
	
	/**
	 * 专题列表
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public ZhuantiList getZhuantiList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		ZhuantiList list = null;
		String key = "postlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {		
			try{
				list = ApiClient.getZhuantiList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (ZhuantiList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (ZhuantiList)readObject(key);
			if(list == null)
				list = new ZhuantiList();
		}
		return list;
	}
	
	/**
	 * Tag相关专题列表
	 * @param tag
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public ZhuantiList getZhuantiListByTag(String tag, int pageIndex, boolean isRefresh) throws AppException {
		ZhuantiList list = null;
		String key = "postlist_"+(URLEncoder.encode(tag))+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {		
			try{
				list = ApiClient.getZhuantiListByTag(this, tag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (ZhuantiList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (ZhuantiList)readObject(key);
			if(list == null)
				list = new ZhuantiList();
		}
		return list;
	}
	
	/**
	 * 读取专题详情
	 * @param post_id
	 * @return
	 * @throws ApiException
	 */
	public Zhuanti getZhuanti(int post_id, boolean isRefresh) throws AppException {		
		Zhuanti zhuanti = null;
		String key = "post_"+post_id;
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {	
			try{
				zhuanti = ApiClient.getZhuantiDetail(this, post_id);
				if(zhuanti != null){
					Notice notice = zhuanti.getNotice();
					zhuanti.setNotice(null);
					saveObject(zhuanti, key);
					zhuanti.setNotice(notice);
				}
			}catch(AppException e){
				zhuanti = (Zhuanti)readObject(key);
				if(zhuanti == null)
					throw e;
			}
		} else {
			zhuanti = (Zhuanti)readObject(key);
			if(zhuanti == null)
				zhuanti = new Zhuanti();
		}
		return zhuanti;		
	}
	
	/**
	 * 博客评论列表
	 * @param id 博客Id
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public BlogCommentList getBlogCommentList(int id, int pageIndex, boolean isRefresh) throws AppException {
		BlogCommentList list = null;
		String key = "blogcommentlist_"+id+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getBlogCommentList(this, id, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (BlogCommentList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (BlogCommentList)readObject(key);
			if(list == null)
				list = new BlogCommentList();
		}
		return list;
	}
	
	/**
	 * 评论列表
	 * @param catalog 1新闻 2帖子 3动弹 4动态
	 * @param id 某条新闻，帖子，动弹的id 或者某条留言的friendid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public CommentList getCommentList(int catalog, int id, int pageIndex, boolean isRefresh) throws AppException {
		CommentList list = null;
		String key = "commentlist_"+catalog+"_"+id+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (!isExistDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getCommentList(this, catalog, id, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (CommentList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (CommentList)readObject(key);
			if(list == null)
				list = new CommentList();
		}
		return list;
	}
	
	/**
	 * 获取搜索列表
	 * @param catalog 全部:all 新闻:news  问答:post 软件:software 博客:blog 代码:code
	 * @param content 搜索的内容
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public SearchList getSearchList(String catalog, String content, int pageIndex, int pageSize) throws AppException {
		return ApiClient.getSearchList(this, catalog, content, pageIndex, pageSize);
	}
	
	/**
	 * 发表评论
	 * @param catalog 1新闻  2帖子  3动弹  4动态
	 * @param id 某条新闻，帖子，动弹的id
	 * @param uid 用户uid
	 * @param content 发表评论的内容
	 * @param isPostToMyZone 是否转发到我的空间  0不转发  1转发
	 * @return
	 * @throws AppException
	 */
	public Result pubComment(int catalog, int id, int uid, String content, int isPostToMyZone) throws AppException {
		return ApiClient.pubComment(this, catalog, id, uid, content, isPostToMyZone);
	}
	
	/**
	 * 
	 * @param id 表示被评论的某条新闻，帖子，动弹的id 或者某条消息的 friendid 
	 * @param catalog 表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态
	 * @param replyid 表示被回复的单个评论id
	 * @param authorid 表示该评论的原始作者id
	 * @param uid 用户uid 一般都是当前登录用户uid
	 * @param content 发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public Result replyComment(int id, int catalog, int replyid, int authorid, int uid, String content) throws AppException {
		return ApiClient.replyComment(this, id, catalog, replyid, authorid, uid, content);
	}
	
	/**
	 * 删除评论
	 * @param id 表示被评论对应的某条新闻,帖子,动弹的id 或者某条消息的 friendid
	 * @param catalog 表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态&留言
	 * @param replyid 表示被回复的单个评论id
	 * @param authorid 表示该评论的原始作者id
	 * @return
	 * @throws AppException
	 */
	public Result delComment(int id, int catalog, int replyid, int authorid) throws AppException {
		return ApiClient.delComment(this, id, catalog, replyid, authorid);
	}
	
	/**
	 * 发表博客评论
	 * @param blog 博客id
	 * @param uid 登陆用户的uid
	 * @param content 评论内容
	 * @return
	 * @throws AppException
	 */
	public Result pubBlogComment(int blog, int uid, String content) throws AppException {
		return ApiClient.pubBlogComment(this, blog, uid, content);
	}
	
	/**
	 * 发表博客评论
	 * @param blog 博客id
	 * @param uid 登陆用户的uid
	 * @param content 评论内容
	 * @param reply_id 评论id
	 * @param objuid 被评论的评论发表者的uid
	 * @return
	 * @throws AppException
	 */
	public Result replyBlogComment(int blog, int uid, String content, int reply_id, int objuid) throws AppException {
		return ApiClient.replyBlogComment(this, blog, uid, content, reply_id, objuid);
	}
	
	/**
	 * 删除博客评论
	 * @param uid 登录用户的uid
	 * @param blogid 博客id
	 * @param replyid 评论id
	 * @param authorid 评论发表者的uid
	 * @param owneruid 博客作者uid
	 * @return
	 * @throws AppException
	 */
	public Result delBlogComment(int uid, int blogid, int replyid, int authorid, int owneruid) throws AppException {
		return ApiClient.delBlogComment(this, uid, blogid, replyid, authorid, owneruid);
	}
	
	/**
	 * 删除博客
	 * @param uid 登录用户的uid
	 * @param authoruid 博客作者uid
	 * @param id 博客id
	 * @return
	 * @throws AppException
	 */
	public Result delBlog(int uid, int authoruid, int id) throws AppException { 	
		return ApiClient.delBlog(this, uid, authoruid, id);
	}
	
	/**
	 * 用户添加收藏
	 * @param uid 用户UID
	 * @param objid 比如是新闻ID 或者问答ID 或者动弹ID
	 * @param type 1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @return
	 * @throws AppException
	 */
	public Result addFavorite(int objid, int type) throws AppException {
		return ApiClient.addFavorite(this, objid, type);
	}
	
	/**
	 * 用户删除收藏
	 * @param uid 用户UID
	 * @param objid 比如是新闻ID 或者问答ID 或者动弹ID
	 * @param type 1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @return
	 * @throws AppException
	 */
	public Result delFavorite(int objid, int type) throws AppException { 	
		return ApiClient.delFavorite(this, objid, type);
	}
	
	/**
	 * 保存登录信息
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo(final User user) {
		this.loginUid = user.getUid();
		this.login = true;
		setProperties(new Properties(){{
			setProperty("user.uid", String.valueOf(user.getUid()));
			setProperty("user.name", user.getName());
			setProperty("user.face", FileUtils.getFileName(user.getFace()));//用户头像-文件名
			setProperty("user.account", user.getAccount());
			setProperty("user.pwd", CyptoUtils.encode("oschinaApp",user.getPwd()));
			setProperty("user.location", user.getLocation());
			setProperty("user.followers", String.valueOf(user.getFollowers()));
			setProperty("user.fans", String.valueOf(user.getFans()));
			setProperty("user.score", String.valueOf(user.getScore()));
			setProperty("user.isRememberMe", String.valueOf(user.isRememberMe()));//是否记住我的信息
		}});		
	}
	
	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty("user.uid","user.name","user.face","user.account","user.pwd",
				"user.location","user.followers","user.fans","user.score","user.isRememberMe");
	}
	
	/**
	 * 获取登录信息
	 * @return
	 */
	public User getLoginInfo() {		
		User lu = new User();		
		lu.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
		lu.setName(getProperty("user.name"));
		lu.setFace(getProperty("user.face"));
		lu.setAccount(getProperty("user.account"));
		lu.setPwd(CyptoUtils.decode("oschinaApp",getProperty("user.pwd")));
		lu.setLocation(getProperty("user.location"));
		lu.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
		lu.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
		lu.setScore(StringUtils.toInt(getProperty("user.score"), 0));
		lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		return lu;
	}
	
	/**
	 * 保存用户头像
	 * @param fileName
	 * @param bitmap
	 */
	public void saveUserFace(String fileName,Bitmap bitmap) {
		try {
			ImageUtils.saveImage(this, fileName, bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户头像
	 * @param key
	 * @return
	 * @throws AppException
	 */
	public Bitmap getUserFace(String key) throws AppException {
		FileInputStream fis = null;
		try{
			fis = openFileInput(key);
			return BitmapFactory.decodeStream(fis);
		}catch(Exception e){
			throw AppException.run(e);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 是否加载显示文章图片
	 * @return
	 */
	public boolean isLoadImage()
	{
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		//默认是加载的
		if(StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}
	
	/**
	 * 设置是否加载文章图片
	 * @param b
	 */
	public void setConfigLoadimage(boolean b)
	{
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}
	
	/**
	 * 是否发出提示音
	 * @return
	 */
	public boolean isVoice()
	{
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		//默认是开启提示声音
		if(StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}
	
	/**
	 * 设置是否发出提示音
	 * @param b
	 */
	public void setConfigVoice(boolean b)
	{
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}
	
	/**
	 * 是否左右滑动
	 * @return
	 */
	public boolean isScroll()
	{
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		//默认是关闭左右滑动
		if(StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}
	
	/**
	 * 设置是否左右滑动
	 * @param b
	 */
	public void setConfigScroll(boolean b)
	{
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}
	
	/**
	 * 是否Https登录
	 * @return
	 */
	public boolean isHttpsLogin()
	{
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		//默认是http
		if(StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}
	
	/**
	 * 设置是是否Https登录
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b)
	{
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}
	
	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)                   //手机上的缓存文件
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile)
	{
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 清除app缓存
	 */
	public void clearAppCache()
	{
		//清除webview缓存
		File file = CacheManager.getCacheFileBaseDir();  
		if (file != null && file.exists() && file.isDirectory()) {  
		    for (File item : file.listFiles()) {  
		    	item.delete();  
		    }  
		    file.delete();  
		}  		  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
		//清除编辑器保存的临时内容
		Properties props = getProperties();
		for(Object key : props.keySet()) {
			String _key = key.toString();
			if(_key.startsWith("temp"))
				removeProperty(_key);
		}
	}	
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 将对象保存到内存缓存中
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}
	
	/**
	 * 从内存缓存中获取对象
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key){
		return memCacheRegion.get(key);
	}
	
	/**
	 * 保存磁盘缓存
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}

	public boolean containsProperty(String key){
		Properties props = getProperties();
		 return props.containsKey(key);
	}
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}	
}
