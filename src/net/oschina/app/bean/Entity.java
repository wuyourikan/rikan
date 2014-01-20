package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import net.oschina.app.AppException;

public abstract class Entity implements Serializable{
	
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "wyzxwk";
	
	public abstract void parse(InputStream inputStream) throws IOException, AppException;
}
