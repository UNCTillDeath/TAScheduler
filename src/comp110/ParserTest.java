package comp110;

import static org.junit.Assert.*;
import org.junit.*;

public class ParserTest{
    @Test
    public void parseEmployee_test(){
        Parser p = new Parser(null);
        Employee e = p.parseEmployee("src/test.csv");
        
        assertEquals(e.getName(), "Test"); 
        assertEquals(e.getIsFemale(), false);
        assertEquals(e.getCapacity(), 4);
        assertEquals(e.getLevel(), 3);
        
        int[][] capacity = {
        		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0},
        		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0},
        		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0},
        		{0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,1,1,0,0,0,0},
        		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0},
        		{0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0},
        		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        		};
        assertArrayEquals(e.getAvailability(), capacity);
        
        
    }
    @Test
    public void parseSchedule_test(){
    	
    }
    @Test
    public void writeFile_test(){

    }
    @Test 
    public void writeScheduleToJson_Test(){

    }
}