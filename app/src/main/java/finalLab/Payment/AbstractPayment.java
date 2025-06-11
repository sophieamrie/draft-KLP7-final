package finalLab.Payment;

import finalLab.Model.User;

public abstract class AbstractPayment {
    protected String paymentType;

    public abstract boolean pay(User user, double amount);
    
    public String getPaymentType() {
    return paymentType;
}

public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
}
}
