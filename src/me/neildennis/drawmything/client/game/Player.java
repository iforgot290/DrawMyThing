package me.neildennis.drawmything.client.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.screen.ScreenManager;
import me.neildennis.drawmything.client.thread.GameThread;
import me.neildennis.drawmything.client.utils.ChatUtils;

public class Player {

	private Main main;
	@SuppressWarnings("unused")
	private GameThread game;

	private String username;
	private BufferedImage pic;

	private BufferedImage panel;

	private Color lightgray = new Color(0x999999);
	private Color background = new Color(0xf2f2f2);
	
	private Font boldfont = new Font("Segoe UI", Font.BOLD, 22);
	private FontMetrics boldmetrics;
	private Font normalfont = new Font("Segoe UI", Font.PLAIN, 15);
	private FontMetrics normalmetrics;

	private int score;

	public Player(String username){
		main = Main.getMain();
		game = main.getGameThread();

		this.username = username;

		Random random = new Random();
		score = random.nextInt(10);

		panel = new BufferedImage(ScreenManager.getManager().getPlayerInfo().width - 20, 80, BufferedImage.TYPE_INT_RGB);
	}

	public String getUsername(){
		return username;
	}

	public BufferedImage getPic(){
		return pic;
	}

	public BufferedImage renderPanel(int place){
		Graphics2D g = panel.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		boldmetrics = g.getFontMetrics(boldfont);
		normalmetrics = g.getFontMetrics(normalfont);

		int offset = 0;

		g.setColor(background);
		g.fillRect(0, 0, panel.getWidth(), panel.getHeight());

		Font font = new Font("Segoe UI", Font.BOLD, 55);
		g.setFont(font);

		String score = String.valueOf(place);
		Rectangle bounds = ChatUtils.getStringBounds(g, score, 100, 75);

		int bheight = (int) bounds.getHeight();

		g.setColor(lightgray);
		g.drawString(score, offset, panel.getHeight() / 2 + bheight / 2);

		offset += 13 + 29;

		g.setColor(Color.WHITE);
		g.fillRect(offset, 0, panel.getWidth()-offset, panel.getHeight());

		g.setColor(lightgray);
		g.fillRect(offset, 0, 80, 80);

		offset += 85;
		
		g.setColor(Color.BLACK);
		g.setFont(boldfont);
		g.drawString(username, offset, boldmetrics.getHeight() - 5);
		
		g.setFont(normalfont);
		g.drawString("Score: "+this.score, offset, boldmetrics.getHeight() + normalmetrics.getHeight() - 5);

		return panel;
	}

	public int getScore(){
		return score;
	}

}
