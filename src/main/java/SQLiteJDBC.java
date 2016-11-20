import java.io.File;
import java.net.URL;
import java.sql.*;

public class SQLiteJDBC
{
	private Connection c;
	private Statement stmt;

	public SQLiteJDBC()
	{
		c = null;
		stmt = null;
		try
		{

			URL location = SQLiteJDBC.class.getProtectionDomain().getCodeSource().getLocation();
			String chemin = location.getFile();
			File initialisation = new File(chemin);
			initialisation = initialisation.getParentFile().getParentFile();
			chemin = initialisation.getAbsolutePath();

			System.out.println(chemin);
			
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+chemin+"/counter.db");
			// java.sql.SQLException: path to ':/main/resources/counter.db': 'C:\Users\Alex\Desktop\:' does not exist
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			String sql = "CREATE TABLE COUNTER " +
					"(ID INT PRIMARY KEY     NOT NULL," +
					" VALUE          TEXT    NOT NULL)";
			//String sql = "DROP TABLE COMPANY";
			stmt.executeUpdate(sql);
			// stmt.close();
			// c.commit();
			// c.close();
		}
		catch ( Exception e )
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	public void update(String sql)
	{
		try
		{
			stmt.executeUpdate(sql);
		}catch ( Exception e )
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	public int addCounter(String value)
	{
		int i = idDispo();
		//System.out.println(i);
		String sql = "INSERT INTO COUNTER (ID,VALUE) VALUES ("+i+", \""+value+"\");";
		update(sql);
		return i;
	}

	public String getCounter(int id)
	{
		String value = "";
		try
		{
			ResultSet rs = stmt.executeQuery( "SELECT * FROM COUNTER WHERE ID="+id+";");
			while(rs.next())
			{
				value = rs.getString("value");
			}
			rs.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		if(value.equals(""))
		{
			System.err.println("Attention : un compteur vide a été retourné de la base de données.");
		}
		return value;
	}

	public void setCounter(int id, String value)
	{
	update("UPDATE COUNTER SET VALUE = \""+value+"\" WHERE ID="+id+";");
	}

	public void deleteCounter(int id)
	{
		update("DELETE FROM COUNTER WHERE ID="+id+";");
	}
	
	public int idDispo()
	{
		int idMax = -1;
		int idEnCours = -1;
		try
		{
			ResultSet rs = stmt.executeQuery( "SELECT * FROM COUNTER;");
			while(rs.next())
			{
				idEnCours = rs.getInt("ID");
				if(idEnCours>idMax) idMax = idEnCours;
			}
			rs.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		
		idMax += 1;
		
		//System.out.println("id "+idMax);
		
		return idMax;
	}
}