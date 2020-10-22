import java.util.Arrays;

public class SandwichExtractor
{
    private static boolean isOlives(String word)
    {
        return word.equals("olives");
    }


    private static String[] oneIngredientOrNone(String word)
    {
        int index = word.indexOf("bread");

        if(index == -1)
        {
            return new String[]{};
        }

        int index2 = word.indexOf("bread", index + 5);

        if(index2 == -1)
        {
            return new String[]{};
        }

        String onlyOne = word.substring(index + 5, index2);

        if(isOlives(onlyOne))
        {
            return new String[]{};
        }

        return new String[]{onlyOne};
    }

    private static String[] removeOlivesAndSelectIngredients(String[] words)
    {
        int size = words.length;
        String[] ingredients = new String[size];

        int countOlive = 0;

        int index = words[0].indexOf("bread");
        int index2 = words[size - 1].indexOf("bread");

        for(int i = 0; i < size; ++i)
        {
            String temp;
            if (i == 0)
            {
                temp = words[i].substring(index + 5);

                if (isOlives(temp))
                {
                    countOlive++;
                }
                else
                {
                    ingredients[i] = temp;
                }
            }
            else if (i == size - 1)
            {
                temp = words[i].substring(0, index2);
                if (isOlives(temp))
                {
                    countOlive++;
                }
                else
                {
                    ingredients[i - countOlive] = temp;
                }
            }
            else
            {
                if (isOlives(words[i]))
                {
                    countOlive++;
                }
                else
                {
                    ingredients[i - countOlive] = words[i];
                }
            }
        }
        return Arrays.copyOf(ingredients, size - countOlive);
    }

    public static String[] extractIngredients(String sandwich)
    {
        if(sandwich.isEmpty())
        {
            return new String[]{};
        }

        String[] words = sandwich.split("-");
        int size = words.length;

        if(size == 1)
        {
            return oneIngredientOrNone(words[0]);
        }

        int index = words[0].indexOf("bread");
        int index2 = words[size - 1].indexOf("bread");

        if(index == -1 || index2 == -1)
        {
            return new String[]{};
        }

        String[] ingredients = removeOlivesAndSelectIngredients(words);
        Arrays.sort(ingredients);

        return ingredients;
    }
    


    public static void main(String[] args)
    {
        String[] ingredients = extractIngredients("asdbreadham-tomato-mayobreadblabla");
        for(String i: ingredients)
        {
            System.out.println(i);
        }
    }
}


