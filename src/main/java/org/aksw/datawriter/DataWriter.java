package org.aksw.datawriter;

import java.util.concurrent.BlockingQueue;


import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.insert.RowInsertionBuilder;
import org.apache.metamodel.schema.Column;

import com.hp.hpl.jena.query.QuerySolution;

public class DataWriter implements Runnable{

	private UpdateableDataContext dataContext;
	private String tableName;
	private BlockingQueue<QuerySolution> queue;
	private QuerySolution sol;
	private String schemaName;

	public DataWriter(UpdateableDataContext dataContext) {
		super();
		this.dataContext = dataContext;
	}
	

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}





	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setQueue(BlockingQueue<QuerySolution> queue) {
		this.queue = queue;
	}
	
	public void insertValues() throws InterruptedException{
		boolean run = true;
	
		while(run){
			sol = queue.take();
			if(sol.get( dataContext.getSchemaByName(schemaName).getTableByName(tableName).getColumns()[0].getName()) != null){
				dataContext.executeUpdate(isnertScript());
			}
			else{
				run = false;
			}
			
		}
	}
	
	private UpdateScript isnertScript(){
		return new UpdateScript() {
			
			public void run(UpdateCallback callback) {
				
				RowInsertionBuilder insertRow = callback.insertInto(dataContext.getSchemaByName(schemaName).getTableByName(tableName));
				for (Column col : dataContext.getSchemaByName(schemaName).getTableByName(tableName).getColumns()) {
					System.out.println(sol.get(col.getName()));
					if( sol.get(col.getName()) != null){
						insertRow.value(col, sol.get(col.getName()).toString());
					}
						
					
					
				}
				insertRow.execute();
				
			}
		};
	}




	public void run() {
		try {
			insertValues();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
