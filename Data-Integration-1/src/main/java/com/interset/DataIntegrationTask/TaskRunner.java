package com.interset.DataIntegrationTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.interset.DataIntegrationTask.dao.CSVDao;
import com.interset.DataIntegrationTask.dao.MetaDataDao;
import com.interset.DataIntegrationTask.dao.OutputMetrics;

public class TaskRunner {

	/**
	 * This is main method which is starting point for this application.
	 * It requires 2 arguments to run successfully.
	 * 
	 * @param	args[0] : Path to Json file to read.
	 * @param	args[1] : Path to CSV file to generate output.
	 * 
	 * @throws  IOException : This exception is thrown if there is any IO related issue occurs at run time. Like fileNotFound etc. 
	 * @throws	ParseException : This is related to parsing of date format to string and vice-versa.
	 */
	public static void main(String args[]) throws IOException, ParseException {

		if (args.length != 2) {
			System.out.println("We currently only expect 2 arguments! A path to a JSON file to read, and a path for a CSV file to write.");
			System.exit(1);
		}

		Path jsonFile = null;

		try {
			jsonFile = Paths.get(args[0]);
		} catch (InvalidPathException e) {
			System.err.println("Couldn't convert JSON file argument [" + args[0] + "] into a path!");
			throw e;
		}

		Path csvFile = null;

		try {
			csvFile = Paths.get(args[1]);
		} catch (InvalidPathException e) {
			System.err.println("Couldn't convert CSV file argument [" + args[1] + "] into a path!");
			throw e;
		}

		if (!Files.exists(jsonFile)) {
			System.err.println("JSON file [" + jsonFile.toString() + "] doesn't exist!");
			System.exit(1);
		}

		if (!Files.isWritable(csvFile.getParent())) {
			System.err.println("Can't write to the directory [" + csvFile.getParent().toString() + "] to create the CSV file! Does directory exist?");
			System.exit(1);
		}

		parseJsonFileAndCreateCsvFile(jsonFile, csvFile);

	}

	/**
	 * This method is responsible to parse the Json and provide CSV output as file  and Json output for console.
	 * 
	 * It internally calls FileReader and Buffered Reader to read json file line by line and convert the same in DAO class (i.e., MetaDataDao)
	 * It passes MetaDataDao Object to CSVDao constructor to setup required values.
	 * It calls FileWriter and BufferredWriter to write output to CSV file from CSVDao object.
	 * 
	 * It also populate outputMetrics object to prepare console output.
	 * 
	 * Jackson API is used to read and write the json format.
	 * 
	 * @param	jsonPath : Path to Json file
	 * @param	csvPath : Path to CSV file
	 * 
	 * @throws  IOException : This exception is thrown if there is any IO related issue occurs at run time. Like fileNotFound etc. 
	 * @throws	ParseException : This is related to parsing of date format to string and vice-versa.
	 */
	private static void parseJsonFileAndCreateCsvFile(Path jsonPath, Path csvPath) throws IOException, ParseException {

		File jsonFile = new File(jsonPath.toString());
		File csvFile = new File(csvPath.toString());
		FileWriter fileWriter = null;
		BufferedWriter bufferredWriter = null;

		OutputMetrics outputMetrics = new OutputMetrics();
		FileReader fileReader = new FileReader(jsonFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		try {
			String line;

			fileWriter = new FileWriter(csvFile);
			bufferredWriter = new BufferedWriter(fileWriter);
			List<MetaDataDao> metaDaoList = new ArrayList<MetaDataDao>();
			bufferredWriter.write(CSVDao.getHeaderLine()+System.getProperty("line.separator"));

			while ((line = bufferedReader.readLine()) != null) {
				byte[] jsonData = line.getBytes();
				ObjectMapper objectMapper = new ObjectMapper();

				MetaDataDao dao = objectMapper.readValue(jsonData, MetaDataDao.class);

				CSVDao csvDao = new CSVDao(dao);

				if(metaDaoList.contains(dao)) {
					outputMetrics.addDuplicates();
				} else if(!MetaDataDao.actionList.contains(dao.getActivity())){
					outputMetrics.addNoActionMapping();
				} else {
					String csvLine = csvDao.getCsvLine();
					bufferredWriter.write(csvLine+System.getProperty("line.separator"));
					metaDaoList.add(dao);
					outputMetrics.updateValues(csvDao);
					outputMetrics.updateValues(dao);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bufferredWriter.close();
				fileWriter.close();

				bufferedReader.close();
				fileReader.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		outputMetrics(outputMetrics);
	}

	/**
	 * This method takes OutputMetrics object as an input and print the output to console.
	 * 
	 * @param	metrics : OutputMetrics object to create Json output.
	 * @throws	JsonGenerationException : Exception occurs during Json parsing
	 * @throws	JsonMappingException : Exception occurs during Json parsing
	 * @throws	IOException : This exception is thrown if there is any IO related issue occurs at run time. Like fileNotFound etc.
	 */
	private static void outputMetrics(OutputMetrics metrics) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		StringWriter stringMetrics = new StringWriter();
		objectMapper.writeValue(stringMetrics, metrics);
		System.out.println("Metrics Output :\n"+stringMetrics);
	}
}