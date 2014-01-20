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
 * 专题实体类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-3
 */
public class Special extends BaseItem{
	
	public final static String NODE_TITLE = "title";		//列表标题
	public final static String NODE_IMG = "img";		//列表图/详细置顶图
	public final static String NODE_INTRO = "intro";	//摘要
	public final static String NODE_URL = "url";	//专题URL
	public final static String NODE_FAVORITE = "favorite";	//专题URL
	public final static String NODE_START = "special"; //XML中某一话题对应的标签名

	private String title;
	private String img;
	private String intro;
	private String url;		//用于分享此专题
	private int favorite;	//是否收藏 0-否 1-是
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "special_" + map.get("id");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.SPECIAL);
		//return ApiClient.makeURL(URLs.GET_ITEM, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_ITEM, map);
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_ITEM;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		boolean gotNodeStart = false;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType=xmlParser.getEventType();
			//一直循环，直到文档结束    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); //获得标签名
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase(NODE_START)){
			    			gotNodeStart = true;
			    		}
			    		else if(gotNodeStart){	
				    		if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				    			this.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				    		else if(tag.equalsIgnoreCase(NODE_URL)) {
				    			this.url = xmlParser.nextText();
				    		}
				    		else if(tag.equalsIgnoreCase(NODE_IMG)) {
				    			this.img = xmlParser.nextText();
				    		}
				    		else if(tag.equalsIgnoreCase(NODE_TITLE)) {
				    			this.title = xmlParser.nextText();
				    		}
				    		else if(tag.equalsIgnoreCase(NODE_INTRO)) {
				    			this.intro = xmlParser.nextText();
				    		}
				    		else if(tag.equalsIgnoreCase(NODE_FAVORITE)) {
				    			this.favorite = StringUtils.toInt(xmlParser.nextText());
				    		}
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
			    		if(tag.equalsIgnoreCase(NODE_START))
			    			return; //如果遇到结束符则停止遍历XML
			    		break;
			    }
			    //如果xml没有结束，则导航到下一个节点
			    evtType = xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
        	e.printStackTrace();
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        } 
	}
}
