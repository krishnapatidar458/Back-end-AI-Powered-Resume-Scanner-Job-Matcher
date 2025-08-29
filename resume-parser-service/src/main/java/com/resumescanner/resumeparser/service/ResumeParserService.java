package com.resumescanner.resumeparser.service;

import com.resumescanner.resumeparser.entity.Resume;
import com.resumescanner.resumeparser.entity.WorkExperience;
import com.resumescanner.resumeparser.entity.Education;
import com.resumescanner.resumeparser.repository.ResumeRepository;
import com.resumescanner.shared.dto.ResumeDTO;
import com.resumescanner.shared.dto.ExperienceDTO;
import com.resumescanner.shared.dto.EducationDTO;
import com.resumescanner.shared.enums.ResumeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeParserService {

    private final ResumeRepository resumeRepository;

    public ResumeDTO uploadResume(Long userId, MultipartFile file) throws IOException {
        log.info("Uploading resume for user: {}, filename: {}", userId, file.getOriginalFilename());
        
        // Validate file
        validateFile(file);
        
        // Create resume entity
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setOriginalFilename(file.getOriginalFilename());
        resume.setFileSize(file.getSize());
        resume.setFileType(file.getContentType());
        resume.setStatus(ResumeStatus.UPLOADED);
        resume.setCreatedAt(LocalDateTime.now());
        resume.setUpdatedAt(LocalDateTime.now());
        
        // TODO: Store file to object storage (MinIO/S3) and set filePath
        resume.setFilePath("uploads/resumes/" + System.currentTimeMillis() + "_" + file.getOriginalFilename());
        
        // Extract raw text
        String rawText = extractTextFromFile(file);
        resume.setRawText(rawText);
        
        // Save initial resume
        resume = resumeRepository.save(resume);
        
        // Start async parsing
        parseResumeAsync(resume.getId());
        
        return convertToDTO(resume);
    }

    public Optional<ResumeDTO> getResumeById(Long id) {
        return resumeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ResumeDTO> getResumesByUserId(Long userId) {
        return resumeRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ResumeDTO> getResumesByStatus(ResumeStatus status) {
        return resumeRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ResumeDTO> getAllResumes() {
        return resumeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteResume(Long id) {
        log.info("Deleting resume with id: {}", id);
        resumeRepository.deleteById(id);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && 
            !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Only PDF and DOCX files are supported");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size cannot exceed 10MB");
        }
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        
        if ("application/pdf".equals(contentType)) {
            return extractTextFromPDF(file);
        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            return extractTextFromDOCX(file);
        }
        
        throw new IllegalArgumentException("Unsupported file type: " + contentType);
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromDOCX(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private void parseResumeAsync(Long resumeId) {
        // TODO: Implement async parsing using @Async or message queue
        // For now, we'll do synchronous parsing
        try {
            parseResume(resumeId);
        } catch (Exception e) {
            log.error("Error parsing resume with id: {}", resumeId, e);
            updateResumeStatus(resumeId, ResumeStatus.FAILED);
        }
    }

    private void parseResume(Long resumeId) {
        log.info("Starting to parse resume with id: {}", resumeId);
        
        Optional<Resume> resumeOpt = resumeRepository.findById(resumeId);
        if (resumeOpt.isEmpty()) {
            log.error("Resume not found with id: {}", resumeId);
            return;
        }
        
        Resume resume = resumeOpt.get();
        resume.setStatus(ResumeStatus.PROCESSING);
        resumeRepository.save(resume);
        
        try {
            // Basic parsing logic - in production, use NLP libraries
            parseBasicInfo(resume);
            parseSkills(resume);
            // TODO: Implement advanced parsing for work experience and education
            
            resume.setStatus(ResumeStatus.COMPLETED);
            resume.setParsedAt(LocalDateTime.now());
            resume.setUpdatedAt(LocalDateTime.now());
            
            resumeRepository.save(resume);
            log.info("Successfully parsed resume with id: {}", resumeId);
            
        } catch (Exception e) {
            log.error("Error parsing resume content for id: {}", resumeId, e);
            resume.setStatus(ResumeStatus.FAILED);
            resumeRepository.save(resume);
        }
    }

    private void parseBasicInfo(Resume resume) {
        String text = resume.getRawText();
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        
        // Simple email extraction
        if (text.contains("@")) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.contains("@") && line.contains(".")) {
                    resume.setEmail(extractEmail(line));
                    break;
                }
            }
        }
        
        // Simple phone extraction
        if (text.matches(".*\\d{3}[-.\\s]?\\d{3}[-.\\s]?\\d{4}.*")) {
            resume.setPhone(extractPhone(text));
        }
        
        // Extract name (first line usually contains name)
        String[] lines = text.split("\n");
        if (lines.length > 0 && !lines[0].trim().isEmpty()) {
            resume.setFullName(lines[0].trim());
        }
    }

    private void parseSkills(Resume resume) {
        String text = resume.getRawText().toLowerCase();
        
        // Basic skill extraction - in production, use NLP
        String[] commonSkills = {
            "java", "python", "javascript", "react", "spring", "hibernate", 
            "sql", "mongodb", "docker", "kubernetes", "aws", "git", "maven",
            "microservices", "rest api", "machine learning", "data analysis"
        };
        
        for (String skill : commonSkills) {
            if (text.contains(skill.toLowerCase())) {
                resume.getSkills().add(skill);
            }
        }
    }

    private String extractEmail(String line) {
        // Simple email extraction
        String[] words = line.split("\\s+");
        for (String word : words) {
            if (word.contains("@") && word.contains(".")) {
                return word.replaceAll("[^a-zA-Z0-9@.]", "");
            }
        }
        return null;
    }

    private String extractPhone(String text) {
        // Simple phone extraction
        return text.replaceAll(".*?(\\d{3}[-.\\s]?\\d{3}[-.\\s]?\\d{4}).*", "$1");
    }

    private void updateResumeStatus(Long resumeId, ResumeStatus status) {
        resumeRepository.findById(resumeId).ifPresent(resume -> {
            resume.setStatus(status);
            resume.setUpdatedAt(LocalDateTime.now());
            resumeRepository.save(resume);
        });
    }

    private ResumeDTO convertToDTO(Resume resume) {
        ResumeDTO dto = new ResumeDTO();
        dto.setId(resume.getId());
        dto.setUserId(resume.getUserId());
        dto.setOriginalFilename(resume.getOriginalFilename());
        dto.setFilePath(resume.getFilePath());
        dto.setFileSize(resume.getFileSize());
        dto.setFileType(resume.getFileType());
        dto.setStatus(resume.getStatus());
        
        // Parsed content
        dto.setFullName(resume.getFullName());
        dto.setEmail(resume.getEmail());
        dto.setPhone(resume.getPhone());
        dto.setSummary(resume.getSummary());
        dto.setSkills(resume.getSkills());
        dto.setCertifications(resume.getCertifications());
        dto.setLanguages(resume.getLanguages());
        dto.setRawText(resume.getRawText());
        
        // Convert work experiences
        if (resume.getWorkExperiences() != null) {
            dto.setWorkExperiences(resume.getWorkExperiences().stream()
                    .map(this::convertWorkExperienceToDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert educations
        if (resume.getEducations() != null) {
            dto.setEducations(resume.getEducations().stream()
                    .map(this::convertEducationToDTO)
                    .collect(Collectors.toList()));
        }
        
        // Timestamps
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setParsedAt(resume.getParsedAt());
        
        return dto;
    }

    private ExperienceDTO convertWorkExperienceToDTO(WorkExperience experience) {
        ExperienceDTO dto = new ExperienceDTO();
        dto.setId(experience.getId());
        dto.setCompanyName(experience.getCompanyName());
        dto.setJobTitle(experience.getJobTitle());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());
        dto.setIsCurrent(experience.getIsCurrent());
        dto.setDescription(experience.getDescription());
        dto.setResponsibilities(experience.getResponsibilities());
        dto.setAchievements(experience.getAchievements());
        dto.setLocation(experience.getLocation());
        return dto;
    }

    private EducationDTO convertEducationToDTO(Education education) {
        EducationDTO dto = new EducationDTO();
        dto.setId(education.getId());
        dto.setInstitutionName(education.getInstitutionName());
        dto.setDegree(education.getDegree());
        dto.setFieldOfStudy(education.getFieldOfStudy());
        dto.setStartDate(education.getStartDate());
        dto.setEndDate(education.getEndDate());
        dto.setGpa(education.getGpa());
        dto.setLocation(education.getLocation());
        dto.setDescription(education.getDescription());
        dto.setIsCurrent(education.getIsCurrent());
        return dto;
    }
}
