package com.airesume.resume.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysis {
    private String resumeFilePath;      // stored file path or cloud URL
    private String extractedText;       // parsed resume content
    private Double atsScore;            // AI-calculated ATS score
    private String aiFeedback;          // AI suggestions
    private Boolean analyzed = false;
    private double[] embeddingVector;   // vector for AI matching
}
