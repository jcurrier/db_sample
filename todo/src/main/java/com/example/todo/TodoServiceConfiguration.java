package com.example.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by Jeff on 11/12/16.
 */
public class TodoServiceConfiguration extends Configuration {
    @NotEmpty
    private String m_storageProvider = "";

    @NotEmpty
    private String m_daxClusterUrl= "";

    @JsonProperty("store")
    public String getStore() {
        return m_storageProvider;
    }

    @JsonProperty("store")
    public void setStore(String provider) {
        this.m_storageProvider = provider;
    }

    @JsonProperty("daxCluster")
    public String getDaxCluster() {
        return m_daxClusterUrl;
    }

    @JsonProperty("daxCluster")
    public void setDaxCluster(String clusterName) {
        this.m_daxClusterUrl = clusterName;
    }
}

