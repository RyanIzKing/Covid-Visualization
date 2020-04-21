package vis;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class App
{
	public static void main(String args[])
	{
		JFrame frame = new JFrame();
		DataMod dataMod = new DataMod();
		dataMod.generateModifiedData();
		JScrollPane scrollPane = DataVis.getVis();
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		
		frame.add(scrollPane);
		frame.setSize(1000, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
	}
}
