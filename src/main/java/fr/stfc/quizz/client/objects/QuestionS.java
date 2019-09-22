package fr.stfc.quizz.client.objects;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import fr.colin.stfc.quizzapi.objects.Questions;

public class QuestionS {

    private JFXTextField title;
    private JFXTextArea content;
    private JFXTextArea answer;


    public QuestionS(JFXTextField title, JFXTextArea content, JFXTextArea answer) {
        this.title = title;
        this.content = content;
        this.answer = answer;
    }

    public Questions transformToQuestion(String category) {
        String titles = title.getText();
        String contents = content.getText();
        String answers = answer.getText();
        title.setText("Titre");
        content.setText("Question");
        answer.setText("Réponse");
        if (titles.equalsIgnoreCase("Titre") || contents.equalsIgnoreCase("Question") || answers.equalsIgnoreCase(("Réponse"))) {
            return null;
        }
        return new Questions("", titles, contents, answers, category);

    }

    public JFXTextArea getAnswer() {
        return answer;
    }

    public JFXTextArea getContent() {
        return content;
    }

    public JFXTextField getTitle() {
        return title;
    }
}
