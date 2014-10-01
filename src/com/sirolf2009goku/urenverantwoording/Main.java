package com.sirolf2009goku.urenverantwoording;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.hardmatch.neo4j.cypher.CypherHelper;

public class Main {
	
	private GraphDatabaseService graph;
	private ExecutionEngine engine;
	private final String DB_PATH = "C:/Users/Floris/Documents/Neo4j/Urenverantwoording.graphdb";
	private BufferedReader reader;

	public Main() {
		System.out.println("Starting up");
		this.graph = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        engine = new ExecutionEngine(graph);
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Done");
        try {
			run();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		new Main();
	}

}
