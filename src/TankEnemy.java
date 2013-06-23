import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TankEnemy extends ArtificialIntelligence implements LevelsData
{
	//to store whether the tank is alive or dead
	private Boolean alive;
	
	private Boolean cleared;
	
	//to store speed
	private final int speed = 1;
	
	//x and y position of the enemy tank
	private int posx=0;
	private int posy=0;
	
	//array to load and store images of tank in various directions
	private Image img[] = new Image [4];
	
	//to store collision area for vertical and horizontal tanks
	private Rectangle collV;
	private Rectangle collH;
	
	//starting position of the enemy tank
	private int startX = 0;
	private int startY = 0;
	
	//to store a instance of bullet belonging for that tank
	private Bullet bul;
	
	//to set the position for creation of bullets
	private final int shootx[] = {18,18,-2,38};
	private final int shooty[] = {-2,38,18,18};	
	
	//to set bullet position
	private int x = 0, y = 0;
	
	//health of the enemy
	private volatile int health;
	
	//lives of the enemy
	private volatile int life;

	//to initialise the contents of the object of the type TankPlayer
	TankEnemy(int posx, int posy, int direction)
	{
		this.posx = posx;
		this.posy = posy;
		this.startX = posx;
		this.startY = posy;
		
		collV = new Rectangle(posx+5, posy+0 , 38-10, 38);
		collH = new Rectangle(posx+0, posy+5 , 38, 38-10);
		
		this.direction = direction;

		img[0] = Toolkit.getDefaultToolkit().getImage("images/enemyUp.gif");
		img[1] = Toolkit.getDefaultToolkit().getImage("images/enemyDown.gif");
		img[2] = Toolkit.getDefaultToolkit().getImage("images/enemyLeft.gif");
		img[3] = Toolkit.getDefaultToolkit().getImage("images/enemyRight.gif");
		

		alive = true;
		
		bul = new Bullet(100);
		
		health = 100;
		
		life = 2;
		
		blocked = 4;
		
		cleared = false;
	}
	
 	public void drawTank(Graphics2D g2d)
	{
 		if(alive)
 		{
 			g2d.drawImage(img[direction], posx, posy, null);
 			bul.draw(g2d);
 			g2d.drawRect(collV.x, collV.y, collV.width, collV.height);
 			g2d.drawRect(collH.x, collH.y, collH.width, collH.height);
 		}
	}
 	
 	public void updateTank() throws FileNotFoundException, IOException
	{
 		if(alive)
 		{
 			switch(direction)
 			{
 			case 0: case 1:
 				coll = collV;
 				break;
 			case 2: case 3:
 				coll = collH;
 				break;
 			}
 			super.updateColl();
 			if(shoot)
 				fireBullet();
 			if(health <= 0)
 			{
 				life --;
 				health = 100;
 				resetPos();
 			}
 			bul.update(x, y);
 			x = 0; y = 0;
 			if(this.life <= 0)
			{
				alive = false;
				bul.setStatus(false);
			}
 		}
		System.out.println("Enemy's health is "+health);
		System.out.println("Enemy's life is "+life);
		System.out.println("Current collision: "+collH);
    }

 	public void setStartPos(int x, int y)
 	{
 		startX = x;
 		startY = y;
 	}
 	public void setPlayerColl(Rectangle coll)
 	{
 		playerColl = coll;
 	}
 		
 	public Boolean getStatus()
 	{
 		if(!cleared & !alive)
 		{
 			cleared = true;
 			return true;
 		}
 		else
 			return false;
 	}
 	

 	@Override
 	protected void moveUp()
	{
		if(blocked != 0)
		{
			direction = 0;
			collV.y-=speed;
			collH.y-=speed;
			posy-=speed;
		}
	}

 	@Override
	protected void moveDown()
	{
		if(blocked != 1)
		{
			blocked = 4;
			direction = 1;
			collV.y+=speed;
			collH.y+=speed;
			posy+=speed;
		}
	}
	
	@Override
	protected void moveLeft()
	{
		if(blocked != 2)
		{
			blocked = 4;
			direction = 2;
			collV.x-=speed;
			collH.x-=speed;
			posx-=speed;
		}
	}

	@Override
	protected void moveRight()
	{
		if(blocked != 3)
		{
			blocked = 4;
			direction = 3;
			collV.x+=speed;
			collH.x+=speed;
			posx+=speed;
		}
	}
	private void fireBullet() throws FileNotFoundException, IOException
	{
		switch(direction)
		{
		case 0: case 1: case 2: case 3:
		bul.create(posx+shootx[direction], posy+shooty[direction], direction);
		if(bul.getSoundStatus())
			new Sound().gunshotEnemy();
		}
	}

	public Rectangle coll()
	{
		if(alive)
		switch(direction)
		{
		case 0: case 1:
			this.coll = collV;
			return collV;
		case 2: case 3:
			this.coll = collH;
			return collH;
		}
		return new Rectangle(0,0,0,0);
	}
	
	private void resetPos()
	{
		posx = startX;
		posy = startY;
		collH.x = posx;
		collH.y = posy+5;
		collV.x = posx+5;
		collV.y = posy;
	}

	public Rectangle collBul()
	{
		return bul.coll();
	}

	public void notifyBul(Boolean res)
	{
		if(alive)
			bul.notifyColl(res);
	}

	public void notifyCollision(Boolean res)
	{
		if(res)
			blocked = direction;
	}

	public void notifyCollisionBull(Boolean res)
	{
		if(res && alive)
		{
			health-=50;
		}
	}

	public Boolean getBulletStatus()
	{
		return bul.getStatus();
	}
}