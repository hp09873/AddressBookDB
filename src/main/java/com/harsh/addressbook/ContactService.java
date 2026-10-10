
package com.harsh.addressbook;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author HarshitPandey
 */
@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // Find contact by ID
    public Optional<Contact> findContactById(Long id) {
        return contactRepository.findById(id);
    }

    // Find contact by phone number
    public Optional<Contact> findContactByPhone(String phoneNumber) {
        return contactRepository.findByPhoneNumber(phoneNumber);
    }

    // Find contact by email
    public Optional<Contact> findContactByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return contactRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT)
        );
    }

    // Find contacts by full name, then first name
    public List<Contact> findContactByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchName = name.trim();

        List<Contact> matches =
                contactRepository.findByNameIgnoreCase(searchName);

        if (matches.isEmpty()) {
            matches =
                    contactRepository.findByFirstNameIgnoreCase(searchName);
        }

        return matches;
    }

    // Validate and add contact
    public Contact addContact(
            String name,
            String phoneNumber,
            String email) {

        validateContact(name, phoneNumber, email);

        name = name.trim();
        phoneNumber = phoneNumber.trim();
        email = email.trim().toLowerCase(Locale.ROOT);

        if (contactRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new DuplicateContactException(
                    "Phone number already exists"
            );
        }

        if (contactRepository.findByEmail(email).isPresent()) {
            throw new DuplicateContactException(
                    "Email already exists"
            );
        }

        Contact contact = new Contact(name, phoneNumber, email);

        return contactRepository.save(contact);
    }

    // Validate and edit contact
    public Contact editContact(
            Long id,
            String name,
            String phoneNumber,
            String email) {

        validateContact(name, phoneNumber, email);

        name = name.trim();
        phoneNumber = phoneNumber.trim();
        email = email.trim().toLowerCase(Locale.ROOT);

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() ->
                        new ContactNotFoundException("Contact not found")
                );

        Optional<Contact> phoneContact =
                contactRepository.findByPhoneNumber(phoneNumber);

        if (phoneContact.isPresent()
                && !phoneContact.get().getContactId().equals(id)) {
            throw new DuplicateContactException(
                    "Phone number already exists"
            );
        }

        Optional<Contact> emailContact =
                contactRepository.findByEmail(email);

        if (emailContact.isPresent()
                && !emailContact.get().getContactId().equals(id)) {
            throw new DuplicateContactException(
                    "Email already exists"
            );
        }

        contact.setName(name);
        contact.setPhoneNumber(phoneNumber);
        contact.setEmail(email);

        return contactRepository.save(contact);
    }

    // Delete contact
    public void deleteContact(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() ->
                        new ContactNotFoundException("Contact not found")
                );

        contactRepository.delete(contact);
    }

    // Search by phone number, email, or name
    public List<Contact> searchContact(String searchTerm) {

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        searchTerm = searchTerm.trim();

        // 1. Search by phone number
        if (searchTerm.matches("\\d+")) {
            if (!isValidPhoneNumber(searchTerm)) {
                throw new IllegalArgumentException(
                        "Phone number must contain exactly 10 digits"
                );
            }

            return contactRepository.findByPhoneNumber(searchTerm)
                    .map(List::of)
                    .orElseGet(ArrayList::new);
        }

        // 2. Search by email
        if (searchTerm.contains("@")) {
            if (!isValidEmail(searchTerm)) {
                throw new IllegalArgumentException(
                        "Invalid email format"
                );
            }

            return contactRepository.findByEmail(
                            searchTerm.toLowerCase(Locale.ROOT)
                    )
                    .map(List::of)
                    .orElseGet(ArrayList::new);
        }

        // 3. Search by name
        if (!isValidName(searchTerm)) {
            throw new IllegalArgumentException("Invalid name");
        }

        return findContactByName(searchTerm);
    }

    // Return all contacts
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    // Validate contact fields before duplicate checks
    private void validateContact(
            String name,
            String phoneNumber,
            String email) {

        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid name");
        }

        if (phoneNumber == null
                || !isValidPhoneNumber(phoneNumber.trim())) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits"
            );
        }

        if (!isValidEmail(email == null ? null : email.trim())) {
            throw new IllegalArgumentException(
                    "Invalid email format"
            );
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && phoneNumber.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email != null
                && email.matches(
                "^[A-Za-z0-9._%+'-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$"
        );
    }

    private boolean isValidName(String name) {
        return name != null
                && !name.trim().isEmpty()
                && name.trim().matches(
                "\\p{L}+([ '\\u2019-]\\p{L}+)*"
        );
    }
}
