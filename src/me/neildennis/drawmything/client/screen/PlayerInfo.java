package me.neildennis.drawmything.client.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.game.Player;
import me.neildennis.drawmything.client.managers.GameManager;
import me.neildennis.drawmything.client.managers.Manager;
import me.neildennis.drawmything.client.utils.FileUtils;

public class PlayerInfo extends DrawComponent{

	private static final long serialVersionUID = 1L;
	private Main main;
	private GameManager game;
	
	public int width, height;
	
	private BufferStrategy bs;
	private Graphics2D g;
	
	private BufferedImage pic;
	private boolean retry = true;
	
	private Color background = new Color(0xf2f2f2);
	private Color lightgray = new Color(0xa9a9a9);
	
	private Font boldfont = new Font("Segoe UI", Font.BOLD, 22);
	private FontMetrics boldmetrics;
	private Font normalfont = new Font("Segoe UI", Font.PLAIN, 15);
	private FontMetrics normalmetrics;

	public PlayerInfo(int width, int height){
		main = Main.getMain();
		game = Manager.getGameManager();
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));
		
		registerClicks();
	}

	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = (Graphics2D) bs.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (boldmetrics == null) boldmetrics = g.getFontMetrics(boldfont);
		if (normalmetrics == null) normalmetrics = g.getFontMetrics(normalfont);
		
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		
		createUserPanel();
		
		int num = 0;
		Player[] ranks = new Player[game.getPlayers().size()];
		
		for (Player p : game.getPlayers()){
			if (ranks[0]==null) {
				ranks[0] = p;
				continue;
			}
			
			for (int i = 0; i < ranks.length; i++){
				if (ranks[i]!=null){
					if (p.getScore()>=ranks[i].getScore()){
						for (int r = ranks.length - 1; r >= 0; r--){
							if (ranks[r]!=null){
								ranks[r+1] = ranks[r];
							}
						}
						ranks[i] = p;
						break;
					}
				} else {
					ranks[i] = p;
					break;
				}
			}
		}
		
		for (int i = 0; i < ranks.length; i++){
			g.drawImage(ranks[i].renderPanel(i+1), 10, 120 + 85 * num, width - 20, 80, null);
			num++;
		}
		
		g.dispose();
		bs.show();
	}
	
	public void registerClicks(){
		
	}
	
	private void createUserPanel(){
		g.setColor(Color.WHITE);
		g.fillRect(10, 10, width - 20, 100);
		
		g.setColor(lightgray);
		g.fillRect(18, 18, 86, 86);
		if (pic==null&&retry)
			try {
				pic = ImageIO.read(new File(FileUtils.loadSkypePic()));
			} catch (Exception e) {
				e.printStackTrace();
				retry = false;
			}
		g.drawImage(pic, 18, 18, 86, 86, null);
		
		g.setColor(Color.BLACK);
		g.setFont(boldfont);
		g.drawString(main.getUsername(), 18 + 86 + 5, 10+boldmetrics.getHeight());
		
		g.setFont(normalfont);
		g.drawString("Score: 9001", 18 + 86 + 5, 10 + boldmetrics.getHeight() + normalmetrics.getHeight());
	}

}
