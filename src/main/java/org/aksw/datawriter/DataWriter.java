package org.aksw.datawriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.insert.RowInsertionBuilder;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.QuerySolution;

public class DataWriter implements Runnable {

	private UpdateableDataContext dataContext;
	private String tableName;
	private BlockingQueue<ResultSet> queue;
	private ResultSet sol;
	private String schemaName;
	private List<String> column = new ArrayList<String>();
	private List<String> properties = new ArrayList<String>();
	private final static String SEPERATOR  = ":";

	public DataWriter(UpdateableDataContext dataContext, List<String> map) {
		super();
		this.dataContext = dataContext;
		setMap(map);
	}
	
	
	
	public DataWriter( List<String> map) {
		super();
		setMap(map);
	}



	public String [] getColumn(){
		String[] test = new String[column.size()];
	    return  column.toArray(test);
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setQueue(BlockingQueue<ResultSet> queue) {
		this.queue = queue;
	}

	public void insertValues() throws InterruptedException {
		boolean run = true;

		while (run) {
			sol = queue.take();
			if (sol != null) {
				dataContext.executeUpdate(isnertScript());
			} else {
				run = false;
			}

		}
	}

	private void setMap(List<String> hosts) {
		for (String ma : hosts) {
			column.add(ma.split(SEPERATOR)[1]);
			properties.add(ma.split(SEPERATOR)[0]);
		}

	}

	private UpdateScript isnertScript() {
		return new UpdateScript() {

			public void run(UpdateCallback callback) {

				RowInsertionBuilder insertRow = callback
						.insertInto(dataContext.getSchemaByName(schemaName).getTableByName(tableName));
				while (sol.hasNext()) {
					QuerySolution soln = sol.nextSolution();
					if (getColumnName(soln.get("p").toString()) != null) {
						insertRow.value(getColumnName(soln.get("p").toString()), soln.get("o").toString());
						
					}
				}
				insertRow.execute();
			}
		};
	}

	private String getColumnName(String wert) {
		for (String property : properties) {
			if (wert.contains(property)) {
				return column.get(properties.indexOf(property));
			}
		}
		return null;
	}

	public void run() {
		try {
			insertValues();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
