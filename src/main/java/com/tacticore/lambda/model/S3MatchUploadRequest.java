package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO para subir un match desde S3.
 * Contiene la información del bucket y key donde está el archivo .dem
 */
public class S3MatchUploadRequest {
    
    @JsonProperty("bucket")
    private String bucket;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("metadata")
    private MatchMetadata metadata;
    
    public S3MatchUploadRequest() {
    }
    
    public S3MatchUploadRequest(String bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }
    
    public String getBucket() {
        return bucket;
    }
    
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public MatchMetadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(MatchMetadata metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Extrae el nombre del archivo desde el key de S3.
     * Si el archivo tiene extensión .gz, la remueve para obtener el nombre original.
     * @return Nombre del archivo (última parte del path, sin .gz)
     */
    public String getFileName() {
        if (key == null || key.isEmpty()) {
            return "unknown.dem";
        }
        int lastSlash = key.lastIndexOf('/');
        String fileName = lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
        
        // Remover extensión .gz si existe
        if (fileName.toLowerCase().endsWith(".gz")) {
            fileName = fileName.substring(0, fileName.length() - 3);
        }
        
        return fileName;
    }
    
    @Override
    public String toString() {
        return "S3MatchUploadRequest{" +
                "bucket='" + bucket + '\'' +
                ", key='" + key + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}

