import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ImageIcon;
import javax.swing.*;

public class MainWindow {

	// Declarations for fields that need a wider scope than individual methods

	// Main JFrame for the program
	private JFrame frame;

	// JPanels used in MainWindow JFrame
	private JPanel customerViewPanel, QRCodePanel, storeMapPanel, menuItemsContainer, menuPanel, locationManager,
			QRPanel, mapPanel, locationManagerSidebar, addAreaPanel, editAreaPanel, editAndAddContainer;

	// x and y coordinates are sometimes used in multiple different action events
	// and need a wide scope
	private int xCoordinate;
	private int yCoordinate;

	// boolean flag used in multiple different action evends and needs a wider scope
	private boolean newCoordinatesSelected = false;

	// boolean flag used in multiple different action evends and needs a wider scope
	private boolean areaSet = false;

	// Declare store at top of class so it can be used by various methods
	private Store store;

	// Constructor for MainWindow
	public MainWindow() {

		// Create a new store
		store = new Store();
		
		// Populate items list from database
		store.PopulateItemsList();

		// Create JFrame and set properties
		frame = new JFrame();
		frame.setTitle("Final Project");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		// Create program's main panels
		// Top Menu bar
		createMenuPanel();

		// Panels that the menu bar is used to navigate between
		createMenuItemsContainer();

		// Ensures that the location manager is the first screen visible after launching
		// NOTE: I ran into issues with buttons from panels "behind" the front panel
		// appearing after a mouse hover
		// Manually turning off all panels except for one was my solution for this
		customerViewPanel.setVisible(false);
		QRCodePanel.setVisible(false);
		menuPanel.setVisible(true);
		menuItemsContainer.setVisible(true);
		locationManager.setVisible(true);
		locationManagerSidebar.setVisible(true);
		storeMapPanel.setVisible(false);
		editAreaPanel.setVisible(false);

		// Sets JFrame visible
		frame.setVisible(true);

		// Repaints all the main panels to ensure everything works properly. Also helped
		// fix the random appearing
		// of controls described in above note
		Repaint();

	}
	// -----------------------------------------------
	// ---------------Top Menu Bar--------------------
	// -----------------------------------------------

	// Create Menu Panel, set its properties, add buttons, and add labels
	public void createMenuPanel() {

		// Create the panel and set properties
		menuPanel = new JPanel();
		menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		menuPanel.setBackground(Color.BLUE);

		// Create store header
		JLabel storeName = new JLabel("Fred's Clothes");
		storeName.setForeground(Color.WHITE);
		storeName.setFont(new Font("Sans-serif", Font.BOLD, 42));
		menuPanel.add(storeName);

		// Button to switch to location manager
		JButton locationManagerButton = new JButton("Location Manager");
		locationManagerButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SwitchPanel(locationManager);
			}
		});

		// Button to switch to store map
		JButton mapButton = new JButton("Store Map");
		mapButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SwitchPanel(storeMapPanel);
			}
		});

		// Button to switch to QR Code
		JButton QRButton = new JButton("QR Code");
		QRButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SwitchPanel(QRCodePanel);
			}
		});

		// Button to switch to customer view
		JButton customerViewButton = new JButton("Customer View");
		customerViewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SwitchPanel(customerViewPanel);

				// Ensures that store's list model is updated so search function works properly
				store.PopulateItemsList();
			}
		});

		// Add buttons to menu panel
		menuPanel.add(locationManagerButton);
		menuPanel.add(mapButton);
		menuPanel.add(QRButton);
		menuPanel.add(customerViewButton);

		// Add menu panel to JFrame
		frame.add(menuPanel, BorderLayout.NORTH);
	}

	// Creates a JPanel that will hold the main JPanels that get swtiched between
	// Doing it this way works best with the JFrame's layout manager
	private void createMenuItemsContainer() {
		// Create panel
		menuItemsContainer = new JPanel();
		menuItemsContainer.setLayout(null);

		// Add container to JFrame
		frame.add(menuItemsContainer, BorderLayout.CENTER);

		// Create all the panels and repaint
		createLocationManagerPanel();
		createStoreMapPanel();
		createQRCodePanel();
		createCustomerViewPanel();
		Repaint();
	}

	// -----------------------------------------------
	// ------------Location Manager panel-------------
	// -----------------------------------------------

	// Create Panel for managing locations of items and Areas
	public void createLocationManagerPanel() {

		// Create panel and set properties
		locationManager = new JPanel();
		locationManager.setLayout(new BorderLayout(10, 0));
		locationManager.setBackground(new Color(222, 222, 222));
		locationManager.setBounds(0, 0, 800, 490);

		// Create Panels within location manager panel
		// sidebar
		createLocationManagerSidebar();
		// container that will hold Edit and Add Panels
		createEditandAddContainer();

		// Edit and add panels get added to container within methods
		createAddAreaPanel();
		createEditAreaPanel();

		// Add lcoation manager to the menu items container
		menuItemsContainer.add(locationManager);
	}

	// Create sidebar for location manager
	public void createLocationManagerSidebar() {
		// Create sidebar and set properties
		locationManagerSidebar = new JPanel();
		locationManagerSidebar.setLayout(new BoxLayout(locationManagerSidebar, BoxLayout.Y_AXIS));
		locationManagerSidebar.setBackground(new Color(91, 121, 186));

		// Create Sidebar buttons
		JButton addAreaButton = new JButton("Add Area");
		JButton editAreaButton = new JButton("Edit Area");

		// Click event for addAreaButton switches to that panel
		addAreaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAreaPanel.setVisible(true);
				editAreaPanel.setVisible(false);

			}
		});

		// Click event for editAreaButton swithces to that panel
		editAreaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAreaPanel.setVisible(false);
				editAreaPanel.setVisible(true);
			}
		});

		// Add controls to Panel
		locationManagerSidebar.add(addAreaButton);
		locationManagerSidebar.add(editAreaButton);

		// Add Sidebar to panel
		locationManager.add(locationManagerSidebar, BorderLayout.WEST);

	}

	// Container that holds the Edit screen and add screen
	public void createEditandAddContainer() {

		// Create container and set properties
		editAndAddContainer = new JPanel();
		editAndAddContainer.setLayout(null);
		editAndAddContainer.setBounds(0, 0, 800, 600);

		// Add container to location manager
		locationManager.add(editAndAddContainer, BorderLayout.CENTER);
	}

	// Panel for adding an area to the inventory database
	private void createAddAreaPanel() {

		// Create panel and set properties
		addAreaPanel = new JPanel();
		addAreaPanel.setLayout(null);
		addAreaPanel.setBackground(new Color(255, 247, 189));
		addAreaPanel.setBounds(0, 0, editAndAddContainer.getWidth(), editAndAddContainer.getHeight());

		// Label for error syaing that no location was set for item
		JLabel errorNoLocation = new JLabel("Please set a location.");
		errorNoLocation.setBounds(10, 160, 150, 15);
		errorNoLocation.setVisible(false);

		// Label for error saying that no name was set for item
		JLabel errorNoName = new JLabel("Please give the area a name.");
		errorNoName.setBounds(10, 180, 200, 15);
		errorNoName.setVisible(false);

		// Label for sayingg area was added successfully
		JLabel successLabel = new JLabel("Added succesfully");
		successLabel.setBounds(10, 160, 200, 15);
		successLabel.setVisible(false);

		// Label and text field for naming Area
		JLabel addItemLabel = new JLabel("Area Name");
		addItemLabel.setBounds(10, 10, 100, 10);
		JTextField txtAddArea = new JTextField(15);
		txtAddArea.setBounds(10, 30, 175, 25);

		// Load the red dot onto a JLabel that will be added to the map
		JLabel redDotLabel = LoadDot();

		// Creating store map
		JLabel mapLabelAddCoordinates = new JLabel(store.getStoreMap());
		mapLabelAddCoordinates.setBounds(200, 20, 400, 400);

		// Mouse click listener for setting red dot
		mapLabelAddCoordinates.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Make dot appear
				redDotLabel.setVisible(true);

				// set flag to true
				areaSet = true;

				// Set coordinate variables according to click event
				xCoordinate = e.getX();
				yCoordinate = e.getY();

				// Set size of red dot (otherwise it will be zero)
				redDotLabel.setSize(10, 10);

				// Set location according to coordinates
				redDotLabel.setLocation(xCoordinate, yCoordinate);

				// Add red dot label to map label
				mapLabelAddCoordinates.add(redDotLabel);
				mapLabelAddCoordinates.revalidate();
				mapLabelAddCoordinates.repaint();

			}
		});

		// Button for adding item to program
		JButton btnAddArea = new JButton("Add Area");
		btnAddArea.setBounds(10, 60, 175, 30);

//		When the button is clicked, add item to the database
		btnAddArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check if location has been set
				if (areaSet) {
					
					// Check if name has been set
					if (!txtAddArea.getText().equals("")) {
						
						// Create a new area. Set properties to textbox text and clicked coordinates
						Area newArea = new Area();
						newArea.setName(txtAddArea.getText());
						newArea.setXCoordinate(xCoordinate);
						newArea.setYCoordinate(yCoordinate);

						// Reset textbox text
						txtAddArea.setText("");
						
						// Make any errors disappear
						errorNoName.setVisible(false);
						errorNoLocation.setVisible(false);
						
						// Make success label appear
						successLabel.setVisible(true);
						
						// Make dot disappear
						redDotLabel.setVisible(false);
						
						// Reset flag
						areaSet = false;
						
						Repaint();

						// Connect to database
						try (Connection connection = DriverManager.getConnection(store.GetjdbcURL())) {
							// Prepare SQL statement
							String sql = "INSERT INTO Areas (Name, XCoordinate, YCoordinate) VALUES (?, ?, ?)";
							PreparedStatement statement = connection.prepareStatement(sql);

							// Adding name and coordinates to statement
							statement.setString(1, newArea.getName());
							statement.setDouble(2, xCoordinate);
							statement.setDouble(3, yCoordinate);

							// Execute statement
							statement.executeUpdate();

							// Populate Items list of store
							store.PopulateItemsList();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

						// Give error if no name was set
					} else {
						errorNoName.setVisible(true);
						successLabel.setVisible(false);
					}

					// Give error if no location was set
				} else {
					errorNoLocation.setVisible(true);
					successLabel.setVisible(false);
				}
			}
		});

		// add controls to panel
		addAreaPanel.add(addItemLabel);
		addAreaPanel.add(txtAddArea);
		addAreaPanel.add(btnAddArea);
		addAreaPanel.add(errorNoLocation);
		addAreaPanel.add(errorNoName);
		addAreaPanel.add(mapLabelAddCoordinates);
		addAreaPanel.add(successLabel);

		// add panel to edit and add container
		editAndAddContainer.add(addAreaPanel);
	}

	// Create panel for editing an area
	private void createEditAreaPanel() {
		// Declare string to be used as selected item to edit
		String selection;

		// Create panel and set properties
		editAreaPanel = new JPanel();
		editAreaPanel.setLayout(null);
		editAreaPanel.setBackground(new Color(255, 247, 189));
		editAreaPanel.setBounds(0, 0, editAndAddContainer.getWidth(), editAndAddContainer.getHeight());

		// Label for searching for area
		JLabel searchArea = new JLabel("Select an Area");
		searchArea.setBounds(10, 30, 150, 15);

		// Label for editing item name
		JLabel lblEditItem = new JLabel("Edit Item Name");
		lblEditItem.setBounds(10, 357, 100, 20);
		lblEditItem.setVisible(false);

		// Text field for editing item name
		JTextField txtEditName = new JTextField(15);
		txtEditName.setBounds(10, 380, 200, 25);
		txtEditName.setVisible(false);

		// Button to submit edit
		JButton btnEditItem = new JButton("Submit edit");
		btnEditItem.setBounds(10, 415, 125, 20);
		btnEditItem.setVisible(false);

		// Adding map to the Panel
		JLabel mapLabelEditCoordinates = new JLabel(store.getStoreMap());
		mapLabelEditCoordinates.setBounds(250, 20, 400, 400);
		mapLabelEditCoordinates.setVisible(false);

		
		// Add redDotIcon to a new JLabel that holds the dot
		JLabel redDotLabel = LoadDot();
		redDotLabel.setVisible(true);

		// Create scrollable list using JList and JscrollPane
		JList lstAreas = new JList(store.GetListModel());
		lstAreas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(lstAreas);
		scrollPane.setBounds(10, 60, 200, 250);
		
		// Action event for when an item from the JList is selected
		// Loads in editing controls
		lstAreas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				
				// Using this prevents glitches in the JList
				if (!e.getValueIsAdjusting()) {
					
					// Selected item is taken from textbox
					String selectedItem = (String) lstAreas.getSelectedValue();
					
					//Create Area object to hold properties of what is being edited
					Area areaEdited = new Area();
					
					// Set name to what is in the textbox
					areaEdited.setName(selectedItem);
					
					// Connect to the database so that when selecting an item its location loads
					// onto the map
					try (Connection connection = DriverManager.getConnection(store.GetjdbcURL())) {

						// Select the coordinates of the item selected
						String sql = "SELECT XCoordinate, YCoordinate FROM Areas WHERE Name = ?";
						PreparedStatement statement = connection.prepareStatement(sql);
						statement.setString(1, selectedItem); 

						// Execute the slq query
						ResultSet resultSet = statement.executeQuery();

						// Assign x and y coordinates to variables
						int xCoordinate = resultSet.getInt("XCoordinate");
						int yCoordinate = resultSet.getInt("YCoordinate");

						// Set the coordinates to the area object
						areaEdited.setXCoordinate(xCoordinate);
						areaEdited.setYCoordinate(yCoordinate);

						// Set size of red dot (it will be nothing otherwise)
						redDotLabel.setSize(10, 10);

						// Set the location of the red dot using coordinates
						redDotLabel.setLocation(xCoordinate, yCoordinate);
						
						// Add red dot to the map
						mapLabelEditCoordinates.add(redDotLabel);
						mapLabelEditCoordinates.revalidate();
						mapLabelEditCoordinates.repaint();

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					// give the store Area being edited property an area object
					store.setAreaBeingEdited(areaEdited);

					// Make editing controls visible
					lblEditItem.setVisible(true);
					txtEditName.setVisible(true);
					btnEditItem.setVisible(true);
					mapLabelEditCoordinates.setVisible(true);

					// Set editing text box to the selected item
					txtEditName.setText(selectedItem);
					txtEditName.requestFocusInWindow();

				}
			}
		});
		
		// Mouse click even listener for reassigning a location to the area
		mapLabelEditCoordinates.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				// set flag to true
				newCoordinatesSelected = true;
				
				// Make red dot visible
				redDotLabel.setVisible(true);
				
				// assign x and y coordinate variables to location that was clicked
				xCoordinate = e.getX();
				yCoordinate = e.getY();

				// make red dot visible and set its location according to coordinates
				redDotLabel.setSize(10, 10); // Set size of the red dot label
				redDotLabel.setLocation(xCoordinate, yCoordinate); // Set position of the red dot label
				
				// Assign coordinates of area being edited
				store.getAreaBeingEdited().setXCoordinate(xCoordinate);
				store.getAreaBeingEdited().setYCoordinate(yCoordinate);

				// Add red dot label to map label
				mapLabelEditCoordinates.add(redDotLabel);
				mapLabelEditCoordinates.revalidate();
				mapLabelEditCoordinates.repaint();

			}
		});

		btnEditItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean nameEdited = false;
				
				// If the name property of the area being edited is NOT what is in the textbox
				if (!store.getAreaBeingEdited().getName().equals(txtEditName.getText())) {
					
					// If so, that means the name was edited
					nameEdited = true;
				}

				// Conenct to the database
				try (Connection connection = DriverManager.getConnection(store.GetjdbcURL())) {

					//SQL query will be different depending on the conditions of what was changed
					// IF both a new name and new coordinates were selected..
					if (nameEdited && newCoordinatesSelected) {

						// Prepare SQL statement
						String sql = "UPDATE Areas SET Name = ?, XCoordinate = ?, YCoordinate = ? WHERE Name = ?";
						PreparedStatement statement = connection.prepareStatement(sql);

						// assigne values to appropriate variables
						statement.setString(1, txtEditName.getText());
						statement.setDouble(2, store.getAreaBeingEdited().getXCoordinate());
						statement.setDouble(3, store.getAreaBeingEdited().getYCoordinate());
						statement.setString(4, store.getAreaBeingEdited().getName());

						// Execute the update
						statement.executeUpdate();

					}
					
					// If only the name was edited...
					else if (nameEdited && !newCoordinatesSelected) {
						
						// Prepare sql statement
						String sql = "UPDATE Areas SET Name = ?  WHERE Name = ?";
						PreparedStatement statement = connection.prepareStatement(sql);

						// set values to appropriate variables
						statement.setString(1, txtEditName.getText());
						statement.setString(2, store.getAreaBeingEdited().getName());

						statement.executeUpdate();

					// If only the coordinates were edited...
					} else if (!nameEdited && newCoordinatesSelected) {
						// Prepare SQL statement
						String sql = "UPDATE Areas SET XCoordinate = ?, YCoordinate = ? WHERE Name = ?";
						PreparedStatement statement = connection.prepareStatement(sql);

						// set values to appropriate variables
						statement.setDouble(1, store.getAreaBeingEdited().getXCoordinate());
						statement.setDouble(2, store.getAreaBeingEdited().getYCoordinate());
						statement.setString(3, store.getAreaBeingEdited().getName());

						statement.executeUpdate();

					}
					
					// Fill store's list of item names
					store.PopulateItemsList();
					
					// Reset editing flags 
					newCoordinatesSelected = false;
					nameEdited = false;

				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				// Set editing labels invisible 
				
				lblEditItem.setVisible(false);
				txtEditName.setVisible(false);
				btnEditItem.setVisible(false);
				mapLabelEditCoordinates.setVisible(false);
			}
		});

		// Add controls to panel
		editAreaPanel.add(searchArea);
		editAreaPanel.add(scrollPane);
		editAreaPanel.add(mapLabelEditCoordinates);
		editAreaPanel.add(lblEditItem);
		editAreaPanel.add(txtEditName);
		editAreaPanel.add(btnEditItem);

		// add panel to editandadd container
		editAndAddContainer.add(editAreaPanel);
	}

	// -----------------------------------------------
	// --------------Store Map panel------------------
	// -----------------------------------------------
	public void createStoreMapPanel() {
		
		// Create panel and set properties
		storeMapPanel = new JPanel();
		storeMapPanel.setLayout(null);
		storeMapPanel.setBackground(new Color(255, 247, 189));
		storeMapPanel.setBounds(0, 0, 800, 490);

		// Create map and add to panel
		JLabel mapLabel = new JLabel(store.getStoreMap());
		mapLabel.setBounds(205, 20, 400, 400);
		storeMapPanel.add(mapLabel);

		// Create button for uploading new map
		JButton uploadMapButton = new JButton("Upload a new image");
		uploadMapButton.setBounds(320, 440, 175, 30);

		// Click event for uploading map button
		uploadMapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Open dialog for choosing a file
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(storeMapPanel);
				
				// If file is chosen
				if (result == JFileChooser.APPROVE_OPTION) {
					// Get file
					File selectedFile = fileChooser.getSelectedFile();
					
					try {
						// Scale image using buffered image
						// Make sure image fits inside a 400x400 box
						BufferedImage originalImage = ImageIO.read(selectedFile);
						
						// Find scale factor
						double scaleFactor = Math.min((double) 400 / originalImage.getWidth(),
								(double) 400 / originalImage.getHeight());
						
						// Find scaled width and height
						int scaledWidth = (int) (originalImage.getWidth() * scaleFactor);
						int scaledHeight = (int) (originalImage.getHeight() * scaleFactor);
						
						// Set new image using scaling
						Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight,
								Image.SCALE_SMOOTH);
						
						// Create new imageIcon using output
						ImageIcon imageOutput = new ImageIcon(scaledImage);

						// Set scaled ImageIcon to the store object
						store.setStoreMap(imageOutput);
						
						// Remove previous map
						storeMapPanel.remove(mapLabel);

						// Create new JLabel to hold new image and set it to the panel
						JLabel mapLabel = new JLabel(imageOutput);
						mapLabel.setBounds(205, 20, 400, 400);
						storeMapPanel.add(mapLabel);
						storeMapPanel.revalidate();
						storeMapPanel.repaint();
						
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		// Add button to panel
		storeMapPanel.add(uploadMapButton);

		// Add panel to container
		menuItemsContainer.add(storeMapPanel);
	}

	// -----------------------------------------------
	// ---------------QR Code panel-------------------
	// -----------------------------------------------

	public void createQRCodePanel() {
		// Create panel and set properties
		QRCodePanel = new JPanel();
		QRCodePanel.setLayout(null);
		QRCodePanel.setBackground(new Color(255, 247, 189));
		QRCodePanel.setBounds(0, 0, 800, 490);

		// QR Code in files gets scaled to fit into the panel
		ImageIcon originalIcon = store.getQRCode();
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(scaledImage);

		// Add the scaled ImageIcon to a JLabel
		JLabel QRLabel = new JLabel(scaledIcon);
		QRLabel.setBounds(205, 20, 400, 400);

		// Add print button and set properites
		JButton printButton = new JButton("Print QR Code");
		printButton.setBounds(320, 440, 175, 30);
		
		// Clicking the printer creates to new Printer instance, calling printing dialog through constructor
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Printer printer = new Printer(scaledIcon);
			}
		});

		// Add QR Label and button to panel
		QRCodePanel.add(QRLabel);
		QRCodePanel.add(printButton);

		menuItemsContainer.add(QRCodePanel);
	}

	// -----------------------------------------------
	// ------------Customer View panel----------------
	// -----------------------------------------------

	public void createCustomerViewPanel() {
		
		//Create panel and set properties
		customerViewPanel = new JPanel();
		customerViewPanel.setLayout(null);
		customerViewPanel.setBackground(new Color(255, 247, 189));
		customerViewPanel.setBounds(0, 0, 800, 490);
		
		JLabel welcomeLabel = new JLabel("Welcome to Fred's Clothes! Search for an item");
		welcomeLabel.setBounds(175, 20, 500, 30);
		welcomeLabel.setFont(new Font ("Sans-serif", Font.BOLD, 20));

		// Create new JList and scrollpane for search result box
		JList suggestionsList = new JList<>();
		DefaultListModel<String> suggestionsListModel = new DefaultListModel<>();
		JScrollPane ScrollPane = new JScrollPane(suggestionsList);
		ScrollPane.setBounds(250, 95, 300, 100);
		
		// Add model to suggestions list
		suggestionsList.setModel(suggestionsListModel);

		// Add new text box for serching
		JTextField searchField = new JTextField(15);
		searchField.setBounds(350, 70, 100, 20);

		

		// Event listener for text changes in the search field
		searchField.getDocument().addDocumentListener(new DocumentListener() {
		
			// Listends for insertions into the textbox
			public void insertUpdate(DocumentEvent e) {
				updateSuggestions(searchField, suggestionsListModel, store.GetListModel(), ScrollPane);
			}

			// Listens for removals from textbox
			public void removeUpdate(DocumentEvent e) {
				updateSuggestions(searchField, suggestionsListModel, store.GetListModel(), ScrollPane);
			}

			// Listens for other changes
			// I think this needs to be here for the addDocumentListener interface to actually work
			public void changedUpdate(DocumentEvent e) {
				updateSuggestions(searchField, suggestionsListModel, store.GetListModel(), ScrollPane);
			}
		});

		// Adding map to the Panel
		JLabel mapLabelCustomerView = new JLabel(store.getStoreMap());
		mapLabelCustomerView.setBounds(200, 100, 400, 380);
		mapLabelCustomerView.setVisible(false);

		
		// Add red dot to a new JLabel that holds the dot
		JLabel redDotLabel = LoadDot();
		redDotLabel.setVisible(true);

		// Event listener for clicking on a search result
		// Very similar to JList in edit Area Panel
		suggestionsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					
					// Set dot to visible
					redDotLabel.setVisible(true);

					// set selected item variable based off of what is selected
					String selectedItem = (String) suggestionsList.getSelectedValue();
					
//					// Fills text box with selection
//					searchField.setText(selectedItem);
					
					// Connect to database
					try (Connection connection = DriverManager.getConnection(store.GetjdbcURL())) {

						// Prepare sql statement
						String sql = "SELECT XCoordinate, YCoordinate FROM Areas WHERE Name = ?";
						PreparedStatement statement = connection.prepareStatement(sql);
						statement.setString(1, selectedItem);

						// Execute the query
						ResultSet resultSet = statement.executeQuery();

						// Set variables to results of query
						int xCoordinate = resultSet.getInt("XCoordinate");
						int yCoordinate = resultSet.getInt("YCoordinate");
						
						// Set red dot size
						redDotLabel.setSize(10, 10);

						// Set lcoatin of red dot to coordinates from query
						redDotLabel.setLocation(xCoordinate, yCoordinate);
						
						// Add dot to map
						mapLabelCustomerView.add(redDotLabel);
						mapLabelCustomerView.revalidate();
						mapLabelCustomerView.repaint();

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					// Make map visible
					mapLabelCustomerView.setVisible(true);
					mapLabelCustomerView.revalidate();
					mapLabelCustomerView.repaint();
					
					// Make results box invisible after selection is clicked
					ScrollPane.setVisible(false);

				}
			}
		});

		// Add controls to panel
		customerViewPanel.add(searchField);
		customerViewPanel.add(ScrollPane);
		customerViewPanel.add(mapLabelCustomerView);
		customerViewPanel.add(redDotLabel);
		customerViewPanel.add(welcomeLabel);

		customerViewPanel.repaint();
		customerViewPanel.revalidate();

		// Add panel to container
		menuItemsContainer.add(customerViewPanel);
	}

	// Updates the suggestions box in the customer view panel
	private void updateSuggestions(JTextField searchField, DefaultListModel<String> listModel,
		DefaultListModel<String> storeListModel, JScrollPane ScrollPane) {
		
		// Get text from text box
		String searchText = searchField.getText().toLowerCase();

		// Check if empty
		if (searchText.isEmpty()) {
			listModel.clear();
		} else {
			listModel.clear();
			
			// Loop through store list model and add items to new list model that contains similar text
			for (int i = 0; i < storeListModel.getSize(); i++) {
				if (storeListModel.getElementAt(i).toLowerCase().contains(searchText)) {
					listModel.addElement(storeListModel.getElementAt(i));
				}
			}
		}
		// set scrollpane to visible
		ScrollPane.setVisible(true);
	}

	// Repaint all relevant panels to get rid of glitching controls
	public void Repaint() {
		customerViewPanel.repaint();
		QRCodePanel.repaint();
		menuPanel.repaint();
		menuItemsContainer.repaint();
		locationManager.repaint();
		locationManagerSidebar.repaint();
		storeMapPanel.repaint();
		editAreaPanel.repaint();
		menuItemsContainer.repaint();

		customerViewPanel.revalidate();
		QRCodePanel.revalidate();
		menuPanel.revalidate();
		menuItemsContainer.revalidate();
		locationManager.revalidate();
		locationManagerSidebar.revalidate();
		storeMapPanel.revalidate();
		editAreaPanel.revalidate();
		menuItemsContainer.repaint();
	}

	// Method for swithcinng between panels
	public void SwitchPanel(JPanel desinationPanel) {
		// Set all panels to invisible
		locationManager.setVisible(false);
		QRCodePanel.setVisible(false);
		storeMapPanel.setVisible(false);
		customerViewPanel.setVisible(false);

		// Make desired panel visible
		desinationPanel.setVisible(true);
	}

	// Loads the red dot
	public JLabel LoadDot() {
		
		// create file
		File file = new File("Graphics/redDot.jpg");

		// Create image of dot
		Image redDotOriginal = null;

		// load dot using file
		try {
			redDotOriginal = ImageIO.read(file);
		} catch (IOException e) {

			e.printStackTrace();
		}

		// Scale the dot
		Image redDotScaled = redDotOriginal.getScaledInstance(10, 10, Image.SCALE_SMOOTH);

		// Create a ImageIcon from the scaled image
		ImageIcon redDotIcon = new ImageIcon(redDotScaled);

		// Add redDotIcon to a new JLabel that holds the dot
		JLabel redDotLabel = new JLabel(redDotIcon);

		// Return dot
		return redDotLabel;
	}
}
