package net.oschina.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import net.oschina.app.api.ApiClient;

/**
 * 列表实体基类
 * @author wyf
 * @version 1.0
 * @created 2013-12-23
 */
public abstract class BaseList extends Base{
	
	public final static String NODE_CATALOG = "catalog";	//XML节点名
	public final static String NODE_PAGESIZE = "pageSize";	
	public final static String NODE_ALLCOUNT = "allCount";	
	
	protected int catalog;	//类别
	protected int pageSize;	//列表项的个数
	protected int allCount; //列表项的总数（目前只有CommentList会用到）
	
	protected List<BaseItem> list = new ArrayList<BaseItem>();	//基类List，可嵌套BaseList
	
	public int getCatalog() {
		return catalog;
	}
	public int getPageSize() {
		return pageSize;
	} 
	public int getAllCount() {
		return allCount;
	}
	public List<BaseItem> getList() {
		return list;
	}
	
	public abstract int getMaxPageSize();
	//public void parse(InputStream inputStream) {}
}
