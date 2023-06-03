package com.yevhen.berladyniuk.codesimilaritychecker.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Log4j2
public class DirectoryManager {

    @Value("${code-similarity.main-directory}")
    private String mainDirectory;

    public void createDirectory(String folderName) {
        File mainDir = new File(mainDirectory);
        File newFolder = new File(mainDir, folderName);
        if (newFolder.mkdirs()) {
            log.info("New folder created at " + newFolder.getAbsolutePath());
        } else {
            log.error("Failed to create new folder");
        }
    }

    public void deleteDirectory(String folderName) {
        File mainDir = new File(mainDirectory);
        File folderToDelete = new File(mainDir, folderName);
        if (folderToDelete.exists() && folderToDelete.isDirectory()) {
            boolean success = deleteContents(folderToDelete) && folderToDelete.delete();
            if (success) {
                log.info("Folder " + folderName + " deleted successfully");
            } else {
                log.error("Failed to delete folder " + folderName);
            }
        } else {
            log.error("Folder " + folderName + " does not exist");
        }
    }

    private boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    log.error("Failed to delete " + file.getAbsolutePath());
                    success = false;
                }
            }
        }
        return success;
    }

    public void deleteFile(String filePath) {
        File fileToDelete = new File(mainDirectory, filePath);
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            if (fileToDelete.delete()) {
                log.info("File " + filePath + " deleted successfully");
            } else {
                log.error("Failed to delete file " + filePath);
            }
        } else {
            log.error("File " + filePath + " does not exist");
        }
    }

    public Map<String, Object> getDirectoryStructure(String folderName) {
        File mainDir = new File(mainDirectory);
        File folder = new File(mainDir, folderName);
        Map<String, Object> dirMap = new LinkedHashMap<>();
        dirMap.put("name", folder.getName());

        if (folder.exists() && folder.isDirectory()) {
            addFilesToMap(folder, dirMap);
            addSubDirsToMap(folder, dirMap);
        } else {
            log.error("Folder " + folderName + " does not exist");
        }

        return dirMap;
    }

    private void addFilesToMap(File dir, Map<String, Object> dirMap) {
        File[] files = dir.listFiles();
        List<String> fileList = new ArrayList<>();
        assert files != null;
        for (File file : files) {
            if (!file.isDirectory()) {
                fileList.add(file.getName());
            }
        }
        if (!fileList.isEmpty()) {
            dirMap.put("files", fileList);
        }
    }

    private void addSubDirsToMap(File dir, Map<String, Object> dirMap) {
        File[] files = dir.listFiles();

        if (files != null) {
            List<Map<String, Object>> subDirs = new ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    Map<String, Object> subDirMap = new LinkedHashMap<>();
                    subDirMap.put("name", file.getName());
                    addFilesToMap(file, subDirMap);
                    addSubDirsToMap(file, subDirMap);
                    if (!subDirMap.isEmpty()) {
                        subDirs.add(subDirMap);
                    }
                }
            }
            if (!subDirs.isEmpty()) {
                dirMap.put("subdirectories", subDirs);
            }
        }
    }

    public void uploadFiles(MultipartFile[] files, String pathToUpload) {
        File mainDir = new File(mainDirectory);
        File uploadDir = new File(mainDir, pathToUpload);

        if (!uploadDir.exists()) {
            if (uploadDir.mkdirs()) {
                log.info("Created directory: " + uploadDir.getAbsolutePath());
            } else {
                log.error("Failed to create directory: " + uploadDir.getAbsolutePath());
                return;
            }
        }

        for (MultipartFile file : files) {
            File newFile = new File(uploadDir, Objects.requireNonNull(file.getOriginalFilename()));
            try {
                file.transferTo(newFile);
                log.info("File uploaded successfully: " + newFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }
    }

}
