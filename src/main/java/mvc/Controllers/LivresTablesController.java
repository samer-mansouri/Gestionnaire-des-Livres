package mvc.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import mvc.Controllers.CRUDLivres.CRUDL;
import mvc.Models.Livre;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LivresTablesController implements Initializable{

        @FXML
        private TableView<Livre> tbData;

        @FXML
        public TableColumn<Livre, Integer> id;

        @FXML
        public TableColumn<Livre, String> titre;

        @FXML
        public TableColumn<Livre, String> auteur;

        @FXML
        public TableColumn<Livre, String> disponnible;

        @FXML
        public TextField searchField;

        @FXML
        public Button boutonSupprimer;

        @FXML
        private TextField tit;

        @FXML
        private TextField au;

        @FXML
        private ComboBox<String> dis;

        @FXML
        private Button boutonMettreAJour;


        private ObservableList<Livre> livres = FXCollections.observableArrayList(fetchData());

    @Override
        public void initialize(URL location, ResourceBundle resources) {

            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            titre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            auteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
            disponnible.setCellValueFactory(new PropertyValueFactory<>("disponnible"));
            //tbData.setItems(livres);
            searchFunction();

    }

    public void searchFunction(){
        FilteredList<Livre> filteredData = new FilteredList<>(livres, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(livre -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (livre.getTitre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return livre.getAuteur().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Livre> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tbData.comparatorProperty());
        tbData.setItems(sortedData);

    }


        private ArrayList<Livre> fetchData() {

            ArrayList<Livre> livresList = new ArrayList<Livre>();

            ArrayList<ArrayList<String>> l = CRUDL.readLivres();
            for (ArrayList<String> strings : l) {
                livresList.add(new Livre(Integer.parseInt(strings.get(0)), strings.get(1), strings.get(2), strings.get(3).equals("Oui")));
            }
            return livresList;
        }

        @FXML
        private void supprimerLivre(){
            int selectedIndex = tbData.getSelectionModel().getSelectedIndex();
            Window owner = boutonSupprimer.getScene().getWindow();
            System.out.println(selectedIndex);
            if(selectedIndex >=0 ){
                Livre l = tbData.getSelectionModel().getSelectedItem();
                if(CRUDL.deleteLivre(l.getId()) == 1){
                    tbData.setEditable(true);
                    livres.remove(selectedIndex);
                    tbData.setItems(livres);
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Succ??ss !", "Le livre a ??t?? supprim?? avec succ??s!");
                } else {
                    AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "??chec !", "Le livre s??lectionn?? n'a pas ??t?? supprim??!");
                }

            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Erreur !", "Aucun livre n'est s??l??ctionn??!");
            }
        }

        @FXML
        public void mettreAJourLivre(ActionEvent event) throws MalformedURLException {
            Window owner = boutonMettreAJour.getScene().getWindow();
            //System.out.println(tbData.getSelectionModel().getSelectedItem().getId());
            if(tit.getText().isEmpty() && au.getText().isEmpty()){
                AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "Erreur !", "Veuillez remplir le fomulaire!");
            } else if(tit.getText().isEmpty()) {
                AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "Erreur !", "Le champ titre est obligatoire!");
            } else if(au.getText().isEmpty()) {
                AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "Erreur !", "Le champ auteur est obligatoire!");
            } else {
                if(CRUDL.updateLivre(new Livre(tbData.getSelectionModel().getSelectedItem().getId(), tit.getText(), au.getText(), dis.getValue().toString().equals("Oui"))) == 1){
                    System.out.println(au.getText());
                    System.out.println(tit.getText());
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Succ??ss !", "Le livre a ??t?? mis ?? jour avec succ??s!");
                    tit.setDisable(true);
                    au.setDisable(true);
                    dis.setDisable(true);
                    boutonMettreAJour.setDisable(true);
                    tit.setText("");
                    au.setText("");
                    livres = FXCollections.observableArrayList(fetchData());
                    tbData.setItems(livres);
                    searchFunction();

                } else {
                    AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "??chec !", "Le livre s??l??ctionn??e n'a pas ??t?? mis ?? jour!");
                }

            }
        }

        @FXML
        public void cliqueIci(MouseEvent event){
            if (event.getClickCount() == 1){
                tit.setText(tbData.getSelectionModel().getSelectedItem().getTitre());
                au.setText(tbData.getSelectionModel().getSelectedItem().getAuteur());
                dis.setValue(tbData.getSelectionModel().getSelectedItem().isDisponnible() ? dis.getValue(): "Non");
                //dis.setValue("Hello");
                boutonMettreAJour.setDisable(false);
                tit.setDisable(false);
                au.setDisable(false);
                dis.setDisable(false);
                System.out.println(tbData.getSelectionModel().getSelectedItem().getId());
            }
        }

}
