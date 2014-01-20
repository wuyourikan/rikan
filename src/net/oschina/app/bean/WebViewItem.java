package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

/**
 * 使用webview的列表Item类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014/1/1
 */
public abstract class WebViewItem extends BaseItem{
	
	/* XML文件的标签名 */
	public final static String NODE_TITLE = "title";	//标题
	public final static String NODE_URL = "url";		//地址
	public final static String NODE_BODY = "body";		//HTML内容
	//public final static String NODE_CATALOG = "catalog";	//栏目类型
	public final static String NODE_AUTHOR = "author";	//作者名
	public final static String NODE_PUBDATE = "pubDate";//发文日期
	//public final static String NODE_AUTHORID = "authorid";		//作者id
	//public final static String NODE_COMMENTCOUNT = "commentCount";//评论数
	//public final static String NODE_VIEWCOUNT = "viewCount";		//浏览数
	//public final static String NODE_TAG_START = "tags"	
	//public final static String NODE_TAG =	"tag"	
	public final static String NODE_FAVORITE = "favorite";//是否收藏
	//public final static String NODE_START = "";			//XML开始标签

	protected String 	title;
	protected String 	url;
	protected String 	body;
	//protected int 	catalog;
	protected String 	author;
	protected String 	pubDate;
	protected int 		favorite;
	//private int 		authorId;
	//private int 		commentCount;
	//private int 		viewCount;
	//private List<String> tags;
	//private List<Relative> relatives;
	
	//相关话题内部类
	/*public static class Relative implements Serializable{
	  	public final static String NODE_START 	= "relative";	//XML中某一相关话题对应的标签名
		public final static String NODE_TITLE 	= "rtitle";		//相关话题标题名
		public final static String NODE_URL 	= "rurl";		//相关话题链接
		public final static String NODE_PUBDATE = "rpubDate";	//相关话题发布日期
		//public final static String NODE_IMG 	= "rimg";		//相关话题图片
		
		public String title;
		public String url;
		public String pubDate;
		//public String img;
	} 
	
	public List<Relative> getRelatives() {
		return relatives;
	}
	public void setRelatives(List<Relative> relatives) {
		this.relatives = relatives;
	}*/
	
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	public String getPubDate() {
		return this.pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	/*public int getCatalog() {
		return this.catalog;
	}
	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}*/
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/*public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public int getCommentCount() {
		/return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public List<String> getTags() {
	return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}*/

/*	public void parse(InputStream inputStream) throws IOException, AppException {
		boolean gotNodeStart = false;
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
			    		else if (gotNodeStart) {
				            else if(tag.equalsIgnoreCase(NODE_TITLE)){			            	
				            	this.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_URL)){			            	
				            	this.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_BODY)){			            	
				            	this.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR)){			            	
				            	this.setAuthor(xmlParser.nextText());		            	
				            }
							else if(tag.equalsIgnoreCase(WebViewItem.NODE_AUTHORID)){			            	
				            	this.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
			            	else if(tag.equalsIgnoreCase(WebViewItem.NODE_ANSWERCOUNT)){			            	
				            	this.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_VIEWCOUNT)){			            	
				            	this.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE)){			            	
				            	this.setPubDate(xmlParser.nextText());
				            }
							else if(tag.equalsIgnoreCase(WebViewItem.NODE_FAVORITE)){			            	
				            	this.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            //标签
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_TAG_START)){
				            	this.tags = new ArrayList<String>();
				            }
				            else if(this.getTags() != null && tag.equalsIgnoreCase(WebViewItem.NODE_TAG)){
				            	this.getTags().add(xmlParser.nextText());
				    		}
				            //相关链接
							else if(tag.equalsIgnoreCase(Relative.NODE_START)){			            	
				            	relative = new Relative();
				            }
				            else if(relative != null){			            	
				            	if(tag.equalsIgnoreCase(Relative.NODE_TITLE)){
				            		relative.title = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase(Relative.NODE_URL)){
				            		relative.url = xmlParser.nextText(); 	
				            	}
			            		else if(tag.equalsIgnoreCase(Relative.NODE_PUBDATE)){
				            		relative.pubDate = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase(Relative.NODE_IMG)){
				            		relative.img = xmlParser.nextText(); 	
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
				            
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		    		
			    		if (tag.equalsIgnoreCase("relative") && gotNodeStart && relative!=null) { 
				       		this.getRelatives().add(relative);
				       		relative = null; 
			    		}
			    		if(tag.equalsIgnoreCase(NODE_START))
			    			return; //如果遇到结束符则停止遍历XML
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
	}*/
}
