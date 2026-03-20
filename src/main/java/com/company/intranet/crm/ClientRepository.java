package com.company.intranet.crm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findAllByOrderByCompanyNameAsc();

    List<Client> findByStatusOrderByCompanyNameAsc(Client.ClientStatus status);

    Optional<Client> findByCompanyNameIgnoreCase(String companyName);

    boolean existsByOrgNumberAndIdNot(String orgNumber, java.util.UUID excludeId);

    boolean existsByOrgNumber(String orgNumber);
}
