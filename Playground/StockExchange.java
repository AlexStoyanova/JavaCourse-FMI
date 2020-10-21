public class StockExchange
{
    public static int maxProfit(int[] prices)
    {
        int minimum = prices[0];
        int maximum = prices[0];
        int result = 0;

        int size = prices.length;

        for(int i = 1; i < size; ++i)
        {
            if (minimum > prices[i])
            {
                minimum = prices[i];
                maximum = prices[i];
                continue;
            }
            if (maximum > prices[i])
            {
                result += (maximum - minimum);

                minimum = prices[i];
                maximum = prices[i];
                continue;
            }
            maximum = prices[i];
        }

        result += (maximum - minimum);
        return result;
    }

    public static void main(String[] args)
    {
        System.out.println(maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
    }
}
