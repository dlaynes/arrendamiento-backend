package com.grupo2is2.arrendamiento.config;

import com.grupo2is2.arrendamiento.domain.*;
import com.grupo2is2.arrendamiento.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ContractRepository contractRepository;
    private final PaymentRepository paymentRepository;
    private final AmenityRepository amenityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .name("Administrador")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMINISTRADOR)
                .status(UserStatus.ACTIVO)
                .avatar("https://i.pravatar.cc/150?u=admin")
                .build());

        User arrendador = userRepository.save(User.builder()
                .name("Carlos Arrendador")
                .email("arrendador@example.com")
                .password(passwordEncoder.encode("landlord123"))
                .role(UserRole.ARRENDADOR)
                .status(UserStatus.ACTIVO)
                .avatar("https://i.pravatar.cc/150?u=landlord")
                .build());

        User inquilino = userRepository.save(User.builder()
                .name("María Inquilina")
                .email("inquilino@example.com")
                .password(passwordEncoder.encode("tenant123"))
                .role(UserRole.INQUILINO)
                .status(UserStatus.ACTIVO)
                .avatar("https://i.pravatar.cc/150?u=tenant")
                .build());

        // Seed amenities
        List<Amenity> allAmenities = amenityRepository.saveAll(List.of(
            Amenity.builder().name("AIRE_ACONDICIONADO").displayLabel("Aire acondicionado").build(),
            Amenity.builder().name("ALTOS_TECHOS").displayLabel("Altos techos").build(),
            Amenity.builder().name("BALCON").displayLabel("Balcón").build(),
            Amenity.builder().name("BARBACOA").displayLabel("BBQ").build(),
            Amenity.builder().name("CERCO_ELECTRICO").displayLabel("Cerco eléctrico").build(),
            Amenity.builder().name("COCINA_AMERICANA").displayLabel("Cocina americana").build(),
            Amenity.builder().name("COCINA_EQUIPADA").displayLabel("Cocina equipada").build(),
            Amenity.builder().name("COCHERA_TRIPLE").displayLabel("Cochera triple").build(),
            Amenity.builder().name("CUARTO_DE_LAVADO").displayLabel("Cuarto de lavado").build(),
            Amenity.builder().name("CUARTO_DE_SERVICIO").displayLabel("Cuarto de servicio").build(),
            Amenity.builder().name("ESPACIOS_ABIERTOS").displayLabel("Espacios abiertos").build(),
            Amenity.builder().name("ESTACIONAMIENTO").displayLabel("Estacionamiento").build(),
            Amenity.builder().name("GARAGE_2_AUTOS").displayLabel("Garaje 2 autos").build(),
            Amenity.builder().name("GIMNASIO").displayLabel("Gimnasio").build(),
            Amenity.builder().name("INTERNET_FIBRA_OPTICA").displayLabel("Internet fibra óptica").build(),
            Amenity.builder().name("INTERNET_INCLUIDO").displayLabel("Internet incluido").build(),
            Amenity.builder().name("JARDIN").displayLabel("Jardín").build(),
            Amenity.builder().name("JARDIN_AMPLO").displayLabel("Jardín amplio").build(),
            Amenity.builder().name("LAVADORA").displayLabel("Lavadora").build(),
            Amenity.builder().name("LAVANDERIA_COMPARTIDA").displayLabel("Lavandería compartida").build(),
            Amenity.builder().name("PISCINA").displayLabel("Piscina").build(),
            Amenity.builder().name("PISCINA_COMPARTIDA").displayLabel("Piscina compartida").build(),
            Amenity.builder().name("SEGURIDAD").displayLabel("Seguridad").build(),
            Amenity.builder().name("SEGURIDAD_24_7").displayLabel("Seguridad 24/7").build(),
            Amenity.builder().name("SISTEMA_DE_SEGURIDAD").displayLabel("Sistema de seguridad").build(),
            Amenity.builder().name("TERRAZA").displayLabel("Terraza").build(),
            Amenity.builder().name("VENTANALES").displayLabel("Ventanales").build(),
            Amenity.builder().name("VISTA_AL_MAR").displayLabel("Vista al mar").build(),
            Amenity.builder().name("WIFI").displayLabel("WiFi").build()
        ));

        Map<String, Amenity> amenityMap = allAmenities.stream()
                .collect(Collectors.toMap(Amenity::getName, a -> a));

        Property prop1 = propertyRepository.save(Property.builder()
                .name("Departamento Central")
                .address("Av. Principal 123, Lima")
                .type(PropertyType.APARTAMENTO)
                .bedrooms(2)
                .bathrooms(1)
                .area("75 m²")
                .rent("1200")
                .status(PropertyStatus.OCUPADO)
                .description("Moderno departamento en el centro de la ciudad")
                .yearBuilt(2018)
                .floors(5)
                .furnished(true)
                .amenities(List.of(
                    amenityMap.get("WIFI"),
                    amenityMap.get("ESTACIONAMIENTO"),
                    amenityMap.get("GIMNASIO")
                ))
                .owner(arrendador)
                .build());

        Property prop2 = propertyRepository.save(Property.builder()
                .name("Casa de Playa")
                .address("Malecón Sur 456, Miraflores")
                .type(PropertyType.CASA)
                .bedrooms(4)
                .bathrooms(3)
                .area("200 m²")
                .rent("3500")
                .status(PropertyStatus.DISPONIBLE)
                .description("Hermosa casa con vista al mar")
                .yearBuilt(2015)
                .floors(2)
                .furnished(false)
                .amenities(List.of(
                    amenityMap.get("PISCINA"),
                    amenityMap.get("JARDIN"),
                    amenityMap.get("BARBACOA")
                ))
                .owner(arrendador)
                .build());

        Property prop3 = propertyRepository.save(Property.builder()
                .name("Estudio Loft")
                .address("Calle Cultural 789, San Isidro")
                .type(PropertyType.ESTUDIO)
                .bedrooms(1)
                .bathrooms(1)
                .area("45 m²")
                .rent("800")
                .status(PropertyStatus.DISPONIBLE)
                .description("Estudio ideal para estudiantes")
                .yearBuilt(2020)
                .floors(1)
                .furnished(true)
                .amenities(List.of(
                    amenityMap.get("WIFI"),
                    amenityMap.get("LAVADORA")
                ))
                .owner(arrendador)
                .build());

        Contract contract1 = contractRepository.save(Contract.builder()
                .code("CNT-2024-001")
                .tenant(inquilino)
                .landlord(arrendador)
                .property(prop1)
                .invitedTenantName("María Inquilina")
                .invitedTenantEmail("inquilino@example.com")
                .invitedTenantPhone("+51 999 888 777")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .monthlyRent("1200")
                .deposit("2400")
                .status(ContractStatus.ACTIVO)
                .paymentDay(5)
                .terms(List.of("No se permiten mascotas", "No fumar en interiores"))
                .notes("Contrato de primer año")
                .build());

        paymentRepository.save(Payment.builder()
                .contract(contract1)
                .tenant(inquilino)
                .property(prop1.getName())
                .propertyAddress(prop1.getAddress())
                .amount("1200")
                .status(PaymentStatus.PAGADO)
                .method(PaymentMethod.TRANSFERENCIA)
                .dueDate(LocalDate.of(2024, 1, 5))
                .paidDate(LocalDate.of(2024, 1, 4))
                .referenceNumber("REF-001")
                .transactionId("TXN-001")
                .notes("Primer pago")
                .build());

        paymentRepository.save(Payment.builder()
                .contract(contract1)
                .tenant(inquilino)
                .property(prop1.getName())
                .propertyAddress(prop1.getAddress())
                .amount("1200")
                .status(PaymentStatus.PAGADO)
                .method(PaymentMethod.TRANSFERENCIA)
                .dueDate(LocalDate.of(2024, 2, 5))
                .paidDate(LocalDate.of(2024, 2, 3))
                .referenceNumber("REF-002")
                .transactionId("TXN-002")
                .build());

        paymentRepository.save(Payment.builder()
                .contract(contract1)
                .tenant(inquilino)
                .property(prop1.getName())
                .propertyAddress(prop1.getAddress())
                .amount("1200")
                .status(PaymentStatus.PENDIENTE)
                .method(PaymentMethod.TRANSFERENCIA)
                .dueDate(LocalDate.of(2024, 3, 5))
                .build());
    }
}
