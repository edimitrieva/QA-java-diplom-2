package org.example.model;

import java.util.List;

public class IngredientsList {
    List<String> ingredients;
    public IngredientsList(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
