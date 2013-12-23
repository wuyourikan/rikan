package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 博客实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Recommend extends Entity {
	
	private String title;
	//private String where;
	private String body;
	private String author;
	//private int authorId;
	//private int documentType;
	private String pubDate;
	private int favorite;
	//private int commentCount;
	private String url;
	
	//public int getCommentCount() {
		//return commentCount;
	//}
	//public void setCommentCount(int commentCount) {
		//this.commentCount = commentCount;
	//}
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
	public String getPubDate() {
		return pubDate;
	}	
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	//public String getWhere() {
		//return where;
	//}
	//public void setWhere(String where) {
		//this.where = where;
	//}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	//public int getAuthorId() {
		//return authorId;
	//}
	//public void setAuthorId(int authorId) {
		//this.authorId = authorId;
	//}
	//public int getDocumentType() {
		//return documentType;
	//}
	//public void setDocumentType(int documentType) {
		//this.documentType = documentType;
	//}
	
	public static Recommend parse(InputStream inputStream) throws IOException, AppException {
		Recommend recommend = null;
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
			    		if(tag.equalsIgnoreCase("recommend"))
			    		{
			    			recommend = new Recommend();
			    		}  
			    		else if(recommend != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	recommend.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("title"))
				            {			            	
				            	recommend.setTitle(xmlParser.nextText());
				            }
				            //else if(tag.equalsIgnoreCase("where"))
				            //{			            	
				            	//blog.setWhere(xmlParser.nextText());
				            //}
				            else if(tag.equalsIgnoreCase("body"))
				            {			            	
				            	recommend.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	recommend.setAuthor(xmlParser.nextText());		            	
				            }
				            //else if(tag.equalsIgnoreCase("authorid"))
				            //{			            	
				            	//blog.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            //else if(tag.equalsIgnoreCase("documentType"))
				            //{			            	
				            	//recommend.setDocumentType(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	recommend.setPubDate(xmlParser.nextText());            	
				            }
				            else if(tag.equalsIgnoreCase("favorite"))
				            {			            	
				            	recommend.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            //else if(tag.equalsIgnoreCase("commentCount"))
				            //{			            	
				            	//blog.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            //}
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	recommend.setUrl(xmlParser.nextText());
				            }
				            //通知信息
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	recommend.setNotice(new Notice());
				    		}
				            else if(recommend.getNotice() != null)
				    		{
				    			if(tag.equalsIgnoreCase("atmeCount"))
					            {			      
				    				recommend.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("msgCount"))
					            {			            	
					            	recommend.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("reviewCount"))
					            {			            	
					            	recommend.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("newFansCount"))
					            {			            	
					            	recommend.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
				    		}
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		    		
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
        return recommend;       
	}
}
