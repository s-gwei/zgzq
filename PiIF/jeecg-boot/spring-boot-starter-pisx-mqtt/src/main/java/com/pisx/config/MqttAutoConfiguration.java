package com.pisx.config;

import com.pisx.utils.MqttUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.io.UnsupportedEncodingException;

/**
 * @Description: mqtt自动配置
 * @author: boliu
 * @date: 2020年09月22日 9:28
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttAutoConfiguration implements ApplicationContextAware, BeanPostProcessor {

    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private MqttProperties mqttProperties;

    private static final Logger logger = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        mqttProperties.getConfig().forEach((channelName, config) -> init(channelName, config));
    }

    /**
     * 初始化
     *
     * @param channelName
     * @param config
     */
    private void init(String channelName, MqttProperties.Config config) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        //通道信息初始化
        beanFactory.registerBeanDefinition(channelName, mqttChannel());
        MessageChannel mqttChannel = beanFactory.getBean(channelName, MessageChannel.class);
        //初始化通道转换器
        beanFactory.registerBeanDefinition(channelName + "MqttChannelAdapter", channelAdapter(config, mqttChannel));
        String handlerBeanName = channelName + MqttUtils.CHANNEL_NAME_SUFFLX;
        //MqttPahoMessageHandler初始化
        beanFactory.registerBeanDefinition(handlerBeanName, mqttOutbound(config));
        MqttUtils.put(channelName, beanFactory.getBean(handlerBeanName, MqttPahoMessageHandler.class));
    }

    /**
     * 消息发送客户端
     *
     * @param config
     * @return
     */
    private AbstractBeanDefinition mqttOutbound(MqttProperties.Config config) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MqttPahoMessageHandler.class);
        builder.addConstructorArgValue(config.getProducerClientId());
        builder.addConstructorArgValue(mqttClientFactory(config, false));
        builder.addPropertyValue("async", config.getAsync());
        return builder.getBeanDefinition();
    }

    /**
     * mqtt消息驱动转换器
     *
     * @param config
     * @param mqttChannel
     * @return
     */
    private AbstractBeanDefinition channelAdapter(MqttProperties.Config config, MessageChannel mqttChannel) {
        BeanDefinitionBuilder messageProducerBuilder = BeanDefinitionBuilder.genericBeanDefinition(MqttPahoMessageDrivenChannelAdapter.class);
        messageProducerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        messageProducerBuilder.addConstructorArgValue(config.getConsumerClientId());
        messageProducerBuilder.addConstructorArgValue(mqttClientFactory(config, true));
        messageProducerBuilder.addConstructorArgValue(config.getTopics());
        messageProducerBuilder.addPropertyValue("converter", new DefaultPahoMessageConverter());
        messageProducerBuilder.addPropertyValue("qos", config.getQos());
        messageProducerBuilder.addPropertyValue("outputChannel", mqttChannel);
        return messageProducerBuilder.getBeanDefinition();
    }

    /**
     * mqtt工厂
     *
     * @param config
     * @param isConsumer
     * @return
     */
    private Object mqttClientFactory(MqttProperties.Config config, boolean isConsumer) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(config.getUrl());
        //cleanSession设置为false，接收qos>0的离线消息
        options.setCleanSession(true);
        options.setKeepAliveInterval(config.getKepAliveInterval());
        options.setPassword(config.getPassword().toCharArray());
        options.setUserName(config.getUsername());
        options.setConnectionTimeout(config.getTimeout());
        MqttProperties.Will will = null;
        if (isConsumer && config.getConsumerWill() != null) {
            will = config.getConsumerWill();
        } else if (!isConsumer && config.getProducerWill() != null) {
            will = config.getProducerWill();
        }
        if (will != null) {
            try {
                options.setWill(will.getTopic(), will.getPayload().getBytes("utf-8"), will.getQos(), will.getRetained());
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * 初始化通道
     *
     * @return
     */
    private AbstractBeanDefinition mqttChannel() {
        BeanDefinitionBuilder messageChannelBuilder = BeanDefinitionBuilder.genericBeanDefinition(DirectChannel.class);
        messageChannelBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        return messageChannelBuilder.getBeanDefinition();
    }
}
