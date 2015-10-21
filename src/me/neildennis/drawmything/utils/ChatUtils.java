package me.neildennis.drawmything.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

public class ChatUtils {
	
	public static int[] getPixelArray(int width, int height, ArrayList<String> chat){
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		for (int i = 0; i < pixels.length; i++) pixels[i] = 0xff00ff;
		
		/*Graphics gfx = image.getGraphics();
		FontMetrics fm = gfx.getFontMetrics();
		
		int msgnum = 0, fheight = fm.getHeight();
		for (int i = chat.size()-1; i >= 0; i--){
			gfx.drawString(chat.get(i), 4, image.getHeight() - 4 - (msgnum * fheight));
			msgnum++;
		}*/
		
		return pixels;
	}

}
