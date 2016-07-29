package comp110.mlans13.genalgo;

import java.util.Random;

import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Scorecard;
import comp110.Scorer;

/**
 * 
 * @author mlans13
 *
 *	This class is used to work around the way that the scorer is written.  It acts like a scoring algorithm, but
 *	literally just returns schedule it's been given in order to test that schedule directly 
 */

public class TestingSandbox implements SchedulingAlgo {

	public static double testSchedule(Schedule schedule, Random random) {
		// Return the score of the run of an evaluation of the schedule
		Scorecard runVal = Scorer.evaluate(schedule, new TestingSandbox(), random);
		
		// temp
		return runVal.getScore();
	}
	
	@Override
	public Schedule run(Schedule input, Random random) {
		
		// Literally just return the schedule given
		return input;
	}

}
