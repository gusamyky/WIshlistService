package app.wishlist.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Currency;
import java.util.List;

@AllArgsConstructor
public class MonetaryAmount {
    @Setter
    @Getter
    private double amount;
    @Getter
    private Currency currency;

    public static List<Currency> getAvailableCurrencies() {
        return List.of(Currency.getInstance("PLN"),
                Currency.getInstance("USD"),
                Currency.getInstance("EUR"));
    }
}
