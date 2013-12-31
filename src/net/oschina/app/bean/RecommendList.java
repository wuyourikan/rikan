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
public class RecommendList extends Entity{

	public final static int CATALOG_RECOMMEND = 3;
	
	private int catalog;//类别
	private int pageSize;//页数
	private int recommendCount;
	private String imageurl;
	private String recommendurl;
	private List<Recommend> recommendlist = new ArrayList<Recommend>();
	
	public int getCatalog() {
		return catalog;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getRecommendCount() {
		return recommendCount;
	}
	public List<Recommend> getRecommendlist() {
		return recommendlist;
	}
	public String getImageView() {
		return imageurl;
	}
	public String getRecommendUrl() {
		return recommendurl;
	}
	
	public static RecommendList parse(InputStream inputStream) throws IOException, AppException {
		RecommendList recommendlist = new RecommendList();
		Recommend recommend = null;
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
			    			recommendlist.catalog = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			recommendlist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("newsCount")) 
			    		{
			    			recommendlist.recommendCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Recommend.NODE_START)) //news
			    		{ 
			    			recommend = new Recommend();
			    		}
			    		else if(recommend != null)
			    		{	
				            if(tag.equalsIgnoreCase(Recommend.NODE_ID))//id
				            {			      
				            	recommend.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Recommend.NODE_TITLE))//title
				            {			            	
				            	recommend.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Recommend.NODE_URL))//url
				            {			            	
				            	recommend.setUrl(xmlParser.nextText());
				            	recommendlist.recommendurl = recommend.getUrl();
				            }
				            else if(tag.equalsIgnoreCase(Recommend.NODE_AUTHOR))//author
				            {			            	
				            	recommend.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Recommend.NODE_IMAGE))
				            {
				            	recommend.setImageView(xmlParser.nextText());
				            	recommendlist.imageurl = recommend.getImageView();
				            }
				            //else if(tag.equalsIgnoreCase(News.NODE_AUTHORID))//authorid
				            //{			            	
				            	//news.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            //else if(tag.equalsIgnoreCase(News.NODE_COMMENTCOUNT))//commentcount
				            //{			            	
				            	//news.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            //}
				            else if(tag.equalsIgnoreCase(Recommend.NODE_PUBDATE))//pubDate
				            {			            	
				            	recommend.setPubDate(xmlParser.nextText());	
				            }
				            //else if(tag.equalsIgnoreCase(Recommend.NODE_TYPE))//type
				            //{	
				            	//news.getNewType().type = StringUtils.toInt(xmlParser.nextText(),0); 
				            //}
				            //else if(tag.equalsIgnoreCase(Recommend.NODE_ATTACHMENT))//attachment
				            //{			            	
				            	//news.getNewType().attachment = xmlParser.nextText(); 	
				            //}
				            //else if(tag.equalsIgnoreCase(News.NODE_AUTHORUID2))//authorid2
				            //{			            	
				            	//news.getNewType().authoruid2 = StringUtils.toInt(xmlParser.nextText(),0); 
				            //}
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	recommendlist.setNotice(new Notice());
			    		}
			            else if(recommendlist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				recommendlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	recommendlist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	recommendlist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	recommendlist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("news") && recommend != null) { 
				    	   recommendlist.getRecommendlist().add(recommend); 
				           recommend = null; 
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
        return recommendlist;       
	}
}
