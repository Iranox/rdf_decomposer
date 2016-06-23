package org.aksw.connectionproperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.jdbc.JdbcDataContext;

public class JDBCConnection {
	private String url;
	private String user;
	private String password;
	
	public JDBCConnection(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public UpdateableDataContext getConnection() throws ClassNotFoundException, SQLException{
		Class.forName(getClassName(url));
		Connection connection = DriverManager.getConnection(url, user, password);
		UpdateableDataContext dataContext = new JdbcDataContext(connection);
		return dataContext;
	}
	
	private String getClassName(String jdbc) {

		if (jdbc.contains("mysql")) {
			return "com.mysql.jdbc.Driver";
		}

		if (jdbc.contains("postgresql")) {
			return "org.postgresql.Driver";
		}

		return null;
	}
	

}
