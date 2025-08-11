package org.aryak.batch.database;


import org.aryak.batch.file.Order;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DatabaseReading {

    private static final String[] tokens = new String[]{"order_id", "first_name",
            "last_name", "email", "cost", "item_id", "item_name", "ship_date"};

    private static final String SQL = """
            SELECT order_id, first_name, last_name, email, cost, item_id, item_name, ship_date
            FROM orders
            ORDER BY order_id
            """;

    private final DataSource datasource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public DatabaseReading(DataSource datasource, JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.datasource = datasource;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    /***
     * JDBC Cursor Item Reader : NOT thread-safe, suitable for single-threaded processing
     * @return
     */
    @Bean
    public ItemReader<Order> itemReader() {

        return new JdbcCursorItemReaderBuilder<Order>()
                .name("jdbcCursorItemReader")
                .sql(SQL)
                .dataSource(datasource)
                .rowMapper((rs, rowNum) ->
                        Order.builder()
                                .orderId(rs.getLong(tokens[0]))
                                .firstName(rs.getString(tokens[1]))
                                .lastName(rs.getString(tokens[2]))
                                .email(rs.getString(tokens[3]))
                                .cost(rs.getBigDecimal(tokens[4]))
                                .itemId(rs.getString(tokens[5]))
                                .itemName(rs.getString(tokens[6]))
                                .shipDate(rs.getString(tokens[7]))
                                .build())
                .build();
    }

    @Bean
    public Job dbJob(){
        return new JobBuilder("dbJob", jobRepository)
                .start(chunkedStep())
                .build();

    }

    @Bean
    public Step chunkedStep() {
        return new StepBuilder("chunkedStep", jobRepository)
                .<Order, Order>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    // JDBC Paging Item Reader : thread-safe, suitable for multi-threaded processing
    @Bean
    public ItemReader<Order> multithreadedPagingItemReader() throws Exception {

        return new JdbcPagingItemReaderBuilder<Order>()
                .name("jdbcCursorItemReader")
                .pageSize(10) // should be same as chunk size
                .queryProvider(queryProvider())
                .build();
    }

    @Bean
    public PagingQueryProvider queryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(datasource);
        factory.setSelectClause(String.join(",", tokens));
        factory.setFromClause("FROM orders");
        factory.setSortKey("order_id");
        return factory.getObject();
    }
}
