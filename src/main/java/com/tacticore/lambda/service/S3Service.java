package com.tacticore.lambda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Servicio para interactuar con AWS S3.
 * Permite descargar archivos desde buckets de S3.
 */
@Service
public class S3Service {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Descarga un archivo de S3 y devuelve su contenido como byte array.
     * Si el archivo está comprimido con gzip (.gz), lo descomprime automáticamente.
     *
     * @param bucketName Nombre del bucket de S3
     * @param objectKey  Key (ruta) del objeto en S3
     * @return byte array con el contenido del archivo (descomprimido si era .gz)
     * @throws RuntimeException si ocurre un error al descargar el archivo
     */
    public byte[] downloadFile(String bucketName, String objectKey) {
        try {
            System.out.println("📥 Downloading file from S3: s3://" + bucketName + "/" + objectKey);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
                byte[] content = response.readAllBytes();
                System.out.println("✅ File downloaded successfully. Size: " + content.length + " bytes");
                
                // Si el archivo está comprimido con gzip, descomprimirlo
                if (objectKey.toLowerCase().endsWith(".gz")) {
                    System.out.println("🗜️ File is gzip compressed, decompressing...");
                    content = decompressGzip(content);
                    System.out.println("✅ File decompressed successfully. New size: " + content.length + " bytes");
                }
                
                return content;
            }
        } catch (S3Exception e) {
            String errorMsg = "Error downloading file from S3 (s3://" + bucketName + "/" + objectKey + "): " + e.getMessage();
            System.err.println("❌ " + errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = "IO error reading S3 file: " + e.getMessage();
            System.err.println("❌ " + errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    /**
     * Descomprime un byte array que está en formato gzip.
     *
     * @param compressed Datos comprimidos en formato gzip
     * @return Datos descomprimidos
     * @throws IOException si ocurre un error durante la descompresión
     */
    private byte[] decompressGzip(byte[] compressed) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
             GZIPInputStream gis = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        }
    }

    /**
     * Descarga un archivo de S3 y devuelve un InputStream.
     *
     * @param bucketName Nombre del bucket de S3
     * @param objectKey  Key (ruta) del objeto en S3
     * @return InputStream con el contenido del archivo
     */
    public InputStream downloadFileAsStream(String bucketName, String objectKey) {
        byte[] content = downloadFile(bucketName, objectKey);
        return new ByteArrayInputStream(content);
    }

    /**
     * Verifica si un archivo existe en S3.
     *
     * @param bucketName Nombre del bucket de S3
     * @param objectKey  Key (ruta) del objeto en S3
     * @return true si el archivo existe, false en caso contrario
     */
    public boolean fileExists(String bucketName, String objectKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw new RuntimeException("Error checking file existence in S3: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los metadatos de un archivo en S3.
     *
     * @param bucketName Nombre del bucket de S3
     * @param objectKey  Key (ruta) del objeto en S3
     * @return HeadObjectResponse con los metadatos del archivo
     */
    public HeadObjectResponse getFileMetadata(String bucketName, String objectKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            return s3Client.headObject(headObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Error getting file metadata from S3: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae el nombre del archivo desde el objectKey de S3.
     * Si el archivo tiene extensión .gz, la remueve para obtener el nombre original.
     *
     * @param objectKey Key completo del objeto en S3
     * @return Nombre del archivo (última parte del path, sin .gz si estaba comprimido)
     */
    public String extractFileName(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return "unknown.dem";
        }
        int lastSlash = objectKey.lastIndexOf('/');
        String fileName = lastSlash >= 0 ? objectKey.substring(lastSlash + 1) : objectKey;
        
        // Remover extensión .gz si existe
        if (fileName.toLowerCase().endsWith(".gz")) {
            fileName = fileName.substring(0, fileName.length() - 3);
        }
        
        return fileName;
    }
}

