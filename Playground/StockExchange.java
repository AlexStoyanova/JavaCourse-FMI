public class StockExchange
{
    public static int maxProfit(int[] prices)
    {
        int size = prices.length;
        int[][] matrix = new int[size][size];
        int maxProfit = 0;
        int currProfit = 0;

        for(int i = 0; i < size; ++i)
        {
            for(int j = i + 1; j < size; ++j)
            {
                matrix[i][j] = (prices[j] - prices[i]);
            }
        }

        for(int i = 0; i < size; ++i)
        {
            for(int j = i + 1; j < size; ++j)
            {
                if(matrix[i][j] > 0)
                {
                    currProfit = matrix[i][j];
                    if(currProfit > maxProfit)
                    {
                        maxProfit = currProfit;
                    }
                    for(int k = j + 1; k < size; ++k)
                    {
                        for(int l = j + 2; l < size; ++l)
                        {
                            currProfit = matrix[i][j] + matrix[k][l];
                            if(currProfit > maxProfit)
                            {
                                maxProfit = currProfit;
                                currProfit = 0;
                            }
                        }
                    }
                }
            }
        }

        return maxProfit;
    }

   /* public static void main(String[] args)
    {
        System.out.println(maxProfit(new int[]{7, 6, 4, 3, 1}));
    }*/
}
