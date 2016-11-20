import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class WebSocketTest {
	
	WebSocket ws = new WebSocket();
	Date d = new Date(200, 10, 15, 11, 20, 00);
	
	@Test
	public void testStringToDate(){
		assertEquals(ws.stringToDate("15/11/2100 11:20:00"), d);
	}
	
	@Test
	public void testDiffNonNulle(){
		System.out.println(d);
		assertNotSame(ws.diff("Europe/Paris", d),"0a0m0j0h0m0s");
	}
	
	@Test
	public void testDiff(){
		assertEquals(ws.diff("Europe/Moscow", new Date()),"0a0m0j0h0m0s");
	}

}
