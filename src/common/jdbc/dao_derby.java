package common.jdbc;

/**
 * Модуль доступа к серверу СУБД Apache Derby 
 */
import java.io.File;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class dao_derby extends dao_base
{
	private  Connection  connection = null;
	
	private  final  String   SCHEMA_CREATE = "CREATE SCHEMA %s";	
	private  final  String   SCHEMA_DROP   = "DROP SCHEMA %s RESTRICT";	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public dao_derby()
	{
		super ("org.apache.derby.jdbc.EmbeddedDriver");
	};
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void setURL (String host, String database, int port)
	{
		if (database.length() > 0) {
			File file = new File(database);
			this.url = "jdbc:derby:" + file.getAbsolutePath();
		}
	};
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public Connection getConnection ()
	{
		return connection;
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void Connect (String login, String password)
	{
		super.Connect(login, password);
		try {
			connection = (Connection) DriverManager.getConnection(url, properties);
		} catch (SQLException e) {
			connection = null;
			e.printStackTrace();
		}
	};
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public boolean createSchema(final String schema)
	{
		String sql = String.format(SCHEMA_CREATE, schema);
		return execSQL (sql); 
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public boolean dropSchema(final String schema)
	{
		String sql = String.format(SCHEMA_DROP, schema);
		return execSQL (sql); 
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
