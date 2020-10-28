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

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
        {
            return true;
        }
        if(!(obj instanceof VirtualOneTimeCard))
        {
            return false;
        }
        return ((VirtualOneTimeCard) obj).number.equals(this.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
