package com.plazoleta.notification_service.infrastructure.output.vonage;

import com.plazoleta.notification_service.domain.model.NotificationData;
import com.vonage.client.VonageClient;
import com.vonage.client.messages.MessageResponse;
import com.vonage.client.messages.MessagesClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VonageSenderAdapterTest {

    private VonageClient vonageClient;
    private MessagesClient messagesClient;
    private MessageResponse messageResponse;
    private VonageSenderAdapter vonageSenderAdapter;

    @BeforeEach
    void setUp() {
        vonageClient = mock(VonageClient.class);
        messagesClient = mock(MessagesClient.class);
        messageResponse = mock(MessageResponse.class);
        vonageSenderAdapter = new VonageSenderAdapter(vonageClient);
    }

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

        verify(vonageClient).getMessagesClient();
        verify(messagesClient).sendMessage(any());
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

        verify(vonageClient).getMessagesClient();
        verify(messagesClient).sendMessage(any());
    }

    @Test
    void shouldReturnErrorWhenPhoneIsNull() {
        NotificationData notificationData = NotificationData.builder()
                .phone(null)
                .pin("123456")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vonageSenderAdapter.send(notificationData)
        );

        assertEquals("El teléfono no puede ser null", exception.getMessage());
        verifyNoInteractions(vonageClient);
    }
}
