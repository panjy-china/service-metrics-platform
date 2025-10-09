package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.panjy.servicemetricsplatform.entity.FirstCallSummary;
import org.panjy.servicemetricsplatform.mapper.FirstCallSummaryMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FirstCallSummaryServiceTest {
    
    @Mock
    private FirstCallSummaryMapper firstCallSummaryMapper;
    
    @InjectMocks
    private FirstCallSummaryService firstCallSummaryService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void calculateQualifiedRateWithEmptyList() {
        // Given
        when(firstCallSummaryMapper.selectAll()).thenReturn(Arrays.asList());
        
        // When
        double rate = firstCallSummaryService.calculateQualifiedRate();
        
        // Then
        assertEquals(0.0, rate);
    }
    
    @Test
    void calculateQualifiedRateWithAllQualified() {
        // Given
        List<FirstCallSummary> records = Arrays.asList(
            createFirstCallSummary(700L),
            createFirstCallSummary(800L),
            createFirstCallSummary(900L)
        );
        when(firstCallSummaryMapper.selectAll()).thenReturn(records);
        
        // When
        double rate = firstCallSummaryService.calculateQualifiedRate();
        
        // Then
        assertEquals(1.0, rate);
    }
    
    @Test
    void calculateQualifiedRateWithNoneQualified() {
        // Given
        List<FirstCallSummary> records = Arrays.asList(
            createFirstCallSummary(100L),
            createFirstCallSummary(200L),
            createFirstCallSummary(300L)
        );
        when(firstCallSummaryMapper.selectAll()).thenReturn(records);
        
        // When
        double rate = firstCallSummaryService.calculateQualifiedRate();
        
        // Then
        assertEquals(0.0, rate);
    }
    
    @Test
    void calculateQualifiedRateWithMixed() {
        // Given
        List<FirstCallSummary> records = Arrays.asList(
            createFirstCallSummary(100L),  // Not qualified
            createFirstCallSummary(700L),  // Qualified
            createFirstCallSummary(300L),  // Not qualified
            createFirstCallSummary(800L)   // Qualified
        );
        when(firstCallSummaryMapper.selectAll()).thenReturn(records);
        
        // When
        double rate = firstCallSummaryService.calculateQualifiedRate();
        
        // Then
        assertEquals(0.5, rate);
    }
    
    @Test
    void getQualifiedAndTotalCount() {
        // Given
        List<FirstCallSummary> records = Arrays.asList(
            createFirstCallSummary(100L),  // Not qualified
            createFirstCallSummary(700L),  // Qualified
            createFirstCallSummary(300L),  // Not qualified
            createFirstCallSummary(800L),  // Qualified
            createFirstCallSummary(500L)   // Not qualified
        );
        when(firstCallSummaryMapper.selectAll()).thenReturn(records);
        
        // When
        long[] counts = firstCallSummaryService.getQualifiedAndTotalCount();
        
        // Then
        assertEquals(2, counts[0]);  // Qualified count
        assertEquals(5, counts[1]);  // Total count
    }
    
    private FirstCallSummary createFirstCallSummary(Long callDuration) {
        FirstCallSummary summary = new FirstCallSummary();
        summary.setColCltID("test-id");
        summary.setFirstCallDate(LocalDateTime.now());
        summary.setCallDuration(callDuration);
        return summary;
    }
}