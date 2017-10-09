package com.interset.DataIntegrationTask.dao;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to store data from Json. One object of this class represents one row in Json file
 */
public class MetaDataDao {
	
	public static final String CREATED_DOC = "createdDoc";
	public static final String ADDED_TEXT = "addedText";
	public static final String CHANGED_TEXT = "changedText" ;
	public static final String DELETED_DOC = "deletedDoc" ;
	public static final String DELETED_TEXT = "deletedText" ;
	public static final String ARCHIVED = "archived" ;
	public static final String VIEW_DOC = "viewedDoc";

	public static final List<String> actionList = Arrays.asList(CREATED_DOC,ADDED_TEXT,CHANGED_TEXT,DELETED_DOC,DELETED_TEXT,ARCHIVED,VIEW_DOC);
	
	private Long eventId;
	private String user;
	private String ipAddr;
	private String file;
	private String activity;
	private String timestamp;
	private String timeOffset;

	/**
	 * Blank constructor required for Json Reader to parse json line and set values for this object via setter methods.
	 */
	public MetaDataDao() {
	}
	
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getTimeOffset() {
		return timeOffset;
	}
	public void setTimeOffset(String timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	/**
	 * Override of equals method will facilitate us in finding duplicates based on eventId.
	 * Whenever criteria for finding duplicate Json line changes. This method will facilitate the change across application.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MetaDataDao) {
			MetaDataDao dao = (MetaDataDao)obj;
			if(dao.eventId.equals(this.eventId)) {
				return true;
			}
		}
		return false;
	}
}