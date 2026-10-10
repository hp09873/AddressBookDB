
package com.harsh.addressbook;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Search contacts
    @GetMapping("/search")
    public List<Contact> searchContacts(@RequestParam String term) {
        return contactService.searchContact(term);
    }

    // Add contact
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contact addContact(@RequestBody Contact contact) {
        return contactService.addContact(
                contact.getName(),
                contact.getPhoneNumber(),
                contact.getEmail()
        );
    }

    // Edit contact
    @PutMapping("/{id}")
    public Contact editContact(
            @PathVariable Long id,
            @RequestBody Contact contact) {

        return contactService.editContact(
                id,
                contact.getName(),
                contact.getPhoneNumber(),
                contact.getEmail()
        );
    }

    // Delete contact
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
    }

    // Get contact by ID
    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable Long id) {
        return contactService.findContactById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contact not found"
                        )
                );
    }

    // Get all contacts
    @GetMapping
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }
}
