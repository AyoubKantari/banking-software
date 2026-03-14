package ui;

import bank.*;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainviewController {

    @FXML
    private ListView<String> accountListView;
    @FXML
    private Button createAccountButton;
    private ObservableList<String> accounts;
    private PrivateBank bank;


    @FXML
    public void initialize() {
        try {

            bank = new PrivateBank("FH-Bank", 0.05, 0.05, "accounts");

            // Accounts laden
            loadAccounts();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Fehler beim Initialisieren der Bank: " + e.getMessage());
        }
    }


    private void loadAccounts() {
        // Alle Account-Namen von der Bank holen
        List<String> accountList = bank.getAllAccounts();

        // ObservableList erstellen und mit ListView verbinden
        accounts = FXCollections.observableArrayList(accountList);
        accountListView.setItems(accounts);
    }


    @FXML
    private void handleCreate() {
        // TextInputDialog für Account-Namen
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neuer Account");
        dialog.setHeaderText("Account erstellen");
        dialog.setContentText("Bitte Account-Namen eingeben:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String accountName = result.get().trim();

            if (accountName.isEmpty()) {
                showErrorAlert("Account-Name darf nicht leer sein!");
                return;
            }

            try {
                // Account in Bank erstellen
                bank.createAccount(accountName);

                // Account zur ObservableList hinzufügen
                accounts.add(accountName);

                showInfoAlert("Account '" + accountName + "' wurde erfolgreich erstellt.");

            } catch (AccountAlreadyExistsException e) {
                showErrorAlert("Account '" + accountName + "' existiert bereits!");
            } catch (IOException e) {
                showErrorAlert("Fehler beim Erstellen des Accounts: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSelect() {

        String selectedAccount = accountListView.getSelectionModel().getSelectedItem();

        if (selectedAccount != null) {
            openAccountView(selectedAccount);
        } else {
            showWarningAlert("Bitte wählen Sie einen Account aus!");
        }
    }
    @FXML
    private void handleDelete() {
        // Ausgewählten Account holen
        String selectedAccount = accountListView.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            showWarningAlert("Bitte wählen Sie einen Account aus!");
            return;
        }

        // Bestätigungsdialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Account löschen");
        confirmAlert.setHeaderText("Bestätigung erforderlich");
        confirmAlert.setContentText(
                "Möchten Sie den Account '" + selectedAccount + "' wirklich löschen?\n\n" +
                        "Alle Transaktionen werden ebenfalls gelöscht!"
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bank.deleteAccount(selectedAccount);

                // Account aus ObservableList entfernen
                accounts.remove(selectedAccount);

                showInfoAlert("Account '" + selectedAccount + "' wurde gelöscht.");

            } catch (AccountDoesNotExistException e) {
                showErrorAlert("Account existiert nicht: " + e.getMessage());
            } catch (IOException e) {
                showErrorAlert("Fehler beim Löschen des Accounts: " + e.getMessage());
            }
        }
    }


    private void openAccountView(String accountName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/Accountview.fxml")
            );
            Parent root = loader.load();

            AccountviewController controller = loader.getController();
            controller.setAccountName(accountName);
            controller.setBank(bank);
            controller.loadAccountData();
            Scene accountScene = new Scene(root);
            Stage stage = (Stage) accountListView.getScene().getWindow();
            stage.setScene(accountScene);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Fehler beim Öffnen der Accountview: " + e.getMessage());
        }
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();}
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText("Ein Fehler ist aufgetreten");
        alert.setContentText(message);
        alert.showAndWait();}
    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
