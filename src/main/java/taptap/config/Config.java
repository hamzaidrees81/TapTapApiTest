package taptap.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {

    String host;
    String port;
    String createUserApi;
    String makeTransactionApi;
    String upgradeUserApi;


}
