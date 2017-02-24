package comp110;

import javafx.application.Application;

public class Main {

	public static void main(String[] args) {
		
		Controller controller = new Controller();
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			done = controller.getDone();
		}
		
	}

}
