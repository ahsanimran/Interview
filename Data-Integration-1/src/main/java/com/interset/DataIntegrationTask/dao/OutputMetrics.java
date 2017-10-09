package com.interset.DataIntegrationTask.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.interset.DataIntegrationTask.util.DateTimeUtils;


/**
 * Represents output json object for summary output.
 */
public class OutputMetrics {
	private Long linesRead = 0l;
	private Long droppedEventsCount = 0l;
	private Map<String,Long> droppedEvents = new HashMap<String,Long>();
	private Set<String> uniqueUsers = new HashSet<String>();
	private Set<String> uniqueFiles = new HashSet<String>();
	private Date startDate;
	private String startDateTimeStamp;
	private Date endDate;
	private String endDateTimeStamp;
	private Map<String,Long> actions = new HashMap<String,Long>();
	
	private static final String NO_ACTION_MAPPING = "No Action Mapping";
	private static final String DUPLICATES = "Duplicates";

	/**
	 * Read csvDao object and populated output values. like linesRead, User, Action.
	 * 
	 * @param csvDao : Incoming object for creating output state.
	 */
	public void updateValues(CSVDao csvDao) {
		++linesRead;
		addUser(csvDao.getUser());
		addAddAction(csvDao.getAction());
	}
	
	/**
	 * Read metaDataDao Object and populate related properties in output values
	 * 
	 * @param metaDao : Incoming object for creating output state.
	 * @throws ParseException : Exception occurs during date conversions.
	 */
	public void updateValues(MetaDataDao metaDao) throws ParseException {
		setStartDate(metaDao.getTimestamp(), metaDao.getTimeOffset());
		setEndDate(metaDao.getTimestamp(),metaDao.getTimeOffset());
		addUniqueFiles(metaDao.getFile());
	}

	/**
	 * This method represent increment in No Action Mapping count.
	 */
	public void addNoActionMapping() {
		++linesRead;
		++droppedEventsCount;
		Long count = droppedEvents.get(NO_ACTION_MAPPING);
		droppedEvents.put(NO_ACTION_MAPPING, count==null?1:count+1);
	}
	
	/**
	 * This method represent increment in Duplicate event count.
	 */
	public void addDuplicates() {
		++linesRead;
		++droppedEventsCount;
		Long count = droppedEvents.get(DUPLICATES);
		droppedEvents.put(DUPLICATES, count==null?1:count+1);
	}
	
	public void addLinesRead() {
		++linesRead;
	}

	/**
	 * Add user to a set. In last we will get set of distinct users.
	 * @param user : Users from csvDao
	 */
	public void addUser(String user) {
		uniqueUsers.add(user);
	}
	
	/**
	 * Checks if current date passed as argument is before start date. If yes then populate the same in start date.
	 * It will provide start date to us.
	 * @param timestamp : timestamp from json
	 * @param timeOffset : timeoffset from json 
	 * @throws ParseException : date conversion exception if occurs
	 */
	public void setStartDate(String timestamp, String timeOffset) throws ParseException {
		Date newDate = DateTimeUtils.getDate(timestamp);
		if(this.startDate==null ||this.startDate.after(newDate)) {
			this.startDate = newDate;
			this.startDateTimeStamp = DateTimeUtils.getZonedTime(timestamp, timeOffset);
		}
	}

	/**
	 * Checks if current date passed as argument is after end date. If yes then populate the same in end date.
	 * It will provide end date to us.
	 * @param timestamp : timestamp from json
	 * @param timeOffset : timeoffset from json 
	 * @throws ParseException : date conversion exception if occurs
	 */
	public void setEndDate(String timestamp, String timeOffset) throws ParseException {
		Date newDate = DateTimeUtils.getDate(timestamp);
		if(this.endDate==null || this.endDate.before(newDate)) {
			this.endDate = newDate;
			this.endDateTimeStamp = DateTimeUtils.getZonedTime(timestamp, timeOffset);;
		}
	}
	
	/**
	 * Add actions occurred during csv conversion. It will show count at end in json format.
	 * @param action : Action mapped to json activity.
	 */
	public void addAddAction(String action) {
		if(action !=null) {
			Long count = actions.get(action);
			actions.put(action, count==null?1:count+1);
		}
	}

	/**
	 * Add file to a set. In last we will get set of distinct files.
	 * @param file : file from json.
	 */
	private void addUniqueFiles(String file) {
		uniqueFiles.add(file);
	}

	public Long getLinesRead() {
		return linesRead;
	}

	/**
	 * Fetches cumulative count of total events discarded.
	 * @return Long : Count of duplicate events.
	 */
	public Long getDroppedEventsCount() {
		return droppedEventsCount;
	}

	public Map<String, Long> getDroppedEvents() {
		return droppedEvents;
	}

	public long getUniqueUsers() {
		return uniqueUsers.size();
	}

	public long getUniqueFiles() {
		return uniqueFiles.size();
	}

	public String getStartDate() {
		return startDateTimeStamp;
	}

	public String getEndDate() {
		return endDateTimeStamp;
	}

	public Map<String, Long> getActions() {
		return actions;
	}
}