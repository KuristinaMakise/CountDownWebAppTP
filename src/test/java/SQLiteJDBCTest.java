import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class SQLiteJDBCTest {
	
	SQLiteJDBC sq = new SQLiteJDBC();
	
	int counter = sq.idDispo();
	
	@Before
	public void init(){
		sq.addCounter("premier");
		counter++;
		sq.addCounter("deuxieme");
		counter++;
		sq.addCounter("troisieme");
		counter++;
		sq.deleteCounter(1);
		sq.setCounter(0, "changer");
	}
	
	
	@Test
	public void testincrement(){
		
		assertTrue(sq.idDispo()==counter);
	}
	
	@Test
	public void testGetCounter(){
		assertEquals(sq.getCounter(2), "troisieme");
	}
	
	@Test
	public void testDeleteCounter(){
		assertNotSame(sq.getCounter(1), "deuxieme");
	}
	
	@Test
	public void testSetCounter(){
		System.out.println(sq.getCounter(0));
		assertEquals(sq.getCounter(0), "changer");
	}

}
