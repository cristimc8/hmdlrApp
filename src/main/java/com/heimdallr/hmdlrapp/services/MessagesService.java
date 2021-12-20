package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.repository.MessagesRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;

@Service(AssociatedRepo = MessagesRepository.class)
public class MessagesService {
    private MessagesRepository messagesRepository;

    private MessagesService(Object messagesRepo) {
        this.messagesRepository = (MessagesRepository) messagesRepo;
    }

    
}
