package me.neildennis.drawmything.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.neildennis.drawmything.Main;

public class FileUtils {
	
	public static BufferedImage loadImage(String path){
		try {
			return ImageIO.read(Main.class.getClass().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
