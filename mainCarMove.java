import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class mainCarMove {

    JFrame frame;
    //JFrame textFrame;
    DrawPanel drawPanel;

    

    private boolean timerOn = false;
    private boolean moving = false;
    private boolean newRun = false;

    private int screenWidth = 1800;
    private int screenHeight = 500;
    
    private double timerTime = 0;
    private long firstTimerTime;
    
    private long startTime;
    private long firstPassTime;
    private long lastPassTime;
    
    private int runs = 26;
   
    private double runTimes[];
    private double firstDelta[];
    private double realTimes[];
    private int currentRun;
    
    private int distances[];
    private int speeds[];
    private boolean slowDown[];
    private int refresh = 90;
    
    //distance between lines
    private int distance;
    //whether car slows down halfway through
    private boolean halfSpeed;
    //speed in pixels per second
    private int speed;
    
    Out outputFile;

    
    private int oneX = 0;
    private int oneY = (screenHeight / 2);
    
    private int carLength = 40;
        
    public static void main(String[] args) {
    	int distances[] = {300, 200, 200, 400, 400, 
        		200, 200, 400, 400, 200, 200, 400, 
        		400, 200, 200, 400, 400, 200, 200, 
        		400, 400, 200, 200, 400, 400, 300};
    	int speeds[] = {450, 300, 300, 300, 300, 600, 600, 600, 600,
        		300, 300, 300, 300, 600, 600, 600, 600,
        		300, 300, 300, 300, 600, 600, 600, 600, 450};
    	boolean slowDown[] = {false, false, true, false, true,
    	    		false, true, false, true,
    	    		false, true, false, true,
    	    		false, true, false, true,
    	    		false, true, false, true,
    	    		false, true, false, true, false};
        mainCarMove main = new mainCarMove(speeds, distances, slowDown, speeds.length, "mainData");
        main.go();
    }
    
    public mainCarMove(int[] speeds, int[] distances, boolean[] slowDown, int runs, String dataFile) {
    	this.speeds = speeds;
    	this.distances = distances;
    	this.slowDown = slowDown;
    	this.runs = runs;
    	this.outputFile = new Out(dataFile);
    	runTimes = new double[runs];
    	firstDelta = new double[runs];
    	realTimes = new double[runs];
    }
    public void go() {
    	DrawPanel panel = new DrawPanel();
    	currentRun = 0;
    	speed = speeds[0];
    	distance = distances[0];
    	halfSpeed = slowDown[0];
    	Listener listener = new Listener();
        frame = new JFrame("Test");
        frame.addKeyListener(listener);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drawPanel = new DrawPanel();

        frame.getContentPane().add(BorderLayout.CENTER, drawPanel);

        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(screenWidth, screenHeight);
        frame.setBackground(Color.WHITE);
        frame.setLocation(375, 55);
        panel.animate();
        
        //textFrame = new JFrame("Data");
    }

    public class DrawPanel extends JPanel {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DrawPanel() {
    		setPreferredSize(new Dimension(screenWidth, screenHeight));
    	}
    	
        public void paintComponent(Graphics g) {
        	g.setColor(Color.BLACK);
        	String textString;
        	
        	if(currentRun < runs) {
            	textString = String.format("%.2f" + "ms, Saves: %d", timerTime, currentRun);
        	} else {
        		textString = "You're Done!";
        	}
        	
            g.drawString(textString, screenWidth / 2, (int) (screenHeight * .75));
            g.fillRect(0, oneY - (27), screenWidth, 54);
           
            g.setColor(Color.WHITE);
            int linePos = (screenWidth / 2) - (distance / 2);
            g.drawLine(linePos, -screenHeight, linePos, screenHeight);    
            g.drawLine(linePos + distance, -screenHeight, linePos + distance, screenHeight);
            g.drawLine(linePos + 1, -screenHeight, linePos + 1, screenHeight);    
            g.drawLine(linePos + distance + 1, -screenHeight, linePos + distance + 1, screenHeight);
 
            g.setColor(Color.YELLOW);
            g.fillRect(0, screenHeight / 2, screenWidth, 3);
           
            g.setColor(Color.RED);      
            g.fillRect(oneX, oneY + 5, carLength, 18);
            
            }
       
        private void animate() {
        	double movePos;
        	int currentOneX = 0;
			long newFirstTime = 0;
            while(oneX < screenWidth){
            	if(newRun) {
            		currentOneX = 0;
            		newFirstTime = 0;
            		newRun = !newRun;
            	}
            	
            	long time = System.currentTimeMillis();
            	if(moving) {

            		if(halfSpeed && (oneX + carLength) > (screenWidth / 2)) {
            			
            			if (currentOneX == 0) {
            				currentOneX = oneX;
            				newFirstTime = System.currentTimeMillis();
            			}
            			
            			double halfOneX = ((double)(System.currentTimeMillis() - newFirstTime)) * (0.75 * speed / 1000.0);
            			movePos = currentOneX + halfOneX;
            			
            		} else {
                		movePos = (((double)(System.currentTimeMillis() - startTime)) * (speed / 1000.0));

            		}
            		
            		oneX = (int) movePos;
            		if (firstPassTime == 0 && oneX + carLength > (screenWidth / 2) - (distance / 2)) {
            			firstPassTime = System.currentTimeMillis();
            		} else if (lastPassTime == 0 && oneX + carLength > (screenWidth / 2) + (distance / 2)) {
            			lastPassTime = System.currentTimeMillis();
            		}
            	}
            	frame.repaint();
            	while (System.currentTimeMillis() - time < (1000 / refresh)) {            		
            		

            		if(timerOn) {
                		timerTime = (int) (System.currentTimeMillis() - firstTimerTime);
            		}
            	}
            	
            	if (oneX > screenWidth - 20) {
            		moving = false;
            	}
            }  
        }
    }

    private void reset() {
    	if(oneX == 0) {
    		timerTime = 0;
    	}
    	oneX = 0;
    	moving = false;
    	firstPassTime = 0;
    	lastPassTime = 0;
    	newRun = true;
    }
    
    private void saveTime() {
    	runTimes[currentRun] = timerTime;
    	firstDelta[currentRun] = firstTimerTime - firstPassTime;
    	realTimes[currentRun] = (lastPassTime - firstPassTime);

    	currentRun++;
    	if(currentRun < runs) {
	    	speed = speeds[currentRun];
	    	distance = distances[currentRun];
	    	halfSpeed = slowDown[currentRun];
    	}
    	reset();
    	
    }
    
    private void takeTime() {
    	if(timerOn) {
			timerOn = false;
		} else {
			timerOn = true;
			firstTimerTime = System.currentTimeMillis();
		}
    	
    }
   
    
    private class Listener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			char c = arg0.getKeyChar();
			if(c == ' ') {
				if(oneX == 0) {
					moving = !moving;
					startTime = System.currentTimeMillis();
				} else {
					takeTime();
				}
				
					
			} else if (c == 'r') {
				reset();
			} else if (c == 'n') {
				moving = !moving;
				startTime = System.currentTimeMillis();
				
			} else if (c == 's') {
				saveTime();
			} else if (c == 'h') {
				for (int i = 0; i < runTimes.length; i++) {
					System.out.println(runTimes[i]);
					outputFile.println(runTimes[i]);
				}
				
				for (int i = 0; i < runTimes.length; i++) {
					System.out.println(firstDelta[i]);
					outputFile.println(firstDelta[i]);
				}
				
				for (int i = 0; i < runTimes.length; i++) {
					System.out.println(realTimes[i]);
					outputFile.println(realTimes[i]);
				}
			}
			
			
		}

    }
}


