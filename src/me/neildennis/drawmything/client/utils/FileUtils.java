package me.neildennis.drawmything.client.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.neildennis.drawmything.client.Main;

public class FileUtils {
	
	public static BufferedImage loadImage(String path){
		try {
			return ImageIO.read(Main.class.getClass().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String loadSkypePic(){
		File skypefolder = new File(System.getenv("APPDATA")+"/Skype/");
		
		if (skypefolder.isDirectory()){
			for (File file : skypefolder.listFiles()){
				File picfolder = new File(file + "/Pictures/");
				if (picfolder.exists() && picfolder.isDirectory()){
					int newest = 0;
					File newestpic = null;
					for (File pic : picfolder.listFiles()){
						char firstchar = pic.getName().charAt(pic.getName().length()-5);
						if (Character.isDigit(firstchar)){
							String number = String.valueOf(firstchar);
							char secondchar = pic.getName().charAt(pic.getName().length()-6);
							if (Character.isDigit(secondchar)){
								number = String.valueOf(secondchar) + number;
							}
							
							if (Integer.valueOf(number) > newest) {
								newestpic = pic;
								newest = Integer.valueOf(number);
							}
						}
					}
					if (newest > 0)
						try {
							return newestpic.getAbsolutePath();
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}
		
		return null;
	}

}
