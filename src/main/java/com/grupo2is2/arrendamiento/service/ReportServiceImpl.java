package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Payment;
import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PaymentRepository;
import com.grupo2is2.arrendamiento.repository.PropertyRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PropertyRepository propertyRepository;
    private final ContractRepository contractRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ByteArrayResource excelResource(String[] headers, List<String[]> rows, String filename) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Reporte");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            for (int r = 0; r < rows.size(); r++) {
                Row row = sheet.createRow(r + 1);
                String[] data = rows.get(r);
                for (int c = 0; c < data.length; c++) {
                    row.createCell(c).setCellValue(data[c]);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            byte[] bytes = out.toByteArray();

            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }

    @Override
    public Map<String, Long> getSummaryCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("properties", propertyRepository.count());
        counts.put("contracts", contractRepository.count());
        counts.put("payments", paymentRepository.count());
        counts.put("users", userRepository.count());

        long incomeCount = paymentRepository.findAll().stream()
                .filter(p -> p.getDueDate() != null)
                .map(p -> p.getDueDate().getYear() + "-" + p.getDueDate().getMonthValue())
                .distinct()
                .count();
        counts.put("income", incomeCount);

        List<Contract> allContracts = contractRepository.findAll();
        long calendarCount = allContracts.stream()
                .mapToLong(c -> (c.getStartDate() != null ? 1L : 0L) + (c.getEndDate() != null ? 1L : 0L))
                .sum();
        counts.put("calendar", calendarCount);

        return counts;
    }

    @Override
    public ByteArrayResource generatePropertiesReport() {
        List<Property> properties = propertyRepository.findAll();
        String[] headers = {"ID", "Nombre", "Dirección", "Tipo", "Habitaciones", "Baños", "Área", "Renta", "Estado", "Año", "Pisos", "Amueblado", "Dueño"};
        List<String[]> rows = properties.stream().map(p -> new String[]{
                String.valueOf(p.getId()),
                p.getName(),
                p.getAddress(),
                p.getType() != null ? p.getType().name() : "",
                String.valueOf(p.getBedrooms() != null ? p.getBedrooms() : 0),
                String.valueOf(p.getBathrooms() != null ? p.getBathrooms() : 0),
                p.getArea() != null ? p.getArea() : "",
                p.getRent() != null ? p.getRent() : "",
                p.getStatus() != null ? p.getStatus().name() : "",
                String.valueOf(p.getYearBuilt() != null ? p.getYearBuilt() : 0),
                String.valueOf(p.getFloors() != null ? p.getFloors() : 0),
                p.getFurnished() != null && p.getFurnished() ? "Sí" : "No",
                p.getOwner() != null ? p.getOwner().getName() : ""
        }).collect(Collectors.toList());
        return excelResource(headers, rows, "reporte-propiedades.xlsx");
    }

    @Override
    public ByteArrayResource generateContractsReport() {
        List<Contract> contracts = contractRepository.findAll();
        String[] headers = {"ID", "Código", "Propiedad", "Inquilino", "Arrendador", "Inicio", "Fin", "Renta", "Depósito", "Estado", "Día de Pago"};
        List<String[]> rows = contracts.stream().map(c -> new String[]{
                String.valueOf(c.getId()),
                c.getCode(),
                c.getProperty() != null ? c.getProperty().getName() : "",
                c.getTenant() != null ? c.getTenant().getName() : c.getInvitedTenantName() != null ? c.getInvitedTenantName() : "",
                c.getLandlord() != null ? c.getLandlord().getName() : "",
                c.getStartDate() != null ? c.getStartDate().format(DATE_FMT) : "",
                c.getEndDate() != null ? c.getEndDate().format(DATE_FMT) : "",
                c.getMonthlyRent(),
                c.getDeposit() != null ? c.getDeposit() : "",
                c.getStatus() != null ? c.getStatus().name() : "",
                String.valueOf(c.getPaymentDay() != null ? c.getPaymentDay() : 0)
        }).collect(Collectors.toList());
        return excelResource(headers, rows, "reporte-contratos.xlsx");
    }

    @Override
    public ByteArrayResource generatePaymentsReport() {
        List<Payment> payments = paymentRepository.findAll();
        String[] headers = {"ID", "Contrato", "Inquilino", "Propiedad", "Monto", "Estado", "Método", "Vencimiento", "Pagado"};
        List<String[]> rows = payments.stream().map(p -> new String[]{
                String.valueOf(p.getId()),
                p.getContract() != null ? String.valueOf(p.getContract().getId()) : "",
                p.getTenant() != null ? p.getTenant().getName() : "",
                p.getProperty() != null ? p.getProperty() : "",
                p.getAmount(),
                p.getStatus() != null ? p.getStatus().name() : "",
                p.getMethod() != null ? p.getMethod().name() : "",
                p.getDueDate() != null ? p.getDueDate().format(DATE_FMT) : "",
                p.getPaidDate() != null ? p.getPaidDate().format(DATE_FMT) : ""
        }).collect(Collectors.toList());
        return excelResource(headers, rows, "reporte-pagos.xlsx");
    }

    @Override
    public ByteArrayResource generateUsersReport() {
        List<User> users = userRepository.findAll();
        String[] headers = {"ID", "Nombre", "Email", "Rol", "Estado", "Último Login"};
        List<String[]> rows = users.stream().map(u -> new String[]{
                String.valueOf(u.getId()),
                u.getName(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().name() : "",
                u.getStatus() != null ? u.getStatus().name() : "",
                u.getLastLogin() != null ? u.getLastLogin().toString() : ""
        }).collect(Collectors.toList());
        return excelResource(headers, rows, "reporte-usuarios.xlsx");
    }

    @Override
    public ByteArrayResource generateIncomeReport() {
        List<Payment> payments = paymentRepository.findAll();
        String[] headers = {"Mes", "Ingresos Rentas", "Pagos Pendientes", "Total"};
        Map<String, List<Payment>> byMonth = payments.stream()
                .collect(Collectors.groupingBy(p ->
                        p.getDueDate() != null ? p.getDueDate().getMonth().name() + " " + p.getDueDate().getYear() : "Sin fecha"));

        List<String[]> rows = byMonth.entrySet().stream().map(entry -> {
            double paid = entry.getValue().stream()
                    .filter(p -> p.getStatus().name().equals("PAGADO"))
                    .mapToDouble(p -> parseAmount(p.getAmount()))
                    .sum();
            double pending = entry.getValue().stream()
                    .filter(p -> !p.getStatus().name().equals("PAGADO"))
                    .mapToDouble(p -> parseAmount(p.getAmount()))
                    .sum();
            return new String[]{entry.getKey(), String.valueOf(paid), String.valueOf(pending), String.valueOf(paid + pending)};
        }).collect(Collectors.toList());
        return excelResource(headers, rows, "reporte-ingresos.xlsx");
    }

    @Override
    public ByteArrayResource generateCalendarReport() {
        List<Contract> contracts = contractRepository.findAll();
        String[] headers = {"Fecha", "Evento", "Tipo", "Estado"};
        List<String[]> rows = new java.util.ArrayList<>();
        for (Contract c : contracts) {
            rows.add(new String[]{
                    c.getStartDate() != null ? c.getStartDate().format(DATE_FMT) : "",
                    "Inicio de contrato: " + c.getCode(),
                    "Inicio",
                    c.getStatus() != null ? c.getStatus().name() : ""
            });
            rows.add(new String[]{
                    c.getEndDate() != null ? c.getEndDate().format(DATE_FMT) : "",
                    "Fin de contrato: " + c.getCode(),
                    "Fin",
                    c.getStatus() != null ? c.getStatus().name() : ""
            });
        }
        return excelResource(headers, rows, "reporte-calendario.xlsx");
    }

    private double parseAmount(String amount) {
        if (amount == null || amount.isBlank()) return 0;
        try {
            return Double.parseDouble(amount.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
