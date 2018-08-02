package com.fxsh.es.attribute;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 配置的ES属性信息
 * @author hell
 * @date 2018/6/29
 * @sin 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "fxsh.es.enable", havingValue = "true")
@ConfigurationProperties(prefix = "fxsh.es.config")
public class EsAttributeData {

    private List<String>  hosts;
    private List<Integer> ports;
    private String        schema;

    public EsAttributeData() {}

    public EsAttributeData(List<String> hosts, List<Integer> ports, String schema) {
        this.hosts  = hosts;
        this.ports  = ports;
        this.schema = schema;
    }

    public List<String> getHosts() { return hosts; }

    public void setHosts(String hosts) {
        this.hosts = Arrays.asList(hosts.split(","));
    }

    public List<Integer> getPorts() { return ports; }

    public void setPorts(String ports) {
        List<String> portsStr = Arrays.asList(ports.split(","));
        this.ports  = new ArrayList<>();
        for (String port : portsStr) {
            this.ports.add(Integer.parseInt(port));
        }
    }

    public String getSchema() { return schema; }

    public void setSchema(String schema) { this.schema = schema; }

    /**
     * 判断配置对象是否合法
     * @return
     */
    public boolean isValidate(){
        boolean validate = true;
        if(hosts == null || ports == null ||
                schema == null || hosts.size() != ports.size()){
            validate =  false;
        }
        return validate;
    }

    @Override
    public String toString() {
        return "EsAttributeData{" +
                "hosts=" + hosts +
                ", ports=" + ports +
                ", schema='" + schema + '\'' +
                '}';
    }
}
