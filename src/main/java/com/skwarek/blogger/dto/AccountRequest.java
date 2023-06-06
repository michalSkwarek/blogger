package com.skwarek.blogger.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class AccountRequest {

    private String email;
    private String password;

}
