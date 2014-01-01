package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.integer;
import android.util.Xml;

/**
 * 文章实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Artical extends Entity{
	
	public final static String NODE_ID = "id";		//文章id
	public final static String NODE_TITLE = "title";//标题
	public final static String NODE_URL = "url";	//地址
	public final static String NODE_BODY = "body";	//HTML标签
	//public final static String NODE_AUTHORID = "authorid";
	public final static String NODE_AUTHOR = "author";	//作者名
	public final static String NODE_PUBDATE = "pubDate";//发文日期
	//public final static String NODE_COMMENTCOUNT = "commentCount";
	public final static String NODE_FAVORITE = "favorite";//是否收藏
	public final static String NODE_TYPE = "type";		//文章类型
	public final static String NODE_SUBTYPE = "alltype";//子类型
	public final static String NODE_ATTACHMENT = "attachment";
	//public final static String NODE_AUTHORUID2 = "authoruid2";
	public final static String NODE_START = "artical";	//XML开始标签
	
	public final static int CATALOG_NEWS = 0x00;//0 新闻
	public final static int CATALOG_RECOMMEND = 0x01;//1 推荐
	public final static int CATALOG_ZATAN = 0x02;//2 杂谈

	private String 	title;
	private String 	url;
	private String 	body;
	private int 	type;
	private String 	author;
	//private int 	authorId;
	//private int 	commentCount;
	private String 	pubDate;
	private int 	favorite;
	//private AllType allType;
	private List<Relative> relatives;

	/*public Artical(){
		this.allType = new AllType();
		this.relatives = new ArrayList<Relative>();
	}	*/
	
	/*public class AllType implements Serializable{
		public int type;
		public String attachment;
		//public int authoruid2;
	} */
	
	public static class Relative implements Serializable{
		public String rtitle;//相关文章标题
		public String rurl;//相关文章地址
		public int type;//相关文章类别
	}
	
	public List<Relative> getRelatives() {
		return relatives;
	}
	public void setRelatives(List<Relative> relatives) {
		this.relatives = relatives;
	}
	/*public AllType getAllType() {
		return allType;
	}
	public void setNewType(AllType allType) {
		this.allType = allType;
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
	public int getType() {
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
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
	//public int getAuthorId() {
		//return authorId;
	//}
	//public void setAuthorId(int authorId) {
		//this.authorId = authorId;
	//}
	//public int getCommentCount() {
		///return commentCount;
	//}
	//public void setCommentCount(int commentCount) {
		//this.commentCount = commentCount;
	//}
	
	public static Artical parse(InputStream inputStream) throws IOException, AppException {
		Artical art = null;
		Relative relative = null;
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
			    			art = new Artical();
			    		}
			    		else if(art != null)
			    		{	
				            if(tag.equalsIgnoreCase(NODE_ID))
				            {			      
				            	art.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(NODE_TITLE))
				            {			            	
				            	art.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_URL))
				            {			            	
				            	art.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_BODY))
				            {			            	
				            	art.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR))
				            {			            	
				            	art.setAuthor(xmlParser.nextText());		            	
				            }
				            //else if(tag.equalsIgnoreCase(NODE_AUTHORID))
				            //{			            	
				            	//news.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            //else if(tag.equalsIgnoreCase(NODE_COMMENTCOUNT))
				            //{			            	
				            	//news.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            //}
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE))
				            {			            	
				            	art.setPubDate(xmlParser.nextText());      	
				            }	
				            else if(tag.equalsIgnoreCase(NODE_FAVORITE))
				            {			            	
				            	art.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            /*else if(tag.equalsIgnoreCase(NODE_TYPE))
				            {	
				            	art.getType() = StringUtils.toInt(xmlParser.nextText(),0); 
				            }
				            else if(tag.equalsIgnoreCase(NODE_ATTACHMENT))
				            {			            	
				            	art.getAllType().attachment = xmlParser.nextText(); 	
				            }*/
				            //else if(tag.equalsIgnoreCase(NODE_AUTHORUID2))
				            //{			            	
				            	//news.getNewType().authoruid2 = StringUtils.toInt(xmlParser.nextText(),0); 
				            //}
				            else if(tag.equalsIgnoreCase("relative"))
				            {			            	
				            	relative = new Relative();
				            }
				            else if(relative != null)
				            {			            	
				            	if(tag.equalsIgnoreCase("rtitle"))
				            	{
				            		relative.rtitle = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase("rurl"))
				            	{
				            		relative.rurl = xmlParser.nextText(); 	
				            	}
				            }
				            //通知信息
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	art.setNotice(new Notice());
				    		}
				            else if(art.getNotice() != null)
				    		{
				    			if(tag.equalsIgnoreCase("atmeCount"))
					            {			      
				    				art.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("msgCount"))
					            {			            	
					            	art.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("reviewCount"))
					            {			            	
					            	art.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("newFansCount"))
					            {			            	
					            	art.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
				    		}
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		
			    		//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("relative") && art!=null && relative!=null) { 
				       		art.getRelatives().add(relative);
				       		relative = null; 
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
        return art;       
	}
}
