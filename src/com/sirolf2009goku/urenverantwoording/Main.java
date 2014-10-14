package com.sirolf2009goku.urenverantwoording;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.sirolf2009.util.neo4j.cypher.CypherHelper;

public class Main {

	private GraphDatabaseService graph;
	private ExecutionEngine engine;
	private String DB_PATH = "Urenverantwoording.graphdb";
	private BufferedReader reader;
	private boolean debugging;

	public Main(Options options, String[] args) {
		try {
			init(options, args);
			run();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void init(Options options, String[] args) throws ParseException {
		System.out.println("Starting up");
		BasicParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);
		if(cmd.hasOption("p")) {
			DB_PATH = cmd.getOptionValue("p");
		}
		if(cmd.hasOption("d")) {
			debugging = true;
		}

		File file = new File(DB_PATH);
		if(!file.exists()) {
			System.out.println("Creating database");
			file.mkdir();
		}

		this.graph = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		engine = new ExecutionEngine(graph);
		reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Done");
	}

	public void run() throws IOException {
		while(true) {
			System.out.println("Type a command");
			String input = reader.readLine();
			if(input.equalsIgnoreCase("STOP") || input.equalsIgnoreCase("EXIT")) {
				break;
			} else if(input.equalsIgnoreCase("SEND")) {
				System.out.println("Cypher:");
				String cypher = reader.readLine();
				System.out.println(CypherHelper.Cypher(engine, cypher).dumpToString());
			} else if(input.equalsIgnoreCase("ADD JOB")) {
				System.out.println("Job Name:");
				String jobName = reader.readLine();
				String cypher = String.format("CREATE (job:Job {name:\"%s\"}) RETURN job", jobName);
				sendCypher(cypher);
			} else if(input.equalsIgnoreCase("ADD PERSON")) {
				System.out.println("Person Name:");
				String personName = reader.readLine();
				String cypher = String.format("CREATE (person:Person {name:\"%s\"}) RETURN person", personName);
				sendCypher(cypher);
			} else if(input.equalsIgnoreCase("ADD PERSON TO JOB")) {
				System.out.println("Person Name:");
				String personName = reader.readLine();
				System.out.println("Job Name:");
				String jobName = reader.readLine();
				String cypher = String.format("MATCH (person:Person {name:\"%s\"}), (job:Job {name:\"%s\"}) CREATE (person)-[r:WORKS_ON]->(job) RETURN r", personName, jobName);
				sendCypher(cypher);
			} else if(input.equalsIgnoreCase("START WORK ON")) {
				System.out.println("Your Name:");
				String personName = reader.readLine();
				System.out.println("Job Name:");
				String jobName = reader.readLine();
				System.out.println("Description:");
				String description = reader.readLine().replace(" ", "_");

				long startTime = System.currentTimeMillis();
				System.out.println("Work started. Type anything to complete");
				reader.readLine();
				long time = System.currentTimeMillis() - startTime;

				int seconds = (int) (time / 1000) % 60 ;
				int minutes = (int) ((time / (1000*60)) % 60);
				int hours   = (int) ((time / (1000*60*60)) % 24);
				String dateFormat = String.format("%dh%dm%ds", hours, minutes, seconds);

				String cypher = String.format("MATCH (person:Person {name:\"%s\"})-[r:WORKS_ON]->(job:Job {name:\"%s\"}) SET r.%s=\"%s\" RETURN r", personName, jobName, description, dateFormat);
				sendCypher(cypher);
			} else if(input.equalsIgnoreCase("LIST WORK")) {
				String cypher = "MATCH (a:Person)-[r]-(b:Job) return *";
				System.out.println(CypherHelper.Cypher(engine, cypher).dumpToString());
			} else {
				System.out.println("Unknown Syntax. Type \"help\" for help");
			}
		}
		System.out.println("Shutting down database");
		graph.shutdown();
	}

	public void sendCypher(String cypher) {
		ExecutionResult result = CypherHelper.Cypher(engine, cypher);
		if(debugging) {
			System.out.println(result.dumpToString());
		}
	}

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(new Option("p", true, "The path of the Neo4J database"));
		options.addOption(new Option("d", false, "Show debugging logs"));
		new Main(options, args);
	}

}
