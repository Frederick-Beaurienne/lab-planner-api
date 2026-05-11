package com.fred.labplanner.controller;

import com.fred.labplanner.model.payload.PlanningRequest;
import com.fred.labplanner.model.payload.PlanningResponse;
import com.fred.labplanner.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
public class PlanningLabController {
  private final PlanningService planningService;

  @PostMapping
  @Operation(summary = "Generate laboratory planning")
  public PlanningResponse planify(@Valid @RequestBody PlanningRequest request) {

    return planningService.planify(request);
  }
}
