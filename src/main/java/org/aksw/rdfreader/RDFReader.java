package org.aksw.rdfreader;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFReader implements Runnable {
	private String endpoint;
	private BlockingQueue<QuerySolution> queue;
	private String query;
	
	

	public RDFReader(String endpoint, String query) {
		super();
		this.endpoint = endpoint;
		this.query = query;
	}

	public RDFReader(String endpoint, BlockingQueue<QuerySolution> queue, String query) {
		this.endpoint = endpoint;
		this.queue = queue;
		this.query = query;
	}

	public String[] getVariable(String query) {
		QueryExecution quer = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = quer.execSelect();
		String[] resultVar = new String[results.getResultVars().size()];
		for (int i = 0; i < results.getResultVars().size(); i++) {
			resultVar[i] = results.getResultVars().get(i).toString();
		}
		return resultVar;
	}

	public void readData() throws Exception {
		QueryExecution quer = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = quer.execSelect();
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			queue.put(soln);
		}
		queue.put(getPosion());

	}
	
	private QuerySolution getPosion(){
		return new QuerySolution() {
			
			public Iterator varNames() {
				return null;
			}
			
			public Resource getResource(String arg0) {
				return null;
			}
			
			public Literal getLiteral(String arg0) {
				return null;
			}
			
			public RDFNode get(String arg0) {
				return null;
			}
			
			public boolean contains(String arg0) {
				return false;
			}
		};
	}

	public void run() {
		try {
			readData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
