package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;


import java.util.Map;

import net.oschina.app.AppException;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 评论列表实体类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class CommentList extends BaseList{

	public final static int CATALOG_ARTICAL = 1;
	public final static int CATALOG_TOPIC = 2;
	public final static int CATALOG_ZHUANTI = 3;
	public final static int CATALOG_ACTIVE = 4;
	public final static int CATALOG_MESSAGE = 5;//动态与留言都属于消息中心

	@Override
	public int getMaxPageSize(){
		return 20;
	}
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "commentlist_" + map.get("catalog")+
				"_" + map.get("pageIndex");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.COMMENT);
		//return ApiClient.makeURL(URLs.GET_LIST, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_LIST, map);	
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_LIST;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		Comment comm = null;
		Reply reply = null;
		Refer refer = null;
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
			    		if(tag.equalsIgnoreCase("allCount")) {
			    			this.allCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase(BaseList.NODE_PAGESIZE)) {
			    			this.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Comment.NODE_START)) { 
			    			comm = new Comment();
			    		}
			    		else if(comm != null){	
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	comm.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_FACE)){			            	
				            	comm.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_AUTHOR)){			            	
				            	comm.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_AUTHORID)){			            	
				            	comm.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_CONTENT)){			            	
				            	comm.setContent(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_PUBDATE)){			            	
				            	comm.setPubDate(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_APPCLIENT)){			            	
				            	comm.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Reply.NODE_START)){			            	
				            	reply = new Reply();         	
				            }
				            else if(reply!=null){
				            	if(tag.equalsIgnoreCase(Reply.NODE_AUTHOR)){
					            	reply.rauthor = xmlParser.nextText();
					            }
					            else if(tag.equalsIgnoreCase(Reply.NODE_PUBDATE)){
					            	reply.rpubDate = xmlParser.nextText();
					            }
					            else if(tag.equalsIgnoreCase(Reply.NODE_CONTENT)){
					            	reply.rcontent = xmlParser.nextText();
					            }
				            }
				            else if(tag.equalsIgnoreCase(Refer.NODE_START)){			            	
				            	refer = new Refer();         	
				            }
				            else if(refer!=null){
				            	if(tag.equalsIgnoreCase(Refer.NODE_TITLE)){
					            	refer.refertitle = xmlParser.nextText();
					            }
					            else if(tag.equalsIgnoreCase(Refer.NODE_BODY)){
					            	refer.referbody = xmlParser.nextText();
					            }
				            }
			    		}
			    		//通知信息
			            else if(tag.equalsIgnoreCase(Notice.NODE_START)){
			            	this.setNotice(new Notice());
			    		}
			            else if(this.getNotice() != null){
			    			if(tag.equalsIgnoreCase(Notice.NODE_ATMECOUNT)){			      
			    				this.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_MSGCOUNT)){			            	
				            	this.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_REVIEWCOUNT)){			            	
				            	this.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase(Notice.NODE_NEWFANSCOUNT)){			            	
				            	this.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase(Comment.NODE_START) && comm != null) { 
				       		this.getList().add(comm); 
				       		comm = null; 
				       	}
				       	else if (tag.equalsIgnoreCase(Reply.NODE_START) && comm!=null && reply!=null) { 
				       		comm.getReplies().add(reply);
				       		reply = null; 
				       	}
				       	else if(tag.equalsIgnoreCase(Refer.NODE_START) && comm!=null && refer!=null) {
				       		comm.getRefers().add(refer);
				       		refer = null;
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
	}
}
