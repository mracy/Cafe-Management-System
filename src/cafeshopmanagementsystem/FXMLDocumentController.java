package cafeshopmanagementsystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField fp_answer;

    @FXML
    private Button fp_back;

    @FXML
    private Button fp_proceedBtn;

    @FXML
    private ComboBox<String> fp_question; // Changed ComboBox<?> to ComboBox<String>

    @FXML
    private AnchorPane fp_questionForm;

    @FXML
    private AnchorPane np_NewPassForm;

    @FXML
    private Button np_backBtn;

    @FXML
    private Button np_changePassBtn;

    @FXML
    private PasswordField np_confirmPassword;

    @FXML
    private PasswordField np_newPassword;

    @FXML
    private Hyperlink si_forgotPass;

    @FXML
    private Button si_loginBtn;

    @FXML
    private AnchorPane si_loginForm;

    @FXML
    private TextField si_username;

    @FXML
    private PasswordField si_password;

    @FXML
    private Button side_CreateBtn;

    @FXML
    private Button side_alreadyHave;

    @FXML
    private AnchorPane side_form;

    @FXML
    private TextField su_answer;

    @FXML
    private PasswordField su_password;

    @FXML
    private ComboBox<String> su_question; // Changed ComboBox<?> to ComboBox<String>

    @FXML
    private Button su_signupBtn;

    @FXML
    private AnchorPane su_signupForm;

    @FXML
    private TextField su_username;

    @FXML
    private TextField fp_username;



    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;


    public void loginBtn() {
        if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Incomplete Information", "Please enter both username and password.");
        } else {
            String selectData = "SELECT username, password FROM employee WHERE username = ? AND password = ?";

            connect = database.connectDB();

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, si_username.getText());
                prepare.setString(2, si_password.getText());

                result = prepare.executeQuery();

                //IF SUCCESSFULLY LOGIN, THEN PROCEED TO ANOTHER FORM WHICH IS OUR MAIN FORM

                if (result.next()) {

                    //TO GET THE USERNAME THAT USER USED

                    data.username = si_username.getText();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Login Successful", "Welcome, " + si_username.getText() + "!");

                    //LINK YOUR <MAIN FORM>
                    Parent root = FXMLLoader.load(getClass().getResource("mainForm.fxml"));

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    stage.setTitle("Cafe Shop Management System");
                    stage.setMinHeight(600);
                    stage.setMinWidth(1100);

                    stage.setScene(scene);
                    stage.show();

                    si_loginBtn.getScene().getWindow().hide();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Login Failed", "Incorrect username or password. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while trying to login. Please try again later.");
            } finally {
                try {
                    if (result != null) result.close();
                    if (prepare != null) prepare.close();
                    if (connect != null) connect.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private final String[] questionList = {"What is your favorite Color?", "What is your favorite food?", "What is your birth date?"};

    public void regBtn() {
        if (su_username.getText().isEmpty() || su_password.getText().isEmpty()
                || su_question.getValue() == null || su_answer.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Incomplete Form", "Please fill in all the required fields.");

        } else {
            String regData = "INSERT INTO employee (username, password, question, answer, date)"
                    + " VALUES(?,?,?,?,?)";
            connect = database.connectDB();
            try {
                String checkUsername = "SELECT username FROM employee WHERE username = ?";
                prepare = connect.prepareStatement(checkUsername);
                prepare.setString(1, su_username.getText());
                result = prepare.executeQuery();

                if (result.next()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Username Unavailable", "The username '" + su_username.getText() + "' is already in use. Please choose a different username.");

                } else if (su_password.getText().length() < 8) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Password", "The password must be at least 8 characters long. Please choose a stronger password.");

                } else {
                    prepare = connect.prepareStatement(regData);
                    prepare.setString(1, su_username.getText());
                    prepare.setString(2, su_password.getText());
                    prepare.setString(3, su_question.getValue());
                    prepare.setString(4, su_answer.getText());
                    prepare.setDate(5, new java.sql.Date(new Date().getTime()));
                    prepare.executeUpdate();
                    showInformationAlert("Information Message", "Successfully registered Account!");
                    clearFields();
                    slideForm();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null) result.close();
                    if (prepare != null) prepare.close();
                    if (connect != null) connect.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showErrorAlert(String title, String content) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInformationAlert(String title, String content) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        su_username.clear();
        su_password.clear();
        su_question.getSelectionModel().clearSelection();
        su_answer.clear();
    }

    private void slideForm() {
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(side_form);
        slider.setToX(0);
        slider.setDuration(Duration.seconds(.5));
        slider.setOnFinished((ActionEvent e) -> {
            side_alreadyHave.setVisible(false);
            side_CreateBtn.setVisible(true);
        });
        slider.play();
    }

    public void regLquestionList() {
        List<String> listQ = new ArrayList<>();
        for (String question : questionList) {
            listQ.add(question);
        }
        ObservableList<String> listData = FXCollections.observableArrayList(listQ);
        su_question.setItems(listData);
    }

    public void switchForgotPass(){

        fp_questionForm.setVisible(true);
        si_loginForm.setVisible(false);

        forgotPassQuestionList();

    }

    public void proceedBtn(){

        if(fp_username.getText().isEmpty() || fp_question.getSelectionModel().getSelectedItem() == null
                || fp_answer.getText().isEmpty()){

            alert = new Alert (Alert.AlertType.ERROR);
            alert.setTitle("Error");

            alert.setHeaderText(null);
            alert.setContentText("Please ensure all required fields are filled in.");

            alert.showAndWait();

        }else {

            String selectData = "SELECT username, question, answer FROM employee WHERE username = ? AND question = ? AND answer = ? ";
            connect = database.connectDB();

            try{

                prepare = connect.prepareStatement(selectData);

                prepare.setString(1, fp_username.getText());
                prepare.setString(2, (String)fp_question.getSelectionModel().getSelectedItem());
                prepare.setString(3, fp_answer.getText());

                result = prepare.executeQuery();

                if(result.next()){

                    np_NewPassForm.setVisible(true);
                    fp_questionForm.setVisible(false);

                }else{
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The provided information is incorrect. Please review and try again.");

                    alert.showAndWait();
                }


            }catch(Exception e){
                e.printStackTrace();
            }



        }


        }

    public void changePassBtn() {
        if (np_newPassword.getText().isEmpty() || np_confirmPassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please ensure all required fields are filled out.");

        } else {
            if (np_newPassword.getText().equals(np_confirmPassword.getText())) {
                try {
                    // Get the current date
                    java.util.Date currentDate = new java.util.Date();

                    // Construct the update query
                    String updatePass = "UPDATE employee SET password = ?, question = ?, answer = ?, date = ? WHERE username = ?";
                    prepare = connect.prepareStatement(updatePass);
                    prepare.setString(1, np_newPassword.getText());
                    prepare.setString(2, fp_question.getSelectionModel().getSelectedItem());
                    prepare.setString(3, fp_answer.getText());
                    prepare.setDate(4, new java.sql.Date(currentDate.getTime()));
                    prepare.setString(5, fp_username.getText());

                    // Execute the update
                    prepare.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Success", null, "Your password has been changed successfully.");

                    // Reset form fields and visibility
                    si_loginForm.setVisible(true);
                    np_NewPassForm.setVisible(false);
                    np_confirmPassword.clear();
                    np_newPassword.clear();
                    fp_question.getSelectionModel().clearSelection();
                    fp_answer.clear();
                    fp_username.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error Message", null, "An error occurred while changing the password. Please try again.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, "Passwords do not match");
            }
        }
    }



    public void forgotPassQuestionList(){

        List<String> listQ = new ArrayList<>();
        for(String data: questionList) {
            listQ.add(data);

        }
        ObservableList listData = FXCollections.observableArrayList(listQ);
        fp_question.setItems(listData);

    }


    public void backToLoginForm(){
        si_loginForm.setVisible(true);
        fp_questionForm.setVisible(false);
    }

    public void backToQuestionForm(){
        fp_questionForm.setVisible(true);
        np_NewPassForm.setVisible(false);
    }


    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();
        if (event.getSource() == side_CreateBtn) {
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_CreateBtn.setVisible(false);

                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);

                np_NewPassForm.setVisible(false);

                regLquestionList();
            });
            slider.play();
        } else if (event.getSource() == side_alreadyHave) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_CreateBtn.setVisible(true);

                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);

                np_NewPassForm.setVisible(false);

            });
            slider.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize
    }
}