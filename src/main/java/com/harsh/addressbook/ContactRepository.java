package com.harsh.addressbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

/**
 * @author - HarshitPandey
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact>  findByNameIgnoreCase(String name);

    Optional<Contact> findByPhoneNumber(String PhoneNumber);

    Optional<Contact> findByEmail(String Email);

    @Query(value = """
        SELECT *
        FROM contact
        WHERE LOWER(SPLIT_PART(TRIM(name), ' ', 1)) = LOWER(:firstName)
        """, nativeQuery = true)
    List<Contact> findByFirstNameIgnoreCase(
            @Param("firstName") String firstName
    );
}
