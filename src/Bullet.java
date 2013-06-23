import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;


public class Bullet
{
	Boolean active = false;
	Boolean sound = false;
	Boolean shoot = true;
	private int posx=0;
	private int posy=0;
	private int direction=0;
	private int bulletNo;
	private Image img;
	private Rectangle coll;
	private long beforeTime, afterTime, timeDiff = 0L, sleepTime = 1000;
	private final int speed = 4;
	
	Bullet(int bulletNo)
	{
		img = Toolkit.getDefaultToolkit().getImage("images/Bullet_Final.gif");
		coll = new Rectangle(0+posx,0+posy,7,8);
		this.bulletNo = bulletNo;
	}

	public void update(int x, int y)
	{
		coll.x += x;
		coll.y += y;
		timeDiff = afterTime - beforeTime;
		if(timeDiff >= (sleepTime*1000000L)/2)
			{active = false;shoot = true;}
		if(active)
		{
		if(direction == 0)
			moveDown();
		else if (direction == 1)
			moveUp();
		else if (direction == 2)
			moveLeft();
		else if (direction == 3)
			moveRight();
		else
			System.out.println("Direction error..");
		}
		afterTime = System.nanoTime();
	}
	
	private void moveRight()
	{
		sound = false;
		posx +=speed;
		coll.x =posx;
	}
	
	private void moveUp()
	{
		sound = false;
		posy +=speed;
		coll.y = posy;
	}
	
	private void moveLeft()
	{
		sound = false;
		posx -=speed;
		coll.x = posx;
	}
	
	private void moveDown()
	{
		sound = false;
		posy -=speed;
		coll.y = posy;
	}

	public void create(int posx, int posy, int direction)
	{
		if(shoot)
		{
			sound = true;
			shoot = false;
			bulletNo--;
			beforeTime = System.nanoTime();
			active = true;
		    this.posx = posx;
		    coll.x = posx;
		    this.posy = posy;
		    coll.y = posy;
		    this.direction = direction;
		}
	}

	public void draw(Graphics2D g2d)
	{
		if(active)
		{
		   g2d.drawImage(img, posx, posy, null);
		   g2d.drawRect(coll.x,coll.y,coll.width,coll.height);
		}
	}

	public Rectangle coll()
	{
		if(active)
			return coll;
		else return new Rectangle(-9000,0,0,0);
	}

	public void notifyColl(Boolean res)
	{
		if(res)
		{
			active = false;
		}
	}
	
	public Boolean getStatus()
	{
		return active;
	}
	
	public Boolean getSoundStatus()
	{
		return sound;
	}

	public void setStatus(boolean active) 
	{
		this.active = active;	
	}
}