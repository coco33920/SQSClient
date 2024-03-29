package fr.stfc.quizz.client;

import fr.colin.stfc.quizzapi.QuizzAPI;
import fr.colin.stfc.quizzapi.objects.Quizz;
import fr.colin.stfc.quizzapi.objects.Scores;
import fr.stfc.quizz.client.objects.GraphScale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GraphDataVisualizationController {

    @FXML
    private AnchorPane anchorPane;

    public static void main(String... args) {
        GraphScale m = GraphScale.MONTH;
        try {
            ArrayList<ArrayList<Quizz>> q = QuizzAPI.DEFAULT_INSTANCE.fetchQuizz(m.getFormat().format(new Date(System.currentTimeMillis() - m.getAmplitude())), m.getFormat().format(new Date(System.currentTimeMillis())), m.getInterval());
            System.out.println(q.size());
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

    }

    @FXML
    public void initialize() {
        VBox firstGraphButtons = new VBox();
        firstGraphButtons.setPrefSize(220, 360);
        firstGraphButtons.setLayoutX(0.0);
        firstGraphButtons.setLayoutY(0.0);
        firstGraphButtons.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        firstGraphButtons.setPadding(new Insets(10));
        firstGraphButtons.setSpacing(10);

        Label fL = new Label("Echelle");
        fL.setStyle("-fx-text-fill: #AD722C");
        fL.setWrapText(true);

        Button heure = new Button("Heure");
        Button jour = new Button("Jour");
        Button mois = new Button("Mois");
        Button ans = new Button("An");
        Button back = new Button("Retour");
        HBox h = new HBox();
        h.setAlignment(Pos.BOTTOM_LEFT);
        h.getChildren().add(back);
        firstGraphButtons.getChildren().addAll(fL, heure, jour, mois, ans, h);

        VBox secondGraphButtons = new VBox();
        secondGraphButtons.setPrefSize(220, 359);
        secondGraphButtons.setLayoutX(0.0);
        secondGraphButtons.setLayoutY(361);
        secondGraphButtons.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        secondGraphButtons.setPadding(new Insets(10));
        secondGraphButtons.setSpacing(10);

        Label sL = new Label("Echelle");
        sL.setStyle("-fx-text-fill: #AD722C");
        sL.setWrapText(true);

        Button heures = new Button("Heure");
        Button jours = new Button("Jour");
        Button moiss = new Button("Mois");
        Button anss = new Button("An");


        secondGraphButtons.getChildren().addAll(sL, heures, jours, moiss, anss);

        anchorPane.getChildren().addAll(firstGraphButtons, secondGraphButtons);

        AnchorPane firstGraph = new AnchorPane();
        firstGraph.setPrefSize(1059, 360);
        firstGraph.setLayoutX(221);
        firstGraph.setLayoutY(0);
        firstGraph.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        firstGraph.getChildren().add(constructFirstGraph(GraphScale.HOUR));

        heure.setOnMouseClicked(event -> {
            firstGraph.getChildren().clear();
            firstGraph.getChildren().add(constructFirstGraph(GraphScale.HOUR));
        });
        jour.setOnMouseClicked(event -> {
            firstGraph.getChildren().clear();
            firstGraph.getChildren().add(constructFirstGraph(GraphScale.DAY));
        });
        mois.setOnMouseClicked(event -> {
            firstGraph.getChildren().clear();
            firstGraph.getChildren().add(constructFirstGraph(GraphScale.MONTH));
        });
        ans.setOnMouseClicked(event -> {
            firstGraph.getChildren().clear();
            firstGraph.getChildren().add(constructFirstGraph(GraphScale.YEAR));
        });


        AnchorPane secondGraph = new AnchorPane();
        secondGraph.setPrefSize(1059, 359);
        secondGraph.setLayoutX(221);
        secondGraph.setLayoutY(361);
        secondGraph.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        secondGraph.getChildren().add(constructSecondGraph(GraphScale.HOUR));

        heures.setOnMouseClicked(event -> {
            secondGraph.getChildren().clear();
            secondGraph.getChildren().add(constructSecondGraph(GraphScale.HOUR));
        });
        jours.setOnMouseClicked(event -> {
            secondGraph.getChildren().clear();
            secondGraph.getChildren().add(constructSecondGraph(GraphScale.DAY));
        });
        moiss.setOnMouseClicked(event -> {
            secondGraph.getChildren().clear();
            secondGraph.getChildren().add(constructSecondGraph(GraphScale.MONTH));
        });
        anss.setOnMouseClicked(event -> {
            secondGraph.getChildren().clear();
            secondGraph.getChildren().add(constructSecondGraph(GraphScale.YEAR));
        });

        anchorPane.getChildren().addAll(firstGraph, secondGraph);
    }

    public LineChart<String, Number> constructFirstGraph(GraphScale scale) {
        final CategoryAxis x = new CategoryAxis();
        final NumberAxis y = new NumberAxis();
        x.setLabel(scale.getLabel());
        y.setLabel("Nombre de quizz");
        x.setAnimated(false);
        y.setAnimated(false);

        String date1 = scale.getFormat().format(new Date(System.currentTimeMillis() - scale.getAmplitude()));
        String date2 = scale.getFormat().format(new Date(System.currentTimeMillis()));

        final LineChart<String, Number> lineChart = new LineChart<>(x, y);
        lineChart.setTitle("Nombre de quizzs créés. Date : " + date1.substring(0, date1.length() - 8) + " - " + date2.substring(0, date2.length() - 8));
        lineChart.setAnimated(false);
        lineChart.setPrefSize(1059, 360);
        lineChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        ArrayList<ArrayList<Quizz>> q = QuizzAPI.DEFAULT_INSTANCE.fetchQuizz(date1, date2, scale.getInterval());
        //TODO : Better X values
        long sDate = scale.getFormat().parse(date1, new ParsePosition(0)).getTime();
        for (int i = 0; i < q.size(); i+=scale.getNumber()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(scale.constructLabel(i, sDate), q.get(i).size());
            series.getData().add(data);
        }

        lineChart.getData().add(series);
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            data.getNode().setOnMouseEntered(event -> {
                Tooltip t = new Tooltip("Nombre : " + data.getYValue() + "\nDate : " + data.getXValue());
                Tooltip.install(data.getNode(), t);
            });
        }


        return lineChart;
    }

    public StackedBarChart<String, Number> constructSecondGraph(GraphScale scale) {
        final CategoryAxis x = new CategoryAxis();
        final NumberAxis y = new NumberAxis();
        x.setTickLabelRotation(90);
        String date1 = scale.getFormat().format(new Date(System.currentTimeMillis() - scale.getAmplitude()));
        String date2 = scale.getFormat().format(new Date(System.currentTimeMillis()));
        x.setLabel(scale.getLabel());
        y.setLabel("Meilleur Score");
        x.setAnimated(false);
        y.setAnimated(false);

        long sDate = scale.getFormat().parse(date1, new ParsePosition(0)).getTime();


        final StackedBarChart<String, Number> barChart = new StackedBarChart<>(x, y);
        barChart.setTitle("Meilleur score. Date : " + date1.substring(0, date1.length() - 8) + " - " + date2.substring(0, date2.length() - 8));
        barChart.setAnimated(false);
        barChart.setPrefSize(1059, 359);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series0 = new XYChart.Series<>();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();


        ArrayList<ArrayList<Scores>> scores = QuizzAPI.DEFAULT_INSTANCE.fetchQuizzData(date1, date2, scale.getInterval());


        ArrayList<Scores[]> twoBest = new ArrayList<>();

        for (ArrayList<Scores> s : scores) {
            if (s.isEmpty()) {
                twoBest.add(new Scores[]{new Scores("", 0d, 0L, ""), new Scores("", 0d, 0L, "")});
                continue;
            }
            s.sort((o1, o2) -> (int) (o2.getScore() - o1.getScore()));
            if (s.size() < 2)
                twoBest.add(new Scores[]{s.get(0), new Scores("", 0d, 0L, "")});
            else{
                twoBest.add(new Scores[]{s.get(0), s.get(1)});
            }

        }
        ArrayList<String> cats = new ArrayList<>();
        int s = 0;
        while (s < scores.size()) {
            cats.add(scale.constructLabel(s, sDate));
            s+=scale.getNumber();
        }
        x.setCategories(FXCollections.<String>observableArrayList(cats));

        int i = 0;
        for (Scores[] scoresss : twoBest) {
            XYChart.Data<String, Number> data0 = new XYChart.Data<>(cats.get(i), scoresss[0].getScore());
            data0.setExtraValue(scoresss[0]);
            XYChart.Data<String, Number> data1 = new XYChart.Data<>(cats.get(i), scoresss[1].getScore());
            data1.setExtraValue(scoresss[1]);
            series1.getData().add(data0);
            series0.getData().add(data1);
            i++;
        }

        barChart.getData().add(series0);
        barChart.getData().add(series1);


        for (int sd = 0; sd < series0.getData().size(); sd++) {
            XYChart.Data<String, Number> data = series1.getData().get(sd);
            XYChart.Data<String, Number> data2 = series0.getData().get(sd);
            data.getNode().setOnMouseEntered(event -> {
                Scores score = (Scores) data.getExtraValue();
                Tooltip t = new Tooltip("Meilleur score : " + data.getYValue() + "\nDate : " + data.getXValue() + "\n" + "Par : " + score.getUser());
                Tooltip.install(data.getNode(), t);
            });
            data2.getNode().setOnMouseEntered(event -> {
                Scores score = (Scores) data2.getExtraValue();
                Tooltip t = new Tooltip("Deuxième score : " + data2.getYValue() + "\nDate : " + data2.getXValue() + "\n" + "Par : " + score.getUser());
                Tooltip.install(data2.getNode(), t);
            });
        }

        return barChart;

    }


}
