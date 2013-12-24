package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 话题实体类
 * @author wyf
 * @version 1.0
 * @created 2013-12-23
 */
public class Huati extends Entity{
	
	/* XML文件的标签名 */
	public final static String NODE_ID = "id";
	public final static String NODE_TITLE = "title";
	public final static String NODE_URL = "url";
	public final static String NODE_IMG = "img";
	public final static String NODE_INTRODUCE = "introduce";//导语
	public final static String NODE_TITLE_H2 = "h2";//导语
	public final static String NODE_TITLE_H3 = "h3";//导语
	public final static String NODE_BODY = "body";
	//public final static String NODE_AUTHORID = "authorid";
	public final static String NODE_AUTHOR = "author";
	public final static String NODE_PUBDATE = "pubDate";
	//public final static String NODE_ANSWERCOUNT = "answerCount";
	//public final static String NODE_VIEWCOUNT = "viewCount";
	//public final static String NODE_FAVORITE = "favorite";
	public final static String NODE_START = "huati"; //XML中某一个话题对应的标签名
	
	/* 话题类型 */
	//public final static int CATALOG_CENTRE = 0;	//话题中心（暂时不需要）
	public final static int CATALOG_SIXIANG = 1;	//思想
	public final static int CATALOG_ZHENGJING = 2;	//政经
	public final static int CATALOG_WENSHI = 3;		//文史
	
	/* 话题实体字段  */
	private String title;
	private String url;
	private String img;
	private String body;
	private String author;
	private int authorId;
	private int answerCount;
	private int viewCount;
	private String pubDate;
	private int catalog;
	private int isNoticeMe;	
	private int favorite;
	private List<String> tags;
	
	
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	public int getCatalog() {
		return catalog;
	}
	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}
	public int getIsNoticeMe() {
		return isNoticeMe;
	}
	public void setIsNoticeMe(int isNoticeMe) {
		this.isNoticeMe = isNoticeMe;
	}
	public String getPubDate() {
		return this.pubDate;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
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
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public int getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	
	public static Huati parse(InputStream inputStream) throws IOException, AppException {
		Huati huati = null;
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
			    		if(tag.equalsIgnoreCase(NODE_START))
			    		{
			    			huati = new Huati();
			    		}
			    		else if(huati != null)
			    		{	
				            if(tag.equalsIgnoreCase(NODE_ID))
				            {			      
				            	huati.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(NODE_TITLE))
				            {			            	
				            	huati.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_URL))
				            {			            	
				            	huati.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_IMG))
				            {			            	
				            	huati.setImg(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_BODY))
				            {			            	
				            	huati.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR))
				            {			            	
				            	huati.setAuthor(xmlParser.nextText());		            	
				            }
				            /*else if(tag.equalsIgnoreCase(NODE_AUTHORID))
				            {			            	
				            	huati.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_ANSWERCOUNT))
				            {			            	
				            	huati.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_VIEWCOUNT))
				            {			            	
				            	huati.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }*/
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE))
				            {			            	
				            	huati.setPubDate(xmlParser.nextText());
				            }
				            /*else if(tag.equalsIgnoreCase(NODE_FAVORITE))
				            {			            	
				            	huati.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }*/
				            //标签
				            else if(tag.equalsIgnoreCase("tags"))
				            {
				            	huati.tags = new ArrayList<String>();
				            }
				            else if(huati.getTags() != null && tag.equalsIgnoreCase("tag"))
				    		{
				            	huati.getTags().add(xmlParser.nextText());
				    		}
				            //通知信息
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	huati.setNotice(new Notice());
				    		}
				            else if(huati.getNotice() != null)
				    		{
				    			if(tag.equalsIgnoreCase("atmeCount"))
					            {			      
				    				huati.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("msgCount"))
					            {			            	
					            	huati.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("reviewCount"))
					            {			            	
					            	huati.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("newFansCount"))
					            {			            	
					            	huati.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
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
        return huati;       
	}
}
