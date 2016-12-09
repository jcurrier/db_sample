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

    @JsonProperty("store")
    public String getStore() {
        return m_storageProvider;
    }

    @JsonProperty("store")
    public void setStore(String provider) {
        this.m_storageProvider = provider;
    }
}

