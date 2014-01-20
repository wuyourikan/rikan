package net.oschina.app.bean;

import java.util.Map;

import android.annotation.SuppressLint;
import net.oschina.app.api.ApiClient;


/**
 * 文章实体类
 * @author wyf (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2014/1/1
 */
@SuppressLint("DefaultLocale")
public abstract class BaseItem extends Base{
	
	public final static String NODE_ID = "id";	
	
	protected int id;
	
	public int getId() {
		return id;
	}
	
	//public abstract void parse(BaseItem item,InputStream inputStream);
}
