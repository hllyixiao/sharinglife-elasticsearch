package com.example.elasticsearch.config;

import com.example.elasticsearch.dsto.BaseDSTO;
import com.example.elasticsearch.dsto.impl.BaseDSTOEsImpl;
import com.example.elasticsearch.pojo.Book;
import com.example.elasticsearch.pojo.User;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * @author hell
 * @date 2018/4/27
 */
@Configuration
@ConditionalOnProperty(name = "ds.source.type", havingValue = "es")
public class ESConfiguration {

    @Value("${ds.source.es.host:localhost}")
    private String host;

    @Value("${ds.source.es.port:9200}")
    private int port;

    @Value("${ds.source.es.schema:http}")
    private String schema;

    @Value("${ds.source.es.isSetConnectTimeOutConfig:false}")
    private boolean isSetConnectTimeOutConfig;

    @Value("${ds.source.es.isSetMutiConnectConfig:false}")
    private boolean isSetMutiConnectConfig;

    private static final int CONNECT_TIME_OUT = 1000;
    private static final int SOCKET_TIME_OUT = 30000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 500;

    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;

    @Primary
    @Bean(name = "primaryESClient")
    public RestHighLevelClient primaryElasticSearChClient(){
        //可以设置多个HttpHost
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(host, port, schema));
        // 主要关于异步httpclient的连接延时配置
        if(isSetConnectTimeOutConfig){
            setConnectTimeOutConfig(builder);
        }
        //主要关于异步httpclient的连接数配置
        if(isSetMutiConnectConfig){
            setMutiConnectConfig(builder);
        }
        return new RestHighLevelClient(builder);
    }

    /**
     * 主要关于异步httpclient的连接延时配置
      */
    public static void setConnectTimeOutConfig(RestClientBuilder builder){
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                builder.setConnectTimeout(CONNECT_TIME_OUT);
                builder.setSocketTimeout(SOCKET_TIME_OUT);
                builder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
                return builder;
            }
        });
    }


    /**
     *    主要关于异步httpclient的连接数配置
     */
    public static void setMutiConnectConfig(RestClientBuilder builder){
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                httpAsyncClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);
                httpAsyncClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);
                return httpAsyncClientBuilder;
            }
        });
    }


    @Primary
    @Bean(name = "defaultBookDSTO")
    public BaseDSTO<Book> getBooktDSTO(
            @Qualifier("primaryESClient") RestHighLevelClient restHighLevelClient){
        return new BaseDSTOEsImpl(restHighLevelClient,"book","book") {
        };
    }

    @Primary
    @Bean(name = "defaultUserDSTO")
    public BaseDSTO<User> getUsertDSTO(
            @Qualifier("primaryESClient") RestHighLevelClient restHighLevelClient){
        return new BaseDSTOEsImpl(restHighLevelClient,"user","user") {
        };
    }
}
