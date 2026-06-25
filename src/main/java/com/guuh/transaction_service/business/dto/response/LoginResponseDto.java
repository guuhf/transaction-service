package com.guuh.transaction_service.business.dto.response;

import com.guuh.transaction_service.infrastructure.entity.RefreshToken;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String refreshToken;
}
