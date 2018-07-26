package com.example.kitchen.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataPlaceholders {

    public static List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(1, "Apple Pie", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(2, "Orange Marmalade", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(3, "Purple Juice", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(4, "Lemon Cake", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(5, "Cheeseburger", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(6, "Chicken Salad", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(7, "Meatballs", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(8, "Cinnamon Buns", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(9, "Chip Muffins", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(10, "Baked Chicken", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(11, "Chicken Breasts", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(12, "Blackberry Cobbler", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(13, "Beef Skewers", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(14, "Spinach Salad", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(15, "The Captain's Mango Ice Cream", "", 0, 0, 0, "", "", "", "", 0));
        return recipes;
    }

    public static List<Recipe> getNotebook() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(10, "Baked Chicken", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(15, "The Captain's Mango Ice Cream", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(5, "Cheeseburger", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(6, "Chicken Salad", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(9, "Chip Muffins", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(1, "Apple Pie", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(2, "Orange Marmalade", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(3, "Purple Juice", "", 0, 0, 0, "", "", "", "", 0));
        recipes.add(new Recipe(4, "Lemon Cake", "", 0, 0, 0, "", "", "", "", 0));
        return recipes;
    }

    public static List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        groups.add("Breakfast");
        groups.add("Lunch");
        groups.add("Dinner");
        return groups;
    }

    public static HashMap<String, List<String>> getChildren() {
        HashMap<String, List<String>> children = new HashMap<>();

        List<String> breakfast = new ArrayList<>();
        breakfast.add("Eggs");
        breakfast.add("Toast");
        breakfast.add("Tea");

        List<String> lunch = new ArrayList<>();
        lunch.add("Chicken");
        lunch.add("Rice");
        lunch.add("Yogurt");

        List<String> dinner = new ArrayList<>();
        dinner.add("Pasta");
        dinner.add("Salad");
        dinner.add("Soup");
        dinner.add("Apple pie");

        children.put(getGroups().get(0), breakfast);
        children.put(getGroups().get(1), lunch);
        children.put(getGroups().get(2), dinner);

        return children;
    }
}
