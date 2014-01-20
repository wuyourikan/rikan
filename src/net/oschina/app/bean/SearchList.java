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
 * 搜索列表实体类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class SearchList extends BaseList{

	/**
	 * 搜索结果实体类
	 */
	public static class SearchResult extends BaseItem {
		public final static String NODE_START = "searchResult";	//XML开始标签
		public final static String NODE_TYPE = "type";	
		public final static String NODE_TITLE = "title";	
		public final static String NODE_URL = "url";
		public final static String NODE_PUBDATE = "pubDate";	
		public final static String NODE_AUTHOR = "author";	
		
		private int type;
		private String title;
		private String url;	//用来跳转到详情
		private String pubDate;
		private String author;
		public int getType() {return type;}
		public void setType(int type) {this.type = type;}
		public String getTitle() {return title;}
		public void setTitle(String title) {this.title = title;}
		public String getUrl() {return url;}
		public void setUrl(String url) {this.url = url;}
		public String getPubDate() {return pubDate;}
		public void setPubDate(String pubDate) {this.pubDate = pubDate;}
		public String getAuthor() {return author;}
		public void setAuthor(String author) {this.author = author;}
		@Override
		public String getCacheKey(Map<String, Object> map) {return null;}
		public String getHttpGetUrl(Map<String, Object> map){return null;}
		public String getHttpPostUrl(){return null;}
		public void parse(InputStream inputStream) throws IOException, AppException {}
	}

	@Override
	public int getMaxPageSize(){
		return 20;
	}
	@Override
	public String getCacheKey(Map<String, Object> map) {
		return "searchlist_" + map.get("catalog")+
				"_" + map.get("pageIndex");
	}
	@Override
	public String getHttpGetUrl(Map<String, Object> map) {
		map.put("type", TypeHelper.SEARCH);
		//return ApiClient.makeURL(URLs.GET_LIST, map);
		return ApiClient.makeStaticURL(URLs.GET_STATIC_LIST, map);	
	}
	@Override
	public String getHttpPostUrl() {
		return URLs.SEARCH_LIST;
	}
	@Override
	public void parse(InputStream inputStream) throws IOException, AppException {
		SearchResult res = null;
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
			    		if(tag.equalsIgnoreCase(BaseList.NODE_PAGESIZE)) {
			    			this.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(SearchResult.NODE_START)) { 
			    			res = new SearchResult();
			    		}
			    		else if(res != null){	
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	res.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(SearchResult.NODE_TYPE)){			            	
				            	res.type = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(SearchResult.NODE_TITLE)){			            	
				            	res.title = xmlParser.nextText();		            	
				            }
				            else if(tag.equalsIgnoreCase(SearchResult.NODE_URL)){			            	
				            	res.url = xmlParser.nextText();		            	
				            }
				            else if(tag.equalsIgnoreCase(SearchResult.NODE_PUBDATE)){			            	
				            	res.pubDate = xmlParser.nextText();		            	
				            }
				            else if(tag.equalsIgnoreCase(SearchResult.NODE_AUTHOR)){			            	
				            	res.author = xmlParser.nextText();		            	
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
				       	if (tag.equalsIgnoreCase(SearchResult.NODE_START) && res != null) { 
				       		this.getList().add(res); 
				       		res = null; 
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
