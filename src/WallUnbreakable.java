import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class WallUnbreakable 
{
	//x and y position of the player tank
	private int posx=0;
	private int posy=0;

	//array to load and store images of tank in various directions
	private Image img;

	//to store collision area for vertical and horizontal tanks
	private Rectangle coll;

	//to initialise the contents of the object of the type TankPlayer
	WallUnbreakable(int posx, int posy)
	{
		this.posx = posx;
		this.posy = posy;
		img = Toolkit.getDefaultToolkit().getImage("images/block2.gif");
		this.coll = new Rectangle(0+this.posx, 0+this.posy , 40, 40);
	}

 	public void draw(Graphics2D g2d)
	{
 		  g2d.drawImage(img, posx, posy, null);
// 		  g2d.draw3DRect(coll.x, coll.y, coll.width, coll.height, true);
	}
 	
 	public Rectangle coll()
 	{
 		    return coll;
 	}
}