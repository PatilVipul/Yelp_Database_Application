package com.dbconn;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.proteanit.sql.DbUtils;

public class hw3{
	final static JFrame window = new JFrame("Yelp Tool");
	final static JPanel panel = new JPanel();
	final static Button startButton = new Button("Start");
	
	final static JPanel main_panel = new JPanel();
	String main_categories [] = {"Active Life", "Arts & Entertainment", "Automotive", "Car Rental","Cafes","Beauty & Spas","Convenience Stores","Dentists","Doctors","Drugstores","Department Stores","Education","Event Planning & Services","Flowers & Gifts","Food","Health & Medical","Home Services","Home & Garden","Hospitals","Hotels & Travel","Hardware Stores","Grocery","Medical Centers","Nurseries & Gardening","Nightlife","Restaurants","Shopping","Transportation"};
	final ArrayList<JCheckBox> main_checkboxes_array = new ArrayList<JCheckBox>();
	
	final static JPanel sub_panel = new JPanel();
	final static JPanel inner_sub_panel = new JPanel();
	final JScrollPane sub_scroll = new 	JScrollPane(inner_sub_panel);
	
	final static JPanel attr_panel = new JPanel();
	final static JPanel inner_attr_panel = new JPanel();
	final JScrollPane attr_scroll = new JScrollPane(inner_attr_panel);
	
	final static JPanel business_panel = new JPanel();
	
	final static JLabel dayLabel = new JLabel("Day of the week:");
	final static JLabel fromLabel = new JLabel("From:");
	final static JLabel toLabel = new JLabel("To:");
	final static JLabel searchLabel = new JLabel("Search for:");
	
	String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	final JComboBox<?> dayComboBox = new JComboBox<>(days);
	
	String time[] = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
	final JComboBox<?> fromComboBox = new JComboBox<>(time);
	final JComboBox<?> toComboBox = new JComboBox<>(time);
	
	final ArrayList<JCheckBox> sub_checkboxes_array = new ArrayList<JCheckBox>();
	final ArrayList<String> sub_checkboxes_string = new ArrayList<String>();
	
	final ArrayList<JCheckBox> attr_checkboxes_array = new ArrayList<JCheckBox>();
	final ArrayList<String> attr_checkboxes_string = new ArrayList<String>();
	
	int old_sub_count = 0, new_sub_count = 0, old_attr_count = 0, new_attr_count = 0;
	
	final static JButton searchButton = new JButton("SEARCH");
	final static JButton closeButton = new JButton("CLOSE");
	
	JTable business_table = new JTable();
	
	JFrame reviewsWindow = new JFrame();
	
	public static void main(String[] args) throws ClassNotFoundException{
		hw3 obj = new hw3();		
	}

	public hw3(){
		window.setSize(1350,720);
		window.setResizable(false);
		
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(1350,720));
		panel.setBackground(new Color(33,33,146));
		
		startButton.setFont(new Font("Arial", Font.BOLD, 15));
    	startButton.setBounds(10, 10, 1320, 20);
    	startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	panel.add(startButton);
    	startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				System.out.println("Refreshed the application");
				dayComboBox.setSelectedIndex(0);
				fromComboBox.setSelectedIndex(0);
				toComboBox.setSelectedIndex(0);
			}
		});
    	
    	main_panel.setLayout(new GridLayout(28, 1, 10, 5));
    	main_panel.setBounds(10, 40, 250, 570);
    	main_panel.setBackground(new Color(179, 179, 255));		
		for(int i = 0; i < 28; ++i)
		{
			final JCheckBox main_itemCheckBox = new JCheckBox();
			main_itemCheckBox.setText(main_categories[i]);
			main_itemCheckBox.setBackground(new Color(179, 179, 255));
			main_itemCheckBox.addActionListener(main_actionListener);
			main_checkboxes_array.add(main_itemCheckBox);
			main_panel.add(main_itemCheckBox);
		}
		panel.add(main_panel);
    	
    	sub_panel.setBounds(270, 40, 250, 570);
    	sub_panel.setBackground(new Color(0, 176, 179));
    	panel.add(sub_panel);
    	
    	attr_panel.setBounds(530, 40, 250, 570);
    	attr_panel.setBackground(new Color(179, 254, 255));
    	panel.add(attr_panel);
    	    	    	
    	business_panel.setLayout(new BorderLayout());
    	business_panel.setBounds(790, 40, 540, 570);
    	business_panel.setBackground(Color.WHITE);
    	panel.add(business_panel);	
    	
    	dayLabel.setBounds(15, 640, 100, 20);
    	dayLabel.setForeground(new Color(255,255,255));
    	panel.add(dayLabel);
    	
    	dayComboBox.setBounds(15, 660, 150, 20);
    	panel.add(dayComboBox);
    	
    	fromLabel.setBounds(220, 640, 100, 20);
    	fromLabel.setForeground(new Color(225,255,255));
    	panel.add(fromLabel);
    	
    	fromComboBox.setBounds(220, 660, 80, 20);
    	panel.add(fromComboBox);
    	
    	toLabel.setBounds(350, 640, 100, 20);
    	toLabel.setForeground(new Color(225,255,255));
    	panel.add(toLabel);
    	
    	toComboBox.setBounds(350, 660, 80, 20);
    	panel.add(toComboBox);
    	
    	searchButton.setBounds(1080, 640, 100, 30);
    	searchButton.addActionListener(searchButtonListener);
    	panel.add(searchButton);
    	
    	closeButton.setBounds(1230, 640, 100, 30);
    	closeButton.addActionListener(closeButtonListener);
    	panel.add(closeButton);
    	
    	window.setVisible(true);
	    window.add(panel);
	}
	
	ActionListener closeButtonListener = new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {

	    	int selectedOption = JOptionPane.showConfirmDialog(window, "Exit Yelp application?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
	    	if(selectedOption == JOptionPane.YES_OPTION)
	    	{	
	    		window.dispose();
	    		reviewsWindow.dispose();
	    	}
	    }
	};
	
	
	ActionListener main_actionListener = new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
	        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
	        if(abstractButton.getModel().isSelected())
			{
	            try 
	            {
	            	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
					Statement st = con.createStatement();
					ResultSet rs1 = st.executeQuery("Select Count(distinct SUB_CATEGORY) from BUSINESS_CATEGORY_TABLE where MAIN_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs1.next()){
						new_sub_count = rs1.getInt(1);
						System.out.println("Sub_Count : " + new_sub_count);
					}
					old_sub_count = old_sub_count + new_sub_count;

					sub_panel.revalidate();
					sub_panel.repaint();
					sub_panel.setBackground(new Color(0, 176, 179));
					inner_sub_panel.revalidate();
					inner_sub_panel.repaint();
					inner_sub_panel.setBackground(new Color(0,176,179));
					inner_sub_panel.setLayout(new GridLayout(old_sub_count, 1, 1, 0));
					sub_panel.setLayout(new FlowLayout());
					ResultSet rs2 = st.executeQuery("Select distinct SUB_CATEGORY from BUSINESS_CATEGORY_TABLE where MAIN_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs2.next())
					{
						if(rs2.getString("SUB_CATEGORY")!=null)
						{
							JCheckBox itemCheckBox = new JCheckBox();
							itemCheckBox.setText(rs2.getString("SUB_CATEGORY"));
							itemCheckBox.setBackground(new Color(0, 176, 179));
							itemCheckBox.setPreferredSize(new Dimension(200, 20));
							sub_checkboxes_array.add(itemCheckBox);
						}
					}
					
					for(int i = 0; i < sub_checkboxes_array.size(); i++)
					{
						JCheckBox itemCheckBox = sub_checkboxes_array.get(i);
						itemCheckBox.setText(sub_checkboxes_array.get(i).getText());
						itemCheckBox.setPreferredSize(new Dimension(200, 20));
						itemCheckBox.addActionListener(sub_actionListener);
						inner_sub_panel.add(itemCheckBox);
						System.out.println("Array[]" + i + ":" + sub_checkboxes_array.get(i).getText() + "\tCheckBox" + i + ": " + itemCheckBox.getText());
					}
					
					sub_scroll.setViewportView(inner_sub_panel);
					sub_scroll.setPreferredSize(new Dimension(250,  570));
					sub_panel.add(sub_scroll);
			    
					panel.add(sub_panel);
					con.close();
				} 
	            catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.out.println(abstractButton.getText() + " selected");
			}	
			
	        else
			{	
				try
				{
					sub_panel.removeAll();
					inner_sub_panel.removeAll();
					sub_panel.setBackground(new Color(0, 176, 179));
					inner_sub_panel.setBackground(new Color(0, 176, 179));
					for(int  i = 0; i < sub_checkboxes_array.size(); ++i)
					{
						sub_checkboxes_string.add(sub_checkboxes_array.get(i).getText());
					}
					
					DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
					Statement st = con.createStatement();
					ResultSet rs1 = st.executeQuery("Select DISTINCT SUB_CATEGORY from BUSINESS_CATEGORY_TABLE where MAIN_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs1.next()){
						if(rs1.getString("SUB_CATEGORY")!=null){
							if(sub_checkboxes_string.contains(rs1.getObject("SUB_CATEGORY")))
							{
								sub_checkboxes_string.remove(rs1.getObject("SUB_CATEGORY"));
							}
						}
					}
					
					System.out.println("remaining: " + sub_checkboxes_string);
					sub_checkboxes_array.clear();
					
					for(int  i = 0; i < sub_checkboxes_string.size(); ++i)
					{
						JCheckBox itemCheckBox = new JCheckBox();
						itemCheckBox.setText(sub_checkboxes_string.get(i));
						itemCheckBox.setBackground(new Color(0, 176, 179));
						itemCheckBox.setPreferredSize(new Dimension(200, 20));
						itemCheckBox.addActionListener(sub_actionListener);
						inner_sub_panel.add(itemCheckBox);						
					}
					
					sub_scroll.setViewportView(inner_sub_panel);
					sub_scroll.setPreferredSize(new Dimension(250,  570));
					sub_panel.add(sub_scroll);
					sub_panel.revalidate();
					sub_panel.repaint();
			    
					panel.add(sub_panel);
			
					attr_panel.removeAll();
					inner_attr_panel.removeAll();
					
					attr_panel.setBackground(new Color(179, 254, 255));
					inner_attr_panel.setBackground(new Color(179, 254, 255));
					for(int  i = 0; i < attr_checkboxes_array.size(); ++i)
					{
						attr_checkboxes_string.add(attr_checkboxes_array.get(i).getText());
					}
					
					// if the main_category is unchecked then also clear the all the related attributes
					ResultSet rs3 = st.executeQuery("SELECT DISTINCT A.ATTRIBUTE_NAME FROM BUSINESS_ATTRIBUTES A, BUSINESS_CATEGORY_TABLE C WHERE A.BUSINESS_ID = C.BUSINESS_ID AND C.MAIN_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs3.next()){
						if(attr_checkboxes_string.contains(rs3.getString("ATTRIBUTE_NAME")))
						{
							attr_checkboxes_string.remove(rs3.getString("ATTRIBUTE_NAME"));
						}
					}
					
					System.out.println("remaining: " + attr_checkboxes_string);
					attr_checkboxes_array.clear();
					
					for(int  i = 0; i < attr_checkboxes_string.size(); ++i)
					{
						JCheckBox itemCheckBox = new JCheckBox();
						itemCheckBox.setText(attr_checkboxes_string.get(i));
						itemCheckBox.setBackground(new Color(179, 254, 255));
						itemCheckBox.setPreferredSize(new Dimension(200, 20));
						inner_attr_panel.add(itemCheckBox);						
					}
					
					attr_scroll.setViewportView(inner_attr_panel);
					attr_scroll.setPreferredSize(new Dimension(250,  570));
					attr_panel.add(attr_scroll);
					attr_panel.revalidate();
					attr_panel.repaint();			    
					panel.add(attr_panel);
					
					con.close();
					
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				
				System.out.println(abstractButton.getText() + " unchecked");					
			}
	      }
	};
	
	ActionListener sub_actionListener = new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
	        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
	        if(abstractButton.getModel().isSelected())
			{
	        	try 
	            {
	            	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
					Statement st = con.createStatement();
					ResultSet rs1 = st.executeQuery("SELECT COUNT(DISTINCT A.ATTRIBUTE_NAME) FROM BUSINESS_ATTRIBUTES A, BUSINESS_CATEGORY_TABLE C WHERE A.BUSINESS_ID = C.BUSINESS_ID AND C.SUB_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs1.next()){
						new_attr_count = rs1.getInt(1);
						System.out.println("Sub_Count : " + new_attr_count);
					}
					old_attr_count = old_attr_count + new_attr_count;
					
					inner_attr_panel.revalidate();
					inner_attr_panel.repaint();
					inner_attr_panel.setBackground(new Color(179, 254, 255));
					attr_panel.revalidate();
					attr_panel.repaint();
					attr_panel.setBackground(new Color(179, 254, 255));
					inner_attr_panel.setLayout(new GridLayout(old_attr_count, 1, 1, 0));
					attr_panel.setLayout(new FlowLayout());
					
					ResultSet rs2 = st.executeQuery("SELECT DISTINCT A.ATTRIBUTE_NAME FROM BUSINESS_ATTRIBUTES A, BUSINESS_CATEGORY_TABLE C WHERE A.BUSINESS_ID = C.BUSINESS_ID AND C.SUB_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs2.next())
					{
						JCheckBox itemCheckBox = new JCheckBox();
						itemCheckBox.setText(rs2.getString("ATTRIBUTE_NAME"));
						itemCheckBox.setBackground(new Color(179, 254, 255));
						itemCheckBox.setPreferredSize(new Dimension(200, 20));
						attr_checkboxes_array.add(itemCheckBox);
					}
					
					for(int i = 0; i < attr_checkboxes_array.size(); i++)
					{
						JCheckBox itemCheckBox = attr_checkboxes_array.get(i);
						itemCheckBox.setText(attr_checkboxes_array.get(i).getText());
						itemCheckBox.setPreferredSize(new Dimension(200, 20));
						inner_attr_panel.add(itemCheckBox);
						System.out.println("Array[]" + i + ":" + attr_checkboxes_array.get(i).getText() + "\tCheckBox" + i + ": " + itemCheckBox.getText());
					}
					
					attr_scroll.setViewportView(inner_attr_panel);
					attr_scroll.setPreferredSize(new Dimension(250,  570));
					attr_panel.add(attr_scroll);
					panel.add(attr_panel);
					con.close();
	            }
	        	catch(SQLException e)
	        	{
	        		e.printStackTrace();
	        	}

	        	System.out.println(abstractButton.getText() + " checked");
			}
	        else
	        {	        	
	        	try
				{
					attr_panel.removeAll();
					inner_attr_panel.removeAll();
					
					attr_panel.setBackground(new Color(179, 254, 255));
					inner_attr_panel.setBackground(new Color(179, 254, 255));
					for(int  i = 0; i < attr_checkboxes_array.size(); ++i)
					{
						attr_checkboxes_string.add(attr_checkboxes_array.get(i).getText());
					}
					
					DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
					Statement st = con.createStatement();
					ResultSet rs1 = st.executeQuery("SELECT DISTINCT A.ATTRIBUTE_NAME FROM BUSINESS_ATTRIBUTES A, BUSINESS_CATEGORY_TABLE C WHERE A.BUSINESS_ID = C.BUSINESS_ID AND C.SUB_CATEGORY = '" + abstractButton.getText() + "'");
					while(rs1.next()){
						if(attr_checkboxes_string.contains(rs1.getString("ATTRIBUTE_NAME")))
						{
							attr_checkboxes_string.remove(rs1.getString("ATTRIBUTE_NAME"));
						}
					}
					
					System.out.println("remaining: " + attr_checkboxes_string);
					attr_checkboxes_array.clear();
					
					for(int  i = 0; i < attr_checkboxes_string.size(); ++i)
					{
						JCheckBox itemCheckBox = new JCheckBox();
						itemCheckBox.setText(attr_checkboxes_string.get(i));
						itemCheckBox.setBackground(new Color(179, 254, 255));
						itemCheckBox.setPreferredSize(new Dimension(200, 20));
						inner_attr_panel.add(itemCheckBox);						
					}
					
					attr_scroll.setViewportView(inner_attr_panel);
					attr_scroll.setPreferredSize(new Dimension(250,  570));
					attr_panel.add(attr_scroll);
					attr_panel.revalidate();
					attr_panel.repaint();
			    
					panel.add(attr_panel);
					con.close();
					
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
	        	System.out.println(abstractButton.getText() + " UNCHECKED");
	        }
	    }
	};
	
	
	ActionListener searchButtonListener = new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
	    	
	    	if(fromComboBox.getSelectedIndex()>=toComboBox.getSelectedIndex())
	    	{
	    		JOptionPane.showMessageDialog(panel, "Please enter valid open and close timings");
	    	}
	    	else
	    	{
	    		try{
	    		String day = (String) dayComboBox.getSelectedItem();
	    		int open = fromComboBox.getSelectedIndex();
	    		int close = toComboBox.getSelectedIndex();
	    		System.out.println("Day selected: " + day);
	    		ArrayList<String> selected_main = new ArrayList<String>();
	    		ArrayList<String> selected_subs = new ArrayList<String>();
	    		ArrayList<String> selected_attr = new ArrayList<String>();
	    		ArrayList<String> businessIDs = new ArrayList<String>();
	    		
	    		for(int i = 0; i < main_checkboxes_array.size(); ++i)
	    		{
	    			if(main_checkboxes_array.get(i).isSelected())
	    			{
	    				selected_main.add(main_checkboxes_array.get(i).getText());
	    			}
	    		}
	    		System.out.println("Selected main categories: " + selected_main);
	    		
	    		for(int i = 0; i < sub_checkboxes_array.size(); ++i)
	    		{
	    			if(sub_checkboxes_array.get(i).isSelected())
	    			{
	    				selected_subs.add(sub_checkboxes_array.get(i).getText());
	    			}
	    		}
	    		System.out.println("Selected sub_categories: " + selected_subs);
	    		
	    		for(int i = 0; i < attr_checkboxes_array.size(); ++i)
	    		{
	    			if(attr_checkboxes_array.get(i).isSelected())
	    			{
	    				selected_attr.add(attr_checkboxes_array.get(i).getText());
	    			}
	    		}
	    		System.out.println("Selected attributes: " + selected_attr);
	    		
	    		
	    		
	    			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	    			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
	    			Statement st = con.createStatement();
	    			for(int i = 0; i < selected_main.size(); ++i)
	    			{
	    				for(int j = 0; j < selected_subs.size(); ++j)
	    				{
	    						ResultSet rs1 = st.executeQuery("SELECT C.BUSINESS_ID FROM BUSINESS_CATEGORY_TABLE C WHERE C.MAIN_CATEGORY = '" + selected_main.get(i) + "' AND C.SUB_CATEGORY = '" + selected_subs.get(j) + "'");
	    						while(rs1.next())
	    						{
	    							businessIDs.add(rs1.getString("BUSINESS_ID"));
	    						}
	    				}
	    			}
	    			
	    			ArrayList<String> businessIDs2 = new ArrayList<String>();
	    			for(int i = 0; i < selected_attr.size(); ++i)
	    			{
	    				for(int j = 0; j < businessIDs.size(); ++j)
	    				{
	    					ResultSet rs2 = st.executeQuery("SELECT DISTINCT A.BUSINESS_ID FROM BUSINESS_ATTRIBUTES A WHERE A.ATTRIBUTE_NAME = '" + selected_attr.get(i) + "' AND A.BUSINESS_ID = '" + businessIDs.get(j) + "'");
    						while(rs2.next())
    						{
    							businessIDs2.add(rs2.getString("BUSINESS_ID"));
    						}
	    				}
	    			}
	    				
	    			ArrayList<String> businessIDs3 = new ArrayList<String>();
	    			for(int i = 0; i < businessIDs2.size(); ++i)
	    			{
	    				ResultSet rs3 = st.executeQuery("SELECT H.BUSINESS_ID FROM BUSINESS_HOURS H WHERE H.BUSINESS_ID = '" + businessIDs2.get(i) + "' AND H.BUSINESS_DAY = '" + day + "' AND H.OPEN_TIME >= " + open + " AND H.CLOSE_TIME <= " + close);
	    				while(rs3.next())
	    				{
	    					businessIDs3.add(rs3.getString("BUSINESS_ID"));
	    				}
	    			}
	    		
	    			final ArrayList<String> businessIDs4 = new ArrayList<String>();
	    			business_panel.removeAll();
	    			String [] column_names = {"Business Name","City","State","Stars"};
	    			Object data[][] =  new Object[businessIDs3.size()][4];
	    			for(int i = 0; i < businessIDs3.size(); ++i)
	    			{
	    				ResultSet rs4 = st.executeQuery("SELECT DISTINCT B.BUSINESS_ID, B.BUSINESS_NAME, B.CITY_NAME, B.STATE_NAME, B.STARS FROM BUSINESS B WHERE B.BUSINESS_ID = '" + businessIDs3.get(i) + "'");
		    		    while (rs4.next()) {
		    		    	
		    		    	System.out.println("Name: " + rs4.getString("BUSINESS_NAME") + "\tCity: " + rs4.getString("CITY_NAME") + "\tState: " + rs4.getString("STATE_NAME") + "\tStars: " + rs4.getInt("STARS"));
		    		    		data[i][0] = rs4.getString("BUSINESS_NAME");
		    		    		data[i][1] = rs4.getString("CITY_NAME");
		    		    		data[i][2] = rs4.getString("STATE_NAME");
		    		    		data[i][3] = new Integer(rs4.getInt("STARS"));
		    		    		businessIDs4.add(rs4.getString("BUSINESS_ID"));
		    		    }
	    			}
	    			business_table = new JTable(data, column_names);
	    			business_table.addMouseListener(new MouseAdapter() {
	    				
	    				public void mouseClicked(MouseEvent e)
	    				{
	    					int row = business_table.rowAtPoint(e.getPoint());   					
	    					System.out.println("\tBid: " + businessIDs4.get(row));
	    					
	    					reviewsWindow = new JFrame("Reviews");
	    					reviewsWindow.setSize(1350,720);
	    					reviewsWindow.setLayout(null);
	    					reviewsWindow.setResizable(false);
	    					
	    					JPanel reviewsPanel = new JPanel();
	    					reviewsPanel.setLayout(new BorderLayout());
	    					reviewsPanel.setBounds(10, 10, 1320, 610);
	    					
	    					JTable reviewsTable = new JTable();	    					
	    					try{
	    						DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	    		    			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "scott", "tiger");
	    		    			Statement st = con.createStatement();
//	    		    			ResultSet rs5 = st.executeQuery("SELECT REVIEW_DATE, STARS, REVIEW_TEXT, USER_ID, FUNNY_VOTES, USEFUL_VOTES, COOL_VOTES FROM REVIEWS_TABLE WHERE BUSINESS_ID = '" + businessIDs4.get(row) + "'");
	    		    			ResultSet rs5 = st.executeQuery("SELECT BUSINESS_ID, FULL_ADDRESS, CITY_NAME, STATE_NAME, REVIEW_COUNT, BUSINESS_NAME FROM BUSINESS");
	    		    			reviewsTable.setModel(DbUtils.resultSetToTableModel(rs5));
	    					}
	    					catch(SQLException e1)
	    					{
	    						e1.printStackTrace();
	    					}
	    					
	    					JScrollPane reviews_scroll = new JScrollPane(reviewsTable);
	    					reviewsTable.getColumnModel().getColumn(0).setMinWidth(150);
	        				reviewsTable.getColumnModel().getColumn(1).setMinWidth(150);
	        				reviewsTable.getColumnModel().getColumn(2).setMinWidth(150);
	        				reviewsTable.getColumnModel().getColumn(3).setMinWidth(150);
	        				reviewsTable.getColumnModel().getColumn(4).setMinWidth(150);
	        				reviewsTable.getColumnModel().getColumn(5).setMinWidth(150);
	        				reviewsTable.setFillsViewportHeight(true);
	        				reviewsPanel.add(reviews_scroll);
	        				reviewsWindow.add(reviewsPanel);

	        				JButton closeReviews_button = new JButton("CLOSE");
	    					closeReviews_button.setBounds(1230, 640, 100, 30);
	    			    	closeReviews_button.addActionListener(closeReviewsListener);
	    					reviewsWindow.add(closeReviews_button);
	    					
	    					reviewsWindow.setVisible(true);
	    					reviewsWindow.add(reviewsPanel);
	    				}
	    				
					});
	    			
	    			JScrollPane business_scroll = new JScrollPane(business_table);
    				TableColumn thirdcolumn = business_table.getColumnModel().getColumn(2);
    				TableColumn fourthcolumn = business_table.getColumnModel().getColumn(3);
    				thirdcolumn.setMaxWidth(70);
    				fourthcolumn.setMaxWidth(70);
    				business_table.setFillsViewportHeight(true);
    				business_panel.add(business_scroll);
    				business_panel.revalidate();
    				business_panel.repaint();
    				panel.add(business_panel);
	    		
	    			con.close();
	    		}
	    		catch(SQLException e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
	    }
	};
	
	
	ActionListener closeReviewsListener = new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
	    		reviewsWindow.dispose();
	    }
	};
}
