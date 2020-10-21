public class SocialDistanceMaximizer
{
    public static int maxDistance(int[] seats)
    {
        int currDist = 0;
        int maxDist = 0;
        int size = seats.length;

        boolean startsWithZeroes = seats[0] == 0;
        boolean endsWithZeroes = seats[size - 1] == 0;
        boolean isEven = false;

        for(int i = 0; i < size; ++i)
        {
            if(seats[i] == 0)
            {
                currDist++;
            }
            else
            {
                if(startsWithZeroes && currDist > maxDist)
                {
                    maxDist = currDist;
                    startsWithZeroes = false;
                    currDist = 0;
                    continue;
                }

                isEven = currDist%2 == 0;
                currDist = (isEven ? currDist/2 : currDist/2 + 1);

                if(isEven && currDist > maxDist)
                {
                    maxDist = currDist;
                }
                else if(currDist > maxDist)
                {
                    maxDist = currDist;
                }
                currDist = 0;

            }
        }
        if(endsWithZeroes && currDist > maxDist)
        {
            maxDist = currDist;
        }
        return maxDist;
    }

    /*public static void main(String[] args)
    {
        System.out.println(maxDistance(new int[]{1,0,0,0,0,0,0,1,1}));
    }*/
}
