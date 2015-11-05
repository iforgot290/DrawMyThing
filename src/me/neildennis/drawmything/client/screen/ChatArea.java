package me.neildennis.drawmything.client.screen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.server.packets.ChatPacket;

public class ChatArea extends DrawComponent{

	static final long serialVersionUID = 1L;
	private Main main;

	private int width, height;
	private BufferStrategy bs;
	private Graphics2D g;

	private ArrayList<String> chat;
	private StringBuilder sb;
	private BufferedImage chatbox;
	private int[] chatpix;

	private int counter = 0;
	public boolean focused = false;
	private boolean blink = false;

	private Color background = new Color(0xf2f2f2);
	private Color lightgray = new Color(0xa9a9a9);

	public ChatArea(int width, int height){
		main = Main.getMain();

		this.width = width;
		this.height = height;
		this.setSize(new Dimension(width, height));

		chat = new ArrayList<String>();
		sb = new StringBuilder();
		chatbox = new BufferedImage(width-20, height-20, BufferedImage.TYPE_INT_RGB);
		chatpix = ((DataBufferInt)chatbox.getRaster().getDataBuffer()).getData();
		
		registerClicks();
		registerEvents();
	}

	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = (Graphics2D) bs.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(background);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.WHITE);
		g.fillRect(10, 10, width - 10, height - 20);

		g.setColor(lightgray);
		g.setStroke(new BasicStroke(2));
		g.drawRect(20, height - 20 - 30, width - 30, 30);

		g.setColor(Color.BLACK);

		for (int i = 0; i < chatpix.length; i++) chatpix[i] = 0xffffff;

		Font plain = new Font("Segoe UI", Font.PLAIN, 13);
		g.setFont(plain);
		FontMetrics fm = g.getFontMetrics();
		int fheight = fm.getHeight();

		String typed = sb.toString();
		//main.log(typed);

		g.drawString(typed, 25, ((height - 50) + 10) + (fheight / 2));

		if (focused && blink){
			g.setStroke(new BasicStroke(1));
			int top = ((height - 50) + 15) - (fheight / 2);
			int over = 25 + fm.stringWidth(typed);
			g.drawLine(over, top, over, top + fheight - 2);
		}
		
		/*if (!focused && typed.equals("")){
			g.setFont(new Font("Segoe UI", Font.ITALIC, 13));
			g.drawString("chat", 25, ((height - 50) + 10) + (fheight / 2));
			g.setFont(plain);
		}*/

		int msgnum = 0;
		for (int i = chat.size()-1; i >= 0; i--){
			int strpos = chatbox.getHeight() - 40 - (msgnum * fheight);
			if (strpos - fheight < 0) break;
			g.drawString(chat.get(i), 25, chatbox.getHeight() - 40 - (msgnum * fheight));
			msgnum++;
		}

		//g.drawImage(chatbox, 10, 10, null);

		g.dispose();
		bs.show();
	}

	public void tick(){
		counter++;

		if (counter > 40){
			counter = 0;
			blink = !blink;
		}
	}

	private void registerClicks(){

		addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e){
				if (isInChat(e.getX(), e.getY())){
					focused = true;
					blink = true;
					counter = 0;
				}
				else focused = false;
			}

		});

	}
	
	private void registerEvents(){
		
		addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				focused = false;
			}
			
		});
		
		addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				if (Character.isLetterOrDigit(e.getKeyChar())||Character.isWhitespace(e.getKeyChar())|| ispunc(Character.getType(e.getKeyChar()))){
					type(e.getKeyChar());
					return;
				} else if (e.getKeyChar()=='\b'){
					backspace();
				}
			}
			
			private boolean ispunc(int c){
				return c == Character.MATH_SYMBOL || c == Character.CONNECTOR_PUNCTUATION || c == Character.CURRENCY_SYMBOL || c == Character.DASH_PUNCTUATION || c == Character.END_PUNCTUATION
						|| c == Character.ENCLOSING_MARK || c == Character.INITIAL_QUOTE_PUNCTUATION || c == Character.FINAL_QUOTE_PUNCTUATION || c == Character.OTHER_PUNCTUATION
						|| c == Character.OTHER_SYMBOL || c == Character.START_PUNCTUATION || c == Character.MODIFIER_SYMBOL;
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && sb.length() > 0){
					chat(main.getUsername()+": "+sb.toString());
					main.getNetworkThread().send(new ChatPacket(sb.toString(), main.getUsername()));
					sb = new StringBuilder();
				}
			}
			
		});
		
	}

	public boolean isInChat(int x, int y){
		
		if (x>20 && x < width - 40 && y > height - 50 && y < height - 20) return true;
		
		return false;
	}

	public void type(char key) {
		if (focused) sb.append(key);
	}
	
	public void backspace(){
		if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
	}
	
	public void chat(String str){
		chat.add(str);
	}
}
