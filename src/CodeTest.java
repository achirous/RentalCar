import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * TODO: make one print method for any array
 * @author Achilleas
 *
 */
public class CodeTest {
	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("vehicles.json"));
			JSONObject jsonObj = (JSONObject) obj;
			JSONObject searchObj = (JSONObject) jsonObj.get("Search");

			JSONArray vehicleList = (JSONArray) searchObj.get("VehicleList");
			
			CodeTest codeTest = new CodeTest();
			System.out.println("Print cars in ascending price order?(y/n)");
			Scanner reader = new Scanner(System.in);
			String in = reader.nextLine();
			if(in.equals("y")){
				JSONArray sortedList = codeTest.sortArray(vehicleList);
				printNamePrice(sortedList);
			}
			System.out.println("Print specifications?(y/n)");
			String in1 = reader.nextLine();
			if(in1.equals("y")){
				JSONArray specs = getSpecs(vehicleList);
				printSpecs(specs);
			}
			System.out.println("Print highest rated suppliers per car type?(y/n)");
			String in2 = reader.nextLine();
			if(in2.equals("y")){
				JSONArray bestSuppliers = getBestSupplier(vehicleList);
				codeTest.printBestSuppliers(bestSuppliers);
			}
			System.out.println("Print overall scores?(y/n)");
			String in3 = reader.nextLine();
			if(in3.equals("y")){
				JSONArray overallScores = getOverallScores(vehicleList);
				codeTest.printOverallScores(overallScores);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void printOverallScores(JSONArray list){
		list.sort(new JSONComparator("sumOfScores"));
		for(int i=list.size()-1; i>=0; i--){
			JSONObject obj = (JSONObject) list.get(i);
			String name = (String) obj.get("name");
			int score = (int) obj.get("vehicleScore");
			double rating = doubleValue(obj.get("rating"));
			double sumScore = doubleValue(obj.get("sumOfScores"));
			System.out.println("{"+name+"}-{"+score+"}-{"+rating+"}-{"+sumScore+"}");
		}
	}
	
	public static JSONArray getOverallScores(JSONArray list){
		
		JSONArray newList = setVehicleScore(list);
		for(Object o : newList){
			JSONObject obj = (JSONObject) o;
			int vehicleScore = (int)obj.get("vehicleScore");
			double rating = doubleValue(obj.get("rating"));
			double sum = vehicleScore + rating;
			obj.put("sumOfScores", sum);
		}
		return newList;
		
	}
	
	public static JSONArray setVehicleScore(JSONArray list){
		for(Object obj : list){
			int score = 0;
			JSONObject vehicle =(JSONObject) obj;
			if(vehicle.get("transmission")=="Manual"){
				score = 1;
			}else if(vehicle.get("transmission")=="Automatic"){
				score = 5;
			}
			if(vehicle.get("airCon")=="AC"){
				score += 2;
			}
			vehicle.put("vehicleScore", score);
		}
		return list;
	}

	public static ArrayList<String> getDistinctNames(JSONArray list) {
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			JSONObject obj = (JSONObject) list.get(i);
			String thisName = (String) obj.get("carType");
			if (!names.contains(thisName)) {
				names.add(thisName);
			}
		}
		return names;
	}

	public static double doubleValue(Object value) {
	    return (value instanceof Number ? ((Number)value).doubleValue() : -1.0);
	}
	
	public static JSONArray getBestSupplier(JSONArray list) {
		JSONArray bestSuppliers = new JSONArray();
		ArrayList<String> distinctNames = getDistinctNames(list);
		double maxRating = 0.0;
		System.out.println(distinctNames.size());
		for(int i=0; i<distinctNames.size(); i++){
			for(Object obj : list){
				JSONObject vehicle = (JSONObject) obj;
				if((String)vehicle.get("carType")==distinctNames.get(i)){
					double rating = doubleValue(vehicle.get("rating"));
					if(rating>maxRating){
						maxRating = rating;
					}
				}
			}
			for(Object o : list){
				JSONObject jsonObj = (JSONObject) o;
				double newRating = doubleValue(jsonObj.get("rating"));
				if((String)jsonObj.get("carType")==distinctNames.get(i) && newRating==maxRating){
					bestSuppliers.add(jsonObj);
				}
			}
			maxRating = 0;
		}
		return bestSuppliers;
	}
	
	public void printBestSuppliers(JSONArray list){
		list.sort(new JSONComparator("rating"));
		for(int i=list.size()-1; i>=0; i--){
			JSONObject obj = (JSONObject) list.get(i);
			String name = (String)obj.get("name");
			String carType = (String) obj.get("carType");
			String supplier = (String) obj.get("supplier");
			double rating = doubleValue(obj.get("rating"));
			System.out.println("{"+name+"}-{"+carType+"}-{"+supplier+"}-{"+rating+"}");
		}
		System.out.println(list.size());
	}

	public JSONArray sortArray(JSONArray array) {
		array.sort(new JSONComparator("price"));
		return array;
	}

	public static JSONArray getSpecs(JSONArray array) {
		JSONArray specifications = new JSONArray();
		for (int j = 0; j < array.size(); j++) {
			JSONObject vehicle = (JSONObject) array.get(j);
			String ssip = (String) vehicle.get("sipp");
			char[] letters = ssip.toCharArray();
			JSONObject specObj = vehicle;
			specObj.putAll(checkSIPP(letters));
			specifications.add(specObj);
		}
		return specifications;
	}

	public static JSONObject checkSIPP(char[] letters) {
		String carType = null;
		String doors = null;
		String transmission = null;
		String fuel = null;
		String airCon = null;

		for (int i = 0; i < letters.length; i++) {
			if (i == 0) {
				switch (letters[i]) {
				case 'M':
					carType = "Mini";
					break;
				case 'E':
					carType = "Economy";
					break;
				case 'C':
					carType = "Compact";
					break;
				case 'I':
					carType = "Intermediate";
					break;
				case 'S':
					carType = "Standard";
					break;
				case 'F':
					carType = "Full size";
					break;
				case 'P':
					carType = "Premium";
					break;
				case 'L':
					carType = "Luxury";
					break;
				case 'X':
					carType = "Special";
					break;
				}
			} else if (i == 1) {
				switch (letters[i]) {
				case 'B':
					doors = "2 doors";
					break;
				case 'C':
					doors = "4 doors";
					break;
				case 'D':
					doors = "5 doors";
					break;
				case 'W':
					doors = "Estate";
					break;
				case 'T':
					doors = "Convertible";
					break;
				case 'F':
					doors = "SUV";
					break;
				case 'P':
					doors = "Pick up";
					break;
				case 'V':
					doors = "Passenger van";
					break;
				}
			} else if (i == 2) {
				switch (letters[i]) {
				case 'M':
					transmission = "Manual";
					break;
				case 'A':
					transmission = "Automatic";
					break;
				}
			} else if (i == letters.length - 1) {
				switch (letters[i]) {
				case 'N':
					fuel = "Petrol";
					airCon = "no AC";
					break;
				case 'R':
					fuel = "Petrol";
					airCon = "AC";
					break;
				}
			}
		}
		JSONObject entries = new JSONObject();
		entries.put("carType", carType);
		entries.put("doors", doors);
		entries.put("transmission", transmission);
		entries.put("fuel", fuel);
		entries.put("airCon", airCon);

		return entries;
	}

	public class JSONComparator implements Comparator<JSONObject> {
		String element;

		public JSONComparator(String key) {
			element = key;
		}

		@Override
		public int compare(JSONObject o1, JSONObject o2) {
			double price1 = CodeTest.doubleValue(o1.get(element));
			double price2 = CodeTest.doubleValue(o2.get(element));
			return price1 > price2 ? 1 : (price1 < price2 ? -1 : 0);

		}
	}

	public static void printNamePrice(JSONArray list) {
		for (Object vehicleObj : list) {
			JSONObject vehicle = (JSONObject) vehicleObj;
			double price = (double) vehicle.get("price");
			String name = (String) vehicle.get("name");
			System.out.println("{" + name + "} - {" + price + "}");
		}
	}

	public static void printSpecs(JSONArray list) {
		for (Object o : list) {
			JSONObject obj = (JSONObject) o;
			String name = (String) obj.get("name");
			String sipp = (String) obj.get("sipp");
			String type = (String) obj.get("carType");
			String doors = (String) obj.get("doors");
			String transmission = (String) obj.get("transmission");
			String fuel = (String) obj.get("fuel");
			String airCon = (String) obj.get("airCon");

			System.out.println("{" + name + "}-{" + sipp + "}-{" + type + "}-{" + doors + "}-{" + transmission + "}-{"
					+ fuel + "}-{" + airCon + "}");
		}
	}

}
