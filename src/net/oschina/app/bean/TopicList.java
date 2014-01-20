package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.oschina.app.AppException;
import net.oschina.app.api.ApiClient;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 话题列表实体类
 * @author wyf
 * @version 1.0
 * @created 2013-12-23
 */
public class TopicList extends BaseList{

	@Override
	public int getMaxPageSize(){
		return 10;
	}
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "topiclist_" + map.get("catalog")+
				"_" + map.get("pageIndex");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.TOPIC);
		//return ApiClient.makeURL(URLs.GET_LIST, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_LIST, map);	
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_LIST;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		Topic topic = null;
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
			    		/*if(tag.equalsIgnoreCase("allCount")) {
			    			this.allCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		*/if(tag.equalsIgnoreCase(NODE_CATALOG)) {
			    			this.catalog = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase(NODE_PAGESIZE)) {
			    			this.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Topic.NODE_START)) { 
			    			topic = new Topic();
			    		}
			    		else if(topic != null){	
				            if(tag.equalsIgnoreCase(Topic.NODE_ID)){			      
				            	topic.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_TITLE)){			            	
				            	topic.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_IMG)){			            	
				            	topic.setImg(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_INTRO)){			            	
				            	topic.setIntro(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_PUBDATE)){			            	
				            	topic.setPubDate(xmlParser.nextText());         	
				            }
				           /* else if(tag.equalsIgnoreCase(Topic.NODE_AUTHOR)){			            	
				            	topic.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_AUTHORID)){			            	
				            	topic.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_ANSWERCOUNT)){			            	
				            	topic.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_VIEWCOUNT)){			            	
				            	topic.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }*/
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
				       	if (tag.equalsIgnoreCase(Topic.NODE_START) && topic != null) { 
				       		this.getList().add(topic); 
				        	topic = null; 
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
