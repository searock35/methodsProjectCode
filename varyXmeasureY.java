
public class varyXmeasureY {
	
	public static void main(String[] args) {
		int speeds[] = {400, 400, 400, 400, 400, 400};
		int distances[] = {100, 200, 300, 400, 500, 600};
		boolean slowDown[] = {false, false, false, false, false, false};
		String dataFile = "ofatFile";
		mainCarMove ofatTest = new mainCarMove(speeds, distances, slowDown, speeds.length, dataFile);
		ofatTest.go();
	}

}
