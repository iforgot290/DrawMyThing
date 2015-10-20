package me.neildennis.drawmything.screen;

import java.util.ArrayList;

public class ScreenManager {
	
	private static ArrayList<DrawComponent> components;
	private static boolean enabled = false;
	
	private static DrawArea drawarea;
	
	private ScreenManager(){}
	
	public static void init(){
		components = new ArrayList<DrawComponent>();
		
		components.add(drawarea = new DrawArea());
		
		enabled = true;
	}
	
	public static ArrayList<DrawComponent> getComponents(){
		return components;
	}
	
	public static DrawArea getDrawArea(){
		return drawarea;
	}

	public static boolean isEnabled() {
		return enabled;
	}

}
