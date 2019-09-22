package fr.stfc.quizz.client;

import fr.colin.stfc.quizzapi.objects.Category;
import javafx.scene.control.Label;

public class CategoriesLabel extends Label {

    private Category category;

    public CategoriesLabel(Category cat){
        super(cat.getName());
        this.category=cat;

    }

    public Category getCategory() {
        return category;
    }
}
