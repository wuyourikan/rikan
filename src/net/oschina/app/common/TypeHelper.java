package net.oschina.app.common;

public class TypeHelper {
	/*public final static int LV_ARTICLE = 0x01;
	public final static int LV_TOPIC = 0x02;                                                                                //次数有改动
	public final static int LV_SPECIAL = 0x03;
	public final static int LV_COMMENT = 0x04;
	public final static int LV_FAVORITE = 0x05;
	public final static int LV_SEARCH = 0x06;*/
	
	public final static int ARTICLE = 0x01;
	public final static int TOPIC 	= 0x02;                                                                                //次数有改动
	public final static int SPECIAL = 0x03;
	public final static int COMMENT = 0x04;
	public final static int FAVORITE = 0x05;
	public final static int SEARCH 	= 0x06;
	
	/* 文章栏目类型 */
	public static class Article{
		public final static int CATALOG_COUNT 		= 5;
		public final static int HEADIMG_COUNT 		= 3;
		
		public final static int CATALOG_NEWS 		= 0x01;	//时事
		public final static int CATALOG_RECOMMEND 	= 0x02;	//推荐
		public final static int CATALOG_ZATAN 		= 0x03;	//杂谈
		public final static int CATALOG_ALL 		= 0x04;	//全部
		public final static int CATALOG_SPECIAL 	= 0x05;	//专题文章
	}
	/* 话题栏目类型 */
	public static class Topic{
		public final static int CATALOG_COUNT 		= 3;
		
		public final static int CATALOG_SIXIANG 	= 0x01;	//思想
		public final static int CATALOG_ZHENGJING 	= 0x02;	//政经
		public final static int CATALOG_WENSHI		= 0x03;	//文史
	}
	
	/* 专题栏目类型 */
	public static class Special{
		public final static int CATALOG_COUNT 		= 2;
		
		public final static int CATALOG_LATEST		= 0x01;	//最新专题
		public final static int CATALOG_IMPORTANT 	= 0x02;	//重点专题
	}
	
	
	public static String getTypeName(int type) {
		switch(type){
		case ARTICLE:
			return "article";
		case TOPIC:
			return "topic";
		case SPECIAL:
			return "special";
		default:
			return null;
		}
	}
	
	public static String getCatalogName(int type, int catalog) {
		switch(type){
		case ARTICLE:
			switch (catalog) {
			case Article.CATALOG_NEWS:
				return "news";
			case Article.CATALOG_RECOMMEND:
				return "recommend";
			case Article.CATALOG_ZATAN:
				return "zatan";
			case Article.CATALOG_ALL:
				return "all";
			case Article.CATALOG_SPECIAL:
				return "special";
			}
		case TOPIC:
			switch (catalog) {
			case Topic.CATALOG_SIXIANG:
				return "sixiang";
			case Topic.CATALOG_ZHENGJING:
				return "zhengjing";
			case Topic.CATALOG_WENSHI:
				return "wenshi";
			}
		case SPECIAL:
			switch (catalog) {
			case Special.CATALOG_LATEST:
				return "latest";
			case Special.CATALOG_IMPORTANT:
				return "important";
			}
		default:
			return null;
		}
	}
}
