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
 * 用户专页信息实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInformation extends Entity{
	
	private int pageSize;
	private User user = new User();	

	public int getPageSize() {
		return pageSize;
	}
	public User getUser() {
		return user;
	}

	public static UserInformation parse(InputStream inputStream) throws IOException, AppException {
		UserInformation uinfo = new UserInformation();
		User user = null;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType=xmlParser.getEventType();
			//一直循环，直到文档结束    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
				String tag = xmlParser.getName(); 
	    		int depth = xmlParser.getDepth();
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase("user")) 
			    		{
			    			user = new User();
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			uinfo.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (user != null)
			    		{
			    			if(tag.equalsIgnoreCase("uid")){
								user.setUid(StringUtils.toInt(xmlParser.nextText(), 0));
							}else if(tag.equalsIgnoreCase("from")){
								user.setLocation(xmlParser.nextText());
							}else if(tag.equalsIgnoreCase("name")){
								user.setName(xmlParser.nextText());
							}else if(depth==3 && tag.equalsIgnoreCase("portrait")){
								user.setFace(xmlParser.nextText());
							}else if(tag.equalsIgnoreCase("jointime")){
								user.setJointime(xmlParser.nextText());
							}else if(tag.equalsIgnoreCase("gender")){
								user.setGender(xmlParser.nextText());
							}else if(tag.equalsIgnoreCase("devplatform")){
								user.setDevplatform(xmlParser.nextText());
							}else if(tag.equalsIgnoreCase("expertise")){
								user.setExpertise(xmlParser.nextText());
							}else if(tag.equalsIgnoreCase("relation")){
								user.setRelation(StringUtils.toInt(xmlParser.nextText(), 0));
							}else if(tag.equalsIgnoreCase("latestonline")){
								user.setLatestonline(xmlParser.nextText());
							}
			    		}
			    		
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	uinfo.setNotice(new Notice());
			    		}
			            else if(uinfo.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				uinfo.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	uinfo.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	uinfo.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	uinfo.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
			    		if (tag.equalsIgnoreCase("user") && user != null) {
			    			uinfo.user = user;
			    			user = null;
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
        return uinfo;       
	}
}
