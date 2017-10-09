package com.interset.DataIntegrationTask.dao;

import java.io.File;
import java.text.ParseException;

import com.interset.DataIntegrationTask.util.DateTimeUtils;

/**
 * Objective of this class is to create and store CSV output. One object of this class represents one csv lines.
 */
public class CSVDao {

	private final String timestamp;
	
    private final String action;

    private final String user;

    private final String folder;

    private final String fileName;

    private final String ip;

	public static final String ADD = "ADD";
	public static final String REMOVE= "REMOVE";
	public static final String ACCESSED = "ACCESSED";
	
	/**
	 * Fetches data from MetadataDao and sets the values to individual fields. Parses data to convert for CSV format.
	 * 
	 * @param metadata : MetaDataDao object which need to be converted in csv format.
	 * @throws ParseException : Its related to date parsing which is called for String to  date and vice-versa.
	 */
	public CSVDao(MetaDataDao metadata) throws ParseException {
		timestamp = getTimestamp(metadata.getTimestamp(), metadata.getTimeOffset());
		action = getAction(metadata.getActivity());
		user = getUserName(metadata.getUser());
		File fileAccessed = new File(metadata.getFile());
		folder = getFolderName(fileAccessed);
		fileName = getFileName(fileAccessed);
		ip = metadata.getIpAddr();
	}

	/**
	 * This method is responsible to convert incoming actions from MetaDataDao to CSV format Actions. Mapping for the same are :
	 * 
	 * ADD : CREATED_DOC, ADDED_TEXT, CHANGED_TEXT
	 * REMOVE : DELETED_DOC, DELETED_TEXT, ARCHIVED
	 * ACCESSED : VIEW_DOC
	 * 
	 * @param activity : Original state activity listed in Json file and come via MetadataDao object.
	 * @return String : output action for corresponding activity.
	 */
	private String getAction(String activity) {
		if(activity !=null) {
			switch(activity) {
			case MetaDataDao.CREATED_DOC :
			case MetaDataDao.ADDED_TEXT :
			case MetaDataDao.CHANGED_TEXT :
				return ADD;
			case MetaDataDao.DELETED_DOC :
			case MetaDataDao.DELETED_TEXT :
			case MetaDataDao.ARCHIVED :
				return REMOVE;
			case MetaDataDao.VIEW_DOC:
				return ACCESSED;
			}
		}
		return null;
	}
	
	/**
	 * Parses Timestamp & Timeoffset to corresponding String in ISO8601 format using DateTimeUtils
	 * 
	 * @param timestamp : Timestamp from Json file
	 * @param timeOffset : Timeoffset form Json file
	 * @return String : ISO8601 format string for above parameters
	 * @throws ParseException : Any parsing exception occurs during conversion.
	 */
	private String getTimestamp(String timestamp, String timeOffset) throws ParseException {		
		return DateTimeUtils.getZonedTime(timestamp, timeOffset);
	}

	/**
	 * Fetches User name from Json UserName. It discards everything  after @ symbol to discard domains.
	 * @param user : user from json
	 * @return String : UserName with out domain names
	 */
	private String getUserName(String user) {
		if(user!=null && user.indexOf("@") != -1) {
			return user.substring(0,user.indexOf('@'));
		} else {
			return user;
		}
	}

	/**
	 * Fethces folder name from file path.
	 * @param file : Original file object from path given in json file
	 * @return Folder : Parent folder of file mentioned in Json.
	 */
	private String getFolderName(File file) {
		return file.getParentFile().toString();
	}

	/**
	 * Fetches file name from file path.
	 * @param file : Original file object from path given in json file
	 * @return String : File Name of file mentioned in Json
	 */
	private String getFileName(File file) {
		return file.getName();
	}

	/**
	 * Concatenation of CSVDao properties which need to get populated in csv file.
	 * @return String : Reprsents one line of CSV in CSV format.
	 */
	public String getCsvLine() {
		return ("\""+timestamp+"\",\""+action+"\",\""+user+"\",\""+folder+"\",\""+fileName+"\",\""+ip+"\"");
	}
	
	/**
	 * Generates HeaderLine for CSV
	 * @return String : HeaderLine for CSV output.
	 */
	public static String getHeaderLine() {
		return ("TIMESTP,ACTION,USER,FOLDER,FILENE,IP");
	}

	public String getTimeStamp() {
		return timestamp;
	}

	public String getUser() {
		return user;
	}

	public String getAction() {
		return action;
	}
}
