package vis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DataMod
{
	// array list of the abbreviated state names
	public static ArrayList<String> stateKeys = new ArrayList<>();
	
	// this is used to help simplify code
	private static HashMap<String, Integer> stateCounters = new HashMap<>();
	
	// hash map that uses the abbreviated state name as a key
	// this key maps to an array list of integers, the array list
	// of integers contains two values for each state
	// total cases are located at index 0, total deaths are located at index 1
	public static HashMap<String, ArrayList<Integer>> totals = new HashMap<>();
	
	// hash map that uses an integer key, the integer key represents
	// an individual 5 day interval, 1 represents the first five day interval
	// 2 represents the second five day interval etc..., this key maps to
	// an array list of integers that contains two values
	// average cases for the particular 5 day interval are located at index 0
	// average deaths for the particular 5 day interval are located at index 1
	public static HashMap<Integer, ArrayList<Integer>> averages = new HashMap<>();
	
	// hash map that uses the abbreviated state name as a key
	// this keys maps to a two-dimensional ArrayList of integers
	// each individual array list represents a particular day of data linked to a state
	// each array list contains two values
	// number of cases for a particular day are stored at index 0
	// number of deaths for a particular day are stored at index 1
	public static HashMap<String, ArrayList<ArrayList<Integer>>> dailyStateData = new HashMap<>();
	
	// this is used to compute the values stored in dailyStateData
	private static HashMap<String, ArrayList<ArrayList<Integer>>> dailyIncreasingStateData = new HashMap<>();
	
	public void generateModifiedData()
	{
		try
		{
			// these files are generated to help make sure
			// that all of the data that we want is being processed
			PrintWriter online = new PrintWriter("online.txt");
			PrintWriter modified = new PrintWriter("modified.txt");
			URL data =
				new URL("https://raw.githubusercontent.com/ablag/msc/master/us_states_covid19_daily.csv");
			BufferedReader in = new BufferedReader(new InputStreamReader(data.openStream()));
			
			int lineNumber = 0;
			int numOfCharacters = 0;
			
			online.println(in.readLine()); // discard the first line of input
			
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				lineNumber++;
				numOfCharacters += inputLine.length();
				
				// reset the buffered reader every 30 lines to
				// ensure that all of the data gets read in
				if ((lineNumber % 30) == 0)
				{
					in.mark(numOfCharacters);
					numOfCharacters = 0;
					in.reset();
				}
				online.println(inputLine);
				modifyData(inputLine, modified);
			}
			computeDailyStateData();
			computeFiveDayAverages();
			modified.close();
			online.close();
			in.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
	
	private void modifyData(String line, PrintWriter modified)
	{
		String[] tokens = line.split(",");
		
		// the file contains extra data that are associated 
		// with territories that are not needed
		if (tokens[1].equals("AS") || tokens[1].equals("DC") || tokens[1].equals("GU")
		 || tokens[1].equals("MP") || tokens[1].equals("PR") || tokens[1].equals("VI"))
			return;
		
		modified.printf("%s,%s,%s\n", tokens[1], tokens[2], tokens[14]);
		
		// some of the data is missing, resulting in a null string,
		// so to ensure that no exceptions are thrown, the null string
		// is replaced with a 0
		if (tokens[2].equals(""))
			tokens[2] = "0";
		
		if (tokens[14].equals(""))
			tokens[14] = "0";
		
		String currentKey = tokens[1];
		if (totals.containsKey(currentKey))
		{	
			stateCounters.replace(currentKey, stateCounters.get(currentKey) + 1);
			
			dailyIncreasingStateData.get(currentKey).add(new ArrayList<>());
			dailyIncreasingStateData.get(currentKey).get(stateCounters.get(currentKey)).add(Integer.valueOf(tokens[2]).intValue());
			dailyIncreasingStateData.get(currentKey).get(stateCounters.get(currentKey)).add(Integer.valueOf(tokens[14]).intValue());
		}
		else
		{
			stateCounters.put(currentKey, 0);
			stateKeys.add(currentKey); // populate array list with a single state key
			
			totals.put(currentKey, new ArrayList<>());
			totals.get(currentKey).add(Integer.valueOf(tokens[2]).intValue());
			totals.get(currentKey).add(Integer.valueOf(tokens[14]).intValue());
			
			dailyIncreasingStateData.put(currentKey, new ArrayList<ArrayList<Integer>>());
			dailyIncreasingStateData.get(currentKey).add(new ArrayList<>());
			
			dailyIncreasingStateData.get(currentKey).get(stateCounters.get(currentKey)).add(Integer.valueOf(tokens[2]).intValue());
			dailyIncreasingStateData.get(currentKey).get(stateCounters.get(currentKey)).add(Integer.valueOf(tokens[14]).intValue());
		}
	}
	
	private void computeDailyStateData()
	{
		for (int i = 0; i < stateKeys.size(); ++i)
		{
			String currentKey = stateKeys.get(i);
			dailyStateData.put(currentKey, new ArrayList<ArrayList<Integer>>());
			for (int j = 0; j < dailyIncreasingStateData.get(currentKey).size() - 1 ; ++j)
			{
				dailyStateData.get(currentKey).add(new ArrayList<>());
				for (int k = 0; k < dailyIncreasingStateData.get(currentKey).get(j).size(); ++k)
					dailyStateData.get(currentKey).get(j).add(dailyIncreasingStateData.get(currentKey).get(j).get(k) 
										- dailyIncreasingStateData.get(currentKey).get(j + 1).get(k));
			}
		}
	}
	
	private void computeFiveDayAverages()
	{
		int fiveCounter = 0;
		int caseSum = 0;
		int deathSum = 0;
		
		for (int i = 0; i < stateKeys.size() / 2; ++i)
		{
			if ((i + 1) % 5 == 0)
			{
				averages.put(++fiveCounter, new ArrayList<>());
				int fiveDayCaseAverage = caseSum / stateKeys.size();
				int fiveDayDeathAverage = deathSum / stateKeys.size();
				averages.get(fiveCounter).add(fiveDayCaseAverage);
				averages.get(fiveCounter).add(fiveDayDeathAverage);
				caseSum = 0;
				deathSum = 0;
			}
			else
			{
				for (int j = 0; j < stateKeys.size(); ++j)
				{
					caseSum += dailyStateData.get(stateKeys.get(j)).get(i).get(0);
					deathSum += dailyStateData.get(stateKeys.get(j)).get(i).get(1);
				}
			}
		}
	}
}
