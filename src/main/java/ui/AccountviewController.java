package ui;

import bank.*;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AccountviewController {

    @FXML
    private Label accountNameLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private ListView<Transaction> transactionListView;

    @FXML
    private Button backButton;

    private String accountName;
    private PrivateBank bank;
    private ObservableList<Transaction> transactions;


    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public void setBank(PrivateBank bank) {
        this.bank = bank;
    }


    public void loadAccountData() {
        // Account-Name anzeigen
        accountNameLabel.setText("Account: " + accountName);

        // Transaktionen laden
        loadTransactions();

        // Kontostand berechnen und anzeigen
        updateBalance();
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactionList = bank.getTransactions(accountName);
            transactions = FXCollections.observableArrayList(transactionList);
            transactionListView.setItems(transactions);
        } catch (Exception e) {
            showErrorAlert("Fehler beim Laden der Transaktionen: " + e.getMessage());
        }
    }

    private void updateBalance() {
        try {
            double balance = bank.getAccountBalance(accountName);
            balanceLabel.setText(String.format("Kontostand: %.2f EUR", balance));

        } catch (AccountDoesNotExistException e) {
            showErrorAlert("Fehler beim Berechnen des Kontostands: " + e.getMessage());
        }
    }


    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/Mainview.fxml")
            );
            Parent root = loader.load();

            Scene mainScene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(mainScene);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Fehler beim Zurück-Navigieren: " + e.getMessage());
        }
    }




    @FXML
    private void handleSortAscending() {
        try {
            List<Transaction> sorted = bank.getTransactionsSorted(accountName, true);
            transactions.setAll(sorted);
        } catch (Exception e) {
            showErrorAlert("Fehler beim Sortieren: " + e.getMessage());
        }
    }


    @FXML
    private void handleSortDescending() {
        try {
            List<Transaction> sorted = bank.getTransactionsSorted(accountName, false);
            transactions.setAll(sorted);
        } catch (Exception e) {
            showErrorAlert("Fehler beim Sortieren: " + e.getMessage());
        }
    }




    @FXML
    private void handleShowAll() {
        loadTransactions();
    }

    @FXML
    private void handleShowPositive() {
        try {
            List<Transaction> positive = bank.getTransactionsByType(accountName, true);
            transactions.setAll(positive);
        } catch (Exception e) {
            showErrorAlert("Fehler beim Filtern: " + e.getMessage());
        }
    }


    @FXML
    private void handleShowNegative() {
        try {
            List<Transaction> negative = bank.getTransactionsByType(accountName, false);
            transactions.setAll(negative);
        } catch (Exception e) {
            showErrorAlert("Fehler beim Filtern: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTransaction() {
        Transaction selected = transactionListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarningAlert("Bitte wählen Sie eine Transaktion aus!");
            return;
        }

        // Bestätigungsdialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Transaktion löschen");
        confirmAlert.setHeaderText("Bestätigung erforderlich");
        confirmAlert.setContentText(
                "Möchten Sie diese Transaktion wirklich löschen?\n\n" +
                        selected.toString()
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Aus Bank entfernen
                bank.removeTransaction(accountName, selected);

                // Aus ObservableList entfernen
                transactions.remove(selected);

                // Kontostand aktualisieren
                updateBalance();

                showInfoAlert("Transaktion wurde gelöscht.");

            } catch (TransactionDoesNotExistException e) {
                showErrorAlert("Transaktion existiert nicht: " + e.getMessage());
            } catch (Exception e) {
                showErrorAlert("Fehler beim Löschen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddTransaction() {
        showAddTransactionDialog();
    }

    private void showAddTransactionDialog() {
        // Custom Dialog erstellen
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Neue Transaktion");
        dialog.setHeaderText("Transaktion hinzufügen");

        // Buttons
        ButtonType addButtonType = new ButtonType("Hinzufügen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Layout erstellen
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        //abstand zwiech rand und cont
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Typ-Auswahl (Payment oder Transfer)
        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton paymentRadio = new RadioButton("Payment");
        paymentRadio.setToggleGroup(typeGroup);
        paymentRadio.setSelected(true);
        RadioButton transferRadio = new RadioButton("Transfer");
        transferRadio.setToggleGroup(typeGroup);

        // Eingabefelder
        TextField dateField = new TextField();
        dateField.setPromptText("DD.MM.YYYY");
        TextField amountField = new TextField();
        amountField.setPromptText("Betrag (z.B. 100.50)");
        TextField descField = new TextField();
        descField.setPromptText("Beschreibung");

        // Transfer-spezifische Felder
        TextField senderField = new TextField();
        senderField.setPromptText("Sender");
        TextField recipientField = new TextField();
        recipientField.setPromptText("Empfänger");

        // Initial verbergen
        Label senderLabel = new Label("Sender:");
        Label recipientLabel = new Label("Empfänger:");
        senderField.setVisible(false);
        recipientField.setVisible(false);
        senderLabel.setVisible(false);
        recipientLabel.setVisible(false);

        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isTransfer = (newVal == transferRadio);
            senderField.setVisible(isTransfer);
            recipientField.setVisible(isTransfer);
            senderLabel.setVisible(isTransfer);
            recipientLabel.setVisible(isTransfer);
        });
        HBox typeBox = new HBox(10);
        typeBox.getChildren().addAll(paymentRadio, transferRadio);

        // Grid aufbauen - KORRIGIERT!
        int row = 0;
        grid.add(new Label("Typ:"), 0, row);
        grid.add(typeBox, 1, row++);
        grid.add(new Label("Datum:"), 0, row);
        grid.add(dateField, 1, row++);
        grid.add(new Label("Betrag:"), 0, row);
        grid.add(amountField, 1, row++);
        grid.add(new Label("Beschreibung:"), 0, row);
        grid.add(descField, 1, row++);
        grid.add(senderLabel, 0, row);
        grid.add(senderField, 1, row++);
        grid.add(recipientLabel, 0, row);
        grid.add(recipientField, 1, row++);
        dialog.getDialogPane().setContent(grid);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String date = dateField.getText().trim();
                    double amount = Double.parseDouble(amountField.getText());
                    String description = descField.getText().trim();
                    // Validierung
                    if (date.isEmpty() || description.isEmpty()) {
                        showErrorAlert("Alle Felder müssen ausgefüllt sein!");
                        return null;
                    }
                    // Payment oder Transfer?
                    if (typeGroup.getSelectedToggle() == paymentRadio) {
                        // Payment erstellen
                        return new Payment(amount, date, description,
                                bank.getIncomingInterest(),
                                bank.getOutgoingInterest());
                    } else {
                        // Transfer erstellen
                        String sender = senderField.getText().trim();
                        String recipient = recipientField.getText().trim();

                        if (sender.isEmpty() || recipient.isEmpty()) {
                            showErrorAlert("Sender und Empfänger müssen angegeben werden!");
                            return null;
                        }

                        // Validierung: Account-Name muss entweder Sender oder Empfänger sein
                        if (!sender.equals(accountName) && !recipient.equals(accountName)) {
                            showErrorAlert("Bei einem Transfer muss der Account-Name '" + accountName +
                                    "' entweder als Sender oder als Empfänger angegeben werden!");
                            return null;
                        }

                        // Automatisch erkennen: Incoming oder Outgoing?
                        if (recipient.equals(accountName)) {
                            return new IncomingTransfer(amount, date, description,
                                    sender, recipient);
                        } else {
                            return new OutgoingTransfer(amount, date, description,
                                    sender, recipient);
                        }
                    }
                } catch (NumberFormatException e) {
                    showErrorAlert("Ungültiger Betrag! Bitte eine Zahl eingeben.");
                    return null;
                } catch (Exception e) {
                    showErrorAlert("Fehler: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Dialog anzeigen und Ergebnis verarbeiten
        Optional<Transaction> result = dialog.showAndWait();

        result.ifPresent(transaction -> {
            try {
                bank.addTransaction(accountName, transaction);

                // Zu ObservableList hinzufügen
                transactions.add(transaction);

                // Kontostand aktualisieren
                updateBalance();

                showInfoAlert("Transaktion wurde hinzugefügt.");

            } catch (TransactionAlreadyExistException e) {
                showErrorAlert("Transaktion existiert bereits!");
            } catch (Exception e) {
                showErrorAlert("Fehler beim Hinzufügen: " + e.getMessage());
            }
        });
    }


    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText("Ein Fehler ist aufgetreten");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}