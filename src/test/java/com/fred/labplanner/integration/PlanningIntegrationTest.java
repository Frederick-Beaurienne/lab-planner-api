package com.fred.labplanner.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlanningIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGeneratePlanning() throws Exception {

        String request = """
                {
                  "samples": [
                    {
                      "id": "S001",
                      "type": "BLOOD",
                      "priority": "URGENT",
                      "analysisTime": 45,
                      "arrivalTime": "09:00"
                    },
                    {
                      "id": "S002",
                      "type": "BLOOD",
                      "priority": "STAT",
                      "analysisTime": 30,
                      "arrivalTime": "09:30"
                    }
                  ],
                  "technicians": [
                    {
                      "id": "T001",
                      "speciality": "BLOOD",
                      "startTime": "08:00",
                      "endTime": "17:00"
                    }
                  ],
                  "equipment": [
                    {
                      "id": "E001",
                      "type": "BLOOD",
                      "available": true
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/planning")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())

                // schedule exists
                .andExpect(jsonPath("$.schedule").isArray())

                // first sample should be STAT
                .andExpect(jsonPath("$.schedule[0].id")
                        .value("S002"))

                // no conflicts
                .andExpect(jsonPath("$.metrics.conflicts")
                        .value(0))

                // metrics exist
                .andExpect(jsonPath("$.metrics.totalTime")
                        .exists())

                .andExpect(jsonPath("$.metrics.efficiency")
                        .exists())

                .andExpect(jsonPath("$.metrics.averageWaitTime")
                        .exists());
    }

    @Test
    void testBasicScheduling() throws Exception {

        String request = """
                {
                  "samples": [
                    {
                      "id": "S001",
                      "type": "BLOOD",
                      "priority": "URGENT",
                      "analysisTime": 30,
                      "arrivalTime": "09:00"
                    }
                  ],
                  "technicians": [
                    {
                      "id": "T001",
                      "speciality": "BLOOD",
                      "startTime": "08:00",
                      "endTime": "17:00"
                    }
                  ],
                  "equipment": [
                    {
                      "id": "E001",
                      "type": "BLOOD",
                      "available": true
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/planning")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule").isArray())
                .andExpect(jsonPath("$.schedule.length()").value(1))
                .andExpect(jsonPath("$.metrics.conflicts").value(0));
    }

    @Test
    void testPriorityRespect() throws Exception {

        String request = """
                {
                  "samples": [
                    {
                      "id": "S001",
                      "type": "BLOOD",
                      "priority": "ROUTINE",
                      "analysisTime": 30,
                      "arrivalTime": "09:00"
                    },
                    {
                      "id": "S002",
                      "type": "BLOOD",
                      "priority": "STAT",
                      "analysisTime": 30,
                      "arrivalTime": "09:05"
                    }
                  ],
                  "technicians": [
                    {
                      "id": "T001",
                      "speciality": "BLOOD",
                      "startTime": "08:00",
                      "endTime": "17:00"
                    }
                  ],
                  "equipment": [
                    {
                      "id": "E001",
                      "type": "BLOOD",
                      "available": true
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/planning")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule[0].id")
                        .value("S002"));
    }

    @Test
    void testSpecializationMatching() throws Exception {

        String request = """
                {
                  "samples": [
                    {
                      "id": "S001",
                      "type": "TISSUE",
                      "priority": "URGENT",
                      "analysisTime": 30,
                      "arrivalTime": "09:00"
                    }
                  ],
                  "technicians": [
                    {
                      "id": "T001",
                      "speciality": "BLOOD",
                      "startTime": "08:00",
                      "endTime": "17:00"
                    },
                    {
                      "id": "T002",
                      "speciality": "MICROBIOLOGY",
                      "startTime": "08:00",
                      "endTime": "17:00"
                    }
                  ],
                  "equipment": [
                    {
                      "id": "E001",
                      "type": "MICROBIOLOGY",
                      "available": true
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/planning")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule[0].technicianId")
                        .value("T002"));
    }

    @Test
    void testLunchBreaks() throws Exception {

        String request = """
                {
                  "samples": [
                    {
                      "id": "S001",
                      "type": "BLOOD",
                      "priority": "URGENT",
                      "analysisTime": 30,
                      "arrivalTime": "12:15"
                    }
                  ],
                  "technicians": [
                    {
                      "id": "T001",
                      "speciality": "BLOOD",
                      "startTime": "08:00",
                      "endTime": "17:00",
                      "lunchBreak": "12:00-13:00"
                    }
                  ],
                  "equipment": [
                    {
                      "id": "E001",
                      "type": "BLOOD",
                      "available": true
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/planning")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule[0].startTime")
                        .value("13:00:00"));
    }

    @Test
    void testEfficiencyCoefficients() throws Exception {

        String request = """
                {
                  "samples": [
                    {
                      "id": "S001",
                      "type": "BLOOD",
                      "priority": "URGENT",
                      "analysisTime": 60,
                      "arrivalTime": "09:00"
                    }
                  ],
                  "technicians": [
                    {
                      "id": "T001",
                      "speciality": "BLOOD",
                      "efficiency": 1.2,
                      "startTime": "08:00",
                      "endTime": "17:00"
                    }
                  ],
                  "equipment": [
                    {
                      "id": "E001",
                      "type": "BLOOD",
                      "available": true
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/planning")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule[0].duration")
                        .value(50));
    }
}