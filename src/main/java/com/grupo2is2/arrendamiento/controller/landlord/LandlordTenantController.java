package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.UserDto;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import com.grupo2is2.arrendamiento.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/landlord/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TENANT_READ')")
public class LandlordTenantController extends LandlordBaseController {
    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping("/tenants")
    public ResponseEntity<List<UserDto>> getTenants() {
        List<User> tenants = userRepository.findTenantsByPropertyOwnerId(getCurrentUserId());
        List<UserDto> dtos = tenants.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
