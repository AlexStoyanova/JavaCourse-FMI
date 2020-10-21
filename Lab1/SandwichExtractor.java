import java.util.Arrays;

public class SandwichExtractor
{
    public static boolean isOlives(String word)
    {
        return word.equals("olives");
    }

    public static int hasOlives(String[] ingredients)
    {
        int size = ingredients.length;
        int counter = 0;
        for(int i = 0; i < size; ++i)
        {
            if(ingredients[i].equals("olives"))
            {
                counter++;
            }
        }
        return counter;
    }

    public static String[] removeOlives(String[] ingredients, int olives)
    {
        int size = ingredients.length;
        String[] removedOlives = new String[size - olives];

        int j = 0;

        for(int i = 0; i < size; ++i)
        {
            if(!ingredients[i].equals("olives"))
            {
                removedOlives[j++] = ingredients[i];
            }
        }
        return removedOlives;
    }

    public static String[] extractIngredients(String sandwich)
    {
        String[] ingredients = {};

        if(sandwich.isEmpty())
        {
            return ingredients;
        }

        int index1 = sandwich.indexOf("bread");
        int index2;

        if(index1 == -1)
        {
            return ingredients;
        }
        else
        {
            index2 = sandwich.indexOf("bread", index1 + 5);
            if(index2 == -1)
            {
                return ingredients;
            }
        }

        String[] words = sandwich.split("-");
        int size = words.length;

        if(size == 1)
        {
            String onlyOne = words[0].substring(index1 + 5, index2);
            if(isOlives(onlyOne))
            {
                return ingredients;
            }

            ingredients = new String[1];
            ingredients[0] = onlyOne;
            return ingredients;

        }
        int sizeFirst = words[0].length();
        String firstIngredient = words[0].substring(index1 + 5, sizeFirst);

        index2 = words[size - 1].indexOf("bread");
        String lastIngredient = words[size - 1].substring(0, index2);

        ingredients = new String[size];
        ingredients[0] = firstIngredient;

        for(int i = 1; i < size - 1; ++i)
        {
            ingredients[i] = words[i];
        }

        ingredients[size - 1] =  lastIngredient;

        Arrays.sort(ingredients);

        int olives = hasOlives(ingredients);

        if(olives > 0)
        {
            ingredients = removeOlives(ingredients,olives);
        }

        return ingredients;
    }

   /* public static void main(String[] args)
    {
        String[] ingredients = extractIngredients("asdbreadham-olives-tomato-olives-mayobreadblabla");
        for(String i: ingredients)
        {
            System.out.println(i);
        }
    }*/
}


