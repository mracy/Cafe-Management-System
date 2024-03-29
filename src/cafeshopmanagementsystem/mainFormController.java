package cafeshopmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class mainFormController implements Initializable {

    @FXML
    private Button customers_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private TableView<productData> inventory_tableView;

    @FXML
    private Button inventory_addBtn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button inventory_clearBtn;

    @FXML
    private TableColumn<productData, String> inventory_col_date;

    @FXML
    private TableColumn<productData, String> inventory_col_price;

    @FXML
    private TableColumn<productData, String> inventory_col_productID;

    @FXML
    private TableColumn<productData, String> inventory_col_productName;

    @FXML
    private TableColumn<productData, String> inventory_col_status;

    @FXML
    private TableColumn<productData, String> inventory_col_stock;

    @FXML
    private TableColumn<productData, String> inventory_col_type;

    @FXML
    private Button inventory_deleteBtn;

    @FXML
    private AnchorPane inventory_form;

    @FXML
    private ImageView inventory_imageView;

    @FXML
    private Button inventory_importBtn;

    @FXML
    private TextField inventory_price;

    @FXML
    private TextField inventory_productID;

    @FXML
    private TextField inventory_productName;

    @FXML
    private ComboBox<?> inventory_status;

    @FXML
    private TextField inventory_stock;

    @FXML
    private Button inventory_updateBtn;

    @FXML
    private Button logout_btn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button menu_btn;

    @FXML
    private Label username;

    @FXML
    private ComboBox<?> inventory_type;



    private Alert alert;

    private PreparedStatement prepare;
    private Connection connect;
    private Statement statement;
    private ResultSet result;

    public void inventoryAddBtn(){

        if(inventory_productID.getText().isEmpty()
            || inventory_productName.getText().isEmpty()
            || inventory_price.getText().isEmpty()
            || inventory_stock.getText().isEmpty()
            || inventory_type.getSelectionModel().getSelectedItem() == null
            || inventory_status.getSelectionModel().getSelectedItem() == null
            || data.path == null ) {

            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter all the details in the fields in the form");
            alert.showAndWait();

        }else{

            //CHECK PRODUCT ID
            String checkProdID = "SELECT prod_id FROM product WHERE prod_id= '"
                    + inventory_productID.getText() + "'";

            connect = database.connectDB();

            try{

                statement = connect.createStatement();
                result = statement.executeQuery(checkProdID);

                if(result.next()){
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText(inventory_productID.getText() + "Product ID already exists");
                    alert.showAndWait();
                }else{
                    String insertData = "INSERT INTO product (prod_id, prod_name, type, stock, price, status, image, date) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    prepare = connect.prepareStatement(insertData, Statement.RETURN_GENERATED_KEYS);
                    prepare.setString(1, inventory_productID.getText());
                    prepare.setString(2, inventory_productName.getText());
                    prepare.setString(3, (String) inventory_type.getSelectionModel().getSelectedItem());
                    prepare.setString(4, inventory_stock.getText());
                    prepare.setString(5, inventory_price.getText());
                    prepare.setString(6, (String) inventory_status.getSelectionModel().getSelectedItem().toString());

                    String path = data.path;
                    path = path.replace("\\", "\\\\");
                    prepare.setString(7, path);

                    // Get current date
                    java.util.Date currentDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
                    prepare.setDate(8, sqlDate);

                    // Execute the query
                    prepare.executeUpdate();

                    // Show success message
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product added successfully");
                    alert.showAndWait();

                    // Refresh the table data
                    inventoryShowData();
                    inventoryClearBtn();

                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public void inventoryClearBtn(){
        inventory_productID.setText("");
        inventory_productName.setText("");
        inventory_price.setText("");
        inventory_stock.setText("");
        inventory_type.getSelectionModel().clearSelection();
        inventory_status.getSelectionModel().clearSelection();
        data.path = " ";
        inventory_imageView.setImage(null);

    }

    //LETS MAKE A BEHAVIOR FOR IMPORT BTN FIRST

    public void inventoryImportBtn() {
        try {
            FileChooser openFile = new FileChooser();
            openFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open Image File", "*.png", "*.jpg"));

            File file = openFile.showOpenDialog(main_form.getScene().getWindow());

            if (file != null) {
                data.path = file.getAbsolutePath();
                Image image = new Image(file.toURI().toString(), 120, 127, false, true);

                inventory_imageView.setImage(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception here, e.g., display an error message to the user
        }
    }

    //MERGE ALL DATA


    public ObservableList<productData> inventoryDataList() {
        ObservableList<productData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM product";

        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                productData prodData = new productData(
                        result.getInt("id"),
                        result.getString("prod_id"),
                        result.getString("prod_name"),
                        result.getInt("stock"),
                        result.getDouble("price"), // Corrected to getDouble for price
                        result.getString("status"),
                        result.getDate("date"),
                        result.getString("image"),
                        result.getString("type")
                );

                listData.add(prodData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listData;
    }

    //TO SHOW DATA ON OUR TABLE

    private ObservableList<productData> inventoryListData;

    public void inventoryShowData() {
        inventoryListData = inventoryDataList();

        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        // Set other columns similarly if they exist

        inventory_tableView.setItems(inventoryListData);
    }



    public String[] typeList = { "Meal", "Drinks"};

    public void inventoryTypeList(){
        List<String> typeL = new ArrayList<String>();

        for(String data: typeList){
            typeL.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(typeL);
        inventory_type.setItems(listData);
    }

    private String[] statusList = {"Available", "Unavailable"};

    public void inventoryStatusList(){

        List<String> statusL = new ArrayList<>();

        for(String data: statusList){
            statusL.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(statusL);
        inventory_status.setItems(listData);

    }

    public void logout() {
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Close the current window
                Stage currentStage = (Stage) logout_btn.getScene().getWindow();
                currentStage.close();

                // Load the login form
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                Parent root = loader.load();

                // Create a new stage for the login form
                Stage stage = new Stage();
                stage.setTitle("Cafe Shop Management System");
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void displayUsername(){

        String user = data.username;
        user = user.substring(0, 1).toUpperCase() + user.substring(1);

        username.setText(user);

    }


    @Override
    public void initialize(URL Location, ResourceBundle resources) {

        displayUsername();
        inventoryTypeList();
        inventoryStatusList();

        inventoryShowData();

    }
}
