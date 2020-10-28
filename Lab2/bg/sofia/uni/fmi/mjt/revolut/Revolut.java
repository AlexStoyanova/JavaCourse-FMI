package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;

import java.time.LocalDate;

public class Revolut implements RevolutAPI
{
    private Account[] accounts;
    private Card[] cards;
    private static final double EXCHANGE_RATE = 1.95583;

    private boolean checkCardIsIn(Card card) {
        for (int i = 0; i < cards.length; ++i) {
            if (cards[i].equals(card)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkAccountInIn(Account account) {
        for (int i = 0; i < accounts.length; ++i) {
            if (accounts[i].equals(account)) {
                return true;
            }
        }
        return false;
    }

    private boolean canPay(double amount, String currency) {
        for (int i = 0; i < accounts.length; ++i) {
            if (accounts[i].getCurrency().equals(currency)
                    && accounts[i].getAmount() >= amount) {
                accounts[i].decreaseAmount(amount);
                return true;
            }
        }
        return false;
    }


    public Revolut(Account[] accounts, Card[] cards) {
        this.accounts = accounts;
        this.cards = cards;
    }

    public boolean pay(Card card, int pin, double amount, String currency) {
        return checkCardIsIn(card)
                && card.getType().equals("PHYSICAL")
                && card.getExpirationDate().isAfter(LocalDate.now())
                && !card.isBlocked()
                && card.checkPin(pin)
                && canPay(amount, currency);
    }

    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        if (checkCardIsIn(card)
                && card.getExpirationDate().isAfter(LocalDate.now())
                && !card.isBlocked()
                && shopURL.indexOf(".biz") == -1
                && card.checkPin(pin)){
            boolean isPaid = canPay(amount, currency);
            if(card.getType().equals("VIRTUALONETIME"))
            {
                card.block();
            }
            return isPaid;
        }
        return false;
    }

    public boolean addMoney(Account account, double amount) {
        if (checkAccountInIn(account)) {
            account.addMoney(amount);
            return true;
        }
        return false;
    }

    public boolean transferMoney(Account from, Account to, double amount) {
        if (checkAccountInIn(from)
                && checkAccountInIn(to)
                && !from.equals(to)
                && from.getAmount() >= amount) {
            if (from.getCurrency().equals(to.getCurrency())) {
                from.decreaseAmount(amount);
                to.addMoney(amount);
            } else if (from.getCurrency().equals("BGN")) {
                from.decreaseAmount(amount);
                to.addMoney(amount / EXCHANGE_RATE);
            } else {
                from.decreaseAmount(amount);
                to.addMoney(amount * EXCHANGE_RATE);
            }
            return true;
        }
        return false;
    }

    public double getTotalAmount() {
        double totalAmount = 0;
        for (int i = 0; i < accounts.length; ++i) {
            if (accounts[i].getCurrency().equals("BGN")) {
                totalAmount += accounts[i].getAmount();
            } else {
                totalAmount += (accounts[i].getAmount() * EXCHANGE_RATE);
            }
        }
        return totalAmount;
    }

    public static void main(String[] args) {

    }
}
