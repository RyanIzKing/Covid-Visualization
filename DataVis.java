package vis;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class DataVis extends JPanel
{
	private static ArrayList<String> stateKeys = DataMod.stateKeys;
	private static HashMap<String, ArrayList<Integer>> totals = DataMod.totals;
	private static HashMap<String, ArrayList<ArrayList<Integer>>> dailyStateData = DataMod.dailyStateData;
	
	private String[] averageCases = { "500", "1K", "1.5K", "2K","2.5K" };
	private String[] averageDeaths = { "0", "20", "50", "100", "150" };
	
	private static Color[] caseColorPalette = { new Color(0xD0D1E6), new Color(0xA6BDDB), 
						    new Color(0x74A9CF), new Color(0x2B8CBE), 
						    new Color(0x045A8D) };
	private static Color[] deathColorPalette = { new Color(0xCCECE6), new Color(0x99D8C9), 
						     new Color(0x66C2A4), new Color(0x2CA25F),
						     new Color(0x006D2C) };
	
	public static JScrollPane getVis()
	{
		DataVis dataVis = new DataVis();
		return new JScrollPane(dataVis);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(2150, 2150);
	}
	
	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D graphics2D = (Graphics2D) graphics;
		
		// draws new cases and deaths strings
		graphics2D.setFont(new Font("Helvitica", Font.BOLD, 15));
		graphics2D.drawString("New cases", 380, 30);
		graphics2D.drawString("New deaths", 722, 30);
		
		// draws 5-day average strings
		graphics2D.setFont(new Font("Helvitica", Font.PLAIN, 12));
		graphics2D.drawString("(5-day average)", 465, 30);
		graphics2D.drawString("(5-day average)", 816, 30);
		
		// horizontal colored rectangles section
		// includes the rectangles for new cases
		// and new deaths
		int caseDx = 390;
		int deathDx = 737;
		Rectangle horizontalRect;
		for (int i = 0; i < caseColorPalette.length; ++i)
		{
			horizontalRect = new Rectangle(caseDx, 40, 30, 10);
			
			// draws the horizontal rectangles with a specified color
			graphics2D.setColor(caseColorPalette[i]);
			graphics2D.fill(horizontalRect);
			
			horizontalRect = new Rectangle(deathDx, 40, 30, 10);
			
			graphics2D.setColor(deathColorPalette[i]);
			graphics2D.fill(horizontalRect);
			
			// draws the individual integer values
			// below the horizontal rectangles
			graphics2D.setColor(new Color(0x000000));
			graphics2D.drawString(averageCases[i], caseDx, 60);
			graphics2D.drawString(averageDeaths[i], deathDx, 60);
			
			// updating the x positions of the rectangles
			caseDx += 30;
			deathDx += 30;
		}
		
		// draws total cases and deaths strings
		graphics2D.setFont(new Font("Helvitica", Font.BOLD, 15));
		graphics2D.drawString("Total cases", 220, 60);
		graphics2D.drawString("Total deaths", 580, 60);
		
		// draws strings indicating the length of time
		// that the data spans, from March 18th to April 11th
		graphics2D.setFont(new Font("Helvitica", Font.PLAIN, 12));
		graphics2D.drawString("Mar. 18", 400, 100);
		graphics2D.drawString("Apr. 11", 512, 100);
		graphics2D.drawString("Mar. 18", 747, 100);
		graphics2D.drawString("Apr. 11", 859, 100);
		
		graphics2D.setFont(new Font("Helvetica", Font.PLAIN, 18));
		
		Rectangle verticalRect;
		
		// starting x and y positions
		int caseVerticalDx = 400;
		int caseVerticalDy = 105;
		int totalCasesDy = 130;
		
		int deathVerticalDx = 747;
		int deathVerticalDy = 105;
		int totalDeathsDy = 130;
		
		Color currentCaseColor = null;
		Color currentDeathColor = null;
		
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		for (int k = 0; k < stateKeys.size(); ++k)
		{
			// drawing the total amount of cases and deaths for a particular state
			graphics2D.setColor(new Color(0x000000));
			graphics2D.drawString(numberFormat.format(totals.get(stateKeys.get(k)).get(0)), 243, totalCasesDy);
			graphics2D.drawString(numberFormat.format(totals.get(stateKeys.get(k)).get(1)), 616, totalDeathsDy);
			
			// have to access the data in reverse because
			// the first day accessed is April 11th and the last
			// accessed is March 18th
			for (int i = stateKeys.size() / 2 - 1; i >= 0; --i)
			{
				// storing the daily cases and deaths for a particular state
				int dailyCases = dailyStateData.get(stateKeys.get(k)).get(i).get(0);
				int dailyDeath = dailyStateData.get(stateKeys.get(k)).get(i).get(1);
				
				// sets the correct color for an individual case rectangle
				if (dailyCases >= 0 && dailyCases < 1000)
					currentCaseColor = caseColorPalette[0];
				else if (dailyCases >= 1000 && dailyCases < 1500)
					currentCaseColor = caseColorPalette[1];
				else if (dailyCases >= 1500 && dailyCases < 2000)
					currentCaseColor = caseColorPalette[2];
				else if (dailyCases >= 2000 && dailyCases < 2500)
					currentCaseColor = caseColorPalette[3];
				else if (dailyCases >= 2500)
					currentCaseColor = caseColorPalette[4];
				
				// sets the correct color for an individual death rectangle
				if (dailyDeath >= 0 && dailyDeath < 20)
					currentDeathColor = deathColorPalette[0];
				else if (dailyDeath >= 20 && dailyDeath < 50)
					currentDeathColor = deathColorPalette[1];
				else if (dailyDeath >= 50 && dailyDeath < 100)
					currentDeathColor = deathColorPalette[2];
				else if (dailyDeath >= 100 && dailyDeath < 150)
					currentDeathColor = deathColorPalette[3];
				else if (dailyDeath >= 150)
					currentDeathColor = deathColorPalette[4];
				
				// draws the vertical rectangles with a specified color
				verticalRect = new Rectangle(caseVerticalDx, caseVerticalDy, 5, 30);
				graphics2D.setColor(currentCaseColor);
				graphics2D.fill(verticalRect);
				
				verticalRect = new Rectangle(deathVerticalDx, deathVerticalDy, 5, 30);
				graphics2D.setColor(currentDeathColor);
				graphics2D.fill(verticalRect);
				
				// updating the x positions of the rectangles
				caseVerticalDx += 6;
				deathVerticalDx += 6;
			}
			
			// updating y positions of the rectangles and totals 
			caseVerticalDx = 400;
			caseVerticalDy += 40;
			totalCasesDy += 40;
			
			deathVerticalDx = 747;
			deathVerticalDy += 40;
			totalDeathsDy += 40;
		}
		
		graphics2D.setColor(new Color(0x000000));
		graphics2D.setFont(new Font("Helvitica", Font.BOLD, 18));
		
		Scanner in = null;
		try
		{
			// reading in the state names from the file: stateNames.txt
			in = new Scanner(new File("stateNames.txt"));
			String line;
			
			int dy = 130;
			while (in.hasNextLine())
			{
				line = in.nextLine();
				graphics2D.drawString(line, 55, dy);
				dy += 40;
			}
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
		finally
		{
			in.close();
		}
		
		int dy = 100;
		for (int i = 0; i < stateKeys.size(); ++i)
		{
			dy += 40;
			// draws a horizontal line
			graphics2D.drawLine(52, dy, 898, dy);
		}
		
		// draws a vertical line
		graphics2D.drawLine(560, 10, 560, 2100);
	}
}
