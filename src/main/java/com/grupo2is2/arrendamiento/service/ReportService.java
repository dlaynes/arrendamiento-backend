package com.grupo2is2.arrendamiento.service;

import org.springframework.core.io.ByteArrayResource;
import java.util.Map;

public interface ReportService {
    Map<String, Long> getSummaryCounts();
    ByteArrayResource generatePropertiesReport();
    ByteArrayResource generateContractsReport();
    ByteArrayResource generatePaymentsReport();
    ByteArrayResource generateUsersReport();
    ByteArrayResource generateIncomeReport();
    ByteArrayResource generateCalendarReport();
}
