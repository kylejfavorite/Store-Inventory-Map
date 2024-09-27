import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

public class Store {

	// Save store map as image from files for use as example. 
	// This can be modified dynamicall in program
	ImageIcon storeMap = new ImageIcon("Graphics/StoreMap.jpg");
	
	// Save store map as image from files for use as example. 
	private ImageIcon QRCode = new ImageIcon("Graphics/QRCode.png");

	// Contains a list of names of items/areas in program
	//PopulateItemsList method refreshes this property
	private DefaultListModel<String> listModel;
	
	// Link to database
	private String jdbcUrl = "jdbc:sqlite:database/StoreInventory.db";
	
	// When an item is clicked to edit, this property changes dynamically to 
	// better keep track of what needs to be changed on GUI and in database
	private Area AreaBeingEdited;
	
	// Constructor for store instantiates listModel
	public Store() {
		listModel = new DefaultListModel<String>();
		
	}

	// Modifies store map with new ImageIcon
	public void setStoreMap(ImageIcon _storeMap) {
		storeMap = _storeMap;
	}

	// Returns store map
	public ImageIcon getStoreMap() {
		return storeMap;
	}

	// Returns listModel
	public DefaultListModel<String> GetListModel() {
		return listModel;
	}

	// Repopulates (refreshes) listModel property GetListModel returns accurate list from database
	public void PopulateItemsList() {
		try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

			// Prepare SQL statement
			String sql = "SELECT Name FROM Areas";
			PreparedStatement statement = connection.prepareStatement(sql);

			// Execute query
			ResultSet resultSet = statement.executeQuery();

			// First clear list model
				listModel.clear();
			
			// Loop through resultSet to add items to the list model
			while (resultSet.next()) {
				String areaName = resultSet.getString("Name");
				listModel.addElement(areaName);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Returns area being edited
	public Area getAreaBeingEdited() {
		return AreaBeingEdited;
	}

	// Sets area being edited
	public void setAreaBeingEdited(Area areaBeingEdited) {
		AreaBeingEdited = areaBeingEdited;
	}

	// Returns QR Code image
	// This doesn't have a setter because IRL, a business using this software wouldn't
	// have an option within the GUI to give themselves a new QR code. This would be assigned
	// by the owners of the software
	public ImageIcon getQRCode() {
		return QRCode;
	}

	public String GetjdbcURL() {
		return jdbcUrl;
	}
	


	

}
