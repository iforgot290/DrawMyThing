package me.neildennis.drawmything.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import me.neildennis.drawmything.Main;
import me.neildennis.drawmything.utils.ChatUtils;
import me.neildennis.drawmything.utils.DrawUtils;

public class ChatArea extends DrawComponent{

	static final long serialVersionUID = 1L;
	private Main main;

	private int width, height;
	private BufferStrategy bs;
	private Graphics g;

	private ArrayList<String> chat;
	private BufferedImage chatbox;
	private int[] chatpix;

	public ChatArea(int width, int height){
		main = Main.getMain();

		this.width = width;
		this.height = height;
		this.setSize(new Dimension(width, height));

		chat = new ArrayList<String>();
		chat.add("gay");
		chatbox = new BufferedImage(width-20, height-20, BufferedImage.TYPE_INT_RGB);
		chatpix = ((DataBufferInt)chatbox.getRaster().getDataBuffer()).getData();

		//setupChat();
		registerClicks();
	}

	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();

		g.setColor(Color.CYAN);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.WHITE);
		g.fillRect(10, 10, width - 20, height - 20);
		
		g.setColor(Color.BLACK);

		for (int i = 0; i < chatpix.length; i++) chatpix[i] = 0xffffff;
		
		FontMetrics fm = g.getFontMetrics();

		int msgnum = 0, fheight = fm.getHeight();
		for (int i = chat.size()-1; i >= 0; i--){
			g.drawString(chat.get(i), 4, chatbox.getHeight() - 4 - (msgnum * fheight));
			msgnum++;
		}

		//g.drawImage(chatbox, 10, 10, null);

		g.dispose();
		bs.show();
	}

	private void registerClicks(){

		addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e){
				Main.getMain().log(e.getY());
			}

		});

	}

	private void setupChat(){
		int[] newchat = ChatUtils.getPixelArray(chatbox.getWidth(), chatbox.getHeight(), chat);

		for (int i = 0; i < newchat.length; i++) chatpix[i] = newchat[i];
	}
}
