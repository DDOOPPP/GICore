package org.gi.gICore.model.log;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionLog {
    private UUID playerId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal previous;
    private BigDecimal current;

    public enum TransactionType {
        NEW("신규 생성"), DEPOSIT("입금"), WITHDRAW("출금");

        private String display;

        TransactionType(String display) {
            this.display = display;
        }

        public TransactionType valueOfDisplay(String display) {
            for (TransactionType t : TransactionType.values()) {
                if (t.display.equals(display)) {
                    return t;
                }
            }
            return null;
        }

        public String getDisplay() {
            return display;
        }
    }
}
