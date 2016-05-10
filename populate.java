package com.dbconn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import oracle.jdbc.proxy.annotation.GetCreator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class populate
{
    public static void main(String[] args)
    {        
    	populate pop = new populate();
    	deletePreviousData();		// deletes previous data
        pop.readJsonFiles(args);		// reads all the json files and inserts the data into specified tables
    }
    
    public static void deletePreviousData()
    {
    	try{
    	    DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
    		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
    		Statement st = con.createStatement();
    		st.executeUpdate("DELETE FROM CHECKIN_TABLE");
    		st.executeUpdate("DELETE FROM REVIEWS_TABLE");
    		st.executeUpdate("DELETE FROM BUSINESS_ATTRIBUTES");
    		st.executeUpdate("DELETE FROM BUSINESS_CATEGORY_TABLE");
    		st.executeUpdate("DELETE FROM NEIGHBORHOOD");
    		st.executeUpdate("DELETE FROM BUSINESS_HOURS");
    		st.executeUpdate("DELETE FROM BUSINESS");
    		st.executeUpdate("DELETE FROM ELITE_TABLE");
    		st.executeUpdate("DELETE FROM COMPLIMENTS_TABLE");
    		st.executeUpdate("DELETE FROM FRIENDS_TABLE");
    		st.executeUpdate("DELETE FROM YELP_USER");
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		System.out.println("Problem while deleting previous data");
    	}
    }

    public void readJsonFiles(String[] args) {
        BufferedReader br = null;
        JSONParser parser = new JSONParser();
        try {
            String sCurrentLine;
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
			Statement st = con.createStatement();
			
			// Parse the YelpUser.json file
			br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(args[3])));
			
			System.out.println("Parsing YelpUser.json...");

            while ((sCurrentLine = br.readLine()) != null) {
            	String since = null, name = null,user_id = null, type = null, complimentType = null;
            	long review_count = 0, fans = 0,  votesFunny = 0, votesUseful = 0, votesCool = 0;
            	double avg_stars = 0;
                Object obj, votesObj, complimentsObj;
                
                Set<String> ComplimentTypeKeys = new HashSet<String>();
                Collection ComplimentTypeCounts = new HashSet<Integer>();
                
                try {
                    obj = parser.parse(sCurrentLine);
                    JSONObject jsonObject = (JSONObject) obj;
                    since = (String) jsonObject.get("yelping_since");
                    votesObj = jsonObject.get("votes");
                    JSONObject votesJsonObject = (JSONObject) votesObj;
                    votesFunny = (long) votesJsonObject.get("funny");
                    votesUseful = (long) votesJsonObject.get("useful");
                    votesCool = (long) votesJsonObject.get("cool");
                    review_count = (long) jsonObject.get("review_count");        			
                    name = ((String) jsonObject.get("name")).replaceAll("'", "''");
                    user_id = (String) jsonObject.get("user_id");
                    fans = (long) jsonObject.get("fans");
                    avg_stars = (double) jsonObject.get("average_stars");        			
        			type = (String) jsonObject.get("type");
        			
        			//Insert data into the YELP_USER table
        			String q = "Insert into YELP_USER Values(TO_DATE('" + since + "', 'YYYY-MM'), "  + votesFunny + ", " + votesUseful + ", " + votesCool + ", " + review_count + ", '" + name + "', '" + user_id + "', " + fans + ", " + avg_stars + ", '" + type + "')";
        			st.executeUpdate(q);
                    
        			//Insert data into the FRIENDS_TABLE table
                    JSONArray friendsList = (JSONArray) jsonObject.get("friends");
                    for(int i = 0; i<friendsList.size(); ++i)
                    {
                    	st.executeUpdate("Insert into FRIENDS_TABLE Values('" + user_id + "', '" + friendsList.get(i) + "')");
                    }
                    
                    //Insert data into the ELITE_TABLE table
                    JSONArray eliteList = (JSONArray) jsonObject.get("elite");
                    for(int i = 0; i<eliteList.size(); ++i)
                    {
                    	st.executeUpdate("Insert into ELITE_TABLE Values('" + user_id + "', '" + eliteList.get(i) + "')");
                    }
                    
                    //Insert data into the COMPLIMENTS_TABLE table
                    complimentsObj = jsonObject.get("compliments");
                    JSONObject complimentsJsonObject = (JSONObject) complimentsObj;
                    ComplimentTypeKeys = complimentsJsonObject.keySet();
                    ComplimentTypeCounts = complimentsJsonObject.values();
                    for(String s: ComplimentTypeKeys)
                    {
                    	st.executeUpdate("Insert into COMPLIMENTS_TABLE VALUES('" + user_id + "', '"+ s + "', " + complimentsJsonObject.get(s) + ")");
                    }
                } 
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }	// end of YelpUser.json parsing
            System.out.println("YelpUser.json Parsed");
            br.close();
            con.close();
           
            
			
            // Parse YelpBusiness.json file
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(args[0])));
			System.out.println("Parsing YelpBusiness.json...");

            while ((sCurrentLine = br.readLine()) != null) {
            	String business_id = null, full_address = null, open = null, city = null, state = null, business_name = null, type = null, newFull_address = null, newBusiness_name = null;
            	long review_count = 0;
            	double latitude = 0, longitude = 0, avg_stars = 0;
                Object obj, hours_obj, open_obj, close_obj, attr_obj, inner_attr_obj;
                int i = 0;
                String main_cat = null, sub_cat = null;
                ArrayList<String> business_days = new ArrayList<String>();
                ArrayList<String> main_categories = new ArrayList<String>();
                main_categories.addAll(Arrays.asList("Active Life", "Arts & Entertainment", "Automotive", "Car Rental","Cafes","Beauty & Spas","Convenience Stores","Dentists","Doctors","Drugstores","Department Stores","Education","Event Planning & Services","Flowers & Gifts","Food","Health & Medical","Home Services","Home & Garden","Hospitals","Hotels & Travel","Hardware Stores","Grocery","Medical Centers","Nurseries & Gardening","Nightlife","Restaurants","Shopping","Transportation"));
                
                try {
                    obj = parser.parse(sCurrentLine);
                    JSONObject jsonObject = (JSONObject) obj;
                    
                    business_id = (String) jsonObject.get("business_id");
                    full_address = (String) jsonObject.get("full_address");
                    newFull_address = full_address.replaceAll("\n", ", ");
                    open = (String) jsonObject.get("open").toString();
                    city = (String) jsonObject.get("city");
                    state = (String) jsonObject.get("state");
                    latitude = (double) jsonObject.get("latitude");
                    longitude = (double) jsonObject.get("longitude");
                    review_count = (long) jsonObject.get("review_count");
                    business_name = (String) jsonObject.get("name");
                    newBusiness_name = business_name.replaceAll("'", "''");
                    avg_stars = (double) jsonObject.get("stars");
                    type = (String) jsonObject.get("type");
        			
        			//Insert data into the BUSINESS table
        			String q = "Insert into BUSINESS Values('" + business_id + "', '" + newFull_address + "', '" + open + "', '" + city + "', '" + state + "', " + latitude + ", " + longitude + ", " + review_count + ", '" + newBusiness_name + "', " + avg_stars + ", '"+ type + "')";
        			st.executeUpdate(q);
                    
        			//Insert data into the BUSINESS_HOURS table
                    hours_obj = jsonObject.get("hours");
                    JSONObject hours_jsonObject = (JSONObject) hours_obj;
                    JSONObject open_jsonObject, close_jsonObject;
                    for(i = 0; i < hours_jsonObject.size(); ++i)
                    {
                    	business_days.clear();
                    	business_days.addAll(hours_jsonObject.keySet());
                    	open_obj = hours_jsonObject.get(business_days.get(i));
                    	open_jsonObject = (JSONObject) open_obj;
                    	float open_time = Float.parseFloat(open_jsonObject.get("open").toString().substring(0,2) + "." + open_jsonObject.get("open").toString().substring(3));
                    	close_obj = hours_jsonObject.get(business_days.get(i));
                    	close_jsonObject = (JSONObject) close_obj;
                    	float close_time = Float.parseFloat(close_jsonObject.get("close").toString().substring(0,2) + "." + close_jsonObject.get("close").toString().substring(3));
//                    	System.out.println("Insert into BUSINESS_HOURS Values('" + business_id + "', '" + business_days.get(i) + "', '" + open_jsonObject.get("open") + "', '" + close_jsonObject.get("close") + "')");
                    	st.executeQuery("Insert into BUSINESS_HOURS Values('" + business_id + "', '" + business_days.get(i) + "', " + open_time + ", " + close_time + ")");
                    }
                    
                    //Insert data into the NEIGHBORHOOD table
                    JSONArray neighborsList = (JSONArray) jsonObject.get("neighborhoods");
                    for(int j = 0; j<neighborsList.size(); ++j)
                    {
//                    	System.out.println("Insert into NEIGHBORHOOD Values('" + business_id + "', '" + neighborsList.get(j) + "')");
                    	st.executeUpdate("Insert into NEIGHBORHOOD Values('" + business_id + "', '" + neighborsList.get(j) + "')");
                    }

                    //Insert data into BUSINESS_CATEGORY_TABLE table
                    JSONArray categoriesList = (JSONArray) jsonObject.get("categories");
                    ArrayList<String> main_categories_list = new ArrayList<String>();
                    ArrayList<String> sub_categories_list = new ArrayList<String>();
                    for(int j = 0; j<categoriesList.size(); ++j)
                    {
                    	if(main_categories.contains(categoriesList.get(j)))
                    		main_categories_list.add(categoriesList.get(j).toString());
                    	else
                    		sub_categories_list.add(categoriesList.get(j).toString());
                    }
                    
                    if(sub_categories_list.size() != 0)
                    {
                    	for(int m = 0; m < main_categories_list.size(); m++)
                    		for(int s = 0; s < sub_categories_list.size(); s++)
//                    			System.out.println("Insert into BUSINESS_CATEGORY_TABLE Values('" + business_id + "', '" + main_categories_list.get(m) + "', '" + sub_categories_list.get(s) + "')");
                    			st.executeUpdate("Insert into BUSINESS_CATEGORY_TABLE Values('" + business_id + "', '" + main_categories_list.get(m).replaceAll("'", "''") + "', '" + sub_categories_list.get(s).replaceAll("'", "''") + "')");
                    }                    
                    else
                    {
                    	for(int m = 0; m < main_categories_list.size(); m++)
//                    		System.out.println("Insert into BUSINESS_CATEGORY_TABLE(BUSINESS_ID, MAIN_CATEGORY) Values('" + business_id + "', '" + main_categories_list.get(m) + "')");
                    		st.executeUpdate("Insert into BUSINESS_CATEGORY_TABLE(BUSINESS_ID, MAIN_CATEGORY) Values('" + business_id + "', '" + main_categories_list.get(m) + "')");
                    }
                    main_categories_list.clear();
                    sub_categories_list.clear();
                    
                    //Insert data into BUSINESS_ATTRIBUTES table
                    Set<String> attr_names = new HashSet<String>();
                    Set<String> inner_attr_names = new HashSet<String>();
                    Iterator iter_names, iter_values, inner_iter_names, inner_iter_values;
                    Collection attr_values = new HashSet<String>();
                    Collection inner_attr_values = new HashSet<String>();
                    String attribute_name = null, attribute_value = null, inner_attribute_name = null, inner_attribute_value = null;
                    
                    attr_obj = jsonObject.get("attributes");
                    JSONObject attr_jsonObject = (JSONObject) attr_obj;
                    
                    attr_names = attr_jsonObject.keySet();
                    attr_values = attr_jsonObject.values();
                    iter_names = attr_names.iterator();
                    iter_values = attr_values.iterator();
                    while(iter_names.hasNext())
                    {
                    	attribute_name = (String) iter_names.next();
                    	attribute_value = (String) iter_values.next().toString();
                    	if(attribute_value.startsWith("{"))		// nested objects
                    	{
                    		inner_attr_obj = attr_jsonObject.get(attribute_name);
                       		JSONObject inner_attr_jsonObject =  (JSONObject) inner_attr_obj;
                       		inner_attr_names = inner_attr_jsonObject.keySet();
                       		inner_attr_values = inner_attr_jsonObject.values();
                        	inner_iter_names = inner_attr_names.iterator();
                    		inner_iter_values = inner_attr_values.iterator();
                   			for(int c = 0; c < inner_attr_jsonObject.size(); c++)
                   			{
                   				String value = inner_iter_values.next().toString();
                   				if(!value.equals("false"))
                   					st.executeUpdate("Insert into BUSINESS_ATTRIBUTES Values('" + business_id + "', '" + attribute_name + "_" + inner_iter_names.next() + "')");
//                    				System.out.println("Insert into BUSINESS_ATTRIBUTES Values('" + business_id + "', '" + attribute_name + "_" + inner_iter_names.next() + "')");
                    			else
                    				inner_iter_names.next();
                    		}
                    	}
                    	else			// simple objects
                    	{	
                   			if(!attribute_value.equals("false"))
                 				st.executeUpdate("Insert into BUSINESS_ATTRIBUTES Values('" + business_id + "', '" + attribute_name + "_" + attribute_value + "')");
//                   				System.out.println("Insert into BUSINESS_ATTRIBUTES Values('" + business_id + "', '" + attribute_name + "_" + attribute_value + "')");
                   		}
                   	}
                    	
                } 
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("YelpBusiness.json Parsed");
            br.close();
			
            
            
//			 Read the YelpReview.json file
			br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(args[1])));
			System.out.println("Parsing YelpReview.json...");
			int count = 0;
            while ((sCurrentLine = br.readLine()) != null) {
            	String user_id = null, review_id = null, review_date = null, review_text = null, type = null, business_id = null, new_review_text = null;
            	long funny = 0, useful = 0, cool = 0, stars = 0;
                Object obj, votesObj;
                
                try {
                    obj = parser.parse(sCurrentLine);
                    JSONObject jsonObject = (JSONObject) obj;
                    votesObj = jsonObject.get("votes");
                    JSONObject votesJsonObject = (JSONObject) votesObj;
                    funny = (long) votesJsonObject.get("funny");
                    useful = (long) votesJsonObject.get("useful");
                    cool = (long) votesJsonObject.get("cool");
                    user_id = (String) jsonObject.get("user_id");
                    review_id = (String) jsonObject.get("review_id");
                    stars = (long) jsonObject.get("stars");
                    review_date = (String) jsonObject.get("date");
                    review_text = ((String) jsonObject.get("text")).replaceAll("\n", "");
                    new_review_text = review_text.replaceAll("'", "''");
                    type = (String) jsonObject.get("type");
                    business_id = (String) jsonObject.get("business_id");
        			
        			//Insert data into the REVIEWS_TABLE table				
        			String q = "Insert into REVIEWS_TABLE Values(" + funny + ", " + useful + ", " + cool + ", '" + user_id + "', '" + review_id + "', " + stars + ", TO_DATE('" + review_date + "', 'YYYY-MM-DD'), '" + new_review_text + "', '" + type + "', '" + business_id + "')";
//        			System.out.println(q);
        			st.executeUpdate(q);
        			
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
            }
            System.out.println("YelpReview.json Parsed");
            br.close();

            
            
//    		Read the YelpCheckin.json file
    		br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(args[2])));
    		System.out.println("Parsing YelpCheckin.json...");
            while ((sCurrentLine = br.readLine()) != null) {
            	String checkin_info = null, type = null, business_id = null;
                Object obj, checkin_obj;
                try 
                {
                	obj = parser.parse(sCurrentLine);
                	JSONObject jsonObject = (JSONObject) obj;
                    checkin_info = jsonObject.get("checkin_info").toString().replace("{","").replace("}", "");
//                  JSONObject votesJsonObject = (JSONObject) checkin_obj;
                    type = (String) jsonObject.get("type");
                    business_id = (String) jsonObject.get("business_id");
            		
            		//Insert data into the REVIEWS_TABLE table				
            		String q = "Insert into CHECKIN_TABLE Values('" + checkin_info + "', '" + type + "', '" + business_id + "')";
//            		System.out.println(q);
            		st.executeUpdate(q);
            			
                } 
                catch (ParseException e) 
                {
                    e.printStackTrace();
                }
            }
            System.out.println("YelpCheckin.json Parsed");
            con.close();
            br.close();
        } 
        
       
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        catch(SQLException e)
        {
        	e.printStackTrace();
        }
        finally 
        {
            try 
            {
                if (br != null)br.close();
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        }
    }
}