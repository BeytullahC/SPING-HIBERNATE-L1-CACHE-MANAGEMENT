package io.dakich.spring.hibernate.custom.repository.sample;

import io.dakich.spring.hibernate.custom.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleRepository extends JpaRepository<Role, Integer>,
    JpaSpecificationExecutor<Role> {

}
