package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.CheckRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.service.PlagiarismCheckService;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class PlagiarismCheckServiceImpl implements PlagiarismCheckService {

    @Value("${code-similarity.main-directory}")
    private String mainDirectory;

//    @Async
//    @Override
//    public void checkPlagiarism(CheckRequest checkRequest, UserDto loggedInUser) {
//        try {
//            List<String> arguments = parseRequest(checkRequest, mainDirectory + "/" + loggedInUser.getMainDirectoryPath(), loggedInUser.getId());
//
//            ClassLoader classLoader = getClass().getClassLoader();
//            InputStream scriptStream = classLoader.getResourceAsStream("similarity-checker/check.py");
//
//            String scriptPath = classLoader.getResource("similarity-checker/check.py").getPath();
//            File scriptFile = new File(scriptPath);
//            String workingDir = scriptFile.getParent();
//            ProcessBuilder pb = new ProcessBuilder("python3", "-");
//            pb.directory(new File(workingDir));
//            pb.redirectInput(ProcessBuilder.Redirect.PIPE);
//            pb.redirectErrorStream(true);
//
//            // Pass the arguments list to the ProcessBuilder constructor
//            pb.command().addAll(arguments);
//
//            Process process = pb.start();
//
//            // Pass the script content to the Python process
//            OutputStream stdin = process.getOutputStream();
//            IOUtils.copy(scriptStream, stdin);
//            stdin.close();
//
//            // Read the output of the Python process
//            InputStream stdout = process.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
//            String line;
//            StringBuilder output = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//            reader.close();
//
//            int exitCode = process.waitFor();
//            log.info("Process for user " + loggedInUser.getId() + " exit with code " + exitCode);
//
//            // Print the output of the Python code
//            log.info("Output:" + output.toString());
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    // FOR DOCKER IMAGE

    @Async
    @Override
    public void checkPlagiarism(CheckRequest checkRequest, UserDto loggedInUser) {
        try {
            List<String> arguments = parseRequest(checkRequest, mainDirectory + "/" + loggedInUser.getMainDirectoryPath(), loggedInUser.getId());

            arguments.forEach(System.out::println);
            // Get the path to the Python script
            Path scriptPath = Paths.get("/app/similarity-checker/check.py");

            // Set the working directory to the parent directory of the script
            Path workingDir = scriptPath.getParent();
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath.toString());
            pb.directory(workingDir.toFile());
            pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            pb.redirectErrorStream(true);

            // Pass the arguments list to the ProcessBuilder constructor
            pb.command().addAll(arguments);

            Process process = pb.start();

            // Copy the script to the process's stdin
            try (InputStream scriptStream = getClass().getResourceAsStream("/similarity-checker/check.py");
                 OutputStream stdin = process.getOutputStream()) {
                IOUtils.copy(scriptStream, stdin);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            log.info("Process for user " + loggedInUser.getId() + " exit with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseRequest(CheckRequest checkRequest, String userDirectory, Long teacherId) {
        List<String> arguments = new ArrayList<>();
        arguments.add("-t");
        checkRequest.getTestDirectories().forEach(testDir -> arguments.add(userDirectory + testDir));

        arguments.add("-r");
        checkRequest.getRefDirectories().forEach(refDir -> arguments.add(userDirectory + refDir));

        arguments.add("-O");
        arguments.add(userDirectory + checkRequest.getReportOutput());

        arguments.add("-TID");
        arguments.add(String.valueOf(teacherId));

        if (checkRequest.getNoiseThreshold() > 0) {
            arguments.add("-n");
            arguments.add(String.valueOf(checkRequest.getNoiseThreshold()));
        }

        if (checkRequest.getGuaranteeThreshold() > 0) {
            arguments.add("-g");
            arguments.add(String.valueOf(checkRequest.getGuaranteeThreshold()));
        }

        if (checkRequest.getDisplayThreshold() > 0) {
            arguments.add("-d");
            arguments.add(String.valueOf(checkRequest.getDisplayThreshold()));
        }

        if (checkRequest.isRemoveImports()) {
            arguments.add("-i");
        }

        if (checkRequest.isSkipPunctuation()) {
            arguments.add("-p");
        }

        if (checkRequest.isTruncate()) {
            arguments.add("-T");
        }

        if (checkRequest.isDisableFilter()) {
            arguments.add("-f");
        }

        return arguments;
    }

}
