
public class trialTest {
	
	public static void main(String[] args) {
		int speeds[] = {300, 300, 500, 500, 300};
		int distances[] = {300, 600, 300, 600, 600};
		boolean slowDown[] = {false, false, false, false, true};
		mainCarMove warmUp = new mainCarMove(speeds, distances, slowDown, speeds.length, "false");
		warmUp.go();
	}

}
