import java.awt.Rectangle;
import java.util.Random;

public abstract class ArtificialIntelligence
{	
	//direction blocked of the enemy tank
	protected int direction;

	//direction of motion of the enemy tank
	protected int blocked = 4;

	protected Boolean shoot = false;

	protected int choice;

	protected int moveX = 0, moveY = 0;

	protected Random ran = new Random();

	public Rectangle playerColl = new Rectangle(0,0,0,0), coll = new Rectangle(0,0,0,0);

	protected void updateColl()
	{
		moveX = coll.x - playerColl.x;
		moveY = coll.y - playerColl.y ;
		System.out.println("moving to: "+ moveX+", "+moveY);
		testShoot();
		testMove();
 	}

	public void testMove()
	{
	  if(!shoot)
	  {
		if(moveX!=0 & choice == 0)
		{
		if(moveX > 0)
			{
				if(blocked==2)
					choice = 1;
				moveLeft();
			}
		else if(moveX < 0)
			{
				if(blocked==3)
					choice = 1;
				moveRight();
			}
		}
		if(moveY!=0 & choice == 1)
		{
			if(moveY < 0)
		 		{
					if(blocked==1)
						choice = 0;
					moveDown();
				}
			else if(moveY > 0)
			{
				if(blocked==0)
					choice = 0;
				moveUp();
			}
		}
	  }
	}//end of testMove()

	public void testShoot()
	{
		if(moveX == 0 && moveY > 0)
		{
			System.out.println("direction 0...........");
			direction = 0;
			shoot = true;
			moveX = 0;
		}
	else if(moveX == 0 && moveY < 0)
		{
			System.out.println("direction 1...........");
			direction = 1;
			shoot = true;
			moveX = 0;
		}
	else if(moveY == 0 && moveX < 0)
		{
			System.out.println("direction 3...........");
			direction = 3;
			shoot = true;
			moveY = 0;
		}
	else if(moveY == 0 && moveX > 0)
		{
			System.out.println("direction 2...........");
			direction = 2;
			shoot = true;
			moveY = 0;
		}
	else if(moveX != 0 & moveY != 0 & moveY != 5 & moveX != 5 & shoot)
		{
			System.out.println("unshoot.........");
			shoot = false; genRandom();
		}
	}//end of testShoot()

	protected void genRandom()
	{
		this.choice = ran.nextInt(2);
	}

	protected abstract void moveRight();

	protected abstract void moveLeft();
	
	protected abstract void moveUp();

	protected abstract void moveDown();
}