package me.neildennis.drawmything.client.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.thread.DrawThread;
import me.neildennis.drawmything.client.thread.GameThread;
import me.neildennis.drawmything.client.utils.ChatUtils;
import me.neildennis.drawmything.client.utils.FileUtils;

public class DrawArea extends DrawComponent{

	private static final long serialVersionUID = 1L;
	private Main main;
	private GameThread game;
	private DrawThread drawthread;

	private BufferStrategy bs;
	private Graphics2D g;
	private int height;
	private int width;

	private BufferedImage image;
	public volatile int[] pixels;
	private int oldx, oldy;

	private BufferedImage clockimg;
	private Font timerfont;
	private FontMetrics tfmetrics;

	private volatile double counter = 0;
	private volatile double countmax = 0;
	private volatile double last = 60;
	
	private boolean choosing = true;
	private boolean drawing = true;
	
	private Color papercolor = new Color(0xfff297);
	private Font paperfont;
	private FontMetrics pmetrics;

	private volatile int mousex = -1, mousey = -1;

	public DrawArea(int height){
		this(height * 16/9, height);
	}

	public DrawArea(int width, int height){
		main = Main.getMain();
		game = main.getGameThread();
		drawthread = main.getDrawThread();

		this.height = height;
		this.width = width;
		this.setSize(new Dimension(width, height));
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

		clear();
		registerClicks();
	}

	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = (Graphics2D) bs.getDrawGraphics();

		if (tfmetrics == null) tfmetrics = g.getFontMetrics(timerfont);
		if (pmetrics == null) pmetrics = g.getFontMetrics(paperfont);

		g.drawImage(image, 0, 0, width, height, null);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int stroke = game.getStroke();
		g.setColor(game.getColor());
		if (mousex != -1 && mousey != -1)
			g.fillOval(mousex - stroke, mousey - stroke, stroke * 2, stroke * 2);

		g.setColor(Color.WHITE);
		g.fillOval(getWidth() - 130 + 57 - 46, 5 + 72 - 46, 46 * 2, 46 * 2);
		
		g.setColor(Color.RED);
		if (last > 0){
			int angle = (int) (((1-(counter/countmax))*360) * -1);
			g.fillArc(getWidth() - 130 + 57 - 46, 5 + 72 - 46, 46 * 2, 46 * 2, 90, angle);
		}
			
		g.setColor(Color.BLACK);
		g.setFont(timerfont);

		String timer = String.valueOf(Math.round(counter / 60 + 0.5));
		Rectangle bounds = ChatUtils.getStringBounds(g, timer, 100, 100);
		int fheight = (int) bounds.getHeight();
		int fwidth = tfmetrics.stringWidth(String.valueOf(timer));

		int xpos = getWidth() - 130 + 57 - fwidth / 2;
		int ypos = 5 + 72 + fheight / 2;

		g.drawString(timer, xpos, ypos);

		g.drawImage(clockimg, getWidth() - 130, 5, 125, 125, null);
		
		if (drawing){
			g.setColor(papercolor);
			g.rotate(Math.toRadians(-1));
			g.fillRect((getWidth()-50) / 2 - 200, 25, 400, 100);
			
			g.setColor(Color.BLACK);
			g.setFont(paperfont);
			
			String todraw = "Your word is ";
			String word = "Nigga";
			bounds = ChatUtils.getStringBounds(g, todraw, 100, 100);
			fheight = (int) bounds.getHeight();
			fwidth = pmetrics.stringWidth(todraw+word);
			
			xpos = (getWidth() - 50) / 2 - fwidth / 2;
			ypos = (25 + 50 - fheight / 2);
			g.drawString(todraw, xpos, ypos);
			
			g.setColor(Color.RED);
			g.drawString(word, xpos + pmetrics.stringWidth(todraw), ypos);
			
			xpos = (getWidth() - 50) / 2 - fwidth / 2;
			ypos = (25 + 50 + fheight);
		}

		g.dispose();
		bs.show();
	}

	public void tick(){
		if (counter >= 0) counter --;
		else if (last > 0) last--;
	}

	public void init(){
		clockimg = FileUtils.loadImage("/res/clock.png");
		timerfont = new Font("Segoe UI", Font.BOLD, 35);
		paperfont = new Font("Segoe UI", Font.BOLD, 25);
		setTimer(60);
	}

	public void setTimer(int i){
		countmax = i * 60;
		counter = countmax;
		last = 60;
	}

	public void clear(){
		for (int i = 0; i < pixels.length; i++){
			pixels[i] = Color.WHITE.hashCode();
		}
	}
	
	private void draw(double oldx, double oldy, double currentx, double currenty){
		Line2D line = new Line2D.Double(oldx, oldy, currentx, currenty);
		game.queueLine(line);
		drawthread.send(line, game.getColor(), game.getStroke());
	}

	private void registerClicks(){

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();

				if (e.getButton()==1)
					draw(oldx, oldy, oldx, oldy);
			}

			@Override
			public void mouseReleased(MouseEvent e){
				mousex = e.getX();
				mousey = e.getY();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();

				mousex = oldx;
				mousey = oldy;
			}

			@Override
			public void mouseExited(MouseEvent e){
				mousex = -1;
				mousey = -1;
			}

		});

		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int currentx = e.getX();
				int currenty = e.getY();

				if (currentx > getWidth() || currenty > getWidth() || currentx < 0 || currenty < 0) return;
				
				draw(oldx, oldy, currentx, currenty);

				oldx = currentx;
				oldy = currenty;

				mousex = -1;
				mousey = -1;
			}

			@Override
			public void mouseMoved(MouseEvent e){
				mousex = e.getX();
				mousey = e.getY();
			}

		});

	}

}
