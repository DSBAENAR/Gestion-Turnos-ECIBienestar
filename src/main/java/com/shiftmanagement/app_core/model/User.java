package com.shiftmanagement.app_core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(String userName, String numberId, String role, String password) {}
