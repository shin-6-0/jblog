package com.poscodx.jblog.vo;

public class BlogVo {
	private String blogId;
	private String title;
	private String imange;
	public String getBlogId() {
		return blogId;
	}
	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImange() {
		return imange;
	}
	public void setImange(String imange) {
		this.imange = imange;
	}
	@Override
	public String toString() {
		return "BlogVo [blogId=" + blogId + ", title=" + title + ", imange=" + imange + ", getBlogId()=" + getBlogId()
				+ ", getTitle()=" + getTitle() + ", getImange()=" + getImange() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
}
