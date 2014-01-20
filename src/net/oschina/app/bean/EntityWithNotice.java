package net.oschina.app.bean;

public abstract class EntityWithNotice extends Entity{
	
	protected Notice notice;
	
	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}
}
