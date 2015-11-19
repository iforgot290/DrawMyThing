package me.neildennis.drawmything.client.managers;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import me.neildennis.drawmything.client.screen.ChatArea;
import me.neildennis.drawmything.client.screen.ColorSelector;
import me.neildennis.drawmything.client.screen.DrawArea;
import me.neildennis.drawmything.client.screen.DrawComponent;
import me.neildennis.drawmything.client.screen.PlayerInfo;

public class ScreenManager extends Manager{

	private JFrame frame;
	private ArrayList<DrawComponent> components;
	private SpringLayout layout;

	private DrawArea drawarea;
	private ColorSelector colorsel;
	private ChatArea chatarea;
	private PlayerInfo playerinfo;

	private static int height = 900;
	private static int width = height * 16/9;

	protected ScreenManager(){
		frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("DrawMyThing");
		
		layout = new SpringLayout();

		frame.setSize(new Dimension(width, height));

		frame.setLayout(layout);

		components = new ArrayList<DrawComponent>();

		int drawbounds = (int) (width * 0.75);

		components.add(drawarea = new DrawArea(drawbounds-50, 630));
		components.add(colorsel = new ColorSelector(drawarea.getHeight()));

		for (Component c : components) frame.add(c);

		layout.putConstraint(SpringLayout.WEST, drawarea, 0, SpringLayout.EAST, colorsel);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		components.add(chatarea = new ChatArea(drawbounds, frame.getContentPane().getHeight() - drawarea.getHeight()));
		layout.putConstraint(SpringLayout.NORTH, chatarea, 0, SpringLayout.SOUTH, drawarea);
		frame.add(chatarea);

		components.add(playerinfo = new PlayerInfo(frame.getContentPane().getWidth() - drawbounds, frame.getContentPane().getHeight()));
		layout.putConstraint(SpringLayout.WEST, playerinfo, 0, SpringLayout.EAST, drawarea);
		frame.add(playerinfo);
		
		for (DrawComponent c : components) c.init();
	}
	
	public JFrame getFrame(){
		return frame;
	}

	public ArrayList<DrawComponent> getComponents(){
		return components;
	}

	public DrawArea getDrawArea(){
		return drawarea;
	}

	public ChatArea getChat() {
		return chatarea;
	}

	public PlayerInfo getPlayerInfo(){
		return playerinfo;
	}
	
	@Override
	public void shutdown(){
		
	}

}
