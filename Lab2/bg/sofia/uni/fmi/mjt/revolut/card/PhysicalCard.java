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

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
        {
            return true;
        }
        if(!(obj instanceof PhysicalCard))
        {
            return false;
        }
        return ((PhysicalCard) obj).number == this.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
