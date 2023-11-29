package org.example.app.entities;

public class User extends Account {
    static final long SerialVersionUID = 3;

    public User(String userLogin, String userPassword) {
        super(userLogin,userPassword);
    }

    @Override
    public AccountRole getRole() {
        return AccountRole.user;
    }
//
//    public void showPayedCarts() {
//        ArrayList<Cart> history = Cart.loadCarts(getLogin());
//        int i = 1;
//        if (history.size() == 0)
//            System.out.println("Ваша история покупок пуста");
//        for (Cart c : history) {
//            System.out.println("Корзина №" + c.getId() + ":\n\tДата оплаты: " + c.getDate() + "\n\tСостав:");
//            c.showCart();
//            i++;
//        }
//    }
}
