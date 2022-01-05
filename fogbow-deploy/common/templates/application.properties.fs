#DB-Free:
#spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
#    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
#    org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
#    org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
spring.jpa.database=POSTGRESQL
spring.datasource.driverClassName=org.postgresql.Driver
spring.jpa.generate-ddl=true
