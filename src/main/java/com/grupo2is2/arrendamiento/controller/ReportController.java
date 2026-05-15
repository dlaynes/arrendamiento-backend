package com.grupo2is2.arrendamiento.controller;

import com.grupo2is2.arrendamiento.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary() {
        return ResponseEntity.ok(reportService.getSummaryCounts());
    }

    @GetMapping("/{type}/download")
    public ResponseEntity<ByteArrayResource> downloadReport(@PathVariable String type) {
        ByteArrayResource resource;
        String filename;

        switch (type) {
            case "properties" -> {
                resource = reportService.generatePropertiesReport();
                filename = "reporte-propiedades.xlsx";
            }
            case "contracts" -> {
                resource = reportService.generateContractsReport();
                filename = "reporte-contratos.xlsx";
            }
            case "payments" -> {
                resource = reportService.generatePaymentsReport();
                filename = "reporte-pagos.xlsx";
            }
            case "users" -> {
                resource = reportService.generateUsersReport();
                filename = "reporte-usuarios.xlsx";
            }
            case "income" -> {
                resource = reportService.generateIncomeReport();
                filename = "reporte-ingresos.xlsx";
            }
            case "calendar" -> {
                resource = reportService.generateCalendarReport();
                filename = "reporte-calendario.xlsx";
            }
            default -> throw new RuntimeException("Tipo de reporte no soportado: " + type);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
