import java.awt.Rectangle;

public class Collision
{
	public Boolean testCollision(Rectangle coll1, Rectangle coll2)
	{
		if(coll1.intersects(coll2))
			return true;
		return false;
    }
}