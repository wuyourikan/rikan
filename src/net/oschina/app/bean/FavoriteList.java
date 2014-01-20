package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.oschina.app.AppException;
import net.oschina.app.api.ApiClient;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 收藏列表实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FavoriteList extends BaseList{

	/**
	 * 收藏实体类
	 */
	public static class Favorite extends BaseItem {
		public final static String NODE_START = "favorite";		
		public final static String NODE_TYPE = "type";	//模型类型(0-Artical 1-Huati ...)
		public final static String NODE_TITLE = "title";	//标题
		//public final static String NODE_URL = "url";	//url链接
		
		public int type;
		public String title;
		//public String url;
		
		@Override
		public String getCacheKey(Map<String, Object> map) {return null;}
		public String getHttpGetUrl(Map<String, Object> map){return null;}
		public String getHttpPostUrl(){return null;}
		public void parse(InputStream inputStream) throws IOException, AppException {}
	}
	
	@Override
	public int getMaxPageSize(){
		return 20;
	}
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "favoritelist_" + map.get("catalog")+
				"_" + map.get("pageIndex");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.FAVORITE);
		//return ApiClient.makeURL(URLs.GET_LIST, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_LIST, map);	
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_LIST;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		Favorite favorite = null;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType=xmlParser.getEventType();
			//一直循环，直到文档结束    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase(BaseList.NODE_PAGESIZE)) {
			    			this.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Favorite.NODE_START)) { 
			    			favorite = new Favorite();
			    		}
			    		else if(favorite != null){	
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	favorite.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Favorite.NODE_TYPE)){			            	
				            	favorite.type = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Favorite.NODE_TITLE)){			            	
				            	favorite.title = xmlParser.nextText();		            	
				            }
				            /*else if(tag.equalsIgnoreCase((Favorite.NODE_URL)){			            	
				            	favorite.url = xmlParser.nextText();		            	
				            } */
			    		}
			    		//通知信息
			    		else if(tag.equalsIgnoreCase(Notice.NODE_START)){
			            	this.setNotice(new Notice());
			    		}
			            else if(this.getNotice() != null){
			    			if(tag.equalsIgnoreCase(Notice.NODE_ATMECOUNT)){			      
			    				this.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_MSGCOUNT)){			            	
				            	this.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_REVIEWCOUNT)){			            	
				            	this.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_NEWFANSCOUNT)){			            	
				            	this.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase(Favorite.NODE_START) && favorite != null) { 
				       		this.getList().add(favorite); 
				       		favorite = null; 
				       	}
				       	break; 
			    }
			    //如果xml没有结束，则导航到下一个节点
			    evtType=xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }          
	}
}
