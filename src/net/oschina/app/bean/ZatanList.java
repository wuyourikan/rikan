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
public class ZatanList extends Entity{
	
	public static final int CATALOG_USER = 1;//用户博客
	public static final int CATALOG_LATEST = 2;//最新博客
	//public static final int CATALOG_ALL = 3;//全部文章
	
	public static final String TYPE_LATEST = "latest";
	//public static final String TYPE_RECOMMEND = "recommend";
	//public static final String TYPE_ALL = "all";
	
	private int zatansCount;
	private int pageSize;
	private List<Zatan> zatanlist = new ArrayList<Zatan>();
	
	public int getZatansCount() {
		return zatansCount;
	}
	public int getPageSize() {
		return pageSize;
	}
	public List<Zatan> getZatanlist() {
		return zatanlist;
	}
	
	public static ZatanList parse(InputStream inputStream) throws IOException, AppException {
		ZatanList zatanlist = new ZatanList();
		Zatan zatan = null;
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
			    		if(tag.equalsIgnoreCase("zatansCount")) 
			    		{
			    			zatanlist.zatansCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			zatanlist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("zatan")) 
			    		{ 
			    			zatan = new Zatan();
			    		}
			    		else if(zatan != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	zatan.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("title"))
				            {			            	
				            	zatan.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	zatan.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	zatan.setPubDate(xmlParser.nextText());
				            }
				            //else if(tag.equalsIgnoreCase("authoruid"))
				            //{			            	
				            	//zatan.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));
				            //}
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	zatan.setAuthor(xmlParser.nextText());
				            }
				            //else if(tag.equalsIgnoreCase("documentType"))
				            //{			            	
				            	//zatan.setDocumentType(StringUtils.toInt(xmlParser.nextText(),0));
				            //}
				            //else if(tag.equalsIgnoreCase("commentCount"))
				            //{			            	
				            	//zatan.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
				            //}
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	zatanlist.setNotice(new Notice());
			    		}
			            else if(zatanlist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				zatanlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	zatanlist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	zatanlist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	zatanlist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("zatan") && zatan != null) { 
				       		zatanlist.getZatanlist().add(zatan); 
				       		zatan = null; 
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
        return zatanlist;       
	}
}
