package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;
import java.util.Objects;

public class PhysicalCard extends GeneralCard
{
    public PhysicalCard(String number, int pin, LocalDate expirationDate)
    {
        super(number, pin, expirationDate);
    }

    public String getType()
    {
        return "PHYSICAL";
    }

}
