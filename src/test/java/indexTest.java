import org.junit.Before;
import org.junit.Test;

import net.sourceforge.jwebunit.junit.JWebUnit;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

public class indexTest {
	
	
	 @Before
	 public void setup() {
		 setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		 setBaseUrl("http://localhost:8080/CountDownWebAppTP");

	}
	 
	 @Test
	 public void testIndexEntryOpen(){
		 beginAt("index.jsp");
		 assertTitleEquals("Counters");
		 assertButtonPresentWithText("Create a counter");
		 assertButtonPresentWithText("Delete cookies");
		 assertElementNotPresent("countername");
		 assertElementNotPresent("language");
		 assertElementNotPresent("day");
		 assertElementNotPresent("month");
		 assertElementNotPresent("year");
		 assertElementNotPresent("hour");
		 assertElementNotPresent("minute");
		 assertElementNotPresent("second");
		 assertButtonNotPresentWithText("Validate this counter");
		 assertButtonNotPresentWithText("modify");
		 assertButtonNotPresentWithText("delete");
		 assertButtonNotPresentWithText("Modify this counter");
	 }
	 
	 @Test
	 public void testAddCounter(){
		 beginAt("index.jsp");
		 clickButtonWithText("Create a counter");
		 assertButtonPresentWithText("Validate this counter");
		 assertElementPresent("countername");
		 assertElementPresent("language");
		 assertElementPresent("day");
		 assertElementPresent("month");
		 assertElementPresent("year");
		 assertElementPresent("hour");
		 assertElementPresent("minute");
		 assertElementPresent("second");
		 assertButtonPresentWithText("Validate this counter");
		
	 }
	 
	
}
