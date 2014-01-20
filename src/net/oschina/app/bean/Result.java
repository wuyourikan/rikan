package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;

import net.oschina.app.AppException;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 数据操作结果实体类
 * @author wyf (http://my.oschina.net/zgtjwyftc)
 * @version 1.0
 * @created 2014-1-6
 */
public class Result extends EntityWithNotice {

	public final static String NODE_START = "result";
	public final static String NODE_ERRORCODE = "errorCode";
	public final static String NODE_ERRORMESSAGE = "errorMessage";	
	
	private int errorCode;
	private String errorMessage;
	
	private Comment comment;
	
	public boolean OK() {
		return errorCode == 1;
	}

	public int getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}

	@Override
	public String toString(){
		return String.format("RESULT: CODE:%d,MSG:%s", errorCode, errorMessage);
	}
	
	/**
	 * 解析调用结果
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	@Override
	public void parse(InputStream stream) throws IOException, AppException {
		//Result res = null;
		boolean gotNodeStart = false;
		Reply reply = null;
		Refer refer = null;        
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(stream, Base.UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {

				case XmlPullParser.START_TAG:
					// 如果是标签开始，则说明需要实例化对象了
					if (tag.equalsIgnoreCase(NODE_START)){
						gotNodeStart = true;
					} 
					else if (gotNodeStart) { 
						if (tag.equalsIgnoreCase(NODE_ERRORCODE)) {
							this.errorCode = StringUtils.toInt(xmlParser.nextText(), -1);
						} 
						else if (tag.equalsIgnoreCase(NODE_ERRORMESSAGE)) {
							this.errorMessage = xmlParser.nextText().trim();
						}
						else if(tag.equalsIgnoreCase(Comment.NODE_START)){
							this.comment = new Comment();
			    		}
			            else if(this.comment != null){
				            if(tag.equalsIgnoreCase(BaseItem.NODE_ID)){			      
				            	this.comment.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_FACE)){			            	
				            	this.comment.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_AUTHOR)){			            	
				            	this.comment.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_AUTHORID)){			            	
				            	this.comment.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_CONTENT)){			            	
				            	this.comment.setContent(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_PUBDATE)){			            	
				            	this.comment.setPubDate(xmlParser.nextText());	
				            }
				            else if(tag.equalsIgnoreCase(Comment.NODE_APPCLIENT)){			            	
				            	this.comment.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
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
			       	if (tag.equalsIgnoreCase("reply") && this.comment!=null && reply!=null){ 
			       		this.comment.getReplies().add(reply);
			       		reply = null; 
			       	}
			       	else if(tag.equalsIgnoreCase("refer") && this.comment!=null && refer!=null){
			       		this.comment.getRefers().add(refer);
			       		refer = null;
			       	}
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}

		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			stream.close();
		}
	}
}
