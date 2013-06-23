import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class WallBreakable 
{
	//to store the status of the wall whether active or not
	private Boolean active = true;
	
	//x and y position of the Breakable wall
	private int posx=0;
	private int posy=0;
	
	//to store image of the tanks
	private Image img;
	
	//to store collision area for vertical and horizontal tanks
	private Rectangle coll;
	
	//to initialise the contents of the object of the type TankPlayer
	WallBreakable(int posx, int posy)
	{
		this.posx = posx;
		this.posy = posy;
		img = Toolkit.getDefaultToolkit().getImage("images/Block1.gif");
		this.coll = new Rectangle(0+this.posx, 0+this.posy , 40, 40);
	}
	
 	public void draw(Graphics2D g2d)
	{
 		if(active)
 		{
 		  g2d.drawImage(img, posx, posy, null);
// 		  g2d.draw3DRect(coll.x, coll.y, coll.width, coll.height, true);
 		}
	}
 	
 	public Rectangle coll()
 	{
 		if(active)
 		    return coll;
 		else
 			return new Rectangle();
 	}
 	
 	public void notifyCollision(Boolean res)
 	{
 		if(res)
 			this.active = false;
 	}
}