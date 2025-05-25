package com.shiftmanagement.app_core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(String userName, String id, String role, String password) {}
