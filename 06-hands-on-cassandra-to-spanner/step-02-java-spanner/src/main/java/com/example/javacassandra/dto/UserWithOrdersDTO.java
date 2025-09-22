package com.example.javacassandra.dto;

import com.example.javacassandra.model.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithOrdersDTO {
    private User user;
    private List<OrderDTO> orders;
}
