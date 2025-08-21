package com.wizwrite.service;

import com.wizwrite.dto.ContentRequest;
import com.wizwrite.dto.ContentResponse;
import com.wizwrite.entity.ContentHistory;
import com.wizwrite.entity.User;
import com.wizwrite.entity.UserCredit;
import com.wizwrite.repository.ContentHistoryRepository;
import com.wizwrite.repository.UserCreditRepository;
import com.wizwrite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentService {

    private final UserRepository userRepository;
    private final UserCreditRepository userCreditRepository;
    private final ContentHistoryRepository contentHistoryRepository;
    private final String openAiApiKey;

    public ContentService(UserRepository userRepository, UserCreditRepository userCreditRepository, ContentHistoryRepository contentHistoryRepository, @Value("${openai.api.key}") String openAiApiKey) {
        this.userRepository = userRepository;
        this.userCreditRepository = userCreditRepository;
        this.contentHistoryRepository = contentHistoryRepository;
        this.openAiApiKey = openAiApiKey;
    }

    @Transactional
    public ContentResponse generateContent(ContentRequest contentRequest) {
        // Ambil user yang sedang login dari security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserCredit userCredit = userCreditRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("User credit not found"));

        // Cek kredit, jika 0 atau kurang, tolak request
        if (userCredit.getCreditsRemaining() <= 0) {
            throw new IllegalStateException("Not enough credits.");
        }

        // TODO: Ganti dengan logika pemanggilan OpenAI API yang sebenarnya
        // Mocking respons AI untuk demo
        String generatedText = "Ini adalah hasil konten dari AI untuk prompt: \"" + contentRequest.getPrompt() + "\"";
        int creditCost = 1; // Biaya satu kali pemanggilan

        // Kurangi kredit
        userCredit.setCreditsRemaining(userCredit.getCreditsRemaining() - creditCost);
        userCredit.setLastUpdated(LocalDateTime.now());
        userCreditRepository.save(userCredit);

        // Simpan ke riwayat
        ContentHistory history = new ContentHistory();
        history.setUser(user);
        history.setPrompt(contentRequest.getPrompt());
        history.setResponseText(generatedText);
        history.setCreatedAt(LocalDateTime.now());
        contentHistoryRepository.save(history);

        // Buat respons
        ContentResponse response = new ContentResponse();
        response.setGeneratedText(generatedText);
        response.setRemainingCredits(userCredit.getCreditsRemaining());

        return response;
    }

    public List<ContentHistory> getContentHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return contentHistoryRepository.findByUser(user);
    }
}
