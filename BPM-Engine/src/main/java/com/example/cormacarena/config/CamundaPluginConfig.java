package com.example.cormacarena.config;

import org.camunda.connect.plugin.impl.ConnectProcessEnginePlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaPluginConfig {

    @Bean
    public ConnectProcessEnginePlugin connectProcessEnginePlugin() {
        return new ConnectProcessEnginePlugin();
    }
}
