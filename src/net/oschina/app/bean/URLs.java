package net.oschina.app.bean;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import android.net.Uri;
import net.oschina.app.common.StringUtils;

/**
 * 接口URL实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {
	
	public final static String HOST = "www.wyzxwk.com";//192.168.1.213  www.oschina.net //www.wyzxwk.com
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;//http://www.wyzxwk.com/
	private final static String URL_MOBILE_HOST = URL_API_HOST + "mobile/";//http://www.wyzxwk.com/
	public final static String LOGIN_VALIDATE_HTTP = HTTP + HOST + URL_SPLITTER + "action/api/login_validate";
	public final static String LOGIN_VALIDATE_HTTPS = HTTPS + HOST + URL_SPLITTER + "action/api/login_validate";
	public final static String GET_LIST = URL_MOBILE_HOST+"getList";  
	public final static String GET_ITEM = URL_MOBILE_HOST+"getItem"; 
	public final static String GET_STATIC_LIST = URL_MOBILE_HOST+"list"; 
	public final static String GET_STATIC_ITEM = URL_MOBILE_HOST+"item"; 
	public final static String SEARCH_LIST = URL_MOBILE_HOST+"searchlist";
//	public final static String TWEET_LIST = URL_API_HOST+"action/api/tweet_list";
//	public final static String TWEET_DETAIL = URL_API_HOST+"action/api/tweet_detail";
//	public final static String TWEET_PUB = URL_API_HOST+"action/api/tweet_pub";
//	public final static String TWEET_DELETE = URL_API_HOST+"action/api/tweet_delete";
//	public final static String ACTIVE_LIST = URL_API_HOST+"action/api/active_list";
//	public final static String MESSAGE_LIST = URL_API_HOST+"action/api/message_list";
//	public final static String MESSAGE_DELETE = URL_API_HOST+"action/api/message_delete";
//	public final static String MESSAGE_PUB = URL_API_HOST+"action/api/message_pub";
	public final static String COMMENT_LIST = URL_API_HOST+"action/api/comment_list";
	public final static String COMMENT_PUB = URL_API_HOST+"action/api/comment_pub";
	public final static String COMMENT_REPLY = URL_API_HOST+"action/api/comment_reply";
	public final static String COMMENT_DELETE = URL_API_HOST+"action/api/comment_delete";	
//	public final static String MY_INFORMATION = URL_API_HOST+"action/api/my_information";
//	public final static String USER_INFORMATION = URL_API_HOST+"action/api/user_information";
//	public final static String USER_UPDATERELATION = URL_API_HOST+"action/api/user_updaterelation";
//	public final static String USER_NOTICE = URL_API_HOST+"action/api/user_notice";
//	public final static String NOTICE_CLEAR = URL_API_HOST+"action/api/notice_clear";
//	public final static String FRIENDS_LIST = URL_API_HOST+"action/api/friends_list";
	public final static String FAVORITE_LIST = URL_API_HOST+"action/api/favorite_list";
	public final static String FAVORITE_ADD = URL_API_HOST+"action/api/favorite_add";
	public final static String FAVORITE_DELETE = URL_API_HOST+"action/api/favorite_delete";
	public final static String PORTRAIT_UPDATE = URL_API_HOST+"action/api/portrait_update";
	public final static String UPDATE_VERSION = URL_API_HOST+"MobileAppVersion.xml";
	
	private final static String URL_HOST = "wyzxwk.com";
	private final static String URL_WWW_HOST = "www."+URL_HOST;
	private final static String URL_MY_HOST = "my."+URL_HOST;
	
	private final static String URL_TYPE_ARTICLE = URL_WWW_HOST + URL_SPLITTER + "Article" + URL_SPLITTER; 	//http://www.wyzxwk.com/Article/
	private final static String URL_TYPE_TOPIC = URL_WWW_HOST + URL_SPLITTER + "topic" + URL_SPLITTER;		//http://www.wyzxwk.com/topic/
	private final static String URL_TYPE_SPECIAL =  URL_WWW_HOST + URL_SPLITTER + "s" + URL_SPLITTER; 		//http://www.wyzxwk.com/s/
	//private final static String URL_TYPE_ZONE = URL_MY_HOST + URL_SPLITTER + "u" + URL_SPLITTER;//my.oschina.net/u/
	//private final static String URL_TYPE_QUESTION_TAG = URL_TYPE_TOPIC + "tag" + URL_SPLITTER;//www.oschina.net/question/tag/
	
	public final static int URL_OBJ_TYPE_OTHER = 0x000;
	public final static int URL_OBJ_TYPE_ARTICLE = 0x001;
	public final static int URL_OBJ_TYPE_TOPIC = 0x002;
	public final static int URL_OBJ_TYPE_SPECIAL = 0x003;
	public final static int URL_OBJ_TYPE_ZONE = 0x004;
	public final static int URL_OBJ_TYPE_TWEET = 0x005;
	//public final static int URL_OBJ_TYPE_TOPIC_TAG = 0x007;
	public static String GET_SEARCH_LIST;
	
	private int objId;
	private int objType = 0;
	private int objModel;
	private String objKey = "";
	
	
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}
	public int getObjModel() {
		return objModel;
	}
	public void setObjModel(int objModel) {
		this.objModel = objModel;
	}
	
	/**
	 * 转化URL为URLs实体
	 * @param path
	 * @return 不能转化的链接返回null
	 */
	public final static URLs parseURL(String path) {
		if(StringUtils.isEmpty(path))return null;
		path = formatURL(path);
		URLs urls = null;
		String objId = "";
		String objKey = "";
		int objType = 0;
		
		try {
			URL url = new URL(path);
			//站内链接
			if(url.getHost().contains(URL_HOST)){//在获得的主站地址中产看是否包含"oschina.net" 
				urls = new URLs();//初始化urls变量
				//www
				if(path.contains(URL_WWW_HOST)){//查看地址中是否包含"www.wyzxwk.com"
					//文章 http://www.wyzxwk.com/Article/guofang/2014/01/312205.html
					if(path.contains(URL_TYPE_ARTICLE)){//查看地址中是否包含 'www.wyzxwk.com/Article/'
						objId = parseObjId(path, URL_TYPE_ARTICLE);
						objType = parseObjType(path, URL_TYPE_ARTICLE);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(objType);
						urls.setObjModel(URL_OBJ_TYPE_ARTICLE);
					}
					//话题  http://www.wyzxwk.com/topic/wenshi/23.html    
					else if(path.contains(URL_TYPE_TOPIC)){
						objId = parseObjId(path, URL_TYPE_TOPIC);
						objType = parseObjType(path, URL_TYPE_ARTICLE);
						urls.setObjId(StringUtils.toInt(objId));					
						urls.setObjType(objType);
						urls.setObjModel(URL_OBJ_TYPE_TOPIC);
					}
					//专题 http://www.wyzxwk.com/s/lsweiwu/
					//http://www.wyzxwk.com/Article/special/shidai/396.html
					//注意！！专题不是ID而是KEY
					else if(path.contains(URL_TYPE_SPECIAL)){
						objKey = parseObjKey(path, URL_TYPE_SPECIAL);
						urls.setObjKey(objKey);
						urls.setObjModel(URL_OBJ_TYPE_SPECIAL);
						/*//问答-标签  http://www.oschina.net/question/tag/python
						if(path.contains(URL_TYPE_QUESTION_TAG)){
							urls.setObjKey(parseObjKey(path, URL_TYPE_QUESTION_TAG));
							urls.setObjType(URL_OBJ_TYPE_QUESTION_TAG);
						}
						//问答  www.oschina.net/question/12_45738
						else{
							objId = parseObjId(path, URL_TYPE_QUESTION);
							String[] _tmp = objId.split(URL_UNDERLINE);
							urls.setObjId(StringUtils.toInt(_tmp[1]));
							urls.setObjType(URL_OBJ_TYPE_QUESTION);
						}*/
					}
					//other
					else{
						urls.setObjKey(path);
						urls.setObjModel(URL_OBJ_TYPE_OTHER);
					}
				}
				//my
				else if(path.contains(URL_MY_HOST)){					
					/*//博客  my.oschina.net/szpengvictor/blog/50879
					if(path.contains(URL_TYPE_ZATAN)){
						objId = parseObjId(path, URL_TYPE_ZATAN);//此时objId为字符串
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_ZATAN);
					}
					//动弹  my.oschina.net/dong706/tweet/612947
					else if(path.contains(URL_TYPE_TWEET)){
						objId = parseObjId(path, URL_TYPE_TWEET);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_TWEET);
					}
					//个人专页  my.oschina.net/u/12
					else if(path.contains(URL_TYPE_ZONE)){
						objId = parseObjId(path, URL_TYPE_ZONE);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_ZONE);
					}
					else{
						//另一种个人专页  my.oschina.net/dong706
						int p = path.indexOf(URL_MY_HOST+URL_SPLITTER) + (URL_MY_HOST+URL_SPLITTER).length();
						String str = path.substring(p);
						if(!str.contains(URL_SPLITTER)){
							urls.setObjKey(str);
							urls.setObjType(URL_OBJ_TYPE_ZONE);
						}
						//other
						else{
							urls.setObjKey(path);
							urls.setObjType(URL_OBJ_TYPE_OTHER);
						}
					}*/
				}
				//other
				else{
					urls.setObjKey(path);
					urls.setObjModel(URL_OBJ_TYPE_OTHER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			urls = null;
		}
		return urls;
	}

	private final static int TypeStrToInt(String type){
		if (type == "Article") {
			return URL_OBJ_TYPE_ARTICLE;
		} else if (type == "topic"){
			return URL_OBJ_TYPE_TOPIC;
		} else if (type == "s"){
			return URL_OBJ_TYPE_SPECIAL;
		} else {
			return URL_OBJ_TYPE_OTHER;
		}
	}
	/**
	 * 解析url获得objId（最后一个/后面的数字）
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_model){
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		String type = parseObjKey(path, url_model);
		
		p = path.indexOf(url_model) + url_model.length() + type.length();//indexof(url_type)返回url_type在path中的开始位置（-1）
		str = path.substring(p);//返回一个新字符串str，它是字符串path的一个子字符串，检索从p开始
		if(str.contains(URL_SPLITTER)){//查看字符串中是否包括/
			tmp = str.split(URL_SPLITTER);//以/分隔字符串str，返回字符串数组
			objId = tmp[tmp.length-1].replaceAll("([0-9]+)\\.html", "$1");
		}else{
			objId = str;
		}
		return objId;
	}
	
	/**
	 * 解析url获得objType (url_model/后面的叫type)
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static int parseObjType(String path, String url_model){
		path = URLDecoder.decode(path);
		String objTypeStr="";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_model) + url_model.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objTypeStr = tmp[0];
		}else{
			objTypeStr = str;
		}
		
		return TypeStrToInt(objTypeStr);
	}
	
	/**
	 * 解析url获得objKey(专题s/后面的叫key) http://www.wyzxwk.com/s/mao120/
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_model){
		path = URLDecoder.decode(path);
		String objKey="";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_model) + url_model.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		
		return objKey;
	}
	
	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}	
}
