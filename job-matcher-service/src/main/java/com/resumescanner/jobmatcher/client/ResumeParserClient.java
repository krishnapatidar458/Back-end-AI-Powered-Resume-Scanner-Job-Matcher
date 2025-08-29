package com.resumescanner.jobmatcher.client;

import com.resumescanner.shared.dto.ResumeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "resume-parser-service")
public interface ResumeParserClient {

    @GetMapping("/api/resume-parser/all")
    ResponseEntity<Map<String, Object>> getAllResumes();

    @GetMapping("/api/resume-parser/{id}")
    ResponseEntity<Map<String, Object>> getResumeById(@PathVariable("id") Long id);

    @GetMapping("/api/resume-parser/status/{status}")
    ResponseEntity<Map<String, Object>> getResumesByStatus(@PathVariable("status") String status);
}
