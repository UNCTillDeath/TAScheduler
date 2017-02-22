package comp110;

public class Credentials {

	private String username;
	private String password;
	
	public Credentials(String user, String pass) {
		username = user;
		password = pass;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
}
