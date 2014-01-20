package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import net.oschina.app.AppException;
import net.oschina.app.api.ApiClient;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.TypeHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 新闻列表实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ArticleList extends BaseList{
	
	private HeadImg headImg;  //TODO: 将headImg升级为数组，可存储三联图

	public HeadImg getHeadImg(){
		return headImg;
	}
	public void setHeadImg(HeadImg headImg) {
		this.headImg = headImg;
	}
	
	@Override
	public int getMaxPageSize(){
		return 20;
	}
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "articlelist_" + map.get("catalog")+
				"_" + map.get("pageIndex");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.ARTICLE);
		//return ApiClient.makeURL(URLs.GET_LIST, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_LIST, map);	
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.GET_LIST;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		Article article = null;
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
			    		if(tag.equalsIgnoreCase(NODE_CATALOG)) {
			    			this.catalog = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase(NODE_PAGESIZE)) {
			    			this.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		/*else if(tag.equalsIgnoreCase("allCount")) {
			    			this.allCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}*/
			    		//置顶大图
			    		/*else if(tag.equalsIgnoreCase(HeadImg.NODE_START)){
			            	this.headImg = new HeadImg();
			    		}
			            else if(this.headImg != null){
			    			if(tag.equalsIgnoreCase(HeadImg.NODE_IMG)){			      
			    				this.headImg.setImg(xmlParser.nextText());
				            }
			    			else if(tag.equalsIgnoreCase(HeadImg.NODE_TITLE)){			      
			    				this.headImg.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(HeadImg.NODE_ID)){			            	
				            	this.headImg.setArticleId(StringUtils.toInt(xmlParser.nextText()));
				            }
			    		}*/
			    		else if (tag.equalsIgnoreCase(Article.NODE_START)){ //articles 
			    			article = new Article();
			    		}
			    		else if(article != null){	
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	article.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Article.NODE_TITLE)){			            	
				            	article.setTitle(xmlParser.nextText());
				            }
				            /*else if(tag.equalsIgnoreCase(Article.NODE_URL)){			            	
				            	article.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Article.NODE_TYPE)){			            	
				            	article.setType(StringUtils.toInt(xmlParser.nextText(),0)); 
				            }*/
				            else if(tag.equalsIgnoreCase(Article.NODE_AUTHOR)){			            	
				            	article.setAuthor(xmlParser.nextText());		            	
				            }
				            //else if(tag.equalsIgnoreCase(News.NODE_AUTHORID)){			            	
				            	//news.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            //}
				            //else if(tag.equalsIgnoreCase(News.NODE_COMMENTCOUNT)){			            	
				            	//news.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            //}
				            else if(tag.equalsIgnoreCase(Article.NODE_PUBDATE)){			            	
				            	article.setPubDate(xmlParser.nextText());	
				            }
				            /*else if(tag.equalsIgnoreCase(Article.NODE_ATTACHMENT)){			            	
				            	article.getOtherType().attachment = xmlParser.nextText(); 	
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
				       	if (tag.equalsIgnoreCase(Article.NODE_START) && article != null) { 
				       		this.getList().add(article); 
				       		article = null; 
				       	}
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
        //return articleList;       
	}
}
