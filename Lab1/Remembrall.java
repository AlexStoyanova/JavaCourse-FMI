public class Remembrall
{
    public static boolean isRepeated(String phoneNumber)
    {
        int[] digits = new int[10];
        int size = phoneNumber.length();

        for(int i = 0; i < size; ++i)
        {
            if(Character.isDigit(phoneNumber.charAt(i)))
            {
                digits[Character.getNumericValue(phoneNumber.charAt(i))]++;
            }
        }
        for(int i = 0; i < 10; ++i)
        {
            if(digits[i] > 1)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLetters(String phoneNumber)
    {
        int size = phoneNumber.length();
        for(int i = 0; i < size; ++i)
        {
            if(Character.isLetter(phoneNumber.charAt(i)))
            {
                return true;
            }
        }
        return false;
    }


    public static boolean isPhoneNumberForgettable(String phoneNumber)
    {
        if(phoneNumber == null || phoneNumber.isEmpty())
        {
            return false;
        }
        if(hasLetters(phoneNumber) || !isRepeated(phoneNumber))
        {
            return true;
        }
        return false;
    }

    /*public static void main(String[] args)
    {
       System.out.println(isPhoneNumberForgettable("(444)-greens"));

    }*/
}
