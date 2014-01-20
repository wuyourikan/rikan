package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import net.oschina.app.AppException;
import net.oschina.app.api.ApiClient;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;

public class HeadImg extends Base{
	
	public final static String NODE_START 	= "headImg";	
	public final static String NODE_IMG 	= "himg";
	public final static String NODE_TITLE 	= "htitle";
	public final static String NODE_ID 		= "hid";
	//public final static String NODE_LINK 	= "hlink";	
	
	private String img;			//置顶大图链接
	private String title;		//置顶大图标题
	private int articleId;		//实际文章id
	//public String articleLink;	//置顶大图实际文章URL链接
	
	public void setImg(String img) {
		this.img = img;
	}
	public String getImg() {
		return img;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public int getArticleId() {
		return articleId;
	}

	public String getCacheKey(Map<String, Object> map) {
		return  "artical_" + map.get("catalog")+ "_img";
	}
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.ARTICLE);
		map.put("headImg", true);
		return ApiClient.makeStaticURL(URLs.GET_LIST, map);
	}
	public String getHttpPostUrl() {
		return URLs.GET_ITEM;
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
	    		String tag = xmlParser.getName(); //获得标签名
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase(NODE_START)){
			    			gotNodeStart = true;
			    		}
			            else if(gotNodeStart){
			    			if(tag.equalsIgnoreCase(HeadImg.NODE_IMG)){			      
			    				this.img = xmlParser.nextText();
				            }
			    			else if(tag.equalsIgnoreCase(HeadImg.NODE_TITLE)){			      
			    				this.title = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase(HeadImg.NODE_ID)){			            	
				            	this.articleId = StringUtils.toInt(xmlParser.nextText());
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
			    		if(tag.equalsIgnoreCase(NODE_START))
			    			return; //如果遇到结束符则停止遍历XML
			    		break;
			    }
			    //如果xml没有结束，则导航到下一个节点
			    int a =xmlParser.next();
			    evtType=a;
			}		
        } catch (XmlPullParserException e) {
        	e.printStackTrace();
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }          
	}
}

