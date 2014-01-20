package net.oschina.app.bean;

import java.util.Map;

public abstract class Base extends EntityWithNotice{
	
	/*public final String typeName = "Base";
	public final int typeID = -1;*/
	
	protected String cacheKey;
	
	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public abstract String getHttpGetUrl(Map<String, Object> map);
	
	public abstract String getHttpPostUrl();
	
	public abstract String getCacheKey(Map<String, Object> map);

}
