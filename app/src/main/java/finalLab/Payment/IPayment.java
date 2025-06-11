package finalLab.Payment;

import finalLab.Model.User;

public interface IPayment {
    boolean pay(User user, double amount);
}
