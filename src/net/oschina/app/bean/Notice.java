package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 通知信息实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Notice extends Entity {
	
	public final static String NODE_START = "notice";
	public final static String NODE_ATMECOUNT = "atmeCount";
	public final static String NODE_MSGCOUNT = "msgCount";	
	public final static String NODE_REVIEWCOUNT = "reviewCount";	
	public final static String NODE_NEWFANSCOUNT = "newFansCount";	
	
	public final static int	TYPE_ATME = 1;
	public final static int	TYPE_MESSAGE = 2;
	public final static int	TYPE_COMMENT = 3;
	public final static int	TYPE_NEWFAN = 4;

	private int atmeCount;
	private int msgCount;
	private int reviewCount;
	private int newFansCount;
	
	public int getAtmeCount() {
		return atmeCount;
	}
	public void setAtmeCount(int atmeCount) {
		this.atmeCount = atmeCount;
	}
	public int getMsgCount() {
		return msgCount;
	}
	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}
	public int getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}
	public int getNewFansCount() {
		return newFansCount;
	}
	public void setNewFansCount(int newFansCount) {
		this.newFansCount = newFansCount;
	}	
	
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
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
			            //通知信息
			    		if(tag.equalsIgnoreCase(Notice.NODE_START)){
			    			gotNodeStart = true;
			    		}
			            else if(gotNodeStart){
			    			if(tag.equalsIgnoreCase(Notice.NODE_ATMECOUNT)){			      
			    				this.setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_MSGCOUNT)){			            	
				            	this.setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_REVIEWCOUNT)){			            	
				            	this.setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_NEWFANSCOUNT)){			            	
				            	this.setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
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
        //return notice;       
	}
}
