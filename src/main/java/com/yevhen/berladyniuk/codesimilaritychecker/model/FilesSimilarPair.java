package com.yevhen.berladyniuk.codesimilaritychecker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "files_pair")
@AllArgsConstructor
@RequiredArgsConstructor
public class FilesSimilarPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String file1;

    @NotNull
    @NotBlank
    private String file2;

    @NotNull
    private double similarityScore;

    @NotNull
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String code1;

    @NotNull
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String code2;

    private Long teacherId;

}
