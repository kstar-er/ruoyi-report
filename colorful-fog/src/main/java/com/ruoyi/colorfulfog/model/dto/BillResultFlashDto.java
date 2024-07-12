package com.ruoyi.colorfulfog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillResultFlashDto {
    String batchCode;
    String collectResultCode;
}
