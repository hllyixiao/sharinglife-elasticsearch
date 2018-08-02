package com.fxsh.es.config;

import com.fxsh.es.anno.FrmsEs;
import com.fxsh.es.attribute.EsAttributeData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


/**
 * Es配置类
 * @author hell
 * @date 2018/4/27
 */
@Configuration
@ConditionalOnProperty(name = "fxsh.es.enable", havingValue = "true")
public class EsConfiguration implements BeanFactoryAware {

    private final EsAttributeData esAttributeData;
    private BeanFactory beanFactory;

    @Autowired
    public EsConfiguration(EsAttributeData esAttributeData) {
        this.esAttributeData = esAttributeData;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        registerEsBeanInfo(getElasticSearChClient());
    }

    /**
     * 获取 RestHighLevelClient
     * @return
     */
    private RestHighLevelClient getElasticSearChClient(){
        if(!esAttributeData.isValidate()){
            return null;
        }
        // 可以设置多个HttpHost
        List<String>  hosts  = esAttributeData.getHosts();
        List<Integer> ports  = esAttributeData.getPorts();
        String        schema = esAttributeData.getSchema();

        HttpHost[] httpHosts = new HttpHost[hosts.size()];
        for(int i = 0; i<hosts.size(); i++){
            httpHosts[i] = new HttpHost(hosts.get(i),ports.get(i),schema);
        }

        RestClientBuilder  builder = RestClient.builder(httpHosts);
        return new RestHighLevelClient(builder);
    }

    /**
     * 动态注册 Es 相关的 bean 信息
     * @param rhlClient
     */
    private void registerEsBeanInfo(RestHighLevelClient rhlClient) {
        if(beanFactory != null){
            if (beanFactory instanceof ListableBeanFactory) {
                ListableBeanFactory        lbf = (ListableBeanFactory)        beanFactory;
                DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) beanFactory;

                // 获取所有被@FrmsEs注解修饰的类的类类型
                Map<String, Object>  frmsEsBeans = lbf.getBeansWithAnnotation(FrmsEs.class);
                // 遍历所有的类类型，获取@FrmsEs注解信息
                for (Object bean : frmsEsBeans.values()) {
                    Class<?> beanClass = bean.getClass();
                    FrmsEs frmsEs = beanClass.getAnnotation(FrmsEs.class);

                    String   beanKey = WordUtils.uncapitalize(beanClass.getSimpleName());
                    String indexName = frmsEs.indexName();
                    String  typeName = frmsEs.typeName();

                    // 动态添加属性
                    BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder. genericBeanDefinition(beanClass);
                    dataSourceBuider.addPropertyValue("indexName",getStrOrDefault(indexName,beanKey));
                    dataSourceBuider.addPropertyValue("typeName" ,getStrOrDefault(typeName,beanKey));
                    dataSourceBuider.addPropertyValue("beanClass",beanClass);
                    dataSourceBuider.addPropertyValue("rhlClient",rhlClient);
                    // 向beanFactory注册，由spring托管
                    dbf.registerBeanDefinition(beanKey, dataSourceBuider.getBeanDefinition());
                }
            }
        }
    }

    /**
     *
     * @param str
     * @param defaultStr
     * @return
     */
    private String getStrOrDefault(String str,String defaultStr){
        if(StringUtils.isNotBlank(str)){
            return str;
        }
        return defaultStr;
    }
}
