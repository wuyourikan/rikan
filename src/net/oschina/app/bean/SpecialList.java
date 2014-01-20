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
 * 专题列表实体类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-3
 */
public class SpecialList extends BaseList{
	
	@Override
	public int getMaxPageSize(){
		return 5;
	}
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "speciallist_" + map.get("catalog")+
				"_" + map.get("pageIndex");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.SPECIAL);
		//return ApiClient.makeURL(URLs.GET_LIST, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_LIST, map);	
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_LIST;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		Special special = null;
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
			    		//super.parse(inputStream)		
			    		if(tag.equalsIgnoreCase(NODE_CATALOG)) {
			    			this.catalog = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase(NODE_PAGESIZE)) {
			    			this.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		/*if(tag.equalsIgnoreCase("postCount")) 
			    		{
			    			zhuantilist.postCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}*/
			    		else if (tag.equalsIgnoreCase(Special.NODE_START)) { 
			    			special = new Special();
			    			//zhuanti.parse(inputStream);
			    		}
			    		else if(special != null){	
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	special.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Special.NODE_TITLE)){			            	
				            	special.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Special.NODE_IMG)){			            	
				            	special.setImg(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Special.NODE_INTRO)){			            	
				            	special.setIntro(xmlParser.nextText());
				            }
				            /*else if(tag.equalsIgnoreCase(Special.NODE_AUTHOR)){			            	
				            	zhuanti.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Special.NODE_AUTHORID)){			            	
				            	zhuanti.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(Special.NODE_ANSWERCOUNT)){			            	
				            	zhuanti.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Special.NODE_VIEWCOUNT)){			            	
				            	zhuanti.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
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
				       	if (tag.equalsIgnoreCase(Special.NODE_START) && special != null) { 
				       		this.getList().add(special); 
				       		special = null; 
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
