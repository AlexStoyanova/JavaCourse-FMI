package bg.sofia.uni.fmi.mjt.revolut.account;

import java.util.Objects;

public abstract class Account
{
    private double amount;
    private String IBAN;

    public Account(String IBAN) {
        this(IBAN, 0);
    }

    public Account(String IBAN, double amount) {
        this.IBAN = IBAN;
        this.amount = amount;
    }

    public abstract String getCurrency();

    public double getAmount() {
        return amount;
    }

    public void decreaseAmount(double money)
    {
        amount-=money;
    }

    public void addMoney(double money)
    {
        amount+=money;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        if(!(obj instanceof Account))
        {
            return false;
        }
        return ((Account) obj).IBAN.equals(this.IBAN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IBAN);
    }
}
