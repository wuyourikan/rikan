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
 * 文章实体类
 * @author wyf (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2014/1/1
 */
public class Article extends WebViewItem{

	public final static String NODE_SOURCE = "source";
	public final static String NODE_ATTACHMENT = "attachment";
	public final static String NODE_START = "article";	//XML开始标签

	private String source;
	//private OtherType otherType;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	/*public class OtherType implements Serializable{
		public int type;
		public String attachment;
	} */

	/*public OtherType getOtherType() {
		return otherType;
	}
	public void setOtherType(OtherType otherType) {
		this.otherType = otherType;
	}	*/
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "article_" + map.get("id");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.ARTICLE);
		//return ApiClient.makeURL(URLs.GET_ITEM, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_ITEM, map);
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_ITEM;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		//Article art = null;
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
			    		else if(gotNodeStart){	
				            if(tag.equalsIgnoreCase(NODE_ID)){			      
				            	this.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(NODE_URL)){			            	
				            	this.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_TITLE)){			            	
				            	this.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR)){			            	
				            	this.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE)){			            	
				            	this.setPubDate(xmlParser.nextText());      	
				            }	
				            else if(tag.equalsIgnoreCase(NODE_SOURCE)){			            	
				            	this.setSource(xmlParser.nextText());	            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_BODY)){			            	
				            	this.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_FAVORITE)){			            	
				            	this.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            //else if(tag.equalsIgnoreCase(NODE_AUTHORID)){			            	
				            	//this.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            //else if(tag.equalsIgnoreCase(NODE_COMMENTCOUNT)){			            	
				            	//this.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            //}      
				            /*else if(tag.equalsIgnoreCase(NODE_ATTACHMENT)){			            	
				            	this.getOtherType().attachment = xmlParser.nextText(); 	
				            }*/
				            //else if(tag.equalsIgnoreCase(NODE_AUTHORUID2)){			            	
				            	//news.getNewType().authoruid2 = StringUtils.toInt(xmlParser.nextText(),0); 
				            //}
				            /*else if(tag.equalsIgnoreCase("relative")){			            	
				            	relative = new Relative();
				            }
				            else if(relative != null){			            	
				            	if(tag.equalsIgnoreCase("rtitle")){
				            		relative.rtitle = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase("rurl")){
				            		relative.rurl = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase("rimg")){
				            		relative.rimg = xmlParser.nextText(); 	
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
			    		//如果遇到标签结束，则把对象添加进集合中
				       	/*if (tag.equalsIgnoreCase("relative") && art!=null && relative!=null) { 
				       		art.getRelatives().add(relative);
				       		relative = null; 
				       	}*/
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
        //return art;
	}
}
