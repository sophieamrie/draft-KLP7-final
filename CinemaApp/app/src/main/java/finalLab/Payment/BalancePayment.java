package finalLab.Payment;

import finalLab.Model.User;

public class BalancePayment extends AbstractPayment implements IPayment {

    public BalancePayment() {
        this.paymentType = "Balance";
    }

    @Override
    public boolean pay(User user, double amount) {
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            return true;
        }
        return false;
    }
}