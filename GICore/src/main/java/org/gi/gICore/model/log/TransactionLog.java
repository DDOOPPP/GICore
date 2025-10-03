package org.gi.gICore.model.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gi.gICore.model.LOG;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
public class TransactionLog  extends LOG {
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal previous;
    private BigDecimal current;

    public TransactionLog(UUID playerId, TransactionType type, BigDecimal amount, BigDecimal previous, BigDecimal current) {
        super(playerId);
        this.type = type;
        this.amount = amount;
        this.previous = previous;
        this.current = current;
    }

    public enum TransactionType {
        NEW("신규 생성"), DEPOSIT("입금"), WITHDRAW("출금"),SET("설정");

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
