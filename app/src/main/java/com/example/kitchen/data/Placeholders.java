package com.example.kitchen.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Placeholders {

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
