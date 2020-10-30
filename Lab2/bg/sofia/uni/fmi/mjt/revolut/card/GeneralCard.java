package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;
import java.util.Objects;

public abstract class GeneralCard implements Card
{
    protected String number;
    protected int pin;
    protected LocalDate expirationDate;
    protected boolean isBlocked;
    protected int countWrongPin;

    public GeneralCard(String number, int pin, LocalDate expirationDate)
    {
        this.number = number;
        this.pin = pin;
        this.expirationDate = expirationDate;
        isBlocked = false;
        countWrongPin = 0;
    }

    public abstract String getType();

    public LocalDate getExpirationDate()
    {
        return expirationDate;
    }

    public boolean checkPin(int pin)
    {
        if(this.pin != pin)
        {
            countWrongPin++;
            if(countWrongPin >= 3)
            {
                this.block();
            }
            return false;
        }
        countWrongPin = 0;
        return true;
    }

    public boolean isBlocked()
    {
        return isBlocked;
    }

    public void block()
    {
        isBlocked = true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
        {
            return true;
        }
        if(!(obj instanceof GeneralCard))
        {
            return false;
        }
        return ((GeneralCard) obj).number.equals(this.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
