package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;
import java.util.Objects;

public class VirtualPermanentCard extends GeneralCard
{
    public VirtualPermanentCard(String number, int pin, LocalDate expirationDate)
    {
        super(number, pin, expirationDate);
    }

    public String getType()
    {
        return "VIRTUALPERMANENT";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
        {
            return true;
        }
        if(!(obj instanceof VirtualPermanentCard))
        {
            return false;
        }
        return ((VirtualPermanentCard) obj).number.equals(this.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
