package org.aksw.rdfreader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class RDFReader implements Runnable {
	private String endpoint;
	private BlockingQueue<ResultSet> queue;
	private String uri;

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public RDFReader(String endpoint) {
		super();
		this.endpoint = endpoint;
	}

	public RDFReader(String endpoint, BlockingQueue<ResultSet> queue) {
		this.endpoint = endpoint;
		this.queue = queue;
	}

	public void getRDFClass(String uri) throws InterruptedException {
		QueryExecution quer = QueryExecutionFactory.sparqlService(endpoint,
				"SELECT distinct ?p ?o { <" + uri.trim() + "> ?p ?o }");
		ResultSet results = quer.execSelect();

		queue.put(results);

	}

	public void readData() throws Exception {
		QueryExecution quer = QueryExecutionFactory.sparqlService(endpoint,
				"select DISTINCT ?prod {?prod ?p ?o. FILTER (regex(str(?prod), \"^" + uri.toString() + "\"))} ");
		ResultSet results = quer.execSelect();
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			getRDFClass(soln.get("prod").toString());
		}

	}

	private ResultSet getPosion() {
		return new ResultSet() {
			
			public void remove() {
				// TODO Auto-generated method stub
				
			}
			
			public QuerySolution nextSolution() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Binding nextBinding() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Object next() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public boolean isOrdered() {
				// TODO Auto-generated method stub
				return false;
			}
			
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return false;
			}
			
			public int getRowNumber() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			public List getResultVars() {
				// TODO Auto-generated method stub
				return null;
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
