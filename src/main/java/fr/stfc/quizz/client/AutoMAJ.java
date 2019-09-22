package fr.stfc.quizz.client;

import fr.colin.stfc.quizzapi.QuizzAPI;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AutoMAJ implements Runnable {

    private QuizzAPI api;

    public AutoMAJ(QuizzAPI api) {
        super();
        this.api = api;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Automatic Update of categories and questions");
            SQSClientController.questions = new ConcurrentHashMap<>(api.getQuestions());
            SQSClientController.categories = new CopyOnWriteArrayList<>(api.getCategories());
            try {
                Thread.sleep(1000 * 60 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
