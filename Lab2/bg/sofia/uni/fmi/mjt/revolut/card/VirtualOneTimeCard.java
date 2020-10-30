package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;
import java.util.Objects;

public class VirtualOneTimeCard extends GeneralCard
{
    public VirtualOneTimeCard(String number, int pin, LocalDate expirationDate)
    {
        super(number, pin, expirationDate);
    }

    public String getType()
    {
        return "VIRTUALONETIME";
    }

}
