package com.plazoleta.notification_service.infrastructure.output.vonage;

import com.plazoleta.notification_service.domain.model.NotificationData;
import com.vonage.client.VonageClient;
import com.vonage.client.messages.MessageResponse;
import com.vonage.client.messages.MessagesClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VonageSenderAdapterTest {

    @Mock
    private VonageClient vonageClient;

    @Mock
    private MessagesClient messagesClient;

    @Mock
    private MessageResponse messageResponse;

    @InjectMocks
    private VonageSenderAdapter vonageSenderAdapter;


    @Test
    void shouldSendNotificationSuccessfully() {
        NotificationData notificationData = NotificationData.builder()
                .phone("+573001234567")
                .pin("123456")
                .build();

        when(vonageClient.getMessagesClient()).thenReturn(messagesClient);
        when(messagesClient.sendMessage(any())).thenReturn(messageResponse);

        StepVerifier.create(vonageSenderAdapter.send(notificationData))
                .verifyComplete();

    }

    @Test
    void shouldReturnMappedErrorWhenVonageFails() {
        NotificationData notificationData = NotificationData.builder()
                .phone("+573001234567")
                .pin("123456")
                .build();

        when(vonageClient.getMessagesClient()).thenReturn(messagesClient);
        when(messagesClient.sendMessage(any()))
                .thenThrow(new RuntimeException("Vonage error"));

        StepVerifier.create(vonageSenderAdapter.send(notificationData))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("No fue posible enviar la notificación"))
                .verify();

    }

    @Test
    void shouldReturnErrorWhenPhoneIsNull() {
        NotificationData notificationData = NotificationData.builder()
                .phone(null)
                .pin("123456")
                .build();

        assertThrows(
                IllegalArgumentException.class, () -> vonageSenderAdapter.send(notificationData)
        );


    }
}
