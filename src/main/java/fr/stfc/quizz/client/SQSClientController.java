package fr.stfc.quizz.client;

import com.jfoenix.controls.*;
import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import fr.colin.stfc.quizzapi.QuizzAPI;
import fr.colin.stfc.quizzapi.objects.Category;
import fr.colin.stfc.quizzapi.objects.CompletedQuizz;
import fr.colin.stfc.quizzapi.objects.Questions;
import fr.colin.stfc.quizzapi.objects.Quizz;
import fr.stfc.quizz.client.objects.QuestionS;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQSClientController {

    @FXML
    public BorderPane pane;

    private JProApplication jProApplication;
    private QuizzAPI api = QuizzAPI.DEFAULT_INSTANCE;

    private static ScrollPane adminQuestionCache;
    private Font DS9 = Font.loadFont(getClass().getResourceAsStream("font/DS9_Credits.ttf"), 15);

    public static CopyOnWriteArrayList<Category> categories;
    public static ConcurrentHashMap<String, ArrayList<Questions>> questions;
    public static ConcurrentHashMap<Category, ScrollPane> removeQuestionCache = new ConcurrentHashMap<>();

    public void init(JProApplication jProApplication) {
        this.jProApplication = jProApplication;
    }

    public HBox constructTop() {
        HBox top = new HBox();
        Button github = new Button("GitHub");
        Button sqs = new Button("SQS");
        top.setPadding(new Insets(10));
        top.setSpacing(10);
        top.getChildren().add(github);
        top.getChildren().add(sqs);


        Button stfc = new Button("STFC");
        Button stfcfb = new Button("Facebook STFC");
        Button dev = new Button("Développeur");

        github.setOnMouseClicked(event -> {
            jProApplication.getHostServices().showDocument("https://github.com/coco33920/SimpleQuizzServer-SQS");
        });
        sqs.setOnMouseClicked(event -> {
            sendPopUp("SQS", "About", "SQS is a simple system to setup your own quizz in the cloud !\nTry it and send your issue on the github page!", Alert.AlertType.INFORMATION);
        });
        stfc.setOnMouseClicked(event -> {
            jProApplication.getHostServices().showDocument("https://star-trek-french-club.fr");
        });
        stfcfb.setOnMouseClicked(event -> {
            jProApplication.getHostServices().showDocument("https://www.facebook.com/StarTrekFrenchClub/");
        });
        dev.setOnMouseClicked(event -> {
            sendPopUp("Développeur", "Informations", "Développé par Colin THOMAS\nGitHub : https://github.com/coco33920 & https://github.com/uss-versailles\n", Alert.AlertType.INFORMATION);
        });

        stfc.setPrefSize(200, 20);
        stfcfb.setPrefSize(200, 20);
        dev.setPrefSize(200, 20);
        top.getChildren().addAll(stfc, stfcfb, dev);


        github.setPrefSize(100, 20);
        sqs.setPrefSize(100, 20);
        return top;
    }

    public HBox constructBottom() {
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10));
        bottom.setSpacing(10);
        Button admin = new Button("Admin");
        Button soon = new Button("Soon");
        Button answer = new Button("Réponses");
        soon.setDisable(true);
        answer.setDisable(true);

        admin.setPrefSize(100, 20);
        soon.setPrefSize(100, 20);
        answer.setPrefSize(100, 20);
        bottom.getChildren().add(admin);
        bottom.getChildren().add(soon);
        bottom.getChildren().add(answer);
        pane.setPadding(new Insets(20));

        admin.setOnMouseClicked(event -> {
                    initializeAdminLook();
                }
        );

        return bottom;
    }


    @FXML
    public void initialize() {
        //Initialize

        categories = new CopyOnWriteArrayList<>(api.getCategories());
        questions = new ConcurrentHashMap<>(api.getQuestions());
        initializeBaseLook();
    }

    public void initializePostAnsweredLook(CompletedQuizz quizz) {
        pane.getChildren().clear();
        pane.setTop(constructTop());

        System.out.println(quizz.getAnswers().size());

        ArrayList<JFXCheckBox> jfxCheckBoxes = new ArrayList<>();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(10));
        Background background = new Background(new BackgroundFill(Color.web("#143F48"), CornerRadii.EMPTY, Insets.EMPTY));
        scrollPane.setBackground(background);
        scrollPane.setBorder(new Border(new BorderStroke(Color.web("#5B1414"), BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(background);
        vBox.setSpacing(40);
        vBox.setPadding(new Insets(10));

        for (int i = 0; i < quizz.getQuizz().getQuestions().size(); i++) {
            VBox v = new VBox();
            v.setBackground(background);
            v.setBorder(new Border(new BorderStroke(Color.web("#AD722C"), BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))));
            Label label = new Label("Question #" + i);
            label.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
            Label gavedAnswer = new Label("Réponse donnée : " + quizz.getAnswers().get(i));
            gavedAnswer.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");

            Label answer = new Label("Réponse attendue : " + quizz.getQuizz().getQuestions().get(i).getAnswer());
            answer.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
            JFXCheckBox checkBox = new JFXCheckBox();
            checkBox.setText("Vrai/Faux ?");
            checkBox.setCheckedColor(Color.web("5B1414"));
            checkBox.setTextAlignment(TextAlignment.LEFT);
            v.getChildren().addAll(label, gavedAnswer, answer, checkBox);
            jfxCheckBoxes.add(checkBox);

            vBox.getChildren().addAll(v);
        }
        JFXButton valider = new JFXButton("Valider");
        vBox.getChildren().addAll(valider);
        scrollPane.setContent(vBox);
        pane.setCenter(scrollPane);

        valider.setOnMouseClicked(event -> {
            int allQuestions = jfxCheckBoxes.size();
            int trueQuestion = 0;
            ArrayList<Questions> q = quizz.getQuizz().getQuestions();
            int i = 0;
            for (JFXCheckBox box : jfxCheckBoxes) {
                if (box.isSelected()) {
                    trueQuestion++;
                    Questions qs = q.get(i);
                    q.remove(i);
                    Questions sd = new Questions(qs.getUuid(), qs.getTitle(), qs.getContent(), "Bonne Réponse ! +1 Point", qs.getCategory_uuid());
                    q.add(i, sd);

                }
                i++;
            }
            quizz.corrected = true;
            pane.getChildren().clear();
            pane.setTop(constructTop());
            pane.setBottom(constructBottom());
            VBox vBoxs = new VBox();
            Label label = new Label("La correction est terminée !\nVous pouvez maintenant la recevoir par mail!");
            label.setStyle("-fx-font-family: Trek; -fx-font-size: 20; -fx-text-fill: #5B1414");
            label.setTextFill(Color.web("#5B1414"));
            label.setAlignment(Pos.CENTER);
            vBoxs.setPadding(new Insets(10));
            vBoxs.setSpacing(30);
            HBox decision = new HBox();
            decision.setSpacing(10);
            Button email = new Button("Email");
            Button retour = new Button("Retour");
            email.setWrapText(true);
            decision.getChildren().addAll(email, retour);
            JFXTextField mail = new JFXTextField();
            vBoxs.getChildren().addAll(label, mail, decision);
            pane.setCenter(vBoxs);
            quizz.setScore(trueQuestion);
            email.setOnMouseClicked(event1 -> {
                email.setDisable(true);
                String emails = mail.getText();
                if (emails.isEmpty()) {
                    sendPopUp("Mail", "Mail", "Vous devez spécifier une adresse email valide", Alert.AlertType.INFORMATION);
                    return;
                }
                api.sendCompletedQuizz(emails, quizz);
                sendPopUp("Mail", "Score", "Votre score a été enregistré et votre feuille vous a été envoyée par mail.", Alert.AlertType.INFORMATION);
                initializeBaseLook();
                //send mail to server to be send
            });
            int sd = trueQuestion;
            retour.setOnMouseClicked(event1 -> {
                sendPopUp("Résultats", "Résultas", "Vous avez obtenu un score de " + sd + " sur " + allQuestions, Alert.AlertType.INFORMATION);
                initializeBaseLook();
            });
        });

        pane.setBottom(constructBottom());
    }

    public void initializePostPlayLook(Category theme, int noq) {
        pane.getChildren().clear();

        ArrayList<JFXTextField> textFields = new ArrayList<>();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        Quizz quizz = api.getRandomQuizz(theme.getUuid(), noq);
        scrollPane.setPadding(new Insets(10));
        Background background = new Background(new BackgroundFill(Color.web("#143F48"), CornerRadii.EMPTY, Insets.EMPTY));
        scrollPane.setBackground(background);
        scrollPane.setBorder(new Border(new BorderStroke(Color.web("#5B1414"), BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
        int i = 0;
        VBox vBoxs = new VBox();
        vBoxs.setAlignment(Pos.CENTER);
        vBoxs.setBackground(background);
        vBoxs.setSpacing(40);
        vBoxs.setPadding(new Insets(10));
        System.out.println(quizz.getQuestions().size());
        for (Questions questions : quizz.getQuestions()) {
            i++;
            Label question = new Label("Question #" + i + " " + questions.getTitle());
            Label content = new Label(questions.getContent());
            question.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
            content.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
            JFXTextField answer = new JFXTextField("Répondre ici");
            textFields.add(answer);
            VBox questioni = new VBox();
            questioni.setBackground(background);
            questioni.setBorder(new Border(new BorderStroke(Color.web("#AD722C"), BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))));
            questioni.setAlignment(Pos.CENTER);
            questioni.setSpacing(10);
            questioni.getChildren().addAll(question, content, answer);
            vBoxs.getChildren().add(questioni);
        }
        JFXButton valider = new JFXButton("Valider");
        vBoxs.getChildren().add(valider);
        scrollPane.setContent(vBoxs);
        pane.setCenter(scrollPane);
        pane.setTop(constructTop());
        pane.setBottom(constructBottom());

        valider.setOnMouseClicked(event -> {
            ArrayList<String> answeredAnswers = new ArrayList<>();
            for (JFXTextField j : textFields) {
                answeredAnswers.add(j.getText());
            }
            System.out.println(answeredAnswers.size());
            CompletedQuizz completedQuizz = new CompletedQuizz(quizz, answeredAnswers);
            pane.getChildren().clear();
            pane.setTop(constructTop());
            pane.setBottom(constructBottom());
            VBox vBox = new VBox();
            Label label = new Label("Le quizz est terminé !\nVous pouvez recevoir la version corrigée par mail ou donner la tablette à un opérateur pour qu'il vous le corrige !");
            label.setStyle("-fx-font-family: Trek; -fx-font-size: 20");
            label.setTextFill(Color.web("#5B1414"));
            label.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(10));
            vBox.setSpacing(30);
            HBox decision = new HBox();
            decision.setSpacing(10);
            Button email = new Button("Email");
            Button correction = new Button("Correction");
            email.setWrapText(true);
            correction.setWrapText(true);
            decision.getChildren().addAll(email, correction);
            JFXTextField mail = new JFXTextField();
            vBox.getChildren().addAll(label, mail, decision);
            pane.setCenter(vBox);

            correction.setOnMouseClicked(event1 -> {
                initializePostAnsweredLook(completedQuizz);
                ;
            });
            email.setOnMouseClicked(event1 -> {
                String emails = mail.getText();
                email.setDisable(true);
                if (emails.isEmpty()) {
                    sendPopUp("Mail", "Mail", "Vous devez spécifier une adresse email valide", Alert.AlertType.INFORMATION);
                    return;
                }
                api.sendCompletedQuizz(emails, completedQuizz);
                sendPopUp("Mail", "Score", "Votre feuille vous a été envoyée par mail.", Alert.AlertType.INFORMATION);
                initializeBaseLook();
                //send mail to server to be send
            });
        });
    }

    public void initializePlayLook() {
        pane.getChildren().clear();
        //DRAWER Pour les catégories + Ligne pour le nombre de questions + Boutons pour
        VBox vBox = new VBox();
        vBox.setSpacing(40);
        Label labelS = new Label("Thème :");
        labelS.setTextFill(Color.WHITE);
        labelS.setAlignment(Pos.CENTER);
        JFXComboBox<CategoriesLabel> categoryComboBox = new JFXComboBox<>();
        categories.forEach(category -> categoryComboBox.getItems().add(new CategoriesLabel(category)));
        categoryComboBox.setValue(categoryComboBox.getItems().get(0));
        Label label = new Label("Nombre de questions :");
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        JFXSlider slider = new JFXSlider();
        slider.setMax(20.0);
        slider.setMin(3.0);
        slider.setIndicatorPosition(JFXSlider.IndicatorPosition.LEFT);
        Button submit = new Button("Jouer !");
        submit.setOnMouseClicked(event -> {
            submit.setDisable(true);
            sendPopUp("Quizz", "Construction", "Votre quizz est entrain d'être fabriqué !", Alert.AlertType.INFORMATION, false);
            Category theme = categoryComboBox.getValue().getCategory();
            int noq = (int) Math.floor(slider.getValue());
            initializePostPlayLook(theme, noq);
            //pane.getChildren().clear();
            //start
        });
        vBox.getChildren().addAll(labelS, categoryComboBox, label, slider, submit);
        pane.setTop(constructTop());
        pane.setCenter(vBox);
        pane.setBottom(constructBottom());
    }

    public void initializeAdminLook() {
        pane.getChildren().clear();
        pane.setTop(constructTop());
        pane.setBottom(constructBottom());

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        Label label = new Label("Mot de passe admin :");
        label.setStyle("-fx-font-family: Trek; -fx-font-size: 15; -fx-text-fill: #5B1414");
        label.setWrapText(true);
        JFXPasswordField passwordField = new JFXPasswordField();
        JFXButton verf = new JFXButton("Valider");
        verf.setOnMouseClicked(event -> {
            String token = passwordField.getText();
            if (!api.checkToken(token)) {
                sendPopUp("Error", "Token", "Error : Token Invalid", Alert.AlertType.ERROR);
                return;
            }
            initializePostAdminLook(token);
        });
        vBox.getChildren().addAll(label, passwordField, verf);

        pane.setCenter(vBox);

    }

    public void initializePostAdminLook(String token) {
        pane.getChildren().clear();
        pane.setTop(constructTop());
        pane.setBottom(constructBottom());
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        HBox middle = new HBox();
        VBox cat = new VBox();
        VBox question = new VBox();
        middle.setSpacing(40);
        middle.setPadding(new Insets(20));
        cat.setPadding(new Insets(10));
        cat.setSpacing(10);
        question.setPadding(new Insets(10));
        question.setSpacing(10);

        Label l = new Label("Catégorie :");
        l.setStyle("-fx-font-family: Trek; -fx-font-size: 15; -fx-text-fill: #5B1414");
        JFXTextField ls = new JFXTextField("Nom");
        JFXButton valid = new JFXButton("Valider");


        Label labelQ = new Label("Question :");
        Separator s = new Separator();
        s.setOrientation(Orientation.HORIZONTAL);
        Label labelS = new Label("Thème :");
        labelS.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
        labelQ.setStyle("-fx-font-family: Trek; -fx-font-size: 15; -fx-text-fill: #5B1414");

        JFXComboBox<CategoriesLabel> categoryComboBox = new JFXComboBox<>();
        categories.forEach(category -> categoryComboBox.getItems().add(new CategoriesLabel(category)));
        categoryComboBox.setValue(categoryComboBox.getItems().get(0));

        JFXTextField name = new JFXTextField("Titre");
        JFXTextArea content = new JFXTextArea("Question");
        JFXTextArea answer = new JFXTextArea("Réponse");
        JFXButton valide = new JFXButton("Valider");


        valid.setOnMouseClicked(event -> {
                    valid.setDisable(true);
                    valide.setDisable(true);
                    String names = ls.getText();
                    String answesr = api.addCategory(names, token);
                    if (answesr.contains("Success")) {
                        sendPopUp("Categorie", "Ajout", "La catégorie spécifiée a bien été ajoutée", Alert.AlertType.INFORMATION);
                        categories = new CopyOnWriteArrayList<>(api.getCategories());
                    } else {
                        sendPopUp("Catégorie", "Ajout", "Erreur lors de l'ajout : " + answesr, Alert.AlertType.ERROR);
                    }
                    valid.setDisable(false);
                    valide.setDisable(false);
                    ls.setText("Nom");
                }
        );

        valide.setOnMouseClicked(event -> {
            valid.setDisable(true);
            valide.setDisable(true);
            String title = name.getText();
            String contents = content.getText();
            String answers = answer.getText();
            Category category = categoryComboBox.getValue().getCategory();
            Questions q = new Questions("", title, contents, answers, category.getUuid());
            String answesr = api.addQuestion(q, token);
            if (answesr.contains("Success")) {
                sendPopUp("Categorie", "Ajout", "La question spécifiée a bien été ajoutée", Alert.AlertType.INFORMATION);
                questions = new ConcurrentHashMap<>(api.getQuestions());
            } else {
                sendPopUp("Catégorie", "Ajout", "Erreur lors de l'ajout : " + answesr, Alert.AlertType.ERROR);
            }
            valid.setDisable(false);
            valide.setDisable(false);
            name.setText("Titre");
            content.setText("Question");
            answer.setText("Réponse");

        });

        cat.getChildren().addAll(l, ls, valid);
        question.getChildren().addAll(labelQ, labelS, categoryComboBox, name, content, answer, valide);
        middle.getChildren().addAll(cat, question);
        cat.setFillWidth(true);
        question.setFillWidth(true);
        pane.setCenter(middle);

        JFXButton button = new JFXButton("Ajout de question par 20");
        button.setWrapText(true);
        JFXButton remove = new JFXButton("Supprimer des catégories");
        JFXButton removeS = new JFXButton("Supprimer des questions");
        JFXButton back = new JFXButton("Retour");
        VBox sideBar = new VBox();
        sideBar.setSpacing(10);
        sideBar.setPadding(new Insets(20));
        sideBar.setAlignment(Pos.TOP_LEFT);

        back.setOnMouseClicked(event -> {
            initializeBaseLook();
        });

        button.setOnMouseClicked(event -> {
            if (adminQuestionCache == null) {
                sendPopUp("Quizz", "Ajout", "La page est entrain d'être fabriquée", Alert.AlertType.INFORMATION, false);
            }
            initializeBulkQuestion(token);
        });

        remove.setOnMouseClicked(event -> {
            initializeRemoveCategories(token);
        });

        removeS.setOnMouseClicked(event -> {
            if (event.getClickCount() > 2) {
                removeQuestionCache.clear();
            }
            intializeQuestionDelete(token);
        });

        sideBar.getChildren().addAll(button, removeS, remove, back);


        pane.setRight(sideBar);
    }

    public void initializeRemoveCategories(String token) {
        pane.getChildren().clear();
        pane.setTop(constructTop());
        pane.setBottom(constructBottom());
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        Background background = new Background(new BackgroundFill(Color.web("#143F48"), CornerRadii.EMPTY, Insets.EMPTY));
        HashMap<JFXCheckBox, String> checks = new HashMap<>();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBorder(new Border(new BorderStroke(Color.web("#5B1414"), BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
        scrollPane.setBackground(background);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox scrollPaneContent = new VBox();
        scrollPaneContent.setBackground(background);
        scrollPaneContent.setPadding(new Insets(20));
        scrollPaneContent.setSpacing(40);

        for (Category c : categories) {
            HBox child = new HBox();
            child.setBackground(background);
            child.setBorder(new Border(new BorderStroke(Color.web("#AD722C"), BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))));
            child.setPadding(new Insets(10));
            child.setSpacing(10);

            Label name = new Label(c.getName());
            Label uuid = new Label(c.getUuid());
            Label noq = new Label("Questions : " + questions.get(c.getUuid()).size());
            name.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
            uuid.setStyle("-fx-font-family: Trek; -fx-font-size: 7; -fx-text-fill: #5B1414");
            noq.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
            JFXCheckBox suppr = new JFXCheckBox("Supprimer");
            suppr.setTextFill(Color.web("#5B1414"));
            suppr.setCheckedColor(Color.web("#5B1414"));
            child.getChildren().addAll(name, uuid, noq, suppr);
            checks.put(suppr, c.getUuid());
            scrollPaneContent.getChildren().addAll(child);
        }
        JFXCheckBox suppra = new JFXCheckBox("Supprimer tous");
        suppra.setCheckedColor(Color.web("#AD722C"));
        suppra.setTextFill(Color.web("#AD722C"));
        suppra.setOnMouseClicked(event -> {
            checks.keySet().forEach(JFXCheckBox::fire);
        });
        Button valider = new Button("Valider");

        valider.setOnMouseClicked(event -> {
            Optional<ButtonType> s = sendPopUp("Categories", "Categories", "Les catégories sélectionnées vont être supprimés ainsi que leurs question en êtes vous sûr ?", Alert.AlertType.CONFIRMATION);
            if (s.isPresent() && s.get().equals(ButtonType.CANCEL)) {
                initializePostAdminLook(token);
                return;
            }
            valider.setDisable(true);
            suppra.setDisable(true);
            checks.keySet().forEach(checkBox -> checkBox.setDisable(true));
            ArrayList<String> toRemove = new ArrayList<>();
            for (JFXCheckBox checkBox : checks.keySet()) {
                if (checkBox.isSelected()) {
                    toRemove.add(checks.get(checkBox));
                    checkBox.fire();
                }
            }
            String answesr = api.removeCategoriesBulk(toRemove, token);
            if (answesr.contains("Success")) {
                sendPopUp("Categorie", "Ajout", "Les catégories ont été supprimés", Alert.AlertType.INFORMATION);
            } else {
                sendPopUp("Catégorie", "Ajout", "Erreur lors de l'ajout : " + answesr, Alert.AlertType.ERROR);
            }
            categories = new CopyOnWriteArrayList<>(api.getCategories());
            questions = new ConcurrentHashMap<>(api.getQuestions());
            initializePostAdminLook(token);
        });

        scrollPaneContent.getChildren().addAll(suppra, valider);
        scrollPane.setContent(scrollPaneContent);
        pane.setCenter(scrollPane);


        JFXButton button = new JFXButton("Ajout de questions par 20");
        button.setWrapText(true);
        JFXButton remove = new JFXButton("Supprimer des catégories");
        remove.setDisable(true);
        JFXButton removeS = new JFXButton("Supprimer des questions");
        JFXButton back = new JFXButton("Retour");
        VBox sideBar = new VBox();
        sideBar.setSpacing(10);
        sideBar.setPadding(new Insets(20));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.getChildren().addAll(button, removeS, remove, back);
        back.setOnMouseClicked(event -> {
            initializePostAdminLook(token);
        });
        button.setOnMouseClicked(event -> {
            if (adminQuestionCache == null) {
                sendPopUp("Quizz", "Ajout", "La page est entrain d'être fabriquée", Alert.AlertType.INFORMATION, false);
            }
            initializeBulkQuestion(token);
        });
        remove.setOnMouseClicked(event -> {
            initializeRemoveCategories(token);
        });
        removeS.setOnMouseClicked(event -> {
            if (event.getClickCount() > 2) {
                removeQuestionCache.clear();
            }
            intializeQuestionDelete(token);
        });
        pane.setRight(sideBar);
    }

    public void intializeQuestionDelete(String token) {
        pane.getChildren().clear();
        HBox top = constructTop();
        Background background = new Background(new BackgroundFill(Color.web("#143F48"), CornerRadii.EMPTY, Insets.EMPTY));
        JFXComboBox<CategoriesLabel> categoryComboBox = new JFXComboBox<>();
        categories.forEach(category -> categoryComboBox.getItems().add(new CategoriesLabel(category)));
        categoryComboBox.setValue(categoryComboBox.getItems().get(0));
        top.getChildren().addAll(categoryComboBox);
        pane.setTop(top);
        pane.setBottom(constructBottom());
        JFXButton button = new JFXButton("Ajout de questions par 20");
        button.setWrapText(true);
        JFXButton remove = new JFXButton("Supprimer des catégories");
        JFXButton removeS = new JFXButton("Supprimer des questions");
        removeS.setDisable(true);
        JFXButton back = new JFXButton("Retour");
        VBox sideBar = new VBox();
        sideBar.setSpacing(10);
        sideBar.setPadding(new Insets(20));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.getChildren().addAll(button, removeS, remove, back);
        back.setOnMouseClicked(event -> {
            initializePostAdminLook(token);
        });
        remove.setOnMouseClicked(event -> {
            initializeRemoveCategories(token);
        });
        button.setOnMouseClicked(event -> {
            initializePostAdminLook(token);
        });
        pane.setRight(sideBar);
        categoryComboBox.setValue(categoryComboBox.getItems().get(0));
        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (removeQuestionCache.containsKey(newValue.getCategory())) {
                pane.setCenter(removeQuestionCache.get(newValue.getCategory()));
            } else {
                //COMPUTE AND CACHE
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setBorder(new Border(new BorderStroke(Color.web("#5B1414"), BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
                scrollPane.setBackground(background);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                VBox scrollPaneContent = new VBox();
                HashMap<JFXCheckBox, String> checkBoxs = new HashMap<>();
                scrollPaneContent.setBackground(background);
                scrollPaneContent.setPadding(new Insets(20));
                scrollPaneContent.setSpacing(40);
                Category c = newValue.getCategory();
                ArrayList<Questions> s = SQSClientController.questions.get(c.getUuid());
                int i = 0;
                for (Questions qs : s) {
                    VBox child = new VBox();
                    child.setBackground(background);
                    child.setBorder(new Border(new BorderStroke(Color.web("#AD722C"), BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))));
                    child.setPadding(new Insets(10));
                    child.setSpacing(10);

                    Label q1 = new Label("Question " + i + " Titre " + qs.getTitle());
                    Label q2 = new Label("UUID " + qs.getUuid());
                    Label q3 = new Label(qs.getContent());

                    q1.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
                    q2.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
                    q3.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");

                    JFXCheckBox suppr = new JFXCheckBox("Supprimer");
                    suppr.setTextFill(Color.web("#5B1414"));
                    suppr.setCheckedColor(Color.web("#5B1414"));

                    child.getChildren().addAll(q1, q2, q3, suppr);
                    checkBoxs.put(suppr, qs.getUuid());
                    scrollPaneContent.getChildren().add(child);
                }
                JFXCheckBox suppra = new JFXCheckBox("Supprimer tous");
                suppra.setCheckedColor(Color.web("#AD722C"));
                suppra.setTextFill(Color.web("#AD722C"));
                suppra.setOnMouseClicked(event -> {
                    checkBoxs.keySet().forEach(JFXCheckBox::fire);
                });
                Button valider = new Button("Valider");

                valider.setOnMouseClicked(event -> {
                    Optional<ButtonType> sd = sendPopUp("Categories", "Categories", "Les questions séléctionnées vont être supprimés, en êtes-vous sûr ?", Alert.AlertType.CONFIRMATION);

                    if (sd.isPresent() && sd.get().equals(ButtonType.CANCEL)) {
                        initializePostAdminLook(token);
                        return;
                    }
                    valider.setDisable(true);
                    suppra.setDisable(true);
                    checkBoxs.keySet().forEach(checkBox -> checkBox.setDisable(true));
                    ArrayList<String> toRemove = new ArrayList<>();
                    for (JFXCheckBox checkBox : checkBoxs.keySet()) {
                        if (checkBox.isSelected()) {
                            toRemove.add(checkBoxs.get(checkBox));
                            checkBox.fire();
                        }
                    }
                    String answesr = api.removeQuestionsBulk(toRemove, token);
                    if (answesr.contains("Success")) {
                        sendPopUp("Categorie", "Ajout", "Les questions ont été supprimés", Alert.AlertType.INFORMATION);
                    } else {
                        sendPopUp("Catégorie", "Ajout", "Erreur lors de l'ajout : " + answesr, Alert.AlertType.ERROR);
                    }
                    categories = new CopyOnWriteArrayList<>(api.getCategories());
                    questions = new ConcurrentHashMap<>(api.getQuestions());
                    removeQuestionCache.remove(c);
                    initializePostAdminLook(token);
                });

                scrollPaneContent.getChildren().addAll(suppra, valider);
                scrollPane.setContent(scrollPaneContent);
                pane.setCenter(scrollPane);
                removeQuestionCache.put(c, scrollPane);

            }
        });
    }

    public void initializeBulkQuestion(String token) {
        pane.getChildren().clear();
        HBox top = constructTop();
        Background background = new Background(new BackgroundFill(Color.web("#143F48"), CornerRadii.EMPTY, Insets.EMPTY));
        JFXComboBox<CategoriesLabel> categoryComboBox = new JFXComboBox<>();
        categories.forEach(category -> categoryComboBox.getItems().add(new CategoriesLabel(category)));
        categoryComboBox.setValue(categoryComboBox.getItems().get(0));
        top.getChildren().addAll(categoryComboBox);
        ArrayList<QuestionS> allQuestionsFields = new ArrayList<>();
        ArrayList<Questions> allQuestions = new ArrayList<>();
        pane.setTop(top);
        pane.setBottom(constructBottom());
        if (adminQuestionCache == null) {

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setBorder(new Border(new BorderStroke(Color.web("#5B1414"), BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
            scrollPane.setBackground(background);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            VBox scrollPaneContent = new VBox();
            scrollPaneContent.setBackground(background);
            scrollPaneContent.setPadding(new Insets(20));
            scrollPaneContent.setSpacing(40);


            for (int i = 0; i < 20; i++) {
                VBox child = new VBox();
                child.setBackground(background);
                child.setBorder(new Border(new BorderStroke(Color.web("#AD722C"), BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))));
                child.setPadding(new Insets(10));
                child.setSpacing(10);
                Label sdf = new Label("Question " + i);
                sdf.setStyle("-fx-font-family: Trek; -fx-font-size: 12; -fx-text-fill: #5B1414");
                JFXTextField title = new JFXTextField("Titre");
                JFXTextArea content = new JFXTextArea("Question");
                JFXTextArea answer = new JFXTextArea("Réponse");
                child.getChildren().addAll(sdf, title, content, answer);
                scrollPaneContent.getChildren().add(child);
                allQuestionsFields.add(new QuestionS(title, content, answer));
            }


            JFXButton valider = new JFXButton("Valider");
            valider.setOnMouseClicked(event -> {
                String catId = categoryComboBox.getValue().getCategory().getUuid();
                valider.setDisable(true);
                for (QuestionS questionS : allQuestionsFields) {
                    Questions sdf = questionS.transformToQuestion(catId);
                    if (!(sdf == null)) {
                        allQuestions.add(sdf);
                    }
                }
                String answesr = api.addQuestionBulk(allQuestions, token);
                if (answesr.contains("Success")) {
                    sendPopUp("Categorie", "Ajout", "La question spécifiée a bien été ajoutée", Alert.AlertType.INFORMATION);
                    questions = new ConcurrentHashMap<>(api.getQuestions());
                } else {
                    sendPopUp("Catégorie", "Ajout", "Erreur lors de l'ajout : " + answesr, Alert.AlertType.ERROR);
                }
                valider.setDisable(false);
            });


            scrollPaneContent.getChildren().addAll(valider);
            scrollPane.setContent(scrollPaneContent);
            pane.setCenter(scrollPane);
            adminQuestionCache = scrollPane;
        } else {
            pane.setCenter(adminQuestionCache);
        }
        JFXButton button = new JFXButton("Ajout de question/catégories à l'unité");
        button.setWrapText(true);
        JFXButton remove = new JFXButton("Supprimer des catégories");
        JFXButton removeS = new JFXButton("Supprimer des questions");
        JFXButton back = new JFXButton("Retour");
        VBox sideBar = new VBox();
        sideBar.setSpacing(10);
        sideBar.setPadding(new Insets(20));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.getChildren().addAll(button, removeS, remove, back);
        back.setOnMouseClicked(event -> {
            initializePostAdminLook(token);
        });
        remove.setOnMouseClicked(event -> {
            initializeRemoveCategories(token);
        });
        button.setOnMouseClicked(event -> {
            initializePostAdminLook(token);
        });
        removeS.setOnMouseClicked(event -> {
            if (event.getClickCount() > 2) {
                removeQuestionCache.clear();
            }
            intializeQuestionDelete(token);
        });
        pane.setRight(sideBar);

    }

    public void initializeBaseLook() {
        pane.getChildren().clear();


        Button play = new Button("Jouer !");
        play.setPrefSize(200, 40);
        play.setWrapText(true);
        play.setOnMouseClicked(event -> {
            initializePlayLook();
        });

        pane.setCenter(play);
        pane.setTop(constructTop());
        pane.setBottom(constructBottom());

        //  play.setFont(DS9);
    }

    public Optional<ButtonType> sendPopUp(String title, String header, String message, Alert.AlertType type) {
        return sendPopUp(title, header, message, type, true);
    }

    public Optional<ButtonType> sendPopUp(String title, String header, String message, Alert.AlertType type, boolean wait) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Label label = new Label(message);
        label.setWrapText(true);
        alert.getDialogPane().setContent(label);

        if (WebAPI.isBrowser()) {
            jProApplication.getWebAPI().executeScript("alert('" + message.replace("\n", "\\n") + "') ");
        } else {
            if (wait) {
                return alert.showAndWait();
            } else {
                alert.show();
            }
        }
        return Optional.empty();
    }


    public JProApplication getjProApplication() {
        return jProApplication;
    }
}
