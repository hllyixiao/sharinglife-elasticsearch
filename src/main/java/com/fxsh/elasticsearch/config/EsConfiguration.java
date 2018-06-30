package com.fxsh.elasticsearch.config;

import com.fxsh.elasticsearch.data.EsAttributeData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;


/**
 * @author hell
 * @date 2018/4/27
 */
@Configuration
@ConditionalOnProperty(name = "fxsh.es.enable", havingValue = "true")
public class EsConfiguration {

    @Autowired
    private EsAttributeData esAttributeData;

    @Primary
    @Bean(name = "primaryESClient")
    public RestHighLevelClient primaryElasticSearChClient(){
        if(esAttributeData.isValidate()){
            //可以设置多个HttpHost
        }
        List<String> hosts = esAttributeData.getHosts();
        List<Integer> ports = esAttributeData.getPorts();
        String schema = esAttributeData.getSchema();
        HttpHost[] httpHosts = new HttpHost[hosts.size()];
        for(int i = 0; i<hosts.size(); i++){
            httpHosts[i] = new HttpHost(hosts.get(i),ports.get(i),schema);
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);
        return new RestHighLevelClient(builder);
    }
}
