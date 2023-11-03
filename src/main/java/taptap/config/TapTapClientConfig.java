package taptap.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TapTapClientConfig {

    String host;
    String port;
    String createUserApi;
    String makeTransactionApi;
    String upgradeUserApi;


}
