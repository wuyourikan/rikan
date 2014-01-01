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
 * 帖子列表实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ZhuantiList extends Entity{

	public final static int CATALOG_COMMON= 1;
	public final static int CATALOG_PARTICULAR = 2;
	//public final static int CATALOG_POLITICS = 3;
	//public final static int CATALOG_JOB = 4;
	//public final static int CATALOG_SITE = 5;
	
	private int pageSize;
	private int postCount;
	private List<Zhuanti> zhuantilist = new ArrayList<Zhuanti>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getZhuantiCount() {
		return postCount;
	}
	public List<Zhuanti> getZhuantilist() {
		return zhuantilist;
	}

	public static ZhuantiList parse(InputStream inputStream) throws IOException, AppException {
		ZhuantiList zhuantilist = new ZhuantiList();
		Zhuanti zhuanti = null;
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
			    		if(tag.equalsIgnoreCase("postCount")) 
			    		{
			    			zhuantilist.postCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			zhuantilist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Huati.NODE_START)) 
			    		{ 
			    			zhuanti = new Zhuanti();
			    		}
			    		else if(zhuanti != null)
			    		{	
				            if(tag.equalsIgnoreCase(Huati.NODE_ID))
				            {			      
				            	zhuanti.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_TITLE))
				            {			            	
				            	zhuanti.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_IMG))
				            {			            	
				            	zhuanti.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_AUTHOR))
				            {			            	
				            	zhuanti.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_AUTHORID))
				            {			            	
				            	zhuanti.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            /*else if(tag.equalsIgnoreCase(Huati.NODE_ANSWERCOUNT))
				            {			            	
				            	zhuanti.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_VIEWCOUNT))
				            {			            	
				            	zhuanti.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }*/
				            else if(tag.equalsIgnoreCase(Huati.NODE_PUBDATE))
				            {			            	
				            	zhuanti.setPubDate(xmlParser.nextText());         	
				            }
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	zhuantilist.setNotice(new Notice());
			    		}
			            else if(zhuantilist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				zhuantilist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	zhuantilist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	zhuantilist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	zhuantilist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("post") && zhuanti != null) { 
				       		zhuantilist.getZhuantilist().add(zhuanti); 
				       		zhuanti = null; 
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
        return zhuantilist;       
	}
}
