package com.nhl.spindp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;



public class Screen extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5826058727264357858L;
	public JLabel screen = new JLabel();
	public BufferedImage image;
	
	public Screen()
	{
		try
		{
			image = ImageIO.read(getClass().getResource("/images/Konachan.com - 199548 atha braids brown_eyes brown_hair hat long_hair original ponytail.png"));
			screen = new JLabel(new ImageIcon(image.getScaledInstance(-1, 360, 0)));
	    }
		catch (IOException ex)
		{
			Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
	    }

		this.add(screen);
		this.setSize(1280,720);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void SetImage(BufferedImage image)
	{
		screen.setIcon(new ImageIcon(image/*.getScaledInstance(-1, 360, 0)*/));
	}
}
