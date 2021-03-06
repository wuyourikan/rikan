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
 * 新闻列表实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class NewsList extends Entity{

	public final static int CATALOG_ALLN = 1;
	public final static int CATALOG_INTEGRATION = 2;
	public final static int CATALOG_SOFTWARE = 3;
	public static final int CATALOG_ALL = 0;
	
	private int catalog;//类别
	private int pageSize;//页数
	private int newsCount;
	private String imageurl;
	private String newsurl;
	private List<News> newslist = new ArrayList<News>();
	
	public int getCatalog() {
		return catalog;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getNewsCount() {
		return newsCount;
	}
	public List<News> getNewslist() {
		return newslist;
	}
	public String getImageView() {
		return imageurl;
	}
	public String getNewsUrl() {
		return newsurl;
	}
	
	public static NewsList parse(InputStream inputStream) throws IOException, AppException {
		NewsList newslist = new NewsList();
		News news = null;
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
			    		if(tag.equalsIgnoreCase("catalog")) 
			    		{
			    			newslist.catalog = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			newslist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("newsCount")) 
			    		{
			    			newslist.newsCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(News.NODE_START)) //news
			    		{ 
			    			news = new News();
			    		}
			    		else if(news != null)
			    		{	
				            if(tag.equalsIgnoreCase(News.NODE_ID))//id
				            {			      
				            	news.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_TITLE))//title
				            {			            	
				            	news.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_URL))//url
				            {			            	
				            	news.setUrl(xmlParser.nextText());
				            	newslist.newsurl = news.getUrl();
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_AUTHOR))//author
				            {			            	
				            	news.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_IMAGE))
				            {
				            	news.setImageView(xmlParser.nextText());
				            	newslist.imageurl = news.getImageView();
				            }
				            //else if(tag.equalsIgnoreCase(News.NODE_AUTHORID))//authorid
				            //{			            	
				            	//news.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            //else if(tag.equalsIgnoreCase(News.NODE_COMMENTCOUNT))//commentcount
				            //{			            	
				            	//news.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            //}
				            else if(tag.equalsIgnoreCase(News.NODE_PUBDATE))//pubDate
				            {			            	
				            	news.setPubDate(xmlParser.nextText());	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_TYPE))//type
				            {	
				            	news.getNewType().type = StringUtils.toInt(xmlParser.nextText(),0); 
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_ATTACHMENT))//attachment
				            {			            	
				            	news.getNewType().attachment = xmlParser.nextText(); 	
				            }
				            //else if(tag.equalsIgnoreCase(News.NODE_AUTHORUID2))//authorid2
				            //{			            	
				            	//news.getNewType().authoruid2 = StringUtils.toInt(xmlParser.nextText(),0); 
				            //}

			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	newslist.setNotice(new Notice());
			    		}
			            else if(newslist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				newslist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	newslist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	newslist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	newslist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("news") && news != null) { 
				    	   newslist.getNewslist().add(news); 
				           news = null; 
				       	}
				       	break; 
			    }
			    //如果xml没有结束，则导航到下一个节点
			    int a =xmlParser.next();
			    evtType=a;
			}		
        } catch (XmlPullParserException e) {
        	e.printStackTrace();
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }      
        return newslist;       
	}
}
