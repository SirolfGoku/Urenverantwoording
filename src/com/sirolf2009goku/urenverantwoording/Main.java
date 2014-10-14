package com.sirolf2009goku.urenverantwoording;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.sirolf2009.util.neo4j.cypher.CypherHelper;

public class Main {
	
	private GraphDatabaseService graph;
	private ExecutionEngine engine;
	private String DB_PATH = "/Urenverantwoording.graphdb";
	private BufferedReader reader;

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
        		System.out.println("Exiting...");
        		break;
        	} else if(input.equalsIgnoreCase("SEND")) {
        		System.out.println("Cypher:");
        		String cypher = reader.readLine();
        		System.out.println(CypherHelper.Cypher(engine, cypher).dumpToString());
        	} else if(input.equalsIgnoreCase("ADD JOB")) {
        		System.out.println("Job Name:");
        		String jobName = reader.readLine();
        		String cypher = String.format("CREATE (job:JOB {name:\"%s\"}) RETURN job", jobName);
        		System.out.println(CypherHelper.Cypher(engine, cypher).dumpToString());
        	} else if(input.equalsIgnoreCase("ADD PERSON")) {
        		System.out.println("Person Name:");
        		String personName = reader.readLine();
        		String cypher = String.format("CREATE (person:Person {name:\"%s\"}) RETURN person", personName);
        		System.out.println(CypherHelper.Cypher(engine, cypher).dumpToString());
        	} else if(input.equalsIgnoreCase("ADD PERSON TO JOB")) {
        		System.out.println("Person Name:");
        		String personName = reader.readLine();
        		String cypher = String.format("CREATE (person:Person {name:\"%s\"}) RETURN person", personName);
        		System.out.println(CypherHelper.Cypher(engine, cypher).dumpToString());
        	} else {
        		System.out.println("Unknown Syntax. Type \"help\" for help");
        	}
        }
	}

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(new Option("p", true, "The path of the Neo4J database"));
		new Main(options, args);
	}

}
