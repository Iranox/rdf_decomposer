package org.aksw.rdfdecomposer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aksw.connectionproperties.JDBCConnection;
import org.aksw.datawriter.DataWriter;
import org.aksw.datawriter.TableCreater;
import org.aksw.rdfreader.RDFReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hp.hpl.jena.query.ResultSet;

public class RDFdecomposer {

	@Parameter(names = "-database", description = "database")
	private String database;
	@Parameter(names = "-url", description = "Url for the triple store")
	private String url;
	@Parameter(names = "-password", description = "password for the triple store")
	private String password;
	@Parameter(names = "-user", description = "username for the triple store")
	private String user;
	@Parameter(names = "-targetUrl", description = "Url for the target system, e.g. a mysql database")
	private String targetUrl;
	@Parameter(names = "-uri", description = "a rdf uri , e.g. http://db.uwaterloo.ca/~galuc/wsdbm/Product")
	private String uri;
	@Parameter(names = "-map", description = "Map")
	private List<String> hosts = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		RDFdecomposer main = new RDFdecomposer();
		new JCommander(main, args);
		main.run();
	}

	public void run() throws Exception {
		 createTable();
		 readData();
	}

	private String getSchemaName() {
		String[] urlSplit = targetUrl.split("/");
		return urlSplit[urlSplit.length - 1];
	}

	private void createTable() throws Exception {
		TableCreater table = new TableCreater(new JDBCConnection(targetUrl, user, password).getConnection(), database,
				                              new DataWriter(hosts).getColumn());
		table.createTable();
	}

	private void readData() throws Exception {
		JDBCConnection jdbc = new JDBCConnection(targetUrl, user, password);
		BlockingQueue<ResultSet> queue = new ArrayBlockingQueue<ResultSet>(1024);
		RDFReader reader = new RDFReader(url, queue);
		reader.setUri(uri);
		DataWriter writer = new DataWriter(jdbc.getConnection(), hosts);
		writer.setQueue(queue);
		writer.setTableName(database);
		writer.setSchemaName(getSchemaName());
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(reader);
		executor.execute(writer);
		executor.shutdown();

	}
}
