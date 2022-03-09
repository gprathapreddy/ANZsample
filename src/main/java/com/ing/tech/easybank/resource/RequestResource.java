package com.ing.tech.EasyBank.resource;

import com.ing.tech.EasyBank.dto.*;
import com.ing.tech.EasyBank.service.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
public class RequestResource {
    RequestService requestService;

    @PostMapping("/create")
    public ResponseEntity<RequestSentDtoInput> createNewRequest(
            @Valid @RequestBody RequestSentDtoInput requestSentDto,
            Principal principal
    ){
        return ResponseEntity.ok(requestService.createRequest(requestSentDto,principal.getName()));
    }

    @PostMapping("/accept") // patch should be better here
    public ResponseEntity<TransactionDto> acceptRequest(
            @Valid @RequestBody AcceptRequestDto acceptRequestDto,
            Principal principal
    ){
        return ResponseEntity.ok(requestService.acceptRequest(acceptRequestDto, principal.getName()));
    }

    @DeleteMapping("/decline")
    public ResponseEntity<Void> declineRequest(
            @Valid @RequestBody DeclineRequestDto declineRequestDto,
            Principal principal
    ){
        requestService.declineRequest(declineRequestDto, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/receivedRequests")
    public ResponseEntity<Set<RequestReceivedDtoOutput>> getReceivedRequests(Principal principal) {
        return ResponseEntity.ok(requestService.getReceivedRequests(principal.getName()));
    }

    @GetMapping("/sentRequests")
    public ResponseEntity<Set<RequestSentDtoOutput>> getSentRequests(Principal principal) {
        return ResponseEntity.ok(requestService.getSentRequests(principal.getName()));
    }
}
