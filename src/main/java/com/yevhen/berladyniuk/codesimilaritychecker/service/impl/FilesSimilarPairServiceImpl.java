package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.FilesSimilarPairDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.StudentStatistic;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.FilesSimilarPair;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.FilesSimilarPairRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.SubjectRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.FilesSimilarPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilesSimilarPairServiceImpl implements FilesSimilarPairService {

    @Value("${code-similarity.main-directory}")
    private String mainDirectory;

    private final FilesSimilarPairRepository filesSimilarPairRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public FilesSimilarPairServiceImpl(FilesSimilarPairRepository filesSimilarPairRepository,
                                       SubjectRepository subjectRepository
    ) {
        this.filesSimilarPairRepository = filesSimilarPairRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<StudentStatistic> getStudentStatistic(String studentEmail, Long subjectId, UserDto loggedInUser) {
        if (!this.subjectRepository.existsByIdAndTeacherId(subjectId, loggedInUser.getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Subject not found!");
        }

        Subject subject = this.subjectRepository.findById(subjectId).get();

        String prefixForPairs = mainDirectory + "/" + loggedInUser.getMainDirectoryPath();
        String prefix = prefixForPairs + "/" + subject.getFolderName() + "/" + studentEmail + "/";

        List<FilesSimilarPair> matchingPairs = filesSimilarPairRepository.findAllByFile1StartingWithOrderBySimilarityScoreDesc(prefix);

        Map<String, StudentStatistic> groupedPairs = new LinkedHashMap<>();

        for (FilesSimilarPair pair : matchingPairs) {
            String file1 = pair.getFile1().replace(prefix, "");
            FilesSimilarPairDto dto = this.convertToDto(pair, prefixForPairs);

            StudentStatistic fileGroupResponse = groupedPairs.get(file1);

            if (fileGroupResponse == null) {
                fileGroupResponse = new StudentStatistic(file1, new ArrayList<>());
                groupedPairs.put(file1, fileGroupResponse);
            }

            fileGroupResponse.getPairs().add(dto);
        }

        return new ArrayList<>(groupedPairs.values());
    }

    @Override
    public FilesSimilarPair getByIdAndTeacher(Long id, UserDto loggedInUser) {
        FilesSimilarPair filesSimilarPair = this.filesSimilarPairRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Record not found!"));
        if (loggedInUser.getId().equals(filesSimilarPair.getTeacherId())) {
            String path = mainDirectory + "/" + loggedInUser.getMainDirectoryPath();
            filesSimilarPair.setFile1(filesSimilarPair.getFile1().replace(path, ""));
            filesSimilarPair.setFile2(filesSimilarPair.getFile2().replace(path, ""));
            return filesSimilarPair;
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "Impossible to get not your record!");
    }

    private FilesSimilarPairDto convertToDto(FilesSimilarPair pair, String prefix) {
        return FilesSimilarPairDto.builder()
                .id(pair.getId())
                .file2(pair.getFile2().replace(prefix, ""))
                .similarityScore(pair.getSimilarityScore())
                .build();
    }
}
