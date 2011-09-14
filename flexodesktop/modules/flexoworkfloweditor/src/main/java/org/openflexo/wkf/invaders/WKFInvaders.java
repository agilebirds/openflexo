/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.wkf.invaders;

import java.util.logging.Logger;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.wkf.processeditor.ProcessRepresentation;


public class WKFInvaders extends DrawingController<ProcessRepresentation> {

	public WKFInvaders(ProcessRepresentation aDrawing) {
		super(aDrawing);
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(WKFInvaders.class.getPackage()
			.getName());

	/*public static final Icon EXPLOSION_ICON = new ImageIconResource("Resources/WS/WSInterface-BIG.gif");
	public static final Icon PLAYER_ICON = new ImageIconResource("Resources/WS/OperationPort-BIG.gif");
	public static final Icon PLAYER_FIRE_ICON = new ImageIconResource("Resources/WS/ServiceOperationBottom.gif");
	public static final Icon ENEMY_FIRE_ICON = new ImageIconResource("Resources/WS/ServiceOperationTop.gif");
	public static final ImageIcon NO_CURSOR = new ImageIconResource("Resources/WS/OperationPort-SMALL.gif");

	protected Player player;

	protected int playerX;
	protected int playerY;

	private Vector enemyFires;
	private Vector playerFires;*/

	/*
	protected class WKFInvadersProcessRepresentation extends ProcessRepresentation
	{
		
	}
	
	public WKFInvaders(FlexoProcess process, WKFController controller)
	{
		super(process,controller);
		player = new Player();
		playerX = 400;
		playerY = 600;
		player.setLocation(playerX,playerY);
		add(player);
		addMouseListener(this);
		addMouseMotionListener(this);
		enemyFires = new Vector();
		playerFires = new Vector();
		updateGameScreen();
		startGame();
	}

	public void playAgain()
	{
		player = new Player();
		player.setLocation(playerX,playerY);
		add(player);
		youAreDead = false;
	}

	public String getTitle() 
	{
		return "hop";
	}

	private Thread _invadersMovingThread;
	private boolean _stopInvadersMovingThread = false;

	private synchronized void startInvadersMoving()
	{
		_invadersMovingThread = new Thread(new Runnable() {
			public void run()
			{
				Thread myThread = Thread.currentThread();
				while ((_invadersMovingThread == myThread) && (!_stopInvadersMovingThread)) {
					enemyMove();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
				if (logger.isLoggable(Level.INFO))
					logger.info("STOP game");
				return;
			}
		});
		_invadersMovingThread.start();
		_stopInvadersMovingThread = false;
	}

	private Thread _gameThread;
	private boolean _stopGameThread = false;

	private synchronized void startGameThread()
	{
		_gameThread = new Thread(new Runnable() {
			public void run()
			{
				Thread myThread = Thread.currentThread();
				while ((_gameThread == myThread) && (!_stopGameThread)) {
					processGame();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
				if (logger.isLoggable(Level.INFO))
					logger.info("STOP game");
				return;
			}
		});
		_gameThread.start();
		_stopGameThread = false;
	}

	private Random random;

	private static Cursor noCursor = Toolkit.getDefaultToolkit().createCustomCursor(NO_CURSOR.getImage(),new java.awt.Point(8,8),"No cursor");

	protected synchronized void startGame()
	{
		setCursor(noCursor);
		random = new Random();
		startInvadersMoving();
		startGameThread();
	}

	protected synchronized void stopGame()
	{
		_stopInvadersMovingThread = true;
		_stopGameThread = true;
	}

	protected class Player extends JLabel
	{
		protected Player()
		{
			super(PLAYER_ICON);
			setSize(43,26);
		}
	}

	public void mouseDragged(MouseEvent e) { }

	public void mouseMoved(MouseEvent e)
	{
		if ((getParent() != null) && (getParent() instanceof JViewport)) {
			if (playerY != getParent().getHeight()-40) {
				playerY = getParent().getHeight()-40;
				_nodesThatPlay = null;
			}
		}
		playerX = e.getX();
		player.setLocation(playerX,playerY);
		updateGameScreen();
	}

	public void mouseClicked(MouseEvent e) { }

	public void mousePressed(MouseEvent e) 
	{ 
		if (!youAreDead) {
			fire();
		}
	}

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }


	protected void fire()
	{
		PlayerFire newFire = new PlayerFire(playerX,playerY);
		playerFires.add(newFire);
		setLayer(newFire,100);
	}

	protected void enemyFire(AbstractNode enemy)
	{
		Point locInProcess = enemy.getLocationInProcess();
		EnemyFire newFire = new EnemyFire(locInProcess.x,locInProcess.y);
		enemyFires.add(newFire);
		setLayer(newFire,100);
	}

	protected void enemyDestroyed(AbstractNode enemy)
	{
		EnemyExplosion explosion = new EnemyExplosion(enemy);
		setLayer(explosion,110);
		explosion.process();
	}

	private boolean youAreDead = false;

	protected void youAreDead()
	{
		youAreDead = true;
		YouAreDead yourEnd = new YouAreDead();
		setLayer(yourEnd,120);
		yourEnd.process();
	}

	protected class PlayerFire extends JLabel
	{
		private int x;
		private int y;

		protected PlayerFire (int x, int y)
		{
			super(PLAYER_FIRE_ICON);
			setSize(2,10);
			WKFInvaders.this.add(this);
			setLocation(x,y);
			this.x = x;
			this.y = y;
		}

		protected void process()
		{
			y = y - 5;
			setLocation(x,y);
			if (y <= 0) {
				WKFInvaders.this.remove(this);
				playerFires.remove(this);
			}
			for (Enumeration en=getNodesThatPlay().elements(); en.hasMoreElements();) {
				AbstractNodeView next = (AbstractNodeView)en.nextElement();
				if (next.getNode() != null) {
					//Rectangle boundsInThis = SwingUtilities.convertRectangle(next,next.getBounds(),this);
					Rectangle boundsInThis = new Rectangle(next.getNode().getLocationInProcess(),next.getNode().getSize());
					Rectangle myBounds = new Rectangle(getLocation(),getSize());
					if (boundsInThis.intersects(myBounds)) {
						enemyDestroyed(next.getNode());
						WKFInvaders.this.remove(this);
						playerFires.remove(this);
					}
				}
			}
		}
	}

	// private Vector explosions = new Vector();

	protected class EnemyExplosion extends JLabel  
	{
		private Thread _explosionThread;
		private boolean _explosionFinished = false;
		private AbstractNode _enemy;

		protected EnemyExplosion (AbstractNode enemy)
		{
			super(EXPLOSION_ICON);
			setSize(61,51);
			_enemy = enemy;
			WKFInvaders.this.add(this);
			Point loc = enemy.getLocationInProcess();
			loc.x = loc.x+((int)enemy.getWidth()-61)/2;
			loc.y = loc.y+((int)enemy.getHeight()-51)/2;
			setLocation(loc);
		}

		protected void process()
		{
			_explosionThread = new Thread(new Runnable() {
				public void run()
				{
					Thread myThread = Thread.currentThread();
					while ((_explosionThread == myThread) && (!_explosionFinished)) {
						try {
							Thread.sleep(500);
							_enemy.delete();
							Thread.sleep(500);
							_explosionFinished = true;
						} catch (Exception e) {
						}
					}
					WKFInvaders.this.remove(EnemyExplosion.this);
					_nodesThatPlay = null;
					return;
				}
			});
			_explosionThread.start();
		}
	}

	protected class YouAreDead extends JLabel  
	{
		private Thread _explosionThread;
		private boolean _explosionFinished = false;

		protected YouAreDead ()
		{
			super(EXPLOSION_ICON);
			setSize(61,51);
			WKFInvaders.this.add(this);
			Point loc = player.getLocation();
			loc.x = loc.x+(player.getWidth()-61)/2;
			loc.y = loc.y+(player.getHeight()-51)/2;
			setLocation(loc);
		}

		protected void process()
		{
			_explosionThread = new Thread(new Runnable() {
				public void run()
				{
					Thread myThread = Thread.currentThread();
					while ((_explosionThread == myThread) && (!_explosionFinished)) {
						try {
							Thread.sleep(500);
							WKFInvaders.this.remove(player);
							Thread.sleep(500);
							_explosionFinished = true;
						} catch (Exception e) {
						}
					}
					WKFInvaders.this.remove(YouAreDead.this);
					_nodesThatPlay = null;
					updateGameScreen();
					getController().notify("You're dead. Worflow has been saved on disk !");

					playAgain();
					return;
				}
			});
			_explosionThread.start();
		}
	}


	protected class EnemyFire extends JLabel
	{
		private int x;
		private int y;

		protected EnemyFire (int x, int y)
		{
			super(ENEMY_FIRE_ICON);
			setSize(2,10);
			WKFInvaders.this.add(this);
			setLocation(x,y);
			this.x = x;
			this.y = y;
		}


		protected void process()
		{
			y = y + 3;
			setLocation(x,y);
			Rectangle playerBounds = new Rectangle(player.getLocation(),player.getSize());
			Rectangle myBounds = new Rectangle(getLocation(),getSize());
			if ((playerBounds.intersects(myBounds)) && (!youAreDead)) {
				youAreDead();
			}
			if (y >= playerY+10) {
				WKFInvaders.this.remove(this);
				enemyFires.remove(this);
			}

		}
	}

	private synchronized void processGame()
	{
		for (Enumeration en=playerFires.elements(); en.hasMoreElements();) {
			PlayerFire next = (PlayerFire)en.nextElement();
			next.process();
		}
		for (Enumeration en=enemyFires.elements(); en.hasMoreElements();) {
			EnemyFire next = (EnemyFire)en.nextElement();
			next.process();
		}

		updateGameScreen();
	}

	private int enemyMove = 0;
	private boolean goLeft = false;

	private synchronized void enemyMove()
	{
		boolean goDown = false;
		if (goLeft) {
			enemyMove -= 10;
			if (enemyMove <= -60) {
				goLeft = false;
				goDown = true;
			}
		}
		else {
			enemyMove += 10;
			if (enemyMove >= 60) {
				goLeft = true;
				goDown = true;
			}
		}
		for (Enumeration en=getNodesThatPlay().elements(); en.hasMoreElements();) {
			AbstractNodeView next = (AbstractNodeView)en.nextElement();
			if ((next.getNode() != null) && (!next.getNode().isDeleted())) {
				Point oldLocation = next.getNode().getLocation();
				next.getNode().setLocation(new Point(oldLocation.x+(goLeft?-10:10),(goDown?oldLocation.y+10:oldLocation.y)));
				if (random.nextInt(10) == 0) {
					enemyFire(next.getNode());
				}
			}
		}

		updateGameScreen();
	}
	private Vector _nodesThatPlay = null;

	public synchronized Vector getNodesThatPlay()
	{
		if (_nodesThatPlay == null) {
			_nodesThatPlay = new Vector();
			for (Enumeration en=_nodeViews.elements(); en.hasMoreElements();) {
				AbstractNodeView next = (AbstractNodeView)en.nextElement();
				if ((next.getNode() != null) && (!next.getNode().isDeleted())) {
					if (next.getNode().getParentPetriGraph().getIsVisible()) {
						if (next.getNode().getPosY() < playerY-50) {
							_nodesThatPlay.add(next);
						}
					}                     
				}
			}

		}
		return _nodesThatPlay;
	}

	private synchronized void updateGameScreen()
	{
		revalidate();
		repaint();
	}*/
}
