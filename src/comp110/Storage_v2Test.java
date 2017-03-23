package comp110;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;

public class Storage_v2Test {

	@Test
	public void test_set_get_username() {
		Storage_v2 storage = new Storage_v2(new Storage_v2.Storage_v2Listener(){
			@Override
			public void storage_get_files_complete(boolean success, String message){
				
			}
			@Override
	        public void storage_save_files_complete(boolean success, String message){
	        	
	        }
		}, ".");
		
		String username = "test_username";
		storage.set_username(username);
		assertTrue(username.equals(storage.get_username()));
	}

	@Test
	public void test_set_get_password() {
		Storage_v2 storage = new Storage_v2(new Storage_v2.Storage_v2Listener(){
			@Override
			public void storage_get_files_complete(boolean success, String message){
				
			}
			@Override
	        public void storage_save_files_complete(boolean success, String message){
	        	
	        }
		}, ".");
		
		String password = "test_password";
		storage.set_password(password);
		assertTrue(password.equals(storage.get_password()));
	}

	@Test
	public void test_get_availability_csv_filename_from_onyen() {
		Storage_v2 storage = new Storage_v2(new Storage_v2.Storage_v2Listener(){
			@Override
			public void storage_get_files_complete(boolean success, String message){
				
			}
			@Override
	        public void storage_save_files_complete(boolean success, String message){
	        	
	        }
		}, ".");
		
		String onyen = "djsteffey";
		String path = "./repo/data/spring-17/staff/" + onyen + ".csv";
		assertTrue(path.equals(storage.get_availability_csv_filename_from_onyen(onyen)));
	}

	@Test
	public void test_get_schedule_json_filename() {
		Storage_v2 storage = new Storage_v2(new Storage_v2.Storage_v2Listener(){
			@Override
			public void storage_get_files_complete(boolean success, String message){
				
			}
			@Override
	        public void storage_save_files_complete(boolean success, String message){
	        	
	        }
		}, ".");
		
		String path = "./repo/testData/schedule.json";
		assertTrue(path.equals(storage.get_schedule_json_filename()));
	}

	@Test
	public void test_get_path_to_onyen_csv_directory() {
		Storage_v2 storage = new Storage_v2(new Storage_v2.Storage_v2Listener(){
			@Override
			public void storage_get_files_complete(boolean success, String message){
				
			}
			@Override
	        public void storage_save_files_complete(boolean success, String message){
	        	
	        }
		}, ".");
		
		String path = "./repo/data/spring-17/staff/";
		assertTrue(path.equals(storage.get_path_to_onyen_csv_directory()));
	}

	@Test
	public void test_get_save_delete_files() {
		final boolean[] result = new boolean[1];
		
		Storage_v2 storage = new Storage_v2(new Storage_v2.Storage_v2Listener(){
			@Override
			public void storage_get_files_complete(boolean success, String message){
				result[0] = success;
			}
			@Override
	        public void storage_save_files_complete(boolean success, String message){
	        	result[0] = success;
	        }
		}, ".");
		storage.set_username("djsteffey");
		storage.set_password("snoogans1278");
		storage.get_files();
		
		// give 10 seconds to complete
		try{
			Thread.sleep(10000);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// check the result was true and check if folder is there
		File f = new File("./repo");
		if ((result[0] == false) || (f.exists() == false)){
			assertTrue("Failed get files", false);
		}

		// check for save
		result[0] = false;
		storage.save_files();
		
		// give 10 seconds to complete
		try{
			Thread.sleep(10000);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// check result was true
		if (result[0] == false){
			assertTrue("Failed save files", false);
		}
		
		// check the delete local storage
		result[0] = storage.delete_storage();
		if ((result[0] == false) || (f.exists() == true)){
			assertTrue("Failed delete local storage", false);
		}
		
		// passed everything
		assertTrue(true);
	}
}
