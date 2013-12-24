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
 * 话题列表实体类
 * @author wyf
 * @version 1.0
 * @created 2013-12-23
 */
public class HuatiList extends Entity{

	//public final static int CATALOG_CENTER = 1;
	public final static int CATALOG_SIXIANG = 1;
	public final static int CATALOG_ZHENGJING = 2;
	public final static int CATALOG_WENSHI = 3;
	
	private int pageSize;
	private int huatiCount;
	private List<Huati> huatilist = new ArrayList<Huati>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getHuatiCount() {
		return huatiCount;
	}
	public List<Huati> getHuatilist() {
		return huatilist;
	}

	public static HuatiList parse(InputStream inputStream) throws IOException, AppException {
		HuatiList huatilist = new HuatiList();
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
			    		if(tag.equalsIgnoreCase("huatiCount")) 
			    		{
			    			huatilist.huatiCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			huatilist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Huati.NODE_START)) 
			    		{ 
			    			huati = new Huati();
			    		}
			    		else if(huati != null)
			    		{	
				            if(tag.equalsIgnoreCase(Huati.NODE_ID))
				            {			      
				            	huati.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_TITLE))
				            {			            	
				            	huati.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_IMG))
				            {			            	
				            	huati.setImg(xmlParser.nextText());
				            }
				           /* else if(tag.equalsIgnoreCase(Huati.NODE_AUTHOR))
				            {			            	
				            	huati.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_AUTHORID))
				            {			            	
				            	huati.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_ANSWERCOUNT))
				            {			            	
				            	huati.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Huati.NODE_VIEWCOUNT))
				            {			            	
				            	huati.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }*/
				            else if(tag.equalsIgnoreCase(Huati.NODE_PUBDATE))
				            {			            	
				            	huati.setPubDate(xmlParser.nextText());         	
				            }
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	huatilist.setNotice(new Notice());
			    		}
			            else if(huatilist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				huatilist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	huatilist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	huatilist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	huatilist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("huati") && huati != null) { 
				    	   huatilist.getHuatilist().add(huati); 
				           huati = null; 
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
        return huatilist;       
	}
}
