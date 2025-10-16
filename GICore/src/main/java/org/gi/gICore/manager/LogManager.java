package org.gi.gICore.manager;

import org.gi.gICore.GICore;
import org.gi.gICore.model.LOG;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.repository.log.Transaction;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.TaskUtil;
import java.sql.Connection;
import java.sql.SQLException;

public class LogManager {
    private Transaction transaction;
    private ModuleLogger logger;
    public LogManager() {
        this.transaction = new Transaction();
        this.logger = new ModuleLogger(GICore.getInstance(),"LogManager");
    }

    public void logInsert(LOG log, LOG_TAG tag){
        TaskUtil.runSync(() -> {
            Result result = Result.ERROR("LOG TAG ERROR : " + tag.name());
            try(Connection connection = DatabaseManager.getconnection()){

                try{
                    switch (tag){
                        case GUILD :
                            break;
                        case TRANSACTION :
                            TransactionLog t_log = (TransactionLog) log;
                            result = transaction.insert(t_log,connection);
                            break;
                        default:
                            logger.error("Log Insert Error: " + result.getMessage());
                            return;
                    }
                } catch (Exception e) {
                    logger.error("Log Insert Error: ", e);
                }

            } catch (SQLException e) {
                logger.error("Connection acquisition failed", e);
                return;
            }
            if (!result.isSuccess()){
                logger.error("Log Insert Error: " + result.getMessage());
            }
        });
    }
}
