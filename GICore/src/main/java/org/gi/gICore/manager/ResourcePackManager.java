package org.gi.gICore.manager;

import org.bukkit.entity.Player;
import org.gi.gICore.GICore;
import org.gi.gICore.util.ModuleLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackManager {
    private static final String RESOURCE_PACK_PATH = "messages/output";
    private static File zipFile;
    private static String currentSha1;
    private static ModuleLogger logger;

    //차후 Config로 옮길예정
    private static final String WEB_SERVER_UPLOAD = "http://localhost:8080/api/resourcepack/upload";
    private static final String WEB_SERVER_DOWNLOAD = "http://localhost:8080/api/resourcepack/download";

    public static void initialize(){
        if (logger == null) {
            logger = new ModuleLogger(GICore.getInstance(),"ResourcePackManager");
        }

        reloadResourcePack();
    }

    public static void downloadResourcePack(Player player){
        if (zipFile == null || currentSha1 == null){
            logger.error("Resource pack not initialized");
            return;
        }

        try{
            player.setResourcePack(WEB_SERVER_DOWNLOAD, currentSha1,true);

            logger.info("Resource pack sent to %s",player.getName());
        }catch (Exception e){
            logger.error("Resource pack send failed: %s",e.getMessage());
        }
    }

    public static void reloadResourcePack(){
        GICore plugin = GICore.getInstance();
        File output = new File(plugin.getDataFolder(),RESOURCE_PACK_PATH);
        try{
            if (!output.exists()){
                plugin.copyResourceFolder("messages");
            }

            File tempDir = new File(plugin.getDataFolder(),"temp");
            if (!tempDir.exists()){
                tempDir.mkdirs();
            }

            zipFile = new File(tempDir, "resourcepack.zip");

            ZIP(output,zipFile);
            currentSha1 = calculateSha1(zipFile);;

            logger.info("Resource pack created");
            logger.info("SHA1: %s",currentSha1);
            logger.info("SIZE: %s",zipFile.length()+"Bytes");

            uploadResourcePack();
        }catch (Exception e){
            logger.error("Resource pack creation failed: %s",e.getMessage());
            return;
        }
    }

    /**
     *
     * 폴더 압축 (동일한 내용은 항상 동일한 SHA-1 생성)
     *
     * */
    private static void ZIP(File sourceFolder, File zipFile) throws IOException {
        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))){
            Path sourcePath = sourceFolder.toPath();

            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .sorted() // 파일 순서 고정
                    .forEach(path -> {
                        try{
                            String zipEntryName = sourcePath.relativize(path).toString().replace("\\","/");

                            ZipEntry zipEntry = new ZipEntry(zipEntryName);
                            zipEntry.setTime(0); // 타임스탬프를 고정값으로 설정 (1970-01-01)

                            out.putNextEntry(zipEntry);
                            Files.copy(path, out);
                            out.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private static String calculateSha1(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        try(InputStream in = new FileInputStream(file)){
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }

        byte[] hashBytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    private static void uploadResourcePack() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(WEB_SERVER_UPLOAD);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Content-Length", String.valueOf(zipFile.length()));
            connection.setRequestProperty("X-SHA1", currentSha1);
            connection.setRequestProperty("X-Filename", zipFile.getName());

            try (OutputStream out = connection.getOutputStream();
                 FileInputStream in = new FileInputStream(zipFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("Resource pack uploaded successfully");
            } else {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream()))) {
                    String errorMsg = reader.lines().reduce("", (a, b) -> a + b);
                    logger.error("Upload failed (code: %d): %s", responseCode, errorMsg);
                } catch (Exception e) {
                    logger.error("Upload failed with response code: %d", responseCode);
                }
            }
        } catch (IOException e) {
            logger.error("Upload failed: %s", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getCurrentSha1() {
        return currentSha1;
    }

    public static File getZipFile() {
        return zipFile;
    }

    public static String getResourcePackPath() {
        return RESOURCE_PACK_PATH;
    }
}
