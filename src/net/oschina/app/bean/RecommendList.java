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
 * 博客列表实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class RecommendList extends Entity{
	
	public static final int CATALOG_USER = 1;//用户博客
	public static final int CATALOG_LATEST = 2;//最新博客
	public static final int CATALOG_RECOMMEND = 3;//推荐博客
	public static final int CATALOG_ALL = 4;//全部文章
	
	public static final String TYPE_LATEST = "latest";
	public static final String TYPE_RECOMMEND = "recommend";
	public static final String TYPE_ALL = "all";
	
	private int recommendsCount;
	private int pageSize;
	private List<Recommend> recommendlist = new ArrayList<Recommend>();
	
	public int getRecommendsCount() {
		return recommendsCount;
	}
	public int getPageSize() {
		return pageSize;
	}
	public List<Recommend> getRecommendlist() {
		return recommendlist;
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
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase("recommendsCount")) 
			    		{
			    			recommendlist.recommendsCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			recommendlist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("recommend")) 
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
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	recommend.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	recommend.setPubDate(xmlParser.nextText());
				            }
				            //else if(tag.equalsIgnoreCase("authoruid"))
				            //{			            	
				            	//zatan.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));
				            //}
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	recommend.setAuthor(xmlParser.nextText());
				            }
				            //else if(tag.equalsIgnoreCase("documentType"))
				            //{			            	
				            	//recommend.setDocumentType(StringUtils.toInt(xmlParser.nextText(),0));
				            //}
				            //else if(tag.equalsIgnoreCase("commentCount"))
				            //{			            	
				            	//zatan.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
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
				       	if (tag.equalsIgnoreCase("recommend") && recommend != null) { 
				       		recommendlist.getRecommendlist().add(recommend); 
				       		recommend = null; 
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
        return recommendlist;       
	}
}
