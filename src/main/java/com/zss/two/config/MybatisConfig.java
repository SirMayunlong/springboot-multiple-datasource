package com.zss.two.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan("com.zss.two.mapper")
public class MybatisConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.core")
    public DataSource coreDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.schedule")
    public DataSource scheduleDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean
    public DynamicDataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceConstants.CORE_DATA_SOURCE, coreDataSource());
        targetDataSources.put(DataSourceConstants.SCHEDULE_DATA_SOURCE, scheduleDataSource());

        DynamicDataSource dataSource = new DynamicDataSource(DataSourceConstants.CORE_DATA_SOURCE);
        //设置数据源映射
        dataSource.setTargetDataSources(targetDataSources);
////        设置默认数据源，当无法映射到数据源时会使用默认数据源
        dataSource.setDefaultTargetDataSource(coreDataSource());
        dataSource.afterPropertiesSet();
        return dataSource;
    }
    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
