package com.vn.hung.xxxpre.repository;

public class ApplicationIntegrationProperties {

    Aws aws;

    public Aws getAws() {
        return this.aws;
    }

    public void setAws(final Aws aws) {
        this.aws = aws;
    }

    public static class Aws {
        String region;
        String accessKey;
        String secretAccessKey;

        public String getRegion() {
            return this.region;
        }

        public String getAccessKey() {
            return this.accessKey;
        }

        public String getSecretAccessKey() {
            return this.secretAccessKey;
        }

        public void setRegion(final String region) {
            this.region = region;
        }

        public void setAccessKey(final String accessKey) {
            this.accessKey = accessKey;
        }

        public void setSecretAccessKey(final String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }
    }
}
