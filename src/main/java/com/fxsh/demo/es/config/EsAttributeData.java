package com.fxsh.demo.es.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * <p>配置的 ES 属性信息
 *
 * <p>通过配置文件构建
 *
 * @author hell
 * @date 2018/6/29
 * @sin 1.0.0
 * @see EsConfig
 */
@ConditionalOnProperty(name = "ds.source.type", havingValue = "es")
@ConfigurationProperties(prefix = "ds.source.esconfig")
@Configuration
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
    public void validate(){
        Assert.notEmpty(hosts,"hosts不能为空数据");
        Assert.notEmpty(ports,"ports不能为空数据");
        Assert.notNull(schema,"schema不能为空数据");
        if(hosts.size() != ports.size()){
            Assert.notNull(null,"hosts与ports数据量必须相同");
        }
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
