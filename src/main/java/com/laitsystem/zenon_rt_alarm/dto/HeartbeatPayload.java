package com.laitsystem.zenon_rt_alarm.dto;


import java.util.List;

public class HeartbeatPayload {
    private String clientId;
    private List<VariableInfo> variables;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<VariableInfo> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableInfo> variables) {
        this.variables = variables;
    }

    public static class VariableInfo {
        private String name;
        private String label;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

