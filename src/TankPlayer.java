import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TankPlayer implements LevelsData
{
	//starting position of the player
	private int startX;
	private int startY;
	
	//x and y position of the player tank
	private int posx=0;
	private int posy=0;
	
	//speed of the motion of the tank
	private final int SPEED = 2;
	
	//direction of motion of the player tank
	private int direction=0;
	private int blocked;
	
	//array to load and store images of tank in various directions
	private Image img[] = new Image [4];
	private Image lif;
	private Boolean alive = true;
	
	//to store collision area for vertical and horizontal tanks
	private Rectangle collV, collH;
	
	//to store a instance of bullet belonging for that player
	private Bullet bul;
	
	//to store the current health and life of the player 
	private int health;
	private int healthX;
	private int healthY;
	private int healthW;
	private int life;
	private int lifeX;
	private int lifeY;
	
	//to set the position for creation of bullets
	private final int shootx[] = {18,18,-2,38};
	private final int shooty[] = {-2,38,18,18};	
	
	//to set bullet position
	private int x = 0, y = 0;

	//to initialise the contents of the object of the type TankPlayer
	TankPlayer(int posx, int posy, int direction)
	{
		this.posx = posx;
		this.posy = posy;
		this.startX = this.posx;
		this.startY = this.posy;
		this.direction = direction;

		img[0] = Toolkit.getDefaultToolkit().getImage("images/tankUp.gif");
		img[1] = Toolkit.getDefaultToolkit().getImage("images/tankDown.gif");
		img[2] = Toolkit.getDefaultToolkit().getImage("images/tankLeft.gif");
		img[3] = Toolkit.getDefaultToolkit().getImage("images/tankRight.gif");
		
		lif = Toolkit.getDefaultToolkit().getImage("images/tankLife.gif");

		health = 100;
		this.life = 3;

		healthX = 150;
		healthY = 30;
		healthW = 10;
		
		lifeX = 300;
		lifeY = 30;

		collV = new Rectangle(posx+5, posy+0 , 38-10, 38);
		collH = new Rectangle(posx+0, posy+5 , 38, 38-10);

		bul = new Bullet(100);

		blocked = 4;
	}

 	public void drawTank(Graphics2D g2d)
	{
	    g2d.drawImage(img[direction], posx, posy, null);
	    drawHealth(g2d);
	    drawLife(g2d);
	    bul.draw(g2d);
//	    g2d.drawRect(collV.x, collV.y, collV.width, collV.height);
//	    g2d.drawRect(collH.x, collH.y, collH.width, collH.height);
	}
 	
 	private void drawHealth(Graphics2D g2d)
 	{
 		g2d.setColor(Color.GREEN);
 		g2d.drawString("Health: ", healthX, healthY-5);
 		g2d.fillRect(healthX, healthY, health, healthW);
 	}
 	
 	private void drawLife(Graphics2D g2d)
 	{
 		g2d.setColor(Color.GREEN);
 		g2d.drawString("Lives: ", lifeX, lifeY-5);
 		for(int i = 0, count = 0 ; i <= life-1 ; i++, count += 20 )
 			g2d.drawImage(lif, lifeX + count, lifeY, null);
 	}
 	
 	public void setStartPosition(int startX, int startY)
 	{
 		this.startX = startX;
 		this.startY = startY;
 		this.posx = this.startX;
 		this.posy = this.startY;
 		collV = new Rectangle(posx+5, posy+0 , 38-10, 38);
		collH = new Rectangle(posx+0, posy+5 , 38, 38-10);
 	}
 	
 	public void updateTank(int keyCode) throws FileNotFoundException, IOException
	{
		if(keyCode == KeyEvent.VK_UP)        moveUp();
		else if(keyCode == KeyEvent.VK_DOWN) moveDown();
		else if(keyCode == KeyEvent.VK_LEFT) moveLeft();
		else if(keyCode == KeyEvent.VK_RIGHT)moveRight();
		else if(keyCode == KeyEvent.VK_SPACE)fireBullet();
		checkHealth();
		checkLives();
		checkBounds();
		bul.update(x, y);
		x =0; y =0;
    }
 	
 	private void checkLives() 
 	{
		if(life <= 0)
			alive = false;
	}
 	
 	public Boolean getStatus()
 	{
 		return alive;
 	}

	private void checkHealth()
 	{
 		if(health <= 0)
 		{
 			life--;
 			health = 100;
 			posx = startX;
 			posy = startY;
 			collH.x = 0+startX;
 			collH.y = 5+startY;
 			collV.x = 5+startX;
 			collV.y = 0+startY;
 		}
 	}
 	
	private void moveUp()
	{
		if(blocked != 0)
		{
			direction = 0;
			collV.y-=SPEED;
			collH.y-=SPEED;
			posy-=SPEED;
			blocked = 4;
		}
	}
	private void moveDown()
	{
		if(blocked != 1)
		{
			direction = 1;
			collV.y+=SPEED;
			collH.y+=SPEED;
			posy+=SPEED;
			blocked = 4;
		}
	}
	private void moveLeft()
	{
		if(blocked != 2)
		{
			direction = 2;
			collV.x-=SPEED;
			collH.x-=SPEED;
			posx-=SPEED;
			blocked = 4;
		}
	}
	private void moveRight()
	{
		if(blocked != 3)
		{
			direction = 3;
			collV.x+=SPEED;
			collH.x+=SPEED;
			posx+=SPEED;
			blocked = 4;
		}
	}
	private void checkBounds()
	{
		switch(direction)
		{
		case 0: case 1:
		if(collV.y <= 0)
			blocked = direction;
		if(collV.y+collV.height >= 768)
			blocked = direction;
		break;
		case 2: case 3:
			if(collH.x <= 0)
				blocked = direction;
			if(collH.x+collH.width >= 1024)
				blocked = direction;
		break;
		}
	}
	private void fireBullet() throws FileNotFoundException, IOException
	{
		bul.create(posx+shootx[direction], posy+shooty[direction], direction);
		if(bul.getSoundStatus())
			new Sound().gunshotPlayer();
	}
	
	public Rectangle coll()
	{
		switch(direction)
		{
		case 0: case 1:
			return collV;
		case 2: case 3:
			return collH;
		}
		return collV;
	}
	
	public Rectangle collBul()
	{
		return bul.coll();
	}
	
	public void notifyBul(Boolean res)
	{
		bul.notifyColl(res);
	}
	
	public void notifyCollision(Boolean res)
	{
		if(res)
		{
			switch(direction)
			{
			case 0: case 1:
				blocked = direction;
			case 2: case 3:
				blocked = direction;
			}
		}
	}
	
	public void notifyCollisionBull(Boolean res)
	{
		if(res)
			health -=5;
	}
}