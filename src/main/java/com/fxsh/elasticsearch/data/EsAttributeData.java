package com.fxsh.elasticsearch.data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hell
 * @date 2018/6/29
 * @sin 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "fxsh.es.enable", havingValue = "true")
@ConfigurationProperties(prefix = "fxsh.es.config")
public class EsAttributeData {
    private List<String> hosts;
    private List<Integer> ports;
    private String schema;

    protected EsAttributeData() {
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 判断配置对象是否合法
     * @return
     */
    public boolean isValidate(){
        boolean validate = true;
        if(hosts == null || ports == null || schema == null){
            validate =  false;
        }else if (hosts.size() != ports.size()){
            validate =  false;
        }
        return validate;
    }

    @Override
    public String toString() {
        return "EsAttributeData{" +
                "hosts=" + hosts +
                ", port=" + ports +
                ", schema='" + schema + '\'' +
                '}';
    }
}
