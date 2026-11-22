package com.huit.sportsbooking.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;

/**
 * Cấu hình Transaction Manager tách biệt cho MongoDB và Neo4j
 * (tránh việc MongoTransactionManager ghi đè Neo4jTransactionManager mặc định).
 */
@Configuration
public class MongoConfig {

    @Bean("mongoTransactionManager")
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean("transactionManager")
    public Neo4jTransactionManager transactionManager(
            Driver driver,
            DatabaseSelectionProvider databaseNameProvider
    ) {
        return new Neo4jTransactionManager(driver, databaseNameProvider);
    }
}
