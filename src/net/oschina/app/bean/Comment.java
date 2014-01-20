package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.oschina.app.AppException;
import net.oschina.app.api.ApiClient;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;
import net.oschina.app.common.UIHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 评论实体类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class Comment extends BaseItem {
	
	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;
	
	public final static String NODE_START = "comment";
	public final static String NODE_AUTHOR = "author";	
	public final static String NODE_AUTHORID = "authorId";
	public final static String NODE_FACE = "portrait";
	public final static String NODE_CONTENT = "content";
	public final static String NODE_PUBDATE = "pubDate";	
	public final static String NODE_APPCLIENT = "appClient";
	
	private String face;
	private String content;
	private String author;
	private int authorId;
	private String pubDate;
	private int appClient;
	private List<Reply> replies = new ArrayList<Reply>();
	private List<Refer> refers = new ArrayList<Refer>();
	
	public static class Reply implements Serializable{
		public final static String NODE_START = "reply";
		public final static String NODE_AUTHOR = "rauthor";
		public final static String NODE_PUBDATE = "rpubDate";	
		public final static String NODE_CONTENT = "rcontent";
		
		public String rauthor;
		public String rpubDate;
		public String rcontent;
	} 
	
	public static class Refer implements Serializable{
		public final static String NODE_START = "refer";
		public final static String NODE_TITLE = "refertitle";
		public final static String NODE_BODY = "referbody";	
		
		public String refertitle;
		public String referbody;
	}
	
	public int getAppClient() {
		return appClient;
	}
	public void setAppClient(int appClient) {
		this.appClient = appClient;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public List<Reply> getReplies() {
		return replies;
	}
	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}	
	public List<Refer> getRefers() {
		return refers;
	}
	public void setRefers(List<Refer> refers) {
		this.refers = refers;
	}
	
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "comment_" + map.get("id");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.COMMENT);
		//return ApiClient.makeURL(URLs.GET_ITEM, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_ITEM, map);
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_ITEM;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		//Comment comm = null;
		boolean gotNodeStart = false;
		Reply reply = null;
		Refer refer = null;
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
			    		if(tag.equalsIgnoreCase(NODE_START)){
			    			gotNodeStart = true;
			    		}
			    		else if(gotNodeStart){	
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	this.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(NODE_FACE)){			            	
				            	this.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR)){			            	
				            	this.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHORID)){			            	
				            	this.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_CONTENT)){			            	
				            	this.setContent(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE)){			            	
				            	this.setPubDate(xmlParser.nextText());	            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_APPCLIENT)){			            	
				            	this.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Reply.NODE_START)){			            	
				            	reply = new Reply();         	
				            }
				            else if(reply!=null){
				            	if(tag.equalsIgnoreCase(Reply.NODE_AUTHOR)){
					            	reply.rauthor = xmlParser.nextText();
					            }
					            else if(tag.equalsIgnoreCase(Reply.NODE_PUBDATE)){
					            	reply.rpubDate = xmlParser.nextText();
					            }
					            else if(tag.equalsIgnoreCase(Reply.NODE_CONTENT)){
					            	reply.rcontent = xmlParser.nextText();
					            }
				            }
				            else if(tag.equalsIgnoreCase(Refer.NODE_START)){			            	
				            	refer = new Refer();         	
				            }
				            else if(refer!=null){
				            	if(tag.equalsIgnoreCase(Refer.NODE_TITLE)){
					            	refer.refertitle = xmlParser.nextText();
					            }
					            else if(tag.equalsIgnoreCase(Refer.NODE_BODY)){
					            	refer.referbody = xmlParser.nextText();
					            }
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
			    		if(tag.equalsIgnoreCase(NODE_START))//如果遇到结束符则停止遍历XML
			    			return; 
			    		//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase(Reply.NODE_START) && gotNodeStart && reply!=null) { 
				       		this.getReplies().add(reply);
				       		reply = null; 
				       	}
				       	else if(tag.equalsIgnoreCase(Refer.NODE_START) && gotNodeStart && refer!=null) {
				       		this.getRefers().add(refer);
				       		refer = null;
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
