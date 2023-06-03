package com.yevhen.berladyniuk.codesimilaritychecker.repository;

import com.yevhen.berladyniuk.codesimilaritychecker.model.FilesSimilarPair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesSimilarPairRepository extends CrudRepository<FilesSimilarPair, Long> {

    List<FilesSimilarPair> findAllByFile1StartingWithOrderBySimilarityScoreDesc(String prefix);

}
