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
 * 话题实体类
 * @author wyf
 * @version 1.0
 * @created 2013-12-27
 */
public class Topic extends WebViewItem{
	
	public final static String NODE_IMG = "img";		//列表图片
	public final static String NODE_INTRO = "intro";	//导语
//	public final static String NODE_END = "end";		//结语
	public final static String NODE_START = "topic"; //XML中某一话题对应的标签名
	
	
	/* 话题实体字段  */
	private String img;
	private String intro;
	//private String end;
	
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
/*	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	} */
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "topic_" + map.get("id");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.TOPIC);
		//return ApiClient.makeURL(URLs.GET_ITEM, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_ITEM, map);
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_ITEM;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		//Relative relative = null;
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
			    		if(tag.equalsIgnoreCase(NODE_START)){
			    			gotNodeStart = true;
			    		}
			    		else if (gotNodeStart) {
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	this.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_URL)){			            	
				            	this.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_TITLE)){			            	
				            	this.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_AUTHOR)){			            	
				            	this.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_PUBDATE)){			            	
				            	this.setPubDate(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_IMG)){			            	
				            	this.setImg(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Topic.NODE_INTRO)){			            	
				            	this.setIntro(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_BODY)){			            	
				            	this.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_FAVORITE)){			            	
				            	this.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
/*				            else if(tag.equalsIgnoreCase(Topic.NODE_END)){			            	
				            	this.setEnd(xmlParser.nextText());
				            }
							else if(tag.equalsIgnoreCase(WebViewItem.NODE_AUTHORID)){			            	
				            	this.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
			            	else if(tag.equalsIgnoreCase(WebViewItem.NODE_ANSWERCOUNT)){			            	
				            	this.setAnswerCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(WebViewItem.NODE_VIEWCOUNT)){			            	
				            	this.setViewCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            } 
				            //标签
			            	else if(tag.equalsIgnoreCase(WebViewItem.NODE_TAG_START)){
				            	this.tags = new ArrayList<String>();
				            }
				            else if(this.getTags() != null && tag.equalsIgnoreCase(WebViewItem.NODE_TAG)){
				            	this.getTags().add(xmlParser.nextText());
				    		}
				            //相关链接
							else if(tag.equalsIgnoreCase(Relative.NODE_START)){			            	
				            	relative = new Relative();
				            }
				            else if(relative != null){			            	
				            	if(tag.equalsIgnoreCase(Relative.NODE_TITLE)){
				            		relative.title = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase(Relative.NODE_URL)){
				            		relative.url = xmlParser.nextText(); 	
				            	}
			            		else if(tag.equalsIgnoreCase(Relative.NODE_PUBDATE)){
				            		relative.pubDate = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase(Relative.NODE_IMG)){
				            		relative.img = xmlParser.nextText(); 	
				            	}
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
			    		if(tag.equalsIgnoreCase(NODE_START))
			    			return; //如果遇到结束符则停止遍历XML
			    		/*if (tag.equalsIgnoreCase("relative") && gotNodeStart && relative!=null) { 
				       		this.getRelatives().add(relative);
				       		relative = null; 
			    		}*/	
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
