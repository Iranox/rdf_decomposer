package org.aksw.datawriter;



import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.create.TableCreationBuilder;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.Schema;

public class TableCreater {
	private UpdateableDataContext dataContext;
	private Schema schema;
	private String tableName;
	private String[] columnNames;
	private String schemaName;
	private final static int SIZE = 6553;
	
	public TableCreater(UpdateableDataContext dc, String tableName, String[] columnNames) {
		dataContext = dc;
		this.tableName = tableName;
		this.columnNames = columnNames;
	}
	
	

	public void setSchema(String schema) {
		this.schemaName = schema;
	}



	public void createTable() throws Exception{
		schema = dataContext.getSchemaByName(schemaName);
		dataContext.executeUpdate(createTableScript());
	}

	private UpdateScript createTableScript() {
		return new UpdateScript() {

			public void run(UpdateCallback callback) {

				TableCreationBuilder create = callback.createTable(schema, tableName);
				for (String name : columnNames) {
					create.withColumn(name).ofType(ColumnType.STRING).ofSize(SIZE);
				}
				create.execute();

			}
		};
	}

}
