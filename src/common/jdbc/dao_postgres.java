package common.jdbc;

import java.sql.Connection
import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.PGConnection;

public class dao_postgres extends dao_base
{
	private  PGConnection  connection = null;

	private  final  String  SCHEMA_CREATE = "CREATE SCHEMA \"%s\"";
    private  final  String  DROP_SCHEMA   = "DROP SCHEMA \"%s\""  ;
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public dao_postgres()
	{
		super ("org.postgresql.Driver");
	};
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void setURL (String host, String database, int port)
	{
		if (database.length() > 0)
			this.url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		else
			this.url = "jdbc:postgresql://" + host + ":" + port;
	};
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public Connection getConnection ()
	{
		return (java.sql.Connection) connection;
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void Connect (String login, String password)
	{
		super.Connect(login, password);
		try {
			connection = (PGConnection) DriverManager.getConnection(url, properties);
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
		String sql = String.format(DROP_SCHEMA, schema);
		return execSQL (sql); 
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
