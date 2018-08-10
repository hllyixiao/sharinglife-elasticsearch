package com.fxsh.demo.es.config;

import com.es.test.demo.es.anno.FrmsEs;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * <p> Es 的配置类，用于注册bean
 * <p> 由配置文件控制 fxsh.es.enable
 * <li> true : 开启 Es</li>
 * <li> false : 禁止启动 Es</li>
 *
 * @author hell
 * @date 2018/8/7
 * @see EsAttributeData
 * @since 1.0.0
 */
@ConditionalOnProperty(name = "ds.source.type", havingValue = "es")
@Configuration
public class EsConfig implements BeanFactoryAware {

    private Logger LOG = LoggerFactory.getLogger(EsConfig.class);

    private BeanFactory beanFactory;
    private EsAttributeData esAttributeData;
    private boolean isRegister = false;

    @Autowired
    public EsConfig(EsAttributeData esAttributeData) {
        this.esAttributeData = esAttributeData;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (!isRegister) {
            start();
            isRegister = true;
        }
    }

    private void start() {
        LOG.info("spring注册Es配置文件——开始" );
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        registerEsBeanInfo(getElasticSearChClient());
        stopWatch.stop();
        LOG.info("spring注册Es配置文件——结束，耗时: " + stopWatch.getTotalTimeSeconds());
    }

    /**
     * 获取 RestHighLevelClient
     *
     * @return
     */
    private RestHighLevelClient getElasticSearChClient() {
        // 验证配置数据是否合法
        esAttributeData.validate();
        // 可以设置多个HttpHost
        List<String>  hosts = esAttributeData.getHosts();
        List<Integer> ports = esAttributeData.getPorts();
        String        schema = esAttributeData.getSchema();

        HttpHost[] httpHosts = new HttpHost[hosts.size()];
        for (int i = 0; i < hosts.size(); i++) {
            httpHosts[i] = new HttpHost(hosts.get(i), ports.get(i), schema);
            LOG.debug("Es节点信息：host=[{}] , port=[{}]", hosts.get(i), ports.get(i));
        }

        RestClientBuilder builder = RestClient.builder(httpHosts);
        return new RestHighLevelClient(builder);
    }

    /**
     * 动态注册 Es 相关的 bean 信息
     *
     * @param rhlClient
     */
    private void registerEsBeanInfo(RestHighLevelClient rhlClient) {
        if (beanFactory != null && beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) beanFactory;

            // 获取所有被@FrmsEs注解修饰的类的类类型
            Map<String, Object> frmsEsBeans = dbf.getBeansWithAnnotation(FrmsEs.class);

            // 遍历所有的类类型，获取@FrmsEs注解信息
            for (Object bean : frmsEsBeans.values()) {
                Class<?> beanClass = bean.getClass();
                FrmsEs frmsEs = beanClass.getAnnotation(FrmsEs.class);

                // 获取父类的泛型类型
                Type supType = beanClass.getGenericSuperclass();
                Type[]  generics = ((ParameterizedType) supType).getActualTypeArguments();
                Type genericType = generics[0];
                String genericTypeName = genericType.getTypeName();
                // 如果注解中没有参数值，则设置默认值
                String defaultValue = getLastStrAndUncapitalizet(genericTypeName);

                String   beanKey = getLastStrAndUncapitalizet(beanClass.getName());
                String indexName = frmsEs.indexName();
                String  typeName = frmsEs.typeName();

                // 动态添加属性
                BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
                dataSourceBuider.addPropertyValue("indexName", getStrOrDefault(indexName, defaultValue));
                dataSourceBuider.addPropertyValue("typeName", getStrOrDefault(typeName, defaultValue));
                dataSourceBuider.addPropertyValue("beanClass", beanClass);
                dataSourceBuider.addPropertyValue("rhlClient", rhlClient);
                // 向beanFactory注册，由spring托管
                dbf.registerBeanDefinition(beanKey, dataSourceBuider.getBeanDefinition());
            }
        }
    }

    /**
     * @param str
     * @param defaultStr
     * @return
     */
    private String getStrOrDefault(String str, String defaultStr) {
        if (StringUtils.isNotBlank(str)) {
            return str;
        }
        return defaultStr;
    }

    /**
     * 截取最后一个由"."分割的最后一个字符串，并且第一个字符小写
     *
     * @param str
     * @return
     */
    private String getLastStrAndUncapitalizet(String str) {
        int index = str.lastIndexOf(".");
        return WordUtils.uncapitalize(
                str.substring(index + 1, str.length()));
    }
}
