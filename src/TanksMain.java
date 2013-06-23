import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
//import java.text.DecimalFormat;

import javax.swing.JFrame;

class TanksMain extends JFrame implements Runnable, LevelsData
{

	private static final int NUM_BUFFERS = 2;    // used for page flipping
	
	private static int DEFAULT_FPS = 100;
	
	public static int levelNo = 0;
	
	private static final int NO_DELAYS_PER_YIELD = 16;
    /* Number of frames with a delay of 0 ms before the animation thread yields
       to other running threads. */
    
    private static int MAX_FRAME_SKIPS = 5;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered
	
    private static int NUM_FPS = 10;    
    // number of FPS values stored to get an average
    
    private int pWidth, pHeight;     // panel dimensions
    
    // used for storing timing values
    @SuppressWarnings("unused")
	private long prevStatsTime;
    private long gameStartTime;

//    private double fpsStore[];

//	private double fpsStore[];
//    private double averageFPS = 0.0;

    private long framesSkipped = 0L;
    private double upsStore[];
//    private double averageUPS = 0.0;
    
//    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
        
    private Thread animator;            // the thread that performs the animation
    private volatile boolean running = false;    // used to stop the animation thread
    
    private long period;                 // period between drawing in _nanosecs_
    
    // used at game termination
    private volatile boolean gameOver = false;
    private int score = 0;  //?
    private Font font;
    private FontMetrics metrics;
    private boolean finishedOff = false;
    
    // used by quit 'button'
    private volatile boolean isOverQuitButton = false;
    private Rectangle quitArea;
    
    // used by the pause 'button'
    private volatile boolean isOverPauseButton = false;
    private Rectangle pauseArea;
    private volatile boolean isPaused = false;
    
    // used for full-screen exclusive mode  
    private GraphicsDevice gd;
    private Graphics2D g2d;
    private BufferStrategy bufferStrategy;
    
    private volatile int enemies;
    
	TankPlayer tp;// object of the player's tank
	
	TankEnemy te[] = new TankEnemy[10];//object of the enemy's tank
	
	WallBreakable wallB[] = new WallBreakable[50];
	WallUnbreakable wallU[] = new WallUnbreakable[50];
	
    int keyCode;// to store the keystrokes of the user
    
    Collision cd = new Collision();//to check collision
    
    //to store current level
    int level = 0;
    
   
    public TanksMain (long period) throws MalformedURLException
    {
    	super("JTankWars");

   	    this.period = period;
   	    initFullScreen();
    		readyForTermination();
    		
            tp = new TankPlayer(50,120,3);
            for(int i = 0 ; i < enemies ; i++)
            	te[i] = new TankEnemy(900,500,2);
            loadLevel();
    		
    	    addMouseListener( new MouseAdapter() {
   	      public void mousePressed(MouseEvent e)
   	      { testPress(e.getX(), e.getY()); }
   	    });
   	    
   	    addMouseMotionListener( new MouseMotionAdapter() {
   	      public void mouseMoved(MouseEvent e)
   	      { testMove(e.getX(), e.getY());}
   	    });
   	    
   	    // set up message font
   	    font = new Font("SansSerif", Font.BOLD, 24);
   	    metrics = this.getFontMetrics(font);
   	    
   	    // specify screen areas for the buttons
   	    pauseArea = new Rectangle(pWidth-100, pHeight-45, 70, 15);
   	    quitArea = new Rectangle(pWidth-100, pHeight-20, 70, 15);

   	    // initialise timing elements
/*   	    fpsStore = new double[NUM_FPS];
   	    upsStore = new double[NUM_FPS];
   	    for (int i=0; i < NUM_FPS; i++) 
   	    {
   	      fpsStore[i] = 0.0;
   	      upsStore[i] = 0.0;
   	    }
*/   	    gameStart();

    }//end of constructor TanksMain()
    
    private void initFullScreen()
    {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      gd = ge.getDefaultScreenDevice();

      setUndecorated(true);    // no menu bar, borders, etc. or Swing components
      setIgnoreRepaint(true);  // turn off all paint events since doing active rendering
//    setResizable(false);   //disabled in linux because of the gnome taskbar bug

      if (!gd.isFullScreenSupported()) 
      {
        System.out.println("Java Full Screen Exclusive Mode not supported");
        System.exit(0);
      }
      gd.setFullScreenWindow(this); // switch on full-screen exclusive mode

      // we can now adjust the display modes, if we wish
      showCurrentMode();
      
      pWidth = getBounds().width;
      pHeight = getBounds().height;
      
      if(pWidth < 1024 || pHeight < 768)
    	  System.out.println("Resolution too low: "+pWidth+"X"+pHeight);

      setBufferStrategy();
    }  // end of initFullScreen()

    private void setBufferStrategy()
    /* Switch on page flipping: NUM_BUFFERS == 2 so
       there will be a 'primary surface' and one 'back buffer'.

       The use of invokeAndWait() is to avoid a possible deadlock
       with the event dispatcher thread. Should be fixed in J2SE 1.5 

       createBufferStrategy) is an asynchronous operation, so sleep
       a bit so that the getBufferStrategy() call will get the
       correct details.
    */
    { try {
        EventQueue.invokeAndWait( new Runnable() {
          public void run() 
          { createBufferStrategy(NUM_BUFFERS);  }
        });
      }
      catch (Exception e) {  
        System.out.println("Error while creating buffer strategy");  
        System.exit(0);
      }

      try {  // sleep to give time for the buffer strategy to be carried out
        Thread.sleep(500);  // 0.5 sec
      }
      catch(InterruptedException ex){}

      bufferStrategy = getBufferStrategy();  // store for later
    }  // end of setBufferStrategy()


    private void readyForTermination()
    {
  	addKeyListener( new KeyAdapter() {
  	// listen for esc, q, end, ctrl-c on the canvas to
  	// allow a convenient exit from the full screen configuration
         public void keyPressed(KeyEvent e)
         { int keyCode = e.getKeyCode();
           if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
               (keyCode == KeyEvent.VK_END) ||
               ((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
             running = false;
           }
         }
       });

       // for shutdown tasks
       // a shutdown may not only come from the program
       Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run()
         { running = false;   
           finishOff();
         }
       });
    }  // end of readyForTermination()

    private void gameStart()
    // initialise and start the thread 
    { 
      if (animator == null || !running) {
        animator = new Thread(this);
  	  animator.start();
  	  animator.setPriority(9);
      }
    } // end of gameStart()
    
    private void loadLevel()
    {   	
    	this.wallB = new WallBreakable[wallBNo[level]];
    	for(int i = 0 ; i < wallBNo[level] ; i++)
    		this.wallB[i] = new WallBreakable(wallBPositionX[level][i], wallBPositionY[level][i]);
    	
    		this.wallU = new WallUnbreakable[wallUNo[level]];
    	for(int i = 0 ; i < wallUNo[level] ; i++)
    		this.wallU[i] = new WallUnbreakable(wallUPositionX[level][i], wallUPositionY[level][i]);
    	
    	enemies = enemyNo[level];
    	
        for(int i = 0 ; i < enemies ; i++)
        	te[i] = new TankEnemy(enemyStartPositionX[level][i],enemyStartPositionY[level][i],2);
    	
    	tp.setStartPosition(playerStartPositionX[level],playerStartPositionY[level]);
        try {
			Thread.sleep(400);
		} catch (InterruptedException e) {}
    }
    
    private void finishOff()
    /* Tasks to do before terminating. Called at end of run()
       and via the shutdown hook in readyForTermination().

       The call at the end of run() is not really necessary, but
       included for safety. The flag stops the code being called
       twice.
    */
    {
      if (!finishedOff) {
        finishedOff = true;
        restoreScreen();
        System.exit(0);
      }
    } // end of finishedOff()

    private void testPress(int x, int y)
    // Deal with pause and quit buttons.
    { if (isOverPauseButton)
        isPaused = !isPaused;     // toggle pausing
      else if (isOverQuitButton)
        running = false;
    }  // end of testPress()
    
    private void testMove(int x, int y)
    // is (x,y) over the pause or quit buttons?
    { 
      if (running) {   // stops problems with a rapid move after pressing 'quit'
        isOverPauseButton = pauseArea.contains(x,y) ? true : false;
        isOverQuitButton = quitArea.contains(x,y) ? true : false;
      }
    }
    
    public void run()
    /* The frames of the animation are drawn inside the while loop. */
    {
      long beforeTime, afterTime, timeDiff, sleepTime;
      long overSleepTime = 0L;
      int noDelays = 0;
      long excess = 0L;

      gameStartTime = System.nanoTime();
      prevStatsTime = gameStartTime;
      beforeTime = gameStartTime;

  	running = true;

  	while(running) {
  	  try {
		gameUpdate();
	} catch (FileNotFoundException e){e.printStackTrace();}
	catch (IOException e){e.printStackTrace();}     
        screenUpdate();

        afterTime = System.nanoTime();
        timeDiff = afterTime - beforeTime;
        sleepTime = (period - timeDiff) - overSleepTime;  

        if (sleepTime > 0) {   // some time left in this cycle
          try {
            Thread.sleep(sleepTime/1000000L);  // nano -> ms
          }
          catch(InterruptedException ex){}
          overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
        }
        else {    // sleepTime <= 0; the frame took longer than the period
          excess -= sleepTime;  // store excess time value
          overSleepTime = 0L;

          if (++noDelays >= NO_DELAYS_PER_YIELD) {
            Thread.yield();   // give another thread a chance to run
            noDelays = 0;
          }
        }

        beforeTime = System.nanoTime();

        /* If frame animation is taking too long, update the game state
           without rendering it, to get the updates/sec nearer to
           the required FPS. */
        int skips = 0;
        while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
          excess -= period;
  	    try {
			gameUpdate();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}    // update state but don't render
          skips++;
        }
        framesSkipped += skips;
  	}
      finishOff();
    } // end of run()

    private void gameUpdate() throws FileNotFoundException, IOException 
    { 
    	if (!isPaused && !gameOver)
    	{
    		addKeyListener( new KeyAdapter() {
    		  	// listen for esc, q, end, ctrl-c on the canvas to
    		  	// allow a convenient exit from the full screen configuration
    		         public void keyPressed(KeyEvent e)
    		         {
    		        	 keyCode = e.getKeyCode();
    		         }
    		         public void keyReleased(KeyEvent e)
    		         {
    		        	 keyCode = 0;
    		         }
    		});
    		
            tp.updateTank(keyCode);
            for(int i = 0 ; i < enemies ; i++)
            	te[i].updateTank();
            for(int i = 0 ; i < enemies ; i++)
            	{
            	final int j = i;
            	new Thread() {
                    public void run() 
                    { 
                		if(te[j].getStatus())
                    		enemies--;
                	}
                  }.start();
            	}
            	
            System.out.println("Enemies number is: "+enemies);
            detectCollision();
            if(enemies <= 0)
            {
            	level++;
            	loadLevel();
            }
            if(!tp.getStatus())
            	gameOver = true;
            //update the game
    		//by calling some methods
    	}
    }  // end of gameUpdate()
    
    private void detectCollision()
    {
    	Boolean wallBullPlayer = false, wallBullEnemy = false, wallPlayer = false, wallEnemy = false
    	, wallUPlayer, wallUEBull[] , wallUPBull, wallUEnemy[] = null;
    	for(int j = 0 ; j < enemies ; j++)
    		te[j].setPlayerColl(tp.coll());
    	wallUEBull = new Boolean[wallUNo[level]];
    	wallUEnemy = new Boolean[wallUNo[level]];
        for(int i = 0; i < wallUNo[level] ; i++)
        {
            wallUPlayer = cd.testCollision(wallU[i].coll(), tp.coll());
            for(int j = 0 ; j < enemies ; j++)
            	wallUEBull[j] = cd.testCollision(wallU[i].coll(), te[j].collBul());
            
            wallUPBull = cd.testCollision(wallU[i].coll(), tp.collBul());
            for(int j = 0 ; j < enemies ; j++)
            	wallUEnemy[j] = cd.testCollision(wallU[i].coll(), te[j].coll());
            
            //notify the player its collision with unbreakable wall
            tp.notifyCollision(wallUPlayer);
            
            //notify the enemy its collision with unbreakable wall
            for(int j = 0 ; j < enemies ; j++)
            	te[j].notifyCollision(wallUEnemy[j]);
            
            //notify the player's bullet its collision with unbreakable wall
            tp.notifyBul(wallUPBull);
            
            //notify the enemy's bullet its collision with unbreakable wall
            for(int j = 0 ; j < enemies ; j++)
            	te[j].notifyBul(wallUEBull[j]);
        }
        
        for(int i = 0; i < wallBNo[level] ; i++)
        {
        	wallBullPlayer = cd.testCollision(wallB[i].coll(),tp.collBul());
            
        	for(int j = 0 ; j < enemies ; j++)
        		wallBullEnemy = cd.testCollision(wallB[i].coll(),te[j].collBul());
            
            wallPlayer = cd.testCollision(wallB[i].coll(),tp.coll());
            for(int j = 0 ; j < enemies ; j++)
            	wallEnemy = cd.testCollision(wallB[i].coll(), te[j].coll());
            
        	//notify wall the player's bullet collision with that wall
            wallB[i].notifyCollision(wallBullPlayer);
            
            //notify wall the enemy's bullet collision with that wall
            wallB[i].notifyCollision(wallBullEnemy);
            
        	//the player hits the wall, notify the player.
        	tp.notifyCollision(wallPlayer);
        	
        	//the player's bullet hits the wall, notify the player's bullet.
            tp.notifyBul(wallBullPlayer);
            
            //the enemy hits the wall, notify the enemy.
            for(int j = 0 ; j < enemies ; j++)
            	te[j].notifyCollision(wallEnemy);
        	
        	//the enemy's bullet hits the wall, notify the enemy's bullet
            for(int j = 0 ; j < enemies ; j++)
            		te[j].notifyBul(wallBullEnemy);
        }
        
        //notify the enemy the collision of player's bullet with the enemy
        for(int j = 0 ; j < enemies ; j++)
        	te[j].notifyCollisionBull(cd.testCollision(tp.collBul(),te[j].coll())); 
        
        //notify the player the collision of the enemy bullet with the player itself
        for(int j = 0 ; j < enemies ; j++)
        	if(te[j].getBulletStatus())
        	tp.notifyCollisionBull(cd.testCollision(te[j].collBul(),tp.coll()));
        
        //notify the enemy the collision of the enemy's bullet with the player
        for(int j = 0 ; j < enemies ; j++)
        	te[j].notifyBul(cd.testCollision(tp.coll(),te[j].collBul()));
        
        //notify the player's bullet the collision of itself with the enemy
        for(int j = 0 ; j < enemies ; j++)
        	tp.notifyBul(cd.testCollision(te[j].coll(), tp.collBul()));
        
        //notify the enemy's bullet the collision of the player's bullet with the enemy
        for(int j = 0 ; j < enemies ; j++)
        	te[j].notifyBul(cd.testCollision(tp.collBul(),te[j].coll()));
    }//end of detectCollision()
    
    private void screenUpdate()
    // use active rendering
    {   
    	try 
        {
    	g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
        gameRender(g2d);
        g2d.dispose();
        if (!bufferStrategy.contentsLost())
          bufferStrategy.show();
        else
          System.out.println("Contents Lost");
        
        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
      }
      catch (Exception e) 
      { e.printStackTrace();  
        running = false; 
      } 
    }  // end of screenUpdate()
    
    private void gameRender(Graphics2D g2d)
    {
      // clear the background
      g2d.setColor(Color.black);
      g2d.fillRect(0, 0, pWidth, pHeight);

      g2d.setColor(Color.blue);
      g2d.setFont(font);
      // report frame count & average FPS and UPS at top left
  	// gScr.drawString("Frame Count " + frameCount, 10, 25);
//      g2d.drawString("Average FPS/UPS: " + df.format(averageFPS) + " / " +
//                                  df.format(averageUPS), 20, 25);  // was (10,55)

     // draw the pause and quit 'buttons'
      drawButtons(g2d);

      g2d.setColor(Color.green);
      g2d.drawString("Level"+(level+1), 10, 30);
      tp.drawTank(g2d);
      for(int j = 0 ; j < enemies ; j++)
    	  te[j].drawTank(g2d);
      for(int i = 0; i < wallBNo[level] ; i++)
    	  wallB[i].draw(g2d);
      
      for(int i = 0; i < wallUNo[level] ; i++)
    	  wallU[i].draw(g2d);
      
      // draw game elements
      //...
      g2d.setColor(Color.RED);
      g2d.drawRect(0, 0, 1024, 768);
      if (gameOver)
        gameOverMessage(g2d);
    }  // end of gameRender()
    
    private void drawButtons(Graphics2D g2d)
    {
    	g2d.setColor(Color.blue);

      // draw the pause 'button'
      if (isOverPauseButton)
    	  g2d.setColor(Color.green);

      g2d.drawOval( pauseArea.x, pauseArea.y, pauseArea.width, pauseArea.height);
      if (isPaused)
    	  g2d.drawString("Paused", pauseArea.x, pauseArea.y+10);
      else
    	  g2d.drawString("Pause", pauseArea.x+5, pauseArea.y+10);

      if (isOverPauseButton)
    	  g2d.setColor(Color.black);


      // draw the quit 'button'
      if (isOverQuitButton)
    	  g2d.setColor(Color.green);

      g2d.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
      g2d.drawString("Quit", quitArea.x+15, quitArea.y+10);

      if (isOverQuitButton)
    	  g2d.setColor(Color.black);
    }  // drawButtons()
    
    private void restoreScreen()
    /* Switch off full screen mode. This also resets the
       display mode if it's been changed. 
    */
    { 
    	Window w = gd.getFullScreenWindow();
      if (w != null)
        w.dispose();
      gd.setFullScreenWindow(null);
    } // end of restoreScreen()
    
    private void showCurrentMode()
    // print the display mode details for the graphics device
    {
      int width, height;
      DisplayMode dm = gd.getDisplayMode();
      width  = dm.getWidth();
      height = dm.getHeight();
      System.out.println("Current Display Mode: (" + 
                             width + "," + height + "," +
                             dm.getBitDepth() + "," + dm.getRefreshRate() + ")  " );
      if(width < 1280 && height < 768)
      {
    	  System.out.println("< < < < < < Resolution too low > > > > > >");
    	  System.exit(1);
      }
    }
    
    private void gameOverMessage(Graphics2D g2d)
    // center the game-over message in the panel
    {
      String msg = "Game Over.";
  	int x = (pWidth - metrics.stringWidth(msg))/2;
  	int y = (pHeight - metrics.getHeight())/2;
  	g2d.setColor(Color.red);
  	g2d.setFont(font);
  	g2d.drawString(msg, x, y);
    }  // end of gameOverMessage()
    
    public static void main(String [] args)
	{
		int fps = DEFAULT_FPS;
	    if (args.length != 0)
	      fps = Integer.parseInt(args[0]);

	    long period = (long) 1000.0/fps;
	    System.out.println("fps: " + fps + "; period: " + period + " ms;");
	 
			try {
				new TanksMain(period*1000000L);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
    // ms --> nanosecs
	}//end of main()
}//end of TanksMain class